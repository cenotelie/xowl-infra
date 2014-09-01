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

package org.xowl.store.owl;

import org.xowl.lang.actions.QueryVariable;
import org.xowl.lang.owl2.*;
import org.xowl.lang.owl2.DataAllValuesFrom;
import org.xowl.lang.owl2.DataExactCardinality;
import org.xowl.lang.owl2.DataHasValue;
import org.xowl.lang.owl2.DataMaxCardinality;
import org.xowl.lang.owl2.DataMinCardinality;
import org.xowl.lang.owl2.DataPropertyAssertion;
import org.xowl.lang.owl2.DataSomeValuesFrom;
import org.xowl.lang.owl2.DatatypeRestriction;
import org.xowl.lang.owl2.Literal;
import org.xowl.lang.owl2.ObjectAllValuesFrom;
import org.xowl.lang.owl2.ObjectExactCardinality;
import org.xowl.lang.owl2.ObjectHasSelf;
import org.xowl.lang.owl2.ObjectHasValue;
import org.xowl.lang.owl2.ObjectMaxCardinality;
import org.xowl.lang.owl2.ObjectMinCardinality;
import org.xowl.lang.owl2.ObjectPropertyAssertion;
import org.xowl.lang.owl2.ObjectSomeValuesFrom;
import org.xowl.lang.runtime.Class;
import org.xowl.lang.runtime.*;
import org.xowl.store.XOWLUtils;
import org.xowl.store.rdf.*;
import org.xowl.store.rdf.Property;
import org.xowl.store.voc.OWL2;
import org.xowl.store.Vocabulary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a single use OWL to RDF translator
 *
 * @author Laurent Wouters
 */
public class Translator {
    /**
     * The RDF graph used to create new RDF nodes
     */
    protected XOWLGraph graph;
    /**
     * The ontology that contains the translated input
     */
    protected Ontology ontology;
    /**
     * The input OWL axioms
     */
    protected Iterator<Axiom> input;
    /**
     * The resulting triples
     */
    protected Collection<Triple> triples;
    /**
     * The translation context
     */
    protected TranslationContext context;
    /**
     * Flag whether to translate the OWL annotations
     */
    protected boolean translateAnnotations;
    /**
     * The evaluator for dynamic expressions
     */
    protected Evaluator evaluator;

    /**
     * Initializes this translator
     *
     * @param context              The existing translation context, or null if a new one shall be used
     * @param graph                The XOWL graph used for the creation of new RDF nodes
     * @param ontology             The target ontology for the translated elements
     * @param input                An iterator over the input axioms to translate
     * @param evaluator            The evaluator for dynamic expressions, or null if dyanmic expression shall no be translated and kept as is
     * @param translateAnnotations Whether to translate the OWL annotations
     */
    public Translator(TranslationContext context, XOWLGraph graph, Ontology ontology, Iterator<Axiom> input, Evaluator evaluator, boolean translateAnnotations) {
        this.graph = graph;
        this.ontology = ontology;
        this.input = input;
        this.triples = new ArrayList<>();
        this.context = context;
        if (this.context == null)
            this.context = new TranslationContext();
        this.translateAnnotations = translateAnnotations;
        this.evaluator = evaluator;
    }

    /**
     * Gets the context associated to this translator
     *
     * @return The translation context
     */
    public TranslationContext getContext() {
        return context;
    }

    /**
     * Executes this job and gets the translation result
     *
     * @return The translation result
     * @throws org.xowl.store.owl.TranslationException When a runtime entity is not named
     */
    public Collection<Triple> execute() throws TranslationException {
        while (input.hasNext())
            translateAxiom(input.next());
        return triples;
    }

    /**
     * Gets a dynamic node for the specified dynamic expression
     *
     * @param expression A dynamic expression
     * @return The representing dynamic node
     */
    protected DynamicNode getDynamicNode(Expression expression) {
        return new DynamicNode(expression);
    }

