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

package org.xowl.store.loaders;

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.Context;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.lang.owl2.*;
import org.xowl.store.Vocabulary;
import org.xowl.utils.Files;
import org.xowl.utils.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Loader of ontologies serialized in the Functional syntax
 *
 * @author Laurent Wouters
 */
public class FunctionalOWL2Loader implements Loader {
    /**
     * The URI of the resource currently being loaded
     */
    protected String resource;
    /**
     * The base URI for relative URIs
     */
    protected String baseURI;
    /**
     * Map of the current namespaces
     */
    protected Map<String, String> namespaces;
    /**
     * Map of the current blank nodes
     */
    protected Map<String, AnonymousIndividual> blanks;
    /**
     * The cached result
     */
    protected OWLLoaderResult cache;

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
    public RDFLoaderResult loadRDF(Logger logger, Reader reader, String uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OWLLoaderResult loadOWL(Logger logger, Reader reader, String uri) {
        ParseResult result = parse(logger, reader);
        if (result == null || !result.isSuccess() || result.getErrors().size() > 0)
            return null;
        this.resource = uri;
        this.namespaces = new HashMap<>();
        this.blanks = new HashMap<>();
        loadDocument(result.getRoot());
        return cache;
    }

    /**
     * Loads a document from the specified AST node
     *
     * @param node An AST node
     */
    protected void loadDocument(ASTNode node) {
        for (ASTNode prefix : node.getChildren().get(0).getChildren())
            loadPrefixID(prefix);
        loadOntology(node.getChildren().get(1));
    }

    /**
     * Loads an ontology from the specified AST node
     *
     * @param node An AST node
     */
    protected void loadOntology(ASTNode node) {
        String version = null;
        ASTNode nodeIRIs = node.getChildren().get(0);
        if (!nodeIRIs.getChildren().isEmpty()) {
            baseURI = loadIRI(nodeIRIs.getChildren().get(0));
            if (nodeIRIs.getChildren().size() > 1)
                version = loadIRI(nodeIRIs.getChildren().get(1));
        }
        this.cache = new OWLLoaderResult(baseURI, version);
        // loadOWL the imports
        for (ASTNode child : node.getChildren().get(1).getChildren()) {
            cache.addImport(loadIRI(child));
        }
        // loadOWL the annotations
        for (ASTNode child : node.getChildren().get(2).getChildren()) {
            cache.addAnnotation(loadAnnotation(child));
        }
        // loadOWL the axioms
        for (ASTNode child : node.getChildren().get(3).getChildren()) {
            loadElement(child);
        }
    }

    /**
     * Loads an ontology element from the specified AST
     *
     * @param node An AST node
     */
    protected void loadElement(ASTNode node) {
        cache.addAxiom(loadAxiom(node));
    }

    /**
     * Loads a prefix and its associated namespace represented by the specified AST node
     *
     * @param node An AST node
     */
    protected void loadPrefixID(ASTNode node) {
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
     * @return The loaded IRI
     */
    protected String loadIRI(ASTNode node) {
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
    protected String getIRIForLocalName(String value) {
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
    protected Annotation loadAnnotation(ASTNode node) {
        Annotation result = new Annotation();
        // loads the annotations on this annotation
        for (ASTNode child : node.getChildren().get(0).getChildren()) {
            result.addAnnotations(loadAnnotation(child));
        }
        result.setAnnotProperty(loadExpAnnotationProperty(node.getChildren().get(1)));
        result.setAnnotValue(loadExpAnnotationValue(node.getChildren().get(2)));
        return result;
    }

    /**
     * Loads an axiom from an AST node
     *
     * @param node The AST node
     * @return The annotation
     */
    protected Axiom loadAxiom(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalOWL2Parser.ID.axiomDeclaration:
                return loadAxiomDeclaration(node);
            case FunctionalOWL2Parser.ID.axiomSubClassOf:
                return loadAxiomSubClassOf(node);
            case FunctionalOWL2Parser.ID.axiomEquivalentClasses:
                return loadAxiomEquivalentClasses(node);
            case FunctionalOWL2Parser.ID.axiomDisjointClasses:
                return loadAxiomDisjointClasses(node);
            case FunctionalOWL2Parser.ID.axiomDisjointUnion:
                return loadAxiomDisjointUnion(node);
            case FunctionalOWL2Parser.ID.axiomSubOjectPropertyOf:
                return loadAxiomSubObjectPropertyOf(node);
            case FunctionalOWL2Parser.ID.axiomEquivalentObjectProperties:
                return loadAxiomEquivalentObjectProperties(node);
            case FunctionalOWL2Parser.ID.axiomDisjointObjectProperties:
                return loadAxiomDisjointObjectProperties(node);
            case FunctionalOWL2Parser.ID.axiomInverseObjectProperties:
                return loadAxiomInverseObjectProperties(node);
            case FunctionalOWL2Parser.ID.axiomObjectPropertyDomain:
                return loadAxiomObjectPropertyDomain(node);
            case FunctionalOWL2Parser.ID.axiomObjectPropertyRange:
                return loadAxiomObjectPropertyRange(node);
            case FunctionalOWL2Parser.ID.axiomFunctionalObjectProperty:
                return loadAxiomFunctionalObjectProperty(node);
            case FunctionalOWL2Parser.ID.axiomInverseFunctionalObjectProperty:
                return loadAxiomInverseFunctionalObjectProperty(node);
            case FunctionalOWL2Parser.ID.axiomReflexiveObjectProperty:
                return loadAxiomReflexiveObjectProperty(node);
            case FunctionalOWL2Parser.ID.axiomIrreflexiveObjectProperty:
                return loadAxiomIrreflexiveObjectProperty(node);
            case FunctionalOWL2Parser.ID.axiomSymmetricObjectProperty:
                return loadAxiomSymmetricObjectProperty(node);
            case FunctionalOWL2Parser.ID.axiomAsymmetricObjectProperty:
                return loadAxiomAsymmetricObjectProperty(node);
            case FunctionalOWL2Parser.ID.axiomTransitiveObjectProperty:
                return loadAxiomTransitiveObjectProperty(node);
            case FunctionalOWL2Parser.ID.axiomSubDataPropertyOf:
                return loadAxiomSubDataPropertyOf(node);
            case FunctionalOWL2Parser.ID.axiomEquivalentDataProperties:
                return loadAxiomEquivalentDataProperties(node);
            case FunctionalOWL2Parser.ID.axiomDisjointDataProperties:
                return loadAxiomDisjointDataProperties(node);
            case FunctionalOWL2Parser.ID.axiomDataPropertyDomain:
                return loadAxiomDataPropertyDomain(node);
            case FunctionalOWL2Parser.ID.axiomDataPropertyRange:
                return loadAxiomDataPropertyRange(node);
            case FunctionalOWL2Parser.ID.axiomFunctionalDataProperty:
                return loadAxiomFunctionalDataProperty(node);
            case FunctionalOWL2Parser.ID.axiomDatatype:
                return loadAxiomDatatypeDefinition(node);
            case FunctionalOWL2Parser.ID.axiomHasKey:
                return loadAxiomHasKey(node);
            case FunctionalOWL2Parser.ID.axiomSameIndividual:
                return loadAxiomSameIndividual(node);
            case FunctionalOWL2Parser.ID.axiomDifferentIndividuals:
                return loadAxiomDifferentIndividuals(node);
            case FunctionalOWL2Parser.ID.axiomClassAssertion:
                return loadAxiomClassAssertion(node);
            case FunctionalOWL2Parser.ID.axiomObjectPropertyAssertion:
                return loadAxiomObjectPropertyAssertion(node);
            case FunctionalOWL2Parser.ID.axiomNegativeObjectPropertyAssertion:
                return loadAxiomNegativeObjectPropertyAssertion(node);
            case FunctionalOWL2Parser.ID.axiomDataPropertyAssertion:
                return loadAxiomDataPropertyAssertion(node);
            case FunctionalOWL2Parser.ID.axiomNegativeDataPropertyAssertion:
                return loadAxiomNegativeDataPropertyAssertion(node);
            case FunctionalOWL2Parser.ID.axiomAnnotationAssertion:
                return loadAxiomAnnotationAssertion(node);
            case FunctionalOWL2Parser.ID.axiomSubAnnotationPropertyOf:
                return loadAxiomSubAnnotationPropertyOf(node);
            case FunctionalOWL2Parser.ID.axiomAnnotationPropertyDomain:
                return loadAxiomAnnotationPropertyDomain(node);
            case FunctionalOWL2Parser.ID.axiomAnnotationPropertyRange:
                return loadAxiomAnnotationPropertyRange(node);
        }
        return null;
    }

    /**
     * Loads the base information of an axiom
     *
     * @param node  The axiom's AST node
     * @param axiom The axiom
     */
    protected void loadAxiomBase(ASTNode node, Axiom axiom) {
        axiom.setFile(baseURI);
        axiom.setLine(node.getPosition().getLine());
        for (ASTNode child : node.getChildren().get(0).getChildren()) {
            axiom.addAnnotations(loadAnnotation(child));
        }
    }

    /**
     * Loads a Declaration axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomDeclaration(ASTNode node) {
        Declaration axiom = new Declaration();
        loadAxiomBase(node, axiom);
        axiom.setType(node.getChildren().get(1).getSymbol().getValue());
        axiom.setEntity(loadEntity(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a SubClassOf axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomSubClassOf(ASTNode node) {
        SubClassOf axiom = new SubClassOf();
        loadAxiomBase(node, axiom);
        axiom.setClasse(loadExpClass(node.getChildren().get(1)));
        axiom.setSuperClass(loadExpClass(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a EquivalentClasses axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomEquivalentClasses(ASTNode node) {
        EquivalentClasses axiom = new EquivalentClasses();
        loadAxiomBase(node, axiom);
        ClassSequence seq = new ClassSequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            ClassElement element = new ClassElement();
            element.setClasse(loadExpClass(node.getChildren().get(i)));
            element.setIndex(i - 1);
            seq.addClassElements(element);
        }
        axiom.setClassSeq(seq);
        return axiom;
    }

    /**
     * Loads a DisjointClasses axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomDisjointClasses(ASTNode node) {
        DisjointClasses axiom = new DisjointClasses();
        loadAxiomBase(node, axiom);
        ClassSequence seq = new ClassSequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            ClassElement element = new ClassElement();
            element.setClasse(loadExpClass(node.getChildren().get(i)));
            element.setIndex(i - 1);
            seq.addClassElements(element);
        }
        axiom.setClassSeq(seq);
        return axiom;
    }

    /**
     * Loads a DisjointUnion axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomDisjointUnion(ASTNode node) {
        DisjointUnion axiom = new DisjointUnion();
        loadAxiomBase(node, axiom);
        axiom.setClasse(loadExpClass(node.getChildren().get(1)));
        ClassSequence seq = new ClassSequence();
        for (int i = 2; i != node.getChildren().size(); i++) {
            ClassElement element = new ClassElement();
            element.setClasse(loadExpClass(node.getChildren().get(i)));
            element.setIndex(i - 1);
            seq.addClassElements(element);
        }
        axiom.setClassSeq(seq);
        return axiom;
    }

    /**
     * Loads a SubObjectPropertyOf axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomSubObjectPropertyOf(ASTNode node) {
        SubObjectPropertyOf axiom = new SubObjectPropertyOf();
        loadAxiomBase(node, axiom);
        if (node.getChildren().get(1).getSymbol().getID() == FunctionalOWL2Parser.ID.expObjectPropertyChain) {
            ObjectPropertySequence seq = new ObjectPropertySequence();
            int index = 0;
            for (ASTNode child : node.getChildren().get(1).getChildren()) {
                ObjectPropertyElement element = new ObjectPropertyElement();
                element.setObjectProperty(loadExpObjectProperty(child));
                element.setIndex(index);
                index++;
                seq.addObjectPropertyElements(element);
            }
            axiom.setObjectPropertyChain(seq);
        } else {
            axiom.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        }
        axiom.setSuperObjectProperty(loadExpObjectProperty(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a EquivalentObjectProperties axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomEquivalentObjectProperties(ASTNode node) {
        EquivalentObjectProperties axiom = new EquivalentObjectProperties();
        loadAxiomBase(node, axiom);
        ObjectPropertySequence seq = new ObjectPropertySequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            ObjectPropertyElement element = new ObjectPropertyElement();
            element.setObjectProperty(loadExpObjectProperty(node.getChildren().get(i)));
            element.setIndex(i - 1);
            seq.addObjectPropertyElements(element);
        }
        axiom.setObjectPropertySeq(seq);
        return axiom;
    }

    /**
     * Loads a DisjointObjectProperties axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomDisjointObjectProperties(ASTNode node) {
        DisjointObjectProperties axiom = new DisjointObjectProperties();
        loadAxiomBase(node, axiom);
        ObjectPropertySequence seq = new ObjectPropertySequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            ObjectPropertyElement element = new ObjectPropertyElement();
            element.setObjectProperty(loadExpObjectProperty(node.getChildren().get(i)));
            element.setIndex(i - 1);
            seq.addObjectPropertyElements(element);
        }
        axiom.setObjectPropertySeq(seq);
        return axiom;
    }

    /**
     * Loads a InverseObjectProperties axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomInverseObjectProperties(ASTNode node) {
        InverseObjectProperties axiom = new InverseObjectProperties();
        loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        axiom.setInverse(loadExpObjectProperty(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a ObjectPropertyDomain axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomObjectPropertyDomain(ASTNode node) {
        ObjectPropertyDomain axiom = new ObjectPropertyDomain();
        loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        axiom.setClasse(loadExpClass(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a ObjectPropertyRange axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomObjectPropertyRange(ASTNode node) {
        ObjectPropertyRange axiom = new ObjectPropertyRange();
        loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        axiom.setClasse(loadExpClass(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a FunctionalObjectProperty axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomFunctionalObjectProperty(ASTNode node) {
        FunctionalObjectProperty axiom = new FunctionalObjectProperty();
        loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        return axiom;
    }

    /**
     * Loads a InverseFunctionalObjectProperty axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomInverseFunctionalObjectProperty(ASTNode node) {
        InverseFunctionalObjectProperty axiom = new InverseFunctionalObjectProperty();
        loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        return axiom;
    }

    /**
     * Loads a ReflexiveObjectProperty axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomReflexiveObjectProperty(ASTNode node) {
        ReflexiveObjectProperty axiom = new ReflexiveObjectProperty();
        loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        return axiom;
    }

    /**
     * Loads a IrreflexiveObjectProperty axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomIrreflexiveObjectProperty(ASTNode node) {
        IrreflexiveObjectProperty axiom = new IrreflexiveObjectProperty();
        loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        return axiom;
    }

    /**
     * Loads a SymmetricObjectProperty axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomSymmetricObjectProperty(ASTNode node) {
        SymmetricObjectProperty axiom = new SymmetricObjectProperty();
        loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        return axiom;
    }

    /**
     * Loads a AsymmetricObjectProperty axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomAsymmetricObjectProperty(ASTNode node) {
        AsymmetricObjectProperty axiom = new AsymmetricObjectProperty();
        loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        return axiom;
    }

    /**
     * Loads a TransitiveObjectProperty axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomTransitiveObjectProperty(ASTNode node) {
        TransitiveObjectProperty axiom = new TransitiveObjectProperty();
        loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        return axiom;
    }

    /**
     * Loads a SubDataPropertyOf axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomSubDataPropertyOf(ASTNode node) {
        SubDataPropertyOf axiom = new SubDataPropertyOf();
        loadAxiomBase(node, axiom);
        axiom.setDataProperty(loadExpDataProperty(node.getChildren().get(1)));
        axiom.setSuperDataProperty(loadExpDataProperty(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a EquivalentDataProperties axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomEquivalentDataProperties(ASTNode node) {
        EquivalentDataProperties axiom = new EquivalentDataProperties();
        loadAxiomBase(node, axiom);
        DataPropertySequence seq = new DataPropertySequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            DataPropertyElement element = new DataPropertyElement();
            element.setDataProperty(loadExpDataProperty(node.getChildren().get(i)));
            element.setIndex(i - 1);
            seq.addDataPropertyElements(element);
        }
        axiom.setDataPropertySeq(seq);
        return axiom;
    }

    /**
     * Loads a DisjointDataProperties axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomDisjointDataProperties(ASTNode node) {
        DisjointDataProperties axiom = new DisjointDataProperties();
        loadAxiomBase(node, axiom);
        DataPropertySequence seq = new DataPropertySequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            DataPropertyElement element = new DataPropertyElement();
            element.setDataProperty(loadExpDataProperty(node.getChildren().get(i)));
            element.setIndex(i - 1);
            seq.addDataPropertyElements(element);
        }
        axiom.setDataPropertySeq(seq);
        return axiom;
    }

    /**
     * Loads a DataPropertyDomain axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomDataPropertyDomain(ASTNode node) {
        DataPropertyDomain axiom = new DataPropertyDomain();
        loadAxiomBase(node, axiom);
        axiom.setDataProperty(loadExpDataProperty(node.getChildren().get(1)));
        axiom.setClasse(loadExpClass(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a DataPropertyRange axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomDataPropertyRange(ASTNode node) {
        DataPropertyRange axiom = new DataPropertyRange();
        loadAxiomBase(node, axiom);
        axiom.setDataProperty(loadExpDataProperty(node.getChildren().get(1)));
        axiom.setDatarange(loadExpDatarange(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a FunctionalDataProperty axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomFunctionalDataProperty(ASTNode node) {
        FunctionalDataProperty axiom = new FunctionalDataProperty();
        loadAxiomBase(node, axiom);
        axiom.setDataProperty(loadExpDataProperty(node.getChildren().get(1)));
        return axiom;
    }

    /**
     * Loads a DatatypeDefinition axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomDatatypeDefinition(ASTNode node) {
        DatatypeDefinition axiom = new DatatypeDefinition();
        loadAxiomBase(node, axiom);
        axiom.setDatatype(loadExpDatarange(node.getChildren().get(1)));
        axiom.setDatarange(loadExpDatarange(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a HasKey axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomHasKey(ASTNode node) {
        HasKey axiom = new HasKey();
        loadAxiomBase(node, axiom);
        axiom.setClasse(loadExpClass(node.getChildren().get(1)));
        ObjectPropertySequence seq1 = new ObjectPropertySequence();
        int index = 0;
        for (ASTNode child : node.getChildren().get(1).getChildren()) {
            ObjectPropertyElement element = new ObjectPropertyElement();
            element.setObjectProperty(loadExpObjectProperty(child));
            element.setIndex(index);
            seq1.addObjectPropertyElements(element);
            index++;
        }
        axiom.setObjectPropertySeq(seq1);
        DataPropertySequence seq2 = new DataPropertySequence();
        index = 0;
        for (ASTNode child : node.getChildren().get(2).getChildren()) {
            DataPropertyElement element = new DataPropertyElement();
            element.setDataProperty(loadExpDataProperty(child));
            element.setIndex(index);
            seq2.addDataPropertyElements(element);
            index++;
        }
        axiom.setDataPropertySeq(seq2);
        return axiom;
    }

    /**
     * Loads a SameIndividual axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomSameIndividual(ASTNode node) {
        SameIndividual axiom = new SameIndividual();
        loadAxiomBase(node, axiom);
        IndividualSequence seq = new IndividualSequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            IndividualElement element = new IndividualElement();
            element.setIndividual(loadExpIndividual(node.getChildren().get(i)));
            element.setIndex(i - 1);
            seq.addIndividualElements(element);
        }
        axiom.setIndividualSeq(seq);
        return axiom;
    }

    /**
     * Loads a DifferentIndividuals axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomDifferentIndividuals(ASTNode node) {
        DifferentIndividuals axiom = new DifferentIndividuals();
        loadAxiomBase(node, axiom);
        IndividualSequence seq = new IndividualSequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            IndividualElement element = new IndividualElement();
            element.setIndividual(loadExpIndividual(node.getChildren().get(i)));
            element.setIndex(i - 1);
            seq.addIndividualElements(element);
        }
        axiom.setIndividualSeq(seq);
        return axiom;
    }

    /**
     * Loads a ClassAssertion axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomClassAssertion(ASTNode node) {
        ClassAssertion axiom = new ClassAssertion();
        loadAxiomBase(node, axiom);
        axiom.setClasse(loadExpClass(node.getChildren().get(1)));
        axiom.setIndividual(loadExpIndividual(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a ObjectPropertyAssertion axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomObjectPropertyAssertion(ASTNode node) {
        ObjectPropertyAssertion axiom = new ObjectPropertyAssertion();
        loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        axiom.setIndividual(loadExpIndividual(node.getChildren().get(2)));
        axiom.setValueIndividual(loadExpIndividual(node.getChildren().get(3)));
        return axiom;
    }

    /**
     * Loads a NegativeObjectPropertyAssertion axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomNegativeObjectPropertyAssertion(ASTNode node) {
        NegativeObjectPropertyAssertion axiom = new NegativeObjectPropertyAssertion();
        loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        axiom.setIndividual(loadExpIndividual(node.getChildren().get(2)));
        axiom.setValueIndividual(loadExpIndividual(node.getChildren().get(3)));
        return axiom;
    }

    /**
     * Loads a DataPropertyAssertion axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomDataPropertyAssertion(ASTNode node) {
        DataPropertyAssertion axiom = new DataPropertyAssertion();
        loadAxiomBase(node, axiom);
        axiom.setDataProperty(loadExpDataProperty(node.getChildren().get(1)));
        axiom.setIndividual(loadExpIndividual(node.getChildren().get(2)));
        axiom.setValueLiteral(loadExpLiteral(node.getChildren().get(3)));
        return axiom;
    }

    /**
     * Loads a NegativeDataPropertyAssertion axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomNegativeDataPropertyAssertion(ASTNode node) {
        NegativeDataPropertyAssertion axiom = new NegativeDataPropertyAssertion();
        loadAxiomBase(node, axiom);
        axiom.setDataProperty(loadExpDataProperty(node.getChildren().get(1)));
        axiom.setIndividual(loadExpIndividual(node.getChildren().get(2)));
        axiom.setValueLiteral(loadExpLiteral(node.getChildren().get(3)));
        return axiom;
    }

    /**
     * Loads a AnnotationAssertion axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomAnnotationAssertion(ASTNode node) {
        AnnotationAssertion axiom = new AnnotationAssertion();
        loadAxiomBase(node, axiom);
        axiom.setAnnotProperty(loadExpAnnotationProperty(node.getChildren().get(1)));
        axiom.setAnnotSubject(loadExpAnnotationSubject(node.getChildren().get(2)));
        axiom.setAnnotValue(loadExpAnnotationValue(node.getChildren().get(3)));
        return axiom;
    }

    /**
     * Loads a SubAnnotationPropertyOf axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomSubAnnotationPropertyOf(ASTNode node) {
        SubAnnotationPropertyOf axiom = new SubAnnotationPropertyOf();
        loadAxiomBase(node, axiom);
        axiom.setAnnotProperty(loadExpAnnotationProperty(node.getChildren().get(1)));
        axiom.setSuperAnnotProperty(loadExpAnnotationProperty(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a AnnotationPropertyDomain axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomAnnotationPropertyDomain(ASTNode node) {
        AnnotationPropertyDomain axiom = new AnnotationPropertyDomain();
        loadAxiomBase(node, axiom);
        axiom.setAnnotProperty(loadExpAnnotationProperty(node.getChildren().get(1)));
        axiom.setAnnotDomain(loadEntity(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads a AnnotationPropertyRange axiom from an AST node
     *
     * @param node The AST node
     * @return The axiom
     */
    protected Axiom loadAxiomAnnotationPropertyRange(ASTNode node) {
        AnnotationPropertyRange axiom = new AnnotationPropertyRange();
        loadAxiomBase(node, axiom);
        axiom.setAnnotProperty(loadExpAnnotationProperty(node.getChildren().get(1)));
        axiom.setAnnotRange(loadEntity(node.getChildren().get(2)));
        return axiom;
    }

    /**
     * Loads an entity expression from an AST node
     *
     * @param node The AST node
     * @return The entity expression
     */
    protected EntityExpression loadExpEntity(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalOWL2Lexer.ID.IRIREF:
            case FunctionalOWL2Lexer.ID.PNAME_LN:
                return loadEntity(node);
        }
        return null;
    }

    /**
     * Loads an entity from an AST node
     *
     * @param node The AST node
     * @return The entity
     */
    protected IRI loadEntity(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalOWL2Lexer.ID.IRIREF:
            case FunctionalOWL2Lexer.ID.PNAME_LN:
                // named individual
                IRI entity = new IRI();
                entity.setHasValue(loadIRI(node));
                return entity;
        }
        return null;
    }

    /**
     * Loads a class expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpClass(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalOWL2Parser.ID.expObjectInterfactionOf:
                return loadExpObjectIntersectionOf(node);
            case FunctionalOWL2Parser.ID.expObjectUnionOf:
                return loadExpObjectUnionOf(node);
            case FunctionalOWL2Parser.ID.expObjectComplementOf:
                return loadExpObjectComplementOf(node);
            case FunctionalOWL2Parser.ID.expObjectOneOf:
                return loadExpObjectOneOf(node);
            case FunctionalOWL2Parser.ID.expObjectSomeValuesFrom:
                return loadExpObjectSomeValuesFrom(node);
            case FunctionalOWL2Parser.ID.expObjectAllValuesFrom:
                return loadExpObjectAllValuesFrom(node);
            case FunctionalOWL2Parser.ID.expObjectHasValue:
                return loadExpObjectHasValue(node);
            case FunctionalOWL2Parser.ID.expObjectHasSelf:
                return loadExpObjectHasSelf(node);
            case FunctionalOWL2Parser.ID.expObjectMinCardinality:
                return loadExpObjectMinCardinality(node);
            case FunctionalOWL2Parser.ID.expObjectMaxCardinality:
                return loadExpObjectMaxCardinality(node);
            case FunctionalOWL2Parser.ID.expObjectExactCardinality:
                return loadExpObjectExactCardinality(node);
            case FunctionalOWL2Parser.ID.expDataSomeValuesFrom:
                return loadExpDataSomeValuesFrom(node);
            case FunctionalOWL2Parser.ID.expDataAllValuesFrom:
                return loadExpDataAllValuesFrom(node);
            case FunctionalOWL2Parser.ID.expDataHasValue:
                return loadExpDataHasValue(node);
            case FunctionalOWL2Parser.ID.expDataMinCardinality:
                return loadExpDataMinCardinality(node);
            case FunctionalOWL2Parser.ID.expDataMaxCardinality:
                return loadExpDataMaxCardinality(node);
            case FunctionalOWL2Parser.ID.expDataExactCardinality:
                return loadExpDataExactCardinality(node);
        }
        return loadExpEntity(node);
    }

    /**
     * Loads a ObjectIntersectionOf expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpObjectIntersectionOf(ASTNode node) {
        ObjectIntersectionOf expression = new ObjectIntersectionOf();
        ClassSequence seq = new ClassSequence();
        for (int i = 0; i != node.getChildren().size(); i++) {
            ClassElement element = new ClassElement();
            element.setClasse(loadExpClass(node.getChildren().get(i)));
            element.setIndex(i);
            seq.addClassElements(element);
        }
        expression.setClassSeq(seq);
        return expression;
    }

    /**
     * Loads a ObjectUnionOf expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpObjectUnionOf(ASTNode node) {
        ObjectUnionOf expression = new ObjectUnionOf();
        ClassSequence seq = new ClassSequence();
        for (int i = 0; i != node.getChildren().size(); i++) {
            ClassElement element = new ClassElement();
            element.setClasse(loadExpClass(node.getChildren().get(i)));
            element.setIndex(i);
            seq.addClassElements(element);
        }
        expression.setClassSeq(seq);
        return expression;
    }

    /**
     * Loads a ObjectComplementOf expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpObjectComplementOf(ASTNode node) {
        ObjectComplementOf expression = new ObjectComplementOf();
        expression.setClasse(loadExpClass(node.getChildren().get(0)));
        return expression;
    }

    /**
     * Loads a ObjectOneOf expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpObjectOneOf(ASTNode node) {
        ObjectOneOf expression = new ObjectOneOf();
        IndividualSequence seq = new IndividualSequence();
        for (int i = 0; i != node.getChildren().size(); i++) {
            IndividualElement element = new IndividualElement();
            element.setIndividual(loadExpIndividual(node.getChildren().get(i)));
            element.setIndex(i);
            seq.addIndividualElements(element);
        }
        expression.setIndividualSeq(seq);
        return expression;
    }

    /**
     * Loads a ObjectSomeValuesFrom expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpObjectSomeValuesFrom(ASTNode node) {
        ObjectSomeValuesFrom expression = new ObjectSomeValuesFrom();
        expression.setObjectProperty(loadExpObjectProperty(node.getChildren().get(0)));
        expression.setClasse(loadExpClass(node.getChildren().get(1)));
        return expression;
    }

    /**
     * Loads a ObjectAllValuesFrom expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpObjectAllValuesFrom(ASTNode node) {
        ObjectAllValuesFrom expression = new ObjectAllValuesFrom();
        expression.setObjectProperty(loadExpObjectProperty(node.getChildren().get(0)));
        expression.setClasse(loadExpClass(node.getChildren().get(1)));
        return expression;
    }

    /**
     * Loads a ObjectHasValue expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpObjectHasValue(ASTNode node) {
        ObjectHasValue expression = new ObjectHasValue();
        expression.setObjectProperty(loadExpObjectProperty(node.getChildren().get(0)));
        expression.setIndividual(loadExpIndividual(node.getChildren().get(1)));
        return expression;
    }

    /**
     * Loads a ObjectHasSelf expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpObjectHasSelf(ASTNode node) {
        ObjectHasSelf expression = new ObjectHasSelf();
        expression.setObjectProperty(loadExpObjectProperty(node.getChildren().get(0)));
        return expression;
    }

    /**
     * Loads a ObjectMinCardinality expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpObjectMinCardinality(ASTNode node) {
        ObjectMinCardinality expression = new ObjectMinCardinality();
        expression.setCardinality(loadExpLiteral(node.getChildren().get(0)));
        expression.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        if (node.getChildren().size() > 2)
            expression.setClasse(loadExpClass(node.getChildren().get(2)));
        return expression;
    }

    /**
     * Loads a ObjectMaxCardinality expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpObjectMaxCardinality(ASTNode node) {
        ObjectMaxCardinality expression = new ObjectMaxCardinality();
        expression.setCardinality(loadExpLiteral(node.getChildren().get(0)));
        expression.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        if (node.getChildren().size() > 2)
            expression.setClasse(loadExpClass(node.getChildren().get(2)));
        return expression;
    }

    /**
     * Loads a ObjectExactCardinality expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpObjectExactCardinality(ASTNode node) {
        ObjectExactCardinality expression = new ObjectExactCardinality();
        expression.setCardinality(loadExpLiteral(node.getChildren().get(0)));
        expression.setObjectProperty(loadExpObjectProperty(node.getChildren().get(1)));
        if (node.getChildren().size() > 2)
            expression.setClasse(loadExpClass(node.getChildren().get(2)));
        return expression;
    }

    /**
     * Loads a DataSomeValuesFrom expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpDataSomeValuesFrom(ASTNode node) {
        DataSomeValuesFrom expression = new DataSomeValuesFrom();
        DataPropertySequence seq = new DataPropertySequence();
        for (int i = 0; i != node.getChildren().size() - 1; i++) {
            DataPropertyElement element = new DataPropertyElement();
            element.setDataProperty(loadExpDataProperty(node.getChildren().get(i)));
            element.setIndex(i);
            seq.addDataPropertyElements(element);
        }
        expression.setDataPropertySeq(seq);
        expression.setDatarange(loadExpDatarange(node.getChildren().get(1)));
        return expression;
    }

    /**
     * Loads a DataAllValuesFrom expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpDataAllValuesFrom(ASTNode node) {
        DataAllValuesFrom expression = new DataAllValuesFrom();
        DataPropertySequence seq = new DataPropertySequence();
        for (int i = 0; i != node.getChildren().size() - 1; i++) {
            DataPropertyElement element = new DataPropertyElement();
            element.setDataProperty(loadExpDataProperty(node.getChildren().get(i)));
            element.setIndex(i);
            seq.addDataPropertyElements(element);
        }
        expression.setDataPropertySeq(seq);
        expression.setDatarange(loadExpDatarange(node.getChildren().get(1)));
        return expression;
    }

    /**
     * Loads a DataHasValue expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpDataHasValue(ASTNode node) {
        DataHasValue expression = new DataHasValue();
        expression.setDataProperty(loadExpDataProperty(node.getChildren().get(0)));
        expression.setLiteral(loadExpLiteral(node.getChildren().get(1)));
        return expression;
    }

    /**
     * Loads a DataMinCardinality expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpDataMinCardinality(ASTNode node) {
        DataMinCardinality expression = new DataMinCardinality();
        expression.setCardinality(loadExpLiteral(node.getChildren().get(0)));
        expression.setDataProperty(loadExpDataProperty(node.getChildren().get(1)));
        if (node.getChildren().size() > 2)
            expression.setDatarange(loadExpDatarange(node.getChildren().get(2)));
        return expression;
    }

    /**
     * Loads a DataMaxCardinality expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpDataMaxCardinality(ASTNode node) {
        DataMaxCardinality expression = new DataMaxCardinality();
        expression.setCardinality(loadExpLiteral(node.getChildren().get(0)));
        expression.setDataProperty(loadExpDataProperty(node.getChildren().get(1)));
        if (node.getChildren().size() > 2)
            expression.setDatarange(loadExpDatarange(node.getChildren().get(2)));
        return expression;
    }

    /**
     * Loads a DataExactCardinality expression from an AST node
     *
     * @param node The AST node
     * @return The class expression
     */
    protected ClassExpression loadExpDataExactCardinality(ASTNode node) {
        DataExactCardinality expression = new DataExactCardinality();
        expression.setCardinality(loadExpLiteral(node.getChildren().get(0)));
        expression.setDataProperty(loadExpDataProperty(node.getChildren().get(1)));
        if (node.getChildren().size() > 2)
            expression.setDatarange(loadExpDatarange(node.getChildren().get(2)));
        return expression;
    }

    /**
     * Loads an individual expression from an AST node
     *
     * @param node The AST node
     * @return The individual expression
     */
    protected IndividualExpression loadExpIndividual(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalOWL2Lexer.ID.BLANK_NODE_LABEL:
                return loadExpAnonymousIndividual(node);
        }
        return loadExpEntity(node);
    }

