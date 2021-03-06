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

import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.lang.owl2.*;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.rete.RETENetwork;
import org.xowl.infra.store.rete.RETERule;
import org.xowl.infra.store.rete.Token;
import org.xowl.infra.store.rete.TokenActivable;
import org.xowl.infra.store.storage.BaseStore;
import org.xowl.infra.store.storage.StoreFactory;
import org.xowl.infra.store.storage.UnsupportedNodeType;

import java.util.*;

/**
 * Represents a parser of RDF data producing OWL2 axioms
 *
 * @author Laurent Wouters
 */
public class RDFParser {
    /**
     * The xOWL store to use
     */
    private BaseStore store;
    /**
     * The graph node to use for building pattern quads
     */
    private GraphNode graphNode;
    /**
     * The rules for the RETE network
     */
    private List<RETERule> rules;
    /**
     * The current rule triggers
     */
    private List<RDFParserTrigger> triggers;
    /**
     * The registerd datarange expression solvers
     */
    private Map<Node, RDFParserExpressionSolver> expDatarange;
    /**
     * The registered class expression solvers
     */
    private Map<Node, RDFParserExpressionSolver> expClasses;
    /**
     * The registered object property expression solvers
     */
    private Map<Node, RDFParserExpressionSolver> expObjProperties;
    /**
     * The resulting axioms
     */
    private List<Axiom> axioms;

    /**
     * Translates the specified quads
     *
     * @param quads The quads
     * @return The equivalent axioms
     * @throws UnsupportedNodeType When a node cannot be translated
     */
    public Collection<Axiom> translate(Collection<Quad> quads) throws UnsupportedNodeType {
        store = StoreFactory.create().make();
        graphNode = new VariableNode("__graph__");
        store.insert(Changeset.fromAdded(quads));
        execute(quads);
        return axioms;
    }

    /**
     * Translates the specified quads from the given store
     *
     * @param store The parent store
     * @param quads The quads
     * @return The equivalent axioms
     */
    public Collection<Axiom> translate(BaseStore store, Collection<Quad> quads) {
        this.store = store;
        this.graphNode = new VariableNode("__graph__");
        execute(quads);
        return axioms;
    }

    /**
     * Translates the quads in the specified graph
     *
     * @param store The parent store
     * @param graph The graph
     * @return The equivalent axioms
     */
    public Collection<Axiom> translate(BaseStore store, GraphNode graph) {
        this.store = store;
        this.graphNode = graph;
        Collection<Quad> quads = new ArrayList<>();
        try {
            Iterator<Quad> iterator = store.getAll(graph);
            while (iterator.hasNext())
                quads.add(iterator.next());
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
        }
        execute(quads);
        return axioms;
    }

    /**
     * Executes the parsing of the specified quads
     *
     * @param quads The quads to parse
     */
    private void execute(Collection<Quad> quads) {
        RETENetwork network = new RETENetwork(store);
        rules = new ArrayList<>();
        triggers = new ArrayList<>();
        expDatarange = new HashMap<>();
        expClasses = new HashMap<>();
        expObjProperties = new HashMap<>();
        axioms = new ArrayList<>();

        buildRules();
        for (RETERule rule : rules)
            network.addRule(rule);
        network.injectPositives(quads);

        Collections.sort(triggers, new Comparator<RDFParserTrigger>() {
            @Override
            public int compare(RDFParserTrigger trigger1, RDFParserTrigger trigger2) {
                return trigger1.rule.priority - trigger2.rule.priority;
            }
        });
        for (RDFParserTrigger trigger : triggers)
            trigger.execute();
    }

    /**
     * Adds the specified parsing rule to this parser
     *
     * @param rule The rule to add
     */
    private void addRule(final RDFParserRule rule) {
        RETERule reteRule = new RETERule(new TokenActivable() {
            @Override
            public void activateToken(Token token) {
                triggers.add(new RDFParserTrigger(rule, token.getBindings()));
            }

            @Override
            public void deactivateToken(Token token) {
            }

            @Override
            public void activateTokens(Collection<Token> tokens) {
                for (Token token : tokens)
                    activateToken(token);
            }

            @Override
            public void deactivateTokens(Collection<Token> tokens) {
            }
        });
        Quad[] patterns = rule.getPatterns();
        for (int i = 0; i != patterns.length; i++)
            reteRule.getPositives().add(patterns[i]);
        rules.add(reteRule);
    }

    /**
     * Gets the quad pattern
     *
     * @param subject  A subject node
     * @param property A property node
     * @param object   An object node
     * @return The quad pattern
     */
    private Quad getPattern(SubjectNode subject, Property property, Node object) {
        return new Quad(graphNode, subject, property, object);
    }

    /**
     * Gets the quad pattern
     *
     * @param subject  A subject node
     * @param property A property IRI
     * @param object   An object node
     * @return The quad pattern
     */
    private Quad getPattern(SubjectNode subject, String property, Node object) {
        return new Quad(graphNode, subject, store.getIRINode(property), object);
    }

    /**
     * Gets the quad pattern
     *
     * @param subject  A subject node
     * @param property A property IRI
     * @param object   An object IRI
     * @return The quad pattern
     */
    private Quad getPattern(SubjectNode subject, String property, String object) {
        return new Quad(graphNode, subject, store.getIRINode(property), store.getIRINode(object));
    }

    /**
     * Gets the datarange expression for the specified node
     *
     * @param node A RDF node
     * @return The expression
     */
    private Datarange getExpressionDatarange(Node node) {
        if (node.getNodeType() == Node.TYPE_IRI)
            return (IRI) RDFUtils.getOWL(node);
        return (Datarange) expDatarange.get(node).getExpression();
    }

    /**
     * Gets the class expression for the specified node
     *
     * @param node A RDF node
     * @return The expression
     */
    private ClassExpression getExpressionClass(Node node) {
        if (node.getNodeType() == Node.TYPE_IRI)
            return (IRI) RDFUtils.getOWL(node);
        return (ClassExpression) expClasses.get(node).getExpression();
    }

