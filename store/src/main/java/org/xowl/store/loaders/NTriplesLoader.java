/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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

package org.xowl.store.loaders;

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.Context;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.rdf.*;
import org.xowl.store.voc.OWLDatatype;
import org.xowl.utils.Files;
import org.xowl.utils.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Loader for N-Triples sources
 *
 * @author Laurent Wouters
 */
public class NTriplesLoader extends Loader {
    /**
     * The RDF graph to load in
     */
    private RDFGraph graph;
    /**
     * Maps of blanks nodes
     */
    private Map<String, BlankNode> blanks;

    /**
     * Initializes this loader
     *
     * @param graph The RDF graph to load in
     */
    public NTriplesLoader(RDFGraph graph) {
        this.graph = graph;
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result = null;
        try {
            String content = Files.read(reader);
            NTriplesLexer lexer = new NTriplesLexer(content);
            NTriplesParser parser = new NTriplesParser(lexer);
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
    public Ontology load(Logger logger, Reader reader) {
        blanks = new HashMap<>();
        Ontology ontology = createNewOntology();

        ParseResult result = parse(logger, reader);
        if (result == null || !result.isSuccess() || result.getErrors().size() > 0)
            return null;

        for (ASTNode triple : result.getRoot().getChildren()) {
            Node n1 = getRDFNode(triple.getChildren().get(0));
            Node n2 = getRDFNode(triple.getChildren().get(1));
            Node n3 = getRDFNode(triple.getChildren().get(2));
            try {
                graph.add(ontology, (SubjectNode) n1, (Property) n2, n3);
            } catch (UnsupportedNodeType ex) {
                // cannot happen
            }
        }

        return ontology;
    }

    /**
     * Gets the RDF node represented by the specified AST node
     *
     * @param node An AST node representing an RDF node
     * @return The represented RDF node
     */
    private Node getRDFNode(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case NTriplesLexer.ID.IRIREF:
                return translateIRIREF(node);
            case NTriplesLexer.ID.BLANK_NODE_LABEL:
                return translateBlankNode(node);
            case NTriplesLexer.ID.STRING_LITERAL_QUOTE:
                return translateLiteral(node);
        }
        return null;
    }

    /**
     * Translates the specified AST node into a IRI RDF node
     *
     * @param node An IRIREF AST node
     * @return The corresponding RDF node
     */
    private Node translateIRIREF(ASTNode node) {
        String value = node.getSymbol().getValue();
        value = unescape(value);
        return graph.getNodeIRI(value);
    }

    /**
     * Translates the specified AST node into a Blank RDF node
     *
     * @param node An BLANK_NODE_LABEL AST node
     * @return The corresponding RDF node
     */
    private Node translateBlankNode(ASTNode node) {
        String key = node.getSymbol().getValue();
        key = key.substring(1, key.length() - 1);
        BlankNode blank = blanks.get(key);
        if (blank != null)
            return blank;
        blank = graph.getBlankNode();
        blanks.put(key, blank);
        return blank;
    }

    /**
     * Translates the specified AST node into a literal RDF node
     *
     * @param node An STRING_LITERAL_QUOTE AST node
     * @return The corresponding RDF node
     */
    private Node translateLiteral(ASTNode node) {
        String value = node.getSymbol().getValue();
        value = unescape(value);
        if (node.getChildren().size() == 0) {
            return graph.getLiteralNode(value, OWLDatatype.xsdString, null);
        }
        ASTNode child = node.getChildren().get(0);
        if (child.getSymbol().getID() == NTriplesLexer.ID.IRIREF) {
            String type = child.getSymbol().getValue();
            type = unescape(type);
            return graph.getLiteralNode(value, type, null);
        } else if (child.getSymbol().getID() == NTriplesLexer.ID.LANGTAG) {
            String lang = child.getSymbol().getValue();
            lang = lang.substring(1);
            return graph.getLiteralNode(value, OWLDatatype.rdfLangString, lang);
        }
        return null;
    }
}