    /**
     * Loads an anonymous individual from an AST node
     *
     * @param node The AST node
     * @return The anonymous individual
     */
    protected AnonymousIndividual loadExpAnonymousIndividual(ASTNode node) {
        String name = node.getSymbol().getValue().substring(2);
        AnonymousIndividual result = blanks.get(name);
        if (result != null)
            return result;
        result = new AnonymousIndividual();
        result.setNodeID(UUID.randomUUID().toString());
        blanks.put(name, result);
        return result;
    }

    /**
     * Loads datarange expression from an AST node
     *
     * @param node The AST node
     * @return The datarange expression
     */
    protected Datarange loadExpDatarange(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalOWL2Parser.ID.expDataIntersectionOf:
                return loadExpDataIntersectionOf(node);
            case FunctionalOWL2Parser.ID.expDataUnionOf:
                return loadExpDataUnionOf(node);
            case FunctionalOWL2Parser.ID.expDataComplementOf:
                return loadExpDataComplementOf(node);
            case FunctionalOWL2Parser.ID.expDataOneOf:
                return loadExpDataOneOf(node);
            case FunctionalOWL2Parser.ID.expDatatypeRestriction:
                return loadExpDatatypeRestriction(node);
        }
        return loadExpEntity(node);
    }

    /**
     * Loads a DataIntersectionOf expression from an AST node
     *
     * @param node The AST node
     * @return The datarange expression
     */
    protected Datarange loadExpDataIntersectionOf(ASTNode node) {
        DataIntersectionOf expression = new DataIntersectionOf();
        DatarangeSequence seq = new DatarangeSequence();
        for (int i = 0; i != node.getChildren().size(); i++) {
            DatarangeElement element = new DatarangeElement();
            element.setDatarange(loadExpDatarange(node.getChildren().get(i)));
            element.setIndex(i);
            seq.addDatarangeElements(element);
        }
        expression.setDatarangeSeq(seq);
        return expression;
    }

    /**
     * Loads a DataUnionOf expression from an AST node
     *
     * @param node The AST node
     * @return The datarange expression
     */
    protected Datarange loadExpDataUnionOf(ASTNode node) {
        DataUnionOf expression = new DataUnionOf();
        DatarangeSequence seq = new DatarangeSequence();
        for (int i = 0; i != node.getChildren().size(); i++) {
            DatarangeElement element = new DatarangeElement();
            element.setDatarange(loadExpDatarange(node.getChildren().get(i)));
            element.setIndex(i);
            seq.addDatarangeElements(element);
        }
        expression.setDatarangeSeq(seq);
        return expression;
    }

    /**
     * Loads a DataComplementOf expression from an AST node
     *
     * @param node The AST node
     * @return The datarange expression
     */
    protected Datarange loadExpDataComplementOf(ASTNode node) {
        DataComplementOf expression = new DataComplementOf();
        expression.setDatarange(loadExpDatarange(node.getChildren().get(0)));
        return expression;
    }

    /**
     * Loads a DataOneOf expression from an AST node
     *
     * @param node The AST node
     * @return The datarange expression
     */
    protected Datarange loadExpDataOneOf(ASTNode node) {
        DataOneOf expression = new DataOneOf();
        LiteralSequence seq = new LiteralSequence();
        for (int i = 0; i != node.getChildren().size(); i++) {
            LiteralElement element = new LiteralElement();
            element.setLiteral(loadExpLiteral(node.getChildren().get(i)));
            element.setIndex(i);
            seq.addLiteralElements(element);
        }
        expression.setLiteralSeq(seq);
        return expression;
    }

    /**
     * Loads a DatatypeRestriction expression from an AST node
     *
     * @param node The AST node
     * @return The datarange expression
     */
    protected Datarange loadExpDatatypeRestriction(ASTNode node) {
        DatatypeRestriction expression = new DatatypeRestriction();
        expression.setDatarange(loadExpDatarange(node.getChildren().get(0)));
        for (ASTNode child : node.getChildren().get(1).getChildren())
            expression.addFacetRestrictions(loadExpFacetRestriction(child));
        return expression;
    }

    /**
     * Loads a facet restriction
     *
     * @param node The AST node
     * @return The facet restriction
     */
    protected FacetRestriction loadExpFacetRestriction(ASTNode node) {
        FacetRestriction restriction = new FacetRestriction();
        restriction.setConstrainingFacet(loadEntity(node.getChildren().get(0)));
        restriction.setConstrainingValue((Literal) loadExpLiteral(node.getChildren().get(1)));
        return restriction;
    }

    /**
     * Loads an object property expression from an AST node
     *
     * @param node The AST node
     * @return The object property expression
     */
    protected ObjectPropertyExpression loadExpObjectProperty(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalOWL2Parser.ID.expInverseObjectProperty:
                return loaExpInverseObjectProperty(node);
        }
        return loadExpEntity(node);
    }

    /**
     * Loads an inverse object property expression from an AST node
     *
     * @param node The AST node
     * @return The object property expression
     */
    protected ObjectPropertyExpression loaExpInverseObjectProperty(ASTNode node) {
        ObjectInverseOf expression = new ObjectInverseOf();
        expression.setInverse(loadExpObjectProperty(node.getChildren().get(0)));
        return expression;
    }

    /**
     * Loads an data property expression from an AST node
     *
     * @param node The AST node
     * @return The data property expression
     */
    protected DataPropertyExpression loadExpDataProperty(ASTNode node) {
        return loadExpEntity(node);
    }

    /**
     * Loads a literal expression from an AST node
     *
     * @param node The AST node
     * @return The literal expression
     */
    protected LiteralExpression loadExpLiteral(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalOWL2Lexer.ID.INTEGER:
                return loadExpIntegerLiteral(node);
            case FunctionalOWL2Parser.ID.literalTyped:
                return loadExpTypedLiteral(node);
            case FunctionalOWL2Parser.ID.literalLang:
                return loadExpLangTaggedLiteral(node);
            default:
                return loadExpStringLiteral(node);
        }
    }

    /**
     * Loads a simple string literal from an AST node
     *
     * @param node The AST node
     * @return The literal expression
     */
    protected Literal loadExpIntegerLiteral(ASTNode node) {
        Literal result = new Literal();
        String value = node.getSymbol().getValue();
        result.setLexicalValue(value);
        IRI type = new IRI();
        type.setHasValue(Vocabulary.xsdInt);
        result.setMemberOf(type);
        return result;
    }

    /**
     * Loads a simple string literal from an AST node
     *
     * @param node The AST node
     * @return The literal expression
     */
    protected Literal loadExpStringLiteral(ASTNode node) {
        Literal result = new Literal();
        String value = node.getChildren().get(0).getSymbol().getValue();
        value = value.substring(1, value.length() - 1);
        result.setLexicalValue(value);
        IRI type = new IRI();
        type.setHasValue(Vocabulary.xsdString);
        result.setMemberOf(type);
        return result;
    }

    /**
     * Loads a typed literal from an AST node
     *
     * @param node The AST node
     * @return The literal expression
     */
    protected Literal loadExpTypedLiteral(ASTNode node) {
        Literal result = new Literal();
        String value = node.getChildren().get(0).getSymbol().getValue();
        value = value.substring(1, value.length() - 1);
        result.setLexicalValue(value);
        IRI type = new IRI();
        type.setHasValue(loadIRI(node.getChildren().get(1)));
        result.setMemberOf(type);
        return result;
    }

    /**
     * Loads a language-tagged literal from an AST node
     *
     * @param node The AST node
     * @return The literal expression
     */
    protected Literal loadExpLangTaggedLiteral(ASTNode node) {
        Literal result = new Literal();
        String value = node.getChildren().get(0).getSymbol().getValue();
        value = value.substring(1, value.length() - 1);
        result.setLexicalValue(value);
        String tag = node.getChildren().get(1).getSymbol().getValue();
        result.setLangTag(tag.substring(1));
        IRI type = new IRI();
        type.setHasValue(Vocabulary.rdfLangString);
        result.setMemberOf(type);
        return result;
    }

    /**
     * Loads an annotation property from an AST node
     *
     * @param node The AST node
     * @return The annotation value
     */
    protected IRI loadExpAnnotationProperty(ASTNode node) {
        return loadEntity(node);
    }

    /**
     * Loads an annotation subject from an AST node
     *
     * @param node The AST node
     * @return The annotation value
     */
    protected AnnotationSubject loadExpAnnotationSubject(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalOWL2Lexer.ID.BLANK_NODE_LABEL:
                return loadExpAnonymousIndividual(node);
            default:
                return loadEntity(node);
        }
    }

    /**
     * Loads an annotation value from an AST node
     *
     * @param node The AST node
     * @return The annotation value
     */
    protected AnnotationValue loadExpAnnotationValue(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case FunctionalOWL2Lexer.ID.IRIREF:
            case FunctionalOWL2Lexer.ID.PNAME_LN:
                return loadEntity(node);
            case FunctionalOWL2Lexer.ID.BLANK_NODE_LABEL:
                return loadExpAnonymousIndividual(node);
            default:
                return (AnnotationValue) loadExpLiteral(node);
        }
    }
}
