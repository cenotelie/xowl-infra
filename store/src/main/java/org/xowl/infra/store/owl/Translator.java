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

package org.xowl.infra.store.owl;

import org.xowl.infra.lang.actions.*;
import org.xowl.infra.lang.owl2.*;
import org.xowl.infra.lang.owl2.DataAllValuesFrom;
import org.xowl.infra.lang.owl2.DataExactCardinality;
import org.xowl.infra.lang.owl2.DataHasValue;
import org.xowl.infra.lang.owl2.DataMaxCardinality;
import org.xowl.infra.lang.owl2.DataMinCardinality;
import org.xowl.infra.lang.owl2.DataPropertyAssertion;
import org.xowl.infra.lang.owl2.DataSomeValuesFrom;
import org.xowl.infra.lang.owl2.DatatypeRestriction;
import org.xowl.infra.lang.owl2.Literal;
import org.xowl.infra.lang.owl2.ObjectAllValuesFrom;
import org.xowl.infra.lang.owl2.ObjectExactCardinality;
import org.xowl.infra.lang.owl2.ObjectHasSelf;
import org.xowl.infra.lang.owl2.ObjectHasValue;
import org.xowl.infra.lang.owl2.ObjectMaxCardinality;
import org.xowl.infra.lang.owl2.ObjectMinCardinality;
import org.xowl.infra.lang.owl2.ObjectPropertyAssertion;
import org.xowl.infra.lang.owl2.ObjectSomeValuesFrom;
import org.xowl.infra.lang.runtime.Class;
import org.xowl.infra.lang.runtime.*;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.loaders.OWLLoaderResult;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.rdf.Property;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.utils.collections.Adapter;
import org.xowl.infra.utils.collections.AdaptingIterator;

import java.util.*;

/**
 * Represents a OWL to RDF translator
 *
 * @author Laurent Wouters
 */
public class Translator {
    /**
     * The node manager for the creation of new RDF nodes
     */
    protected final NodeManager nodeManager;
    /**
     * The graph that contains the translated input
     */
    protected GraphNode graph;
    /**
     * The resulting triples
     */
    protected Collection<Quad> quads;
    /**
     * The translation context
     */
    protected TranslationContext context;

    /**
     * Initializes this translator
     *
     * @param context     The existing translation context, or null if a new one shall be used
     * @param nodeManager The node manager for the creation of new RDF nodes
     */
    public Translator(TranslationContext context, NodeManager nodeManager) {
        this.nodeManager = nodeManager;
        this.context = context;
        if (this.context == null)
            this.context = new TranslationContext();
    }

    /**
     * Translates the specified input coming from a loader
     *
     * @param input The loader result to translate
     * @return The translation result
     * @throws TranslationException When a runtime entity is not named
     */
    public Collection<Quad> translate(OWLLoaderResult input) throws TranslationException {
        graph = nodeManager.getIRINode(input.getIRI());
        quads = new ArrayList<>();
        for (Axiom axiom : input.getAxioms())
            translateAxiom(axiom);
        for (Annotation annotation : input.getAnnotations())
            translateAnnotation((SubjectNode) graph, annotation);
        return quads;
    }

    /**
     * Translates the specified axioms
     *
     * @param input The OWL axioms to translate
     * @param graph The target graph
     * @return The translation result
     * @throws TranslationException When a runtime entity is not named
     */
    public Collection<Quad> translate(Collection<Axiom> input, GraphNode graph) throws TranslationException {
        this.graph = graph;
        quads = new ArrayList<>();
        for (Axiom axiom : input)
            translateAxiom(axiom);
        return quads;
    }

    /**
     * Gets a dynamic node for the specified dynamic expression of the specified expected type
     *
     * @param expression A dynamic expression
     * @param type       The expected type of the expression
     * @return The representing dynamic node
     */
    protected DynamicNode getDynamicNode(DynamicExpression expression, java.lang.Class type) {
        DynamicNode node = new DynamicNode(expression);
        node.addType(type);
        return node;
    }

    /**
     * Gets the triple for the specified elements
     *
     * @param subject  A subject node
     * @param property The property
     * @param object   The object node
     * @return The equivalent triple
     */
    protected Quad getTriple(SubjectNode subject, String property, Node object) {
        return new Quad(graph, subject, nodeManager.getIRINode(property), object);
    }

    /**
     * Gets the triple for the specified elements
     *
     * @param subject  A subject node
     * @param property The property
     * @param object   The object value
     * @return The equivalent triple
     */
    protected Quad getTriple(SubjectNode subject, String property, String object) {
        return new Quad(graph, subject, nodeManager.getIRINode(property), nodeManager.getIRINode(object));
    }

    /**
     * Translate the specified list of RDF nodes into an ordered RDF list
     *
     * @param elements The list of elements to translate
     * @return The first node of the RDF list
     */
    protected SubjectNode translateOrderedSequence(List<Node> elements) {
        if (elements.isEmpty())
            return nodeManager.getIRINode(org.xowl.infra.store.Vocabulary.rdfNil);
        SubjectNode[] proxies = new SubjectNode[elements.size()];
        for (int i = 0; i != proxies.length; i++) {
            BlankNode proxy = nodeManager.getBlankNode();
            proxies[i] = proxy;
            quads.add(getTriple(proxy, org.xowl.infra.store.Vocabulary.rdfFirst, elements.get(i)));
        }
        for (int i = 0; i != proxies.length - 1; i++)
            quads.add(getTriple(proxies[i], org.xowl.infra.store.Vocabulary.rdfRest, proxies[i + 1]));
        quads.add(getTriple(proxies[proxies.length - 1], org.xowl.infra.store.Vocabulary.rdfRest, org.xowl.infra.store.Vocabulary.rdfNil));
        return proxies[0];
    }

