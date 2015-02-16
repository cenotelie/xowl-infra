/**********************************************************************
 * Copyright (c) 2015 Laurent Wouters and others
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 **********************************************************************/

package org.xowl.engine.loaders;

import org.xowl.engine.owl.XOWLStore;
import org.xowl.hime.redist.*;
import org.xowl.lang.owl2.*;
import org.xowl.store.loaders.Utils;
import org.xowl.utils.Files;
import org.xowl.utils.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Loader of ontologies serialized in the Functional syntax
 */
public class FunctionalOWL2Loader implements Loader {
    /**
     * The RDF store to create nodes from
     */
    private XOWLStore store;
    /**
     * The URI of the resource currently being loaded
     */
    private String resource;
    /**
     * The base URI for relative URIs
     */
    private String baseURI;
    /**
     * Map of the current namespaces
     */
    private Map<String, String> namespaces;
    /**
     * Map of the current blank nodes
     */
    private Map<String, AnonymousIndividual> blanks;
    /**
     * The cached result
     */
    private LoaderResult cache;

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result;
        try {
            String content = Files.read(reader);
            FunctionalOWL2Lexer lexer = new FunctionalOWL2Lexer(content);
            FunctionalOWL2Parser parser = new FunctionalOWL2Parser(lexer);
            parser.setRecover(false);
            result = parser.parse();
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
        for (ParseError error : result.getErrors()) {
            logger.error(error);
            Context context = result.getInput().getContext(error.getPosition());
            logger.error(context.getContent());
            logger.error(context.getPointer());
        }
        return result;
    }

    @Override
    public LoaderResult load(Logger logger, Reader reader, String uri) {
        ParseResult result = parse(logger, reader);
        if (result == null || !result.isSuccess() || result.getErrors().size() > 0)
            return null;

        this.resource = uri;
        this.namespaces = new HashMap<>();
        this.blanks = new HashMap<>();

        // load the prefixes
        for (ASTNode node : result.getRoot().getChildren().get(0).getChildren())
            loadPrefixID(node);
        ASTNode nodeOnto = result.getRoot().getChildren().get(1);
        // load the iris
        String version = null;
        ASTNode nodeIRIs = nodeOnto.getChildren().get(0);
        if (!nodeIRIs.getChildren().isEmpty()) {
            baseURI = loadIRI(nodeIRIs.getChildren().get(0));
            if (nodeIRIs.getChildren().size() > 1)
                version = loadIRI(nodeIRIs.getChildren().get(1));
        }
        this.cache = new LoaderResult(baseURI, version);
        // load the imports
        for (ASTNode node : nodeOnto.getChildren().get(1).getChildren()) {
            cache.addImport(loadIRI(node));
        }
        // load the annotations
        for (ASTNode node : nodeOnto.getChildren().get(2).getChildren()) {
            cache.addAnnotation(loadAnnotation(node));
        }
        // load the axioms
        for (ASTNode node : nodeOnto.getChildren().get(3).getChildren()) {
            cache.addAxiom(loadAxiom(node));
        }
        return cache;
    }

    /**
     * Loads a prefix and its associated namespace represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadPrefixID(ASTNode node) {
        String prefix = node.getChildren().get(0).getSymbol().getValue();
        String uri = node.getChildren().get(1).getSymbol().getValue();
        prefix = prefix.substring(0, prefix.length() - 1);
        uri = Utils.unescape(uri.substring(1, uri.length() - 1));
        namespaces.put(prefix, uri);
    }

    /**
     * Loads an IRI from an AST node
     *
     * @param node An AST node
     */
    private String loadIRI(ASTNode node) {
        if (node.getSymbol().getID() == FunctionalOWL2Lexer.ID.IRIREF) {
            String value = node.getSymbol().getValue();
            value = Utils.unescape(value.substring(1, value.length() - 1));
            return Utils.normalizeIRI(resource, baseURI, value);
        } else {
            // this is a local name
            return getIRIForLocalName(node.getSymbol().getValue());
        }
    }