    /**
     * Gets the object property expression for the specified node
     *
     * @param node A RDF node
     * @return The expression
     */
    private ObjectPropertyExpression getExpressionObjectProperty(Node node) {
        if (node.getNodeType() == Node.TYPE_IRI)
            return (IRI) RDFUtils.getOWL(node);
        return (ObjectPropertyExpression) expObjProperties.get(node).getExpression();
    }

    /**
     * Gets the annotation property expression for the specified node
     *
     * @param node A RDF node
     * @return The expression
     */
    private IRI getExpressionAnnotationProperty(Node node) {
        if (node.getNodeType() == Node.TYPE_IRI)
            return (IRI) RDFUtils.getOWL(node);
        return null;
    }

    /**
     * Gets the data property expression for the specified node
     *
     * @param node A RDF node
     * @return The expression
     */
    private DataPropertyExpression getExpressionDataProperty(Node node) {
        if (node.getNodeType() == Node.TYPE_IRI)
            return (IRI) RDFUtils.getOWL(node);
        return null;
    }

    /**
     * Gets the literal expression for the specified node
     *
     * @param node A RDF node
     * @return The expression
     */
    private LiteralExpression getExpressionLiteral(Node node) {
        if (node.getNodeType() == Node.TYPE_LITERAL)
            return (LiteralExpression) RDFUtils.getOWL(node);
        return null;
    }

    /**
     * Gets the individual expression for the specified node
     *
     * @param node A RDF node
     * @return The expression
     */
    private IndividualExpression getExpressionIndividual(Node node) {
        if (node.getNodeType() == Node.TYPE_IRI)
            return (IRI) RDFUtils.getOWL(node);
        else if (node.getNodeType() == Node.TYPE_ANONYMOUS)
            return ((AnonymousNode) node).getIndividual();
        return null;
    }

    /**
     * Gets the datarange sequence for the specified RDF nodes
     *
     * @param nodes The nodes
     * @return The sequence
     */
    private DatarangeSequence getSequenceDatarange(List<Node> nodes) {
        DatarangeSequence sequence = Owl2Factory.newDatarangeSequence();
        int i = 0;
        for (Node node : nodes) {
            DatarangeElement element = Owl2Factory.newDatarangeElement();
            element.setIndex(i);
            element.setDatarange(getExpressionDatarange(node));
            sequence.addDatarangeElements(element);
        }
        return sequence;
    }

    /**
     * Gets the class sequence for the specified RDF nodes
     *
     * @param nodes The nodes
     * @return The sequence
     */
    private ClassSequence getSequenceClass(List<Node> nodes) {
        ClassSequence sequence = Owl2Factory.newClassSequence();
        int i = 0;
        for (Node node : nodes) {
            ClassElement element = Owl2Factory.newClassElement();
            element.setIndex(i);
            element.setClasse(getExpressionClass(node));
            sequence.addClassElements(element);
        }
        return sequence;
    }

    /**
     * Gets the data property sequence for the specified RDF nodes
     *
     * @param nodes The nodes
     * @return The sequence
     */
    private DataPropertySequence getSequenceDataProperty(List<Node> nodes) {
        DataPropertySequence sequence = Owl2Factory.newDataPropertySequence();
        int i = 0;
        for (Node node : nodes) {
            DataPropertyElement element = Owl2Factory.newDataPropertyElement();
            element.setIndex(i);
            element.setDataProperty(getExpressionDataProperty(node));
            sequence.addDataPropertyElements(element);
        }
        return sequence;
    }

    /**
     * Gets the object property sequence for the specified RDF nodes
     *
     * @param nodes The nodes
     * @return The sequence
     */
    private ObjectPropertySequence getSequenceObjectProperty(List<Node> nodes) {
        ObjectPropertySequence sequence = Owl2Factory.newObjectPropertySequence();
        int i = 0;
        for (Node node : nodes) {
            ObjectPropertyElement element = Owl2Factory.newObjectPropertyElement();
            element.setIndex(i);
            element.setObjectProperty(getExpressionObjectProperty(node));
            sequence.addObjectPropertyElements(element);
        }
        return sequence;
    }

    /**
     * Gets the literal sequence for the specified RDF nodes
     *
     * @param nodes The nodes
     * @return The sequence
     */
    private LiteralSequence getSequenceLiteral(List<Node> nodes) {
        LiteralSequence sequence = Owl2Factory.newLiteralSequence();
        int i = 0;
        for (Node node : nodes) {
            LiteralElement element = Owl2Factory.newLiteralElement();
            element.setIndex(i);
            element.setLiteral(getExpressionLiteral(node));
            sequence.addLiteralElements(element);
        }
        return sequence;
    }

    /**
     * Gets the individual sequence for the specified RDF nodes
     *
     * @param nodes The nodes
     * @return The sequence
     */
    private IndividualSequence getSequenceIndividual(List<Node> nodes) {
        IndividualSequence sequence = Owl2Factory.newIndividualSequence();
        int i = 0;
        for (Node node : nodes) {
            IndividualElement element = Owl2Factory.newIndividualElement();
            element.setIndex(i);
            element.setIndividual(getExpressionIndividual(node));
            sequence.addIndividualElements(element);
        }
        return sequence;
    }

    /**
     * Gets the list of RDF nodes corresponding to the specified RDF sequence
     *
     * @param sequence The first node of a RDF sequence
     * @return The nodes
     */
    private List<Node> getListOrdered(Node sequence) {
        List<Node> elements = new ArrayList<>();
        while (true) {
            if (sequence.getNodeType() == Node.TYPE_IRI) {
                if (Vocabulary.rdfNil.equals(((IRINode) sequence).getIRIValue()))
                    return elements;
            }
            List<Node> values = getValues((SubjectNode) sequence, Vocabulary.rdfFirst);
            List<Node> rests = getValues((SubjectNode) sequence, Vocabulary.rdfRest);
            elements.add(values.get(0));
            sequence = rests.get(0);
        }
    }