    /**
     * Translate the specified list of RDF nodes into an unordered RDF list
     *
     * @param elements The list of elements to translate
     * @return The first node of the RDF list
     */
    protected SubjectNode translateUnorderedSequence(List<Node> elements) {
        return translateOrderedSequence(elements);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime entity is not named
     */
    protected void translateAxiom(Axiom axiom) throws TranslationException {
        if (axiom instanceof Declaration)
            translateAxiomDeclaration((Declaration) axiom);
        else if (axiom instanceof DatatypeDefinition)
            translateAxiomDatatypeDefinition((DatatypeDefinition) axiom);
        else if (axiom instanceof SubClassOf)
            translateAxiomSubClassOf((SubClassOf) axiom);
        else if (axiom instanceof EquivalentClasses)
            translateAxiomEquivalentClasses((EquivalentClasses) axiom);
        else if (axiom instanceof DisjointClasses)
            translateAxiomDisjointClasses((DisjointClasses) axiom);
        else if (axiom instanceof DisjointUnion)
            translateAxiomDisjointUnion((DisjointUnion) axiom);
        else if (axiom instanceof SubObjectPropertyOf)
            translateAxiomSubObjectPropertyOf((SubObjectPropertyOf) axiom);
        else if (axiom instanceof EquivalentObjectProperties)
            translateAxiomEquivalentObjectProperties((EquivalentObjectProperties) axiom);
        else if (axiom instanceof DisjointObjectProperties)
            translateAxiomDisjointObjectProperties((DisjointObjectProperties) axiom);
        else if (axiom instanceof InverseObjectProperties)
            translateAxiomInverseObjectProperties((InverseObjectProperties) axiom);
        else if (axiom instanceof ObjectPropertyDomain)
            translateAxiomObjectPropertyDomain((ObjectPropertyDomain) axiom);
        else if (axiom instanceof ObjectPropertyRange)
            translateAxiomObjectPropertyRange((ObjectPropertyRange) axiom);
        else if (axiom instanceof FunctionalObjectProperty)
            translateAxiomFunctionalObjectProperty((FunctionalObjectProperty) axiom);
        else if (axiom instanceof InverseFunctionalObjectProperty)
            translateAxiomInverseFunctionalObjectProperty((InverseFunctionalObjectProperty) axiom);
        else if (axiom instanceof ReflexiveObjectProperty)
            translateAxiomReflexiveObjectProperty((ReflexiveObjectProperty) axiom);
        else if (axiom instanceof IrreflexiveObjectProperty)
            translateAxiomIrreflexiveObjectProperty((IrreflexiveObjectProperty) axiom);
        else if (axiom instanceof SymmetricObjectProperty)
            translateAxiomSymmetricObjectProperty((SymmetricObjectProperty) axiom);
        else if (axiom instanceof AsymmetricObjectProperty)
            translateAxiomAsymmetricObjectProperty((AsymmetricObjectProperty) axiom);
        else if (axiom instanceof TransitiveObjectProperty)
            translateAxiomTransitiveObjectProperty((TransitiveObjectProperty) axiom);
        else if (axiom instanceof SubDataPropertyOf)
            translateAxiomSubDataPropertyOf((SubDataPropertyOf) axiom);
        else if (axiom instanceof EquivalentDataProperties)
            translateAxiomEquivalentDataProperties((EquivalentDataProperties) axiom);
        else if (axiom instanceof DisjointDataProperties)
            translateAxiomDisjointDataProperties((DisjointDataProperties) axiom);
        else if (axiom instanceof DataPropertyDomain)
            translateAxiomDataPropertyDomain((DataPropertyDomain) axiom);
        else if (axiom instanceof DataPropertyRange)
            translateAxiomDataPropertyRange((DataPropertyRange) axiom);
        else if (axiom instanceof FunctionalDataProperty)
            translateAxiomFunctionalDataProperty((FunctionalDataProperty) axiom);
        else if (axiom instanceof SameIndividual)
            translateAxiomSameIndividual((SameIndividual) axiom);
        else if (axiom instanceof DifferentIndividuals)
            translateAxiomDifferentIndividuals((DifferentIndividuals) axiom);
        else if (axiom instanceof ClassAssertion)
            translateAxiomClassAssertion((ClassAssertion) axiom);
        else if (axiom instanceof ObjectPropertyAssertion)
            translateAxiomObjectPropertyAssertion((ObjectPropertyAssertion) axiom);
        else if (axiom instanceof NegativeObjectPropertyAssertion)
            translateAxiomNegativeObjectPropertyAssertion((NegativeObjectPropertyAssertion) axiom);
        else if (axiom instanceof DataPropertyAssertion)
            translateAxiomDataPropertyAssertion((DataPropertyAssertion) axiom);
        else if (axiom instanceof NegativeDataPropertyAssertion)
            translateAxiomNegativeDataPropertyAssertion((NegativeDataPropertyAssertion) axiom);
        else if (axiom instanceof HasKey)
            translateAxiomHasKey((HasKey) axiom);
        else if (axiom instanceof SubAnnotationPropertyOf)
            translateAxiomSubAnnotationPropertyOf((SubAnnotationPropertyOf) axiom);
        else if (axiom instanceof AnnotationPropertyDomain)
            translateAxiomAnnotationPropertyDomain((AnnotationPropertyDomain) axiom);
        else if (axiom instanceof AnnotationPropertyRange)
            translateAxiomAnnotationPropertyRange((AnnotationPropertyRange) axiom);
        else if (axiom instanceof AnnotationAssertion)
            translateAxiomAnnotationAssertion((AnnotationAssertion) axiom);
        else if (axiom instanceof FunctionDefinitionAxiom)
            translateAxiomFunctionDefinition((FunctionDefinitionAxiom) axiom);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     */
    protected void translateAxiomDeclaration(Declaration axiom) {
        SubjectNode entityNode = nodeManager.getIRINode(axiom.getEntity().getHasValue());
        Quad quad = null;
        if (Vocabulary.OWL2.entityClass.equals(axiom.getType()))
            quad = getTriple(entityNode, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlClass);
        else if (Vocabulary.OWL2.entityDatatype.equals(axiom.getType()))
            quad = getTriple(entityNode, org.xowl.infra.store.Vocabulary.rdfType, org.xowl.infra.store.Vocabulary.rdfsDatatype);
        else if (Vocabulary.OWL2.entityNamedIndividual.equals(axiom.getType()))
            quad = getTriple(entityNode, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlNamedIndividual);
        else if (Vocabulary.OWL2.entityObjectProperty.equals(axiom.getType()))
            quad = getTriple(entityNode, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlObjectProperty);
        else if (Vocabulary.OWL2.entityDataProperty.equals(axiom.getType()))
            quad = getTriple(entityNode, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlDataProperty);
        else if (Vocabulary.OWL2.entityAnnotationProperty.equals(axiom.getType()))
            quad = getTriple(entityNode, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlAnnotationProperty);
        if (quad != null) {
            quads.add(quad);
            translateAxiomAnnotations(axiom, quad);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime datatype is not named
     */
    protected void translateAxiomDatatypeDefinition(DatatypeDefinition axiom) throws TranslationException {
        SubjectNode dt = translateDatarange(axiom.getDatatype());
        SubjectNode dr = translateDatarange(axiom.getDatarange());
        Quad quad = getTriple(dt, Vocabulary.owlEquivalentClass, dr);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime class is not named
     */
    protected void translateAxiomSubClassOf(SubClassOf axiom) throws TranslationException {
        SubjectNode sub = translateClassExpression(axiom.getClasse());
        SubjectNode sup = translateClassExpression(axiom.getSuperClass());
        Quad quad = getTriple(sub, org.xowl.infra.store.Vocabulary.rdfsSubClassOf, sup);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime class is not named
     */
    protected void translateAxiomEquivalentClasses(EquivalentClasses axiom) throws TranslationException {
        List<SubjectNode> elements = new ArrayList<>();
        Iterator<ClassExpression> expressions = getAll(axiom.getClassSeq());
        while (expressions.hasNext())
            elements.add(translateClassExpression(expressions.next()));
        for (int i = 0; i != elements.size() - 1; i++) {
            Quad quad = getTriple(elements.get(i), Vocabulary.owlEquivalentClass, elements.get(i + 1));
            quads.add(quad);
            translateAxiomAnnotations(axiom, quad);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime class is not named
     */
    protected void translateAxiomDisjointClasses(DisjointClasses axiom) throws TranslationException {
        List<Node> elements = new ArrayList<>();
        Iterator<ClassExpression> expressions = getAll(axiom.getClassSeq());
        while (expressions.hasNext())
            elements.add(translateClassExpression(expressions.next()));
        if (elements.size() == 2) {
            Quad quad = getTriple((SubjectNode) elements.get(0), Vocabulary.owlDisjointWith, elements.get(1));
            quads.add(quad);
            translateAxiomAnnotations(axiom, quad);
        } else {
            SubjectNode main = nodeManager.getBlankNode();
            quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlAllDisjointClasses));
            quads.add(getTriple(main, Vocabulary.owlMembers, translateUnorderedSequence(elements)));
            for (Annotation annotation : axiom.getAllAnnotations())
                translateAnnotation(main, annotation);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime class is not named
     */
    protected void translateAxiomDisjointUnion(DisjointUnion axiom) throws TranslationException {
        SubjectNode classe = translateClassExpression(axiom.getClasse());
        List<Node> elements = new ArrayList<>();
        Iterator<ClassExpression> expressions = getAll(axiom.getClassSeq());
        while (expressions.hasNext())
            elements.add(translateClassExpression(expressions.next()));
        Quad quad = getTriple(classe, Vocabulary.owlDisjointUnionOf, translateUnorderedSequence(elements));
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime object property is not named
     */
    protected void translateAxiomSubObjectPropertyOf(SubObjectPropertyOf axiom) throws TranslationException {
        if (axiom.getObjectPropertyChain() != null) {
            List<Node> elements = new ArrayList<>();
            Iterator<ObjectPropertyExpression> expressions = getAll(axiom.getObjectPropertyChain());
            while (expressions.hasNext())
                elements.add(translateObjectPropertyExpression(expressions.next()));
            SubjectNode sup = translateObjectPropertyExpression(axiom.getSuperObjectProperty());
            Quad quad = getTriple(sup, Vocabulary.owlPropertyChainAxiom, translateOrderedSequence(elements));
            quads.add(quad);
            translateAxiomAnnotations(axiom, quad);
        } else {
            SubjectNode sub = translateObjectPropertyExpression(axiom.getObjectProperty());
            SubjectNode sup = translateObjectPropertyExpression(axiom.getSuperObjectProperty());
            Quad quad = getTriple(sub, org.xowl.infra.store.Vocabulary.rdfsSubPropertyOf, sup);
            quads.add(quad);
            translateAxiomAnnotations(axiom, quad);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime object property is not named
     */
    protected void translateAxiomEquivalentObjectProperties(EquivalentObjectProperties axiom) throws TranslationException {
        List<SubjectNode> elements = new ArrayList<>();
        Iterator<ObjectPropertyExpression> expressions = getAll(axiom.getObjectPropertySeq());
        while (expressions.hasNext())
            elements.add(translateObjectPropertyExpression(expressions.next()));
        for (int i = 0; i != elements.size() - 1; i++) {
            Quad quad = getTriple(elements.get(i), Vocabulary.owlEquivalentProperty, elements.get(i + 1));
            quads.add(quad);
            translateAxiomAnnotations(axiom, quad);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime object property is not named
     */
    protected void translateAxiomDisjointObjectProperties(DisjointObjectProperties axiom) throws TranslationException {
        List<Node> elements = new ArrayList<>();
        Iterator<ObjectPropertyExpression> expressions = getAll(axiom.getObjectPropertySeq());
        while (expressions.hasNext())
            elements.add(translateObjectPropertyExpression(expressions.next()));
        if (elements.size() == 2) {
            Quad quad = getTriple((SubjectNode) elements.get(0), Vocabulary.owlPropertyDisjointWith, elements.get(1));
            quads.add(quad);
            translateAxiomAnnotations(axiom, quad);
        } else {
            SubjectNode main = nodeManager.getBlankNode();
            quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlAllDisjointProperties));
            quads.add(getTriple(main, Vocabulary.owlMembers, translateUnorderedSequence(elements)));
            for (Annotation annotation : axiom.getAllAnnotations())
                translateAnnotation(main, annotation);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime object property is not named
     */
    protected void translateAxiomInverseObjectProperties(InverseObjectProperties axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        SubjectNode inv = translateObjectPropertyExpression(axiom.getInverse());
        Quad quad = getTriple(prop, Vocabulary.owlInverseOf, inv);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime class is not named
     */
    protected void translateAxiomObjectPropertyDomain(ObjectPropertyDomain axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        SubjectNode classe = translateClassExpression(axiom.getClasse());
        Quad quad = getTriple(prop, org.xowl.infra.store.Vocabulary.rdfsDomain, classe);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime class is not named
     */
    protected void translateAxiomObjectPropertyRange(ObjectPropertyRange axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        SubjectNode classe = translateClassExpression(axiom.getClasse());
        Quad quad = getTriple(prop, org.xowl.infra.store.Vocabulary.rdfsRange, classe);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime object property is not named
     */
    protected void translateAxiomFunctionalObjectProperty(FunctionalObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Quad quad = getTriple(prop, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlFunctionalProperty);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime object property is not named
     */
    protected void translateAxiomInverseFunctionalObjectProperty(InverseFunctionalObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Quad quad = getTriple(prop, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlInverseFunctionalProperty);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime object property is not named
     */
    protected void translateAxiomReflexiveObjectProperty(ReflexiveObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Quad quad = getTriple(prop, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlReflexiveProperty);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime object property is not named
     */
    protected void translateAxiomIrreflexiveObjectProperty(IrreflexiveObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Quad quad = getTriple(prop, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlIrreflexiveProperty);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime object property is not named
     */
    protected void translateAxiomSymmetricObjectProperty(SymmetricObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Quad quad = getTriple(prop, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlSymmetricProperty);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime object property is not named
     */
    protected void translateAxiomAsymmetricObjectProperty(AsymmetricObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Quad quad = getTriple(prop, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlAsymmetricProperty);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime object property is not named
     */
    protected void translateAxiomTransitiveObjectProperty(TransitiveObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Quad quad = getTriple(prop, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlTransitiveProperty);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime data property is not named
     */
    protected void translateAxiomSubDataPropertyOf(SubDataPropertyOf axiom) throws TranslationException {
        SubjectNode sub = translateDataPropertyExpression(axiom.getDataProperty());
        SubjectNode sup = translateDataPropertyExpression(axiom.getSuperDataProperty());
        Quad quad = getTriple(sub, org.xowl.infra.store.Vocabulary.rdfsSubPropertyOf, sup);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime data property is not named
     */
    protected void translateAxiomEquivalentDataProperties(EquivalentDataProperties axiom) throws TranslationException {
        List<SubjectNode> elements = new ArrayList<>();
        Iterator<DataPropertyExpression> expressions = getAll(axiom.getDataPropertySeq());
        while (expressions.hasNext())
            elements.add(translateDataPropertyExpression(expressions.next()));
        for (int i = 0; i != elements.size() - 1; i++) {
            Quad quad = getTriple(elements.get(i), Vocabulary.owlEquivalentProperty, elements.get(i + 1));
            quads.add(quad);
            translateAxiomAnnotations(axiom, quad);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime data property is not named
     */
    protected void translateAxiomDisjointDataProperties(DisjointDataProperties axiom) throws TranslationException {
        List<Node> elements = new ArrayList<>();
        Iterator<DataPropertyExpression> expressions = getAll(axiom.getDataPropertySeq());
        while (expressions.hasNext())
            elements.add(translateDataPropertyExpression(expressions.next()));
        if (elements.size() == 2) {
            Quad quad = getTriple((SubjectNode) elements.get(0), Vocabulary.owlPropertyDisjointWith, elements.get(1));
            quads.add(quad);
            translateAxiomAnnotations(axiom, quad);
        } else {
            SubjectNode main = nodeManager.getBlankNode();
            quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlAllDisjointProperties));
            quads.add(getTriple(main, Vocabulary.owlMembers, translateUnorderedSequence(elements)));
            for (Annotation annotation : axiom.getAllAnnotations())
                translateAnnotation(main, annotation);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime class is not named
     */
    protected void translateAxiomDataPropertyDomain(DataPropertyDomain axiom) throws TranslationException {
        SubjectNode prop = translateDataPropertyExpression(axiom.getDataProperty());
        SubjectNode classe = translateClassExpression(axiom.getClasse());
        Quad quad = getTriple(prop, org.xowl.infra.store.Vocabulary.rdfsDomain, classe);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime data property is not named
     */
    protected void translateAxiomDataPropertyRange(DataPropertyRange axiom) throws TranslationException {
        SubjectNode prop = translateDataPropertyExpression(axiom.getDataProperty());
        SubjectNode datatype = translateDatarange(axiom.getDatarange());
        Quad quad = getTriple(prop, org.xowl.infra.store.Vocabulary.rdfsRange, datatype);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime data property is not named
     */
    protected void translateAxiomFunctionalDataProperty(FunctionalDataProperty axiom) throws TranslationException {
        SubjectNode prop = translateDataPropertyExpression(axiom.getDataProperty());
        Quad quad = getTriple(prop, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlFunctionalProperty);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException when a runtime individual is not named
     */
    protected void translateAxiomSameIndividual(SameIndividual axiom) throws TranslationException {
        List<SubjectNode> elements = new ArrayList<>();
        Iterator<IndividualExpression> expressions = getAll(axiom.getIndividualSeq());
        while (expressions.hasNext())
            elements.add(translateIndividualExpression(expressions.next()));
        for (int i = 0; i != elements.size() - 1; i++) {
            Quad quad = getTriple(elements.get(i), Vocabulary.owlSameAs, elements.get(i + 1));
            quads.add(quad);
            translateAxiomAnnotations(axiom, quad);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException when a runtime individual is not named
     */
    protected void translateAxiomDifferentIndividuals(DifferentIndividuals axiom) throws TranslationException {
        List<Node> elements = new ArrayList<>();
        Iterator<IndividualExpression> expressions = getAll(axiom.getIndividualSeq());
        while (expressions.hasNext())
            elements.add(translateIndividualExpression(expressions.next()));
        if (elements.size() == 2) {
            Quad quad = getTriple((SubjectNode) elements.get(0), Vocabulary.owlDifferentFrom, elements.get(1));
            quads.add(quad);
            translateAxiomAnnotations(axiom, quad);
        } else {
            SubjectNode main = nodeManager.getBlankNode();
            quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlAllDifferent));
            quads.add(getTriple(main, Vocabulary.owlMembers, translateUnorderedSequence(elements)));
            for (Annotation annotation : axiom.getAllAnnotations())
                translateAnnotation(main, annotation);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime class is not named
     */
    protected void translateAxiomClassAssertion(ClassAssertion axiom) throws TranslationException {
        SubjectNode ind = translateIndividualExpression(axiom.getIndividual());
        SubjectNode classe = translateClassExpression(axiom.getClasse());
        Quad quad = getTriple(ind, org.xowl.infra.store.Vocabulary.rdfType, classe);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime object property is not named
     */
    protected void translateAxiomObjectPropertyAssertion(ObjectPropertyAssertion axiom) throws TranslationException {
        Property prop;
        if (axiom.getObjectProperty() instanceof ObjectInverseOf) {
            ObjectInverseOf expInv = (ObjectInverseOf) axiom.getObjectProperty();
            prop = (Property) translateObjectPropertyExpression(expInv.getInverse());
        } else
            prop = (Property) translateObjectPropertyExpression(axiom.getObjectProperty());
        SubjectNode ind = translateIndividualExpression(axiom.getIndividual());
        SubjectNode value = translateIndividualExpression(axiom.getValueIndividual());
        Quad quad = new Quad(graph, ind, prop, value);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime object property is not named
     */
    protected void translateAxiomNegativeObjectPropertyAssertion(NegativeObjectPropertyAssertion axiom) throws TranslationException {
        SubjectNode main = nodeManager.getBlankNode();
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        SubjectNode ind = translateIndividualExpression(axiom.getIndividual());
        SubjectNode value = translateIndividualExpression(axiom.getValueIndividual());
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlNegativePropertyAssertion));
        quads.add(getTriple(main, Vocabulary.owlSourceIndividual, ind));
        quads.add(getTriple(main, Vocabulary.owlAssertionProperty, prop));
        quads.add(getTriple(main, Vocabulary.owlTargetIndividual, value));
        for (Annotation annotation : axiom.getAllAnnotations())
            translateAnnotation(main, annotation);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime data property is not named
     */
    protected void translateAxiomDataPropertyAssertion(DataPropertyAssertion axiom) throws TranslationException {
        Property prop = (Property) translateDataPropertyExpression(axiom.getDataProperty());
        SubjectNode ind = translateIndividualExpression(axiom.getIndividual());
        Node value = translateLiteralExpression(axiom.getValueLiteral());
        Quad quad = new Quad(graph, ind, prop, value);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime data property is not named
     */
    protected void translateAxiomNegativeDataPropertyAssertion(NegativeDataPropertyAssertion axiom) throws TranslationException {
        SubjectNode main = nodeManager.getBlankNode();
        SubjectNode prop = translateDataPropertyExpression(axiom.getDataProperty());
        SubjectNode ind = translateIndividualExpression(axiom.getIndividual());
        Node value = translateLiteralExpression(axiom.getValueLiteral());
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlNegativePropertyAssertion));
        quads.add(getTriple(main, Vocabulary.owlSourceIndividual, ind));
        quads.add(getTriple(main, Vocabulary.owlAssertionProperty, prop));
        quads.add(getTriple(main, Vocabulary.owlTargetValue, value));
        for (Annotation annotation : axiom.getAllAnnotations())
            translateAnnotation(main, annotation);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime entity is not named
     */
    protected void translateAxiomHasKey(HasKey axiom) throws TranslationException {
        SubjectNode classe = translateClassExpression(axiom.getClasse());
        List<Node> elements = new ArrayList<>();
        Iterator<ObjectPropertyExpression> objExpressions = getAll(axiom.getObjectPropertySeq());
        while (objExpressions.hasNext())
            elements.add(translateObjectPropertyExpression(objExpressions.next()));
        Iterator<DataPropertyExpression> dataExpressions = getAll(axiom.getDataPropertySeq());
        while (dataExpressions.hasNext())
            elements.add(translateDataPropertyExpression(dataExpressions.next()));
        Quad quad = getTriple(classe, Vocabulary.owlHasKey, translateUnorderedSequence(elements));
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     */
    protected void translateAxiomSubAnnotationPropertyOf(SubAnnotationPropertyOf axiom) {
        SubjectNode sub = translateAnnotationProperty(axiom.getAnnotProperty());
        SubjectNode sup = translateAnnotationProperty(axiom.getSuperAnnotProperty());
        quads.add(getTriple(sub, org.xowl.infra.store.Vocabulary.rdfsSubPropertyOf, sup));
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     */
    protected void translateAxiomAnnotationPropertyDomain(AnnotationPropertyDomain axiom) {
        SubjectNode prop = translateAnnotationProperty(axiom.getAnnotProperty());
        quads.add(getTriple(prop, org.xowl.infra.store.Vocabulary.rdfsDomain, nodeManager.getIRINode(axiom.getAnnotDomain().getHasValue())));
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     */
    protected void translateAxiomAnnotationPropertyRange(AnnotationPropertyRange axiom) {
        SubjectNode prop = translateAnnotationProperty(axiom.getAnnotProperty());
        quads.add(getTriple(prop, org.xowl.infra.store.Vocabulary.rdfsRange, nodeManager.getIRINode(axiom.getAnnotRange().getHasValue())));
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     */
    protected void translateAxiomAnnotationAssertion(AnnotationAssertion axiom) {
        IRINode prop = translateAnnotationProperty(axiom.getAnnotProperty());
        SubjectNode subject = translateAnnotationSubject(axiom.getAnnotSubject());
        Node value = translateAnnotationValue(axiom.getAnnotValue());
        Quad quad = new Quad(graph, subject, prop, value);
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws TranslationException When a runtime entity is not named
     */
    protected void translateAxiomFunctionDefinition(FunctionDefinitionAxiom axiom) throws TranslationException {
        SubjectNode function = translateFunctionExpression(axiom.getFunction());
        OpaqueExpression value = ActionsFactory.newOpaqueExpression();
        value.setValue(axiom.getDefinition());
        Quad quad = getTriple(function, Vocabulary.xowlDefinedAs, new DynamicNode(value));
        quads.add(quad);
        translateAxiomAnnotations(axiom, quad);
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime entity is not named
     */
    protected SubjectNode translateClassExpression(ClassExpression expression) throws TranslationException {
        if (expression instanceof QueryVariable)
            return context.resolve((QueryVariable) expression, Class.class);
        if (expression instanceof DynamicExpression)
            return getDynamicNode((DynamicExpression) expression, Class.class);
        if (expression instanceof IRI)
            return translateClassIRI((IRI) expression);
        if (expression instanceof ObjectUnionOf)
            return translateClassObjectUnionOf((ObjectUnionOf) expression);
        if (expression instanceof ObjectIntersectionOf)
            return translateClassObjectIntersectionOf((ObjectIntersectionOf) expression);
        if (expression instanceof ObjectOneOf)
            return translateClassObjectOneOf((ObjectOneOf) expression);
        if (expression instanceof ObjectComplementOf)
            return translateClassObjectComplementOf((ObjectComplementOf) expression);
        if (expression instanceof DataAllValuesFrom)
            return translateClassDataAllValuesFrom((DataAllValuesFrom) expression);
        if (expression instanceof DataExactCardinality)
            return translateClassDataExactCardinality((DataExactCardinality) expression);
        if (expression instanceof DataHasValue)
            return transltateClassDataHasValue((DataHasValue) expression);
        if (expression instanceof DataMaxCardinality)
            return translateClassDataMaxCardinality((DataMaxCardinality) expression);
        if (expression instanceof DataMinCardinality)
            return translateClassDataMinCardinality((DataMinCardinality) expression);
        if (expression instanceof DataSomeValuesFrom)
            return translateClassDataSomeValuesFrom((DataSomeValuesFrom) expression);
        if (expression instanceof ObjectAllValuesFrom)
            return translateClassObjectAllValuesFrom((ObjectAllValuesFrom) expression);
        if (expression instanceof ObjectExactCardinality)
            return translateClassObjectExactCardinality((ObjectExactCardinality) expression);
        if (expression instanceof ObjectHasSelf)
            return translateClassObjectHasSelf((ObjectHasSelf) expression);
        if (expression instanceof ObjectHasValue)
            return translateClassObjectHasValue((ObjectHasValue) expression);
        if (expression instanceof ObjectMaxCardinality)
            return translateClassObjectMaxCardinality((ObjectMaxCardinality) expression);
        if (expression instanceof ObjectMinCardinality)
            return translateClassObjectMinCardinality((ObjectMinCardinality) expression);
        if (expression instanceof ObjectSomeValuesFrom)
            return translateClassObjectSomeValuesFrom((ObjectSomeValuesFrom) expression);
        return null;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateClassIRI(IRI expression) {
        return nodeManager.getIRINode(expression.getHasValue());
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime class is not a named class
     */
    protected SubjectNode translateClassObjectUnionOf(ObjectUnionOf expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlClass));
        List<Node> elements = new ArrayList<>();
        Iterator<ClassExpression> expressions = getAll(expression.getClassSeq());
        while (expressions.hasNext())
            elements.add(translateClassExpression(expressions.next()));
        SubjectNode seq = translateUnorderedSequence(elements);
        quads.add(getTriple(main, Vocabulary.owlUnionOf, seq));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime class is not a named class
     */
    protected SubjectNode translateClassObjectIntersectionOf(ObjectIntersectionOf expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlClass));
        List<Node> elements = new ArrayList<>();
        Iterator<ClassExpression> expressions = getAll(expression.getClassSeq());
        while (expressions.hasNext())
            elements.add(translateClassExpression(expressions.next()));
        SubjectNode seq = translateUnorderedSequence(elements);
        quads.add(getTriple(main, Vocabulary.owlIntersectionOf, seq));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException when a runtime individual is not named
     */
    protected SubjectNode translateClassObjectOneOf(ObjectOneOf expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlClass));
        List<Node> elements = new ArrayList<>();
        Iterator<IndividualExpression> expressions = getAll(expression.getIndividualSeq());
        while (expressions.hasNext())
            elements.add(translateIndividualExpression(expressions.next()));
        SubjectNode seq = translateUnorderedSequence(elements);
        quads.add(getTriple(main, Vocabulary.owlOneOf, seq));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime class is not a named class
     */
    protected SubjectNode translateClassObjectComplementOf(ObjectComplementOf expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlClass));
        SubjectNode comp = translateClassExpression(expression.getClasse());
        quads.add(getTriple(main, Vocabulary.owlComplementOf, comp));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime data property is not named
     */
    protected SubjectNode translateClassDataAllValuesFrom(DataAllValuesFrom expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlRestriction));
        List<Node> elements = new ArrayList<>();
        Iterator<DataPropertyExpression> expressions = getAll(expression.getDataPropertySeq());
        while (expressions.hasNext())
            elements.add(translateDataPropertyExpression(expressions.next()));
        if (elements.size() == 1)
            quads.add(getTriple(main, Vocabulary.owlOnProperty, elements.get(0)));
        else
            quads.add(getTriple(main, Vocabulary.owlOnProperties, translateUnorderedSequence(elements)));
        SubjectNode datarange = translateDatarange(expression.getDatarange());
        quads.add(getTriple(main, Vocabulary.owlAllValuesFrom, datarange));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime data property is not named
     */
    protected SubjectNode translateClassDataExactCardinality(DataExactCardinality expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlRestriction));
        Node n = translateLiteralExpression(expression.getCardinality());
        SubjectNode prop = translateDataPropertyExpression(expression.getDataProperty());
        quads.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        if (expression.getDatarange() != null) {
            quads.add(getTriple(main, Vocabulary.owlQualifiedCardinality, n));
            SubjectNode datarange = translateDatarange(expression.getDatarange());
            quads.add(getTriple(main, Vocabulary.owlOnDatarange, datarange));
        } else {
            quads.add(getTriple(main, Vocabulary.owlCardinality, n));
        }
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime data property is not named
     */
    protected SubjectNode transltateClassDataHasValue(DataHasValue expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlRestriction));
        SubjectNode prop = translateDataPropertyExpression(expression.getDataProperty());
        quads.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        Node value = translateLiteralExpression(expression.getLiteral());
        quads.add(getTriple(main, Vocabulary.owlHasValue, value));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime data property is not named
     */
    protected SubjectNode translateClassDataMaxCardinality(DataMaxCardinality expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlRestriction));
        Node n = translateLiteralExpression(expression.getCardinality());
        SubjectNode prop = translateDataPropertyExpression(expression.getDataProperty());
        quads.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        if (expression.getDatarange() != null) {
            quads.add(getTriple(main, Vocabulary.owlMaxQualifiedCardinality, n));
            SubjectNode datarange = translateDatarange(expression.getDatarange());
            quads.add(getTriple(main, Vocabulary.owlOnDatarange, datarange));
        } else {
            quads.add(getTriple(main, Vocabulary.owlMaxCardinality, n));
        }
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime data property is not named
     */
    protected SubjectNode translateClassDataMinCardinality(DataMinCardinality expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlRestriction));
        Node n = translateLiteralExpression(expression.getCardinality());
        SubjectNode prop = translateDataPropertyExpression(expression.getDataProperty());
        quads.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        if (expression.getDatarange() != null) {
            quads.add(getTriple(main, Vocabulary.owlMinQualifiedCardinality, n));
            SubjectNode datarange = translateDatarange(expression.getDatarange());
            quads.add(getTriple(main, Vocabulary.owlOnDatarange, datarange));
        } else {
            quads.add(getTriple(main, Vocabulary.owlMinCardinality, n));
        }
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime data property is not named
     */
    protected SubjectNode translateClassDataSomeValuesFrom(DataSomeValuesFrom expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlRestriction));
        List<Node> elements = new ArrayList<>();
        Iterator<DataPropertyExpression> expressions = getAll(expression.getDataPropertySeq());
        while (expressions.hasNext())
            elements.add(translateDataPropertyExpression(expressions.next()));
        if (elements.size() == 1)
            quads.add(getTriple(main, Vocabulary.owlOnProperty, elements.get(0)));
        else
            quads.add(getTriple(main, Vocabulary.owlOnProperties, translateUnorderedSequence(elements)));
        SubjectNode datarange = translateDatarange(expression.getDatarange());
        quads.add(getTriple(main, Vocabulary.owlSomeValuesFrom, datarange));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime class is not a named class
     */
    protected SubjectNode translateClassObjectAllValuesFrom(ObjectAllValuesFrom expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlRestriction));
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        quads.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        SubjectNode classe = translateClassExpression(expression.getClasse());
        quads.add(getTriple(main, Vocabulary.owlAllValuesFrom, classe));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime class is not a named class
     */
    protected SubjectNode translateClassObjectExactCardinality(ObjectExactCardinality expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlRestriction));
        Node n = translateLiteralExpression(expression.getCardinality());
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        quads.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        if (expression.getClasse() != null) {
            quads.add(getTriple(main, Vocabulary.owlQualifiedCardinality, n));
            SubjectNode classe = translateClassExpression(expression.getClasse());
            quads.add(getTriple(main, Vocabulary.owlOnClass, classe));
        } else {
            quads.add(getTriple(main, Vocabulary.owlCardinality, n));
        }
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime object property is not a named object property
     */
    protected SubjectNode translateClassObjectHasSelf(ObjectHasSelf expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlRestriction));
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        quads.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        Node valueTrue = nodeManager.getLiteralNode("true", org.xowl.infra.store.Vocabulary.xsdBoolean, null);
        quads.add(getTriple(main, Vocabulary.owlHasSelf, valueTrue));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime object property is not a named object property
     */
    protected SubjectNode translateClassObjectHasValue(ObjectHasValue expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlRestriction));
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        quads.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        SubjectNode ind = translateIndividualExpression(expression.getIndividual());
        quads.add(getTriple(main, Vocabulary.owlHasValue, ind));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime class is not a named class
     */
    protected SubjectNode translateClassObjectMaxCardinality(ObjectMaxCardinality expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlRestriction));
        Node n = translateLiteralExpression(expression.getCardinality());
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        quads.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        if (expression.getClasse() != null) {
            quads.add(getTriple(main, Vocabulary.owlMaxQualifiedCardinality, n));
            SubjectNode classe = translateClassExpression(expression.getClasse());
            quads.add(getTriple(main, Vocabulary.owlOnClass, classe));
        } else {
            quads.add(getTriple(main, Vocabulary.owlMaxCardinality, n));
        }
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime class is not a named class
     */
    protected SubjectNode translateClassObjectMinCardinality(ObjectMinCardinality expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlRestriction));
        Node n = translateLiteralExpression(expression.getCardinality());
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        quads.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        if (expression.getClasse() != null) {
            quads.add(getTriple(main, Vocabulary.owlMinQualifiedCardinality, n));
            SubjectNode classe = translateClassExpression(expression.getClasse());
            quads.add(getTriple(main, Vocabulary.owlOnClass, classe));
        } else {
            quads.add(getTriple(main, Vocabulary.owlMinCardinality, n));
        }
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime class is not a named class
     */
    protected SubjectNode translateClassObjectSomeValuesFrom(ObjectSomeValuesFrom expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlRestriction));
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        quads.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        SubjectNode classe = translateClassExpression(expression.getClasse());
        quads.add(getTriple(main, Vocabulary.owlSomeValuesFrom, classe));
        return main;
    }

    /**
     * Translate the specified object property expression
     *
     * @param expression An object property expression
     * @return The RDF node representing the expression
     * @throws TranslationException when a runtime object property is not named
     */
    protected SubjectNode translateObjectPropertyExpression(ObjectPropertyExpression expression) throws TranslationException {
        if (expression instanceof QueryVariable)
            return context.resolve((QueryVariable) expression, ObjectProperty.class);
        if (expression instanceof DynamicExpression)
            return getDynamicNode((DynamicExpression) expression, ObjectProperty.class);
        if (expression instanceof IRI)
            return translateObjectPropertyIRI((IRI) expression);
        if (expression instanceof ObjectInverseOf)
            return translateOjectPropertyInverseOf((ObjectInverseOf) expression);
        return null;
    }

    /**
     * Translate the specified object property expression
     *
     * @param expression An object property expression
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateObjectPropertyIRI(IRI expression) {
        return nodeManager.getIRINode(expression.getHasValue());
    }

    /**
     * Translate the specified object property expression
     *
     * @param expression An object property expression
     * @return The RDF node representing the expression
     * @throws TranslationException when a runtime object property is not named
     */
    protected SubjectNode translateOjectPropertyInverseOf(ObjectInverseOf expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        SubjectNode inv = translateObjectPropertyExpression(expression.getInverse());
        quads.add(getTriple(main, Vocabulary.owlInverseOf, inv));
        return main;
    }

    /**
     * Translate the specified data property expression
     *
     * @param expression An data property expression
     * @return The RDF node representing the expression
     * @throws TranslationException when a runtime data property is not named
     */
    protected SubjectNode translateDataPropertyExpression(DataPropertyExpression expression) throws TranslationException {
        if (expression instanceof QueryVariable)
            return context.resolve((QueryVariable) expression, org.xowl.infra.lang.runtime.DataProperty.class);
        if (expression instanceof DynamicExpression)
            return getDynamicNode((DynamicExpression) expression, DataProperty.class);
        if (expression instanceof IRI) return translateDataPropertyIRI((IRI) expression);
        return null;
    }

    /**
     * Translate the specified data property expression
     *
     * @param expression An data property expression
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateDataPropertyIRI(IRI expression) {
        return nodeManager.getIRINode(expression.getHasValue());
    }

    /**
     * Translate the specified datarange
     *
     * @param expression An datarange
     * @return The RDF node representing the expression
     * @throws TranslationException when a runtime datatype is not named
     */
    protected SubjectNode translateDatarange(Datarange expression) throws TranslationException {
        if (expression instanceof QueryVariable)
            return context.resolve((QueryVariable) expression, org.xowl.infra.lang.runtime.Datatype.class);
        if (expression instanceof DynamicExpression)
            return getDynamicNode((DynamicExpression) expression, Datatype.class);
        if (expression instanceof IRI)
            return translateDatatypeIRI((IRI) expression);
        if (expression instanceof DataComplementOf)
            return translateDatarangeDataComplementOf((DataComplementOf) expression);
        if (expression instanceof DataIntersectionOf)
            return translateDatarangeDataIntersectionOf((DataIntersectionOf) expression);
        if (expression instanceof DataOneOf)
            return translateDatarangeDataOneOf((DataOneOf) expression);
        if (expression instanceof DatatypeRestriction)
            return translateDatarangeDatatypeRestriction((DatatypeRestriction) expression);
        if (expression instanceof DataUnionOf)
            return translateDatarangeDataUnionOf((DataUnionOf) expression);
        return null;
    }

    /**
     * Translate the specified datarange
     *
     * @param expression An datarange
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateDatatypeIRI(IRI expression) {
        return nodeManager.getIRINode(expression.getHasValue());
    }

    /**
     * Translate the specified datarange
     *
     * @param expression An datarange
     * @return The RDF node representing the expression
     * @throws TranslationException when a runtime datatype is not named
     */
    protected SubjectNode translateDatarangeDataComplementOf(DataComplementOf expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, org.xowl.infra.store.Vocabulary.rdfsDatatype));
        SubjectNode comp = translateDatarange(expression.getDatarange());
        quads.add(getTriple(main, Vocabulary.owlDatatypeComplementOf, comp));
        return main;
    }

    /**
     * Translate the specified datarange
     *
     * @param expression An datarange
     * @return The RDF node representing the expression
     * @throws TranslationException when a runtime datatype is not named
     */
    protected SubjectNode translateDatarangeDataIntersectionOf(DataIntersectionOf expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, org.xowl.infra.store.Vocabulary.rdfsDatatype));
        List<Node> elements = new ArrayList<>();
        Iterator<Datarange> expressions = getAll(expression.getDatarangeSeq());
        while (expressions.hasNext())
            elements.add(translateDatarange(expressions.next()));
        SubjectNode seq = translateUnorderedSequence(elements);
        quads.add(getTriple(main, Vocabulary.owlIntersectionOf, seq));
        return main;
    }

    /**
     * Translate the specified datarange
     *
     * @param expression An datarange
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateDatarangeDataOneOf(DataOneOf expression) {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, org.xowl.infra.store.Vocabulary.rdfsDatatype));
        List<Node> elements = new ArrayList<>();
        Iterator<LiteralExpression> expressions = getAll(expression.getLiteralSeq());
        while (expressions.hasNext())
            elements.add(translateLiteralExpression(expressions.next()));
        SubjectNode seq = translateUnorderedSequence(elements);
        quads.add(getTriple(main, Vocabulary.owlOneOf, seq));
        return main;
    }

    /**
     * Translate the specified datarange
     *
     * @param expression An datarange
     * @return The RDF node representing the expression
     * @throws TranslationException when a runtime datatype is not named
     */
    protected SubjectNode translateDatarangeDatatypeRestriction(DatatypeRestriction expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, org.xowl.infra.store.Vocabulary.rdfsDatatype));
        SubjectNode base = translateDatarange(expression.getDatarange());
        quads.add(getTriple(main, Vocabulary.owlOnDatatype, base));
        List<Node> elements = new ArrayList<>();
        for (FacetRestriction elem : expression.getAllFacetRestrictions())
            elements.add(translateDatarangeFacetRestriction(elem));
        SubjectNode seq = translateUnorderedSequence(elements);
        quads.add(getTriple(main, Vocabulary.owlWithRestrictions, seq));
        return main;
    }

    /**
     * Translate the specified facet restriction
     *
     * @param restriction An facet restriction
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateDatarangeFacetRestriction(FacetRestriction restriction) {
        BlankNode main = nodeManager.getBlankNode();
        Node lit = translateLiteralExpression(restriction.getConstrainingValue());
        quads.add(getTriple(main, restriction.getConstrainingFacet().getHasValue(), lit));
        return main;
    }

    /**
     * Translate the specified datarange
     *
     * @param expression An datarange
     * @return The RDF node representing the expression
     * @throws TranslationException when a runtime datatype is not named
     */
    protected SubjectNode translateDatarangeDataUnionOf(DataUnionOf expression) throws TranslationException {
        BlankNode main = nodeManager.getBlankNode();
        quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, org.xowl.infra.store.Vocabulary.rdfsDatatype));
        List<Node> elements = new ArrayList<>();
        Iterator<Datarange> expressions = getAll(expression.getDatarangeSeq());
        while (expressions.hasNext())
            elements.add(translateDatarange(expressions.next()));
        SubjectNode seq = translateUnorderedSequence(elements);
        quads.add(getTriple(main, Vocabulary.owlUnionOf, seq));
        return main;
    }


    /**
     * Translate the specified individual expression
     *
     * @param expression An individual expression
     * @return The RDF node representing the expression
     * @throws TranslationException when a runtime individual is not named
     */
    protected SubjectNode translateIndividualExpression(IndividualExpression expression) throws TranslationException {
        if (expression instanceof QueryVariable)
            return context.resolve((org.xowl.infra.lang.actions.QueryVariable) expression, org.xowl.infra.lang.runtime.Individual.class);
        if (expression instanceof DynamicExpression)
            return getDynamicNode((DynamicExpression) expression, Individual.class);
        if (expression instanceof IRI)
            return translateNamedIndividualIRI((IRI) expression);
        if (expression instanceof AnonymousIndividual)
            return translateAnonymousIndividual((AnonymousIndividual) expression);
        return null;
    }

    /**
     * Translate the specified individual expression
     *
     * @param expression An individual expression
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateNamedIndividualIRI(IRI expression) {
        return nodeManager.getIRINode(expression.getHasValue());
    }

    /**
     * Translate the specified anonymous individual
     *
     * @param expression An anonymous individual
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateAnonymousIndividual(AnonymousIndividual expression) {
        return nodeManager.getAnonNode(expression);
    }

    /**
     * Translate the specified literal expression
     *
     * @param expression A literal expression
     * @return The RDF node representing the expression
     */
    protected Node translateLiteralExpression(LiteralExpression expression) {
        if (expression instanceof QueryVariable)
            return context.resolve((QueryVariable) expression, org.xowl.infra.lang.runtime.Literal.class);
        if (expression instanceof DynamicExpression)
            return getDynamicNode((DynamicExpression) expression, Literal.class);
        if (expression instanceof Literal)
            return translateLiteral((Literal) expression);
        return null;
    }

    /**
     * Translate the specified literal expression
     *
     * @param expression A literal expression
     * @return The RDF node representing the expression
     */
    protected LiteralNode translateLiteral(Literal expression) {
        return nodeManager.getLiteralNode(expression.getLexicalValue(), expression.getMemberOf().getHasValue(), expression.getLangTag());
    }

    /**
     * Translate the specified function expression
     *
     * @param expression A function expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime entity is not named
     */
    protected SubjectNode translateFunctionExpression(FunctionExpression expression) throws TranslationException {
        if (expression instanceof QueryVariable)
            return context.resolve((QueryVariable) expression, Function.class);
        if (expression instanceof DynamicExpression)
            return getDynamicNode((DynamicExpression) expression, Function.class);
        if (expression instanceof IRI)
            return translateFunctionIRI((IRI) expression);

        return null;
    }

    /**
     * Translate the specified function expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateFunctionIRI(IRI expression) {
        return nodeManager.getIRINode(expression.getHasValue());
    }

    /**
     * Translate the specified annotation property
     *
     * @param expression An annotation property
     * @return The RDF node representing the expression
     */
    protected IRINode translateAnnotationProperty(IRI expression) {
        return nodeManager.getIRINode(expression.getHasValue());
    }

    /**
     * Translate the specified annotation subject
     *
     * @param expression An annotation subject
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateAnnotationSubject(AnnotationSubject expression) {
        if (expression instanceof IRI)
            return nodeManager.getIRINode(((IRI) expression).getHasValue());
        else
            return nodeManager.getAnonNode((AnonymousIndividual) expression);
    }

    /**
     * Translate the specified annotation value
     *
     * @param expression An annotation value
     * @return The RDF node representing the expression
     */
    protected Node translateAnnotationValue(AnnotationValue expression) {
        if (expression instanceof IRI)
            return nodeManager.getIRINode(((IRI) expression).getHasValue());
        else if (expression instanceof AnonymousIndividual)
            return nodeManager.getAnonNode((AnonymousIndividual) expression);
        else
            return translateLiteral((Literal) expression);
    }

    /**
     * Translate the specified annotation
     *
     * @param annotated  The annotated element
     * @param annotation The annotation to translate
     */
    protected void translateAnnotation(SubjectNode annotated, Annotation annotation) {
        IRINode prop = translateAnnotationProperty(annotation.getAnnotProperty());
        Node value = translateAnnotationValue(annotation.getAnnotValue());
        quads.add(new Quad(graph, annotated, prop, value));
        if (!annotation.getAllAnnotations().isEmpty()) {
            SubjectNode main = nodeManager.getBlankNode();
            quads.add(getTriple(main, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlAnnotation));
            quads.add(getTriple(main, Vocabulary.owlAnnotatedSource, annotated));
            quads.add(getTriple(main, Vocabulary.owlAnnotatedProperty, prop));
            quads.add(getTriple(main, Vocabulary.owlAnnotatedTarget, value));
            for (Annotation child : annotation.getAllAnnotations())
                translateAnnotation(main, child);
        }
    }

    /**
     * Translate the annotations of the specified axioms, which is translated into the specified main triple
     *
     * @param axiom The axiom which annotations shall be translated
     * @param main  The main triple representing the axiom
     */
    protected void translateAxiomAnnotations(Axiom axiom, Quad main) {
        if (!axiom.getAllAnnotations().isEmpty()) {
            SubjectNode x = nodeManager.getBlankNode();
            quads.add(getTriple(x, org.xowl.infra.store.Vocabulary.rdfType, Vocabulary.owlAxiom));
            quads.add(getTriple(x, Vocabulary.owlAnnotatedSource, main.getSubject()));
            quads.add(getTriple(x, Vocabulary.owlAnnotatedProperty, main.getProperty()));
            quads.add(getTriple(x, Vocabulary.owlAnnotatedTarget, main.getObject()));
            for (Annotation child : axiom.getAllAnnotations())
                translateAnnotation(x, child);
        }
    }

    /**
     * Gets an iterator over all the expressions within the specified expression of a sequence
     *
     * @param expression The expression of a sequence
     * @return An iterator over the contained expressions
     */
    private static Iterator<ClassExpression> getAll(ClassSequenceExpression expression) {
        List<ClassElement> elements = new ArrayList<>(((ClassSequence) expression).getAllClassElements());
        sortElements(elements);
        return new AdaptingIterator<>(elements.iterator(), new Adapter<ClassExpression>() {
            @Override
            public <X> ClassExpression adapt(X element) {
                return ((ClassElement) element).getClasse();
            }
        });
    }

    /**
     * Gets an iterator over all the expressions within the specified expression of a sequence
     *
     * @param expression The expression of a sequence
     * @return An iterator over the contained expressions
     */
    private static Iterator<Datarange> getAll(DatarangeSequenceExpression expression) {
        List<DatarangeElement> elements = new ArrayList<>(((DatarangeSequence) expression).getAllDatarangeElements());
        sortElements(elements);
        return new AdaptingIterator<>(elements.iterator(), new Adapter<Datarange>() {
            @Override
            public <X> Datarange adapt(X element) {
                return ((DatarangeElement) element).getDatarange();
            }
        });
    }

    /**
     * Gets an iterator over all the expressions within the specified expression of a sequence
     *
     * @param expression The expression of a sequence
     * @return An iterator over the contained expressions
     */
    private static Iterator<ObjectPropertyExpression> getAll(ObjectPropertySequenceExpression expression) {
        List<ObjectPropertyElement> elements = new ArrayList<>(((ObjectPropertySequence) expression).getAllObjectPropertyElements());
        sortElements(elements);
        return new AdaptingIterator<>(elements.iterator(), new Adapter<ObjectPropertyExpression>() {
            @Override
            public <X> ObjectPropertyExpression adapt(X element) {
                return ((ObjectPropertyElement) element).getObjectProperty();
            }
        });
    }

    /**
     * Gets an iterator over all the expressions within the specified expression of a sequence
     *
     * @param expression The expression of a sequence
     * @return An iterator over the contained expressions
     */
    private static Iterator<DataPropertyExpression> getAll(DataPropertySequenceExpression expression) {
        List<DataPropertyElement> elements = new ArrayList<>(((DataPropertySequence) expression).getAllDataPropertyElements());
        sortElements(elements);
        return new AdaptingIterator<>(elements.iterator(), new Adapter<DataPropertyExpression>() {
            @Override
            public <X> DataPropertyExpression adapt(X element) {
                return ((DataPropertyElement) element).getDataProperty();
            }
        });
    }

    /**
     * Gets an iterator over all the expressions within the specified expression of a sequence
     *
     * @param expression The expression of a sequence
     * @return An iterator over the contained expressions
     */
    private static Iterator<IndividualExpression> getAll(IndividualSequenceExpression expression) {
        List<IndividualElement> elements = new ArrayList<>(((IndividualSequence) expression).getAllIndividualElements());
        sortElements(elements);
        return new AdaptingIterator<>(elements.iterator(), new Adapter<IndividualExpression>() {
            @Override
            public <X> IndividualExpression adapt(X element) {
                return ((IndividualElement) element).getIndividual();
            }
        });
    }

    /**
     * Gets an iterator over all the expressions within the specified expression of a sequence
     *
     * @param expression The expression of a sequence
     * @return An iterator over the contained expressions
     */
    private static Iterator<LiteralExpression> getAll(LiteralSequenceExpression expression) {
        List<LiteralElement> elements = new ArrayList<>(((LiteralSequence) expression).getAllLiteralElements());
        sortElements(elements);
        return new AdaptingIterator<>(elements.iterator(), new Adapter<LiteralExpression>() {
            @Override
            public <X> LiteralExpression adapt(X element) {
                return ((LiteralElement) element).getLiteral();
            }
        });
    }

    /**
     * Sorts the specified list of sequence element by their indices
     *
     * @param elements A list of sequence elements
     */
    private static void sortElements(List<? extends SequenceElement> elements) {
        Collections.sort(elements, new Comparator<SequenceElement>() {
            @Override
            public int compare(SequenceElement left, SequenceElement right) {
                return Integer.compare(left.getIndex(), right.getIndex());
            }
        });
    }
}