    /**
     * Gets the full IRI for the specified escaped local name
     *
     * @param value An escaped local name
     * @return The equivalent full IRI
     */
    private String getIRIForLocalName(String value) {
        value = Utils.unescape(value);
        int index = 0;
        while (index != value.length()) {
            if (value.charAt(index) == ':') {
                String prefix = value.substring(0, index);
                String uri = namespaces.get(prefix);
                if (uri != null) {
                    String name = value.substring(index + 1);
                    return Utils.normalizeIRI(resource, baseURI, uri + name);
                }
            }
            index++;
        }
        throw new IllegalArgumentException("Failed to resolve local name " + value);
    }

    /**
     * Loads an annotation from an AST node
     *
     * @param node The AST node
     * @return The annotation
     */
    private Annotation loadAnnotation(ASTNode node) {
        Annotation result = new Annotation();
        // loads the annotations on this annotation
        for (ASTNode child : node.getChildren().get(0).getChildren()) {
            result.addAnnotations(loadAnnotation(child));
        }
        IRI propertyIRI = new IRI();
        propertyIRI.setHasValue(loadIRI(node.getChildren().get(1)));
        result.setAnnotProperty(propertyIRI);
        result.setAnnotValue(loadAnnotationValue(node.getChildren().get(2)));
        return result;
    }

    /**
     * Loads an annotation value from an AST node
     *
     * @param node The AST node
     * @return The annotation value
     */
    private AnnotationValue loadAnnotationValue(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalOWL2Lexer.ID.IRIREF:
            case FunctionalOWL2Lexer.ID.PNAME_LN: {
                IRI value = new IRI();
                value.setHasValue(loadIRI(node));
                return value;
            }
            case FunctionalOWL2Lexer.ID.BLANK_NODE_LABEL:
                return loadAnonymous(node);
            default:
                return loadLiteral(node);
        }
    }

    /**
     * Loads an anonymous individual from an AST node
     *
     * @param node The AST node
     * @return The anonymous individual
     */
    private AnonymousIndividual loadAnonymous(ASTNode node) {
        String name = node.getSymbol().getValue().substring(2);
        AnonymousIndividual result = blanks.get(name);
        if (result != null)
            return result;
        result = new AnonymousIndividual();
        result.setNodeID(name);
        blanks.put(name, result);
        return result;
    }

    /**
     * Loads a literal from an AST node
     *
     * @param node The AST node
     * @return The literal
     */
    private Literal loadLiteral(ASTNode node) {
        Literal result = new Literal();
        String value = node.getChildren().get(0).getSymbol().getValue();
        value = value.substring(1, value.length() - 1);
        result.setLexicalValue(value);
        switch (node.getSymbol().getID()) {
            case FunctionalOWL2Parser.ID.literalTyped: {
                IRI type = new IRI();
                type.setHasValue(loadIRI(node.getChildren().get(1)));
                result.setMemberOf(type);
                break;
            }
            case FunctionalOWL2Parser.ID.literalLang: {
                String tag = node.getChildren().get(1).getSymbol().getValue();
                result.setLangTag(tag.substring(1));
                break;
            }
        }
        return result;
    }

    /**
     * Loads an axiom from an AST node
     *
     * @param node The AST node
     * @return The annotation
     */
    private Axiom loadAxiom(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalOWL2Parser.ID.axiomDeclaration:
                return loadAxiomDeclaration(node);
        }

        return null;
    }

    /**
     * Loads the base information of an axiom
     * @param node The axiom's AST node
     * @param axiom The axiom
     */
    private void loadAxiomBase(ASTNode node, Axiom axiom) {
        axiom.setFile(baseURI);
        axiom.setLine(node.getPosition().getLine());
        for (ASTNode child : node.getChildren().get(0).getChildren()) {
            axiom.addAnnotations(loadAnnotation(child));
        }
    }

    /**
     * Loads a declaration axiom from an AST node
     * @param node The AST node
     * @return The axiom
     */
    private Axiom loadAxiomDeclaration(ASTNode node) {
        Declaration axiom = new Declaration();
        loadAxiomBase(node, axiom);
        axiom.setType(node.getChildren().get(1).getSymbol().getValue());
        IRI entity = new IRI();
        entity.setHasValue(loadIRI(node.getChildren().get(2)));
        axiom.setEntity(entity);
        return axiom;
    }


}
