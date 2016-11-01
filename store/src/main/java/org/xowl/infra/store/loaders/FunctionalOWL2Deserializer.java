/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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
 ******************************************************************************/

package org.xowl.infra.store.loaders;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.lang.owl2.*;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.http.URIUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implements the deserialization of OWL2 ontologies in the functional-style syntax
 *
 * @author Laurent Wouters
 */
public class FunctionalOWL2Deserializer {
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

    /**
     * De-serailizes the specified document identified by the specified resource URI
     *
     * @param resourceURI The URI of the resource to deserialize
     * @param document    The AST of the document to deserialize
     * @return The deserailized data
     */
    public OWLLoaderResult deserialize(String resourceURI, ASTNode document) {
        this.resource = resourceURI;
        this.baseURI = resourceURI;
        this.namespaces = new HashMap<>();
        this.blanks = new HashMap<>();
        this.cache = null;
        loadDocument(document);
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
        String prefix = node.getChildren().get(0).getValue();
        String uri = node.getChildren().get(1).getValue();
        prefix = prefix.substring(0, prefix.length() - 1);
        uri = TextUtils.unescape(uri.substring(1, uri.length() - 1));
        namespaces.put(prefix, uri);
    }

    /**
     * Loads an IRI from an AST node
     *
     * @param node An AST node
     * @return The loaded IRI
     */
    protected String loadIRI(ASTNode node) {
        if (node.getSymbol().getName().equals("IRIREF")) {
            String value = node.getValue();
            value = TextUtils.unescape(value.substring(1, value.length() - 1));
            return URIUtils.resolveRelative(baseURI, value);
        } else {
            // this is a local name
            return getIRIForLocalName(node.getValue());
        }
    }

