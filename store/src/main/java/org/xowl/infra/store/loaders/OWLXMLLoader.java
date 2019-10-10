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

import fr.cenotelie.commons.utils.TextUtils;
import fr.cenotelie.commons.utils.http.URIUtils;
import fr.cenotelie.commons.utils.logging.Logger;
import fr.cenotelie.commons.utils.xml.Xml;
import fr.cenotelie.commons.utils.xml.XmlElement;
import fr.cenotelie.hime.redist.ParseResult;
import org.w3c.dom.Document;
import org.xowl.infra.lang.owl2.*;
import org.xowl.infra.store.Vocabulary;

import java.io.Reader;
import java.util.*;

/**
 * Loader for OWL XML sources
 *
 * @author Laurent Wouters
 */
public class OWLXMLLoader implements Loader {
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
    private OWLLoaderResult cache;

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RDFLoaderResult loadRDF(Logger logger, Reader reader, String resourceIRI, String graphIRI) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OWLLoaderResult loadOWL(Logger logger, Reader reader, String uri) {
        this.resource = uri;
        this.namespaces = new HashMap<>();
        this.blanks = new HashMap<>();
        try {
            Document document = Xml.parse(reader);
            XmlElement root = new XmlElement(document.getDocumentElement(), uri);
            loadOntology(root);
        } catch (Exception ex) {
            logger.error(ex);
            return null;
        }
        return cache;
    }

    /**
     * Loads an ontology from the specified XML node
     *
     * @param node A XML node
     */
    private void loadOntology(XmlElement node) {
        baseURI = node.getAttribute("ontologyIRI");
        if (baseURI == null)
            baseURI = resource;
        String version = node.getAttribute("versionIRI");
        cache = new OWLLoaderResult(baseURI, version);
        for (XmlElement child : node) {
            switch (child.getNodeName()) {
                case Vocabulary.OWL2.ontoPrefix:
                    loadPrefixID(child);
                    break;
                case Vocabulary.OWL2.ontoImport:
                    cache.addImport(URIUtils.resolveRelative(baseURI, TextUtils.unescape(child.getContent())));
                    break;
                case Vocabulary.OWL2.ontoAnnotation:
                    cache.addAnnotation(loadAnnotation(child));
                    break;
                default:
                    cache.addAxiom(loadAxiom(node));
                    break;
            }
        }
    }

    /**
     * Loads a prefix and its associated namespace represented by the specified XML node
     *
     * @param node A XML node
     */
    private void loadPrefixID(XmlElement node) {
        String prefix = node.getAttribute("name");
        String uri = node.getAttribute("IRI");
        namespaces.put(prefix, TextUtils.unescape(uri));
    }