    /**
     * Gets a dynamic node for the specified dynamic expression of the specified expected type
     *
     * @param expression A dynamic expression
     * @param type       The expected type of the expression
     * @return The representing dynamic node
     */
    protected DynamicNode getDynamicNode(Expression expression, java.lang.Class type) {
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
    protected Triple getTriple(SubjectNode subject, String property, Node object) {
        return new Triple(ontology, subject, graph.getNodeIRI(property), object);
    }

    /**
     * Gets the triple for the specified elements
     *
     * @param subject  A subject node
     * @param property The property
     * @param object   The object value
     * @return The equivalent triple
     */
    protected Triple getTriple(SubjectNode subject, String property, String object) {
        return new Triple(ontology, subject, graph.getNodeIRI(property), graph.getNodeIRI(object));
    }

    /**
     * Translate the specified list of RDF nodes into an ordered RDF list
     *
     * @param elements The list of elements to translate
     * @return The first node of the RDF list
     */
    protected SubjectNode translateOrderedSequence(List<Node> elements) {
        if (elements.isEmpty())
            return graph.getNodeIRI(Vocabulary.rdfNil);
        SubjectNode[] proxies = new SubjectNode[elements.size()];
        for (int i = 0; i != proxies.length; i++) {
            BlankNode proxy = graph.getBlankNode();
            proxies[i] = proxy;
            triples.add(getTriple(proxy, Vocabulary.rdfFirst, elements.get(i)));
        }
        for (int i = 0; i != proxies.length - 1; i++)
            triples.add(getTriple(proxies[i], Vocabulary.rdfRest, proxies[i + 1]));
        triples.add(getTriple(proxies[proxies.length - 1], Vocabulary.rdfRest, Vocabulary.rdfNil));
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
     * @throws org.xowl.store.owl.TranslationException When a runtime entity is not named
     */
    protected void translateAxiom(Axiom axiom) throws TranslationException {
        java.lang.Class c = axiom.getClass();
        if (c == Declaration.class)
            translateAxiomDeclaration((Declaration) axiom);
        else if (c == DatatypeDefinition.class)
            translateAxiomDatatypeDefinition((DatatypeDefinition) axiom);
        else if (c == SubClassOf.class)
            translateAxiomSubClassOf((SubClassOf) axiom);
        else if (c == EquivalentClasses.class)
            translateAxiomEquivalentClasses((EquivalentClasses) axiom);
        else if (c == DisjointClasses.class)
            translateAxiomDisjointClasses((DisjointClasses) axiom);
        else if (c == DisjointUnion.class)
            translateAxiomDisjointUnion((DisjointUnion) axiom);
        else if (c == SubObjectPropertyOf.class)
            translateAxiomSubObjectPropertyOf((SubObjectPropertyOf) axiom);
        else if (c == EquivalentObjectProperties.class)
            translateAxiomEquivalentObjectProperties((EquivalentObjectProperties) axiom);
        else if (c == DisjointObjectProperties.class)
            translateAxiomDisjointObjectProperties((DisjointObjectProperties) axiom);
        else if (c == InverseObjectProperties.class)
            translateAxiomInverseObjectProperties((InverseObjectProperties) axiom);
        else if (c == ObjectPropertyDomain.class)
            translateAxiomObjectPropertyDomain((ObjectPropertyDomain) axiom);
        else if (c == ObjectPropertyRange.class)
            translateAxiomObjectPropertyRange((ObjectPropertyRange) axiom);
        else if (c == FunctionalObjectProperty.class)
            translateAxiomFunctionalObjectProperty((FunctionalObjectProperty) axiom);
        else if (c == InverseFunctionalObjectProperty.class)
            translateAxiomInverseFunctionalObjectProperty((InverseFunctionalObjectProperty) axiom);
        else if (c == ReflexiveObjectProperty.class)
            translateAxiomReflexiveObjectProperty((ReflexiveObjectProperty) axiom);
        else if (c == IrreflexiveObjectProperty.class)
            translateAxiomIrreflexiveObjectProperty((IrreflexiveObjectProperty) axiom);
        else if (c == SymmetricObjectProperty.class)
            translateAxiomSymmetricObjectProperty((SymmetricObjectProperty) axiom);
        else if (c == AsymmetricObjectProperty.class)
            translateAxiomAsymmetricObjectProperty((AsymmetricObjectProperty) axiom);
        else if (c == TransitiveObjectProperty.class)
            translateAxiomTransitiveObjectProperty((TransitiveObjectProperty) axiom);
        else if (c == SubDataPropertyOf.class)
            translateAxiomSubDataPropertyOf((SubDataPropertyOf) axiom);
        else if (c == EquivalentDataProperties.class)
            translateAxiomEquivalentDataProperties((EquivalentDataProperties) axiom);
        else if (c == DisjointDataProperties.class)
            translateAxiomDisjointDataProperties((DisjointDataProperties) axiom);
        else if (c == DataPropertyDomain.class)
            translateAxiomDataPropertyDomain((DataPropertyDomain) axiom);
        else if (c == DataPropertyRange.class)
            translateAxiomDataPropertyRange((DataPropertyRange) axiom);
        else if (c == FunctionalDataProperty.class)
            translateAxiomFunctionalDataProperty((FunctionalDataProperty) axiom);
        else if (c == SameIndividual.class)
            translateAxiomSameIndividual((SameIndividual) axiom);
        else if (c == DifferentIndividuals.class)
            translateAxiomDifferentIndividuals((DifferentIndividuals) axiom);
        else if (c == ClassAssertion.class)
            translateAxiomClassAssertion((ClassAssertion) axiom);
        else if (c == ObjectPropertyAssertion.class)
            translateAxiomObjectPropertyAssertion((ObjectPropertyAssertion) axiom);
        else if (c == NegativeObjectPropertyAssertion.class)
            translateAxiomNegativeObjectPropertyAssertion((NegativeObjectPropertyAssertion) axiom);
        else if (c == DataPropertyAssertion.class)
            translateAxiomDataPropertyAssertion((DataPropertyAssertion) axiom);
        else if (c == NegativeDataPropertyAssertion.class)
            translateAxiomNegativeDataPropertyAssertion((NegativeDataPropertyAssertion) axiom);
        else if (c == HasKey.class)
            translateAxiomHasKey((HasKey) axiom);
        else if (c == SubAnnotationPropertyOf.class)
            translateAxiomSubAnnotationPropertyOf((SubAnnotationPropertyOf) axiom);
        else if (c == AnnotationPropertyDomain.class)
            translateAxiomAnnotationPropertyDomain((AnnotationPropertyDomain) axiom);
        else if (c == AnnotationPropertyRange.class)
            translateAxiomAnnotationPropertyRange((AnnotationPropertyRange) axiom);
        else if (c == AnnotationAssertion.class)
            translateAxiomAnnotationAssertion((AnnotationAssertion) axiom);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     */
    protected void translateAxiomDeclaration(Declaration axiom) {
        SubjectNode entityNode = graph.getNodeIRI(axiom.getEntity().getHasValue());
        Triple triple = null;
        if (OWL2.entityClass.equals(axiom.getType()))
            triple = getTriple(entityNode, Vocabulary.rdfType, Vocabulary.owlClass);
        else if (OWL2.entityDatatype.equals(axiom.getType()))
            triple = getTriple(entityNode, Vocabulary.rdfType, Vocabulary.rdfsDatatype);
        else if (OWL2.entityNamedIndividual.equals(axiom.getType()))
            triple = getTriple(entityNode, Vocabulary.rdfType, Vocabulary.owlNamedIndividual);
        else if (OWL2.entityObjectProperty.equals(axiom.getType()))
            triple = getTriple(entityNode, Vocabulary.rdfType, Vocabulary.owlObjectProperty);
        else if (OWL2.entityDataProperty.equals(axiom.getType()))
            triple = getTriple(entityNode, Vocabulary.rdfType, Vocabulary.owlDataProperty);
        else if (OWL2.entityAnnotationProperty.equals(axiom.getType()))
            triple = getTriple(entityNode, Vocabulary.rdfType, Vocabulary.owlAnnotationProperty);
        if (triple != null) {
            triples.add(triple);
            if (translateAnnotations)
                translateAxiomAnnotations(axiom, triple);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime datatype is not named
     */
    protected void translateAxiomDatatypeDefinition(DatatypeDefinition axiom) throws TranslationException {
        SubjectNode dt = translateDatarange(axiom.getDatatype());
        SubjectNode dr = translateDatarange(axiom.getDatarange());
        triples.add(getTriple(dt, Vocabulary.owlEquivalentClass, dr));
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime class is not named
     */
    protected void translateAxiomSubClassOf(SubClassOf axiom) throws TranslationException {
        SubjectNode sub = translateClassExpression(axiom.getClasse());
        SubjectNode sup = translateClassExpression(axiom.getSuperClass());
        Triple triple = getTriple(sub, Vocabulary.rdfsSubClassOf, sup);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime class is not named
     */
    protected void translateAxiomEquivalentClasses(EquivalentClasses axiom) throws TranslationException {
        List<SubjectNode> elements = new ArrayList<>();
        Iterator<ClassExpression> expressions = XOWLUtils.getAll(axiom.getClassSeq());
        while (expressions.hasNext())
            elements.add(translateClassExpression(expressions.next()));
        for (int i = 0; i != elements.size() - 1; i++) {
            Triple triple = getTriple(elements.get(i), Vocabulary.owlEquivalentClass, elements.get(i + 1));
            triples.add(triple);
            if (translateAnnotations)
                translateAxiomAnnotations(axiom, triple);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime class is not named
     */
    protected void translateAxiomDisjointClasses(DisjointClasses axiom) throws TranslationException {
        List<Node> elements = new ArrayList<>();
        Iterator<ClassExpression> expressions = XOWLUtils.getAll(axiom.getClassSeq());
        while (expressions.hasNext())
            elements.add(translateClassExpression(expressions.next()));
        if (elements.size() == 2) {
            Triple triple = getTriple((SubjectNode) elements.get(0), Vocabulary.owlDisjointWith, elements.get(1));
            triples.add(triple);
            if (translateAnnotations)
                translateAxiomAnnotations(axiom, triple);
        } else {
            SubjectNode main = graph.getBlankNode();
            triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlAllDisjointClasses));
            triples.add(getTriple(main, Vocabulary.owlMembers, translateUnorderedSequence(elements)));
            if (translateAnnotations) {
                for (Annotation annotation : axiom.getAllAnnotations())
                    translateAnnotation(main, annotation);
            }
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime class is not named
     */
    protected void translateAxiomDisjointUnion(DisjointUnion axiom) throws TranslationException {
        SubjectNode classe = translateClassExpression(axiom.getClasse());
        List<Node> elements = new ArrayList<>();
        Iterator<ClassExpression> expressions = XOWLUtils.getAll(axiom.getClassSeq());
        while (expressions.hasNext())
            elements.add(translateClassExpression(expressions.next()));
        Triple triple = getTriple(classe, Vocabulary.owlDisjointUnionOf, translateUnorderedSequence(elements));
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime object property is not named
     */
    protected void translateAxiomSubObjectPropertyOf(SubObjectPropertyOf axiom) throws TranslationException {
        if (axiom.getObjectPropertyChain() != null) {
            List<Node> elements = new ArrayList<>();
            Iterator<ObjectPropertyExpression> expressions = XOWLUtils.getAll(axiom.getObjectPropertyChain());
            while (expressions.hasNext())
                elements.add(translateObjectPropertyExpression(expressions.next()));
            SubjectNode sup = translateObjectPropertyExpression(axiom.getSuperObjectProperty());
            Triple triple = getTriple(sup, Vocabulary.owlPropertyChainAxiom, translateOrderedSequence(elements));
            triples.add(triple);
            if (translateAnnotations)
                translateAxiomAnnotations(axiom, triple);
        } else {
            SubjectNode sub = translateObjectPropertyExpression(axiom.getObjectProperty());
            SubjectNode sup = translateObjectPropertyExpression(axiom.getSuperObjectProperty());
            Triple triple = getTriple(sub, Vocabulary.rdfsSubPropertyOf, sup);
            triples.add(triple);
            if (translateAnnotations)
                translateAxiomAnnotations(axiom, triple);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime object property is not named
     */
    protected void translateAxiomEquivalentObjectProperties(EquivalentObjectProperties axiom) throws TranslationException {
        List<SubjectNode> elements = new ArrayList<>();
        Iterator<ObjectPropertyExpression> expressions = XOWLUtils.getAll(axiom.getObjectPropertySeq());
        while (expressions.hasNext())
            elements.add(translateObjectPropertyExpression(expressions.next()));
        for (int i = 0; i != elements.size() - 1; i++) {
            Triple triple = getTriple(elements.get(i), Vocabulary.owlEquivalentProperty, elements.get(i + 1));
            triples.add(triple);
            if (translateAnnotations)
                translateAxiomAnnotations(axiom, triple);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime object property is not named
     */
    protected void translateAxiomDisjointObjectProperties(DisjointObjectProperties axiom) throws TranslationException {
        List<Node> elements = new ArrayList<>();
        Iterator<ObjectPropertyExpression> expressions = XOWLUtils.getAll(axiom.getObjectPropertySeq());
        while (expressions.hasNext())
            elements.add(translateObjectPropertyExpression(expressions.next()));
        if (elements.size() == 2) {
            Triple triple = getTriple((SubjectNode) elements.get(0), Vocabulary.owlPropertyDisjointWith, elements.get(1));
            triples.add(triple);
            if (translateAnnotations)
                translateAxiomAnnotations(axiom, triple);
        } else {
            SubjectNode main = graph.getBlankNode();
            triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlAllDisjointProperties));
            triples.add(getTriple(main, Vocabulary.owlMembers, translateUnorderedSequence(elements)));
            if (translateAnnotations) {
                for (Annotation annotation : axiom.getAllAnnotations())
                    translateAnnotation(main, annotation);
            }
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime object property is not named
     */
    protected void translateAxiomInverseObjectProperties(InverseObjectProperties axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        SubjectNode inv = translateObjectPropertyExpression(axiom.getInverse());
        Triple triple = getTriple(prop, Vocabulary.owlInverseOf, inv);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime class is not named
     */
    protected void translateAxiomObjectPropertyDomain(ObjectPropertyDomain axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        SubjectNode classe = translateClassExpression(axiom.getClasse());
        Triple triple = getTriple(prop, Vocabulary.rdfsDomain, classe);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime class is not named
     */
    protected void translateAxiomObjectPropertyRange(ObjectPropertyRange axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        SubjectNode classe = translateClassExpression(axiom.getClasse());
        Triple triple = getTriple(prop, Vocabulary.rdfsRange, classe);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime object property is not named
     */
    protected void translateAxiomFunctionalObjectProperty(FunctionalObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Triple triple = getTriple(prop, Vocabulary.rdfType, Vocabulary.owlFunctionalProperty);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime object property is not named
     */
    protected void translateAxiomInverseFunctionalObjectProperty(InverseFunctionalObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Triple triple = getTriple(prop, Vocabulary.rdfType, Vocabulary.owlInverseFunctionalProperty);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime object property is not named
     */
    protected void translateAxiomReflexiveObjectProperty(ReflexiveObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Triple triple = getTriple(prop, Vocabulary.rdfType, Vocabulary.owlReflexiveProperty);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime object property is not named
     */
    protected void translateAxiomIrreflexiveObjectProperty(IrreflexiveObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Triple triple = getTriple(prop, Vocabulary.rdfType, Vocabulary.owlIrreflexiveProperty);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime object property is not named
     */
    protected void translateAxiomSymmetricObjectProperty(SymmetricObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Triple triple = getTriple(prop, Vocabulary.rdfType, Vocabulary.owlSymmetricProperty);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime object property is not named
     */
    protected void translateAxiomAsymmetricObjectProperty(AsymmetricObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Triple triple = getTriple(prop, Vocabulary.rdfType, Vocabulary.owlAsymmetricProperty);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime object property is not named
     */
    protected void translateAxiomTransitiveObjectProperty(TransitiveObjectProperty axiom) throws TranslationException {
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        Triple triple = getTriple(prop, Vocabulary.rdfType, Vocabulary.owlTransitiveProperty);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime data property is not named
     */
    protected void translateAxiomSubDataPropertyOf(SubDataPropertyOf axiom) throws TranslationException {
        SubjectNode sub = translateDataPropertyExpression(axiom.getDataProperty());
        SubjectNode sup = translateDataPropertyExpression(axiom.getSuperDataProperty());
        Triple triple = getTriple(sub, Vocabulary.rdfsSubPropertyOf, sup);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime data property is not named
     */
    protected void translateAxiomEquivalentDataProperties(EquivalentDataProperties axiom) throws TranslationException {
        List<SubjectNode> elements = new ArrayList<>();
        Iterator<DataPropertyExpression> expressions = XOWLUtils.getAll(axiom.getDataPropertySeq());
        while (expressions.hasNext())
            elements.add(translateDataPropertyExpression(expressions.next()));
        for (int i = 0; i != elements.size() - 1; i++) {
            Triple triple = getTriple(elements.get(i), Vocabulary.owlEquivalentProperty, elements.get(i + 1));
            triples.add(triple);
            if (translateAnnotations)
                translateAxiomAnnotations(axiom, triple);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime data property is not named
     */
    protected void translateAxiomDisjointDataProperties(DisjointDataProperties axiom) throws TranslationException {
        List<Node> elements = new ArrayList<>();
        Iterator<DataPropertyExpression> expressions = XOWLUtils.getAll(axiom.getDataPropertySeq());
        while (expressions.hasNext())
            elements.add(translateDataPropertyExpression(expressions.next()));
        if (elements.size() == 2) {
            Triple triple = getTriple((SubjectNode) elements.get(0), Vocabulary.owlPropertyDisjointWith, elements.get(1));
            triples.add(triple);
            if (translateAnnotations)
                translateAxiomAnnotations(axiom, triple);
        } else {
            SubjectNode main = graph.getBlankNode();
            triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlAllDisjointProperties));
            triples.add(getTriple(main, Vocabulary.owlMembers, translateUnorderedSequence(elements)));
            if (translateAnnotations) {
                for (Annotation annotation : axiom.getAllAnnotations())
                    translateAnnotation(main, annotation);
            }
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime class is not named
     */
    protected void translateAxiomDataPropertyDomain(DataPropertyDomain axiom) throws TranslationException {
        SubjectNode prop = translateDataPropertyExpression(axiom.getDataProperty());
        SubjectNode classe = translateClassExpression(axiom.getClasse());
        Triple triple = getTriple(prop, Vocabulary.rdfsDomain, classe);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime data property is not named
     */
    protected void translateAxiomDataPropertyRange(DataPropertyRange axiom) throws TranslationException {
        SubjectNode prop = translateDataPropertyExpression(axiom.getDataProperty());
        SubjectNode datatype = translateDatarange(axiom.getDatarange());
        Triple triple = getTriple(prop, Vocabulary.rdfsRange, datatype);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime data property is not named
     */
    protected void translateAxiomFunctionalDataProperty(FunctionalDataProperty axiom) throws TranslationException {
        SubjectNode prop = translateDataPropertyExpression(axiom.getDataProperty());
        Triple triple = getTriple(prop, Vocabulary.rdfType, Vocabulary.owlFunctionalProperty);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException when a runtime individual is not named
     */
    protected void translateAxiomSameIndividual(SameIndividual axiom) throws TranslationException {
        List<SubjectNode> elements = new ArrayList<>();
        Iterator<IndividualExpression> expressions = XOWLUtils.getAll(axiom.getIndividualSeq());
        while (expressions.hasNext())
            elements.add(translateIndividualExpression(expressions.next()));
        for (int i = 0; i != elements.size() - 1; i++) {
            Triple triple = getTriple(elements.get(i), Vocabulary.owlSameAs, elements.get(i + 1));
            triples.add(triple);
            if (translateAnnotations)
                translateAxiomAnnotations(axiom, triple);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException when a runtime individual is not named
     */
    protected void translateAxiomDifferentIndividuals(DifferentIndividuals axiom) throws TranslationException {
        List<Node> elements = new ArrayList<>();
        Iterator<IndividualExpression> expressions = XOWLUtils.getAll(axiom.getIndividualSeq());
        while (expressions.hasNext())
            elements.add(translateIndividualExpression(expressions.next()));
        if (elements.size() == 2) {
            Triple triple = getTriple((SubjectNode) elements.get(0), Vocabulary.owlDifferentFrom, elements.get(1));
            triples.add(triple);
            if (translateAnnotations)
                translateAxiomAnnotations(axiom, triple);
        } else {
            SubjectNode main = graph.getBlankNode();
            triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlAllDifferent));
            triples.add(getTriple(main, Vocabulary.owlMembers, translateUnorderedSequence(elements)));
            if (translateAnnotations) {
                for (Annotation annotation : axiom.getAllAnnotations())
                    translateAnnotation(main, annotation);
            }
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime class is not named
     */
    protected void translateAxiomClassAssertion(ClassAssertion axiom) throws TranslationException {
        SubjectNode ind = translateIndividualExpression(axiom.getIndividual());
        SubjectNode classe = translateClassExpression(axiom.getClasse());
        Triple triple = getTriple(ind, Vocabulary.rdfType, classe);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime object property is not named
     */
    protected void translateAxiomObjectPropertyAssertion(ObjectPropertyAssertion axiom) throws TranslationException {
        Property prop = null;
        if (axiom.getObjectProperty() instanceof ObjectInverseOf) {
            ObjectInverseOf expInv = (ObjectInverseOf) axiom.getObjectProperty();
            prop = (Property) translateObjectPropertyExpression(expInv.getInverse());
        } else
            prop = (Property) translateObjectPropertyExpression(axiom.getObjectProperty());
        SubjectNode ind = translateIndividualExpression(axiom.getIndividual());
        SubjectNode value = translateIndividualExpression(axiom.getValueIndividual());
        Triple triple = new Triple(ontology, ind, prop, value);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime object property is not named
     */
    protected void translateAxiomNegativeObjectPropertyAssertion(NegativeObjectPropertyAssertion axiom) throws TranslationException {
        SubjectNode main = graph.getBlankNode();
        SubjectNode prop = translateObjectPropertyExpression(axiom.getObjectProperty());
        SubjectNode ind = translateIndividualExpression(axiom.getIndividual());
        SubjectNode value = translateIndividualExpression(axiom.getValueIndividual());
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlNegativePropertyAssertion));
        triples.add(getTriple(main, Vocabulary.owlSourceIndividual, ind));
        triples.add(getTriple(main, Vocabulary.owlAssertionProperty, prop));
        triples.add(getTriple(main, Vocabulary.owlTargetIndividual, value));
        if (translateAnnotations) {
            for (Annotation annotation : axiom.getAllAnnotations())
                translateAnnotation(main, annotation);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime data property is not named
     */
    protected void translateAxiomDataPropertyAssertion(DataPropertyAssertion axiom) throws TranslationException {
        Property prop = (Property) translateDataPropertyExpression(axiom.getDataProperty());
        SubjectNode ind = translateIndividualExpression(axiom.getIndividual());
        Node value = translateLiteralExpression(axiom.getValueLiteral());
        Triple triple = new Triple(ontology, ind, prop, value);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime data property is not named
     */
    protected void translateAxiomNegativeDataPropertyAssertion(NegativeDataPropertyAssertion axiom) throws TranslationException {
        SubjectNode main = graph.getBlankNode();
        SubjectNode prop = translateDataPropertyExpression(axiom.getDataProperty());
        SubjectNode ind = translateIndividualExpression(axiom.getIndividual());
        Node value = translateLiteralExpression(axiom.getValueLiteral());
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlNegativePropertyAssertion));
        triples.add(getTriple(main, Vocabulary.owlSourceIndividual, ind));
        triples.add(getTriple(main, Vocabulary.owlAssertionProperty, prop));
        triples.add(getTriple(main, Vocabulary.owlTargetValue, value));
        if (translateAnnotations) {
            for (Annotation annotation : axiom.getAllAnnotations())
                translateAnnotation(main, annotation);
        }
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     * @throws org.xowl.store.owl.TranslationException When a runtime entity is not named
     */
    protected void translateAxiomHasKey(HasKey axiom) throws TranslationException {
        SubjectNode classe = translateClassExpression(axiom.getClasse());
        List<Node> elements = new ArrayList<>();
        Iterator<ObjectPropertyExpression> objExpressions = XOWLUtils.getAll(axiom.getObjectPropertySeq());
        while (objExpressions.hasNext())
            elements.add(translateObjectPropertyExpression(objExpressions.next()));
        Iterator<DataPropertyExpression> dataExpressions = XOWLUtils.getAll(axiom.getDataPropertySeq());
        while (dataExpressions.hasNext())
            elements.add(translateDataPropertyExpression(dataExpressions.next()));
        Triple triple = getTriple(classe, Vocabulary.owlHasKey, translateUnorderedSequence(elements));
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     */
    protected void translateAxiomSubAnnotationPropertyOf(SubAnnotationPropertyOf axiom) {
        SubjectNode sub = translateAnnotationProperty(axiom.getAnnotProperty());
        SubjectNode sup = translateAnnotationProperty(axiom.getSuperAnnotProperty());
        triples.add(getTriple(sub, Vocabulary.rdfsSubPropertyOf, sup));
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     */
    protected void translateAxiomAnnotationPropertyDomain(AnnotationPropertyDomain axiom) {
        SubjectNode prop = translateAnnotationProperty(axiom.getAnnotProperty());
        triples.add(getTriple(prop, Vocabulary.rdfsDomain, graph.getNodeIRI(axiom.getAnnotDomain().getHasValue())));
    }

    /**
     * Translates the specified axiom
     *
     * @param axiom The OWL axiom to translate
     */
    protected void translateAxiomAnnotationPropertyRange(AnnotationPropertyRange axiom) {
        SubjectNode prop = translateAnnotationProperty(axiom.getAnnotProperty());
        triples.add(getTriple(prop, Vocabulary.rdfsRange, graph.getNodeIRI(axiom.getAnnotRange().getHasValue())));
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
        Triple triple = new Triple(ontology, subject, prop, value);
        triples.add(triple);
        if (translateAnnotations)
            translateAxiomAnnotations(axiom, triple);
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException When a runtime entity is not named
     */
    protected SubjectNode translateClassExpression(ClassExpression expression) throws TranslationException {
        if (XOWLUtils.isDynamicExpression(expression)) {
            if (evaluator != null)
                return translateClassRuntime(evaluator.evalClass(expression));
            return getDynamicNode(expression, org.xowl.lang.runtime.Class.class);
        }
        if (XOWLUtils.isQueryVar(expression)) {
            if (evaluator != null && evaluator.can((QueryVariable) expression))
                return translateClassRuntime(evaluator.evalClass(expression));
            else
                return context.getVariableNode((QueryVariable) expression, Class.class);
        }
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
        return graph.getNodeIRI(expression.getHasValue());
    }

    /**
     * Translate the specified runtime class
     *
     * @param expression A runtime class
     * @return The RDF node representing the expression
     * @throws TranslationException When the runtime class is not a named class
     */
    protected SubjectNode translateClassRuntime(org.xowl.lang.runtime.Class expression) throws TranslationException {
        // Here an OWL Class is expected to be a named Class
        if (expression.getInterpretationOf() == null)
            throw new TranslationException("Cannot translate anonymous entities");
        return graph.getNodeIRI(expression.getInterpretationOf().getHasIRI().getHasValue());
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws TranslationException When a runtime class is not a named class
     */
    protected SubjectNode translateClassObjectUnionOf(ObjectUnionOf expression) throws TranslationException {
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlClass));
        List<Node> elements = new ArrayList<>();
        Iterator<ClassExpression> expressions = XOWLUtils.getAll(expression.getClassSeq());
        while (expressions.hasNext())
            elements.add(translateClassExpression(expressions.next()));
        SubjectNode seq = translateUnorderedSequence(elements);
        triples.add(getTriple(main, Vocabulary.owlUnionOf, seq));
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
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlClass));
        List<Node> elements = new ArrayList<>();
        Iterator<ClassExpression> expressions = XOWLUtils.getAll(expression.getClassSeq());
        while (expressions.hasNext())
            elements.add(translateClassExpression(expressions.next()));
        SubjectNode seq = translateUnorderedSequence(elements);
        triples.add(getTriple(main, Vocabulary.owlIntersectionOf, seq));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime individual is not named
     */
    protected SubjectNode translateClassObjectOneOf(ObjectOneOf expression) throws TranslationException {
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlClass));
        List<Node> elements = new ArrayList<>();
        Iterator<IndividualExpression> expressions = XOWLUtils.getAll(expression.getIndividualSeq());
        while (expressions.hasNext())
            elements.add(translateIndividualExpression(expressions.next()));
        SubjectNode seq = translateUnorderedSequence(elements);
        triples.add(getTriple(main, Vocabulary.owlOneOf, seq));
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
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlClass));
        SubjectNode comp = translateClassExpression(expression.getClasse());
        triples.add(getTriple(main, Vocabulary.owlComplementOf, comp));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException When a runtime data property is not named
     */
    protected SubjectNode translateClassDataAllValuesFrom(DataAllValuesFrom expression) throws TranslationException {
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlRestriction));
        List<Node> elements = new ArrayList<>();
        Iterator<DataPropertyExpression> expressions = XOWLUtils.getAll(expression.getDataPropertySeq());
        while (expressions.hasNext())
            elements.add(translateDataPropertyExpression(expressions.next()));
        if (elements.size() == 1)
            triples.add(getTriple(main, Vocabulary.owlOnProperty, elements.get(0)));
        else
            triples.add(getTriple(main, Vocabulary.owlOnProperties, translateUnorderedSequence(elements)));
        SubjectNode datarange = translateDatarange(expression.getDatarange());
        triples.add(getTriple(main, Vocabulary.owlAllValuesFrom, datarange));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException When a runtime data property is not named
     */
    protected SubjectNode translateClassDataExactCardinality(DataExactCardinality expression) throws TranslationException {
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlRestriction));
        Node n = translateLiteralExpression(expression.getCardinality());
        SubjectNode prop = translateDataPropertyExpression(expression.getDataProperty());
        triples.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        if (expression.getDatarange() != null) {
            triples.add(getTriple(main, Vocabulary.owlQualifiedCardinality, n));
            SubjectNode datarange = translateDatarange(expression.getDatarange());
            triples.add(getTriple(main, Vocabulary.owlOnDatarange, datarange));
        } else {
            triples.add(getTriple(main, Vocabulary.owlCardinality, n));
        }
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException When a runtime data property is not named
     */
    protected SubjectNode transltateClassDataHasValue(DataHasValue expression) throws TranslationException {
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlRestriction));
        SubjectNode prop = translateDataPropertyExpression(expression.getDataProperty());
        triples.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        Node value = translateLiteralExpression(expression.getLiteral());
        triples.add(getTriple(main, Vocabulary.owlHasValue, value));
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException When a runtime data property is not named
     */
    protected SubjectNode translateClassDataMaxCardinality(DataMaxCardinality expression) throws TranslationException {
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlRestriction));
        Node n = translateLiteralExpression(expression.getCardinality());
        SubjectNode prop = translateDataPropertyExpression(expression.getDataProperty());
        triples.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        if (expression.getDatarange() != null) {
            triples.add(getTriple(main, Vocabulary.owlMaxQualifiedCardinality, n));
            SubjectNode datarange = translateDatarange(expression.getDatarange());
            triples.add(getTriple(main, Vocabulary.owlOnDatarange, datarange));
        } else {
            triples.add(getTriple(main, Vocabulary.owlMaxCardinality, n));
        }
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException When a runtime data property is not named
     */
    protected SubjectNode translateClassDataMinCardinality(DataMinCardinality expression) throws TranslationException {
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlRestriction));
        Node n = translateLiteralExpression(expression.getCardinality());
        SubjectNode prop = translateDataPropertyExpression(expression.getDataProperty());
        triples.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        if (expression.getDatarange() != null) {
            triples.add(getTriple(main, Vocabulary.owlMinQualifiedCardinality, n));
            SubjectNode datarange = translateDatarange(expression.getDatarange());
            triples.add(getTriple(main, Vocabulary.owlOnDatarange, datarange));
        } else {
            triples.add(getTriple(main, Vocabulary.owlMinCardinality, n));
        }
        return main;
    }

    /**
     * Translate the specified class expression
     *
     * @param expression A class expression
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException When a runtime data property is not named
     */
    protected SubjectNode translateClassDataSomeValuesFrom(DataSomeValuesFrom expression) throws TranslationException {
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlRestriction));
        List<Node> elements = new ArrayList<>();
        Iterator<DataPropertyExpression> expressions = XOWLUtils.getAll(expression.getDataPropertySeq());
        while (expressions.hasNext())
            elements.add(translateDataPropertyExpression(expressions.next()));
        if (elements.size() == 1)
            triples.add(getTriple(main, Vocabulary.owlOnProperty, elements.get(0)));
        else
            triples.add(getTriple(main, Vocabulary.owlOnProperties, translateUnorderedSequence(elements)));
        SubjectNode datarange = translateDatarange(expression.getDatarange());
        triples.add(getTriple(main, Vocabulary.owlSomeValuesFrom, datarange));
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
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlRestriction));
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        triples.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        SubjectNode classe = translateClassExpression(expression.getClasse());
        triples.add(getTriple(main, Vocabulary.owlAllValuesFrom, classe));
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
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlRestriction));
        Node n = translateLiteralExpression(expression.getCardinality());
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        triples.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        if (expression.getClasse() != null) {
            triples.add(getTriple(main, Vocabulary.owlQualifiedCardinality, n));
            SubjectNode classe = translateClassExpression(expression.getClasse());
            triples.add(getTriple(main, Vocabulary.owlOnDatarange, classe));
        } else {
            triples.add(getTriple(main, Vocabulary.owlCardinality, n));
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
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlRestriction));
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        triples.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        Node valueTrue = graph.getLiteralNode("true", Vocabulary.xsdBoolean, null);
        triples.add(getTriple(main, Vocabulary.owlHasSelf, valueTrue));
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
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlRestriction));
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        triples.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        SubjectNode ind = translateIndividualExpression(expression.getIndividual());
        triples.add(getTriple(main, Vocabulary.owlHasValue, ind));
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
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlRestriction));
        Node n = translateLiteralExpression(expression.getCardinality());
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        triples.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        if (expression.getClasse() != null) {
            triples.add(getTriple(main, Vocabulary.owlMaxQualifiedCardinality, n));
            SubjectNode classe = translateClassExpression(expression.getClasse());
            triples.add(getTriple(main, Vocabulary.owlOnDatarange, classe));
        } else {
            triples.add(getTriple(main, Vocabulary.owlMaxCardinality, n));
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
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlRestriction));
        Node n = translateLiteralExpression(expression.getCardinality());
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        triples.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        if (expression.getClasse() != null) {
            triples.add(getTriple(main, Vocabulary.owlMinQualifiedCardinality, n));
            SubjectNode classe = translateClassExpression(expression.getClasse());
            triples.add(getTriple(main, Vocabulary.owlOnDatarange, classe));
        } else {
            triples.add(getTriple(main, Vocabulary.owlMinCardinality, n));
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
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlRestriction));
        SubjectNode prop = translateObjectPropertyExpression(expression.getObjectProperty());
        triples.add(getTriple(main, Vocabulary.owlOnProperty, prop));
        SubjectNode classe = translateClassExpression(expression.getClasse());
        triples.add(getTriple(main, Vocabulary.owlSomeValuesFrom, classe));
        return main;
    }

    /**
     * Translate the specified object property expression
     *
     * @param expression An object property expression
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime object property is not named
     */
    protected SubjectNode translateObjectPropertyExpression(ObjectPropertyExpression expression) throws TranslationException {
        if (XOWLUtils.isDynamicExpression(expression)) {
            if (evaluator != null)
                return translateObjectPropertyRuntime(evaluator.evalObjectProperty(expression));
            return getDynamicNode(expression, ObjectProperty.class);
        }
        if (XOWLUtils.isQueryVar(expression)) {
            if (evaluator != null && evaluator.can((QueryVariable) expression))
                return translateObjectPropertyRuntime(evaluator.evalObjectProperty(expression));
            else
                return context.getVariableNode((QueryVariable) expression, ObjectProperty.class);
        }
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
        return graph.getNodeIRI(expression.getHasValue());
    }

    /**
     * Translate the specified runtime object property
     *
     * @param expression A runtime object property
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime object property is not named
     */
    protected SubjectNode translateObjectPropertyRuntime(org.xowl.lang.runtime.ObjectProperty expression) throws TranslationException {
        if (expression.getInterpretationOf() == null)
            throw new TranslationException("Cannot translate anonymous entities");
        return graph.getNodeIRI(expression.getInterpretationOf().getHasIRI().getHasValue());
    }

    /**
     * Translate the specified object property expression
     *
     * @param expression An object property expression
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime object property is not named
     */
    protected SubjectNode translateOjectPropertyInverseOf(ObjectInverseOf expression) throws TranslationException {
        BlankNode main = graph.getBlankNode();
        SubjectNode inv = translateObjectPropertyExpression(expression.getInverse());
        triples.add(getTriple(main, Vocabulary.owlInverseOf, inv));
        return main;
    }

    /**
     * Translate the specified data property expression
     *
     * @param expression An data property expression
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime data property is not named
     */
    protected SubjectNode translateDataPropertyExpression(DataPropertyExpression expression) throws TranslationException {
        if (XOWLUtils.isDynamicExpression(expression)) {
            if (evaluator != null)
                return translateDataPropertyRuntime(evaluator.evalDataProperty(expression));
            return getDynamicNode(expression, DataProperty.class);
        }
        if (XOWLUtils.isQueryVar(expression)) {
            if (evaluator != null && evaluator.can(expression))
                return translateDataPropertyRuntime(evaluator.evalDataProperty(expression));
            else
                return context.getVariableNode((org.xowl.lang.actions.QueryVariable) expression, org.xowl.lang.runtime.DataProperty.class);
        }
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
        return graph.getNodeIRI(expression.getHasValue());
    }

    /**
     * Translate the specified runtime data property
     *
     * @param expression A runtime data property
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime data property is not named
     */
    protected SubjectNode translateDataPropertyRuntime(DataProperty expression) throws TranslationException {
        if (expression.getInterpretationOf() == null)
            throw new TranslationException("Cannot translate anonymous entities");
        return graph.getNodeIRI(expression.getInterpretationOf().getHasIRI().getHasValue());
    }

    /**
     * Translate the specified datarange
     *
     * @param expression An datarange
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime datatype is not named
     */
    protected SubjectNode translateDatarange(Datarange expression) throws TranslationException {
        if (XOWLUtils.isDynamicExpression(expression)) {
            if (evaluator != null)
                return translateDatatype(evaluator.evalDataProperty(expression));
            return getDynamicNode(expression, Datatype.class);
        }
        if (XOWLUtils.isQueryVar(expression)) {
            if (evaluator != null && evaluator.can(expression))
                return translateDatatype(evaluator.evalDataProperty(expression));
            else
                return context.getVariableNode((QueryVariable) expression, org.xowl.lang.runtime.Datatype.class);
        }
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
        return graph.getNodeIRI(expression.getHasValue());
    }

    /**
     * Translate the specified datatype
     *
     * @param expression A datatype
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime datatype is not named
     */
    protected SubjectNode translateDatatype(Datatype expression) throws TranslationException {
        if (expression.getInterpretationOf() == null)
            throw new TranslationException("Cannot translate anonymous entities");
        return graph.getNodeIRI(expression.getInterpretationOf().getHasIRI().getHasValue());
    }

    /**
     * Translate the specified datarange
     *
     * @param expression An datarange
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime datatype is not named
     */
    protected SubjectNode translateDatarangeDataComplementOf(DataComplementOf expression) throws TranslationException {
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.rdfsDatatype));
        SubjectNode comp = translateDatarange(expression.getDatarange());
        triples.add(getTriple(main, Vocabulary.owlDatatypeComplementOf, comp));
        return main;
    }

    /**
     * Translate the specified datarange
     *
     * @param expression An datarange
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime datatype is not named
     */
    protected SubjectNode translateDatarangeDataIntersectionOf(DataIntersectionOf expression) throws TranslationException {
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.rdfsDatatype));
        List<Node> elements = new ArrayList<>();
        Iterator<Datarange> expressions = XOWLUtils.getAll(expression.getDatarangeSeq());
        while (expressions.hasNext())
            elements.add(translateDatarange(expressions.next()));
        SubjectNode seq = translateUnorderedSequence(elements);
        triples.add(getTriple(main, Vocabulary.owlIntersectionOf, seq));
        return main;
    }

    /**
     * Translate the specified datarange
     *
     * @param expression An datarange
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateDatarangeDataOneOf(DataOneOf expression) {
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.rdfsDatatype));
        List<Node> elements = new ArrayList<>();
        Iterator<LiteralExpression> expressions = XOWLUtils.getAll(expression.getLiteralSeq());
        while (expressions.hasNext())
            elements.add(translateLiteralExpression(expressions.next()));
        SubjectNode seq = translateUnorderedSequence(elements);
        triples.add(getTriple(main, Vocabulary.owlOneOf, seq));
        return main;
    }

    /**
     * Translate the specified datarange
     *
     * @param expression An datarange
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime datatype is not named
     */
    protected SubjectNode translateDatarangeDatatypeRestriction(DatatypeRestriction expression) throws TranslationException {
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.rdfsDatatype));
        SubjectNode base = translateDatarange(expression.getDatarange());
        triples.add(getTriple(main, Vocabulary.owlOnDatatype, base));
        List<Node> elements = new ArrayList<>();
        for (FacetRestriction elem : expression.getAllFacetRestrictions())
            elements.add(translateDatarangeFacetRestriction(elem));
        SubjectNode seq = translateUnorderedSequence(elements);
        triples.add(getTriple(main, Vocabulary.owlWithRestrictions, seq));
        return main;
    }

    /**
     * Translate the specified facet restriction
     *
     * @param restriction An facet restriction
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateDatarangeFacetRestriction(FacetRestriction restriction) {
        BlankNode main = graph.getBlankNode();
        Node lit = translateLiteralExpression(restriction.getConstrainingValue());
        triples.add(getTriple(main, restriction.getConstrainingFacet().getHasValue(), lit));
        return main;
    }

    /**
     * Translate the specified datarange
     *
     * @param expression An datarange
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime datatype is not named
     */
    protected SubjectNode translateDatarangeDataUnionOf(DataUnionOf expression) throws TranslationException {
        BlankNode main = graph.getBlankNode();
        triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.rdfsDatatype));
        List<Node> elements = new ArrayList<>();
        Iterator<Datarange> expressions = XOWLUtils.getAll(expression.getDatarangeSeq());
        while (expressions.hasNext())
            elements.add(translateDatarange(expressions.next()));
        SubjectNode seq = translateUnorderedSequence(elements);
        triples.add(getTriple(main, Vocabulary.owlUnionOf, seq));
        return main;
    }


    /**
     * Translate the specified individual expression
     *
     * @param expression An individual expression
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime individual is not named
     */
    protected SubjectNode translateIndividualExpression(IndividualExpression expression) throws TranslationException {
        if (XOWLUtils.isDynamicExpression(expression)) {
            if (evaluator != null) {
                Individual ind = evaluator.evalIndividual(expression);
                if (ind instanceof AnonymousIndividual)
                    return translateAnonymousIndividual((AnonymousIndividual) ind);
                if (ind instanceof org.xowl.lang.runtime.NamedIndividual)
                    return translateNamedIndividual((NamedIndividual) ind);
            } else
                return getDynamicNode(expression, Individual.class);
        }
        if (XOWLUtils.isQueryVar(expression)) {
            if (evaluator != null && evaluator.can(expression)) {
                Individual ind = evaluator.evalIndividual(expression);
                if (ind instanceof AnonymousIndividual)
                    return translateAnonymousIndividual((AnonymousIndividual) ind);
                if (ind instanceof NamedIndividual)
                    return translateNamedIndividual((NamedIndividual) ind);
            } else
                return context.getVariableNode((org.xowl.lang.actions.QueryVariable) expression, org.xowl.lang.runtime.Individual.class);
        }
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
        return graph.getNodeIRI(expression.getHasValue());
    }

    /**
     * Translate the specified named individual
     *
     * @param expression A named individual
     * @return The RDF node representing the expression
     * @throws org.xowl.store.owl.TranslationException when a runtime individual is not named
     */
    protected SubjectNode translateNamedIndividual(NamedIndividual expression) throws TranslationException {
        if (expression.getInterpretationOf() == null)
            throw new TranslationException("Cannot translate anonymous entities");
        return graph.getNodeIRI(expression.getInterpretationOf().getHasIRI().getHasValue());
    }

    /**
     * Translate the specified anonymous individual
     *
     * @param expression An anonymous individual
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateAnonymousIndividual(AnonymousIndividual expression) {
        return graph.getAnonymousNode(expression);
    }

    /**
     * Translate the specified literal expression
     *
     * @param expression A literal expression
     * @return The RDF node representing the expression
     */
    protected Node translateLiteralExpression(LiteralExpression expression) {
        if (XOWLUtils.isDynamicExpression(expression)) {
            if (evaluator != null)
                return translateLiteralRuntime(evaluator.evalLiteral(expression));
            return getDynamicNode(expression, Literal.class);
        }
        if (XOWLUtils.isQueryVar(expression)) {
            if (evaluator != null && evaluator.can(expression))
                return translateLiteralRuntime(evaluator.evalLiteral(expression));
            else
                return context.getVariableNode((org.xowl.lang.actions.QueryVariable) expression, org.xowl.lang.runtime.Literal.class);
        }
        if (expression instanceof Literal)
            return translateLiteral((Literal) expression);
        return null;
    }

    /**
     * Translate the specified runtime literal
     *
     * @param expression A runtime literal
     * @return The RDF node representing the expression
     */
    protected LiteralNode translateLiteralRuntime(org.xowl.lang.runtime.Literal expression) {
        return graph.getLiteralNode(expression.getLexicalValue(), expression.getMemberOf().getInterpretationOf().getHasIRI().getHasValue(), expression.getLangTag());
    }

    /**
     * Translate the specified literal expression
     *
     * @param expression A literal expression
     * @return The RDF node representing the expression
     */
    protected LiteralNode translateLiteral(Literal expression) {
        return graph.getLiteralNode(expression);
    }

    /**
     * Translate the specified annotation property
     *
     * @param expression An annotation property
     * @return The RDF node representing the expression
     */
    protected IRINode translateAnnotationProperty(IRI expression) {
        return graph.getNodeIRI(expression.getHasValue());
    }

    /**
     * Translate the specified annotation subject
     *
     * @param expression An annotation subject
     * @return The RDF node representing the expression
     */
    protected SubjectNode translateAnnotationSubject(AnnotationSubject expression) {
        if (expression instanceof IRI)
            return graph.getNodeIRI(((IRI) expression).getHasValue());
        else
            return graph.getAnonymousNode((AnonymousIndividual) expression);
    }

    /**
     * Translate the specified annotation value
     *
     * @param expression An annotation value
     * @return The RDF node representing the expression
     */
    protected Node translateAnnotationValue(AnnotationValue expression) {
        if (expression instanceof IRI)
            return graph.getNodeIRI(((IRI) expression).getHasValue());
        else if (expression instanceof AnonymousIndividual)
            return graph.getAnonymousNode((AnonymousIndividual) expression);
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
        triples.add(new Triple(ontology, annotated, prop, value));
        if (!annotation.getAllAnnotations().isEmpty()) {
            SubjectNode main = graph.getBlankNode();
            triples.add(getTriple(main, Vocabulary.rdfType, Vocabulary.owlAnnotation));
            triples.add(getTriple(main, Vocabulary.owlAnnotatedSource, annotated));
            triples.add(getTriple(main, Vocabulary.owlAnnotatedProperty, prop));
            triples.add(getTriple(main, Vocabulary.owlAnnotatedTarget, value));
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
    protected void translateAxiomAnnotations(Axiom axiom, Triple main) {
        if (!axiom.getAllAnnotations().isEmpty()) {
            SubjectNode x = graph.getBlankNode();
            triples.add(getTriple(x, Vocabulary.rdfType, Vocabulary.owlAxiom));
            triples.add(getTriple(x, Vocabulary.owlAnnotatedSource, main.getSubject()));
            triples.add(getTriple(x, Vocabulary.owlAnnotatedProperty, main.getProperty()));
            triples.add(getTriple(x, Vocabulary.owlAnnotatedTarget, main.getObject()));
            for (Annotation child : axiom.getAllAnnotations())
                translateAnnotation(x, child);
        }
    }
}