    /**
     * Gets the full IRI for the specified escaped local name
     *
     * @param value An escaped local name
     * @return The equivalent full IRI
     */
    protected String getIRIForLocalName(String value) {
        value = TextUtils.unescape(value);
        int index = 0;
        while (index != value.length()) {
            if (value.charAt(index) == ':') {
                String prefix = value.substring(0, index);
                String uri = namespaces.get(prefix);
                if (uri != null) {
                    String name = value.substring(index + 1);
                    return URIUtils.resolveRelative(baseURI, TextUtils.unescape(uri + name));
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
        Annotation result = Owl2Factory.newAnnotation();
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
        switch (node.getSymbol().getName()) {
            case "axiomDeclaration":
                return loadAxiomDeclaration(node);
            case "axiomSubClassOf":
                return loadAxiomSubClassOf(node);
            case "axiomEquivalentClasses":
                return loadAxiomEquivalentClasses(node);
            case "axiomDisjointClasses":
                return loadAxiomDisjointClasses(node);
            case "axiomDisjointUnion":
                return loadAxiomDisjointUnion(node);
            case "axiomSubOjectPropertyOf":
                return loadAxiomSubObjectPropertyOf(node);
            case "axiomEquivalentObjectProperties":
                return loadAxiomEquivalentObjectProperties(node);
            case "axiomDisjointObjectProperties":
                return loadAxiomDisjointObjectProperties(node);
            case "axiomInverseObjectProperties":
                return loadAxiomInverseObjectProperties(node);
            case "axiomObjectPropertyDomain":
                return loadAxiomObjectPropertyDomain(node);
            case "axiomObjectPropertyRange":
                return loadAxiomObjectPropertyRange(node);
            case "axiomFunctionalObjectProperty":
                return loadAxiomFunctionalObjectProperty(node);
            case "axiomInverseFunctionalObjectProperty":
                return loadAxiomInverseFunctionalObjectProperty(node);
            case "axiomReflexiveObjectProperty":
                return loadAxiomReflexiveObjectProperty(node);
            case "axiomIrreflexiveObjectProperty":
                return loadAxiomIrreflexiveObjectProperty(node);
            case "axiomSymmetricObjectProperty":
                return loadAxiomSymmetricObjectProperty(node);
            case "axiomAsymmetricObjectProperty":
                return loadAxiomAsymmetricObjectProperty(node);
            case "axiomTransitiveObjectProperty":
                return loadAxiomTransitiveObjectProperty(node);
            case "axiomSubDataPropertyOf":
                return loadAxiomSubDataPropertyOf(node);
            case "axiomEquivalentDataProperties":
                return loadAxiomEquivalentDataProperties(node);
            case "axiomDisjointDataProperties":
                return loadAxiomDisjointDataProperties(node);
            case "axiomDataPropertyDomain":
                return loadAxiomDataPropertyDomain(node);
            case "axiomDataPropertyRange":
                return loadAxiomDataPropertyRange(node);
            case "axiomFunctionalDataProperty":
                return loadAxiomFunctionalDataProperty(node);
            case "axiomDatatype":
                return loadAxiomDatatypeDefinition(node);
            case "axiomHasKey":
                return loadAxiomHasKey(node);
            case "axiomSameIndividual":
                return loadAxiomSameIndividual(node);
            case "axiomDifferentIndividuals":
                return loadAxiomDifferentIndividuals(node);
            case "axiomClassAssertion":
                return loadAxiomClassAssertion(node);
            case "axiomObjectPropertyAssertion":
                return loadAxiomObjectPropertyAssertion(node);
            case "axiomNegativeObjectPropertyAssertion":
                return loadAxiomNegativeObjectPropertyAssertion(node);
            case "axiomDataPropertyAssertion":
                return loadAxiomDataPropertyAssertion(node);
            case "axiomNegativeDataPropertyAssertion":
                return loadAxiomNegativeDataPropertyAssertion(node);
            case "axiomAnnotationAssertion":
                return loadAxiomAnnotationAssertion(node);
            case "axiomSubAnnotationPropertyOf":
                return loadAxiomSubAnnotationPropertyOf(node);
            case "axiomAnnotationPropertyDomain":
                return loadAxiomAnnotationPropertyDomain(node);
            case "axiomAnnotationPropertyRange":
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
        Declaration axiom = Owl2Factory.newDeclaration();
        loadAxiomBase(node, axiom);
        axiom.setType(node.getChildren().get(1).getValue());
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
        SubClassOf axiom = Owl2Factory.newSubClassOf();
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
        EquivalentClasses axiom = Owl2Factory.newEquivalentClasses();
        loadAxiomBase(node, axiom);
        ClassSequence seq = Owl2Factory.newClassSequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            ClassElement element = Owl2Factory.newClassElement();
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
        DisjointClasses axiom = Owl2Factory.newDisjointClasses();
        loadAxiomBase(node, axiom);
        ClassSequence seq = Owl2Factory.newClassSequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            ClassElement element = Owl2Factory.newClassElement();
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
        DisjointUnion axiom = Owl2Factory.newDisjointUnion();
        loadAxiomBase(node, axiom);
        axiom.setClasse(loadExpClass(node.getChildren().get(1)));
        ClassSequence seq = Owl2Factory.newClassSequence();
        for (int i = 2; i != node.getChildren().size(); i++) {
            ClassElement element = Owl2Factory.newClassElement();
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
        SubObjectPropertyOf axiom = Owl2Factory.newSubObjectPropertyOf();
        loadAxiomBase(node, axiom);
        if (node.getChildren().get(1).getSymbol().getName().equals("expObjectPropertyChain")) {
            ObjectPropertySequence seq = Owl2Factory.newObjectPropertySequence();
            int index = 0;
            for (ASTNode child : node.getChildren().get(1).getChildren()) {
                ObjectPropertyElement element = Owl2Factory.newObjectPropertyElement();
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
        EquivalentObjectProperties axiom = Owl2Factory.newEquivalentObjectProperties();
        loadAxiomBase(node, axiom);
        ObjectPropertySequence seq = Owl2Factory.newObjectPropertySequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            ObjectPropertyElement element = Owl2Factory.newObjectPropertyElement();
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
        DisjointObjectProperties axiom = Owl2Factory.newDisjointObjectProperties();
        loadAxiomBase(node, axiom);
        ObjectPropertySequence seq = Owl2Factory.newObjectPropertySequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            ObjectPropertyElement element = Owl2Factory.newObjectPropertyElement();
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
        InverseObjectProperties axiom = Owl2Factory.newInverseObjectProperties();
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
        ObjectPropertyDomain axiom = Owl2Factory.newObjectPropertyDomain();
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
        ObjectPropertyRange axiom = Owl2Factory.newObjectPropertyRange();
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
        FunctionalObjectProperty axiom = Owl2Factory.newFunctionalObjectProperty();
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
        InverseFunctionalObjectProperty axiom = Owl2Factory.newInverseFunctionalObjectProperty();
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
        ReflexiveObjectProperty axiom = Owl2Factory.newReflexiveObjectProperty();
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
        IrreflexiveObjectProperty axiom = Owl2Factory.newIrreflexiveObjectProperty();
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
        SymmetricObjectProperty axiom = Owl2Factory.newSymmetricObjectProperty();
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
        AsymmetricObjectProperty axiom = Owl2Factory.newAsymmetricObjectProperty();
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
        TransitiveObjectProperty axiom = Owl2Factory.newTransitiveObjectProperty();
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
        SubDataPropertyOf axiom = Owl2Factory.newSubDataPropertyOf();
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
        EquivalentDataProperties axiom = Owl2Factory.newEquivalentDataProperties();
        loadAxiomBase(node, axiom);
        DataPropertySequence seq = Owl2Factory.newDataPropertySequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            DataPropertyElement element = Owl2Factory.newDataPropertyElement();
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
        DisjointDataProperties axiom = Owl2Factory.newDisjointDataProperties();
        loadAxiomBase(node, axiom);
        DataPropertySequence seq = Owl2Factory.newDataPropertySequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            DataPropertyElement element = Owl2Factory.newDataPropertyElement();
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
        DataPropertyDomain axiom = Owl2Factory.newDataPropertyDomain();
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
        DataPropertyRange axiom = Owl2Factory.newDataPropertyRange();
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
        FunctionalDataProperty axiom = Owl2Factory.newFunctionalDataProperty();
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
        DatatypeDefinition axiom = Owl2Factory.newDatatypeDefinition();
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
        HasKey axiom = Owl2Factory.newHasKey();
        loadAxiomBase(node, axiom);
        axiom.setClasse(loadExpClass(node.getChildren().get(1)));
        ObjectPropertySequence seq1 = Owl2Factory.newObjectPropertySequence();
        int index = 0;
        for (ASTNode child : node.getChildren().get(1).getChildren()) {
            ObjectPropertyElement element = Owl2Factory.newObjectPropertyElement();
            element.setObjectProperty(loadExpObjectProperty(child));
            element.setIndex(index);
            seq1.addObjectPropertyElements(element);
            index++;
        }
        axiom.setObjectPropertySeq(seq1);
        DataPropertySequence seq2 = Owl2Factory.newDataPropertySequence();
        index = 0;
        for (ASTNode child : node.getChildren().get(2).getChildren()) {
            DataPropertyElement element = Owl2Factory.newDataPropertyElement();
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
        SameIndividual axiom = Owl2Factory.newSameIndividual();
        loadAxiomBase(node, axiom);
        IndividualSequence seq = Owl2Factory.newIndividualSequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            IndividualElement element = Owl2Factory.newIndividualElement();
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
        DifferentIndividuals axiom = Owl2Factory.newDifferentIndividuals();
        loadAxiomBase(node, axiom);
        IndividualSequence seq = Owl2Factory.newIndividualSequence();
        for (int i = 1; i != node.getChildren().size(); i++) {
            IndividualElement element = Owl2Factory.newIndividualElement();
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
        ClassAssertion axiom = Owl2Factory.newClassAssertion();
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
        ObjectPropertyAssertion axiom = Owl2Factory.newObjectPropertyAssertion();
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
        NegativeObjectPropertyAssertion axiom = Owl2Factory.newNegativeObjectPropertyAssertion();
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
        DataPropertyAssertion axiom = Owl2Factory.newDataPropertyAssertion();
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
        NegativeDataPropertyAssertion axiom = Owl2Factory.newNegativeDataPropertyAssertion();
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
        AnnotationAssertion axiom = Owl2Factory.newAnnotationAssertion();
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
        SubAnnotationPropertyOf axiom = Owl2Factory.newSubAnnotationPropertyOf();
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
        AnnotationPropertyDomain axiom = Owl2Factory.newAnnotationPropertyDomain();
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
        AnnotationPropertyRange axiom = Owl2Factory.newAnnotationPropertyRange();
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
        switch (node.getSymbol().getName()) {
            case "IRIREF":
            case "PNAME_LN":
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
        switch (node.getSymbol().getName()) {
            case "IRIREF":
            case "PNAME_LN":
                // named individual
                IRI entity = Owl2Factory.newIRI();
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
        switch (node.getSymbol().getName()) {
            case "expObjectInterfactionOf":
                return loadExpObjectIntersectionOf(node);
            case "expObjectUnionOf":
                return loadExpObjectUnionOf(node);
            case "expObjectComplementOf":
                return loadExpObjectComplementOf(node);
            case "expObjectOneOf":
                return loadExpObjectOneOf(node);
            case "expObjectSomeValuesFrom":
                return loadExpObjectSomeValuesFrom(node);
            case "expObjectAllValuesFrom":
                return loadExpObjectAllValuesFrom(node);
            case "expObjectHasValue":
                return loadExpObjectHasValue(node);
            case "expObjectHasSelf":
                return loadExpObjectHasSelf(node);
            case "expObjectMinCardinality":
                return loadExpObjectMinCardinality(node);
            case "expObjectMaxCardinality":
                return loadExpObjectMaxCardinality(node);
            case "expObjectExactCardinality":
                return loadExpObjectExactCardinality(node);
            case "expDataSomeValuesFrom":
                return loadExpDataSomeValuesFrom(node);
            case "expDataAllValuesFrom":
                return loadExpDataAllValuesFrom(node);
            case "expDataHasValue":
                return loadExpDataHasValue(node);
            case "expDataMinCardinality":
                return loadExpDataMinCardinality(node);
            case "expDataMaxCardinality":
                return loadExpDataMaxCardinality(node);
            case "expDataExactCardinality":
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
        ObjectIntersectionOf expression = Owl2Factory.newObjectIntersectionOf();
        ClassSequence seq = Owl2Factory.newClassSequence();
        for (int i = 0; i != node.getChildren().size(); i++) {
            ClassElement element = Owl2Factory.newClassElement();
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
        ObjectUnionOf expression = Owl2Factory.newObjectUnionOf();
        ClassSequence seq = Owl2Factory.newClassSequence();
        for (int i = 0; i != node.getChildren().size(); i++) {
            ClassElement element = Owl2Factory.newClassElement();
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
        ObjectComplementOf expression = Owl2Factory.newObjectComplementOf();
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
        ObjectOneOf expression = Owl2Factory.newObjectOneOf();
        IndividualSequence seq = Owl2Factory.newIndividualSequence();
        for (int i = 0; i != node.getChildren().size(); i++) {
            IndividualElement element = Owl2Factory.newIndividualElement();
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
        ObjectSomeValuesFrom expression = Owl2Factory.newObjectSomeValuesFrom();
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
        ObjectAllValuesFrom expression = Owl2Factory.newObjectAllValuesFrom();
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
        ObjectHasValue expression = Owl2Factory.newObjectHasValue();
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
        ObjectHasSelf expression = Owl2Factory.newObjectHasSelf();
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
        ObjectMinCardinality expression = Owl2Factory.newObjectMinCardinality();
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
        ObjectMaxCardinality expression = Owl2Factory.newObjectMaxCardinality();
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
        ObjectExactCardinality expression = Owl2Factory.newObjectExactCardinality();
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
        DataSomeValuesFrom expression = Owl2Factory.newDataSomeValuesFrom();
        DataPropertySequence seq = Owl2Factory.newDataPropertySequence();
        for (int i = 0; i != node.getChildren().size() - 1; i++) {
            DataPropertyElement element = Owl2Factory.newDataPropertyElement();
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
        DataAllValuesFrom expression = Owl2Factory.newDataAllValuesFrom();
        DataPropertySequence seq = Owl2Factory.newDataPropertySequence();
        for (int i = 0; i != node.getChildren().size() - 1; i++) {
            DataPropertyElement element = Owl2Factory.newDataPropertyElement();
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
        DataHasValue expression = Owl2Factory.newDataHasValue();
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
        DataMinCardinality expression = Owl2Factory.newDataMinCardinality();
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
        DataMaxCardinality expression = Owl2Factory.newDataMaxCardinality();
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
        DataExactCardinality expression = Owl2Factory.newDataExactCardinality();
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
        switch (node.getSymbol().getName()) {
            case "BLANK_NODE_LABEL":
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
        String name = node.getValue().substring(2);
        AnonymousIndividual result = blanks.get(name);
        if (result != null)
            return result;
        result = Owl2Factory.newAnonymousIndividual();
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
        switch (node.getSymbol().getName()) {
            case "expDataIntersectionOf":
                return loadExpDataIntersectionOf(node);
            case "expDataUnionOf":
                return loadExpDataUnionOf(node);
            case "expDataComplementOf":
                return loadExpDataComplementOf(node);
            case "expDataOneOf":
                return loadExpDataOneOf(node);
            case "expDatatypeRestriction":
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
        DataIntersectionOf expression = Owl2Factory.newDataIntersectionOf();
        DatarangeSequence seq = Owl2Factory.newDatarangeSequence();
        for (int i = 0; i != node.getChildren().size(); i++) {
            DatarangeElement element = Owl2Factory.newDatarangeElement();
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
        DataUnionOf expression = Owl2Factory.newDataUnionOf();
        DatarangeSequence seq = Owl2Factory.newDatarangeSequence();
        for (int i = 0; i != node.getChildren().size(); i++) {
            DatarangeElement element = Owl2Factory.newDatarangeElement();
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
        DataComplementOf expression = Owl2Factory.newDataComplementOf();
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
        DataOneOf expression = Owl2Factory.newDataOneOf();
        LiteralSequence seq = Owl2Factory.newLiteralSequence();
        for (int i = 0; i != node.getChildren().size(); i++) {
            LiteralElement element = Owl2Factory.newLiteralElement();
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
        DatatypeRestriction expression = Owl2Factory.newDatatypeRestriction();
        List<ASTNode> children = node.getChildren();
        expression.setDatarange(loadExpDatarange(children.get(0)));
        for (int i = 1; i != children.size(); i++)
            expression.addFacetRestrictions(loadExpFacetRestriction(children.get(i)));
        return expression;
    }

    /**
     * Loads a facet restriction
     *
     * @param node The AST node
     * @return The facet restriction
     */
    protected FacetRestriction loadExpFacetRestriction(ASTNode node) {
        FacetRestriction restriction = Owl2Factory.newFacetRestriction();
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
        switch (node.getSymbol().getName()) {
            case "expInverseObjectProperty":
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
        ObjectInverseOf expression = Owl2Factory.newObjectInverseOf();
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
        switch (node.getSymbol().getName()) {
            case "literalTyped":
                return loadExpTypedLiteral(node);
            case "literalLang":
                return loadExpLangTaggedLiteral(node);
            case "INTEGER":
                return loadExpIntegerLiteral(node);
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
        Literal result = Owl2Factory.newLiteral();
        String value = node.getValue();
        result.setLexicalValue(value);
        IRI type = Owl2Factory.newIRI();
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
        Literal result = Owl2Factory.newLiteral();
        String value = node.getChildren().get(0).getValue();
        value = value.substring(1, value.length() - 1);
        result.setLexicalValue(value);
        IRI type = Owl2Factory.newIRI();
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
        Literal result = Owl2Factory.newLiteral();
        String value = node.getChildren().get(0).getValue();
        value = value.substring(1, value.length() - 1);
        result.setLexicalValue(value);
        IRI type = Owl2Factory.newIRI();
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
        Literal result = Owl2Factory.newLiteral();
        String value = node.getChildren().get(0).getValue();
        value = value.substring(1, value.length() - 1);
        result.setLexicalValue(value);
        String tag = node.getChildren().get(1).getValue();
        result.setLangTag(tag.substring(1));
        IRI type = Owl2Factory.newIRI();
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
        switch (node.getSymbol().getName()) {
            case "BLANK_NODE_LABEL":
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
        switch (node.getSymbol().getName()) {
            case "IRIREF":
            case "PNAME_LN":
                return loadEntity(node);
            case "BLANK_NODE_LABEL":
                return loadExpAnonymousIndividual(node);
            default:
                return (AnnotationValue) loadExpLiteral(node);
        }
    }
}