    /**
     * Gets the list of RDF nodes corresponding to the specified RDF sequence
     *
     * @param sequence The first node of a RDF sequence
     * @return The nodes
     */
    private List<Node> getListUnordered(Node sequence) {
        return getListOrdered(sequence);
    }

    /**
     * Gets the values associated to a subject by a property
     *
     * @param subject  The subject
     * @param property The property
     * @return The values
     */
    private List<Node> getValues(SubjectNode subject, String property) {
        List<Node> results = new ArrayList<>();
        try {
            Iterator<Quad> iterator = store.getAll(subject, store.getIRINode(property), null);
            while (iterator.hasNext()) {
                results.add(iterator.next().getObject());
            }
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
        }
        return results;
    }

    /**
     * Gets the first triple in this store corresponding to the specified subject
     *
     * @param subject A subject node
     * @return The first triple
     */
    private Quad getTriple(SubjectNode subject) {
        try {
            Iterator<Quad> iterator = store.getAll(subject, null, null);
            if (!iterator.hasNext())
                return null;
            return iterator.next();
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
            return null;
        }
    }

    /**
     * Gets whether the specified node is typed with the specified type IRI, i.e. whether the triple (subject rdf:type type) exists
     *
     * @param subject A node
     * @param type    The IRI of a type
     * @return Whether the triple (subject rdf:type type) exists
     */
    private boolean isOfType(SubjectNode subject, String type) {
        List<Node> types = getValues(subject, Vocabulary.rdfType);
        for (Node value : types) {
            if (value.getNodeType() == Node.TYPE_IRI) {
                String iri = ((IRINode) value).getIRIValue();
                if (type.equals(iri))
                    return true;
            }
        }
        return false;
    }

    /**
     * Builds the parsing rules
     */
    private void buildRules() {
        // the rules parsing OWL expressions
        buildObjPropExp_InverseOf();
        buildDatarange_Intersection();
        buildDatarange_Union();
        buildDatarange_Complement();
        buildDatarange_OneOf();
        buildDatarange_Restriction();
        buildClassExp_Intersection();
        buildClassExp_Union();
        buildClassExp_ComplementOf();
        buildClassExp_OneOf();
        buildClassExp_ObjAllValuesFrom();
        buildClassExp_ObjSomeValuesFrom();
        buildClassExp_ObjHasValue();
        buildClassExp_ObjHasSelf();
        buildClassExp_ObjMinQualifiedCard();
        buildClassExp_ObjMaxQualifiedCard();
        buildClassExp_ObjExactQualifiedCard();
        buildClassExp_ObjMinCard();
        buildClassExp_ObjMaxCard();
        buildClassExp_ObjExactCard();
        buildClassExp_DataHasValue();
        buildClassExp_DataAllValuesFrom();
        buildClassExp_DataSomeValuesFrom();
        buildClassExp_DataAllValuesFrom2();
        buildClassExp_DataSomeValuesFrom2();
        buildClassExp_DataMinQualifiedCard();
        buildClassExp_DataMaxQualifiedCard();
        buildClassExp_DataExactQualifiedCard();
        buildClassExp_DataMinCard();
        buildClassExp_DataMaxCard();
        buildClassExp_DataExactCard();
        // the rules parsing axioms
        buildAxiomSubClassOf();
        buildAxiomEquivalentClass();
        buildAxiomDisjointClasses();
        buildAxiomAllDisjointClasses();
        buildAxiomDisjointUnionOf();
        buildAxiomSubObjPropertyOf();
        buildAxiomSubObjPropertyOf_Chain();
        buildAxiomEquivalentObjProp();
        buildAxiomDisjointObjProp();
        buildAxiomAllDisjointObjProps();
        buildAxiomObjPropDomain();
        buildAxiomObjPropRange();
        buildAxiomInverseOf();
        buildAxiomFunctionalObjectProperty();
        buildAxiomInverseFunctionalProperty();
        buildAxiomReflexiveProperty();
        buildAxiomIrreflexiveProperty();
        buildAxiomSymmetricProperty();
        buildAxiomAsymmetricProperty();
        buildAxiomTransitiveProperty();
        buildAxiomSubDataPropertyOf();
        buildAxiomEquivalentDataProp();
        buildAxiomDisjointDataProp();
        buildAxiomAllDisjointDataProps();
        buildAxiomDataPropDomain();
        buildAxiomDataPropRange();
        buildAxiomFunctionalDataProperty();
        buildAxiomEquivalentDatatype();
        buildAxiomHasKey();
        buildAxiomSameAs();
        buildAxiomDifferentFrom();
        buildAxiomAllDifferent();
        buildAxiomClassAssertion();
        buildAxiomObjectPropertyAssertion();
        buildAxiomDataPropertyAssertion();
        buildAxiomNegativeObjectPropertyAssertion();
        buildAxiomNegativeDataPropertyAssertion();
        buildAxiomSubAnnotPropertyOf();
        buildAxiomAnnotPropDomain();
        buildAxiomAnnotPropRange();
    }