    /**
     * Gets the full IRI for the specified escaped local name
     *
     * @param value An escaped local name
     * @return The equivalent full IRI
     */
    private String getIRIForLocalName(String value) {
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
     * Loads an annotation from A XML node
     *
     * @param node The XML node
     * @return The annotation
     */
    private Annotation loadAnnotation(XmlElement node) {
        Annotation result = Owl2Factory.newAnnotation();
        // loads the annotations on this annotation
        for (XmlElement child : node) {
            switch (child.getNodeName()) {
                case Vocabulary.OWL2.ontoAnnotation:
                    result.addAnnotations(loadAnnotation(child));
                    break;
                case Vocabulary.OWL2.entityAnnotationProperty:
                    result.setAnnotProperty(loadExpAnnotationProperty(child));
                    break;
                default:
                    result.setAnnotValue(loadExpAnnotationValue(child));
            }
        }
        return result;
    }

    /**
     * Loads an axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiom(XmlElement node) {
        switch (node.getNodeName()) {
            case Vocabulary.OWL2.axiomDeclaration:
                return loadAxiomDeclaration(node);
            case Vocabulary.OWL2.axiomSubClassOf:
                return loadAxiomSubClassOf(node);
            case Vocabulary.OWL2.axiomEquivalentClasses:
                return loadAxiomEquivalentClasses(node);
            case Vocabulary.OWL2.axiomDisjointClasses:
                return loadAxiomDisjointClasses(node);
            case Vocabulary.OWL2.axiomDisjointUnion:
                return loadAxiomDisjointUnion(node);
            case Vocabulary.OWL2.axiomSubObjectPropertyOf:
                return loadAxiomSubObjectPropertyOf(node);
            case Vocabulary.OWL2.axiomEquivalentObjectProperties:
                return loadAxiomEquivalentObjectProperties(node);
            case Vocabulary.OWL2.axiomDisjointObjectProperties:
                return loadAxiomDisjointObjectProperties(node);
            case Vocabulary.OWL2.axiomInverseObjectProperties:
                return loadAxiomInverseObjectProperties(node);
            case Vocabulary.OWL2.axiomObjectPropertyDomain:
                return loadAxiomObjectPropertyDomain(node);
            case Vocabulary.OWL2.axiomObjectPropertyRange:
                return loadAxiomObjectPropertyRange(node);
            case Vocabulary.OWL2.axiomFunctionalObjectProperty:
                return loadAxiomFunctionalObjectProperty(node);
            case Vocabulary.OWL2.axiomInverseFunctionalObjectProperty:
                return loadAxiomInverseFunctionalObjectProperty(node);
            case Vocabulary.OWL2.axiomReflexiveObjectProperty:
                return loadAxiomReflexiveObjectProperty(node);
            case Vocabulary.OWL2.axiomIrreflexiveObjectProperty:
                return loadAxiomIrreflexiveObjectProperty(node);
            case Vocabulary.OWL2.axiomSymmetricObjectProperty:
                return loadAxiomSymmetricObjectProperty(node);
            case Vocabulary.OWL2.axiomAsymmetricObjectProperty:
                return loadAxiomAsymmetricObjectProperty(node);
            case Vocabulary.OWL2.axiomTransitiveObjectProperty:
                return loadAxiomTransitiveObjectProperty(node);
            case Vocabulary.OWL2.axiomSubDataPropertyOf:
                return loadAxiomSubDataPropertyOf(node);
            case Vocabulary.OWL2.axiomEquivalentDataProperties:
                return loadAxiomEquivalentDataProperties(node);
            case Vocabulary.OWL2.axiomDisjointDataProperties:
                return loadAxiomDisjointDataProperties(node);
            case Vocabulary.OWL2.axiomDataPropertyDomain:
                return loadAxiomDataPropertyDomain(node);
            case Vocabulary.OWL2.axiomDataPropertyRange:
                return loadAxiomDataPropertyRange(node);
            case Vocabulary.OWL2.axiomFunctionalDataProperty:
                return loadAxiomFunctionalDataProperty(node);
            case Vocabulary.OWL2.axiomDatatypeDefinition:
                return loadAxiomDatatypeDefinition(node);
            case Vocabulary.OWL2.axiomHasKey:
                return loadAxiomHasKey(node);
            case Vocabulary.OWL2.axiomSameIndividual:
                return loadAxiomSameIndividual(node);
            case Vocabulary.OWL2.axiomDifferentIndividuals:
                return loadAxiomDifferentIndividuals(node);
            case Vocabulary.OWL2.axiomClassAssertion:
                return loadAxiomClassAssertion(node);
            case Vocabulary.OWL2.axiomObjectPropertyAssertion:
                return loadAxiomObjectPropertyAssertion(node);
            case Vocabulary.OWL2.axiomNegativeObjectPropertyAssertion:
                return loadAxiomNegativeObjectPropertyAssertion(node);
            case Vocabulary.OWL2.axiomDataPropertyAssertion:
                return loadAxiomDataPropertyAssertion(node);
            case Vocabulary.OWL2.axiomNegativeDataPropertyAssertion:
                return loadAxiomNegativeDataPropertyAssertion(node);
            case Vocabulary.OWL2.axiomAnnotationAssertion:
                return loadAxiomAnnotationAssertion(node);
            case Vocabulary.OWL2.axiomSubAnnotationPropertyOf:
                return loadAxiomSubAnnotationPropertyOf(node);
            case Vocabulary.OWL2.axiomAnnotationPropertyDomain:
                return loadAxiomAnnotationPropertyDomain(node);
            case Vocabulary.OWL2.axiomAnnotationPropertyRange:
                return loadAxiomAnnotationPropertyRange(node);
        }
        return null;
    }

    /**
     * Loads the base information of an axiom
     *
     * @param node  The axiom's XML node
     * @param axiom The axiom
     * @return The children except the annotations
     */
    private List<XmlElement> loadAxiomBase(XmlElement node, Axiom axiom) {
        axiom.setFile(baseURI);
        List<XmlElement> result = new ArrayList<>();
        for (XmlElement child : node) {
            if (Vocabulary.OWL2.ontoAnnotation.equals(child.getNodeName())) {
                axiom.addAnnotations(loadAnnotation(child));
            } else {
                result.add(child);
            }
        }
        return result;
    }

    /**
     * Loads a Declaration axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomDeclaration(XmlElement node) {
        Declaration axiom = Owl2Factory.newDeclaration();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setType(children.get(0).getNodeName());
        axiom.setEntity(loadEntity(children.get(0)));
        return axiom;
    }

    /**
     * Loads a SubClassOf axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomSubClassOf(XmlElement node) {
        SubClassOf axiom = Owl2Factory.newSubClassOf();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setClasse(loadExpClass(children.get(1)));
        axiom.setSuperClass(loadExpClass(children.get(2)));
        return axiom;
    }

    /**
     * Loads a EquivalentClasses axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomEquivalentClasses(XmlElement node) {
        EquivalentClasses axiom = Owl2Factory.newEquivalentClasses();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        ClassSequence seq = Owl2Factory.newClassSequence();
        for (int i = 0; i != children.size(); i++) {
            ClassElement element = Owl2Factory.newClassElement();
            element.setClasse(loadExpClass(children.get(i)));
            element.setIndex(i);
            seq.addClassElements(element);
        }
        axiom.setClassSeq(seq);
        return axiom;
    }

    /**
     * Loads a DisjointClasses axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomDisjointClasses(XmlElement node) {
        DisjointClasses axiom = Owl2Factory.newDisjointClasses();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        ClassSequence seq = Owl2Factory.newClassSequence();
        for (int i = 0; i != children.size(); i++) {
            ClassElement element = Owl2Factory.newClassElement();
            element.setClasse(loadExpClass(children.get(i)));
            element.setIndex(i);
            seq.addClassElements(element);
        }
        axiom.setClassSeq(seq);
        return axiom;
    }

    /**
     * Loads a DisjointUnion axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomDisjointUnion(XmlElement node) {
        DisjointUnion axiom = Owl2Factory.newDisjointUnion();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setClasse(loadExpClass(children.get(0)));
        ClassSequence seq = Owl2Factory.newClassSequence();
        for (int i = 1; i != children.size(); i++) {
            ClassElement element = Owl2Factory.newClassElement();
            element.setClasse(loadExpClass(children.get(i)));
            element.setIndex(i - 1);
            seq.addClassElements(element);
        }
        axiom.setClassSeq(seq);
        return axiom;
    }

    /**
     * Loads a SubObjectPropertyOf axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomSubObjectPropertyOf(XmlElement node) {
        SubObjectPropertyOf axiom = Owl2Factory.newSubObjectPropertyOf();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        if (Vocabulary.OWL2.expObjectPropertyChain.equals(children.get(0).getNodeName())) {
            ObjectPropertySequence seq = Owl2Factory.newObjectPropertySequence();
            int index = 0;
            for (XmlElement child : children.get(0)) {
                ObjectPropertyElement element = Owl2Factory.newObjectPropertyElement();
                element.setObjectProperty(loadExpObjectProperty(child));
                element.setIndex(index);
                index++;
                seq.addObjectPropertyElements(element);
            }
            axiom.setObjectPropertyChain(seq);
        } else {
            axiom.setObjectProperty(loadExpObjectProperty(children.get(0)));
        }
        axiom.setSuperObjectProperty(loadExpObjectProperty(children.get(1)));
        return axiom;
    }

    /**
     * Loads a EquivalentObjectProperties axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomEquivalentObjectProperties(XmlElement node) {
        EquivalentObjectProperties axiom = Owl2Factory.newEquivalentObjectProperties();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        ObjectPropertySequence seq = Owl2Factory.newObjectPropertySequence();
        for (int i = 0; i != children.size(); i++) {
            ObjectPropertyElement element = Owl2Factory.newObjectPropertyElement();
            element.setObjectProperty(loadExpObjectProperty(children.get(i)));
            element.setIndex(i);
            seq.addObjectPropertyElements(element);
        }
        axiom.setObjectPropertySeq(seq);
        return axiom;
    }

    /**
     * Loads a DisjointObjectProperties axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomDisjointObjectProperties(XmlElement node) {
        DisjointObjectProperties axiom = Owl2Factory.newDisjointObjectProperties();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        ObjectPropertySequence seq = Owl2Factory.newObjectPropertySequence();
        for (int i = 0; i != children.size(); i++) {
            ObjectPropertyElement element = Owl2Factory.newObjectPropertyElement();
            element.setObjectProperty(loadExpObjectProperty(children.get(i)));
            element.setIndex(i);
            seq.addObjectPropertyElements(element);
        }
        axiom.setObjectPropertySeq(seq);
        return axiom;
    }

    /**
     * Loads a InverseObjectProperties axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomInverseObjectProperties(XmlElement node) {
        InverseObjectProperties axiom = Owl2Factory.newInverseObjectProperties();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(children.get(0)));
        axiom.setInverse(loadExpObjectProperty(children.get(1)));
        return axiom;
    }

    /**
     * Loads a ObjectPropertyDomain axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomObjectPropertyDomain(XmlElement node) {
        ObjectPropertyDomain axiom = Owl2Factory.newObjectPropertyDomain();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(children.get(0)));
        axiom.setClasse(loadExpClass(children.get(1)));
        return axiom;
    }

    /**
     * Loads a ObjectPropertyRange axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomObjectPropertyRange(XmlElement node) {
        ObjectPropertyRange axiom = Owl2Factory.newObjectPropertyRange();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(children.get(0)));
        axiom.setClasse(loadExpClass(children.get(1)));
        return axiom;
    }

    /**
     * Loads a FunctionalObjectProperty axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomFunctionalObjectProperty(XmlElement node) {
        FunctionalObjectProperty axiom = Owl2Factory.newFunctionalObjectProperty();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(children.get(0)));
        return axiom;
    }

    /**
     * Loads a InverseFunctionalObjectProperty axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomInverseFunctionalObjectProperty(XmlElement node) {
        InverseFunctionalObjectProperty axiom = Owl2Factory.newInverseFunctionalObjectProperty();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(children.get(0)));
        return axiom;
    }

    /**
     * Loads a ReflexiveObjectProperty axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomReflexiveObjectProperty(XmlElement node) {
        ReflexiveObjectProperty axiom = Owl2Factory.newReflexiveObjectProperty();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(children.get(0)));
        return axiom;
    }

    /**
     * Loads a IrreflexiveObjectProperty axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomIrreflexiveObjectProperty(XmlElement node) {
        IrreflexiveObjectProperty axiom = Owl2Factory.newIrreflexiveObjectProperty();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(children.get(0)));
        return axiom;
    }

    /**
     * Loads a SymmetricObjectProperty axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomSymmetricObjectProperty(XmlElement node) {
        SymmetricObjectProperty axiom = Owl2Factory.newSymmetricObjectProperty();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(children.get(0)));
        return axiom;
    }

    /**
     * Loads a AsymmetricObjectProperty axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomAsymmetricObjectProperty(XmlElement node) {
        AsymmetricObjectProperty axiom = Owl2Factory.newAsymmetricObjectProperty();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(children.get(0)));
        return axiom;
    }

    /**
     * Loads a TransitiveObjectProperty axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomTransitiveObjectProperty(XmlElement node) {
        TransitiveObjectProperty axiom = Owl2Factory.newTransitiveObjectProperty();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(children.get(0)));
        return axiom;
    }

    /**
     * Loads a SubDataPropertyOf axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomSubDataPropertyOf(XmlElement node) {
        SubDataPropertyOf axiom = Owl2Factory.newSubDataPropertyOf();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setDataProperty(loadExpDataProperty(children.get(0)));
        axiom.setSuperDataProperty(loadExpDataProperty(children.get(1)));
        return axiom;
    }

    /**
     * Loads a EquivalentDataProperties axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomEquivalentDataProperties(XmlElement node) {
        EquivalentDataProperties axiom = Owl2Factory.newEquivalentDataProperties();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        DataPropertySequence seq = Owl2Factory.newDataPropertySequence();
        for (int i = 0; i != children.size(); i++) {
            DataPropertyElement element = Owl2Factory.newDataPropertyElement();
            element.setDataProperty(loadExpDataProperty(children.get(i)));
            element.setIndex(i);
            seq.addDataPropertyElements(element);
        }
        axiom.setDataPropertySeq(seq);
        return axiom;
    }

    /**
     * Loads a DisjointDataProperties axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomDisjointDataProperties(XmlElement node) {
        DisjointDataProperties axiom = Owl2Factory.newDisjointDataProperties();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        DataPropertySequence seq = Owl2Factory.newDataPropertySequence();
        for (int i = 0; i != children.size(); i++) {
            DataPropertyElement element = Owl2Factory.newDataPropertyElement();
            element.setDataProperty(loadExpDataProperty(children.get(i)));
            element.setIndex(i);
            seq.addDataPropertyElements(element);
        }
        axiom.setDataPropertySeq(seq);
        return axiom;
    }

    /**
     * Loads a DataPropertyDomain axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomDataPropertyDomain(XmlElement node) {
        DataPropertyDomain axiom = Owl2Factory.newDataPropertyDomain();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setDataProperty(loadExpDataProperty(children.get(0)));
        axiom.setClasse(loadExpClass(children.get(1)));
        return axiom;
    }

    /**
     * Loads a DataPropertyRange axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomDataPropertyRange(XmlElement node) {
        DataPropertyRange axiom = Owl2Factory.newDataPropertyRange();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setDataProperty(loadExpDataProperty(children.get(0)));
        axiom.setDatarange(loadExpDatarange(children.get(1)));
        return axiom;
    }

    /**
     * Loads a FunctionalDataProperty axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomFunctionalDataProperty(XmlElement node) {
        FunctionalDataProperty axiom = Owl2Factory.newFunctionalDataProperty();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setDataProperty(loadExpDataProperty(children.get(0)));
        return axiom;
    }

    /**
     * Loads a DatatypeDefinition axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomDatatypeDefinition(XmlElement node) {
        DatatypeDefinition axiom = Owl2Factory.newDatatypeDefinition();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setDatatype(loadExpDatarange(children.get(0)));
        axiom.setDatarange(loadExpDatarange(children.get(1)));
        return axiom;
    }

    /**
     * Loads a HasKey axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomHasKey(XmlElement node) {
        HasKey axiom = Owl2Factory.newHasKey();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        ObjectPropertySequence seq1 = Owl2Factory.newObjectPropertySequence();
        DataPropertySequence seq2 = Owl2Factory.newDataPropertySequence();
        axiom.setClasse(loadExpClass(children.get(0)));
        axiom.setObjectPropertySeq(seq1);
        axiom.setDataPropertySeq(seq2);
        for (int i = 1; i != children.size(); i++) {
            // we cannot differentiate IRIs of object and data properties
            // so here everything is an object property
            // this does not change anything because they are all translated in the same way in RDF
            ObjectPropertyElement element = Owl2Factory.newObjectPropertyElement();
            element.setObjectProperty(loadExpObjectProperty(children.get(i)));
            element.setIndex(i - 1);
            seq1.addObjectPropertyElements(element);
        }
        return axiom;
    }

    /**
     * Loads a SameIndividual axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomSameIndividual(XmlElement node) {
        SameIndividual axiom = Owl2Factory.newSameIndividual();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        IndividualSequence seq = Owl2Factory.newIndividualSequence();
        for (int i = 0; i != children.size(); i++) {
            IndividualElement element = Owl2Factory.newIndividualElement();
            element.setIndividual(loadExpIndividual(children.get(i)));
            element.setIndex(i);
            seq.addIndividualElements(element);
        }
        axiom.setIndividualSeq(seq);
        return axiom;
    }

    /**
     * Loads a DifferentIndividuals axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomDifferentIndividuals(XmlElement node) {
        DifferentIndividuals axiom = Owl2Factory.newDifferentIndividuals();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        IndividualSequence seq = Owl2Factory.newIndividualSequence();
        for (int i = 0; i != children.size(); i++) {
            IndividualElement element = Owl2Factory.newIndividualElement();
            element.setIndividual(loadExpIndividual(children.get(i)));
            element.setIndex(i);
            seq.addIndividualElements(element);
        }
        axiom.setIndividualSeq(seq);
        return axiom;
    }

    /**
     * Loads a ClassAssertion axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomClassAssertion(XmlElement node) {
        ClassAssertion axiom = Owl2Factory.newClassAssertion();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setClasse(loadExpClass(children.get(0)));
        axiom.setIndividual(loadExpIndividual(children.get(1)));
        return axiom;
    }

    /**
     * Loads a ObjectPropertyAssertion axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomObjectPropertyAssertion(XmlElement node) {
        ObjectPropertyAssertion axiom = Owl2Factory.newObjectPropertyAssertion();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(children.get(0)));
        axiom.setIndividual(loadExpIndividual(children.get(1)));
        axiom.setValueIndividual(loadExpIndividual(children.get(2)));
        return axiom;
    }

    /**
     * Loads a NegativeObjectPropertyAssertion axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomNegativeObjectPropertyAssertion(XmlElement node) {
        NegativeObjectPropertyAssertion axiom = Owl2Factory.newNegativeObjectPropertyAssertion();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setObjectProperty(loadExpObjectProperty(children.get(0)));
        axiom.setIndividual(loadExpIndividual(children.get(1)));
        axiom.setValueIndividual(loadExpIndividual(children.get(2)));
        return axiom;
    }

    /**
     * Loads a DataPropertyAssertion axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomDataPropertyAssertion(XmlElement node) {
        DataPropertyAssertion axiom = Owl2Factory.newDataPropertyAssertion();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setDataProperty(loadExpDataProperty(children.get(0)));
        axiom.setIndividual(loadExpIndividual(children.get(1)));
        axiom.setValueLiteral(loadExpLiteral(children.get(2)));
        return axiom;
    }

    /**
     * Loads a NegativeDataPropertyAssertion axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomNegativeDataPropertyAssertion(XmlElement node) {
        NegativeDataPropertyAssertion axiom = Owl2Factory.newNegativeDataPropertyAssertion();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setDataProperty(loadExpDataProperty(children.get(0)));
        axiom.setIndividual(loadExpIndividual(children.get(1)));
        axiom.setValueLiteral(loadExpLiteral(children.get(2)));
        return axiom;
    }

    /**
     * Loads a AnnotationAssertion axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomAnnotationAssertion(XmlElement node) {
        AnnotationAssertion axiom = Owl2Factory.newAnnotationAssertion();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setAnnotProperty(loadExpAnnotationProperty(children.get(0)));
        axiom.setAnnotSubject(loadExpAnnotationSubject(children.get(1)));
        axiom.setAnnotValue(loadExpAnnotationValue(children.get(2)));
        return axiom;
    }

    /**
     * Loads a SubAnnotationPropertyOf axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomSubAnnotationPropertyOf(XmlElement node) {
        SubAnnotationPropertyOf axiom = Owl2Factory.newSubAnnotationPropertyOf();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setAnnotProperty(loadExpAnnotationProperty(children.get(0)));
        axiom.setSuperAnnotProperty(loadExpAnnotationProperty(children.get(1)));
        return axiom;
    }

    /**
     * Loads a AnnotationPropertyDomain axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomAnnotationPropertyDomain(XmlElement node) {
        AnnotationPropertyDomain axiom = Owl2Factory.newAnnotationPropertyDomain();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setAnnotProperty(loadExpAnnotationProperty(children.get(0)));
        axiom.setAnnotDomain(loadEntity(children.get(1)));
        return axiom;
    }

    /**
     * Loads a AnnotationPropertyRange axiom from A XML node
     *
     * @param node The XML node
     * @return The axiom
     */
    private Axiom loadAxiomAnnotationPropertyRange(XmlElement node) {
        AnnotationPropertyRange axiom = Owl2Factory.newAnnotationPropertyRange();
        List<XmlElement> children = loadAxiomBase(node, axiom);
        axiom.setAnnotProperty(loadExpAnnotationProperty(children.get(0)));
        axiom.setAnnotRange(loadEntity(children.get(1)));
        return axiom;
    }

    /**
     * Loads an entity expression from A XML node
     *
     * @param node The XML node
     * @return The entity expression
     */
    private EntityExpression loadExpEntity(XmlElement node) {
        return loadEntity(node);
    }

    /**
     * Loads an entity from A XML node
     *
     * @param node The XML node
     * @return The entity
     */
    private IRI loadEntity(XmlElement node) {
        IRI iri = Owl2Factory.newIRI();
        switch (node.getNodeName()) {
            case "IRI":
                iri.setHasValue(URIUtils.resolveRelative(baseURI, TextUtils.unescape(node.getContent())));
                break;
            case "AbbreviatedIRI":
                iri.setHasValue(getIRIForLocalName(node.getContent()));
                break;
            default:
                String value = node.getAttribute("IRI");
                if (value != null)
                    iri.setHasValue(URIUtils.resolveRelative(baseURI, TextUtils.unescape(value)));
                else
                    iri.setHasValue(getIRIForLocalName(node.getAttribute("abbreviatedIRI")));
                break;
        }
        return iri;
    }

    /**
     * Loads a class expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpClass(XmlElement node) {
        switch (node.getNodeName()) {
            case Vocabulary.OWL2.expObjectIntersectionOf:
                return loadExpObjectIntersectionOf(node);
            case Vocabulary.OWL2.expObjectUnionOf:
                return loadExpObjectUnionOf(node);
            case Vocabulary.OWL2.expObjectComplementOf:
                return loadExpObjectComplementOf(node);
            case Vocabulary.OWL2.expObjectOneOf:
                return loadExpObjectOneOf(node);
            case Vocabulary.OWL2.expObjectSomeValuesFrom:
                return loadExpObjectSomeValuesFrom(node);
            case Vocabulary.OWL2.expObjectAllValuesFrom:
                return loadExpObjectAllValuesFrom(node);
            case Vocabulary.OWL2.expObjectHasValue:
                return loadExpObjectHasValue(node);
            case Vocabulary.OWL2.expObjectHasSelf:
                return loadExpObjectHasSelf(node);
            case Vocabulary.OWL2.expObjectMinCardinality:
                return loadExpObjectMinCardinality(node);
            case Vocabulary.OWL2.expObjectMaxCardinality:
                return loadExpObjectMaxCardinality(node);
            case Vocabulary.OWL2.expObjectExactCardinality:
                return loadExpObjectExactCardinality(node);
            case Vocabulary.OWL2.expDataSomeValuesFrom:
                return loadExpDataSomeValuesFrom(node);
            case Vocabulary.OWL2.expDataAllValuesFrom:
                return loadExpDataAllValuesFrom(node);
            case Vocabulary.OWL2.expDataHasValue:
                return loadExpDataHasValue(node);
            case Vocabulary.OWL2.expDataMinCardinality:
                return loadExpDataMinCardinality(node);
            case Vocabulary.OWL2.expDataMaxCardinality:
                return loadExpDataMaxCardinality(node);
            case Vocabulary.OWL2.expDataExactCardinality:
                return loadExpDataExactCardinality(node);
        }
        return loadExpEntity(node);
    }

    /**
     * Loads a ObjectIntersectionOf expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpObjectIntersectionOf(XmlElement node) {
        ObjectIntersectionOf expression = Owl2Factory.newObjectIntersectionOf();
        ClassSequence seq = Owl2Factory.newClassSequence();
        int index = 0;
        for (XmlElement child : node) {
            ClassElement element = Owl2Factory.newClassElement();
            element.setClasse(loadExpClass(child));
            element.setIndex(index);
            seq.addClassElements(element);
            index++;
        }
        expression.setClassSeq(seq);
        return expression;
    }

    /**
     * Loads a ObjectUnionOf expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpObjectUnionOf(XmlElement node) {
        ObjectUnionOf expression = Owl2Factory.newObjectUnionOf();
        ClassSequence seq = Owl2Factory.newClassSequence();
        int index = 0;
        for (XmlElement child : node) {
            ClassElement element = Owl2Factory.newClassElement();
            element.setClasse(loadExpClass(child));
            element.setIndex(index);
            seq.addClassElements(element);
            index++;
        }
        expression.setClassSeq(seq);
        return expression;
    }

    /**
     * Loads a ObjectComplementOf expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpObjectComplementOf(XmlElement node) {
        ObjectComplementOf expression = Owl2Factory.newObjectComplementOf();
        expression.setClasse(loadExpClass(node.getChildren().next()));
        return expression;
    }

    /**
     * Loads a ObjectOneOf expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpObjectOneOf(XmlElement node) {
        ObjectOneOf expression = Owl2Factory.newObjectOneOf();
        IndividualSequence seq = Owl2Factory.newIndividualSequence();
        int index = 0;
        for (XmlElement child : node) {
            IndividualElement element = Owl2Factory.newIndividualElement();
            element.setIndividual(loadExpIndividual(child));
            element.setIndex(index);
            seq.addIndividualElements(element);
            index++;
        }
        expression.setIndividualSeq(seq);
        return expression;
    }

    /**
     * Loads a ObjectSomeValuesFrom expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpObjectSomeValuesFrom(XmlElement node) {
        ObjectSomeValuesFrom expression = Owl2Factory.newObjectSomeValuesFrom();
        Iterator<XmlElement> children = node.getChildren();
        expression.setObjectProperty(loadExpObjectProperty(children.next()));
        expression.setClasse(loadExpClass(children.next()));
        return expression;
    }

    /**
     * Loads a ObjectAllValuesFrom expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpObjectAllValuesFrom(XmlElement node) {
        ObjectAllValuesFrom expression = Owl2Factory.newObjectAllValuesFrom();
        Iterator<XmlElement> children = node.getChildren();
        expression.setObjectProperty(loadExpObjectProperty(children.next()));
        expression.setClasse(loadExpClass(children.next()));
        return expression;
    }

    /**
     * Loads a ObjectHasValue expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpObjectHasValue(XmlElement node) {
        ObjectHasValue expression = Owl2Factory.newObjectHasValue();
        Iterator<XmlElement> children = node.getChildren();
        expression.setObjectProperty(loadExpObjectProperty(children.next()));
        expression.setIndividual(loadExpIndividual(children.next()));
        return expression;
    }

    /**
     * Loads a ObjectHasSelf expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpObjectHasSelf(XmlElement node) {
        ObjectHasSelf expression = Owl2Factory.newObjectHasSelf();
        expression.setObjectProperty(loadExpObjectProperty(node.getChildren().next()));
        return expression;
    }

    /**
     * Loads a ObjectMinCardinality expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpObjectMinCardinality(XmlElement node) {
        ObjectMinCardinality expression = Owl2Factory.newObjectMinCardinality();
        Iterator<XmlElement> children = node.getChildren();
        expression.setCardinality(loadExpLiteral(children.next()));
        expression.setObjectProperty(loadExpObjectProperty(children.next()));
        if (children.hasNext())
            expression.setClasse(loadExpClass(children.next()));
        return expression;
    }

    /**
     * Loads a ObjectMaxCardinality expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpObjectMaxCardinality(XmlElement node) {
        ObjectMaxCardinality expression = Owl2Factory.newObjectMaxCardinality();
        Iterator<XmlElement> children = node.getChildren();
        expression.setCardinality(loadExpLiteral(children.next()));
        expression.setObjectProperty(loadExpObjectProperty(children.next()));
        if (children.hasNext())
            expression.setClasse(loadExpClass(children.next()));
        return expression;
    }

    /**
     * Loads a ObjectExactCardinality expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpObjectExactCardinality(XmlElement node) {
        ObjectExactCardinality expression = Owl2Factory.newObjectExactCardinality();
        Iterator<XmlElement> children = node.getChildren();
        expression.setCardinality(loadExpLiteral(children.next()));
        expression.setObjectProperty(loadExpObjectProperty(children.next()));
        if (children.hasNext())
            expression.setClasse(loadExpClass(children.next()));
        return expression;
    }

    /**
     * Loads a DataSomeValuesFrom expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpDataSomeValuesFrom(XmlElement node) {
        DataSomeValuesFrom expression = Owl2Factory.newDataSomeValuesFrom();
        DataPropertySequence seq = Owl2Factory.newDataPropertySequence();
        expression.setDataPropertySeq(seq);
        Iterator<XmlElement> children = node.getChildren();
        int index = 0;
        while (children.hasNext()) {
            XmlElement child = children.next();
            if (children.hasNext()) {
                // not the last child, this is a data property
                DataPropertyElement element = Owl2Factory.newDataPropertyElement();
                element.setDataProperty(loadExpDataProperty(child));
                element.setIndex(index);
                seq.addDataPropertyElements(element);
            } else {
                // this is the last child, i.e. a data range
                expression.setDatarange(loadExpDatarange(child));
            }
        }
        return expression;
    }

    /**
     * Loads a DataAllValuesFrom expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpDataAllValuesFrom(XmlElement node) {
        DataAllValuesFrom expression = Owl2Factory.newDataAllValuesFrom();
        DataPropertySequence seq = Owl2Factory.newDataPropertySequence();
        expression.setDataPropertySeq(seq);
        Iterator<XmlElement> children = node.getChildren();
        int index = 0;
        while (children.hasNext()) {
            XmlElement child = children.next();
            if (children.hasNext()) {
                // not the last child, this is a data property
                DataPropertyElement element = Owl2Factory.newDataPropertyElement();
                element.setDataProperty(loadExpDataProperty(child));
                element.setIndex(index);
                seq.addDataPropertyElements(element);
            } else {
                // this is the last child, i.e. a data range
                expression.setDatarange(loadExpDatarange(child));
            }
        }
        return expression;
    }

    /**
     * Loads a DataHasValue expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpDataHasValue(XmlElement node) {
        DataHasValue expression = Owl2Factory.newDataHasValue();
        Iterator<XmlElement> children = node.getChildren();
        expression.setDataProperty(loadExpDataProperty(children.next()));
        expression.setLiteral(loadExpLiteral(children.next()));
        return expression;
    }

    /**
     * Loads a DataMinCardinality expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpDataMinCardinality(XmlElement node) {
        DataMinCardinality expression = Owl2Factory.newDataMinCardinality();
        Iterator<XmlElement> children = node.getChildren();
        expression.setCardinality(loadExpLiteral(children.next()));
        expression.setDataProperty(loadExpDataProperty(children.next()));
        if (children.hasNext())
            expression.setDatarange(loadExpDatarange(children.next()));
        return expression;
    }

    /**
     * Loads a DataMaxCardinality expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpDataMaxCardinality(XmlElement node) {
        DataMaxCardinality expression = Owl2Factory.newDataMaxCardinality();
        Iterator<XmlElement> children = node.getChildren();
        expression.setCardinality(loadExpLiteral(children.next()));
        expression.setDataProperty(loadExpDataProperty(children.next()));
        if (children.hasNext())
            expression.setDatarange(loadExpDatarange(children.next()));
        return expression;
    }

    /**
     * Loads a DataExactCardinality expression from A XML node
     *
     * @param node The XML node
     * @return The class expression
     */
    private ClassExpression loadExpDataExactCardinality(XmlElement node) {
        DataExactCardinality expression = Owl2Factory.newDataExactCardinality();
        Iterator<XmlElement> children = node.getChildren();
        expression.setCardinality(loadExpLiteral(children.next()));
        expression.setDataProperty(loadExpDataProperty(children.next()));
        if (children.hasNext())
            expression.setDatarange(loadExpDatarange(children.next()));
        return expression;
    }

    /**
     * Loads an individual expression from A XML node
     *
     * @param node The XML node
     * @return The individual expression
     */
    private IndividualExpression loadExpIndividual(XmlElement node) {
        if (Vocabulary.OWL2.entityAnonymousIndividual.equals(node.getNodeName())) {
            return loadExpAnonymousIndividual(node);
        }
        return loadExpEntity(node);
    }

    /**
     * Loads an anonymous individual from A XML node
     *
     * @param node The XML node
     * @return The anonymous individual
     */
    private AnonymousIndividual loadExpAnonymousIndividual(XmlElement node) {
        String name = node.getAttribute("nodeID");
        AnonymousIndividual result = blanks.get(name);
        if (result != null)
            return result;
        result = Owl2Factory.newAnonymousIndividual();
        result.setNodeID(UUID.randomUUID().toString());
        blanks.put(name, result);
        return result;
    }

    /**
     * Loads datarange expression from A XML node
     *
     * @param node The XML node
     * @return The datarange expression
     */
    private Datarange loadExpDatarange(XmlElement node) {
        switch (node.getNodeName()) {
            case Vocabulary.OWL2.expDataIntersectionOf:
                return loadExpDataIntersectionOf(node);
            case Vocabulary.OWL2.expDataUnionOf:
                return loadExpDataUnionOf(node);
            case Vocabulary.OWL2.expDataComplementOf:
                return loadExpDataComplementOf(node);
            case Vocabulary.OWL2.expDataOneOf:
                return loadExpDataOneOf(node);
            case Vocabulary.OWL2.expDatatypeRestriction:
                return loadExpDatatypeRestriction(node);
        }
        return loadExpEntity(node);
    }

    /**
     * Loads a DataIntersectionOf expression from A XML node
     *
     * @param node The XML node
     * @return The datarange expression
     */
    private Datarange loadExpDataIntersectionOf(XmlElement node) {
        DataIntersectionOf expression = Owl2Factory.newDataIntersectionOf();
        DatarangeSequence seq = Owl2Factory.newDatarangeSequence();
        int index = 0;
        for (XmlElement child : node) {
            DatarangeElement element = Owl2Factory.newDatarangeElement();
            element.setDatarange(loadExpDatarange(child));
            element.setIndex(index);
            seq.addDatarangeElements(element);
            index++;
        }
        expression.setDatarangeSeq(seq);
        return expression;
    }

    /**
     * Loads a DataUnionOf expression from A XML node
     *
     * @param node The XML node
     * @return The datarange expression
     */
    private Datarange loadExpDataUnionOf(XmlElement node) {
        DataUnionOf expression = Owl2Factory.newDataUnionOf();
        DatarangeSequence seq = Owl2Factory.newDatarangeSequence();
        int index = 0;
        for (XmlElement child : node) {
            DatarangeElement element = Owl2Factory.newDatarangeElement();
            element.setDatarange(loadExpDatarange(child));
            element.setIndex(index);
            seq.addDatarangeElements(element);
            index++;
        }
        expression.setDatarangeSeq(seq);
        return expression;
    }

    /**
     * Loads a DataComplementOf expression from A XML node
     *
     * @param node The XML node
     * @return The datarange expression
     */
    private Datarange loadExpDataComplementOf(XmlElement node) {
        DataComplementOf expression = Owl2Factory.newDataComplementOf();
        expression.setDatarange(loadExpDatarange(node.getChildren().next()));
        return expression;
    }

    /**
     * Loads a DataOneOf expression from A XML node
     *
     * @param node The XML node
     * @return The datarange expression
     */
    private Datarange loadExpDataOneOf(XmlElement node) {
        DataOneOf expression = Owl2Factory.newDataOneOf();
        LiteralSequence seq = Owl2Factory.newLiteralSequence();
        int index = 0;
        for (XmlElement child : node) {
            LiteralElement element = Owl2Factory.newLiteralElement();
            element.setLiteral(loadExpLiteral(child));
            element.setIndex(index);
            seq.addLiteralElements(element);
            index++;
        }
        expression.setLiteralSeq(seq);
        return expression;
    }

    /**
     * Loads a DatatypeRestriction expression from A XML node
     *
     * @param node The XML node
     * @return The datarange expression
     */
    private Datarange loadExpDatatypeRestriction(XmlElement node) {
        DatatypeRestriction expression = Owl2Factory.newDatatypeRestriction();
        Iterator<XmlElement> children = node.getChildren();
        expression.setDatarange(loadExpDatarange(children.next()));
        while (children.hasNext())
            expression.addFacetRestrictions(loadExpFacetRestriction(children.next()));
        return expression;
    }

    /**
     * Loads a facet restriction
     *
     * @param node The XML node
     * @return The facet restriction
     */
    private FacetRestriction loadExpFacetRestriction(XmlElement node) {
        FacetRestriction restriction = Owl2Factory.newFacetRestriction();
        IRI iri = Owl2Factory.newIRI();
        iri.setHasValue(node.getAttribute("facet"));
        restriction.setConstrainingFacet(iri);
        restriction.setConstrainingValue((Literal) loadExpLiteral(node.getChildren().next()));
        return restriction;
    }

    /**
     * Loads an object property expression from A XML node
     *
     * @param node The XML node
     * @return The object property expression
     */
    private ObjectPropertyExpression loadExpObjectProperty(XmlElement node) {
        if (Vocabulary.OWL2.expObjectInverseOf.equals(node.getNodeName())) {
            return loaExpInverseObjectProperty(node);
        }
        return loadExpEntity(node);
    }

    /**
     * Loads an inverse object property expression from A XML node
     *
     * @param node The XML node
     * @return The object property expression
     */
    private ObjectPropertyExpression loaExpInverseObjectProperty(XmlElement node) {
        ObjectInverseOf expression = Owl2Factory.newObjectInverseOf();
        expression.setInverse(loadExpObjectProperty(node.getChildren().next()));
        return expression;
    }

    /**
     * Loads an data property expression from A XML node
     *
     * @param node The XML node
     * @return The data property expression
     */
    private DataPropertyExpression loadExpDataProperty(XmlElement node) {
        return loadExpEntity(node);
    }

    /**
     * Loads a literal expression from A XML node
     *
     * @param node The XML node
     * @return The literal expression
     */
    private LiteralExpression loadExpLiteral(XmlElement node) {
        String value = node.getContent();
        String datatype = node.getAttribute("datatypeIRI");
        if (datatype == null)
            datatype = Vocabulary.xsdString;
        Literal result = Owl2Factory.newLiteral();
        result.setLexicalValue(value);
        IRI type = Owl2Factory.newIRI();
        type.setHasValue(datatype);
        result.setMemberOf(type);
        return result;
    }

    /**
     * Loads an annotation property from A XML node
     *
     * @param node The XML node
     * @return The annotation value
     */
    private IRI loadExpAnnotationProperty(XmlElement node) {
        return loadEntity(node);
    }

    /**
     * Loads an annotation subject from A XML node
     *
     * @param node The XML node
     * @return The annotation value
     */
    private AnnotationSubject loadExpAnnotationSubject(XmlElement node) {
        if (Vocabulary.OWL2.entityAnonymousIndividual.equals(node.getNodeName())) {
            return loadExpAnonymousIndividual(node);
        }
        return loadEntity(node);
    }

    /**
     * Loads an annotation value from A XML node
     *
     * @param node The XML node
     * @return The annotation value
     */
    private AnnotationValue loadExpAnnotationValue(XmlElement node) {
        switch (node.getNodeName()) {
            case "IRI": {
                IRI iri = Owl2Factory.newIRI();
                iri.setHasValue(URIUtils.resolveRelative(baseURI, TextUtils.unescape(node.getContent())));
                return iri;
            }
            case "AbbreviatedIRI": {
                IRI iri = Owl2Factory.newIRI();
                iri.setHasValue(getIRIForLocalName(node.getContent()));
                return iri;
            }
            case Vocabulary.OWL2.entityAnonymousIndividual: {
                return loadExpAnonymousIndividual(node);
            }
            default:
                return (AnnotationValue) loadExpLiteral(node);
        }
    }
}