    private void buildObjPropExp_InverseOf() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.owlInverseOf, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                expObjProperties.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectInverseOf value = Owl2Factory.newObjectInverseOf();
                        value.setInverse(getExpressionObjectProperty(y));
                        return value;
                    }
                });
            }
        });
    }

    private void buildDatarange_Intersection() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.rdfsDatatype),
                        getPattern(getVariable("x"), Vocabulary.owlIntersectionOf, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                final List<Node> intersected = getListUnordered(y);
                expDatarange.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataIntersectionOf value = Owl2Factory.newDataIntersectionOf();
                        value.setDatarangeSeq(getSequenceDatarange(intersected));
                        return value;
                    }
                });
            }
        });
    }

    private void buildDatarange_Union() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.rdfsDatatype),
                        getPattern(getVariable("x"), Vocabulary.owlUnionOf, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                final List<Node> unioned = getListUnordered(y);
                expDatarange.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataUnionOf value = Owl2Factory.newDataUnionOf();
                        value.setDatarangeSeq(getSequenceDatarange(unioned));
                        return value;
                    }
                });
            }
        });
    }

    private void buildDatarange_Complement() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.rdfsDatatype),
                        getPattern(getVariable("x"), Vocabulary.owlDatatypeComplementOf, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                expDatarange.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataComplementOf value = Owl2Factory.newDataComplementOf();
                        value.setDatarange(getExpressionDatarange(y));
                        return value;
                    }
                });
            }
        });
    }

    private void buildDatarange_OneOf() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.rdfsDatatype),
                        getPattern(getVariable("x"), Vocabulary.owlOneOf, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                final List<Node> individuals = getListUnordered(y);
                expDatarange.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataOneOf value = Owl2Factory.newDataOneOf();
                        value.setLiteralSeq(getSequenceLiteral(individuals));
                        return value;
                    }
                });
            }
        });
    }

    private void buildDatarange_Restriction() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.rdfsDatatype),
                        getPattern(getVariable("x"), Vocabulary.owlOnDatatype, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlWithRestrictions, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                Node z = getValue(bindings, "z");
                final List<Node> restrictions = getListUnordered(z);
                expDatarange.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DatatypeRestriction value = Owl2Factory.newDatatypeRestriction();
                        value.setDatarange(getExpressionDatarange(y));
                        for (Node restrictNode : restrictions) {
                            FacetRestriction facet = Owl2Factory.newFacetRestriction();
                            Quad triple = getTriple((SubjectNode) restrictNode);
                            facet.setConstrainingFacet((IRI) RDFUtils.getOWL(triple.getProperty()));
                            facet.setConstrainingValue((Literal) getExpressionLiteral(triple.getObject()));
                            value.addFacetRestrictions(facet);
                        }
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_Intersection() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlClass),
                        getPattern(getVariable("x"), Vocabulary.owlIntersectionOf, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                final List<Node> intersected = getListUnordered(y);
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectIntersectionOf value = Owl2Factory.newObjectIntersectionOf();
                        value.setClassSeq(getSequenceClass(intersected));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_Union() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlClass),
                        getPattern(getVariable("x"), Vocabulary.owlUnionOf, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                final List<Node> unified = getListUnordered(y);
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectUnionOf value = Owl2Factory.newObjectUnionOf();
                        value.setClassSeq(getSequenceClass(unified));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_ComplementOf() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlClass),
                        getPattern(getVariable("x"), Vocabulary.owlComplementOf, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectComplementOf value = Owl2Factory.newObjectComplementOf();
                        value.setClasse(getExpressionClass(y));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_OneOf() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlClass),
                        getPattern(getVariable("x"), Vocabulary.owlOneOf, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                final List<Node> unified = getListUnordered(y);
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectOneOf value = Owl2Factory.newObjectOneOf();
                        value.setIndividualSeq(getSequenceIndividual(unified));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_ObjAllValuesFrom() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlAllValuesFrom, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                if (!isOfType((SubjectNode) y, Vocabulary.owlObjectProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectAllValuesFrom value = Owl2Factory.newObjectAllValuesFrom();
                        value.setObjectProperty(getExpressionObjectProperty(y));
                        value.setClasse(getExpressionClass(z));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_ObjSomeValuesFrom() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlSomeValuesFrom, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                if (!isOfType((SubjectNode) y, Vocabulary.owlObjectProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectSomeValuesFrom value = Owl2Factory.newObjectSomeValuesFrom();
                        value.setObjectProperty(getExpressionObjectProperty(y));
                        value.setClasse(getExpressionClass(z));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_ObjHasValue() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlHasValue, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                if (!isOfType((SubjectNode) y, Vocabulary.owlObjectProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectHasValue value = Owl2Factory.newObjectHasValue();
                        value.setObjectProperty(getExpressionObjectProperty(y));
                        value.setIndividual(getExpressionIndividual(z));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_ObjHasSelf() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlHasSelf, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectHasSelf value = Owl2Factory.newObjectHasSelf();
                        value.setObjectProperty(getExpressionObjectProperty(y));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_ObjMinQualifiedCard() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlMinQualifiedCardinality, getVariable("n")),
                        getPattern(getVariable("x"), Vocabulary.owlOnClass, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                final Node n = getValue(bindings, "n");
                if (!isOfType((SubjectNode) y, Vocabulary.owlObjectProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectMinCardinality value = Owl2Factory.newObjectMinCardinality();
                        value.setObjectProperty(getExpressionObjectProperty(y));
                        value.setClasse(getExpressionClass(z));
                        value.setCardinality(getExpressionLiteral(n));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_ObjMaxQualifiedCard() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlMaxQualifiedCardinality, getVariable("n")),
                        getPattern(getVariable("x"), Vocabulary.owlOnClass, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                final Node n = getValue(bindings, "n");
                if (!isOfType((SubjectNode) y, Vocabulary.owlObjectProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectMaxCardinality value = Owl2Factory.newObjectMaxCardinality();
                        value.setObjectProperty(getExpressionObjectProperty(y));
                        value.setClasse(getExpressionClass(z));
                        value.setCardinality(getExpressionLiteral(n));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_ObjExactQualifiedCard() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlQualifiedCardinality, getVariable("n")),
                        getPattern(getVariable("x"), Vocabulary.owlOnClass, getVariable("z")),
                        getPattern(getVariable("y"), Vocabulary.rdfType, Vocabulary.owlObjectProperty)
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                final Node n = getValue(bindings, "n");
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectExactCardinality value = Owl2Factory.newObjectExactCardinality();
                        value.setObjectProperty(getExpressionObjectProperty(y));
                        value.setClasse(getExpressionClass(z));
                        value.setCardinality(getExpressionLiteral(n));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_ObjMinCard() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlMinCardinality, getVariable("n"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node n = getValue(bindings, "n");
                if (!isOfType((SubjectNode) y, Vocabulary.owlObjectProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectMinCardinality value = Owl2Factory.newObjectMinCardinality();
                        value.setObjectProperty(getExpressionObjectProperty(y));
                        value.setCardinality(getExpressionLiteral(n));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_ObjMaxCard() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlMaxCardinality, getVariable("n"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node n = getValue(bindings, "n");
                if (!isOfType((SubjectNode) y, Vocabulary.owlObjectProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectMaxCardinality value = Owl2Factory.newObjectMaxCardinality();
                        value.setObjectProperty(getExpressionObjectProperty(y));
                        value.setCardinality(getExpressionLiteral(n));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_ObjExactCard() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlCardinality, getVariable("n"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node n = getValue(bindings, "n");
                if (!isOfType((SubjectNode) y, Vocabulary.owlObjectProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        ObjectExactCardinality value = Owl2Factory.newObjectExactCardinality();
                        value.setObjectProperty(getExpressionObjectProperty(y));
                        value.setCardinality(getExpressionLiteral(n));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_DataHasValue() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlHasValue, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                if (!isOfType((SubjectNode) y, Vocabulary.owlDataProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataHasValue value = Owl2Factory.newDataHasValue();
                        value.setDataProperty(getExpressionDataProperty(y));
                        value.setLiteral(getExpressionLiteral(z));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_DataAllValuesFrom() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlAllValuesFrom, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                if (!isOfType((SubjectNode) y, Vocabulary.owlDataProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataAllValuesFrom value = Owl2Factory.newDataAllValuesFrom();
                        List<Node> list = new ArrayList<>();
                        list.add(y);
                        value.setDataPropertySeq(getSequenceDataProperty(list));
                        value.setDatarange(getExpressionDatarange(z));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_DataSomeValuesFrom() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlSomeValuesFrom, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                if (!isOfType((SubjectNode) y, Vocabulary.owlDataProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataSomeValuesFrom value = Owl2Factory.newDataSomeValuesFrom();
                        List<Node> list = new ArrayList<>();
                        list.add(y);
                        value.setDataPropertySeq(getSequenceDataProperty(list));
                        value.setDatarange(getExpressionDatarange(z));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_DataAllValuesFrom2() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperties, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlAllValuesFrom, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                final List<Node> list = getListUnordered(y);
                if (!isOfType((SubjectNode) y, Vocabulary.owlDataProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataAllValuesFrom value = Owl2Factory.newDataAllValuesFrom();
                        value.setDataPropertySeq(getSequenceDataProperty(list));
                        value.setDatarange(getExpressionDatarange(z));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_DataSomeValuesFrom2() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperties, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlSomeValuesFrom, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                final List<Node> list = getListUnordered(y);
                if (!isOfType((SubjectNode) y, Vocabulary.owlDataProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataSomeValuesFrom value = Owl2Factory.newDataSomeValuesFrom();
                        value.setDataPropertySeq(getSequenceDataProperty(list));
                        value.setDatarange(getExpressionDatarange(z));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_DataMinQualifiedCard() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlMinQualifiedCardinality, getVariable("n")),
                        getPattern(getVariable("x"), Vocabulary.owlOnClass, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                final Node n = getValue(bindings, "n");
                if (!isOfType((SubjectNode) y, Vocabulary.owlDataProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataMinCardinality value = Owl2Factory.newDataMinCardinality();
                        value.setDataProperty(getExpressionDataProperty(y));
                        value.setDatarange(getExpressionDatarange(z));
                        value.setCardinality(getExpressionLiteral(n));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_DataMaxQualifiedCard() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlMaxQualifiedCardinality, getVariable("n")),
                        getPattern(getVariable("x"), Vocabulary.owlOnClass, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                final Node n = getValue(bindings, "n");
                if (!isOfType((SubjectNode) y, Vocabulary.owlDataProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataMaxCardinality value = Owl2Factory.newDataMaxCardinality();
                        value.setDataProperty(getExpressionDataProperty(y));
                        value.setDatarange(getExpressionDatarange(z));
                        value.setCardinality(getExpressionLiteral(n));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_DataExactQualifiedCard() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlQualifiedCardinality, getVariable("n")),
                        getPattern(getVariable("x"), Vocabulary.owlOnClass, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node z = getValue(bindings, "z");
                final Node n = getValue(bindings, "n");
                if (!isOfType((SubjectNode) y, Vocabulary.owlDataProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataExactCardinality value = Owl2Factory.newDataExactCardinality();
                        value.setDataProperty(getExpressionDataProperty(y));
                        value.setDatarange(getExpressionDatarange(z));
                        value.setCardinality(getExpressionLiteral(n));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_DataMinCard() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlMinCardinality, getVariable("n"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node n = getValue(bindings, "n");
                if (!isOfType((SubjectNode) y, Vocabulary.owlDataProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataMinCardinality value = Owl2Factory.newDataMinCardinality();
                        value.setDataProperty(getExpressionDataProperty(y));
                        value.setCardinality(getExpressionLiteral(n));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_DataMaxCard() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlMaxCardinality, getVariable("n"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node n = getValue(bindings, "n");
                if (!isOfType((SubjectNode) y, Vocabulary.owlDataProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataMaxCardinality value = Owl2Factory.newDataMaxCardinality();
                        value.setDataProperty(getExpressionDataProperty(y));
                        value.setCardinality(getExpressionLiteral(n));
                        return value;
                    }
                });
            }
        });
    }

    private void buildClassExp_DataExactCard() {
        addRule(new RDFParserRule(1) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlRestriction),
                        getPattern(getVariable("x"), Vocabulary.owlOnProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlCardinality, getVariable("n"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                final Node y = getValue(bindings, "y");
                final Node n = getValue(bindings, "n");
                if (!isOfType((SubjectNode) y, Vocabulary.owlDataProperty))
                    return;
                expClasses.put(x, new RDFParserExpressionSolver() {
                    @Override
                    public Expression getExpression() {
                        DataExactCardinality value = Owl2Factory.newDataExactCardinality();
                        value.setDataProperty(getExpressionDataProperty(y));
                        value.setCardinality(getExpressionLiteral(n));
                        return value;
                    }
                });
            }
        });
    }

    private void buildAxiomSubClassOf() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("sub"), Vocabulary.rdfsSubClassOf, getVariable("super"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node sup = getValue(bindings, "super");
                Node sub = getValue(bindings, "sub");
                SubClassOf axiom = Owl2Factory.newSubClassOf();
                axiom.setClasse(getExpressionClass(sub));
                axiom.setSuperClass(getExpressionClass(sup));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomEquivalentClass() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("c1"), Vocabulary.owlEquivalentClass, getVariable("c2"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node c1 = getValue(bindings, "c1");
                Node c2 = getValue(bindings, "c2");
                if (!isOfType((SubjectNode) c1, Vocabulary.owlClass))
                    return;
                if (!isOfType((SubjectNode) c2, Vocabulary.owlClass))
                    return;
                EquivalentClasses axiom = Owl2Factory.newEquivalentClasses();
                List<Node> list = new ArrayList<>();
                list.add(c1);
                list.add(c2);
                axiom.setClassSeq(getSequenceClass(list));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomDisjointClasses() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("c1"), Vocabulary.owlDisjointWith, getVariable("c2"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node c1 = getValue(bindings, "c1");
                Node c2 = getValue(bindings, "c2");
                DisjointClasses axiom = Owl2Factory.newDisjointClasses();
                List<Node> list = new ArrayList<>();
                list.add(c1);
                list.add(c2);
                axiom.setClassSeq(getSequenceClass(list));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomAllDisjointClasses() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlAllDisjointClasses),
                        getPattern(getVariable("x"), Vocabulary.owlMembers, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                List<Node> list = getListUnordered(getValue(bindings, "y"));
                DisjointClasses axiom = Owl2Factory.newDisjointClasses();
                axiom.setClassSeq(getSequenceClass(list));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomDisjointUnionOf() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.owlDisjointUnionOf, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                List<Node> list = getListUnordered(getValue(bindings, "y"));
                DisjointUnion axiom = Owl2Factory.newDisjointUnion();
                axiom.setClasse(getExpressionClass(x));
                axiom.setClassSeq(getSequenceClass(list));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomSubObjPropertyOf() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("sub"), Vocabulary.rdfsSubPropertyOf, getVariable("super"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node sup = getValue(bindings, "super");
                Node sub = getValue(bindings, "sub");
                if (!isOfType((SubjectNode) sub, Vocabulary.owlObjectProperty))
                    return;
                SubObjectPropertyOf axiom = Owl2Factory.newSubObjectPropertyOf();
                axiom.setObjectProperty(getExpressionObjectProperty(sub));
                axiom.setSuperObjectProperty(getExpressionObjectProperty(sup));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomSubObjPropertyOf_Chain() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.owlPropertyChainAxiom, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                List<Node> chain = getListOrdered(getValue(bindings, "y"));
                SubObjectPropertyOf axiom = Owl2Factory.newSubObjectPropertyOf();
                axiom.setObjectPropertyChain(getSequenceObjectProperty(chain));
                axiom.setSuperObjectProperty(getExpressionObjectProperty(x));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomEquivalentObjProp() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("c1"), Vocabulary.owlEquivalentProperty, getVariable("c2"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node c1 = getValue(bindings, "c1");
                Node c2 = getValue(bindings, "c2");
                if (!isOfType((SubjectNode) c1, Vocabulary.owlObjectProperty))
                    return;
                if (!isOfType((SubjectNode) c2, Vocabulary.owlObjectProperty))
                    return;
                EquivalentObjectProperties axiom = Owl2Factory.newEquivalentObjectProperties();
                List<Node> list = new ArrayList<>();
                list.add(c1);
                list.add(c2);
                axiom.setObjectPropertySeq(getSequenceObjectProperty(list));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomDisjointObjProp() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("c1"), Vocabulary.owlPropertyDisjointWith, getVariable("c2"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node c1 = getValue(bindings, "c1");
                Node c2 = getValue(bindings, "c2");
                if (!isOfType((SubjectNode) c1, Vocabulary.owlObjectProperty))
                    return;
                if (!isOfType((SubjectNode) c2, Vocabulary.owlObjectProperty))
                    return;
                DisjointObjectProperties axiom = Owl2Factory.newDisjointObjectProperties();
                List<Node> list = new ArrayList<>();
                list.add(c1);
                list.add(c2);
                axiom.setObjectPropertySeq(getSequenceObjectProperty(list));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomAllDisjointObjProps() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlAllDisjointProperties),
                        getPattern(getVariable("x"), Vocabulary.owlMembers, getVariable("y")),
                        getPattern(getVariable("y"), Vocabulary.rdfFirst, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                if (!isOfType((SubjectNode) getValue(bindings, "z"), Vocabulary.owlObjectProperty))
                    return;
                List<Node> list = getListUnordered(getValue(bindings, "y"));
                DisjointObjectProperties axiom = Owl2Factory.newDisjointObjectProperties();
                axiom.setObjectPropertySeq(getSequenceObjectProperty(list));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomObjPropDomain() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfsDomain, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                if (!isOfType((SubjectNode) y, Vocabulary.owlObjectProperty))
                    return;
                ObjectPropertyDomain axiom = Owl2Factory.newObjectPropertyDomain();
                axiom.setObjectProperty(getExpressionObjectProperty(x));
                axiom.setClasse(getExpressionClass(y));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomObjPropRange() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfsDomain, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                if (!isOfType((SubjectNode) y, Vocabulary.owlObjectProperty))
                    return;
                ObjectPropertyRange axiom = Owl2Factory.newObjectPropertyRange();
                axiom.setObjectProperty(getExpressionObjectProperty(x));
                axiom.setClasse(getExpressionClass(y));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomInverseOf() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.owlInverseOf, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                if (!isOfType((SubjectNode) x, Vocabulary.owlObjectProperty))
                    return;
                if (!isOfType((SubjectNode) y, Vocabulary.owlObjectProperty))
                    return;
                InverseObjectProperties axiom = Owl2Factory.newInverseObjectProperties();
                axiom.setObjectProperty(getExpressionObjectProperty(x));
                axiom.setInverse(getExpressionObjectProperty(y));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomFunctionalObjectProperty() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlFunctionalProperty)
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                if (!isOfType((SubjectNode) x, Vocabulary.owlObjectProperty))
                    return;
                FunctionalObjectProperty axiom = Owl2Factory.newFunctionalObjectProperty();
                axiom.setObjectProperty(getExpressionObjectProperty(x));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomInverseFunctionalProperty() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlInverseFunctionalProperty)
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                InverseFunctionalObjectProperty axiom = Owl2Factory.newInverseFunctionalObjectProperty();
                axiom.setObjectProperty(getExpressionObjectProperty(x));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomReflexiveProperty() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlReflexiveProperty)
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                ReflexiveObjectProperty axiom = Owl2Factory.newReflexiveObjectProperty();
                axiom.setObjectProperty(getExpressionObjectProperty(x));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomIrreflexiveProperty() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlIrreflexiveProperty)
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                IrreflexiveObjectProperty axiom = Owl2Factory.newIrreflexiveObjectProperty();
                axiom.setObjectProperty(getExpressionObjectProperty(x));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomSymmetricProperty() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlSymmetricProperty)
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                SymmetricObjectProperty axiom = Owl2Factory.newSymmetricObjectProperty();
                axiom.setObjectProperty(getExpressionObjectProperty(x));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomAsymmetricProperty() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlAsymmetricProperty)
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                AsymmetricObjectProperty axiom = Owl2Factory.newAsymmetricObjectProperty();
                axiom.setObjectProperty(getExpressionObjectProperty(x));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomTransitiveProperty() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlTransitiveProperty)
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                TransitiveObjectProperty axiom = Owl2Factory.newTransitiveObjectProperty();
                axiom.setObjectProperty(getExpressionObjectProperty(x));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomSubDataPropertyOf() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("sub"), Vocabulary.rdfsSubPropertyOf, getVariable("super"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node sup = getValue(bindings, "super");
                Node sub = getValue(bindings, "sub");
                if (!isOfType((SubjectNode) sub, Vocabulary.owlDataProperty))
                    return;
                SubDataPropertyOf axiom = Owl2Factory.newSubDataPropertyOf();
                axiom.setDataProperty(getExpressionDataProperty(sub));
                axiom.setSuperDataProperty(getExpressionDataProperty(sup));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomEquivalentDataProp() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("c1"), Vocabulary.owlEquivalentProperty, getVariable("c2"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node c1 = getValue(bindings, "c1");
                Node c2 = getValue(bindings, "c2");
                if (!isOfType((SubjectNode) c1, Vocabulary.owlDataProperty))
                    return;
                if (!isOfType((SubjectNode) c2, Vocabulary.owlDataProperty))
                    return;
                EquivalentDataProperties axiom = Owl2Factory.newEquivalentDataProperties();
                List<Node> list = new ArrayList<>();
                list.add(c1);
                list.add(c2);
                axiom.setDataPropertySeq(getSequenceDataProperty(list));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomDisjointDataProp() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("c1"), Vocabulary.owlPropertyDisjointWith, getVariable("c2"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node c1 = getValue(bindings, "c1");
                Node c2 = getValue(bindings, "c2");
                if (!isOfType((SubjectNode) c1, Vocabulary.owlDataProperty))
                    return;
                if (!isOfType((SubjectNode) c2, Vocabulary.owlDataProperty))
                    return;
                DisjointDataProperties axiom = Owl2Factory.newDisjointDataProperties();
                List<Node> list = new ArrayList<>();
                list.add(c1);
                list.add(c2);
                axiom.setDataPropertySeq(getSequenceDataProperty(list));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomAllDisjointDataProps() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlAllDisjointProperties),
                        getPattern(getVariable("x"), Vocabulary.owlMembers, getVariable("y")),
                        getPattern(getVariable("y"), Vocabulary.rdfFirst, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                if (!isOfType((SubjectNode) getValue(bindings, "z"), Vocabulary.owlDataProperty))
                    return;
                List<Node> list = getListUnordered(getValue(bindings, "y"));
                DisjointDataProperties axiom = Owl2Factory.newDisjointDataProperties();
                axiom.setDataPropertySeq(getSequenceDataProperty(list));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomDataPropDomain() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfsDomain, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                if (!isOfType((SubjectNode) x, Vocabulary.owlDataProperty))
                    return;
                DataPropertyDomain axiom = Owl2Factory.newDataPropertyDomain();
                axiom.setDataProperty(getExpressionDataProperty(x));
                axiom.setClasse(getExpressionClass(y));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomDataPropRange() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfsDomain, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                if (!isOfType((SubjectNode) x, Vocabulary.owlDataProperty))
                    return;
                DataPropertyRange axiom = Owl2Factory.newDataPropertyRange();
                axiom.setDataProperty(getExpressionDataProperty(x));
                axiom.setDatarange(getExpressionDatarange(y));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomFunctionalDataProperty() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlFunctionalProperty)
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                if (!isOfType((SubjectNode) x, Vocabulary.owlDataProperty))
                    return;
                FunctionalDataProperty axiom = Owl2Factory.newFunctionalDataProperty();
                axiom.setDataProperty(getExpressionDataProperty(x));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomEquivalentDatatype() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("c1"), Vocabulary.owlEquivalentClass, getVariable("c2"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node c1 = getValue(bindings, "c1");
                Node c2 = getValue(bindings, "c2");
                if (!isOfType((SubjectNode) c1, Vocabulary.rdfsDatatype))
                    return;
                if (!isOfType((SubjectNode) c2, Vocabulary.rdfsDatatype))
                    return;
                DatatypeDefinition axiom = Owl2Factory.newDatatypeDefinition();
                axiom.setDatatype(getExpressionDatarange(c1));
                axiom.setDatarange(getExpressionDatarange(c2));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomHasKey() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.owlHasKey, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                List<Node> list = getListUnordered(getValue(bindings, "y"));
                List<Node> objProps = new ArrayList<>();
                List<Node> dataProps = new ArrayList<>();
                for (Node elem : list) {
                    if (isOfType((SubjectNode) elem, Vocabulary.owlObjectProperty))
                        objProps.add(elem);
                    else
                        dataProps.add(elem);
                }
                HasKey axiom = Owl2Factory.newHasKey();
                axiom.setClasse(getExpressionClass(x));
                axiom.setObjectPropertySeq(getSequenceObjectProperty(objProps));
                axiom.setDataPropertySeq(getSequenceDataProperty(dataProps));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomSameAs() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.owlSameAs, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                SameIndividual axiom = Owl2Factory.newSameIndividual();
                List<Node> list = new ArrayList<>();
                list.add(x);
                list.add(y);
                axiom.setIndividualSeq(getSequenceIndividual(list));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomDifferentFrom() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.owlDifferentFrom, getVariable("y")),
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                DifferentIndividuals axiom = Owl2Factory.newDifferentIndividuals();
                List<Node> list = new ArrayList<>();
                list.add(x);
                list.add(y);
                axiom.setIndividualSeq(getSequenceIndividual(list));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomAllDifferent() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlAllDifferent),
                        getPattern(getVariable("x"), Vocabulary.owlMembers, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                List<Node> list = getListUnordered(getValue(bindings, "y"));
                DifferentIndividuals axiom = Owl2Factory.newDifferentIndividuals();
                axiom.setIndividualSeq(getSequenceIndividual(list));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomClassAssertion() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                if (!isOfType((SubjectNode) y, Vocabulary.owlClass))
                    return;
                ClassAssertion axiom = Owl2Factory.newClassAssertion();
                axiom.setIndividual(getExpressionIndividual(x));
                axiom.setClasse(getExpressionClass(y));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomObjectPropertyAssertion() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), getVariable("y"), getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                Node z = getValue(bindings, "z");
                if (!isOfType((SubjectNode) y, Vocabulary.owlObjectProperty))
                    return;
                ObjectPropertyAssertion axiom = Owl2Factory.newObjectPropertyAssertion();
                axiom.setIndividual(getExpressionIndividual(x));
                axiom.setObjectProperty(getExpressionObjectProperty(y));
                axiom.setValueIndividual(getExpressionIndividual(z));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomDataPropertyAssertion() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), getVariable("y"), getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                Node z = getValue(bindings, "z");
                if (!isOfType((SubjectNode) y, Vocabulary.owlDataProperty))
                    return;
                DataPropertyAssertion axiom = Owl2Factory.newDataPropertyAssertion();
                axiom.setIndividual(getExpressionIndividual(x));
                axiom.setDataProperty(getExpressionDataProperty(y));
                axiom.setValueLiteral(getExpressionLiteral(z));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomNegativeObjectPropertyAssertion() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlNegativePropertyAssertion),
                        getPattern(getVariable("x"), Vocabulary.owlSourceIndividual, getVariable("w")),
                        getPattern(getVariable("x"), Vocabulary.owlAssertionProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlTargetIndividual, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node w = getValue(bindings, "w");
                Node y = getValue(bindings, "y");
                Node z = getValue(bindings, "z");
                ObjectPropertyAssertion axiom = Owl2Factory.newObjectPropertyAssertion();
                axiom.setIndividual(getExpressionIndividual(w));
                axiom.setObjectProperty(getExpressionObjectProperty(y));
                axiom.setValueIndividual(getExpressionIndividual(z));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomNegativeDataPropertyAssertion() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfType, Vocabulary.owlNegativePropertyAssertion),
                        getPattern(getVariable("x"), Vocabulary.owlSourceIndividual, getVariable("w")),
                        getPattern(getVariable("x"), Vocabulary.owlAssertionProperty, getVariable("y")),
                        getPattern(getVariable("x"), Vocabulary.owlTargetValue, getVariable("z"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node w = getValue(bindings, "w");
                Node y = getValue(bindings, "y");
                Node z = getValue(bindings, "z");
                DataPropertyAssertion axiom = Owl2Factory.newDataPropertyAssertion();
                axiom.setIndividual(getExpressionIndividual(w));
                axiom.setDataProperty(getExpressionDataProperty(y));
                axiom.setValueLiteral(getExpressionLiteral(z));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomSubAnnotPropertyOf() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("sub"), Vocabulary.rdfsSubPropertyOf, getVariable("super"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node sup = getValue(bindings, "super");
                Node sub = getValue(bindings, "sub");
                if (!isOfType((SubjectNode) sub, Vocabulary.owlAnnotationProperty))
                    return;
                SubAnnotationPropertyOf axiom = Owl2Factory.newSubAnnotationPropertyOf();
                axiom.setAnnotProperty(getExpressionAnnotationProperty(sub));
                axiom.setSuperAnnotProperty(getExpressionAnnotationProperty(sup));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomAnnotPropDomain() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfsDomain, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                if (!isOfType((SubjectNode) x, Vocabulary.owlAnnotationProperty))
                    return;
                AnnotationPropertyDomain axiom = Owl2Factory.newAnnotationPropertyDomain();
                axiom.setAnnotProperty(getExpressionAnnotationProperty(x));
                axiom.setAnnotDomain(getExpressionAnnotationProperty(y));
                axioms.add(axiom);
            }
        });
    }

    private void buildAxiomAnnotPropRange() {
        addRule(new RDFParserRule(2) {
            @Override
            public Quad[] getPatterns() {
                return new Quad[]{
                        getPattern(getVariable("x"), Vocabulary.rdfsDomain, getVariable("y"))
                };
            }

            @Override
            public void activate(Map<VariableNode, Node> bindings) {
                Node x = getValue(bindings, "x");
                Node y = getValue(bindings, "y");
                if (!isOfType((SubjectNode) x, Vocabulary.owlAnnotationProperty))
                    return;
                AnnotationPropertyRange axiom = Owl2Factory.newAnnotationPropertyRange();
                axiom.setAnnotProperty(getExpressionAnnotationProperty(x));
                axiom.setAnnotRange(getExpressionAnnotationProperty(y));
                axioms.add(axiom);
            }
        });
    }
}
