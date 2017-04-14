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

package org.xowl.infra.store;

import org.xowl.infra.lang.actions.FunctionDefinitionAxiom;
import org.xowl.infra.lang.actions.FunctionExpression;
import org.xowl.infra.lang.owl2.*;
import org.xowl.infra.lang.runtime.Entity;
import org.xowl.infra.lang.runtime.RuntimeFactory;
import org.xowl.infra.store.loaders.OWLLoaderResult;
import org.xowl.infra.store.loaders.RDFLoaderResult;
import org.xowl.infra.store.owl.OWLQueryEngine;
import org.xowl.infra.store.owl.OWLRuleEngine;
import org.xowl.infra.store.rdf.RDFQueryEngine;
import org.xowl.infra.store.rdf.RDFRuleEngine;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.store.storage.cache.CachedNodes;
import org.xowl.infra.store.writers.OWLSerializer;
import org.xowl.infra.store.writers.RDFSerializer;
import org.xowl.infra.utils.logging.Logger;

import java.util.*;

/**
 * Represents the direct interpretation of a of OWL2 ontologies
 *
 * @author Laurent Wouters
 */
public class RepositoryDirectSemantics extends Repository {
    /**
     * The node manager used for loading
     */
    private final NodeManager nodeManager;
    /**
     * The entities contained by the ontologies
     */
    private final Map<Ontology, Map<String, Entity>> mapEntities;
    /**
     * The known class unions
     */
    private final List<org.xowl.infra.lang.runtime.Class> classUnions;
    /**
     * The known class intersections
     */
    private final List<org.xowl.infra.lang.runtime.Class> classIntersections;
    /**
     * The known enumeration classes
     */
    private final List<org.xowl.infra.lang.runtime.Class> classOneOfs;
    /**
     * The known class complements
     */
    private final List<org.xowl.infra.lang.runtime.Class> classComplements;
    /**
     * The known inverse properties
     */
    private final List<org.xowl.infra.lang.runtime.ObjectProperty> propInverses;
    /**
     * The known data unions
     */
    private final List<org.xowl.infra.lang.runtime.Datatype> dataUnions;
    /**
     * The known data intersections
     */
    private final List<org.xowl.infra.lang.runtime.Datatype> dataIntersections;
    /**
     * The known enumeration datatypes
     */
    private final List<org.xowl.infra.lang.runtime.Datatype> dataOneOfs;
    /**
     * The known complement datatypes
     */
    private final List<org.xowl.infra.lang.runtime.Datatype> dataComplements;
    /**
     * The known anonymous individuals
     */
    private final List<org.xowl.infra.lang.owl2.AnonymousIndividual> anonymousIndividuals;

    /**
     * Initializes this repository
     *
     * @param mapper              The IRI mapper to use
     * @param resolveDependencies Whether dependencies should be resolved when loading resources
     */
    public RepositoryDirectSemantics(IRIMapper mapper, boolean resolveDependencies) {
        super(mapper, resolveDependencies);
        this.nodeManager = new CachedNodes();
        this.mapEntities = new HashMap<>();
        this.classUnions = new ArrayList<>();
        this.classIntersections = new ArrayList<>();
        this.classOneOfs = new ArrayList<>();
        this.classComplements = new ArrayList<>();
        this.propInverses = new ArrayList<>();
        this.dataUnions = new ArrayList<>();
        this.dataIntersections = new ArrayList<>();
        this.dataOneOfs = new ArrayList<>();
        this.dataComplements = new ArrayList<>();
        this.anonymousIndividuals = new ArrayList<>();
    }

    @Override
    public OWLQueryEngine getOWLQueryEngine() {
        return null;
    }

    @Override
    public RDFQueryEngine getRDFQueryEngine() {
        return null;
    }

    @Override
    public OWLRuleEngine getOWLRuleEngine() {
        return null;
    }

    @Override
    public RDFRuleEngine getRDFRuleEngine() {
        return null;
    }

    @Override
    public void setEntailmentRegime(EntailmentRegime regime) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    protected NodeManager getNodeManager() {
        return nodeManager;
    }

    @Override
    protected void doLoadRDF(Logger logger, Ontology ontology, RDFLoaderResult input) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doLoadOWL(Logger logger, Ontology ontology, OWLLoaderResult input) throws Exception {
        for (Axiom axiom : input.getAxioms())
            apply(axiom, false);
    }

    @Override
    protected void doExportRDF(Logger logger, Ontology ontology, RDFSerializer output) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doExportRDF(Logger logger, RDFSerializer output) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void exportResourceOWL(Logger logger, Ontology ontology, OWLSerializer output) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the entities in the specified ontology
     *
     * @param ontology An ontology
     * @return The entities in the ontology
     */
    public Collection<Entity> getEntities(Ontology ontology) {
        Map<String, Entity> sub = mapEntities.get(ontology);
        if (sub == null)
            return new ArrayList<>(0);
        return sub.values();
    }

    /**
     * Resolves an ontology for the specified IRI
     *
     * @param iri An IRI
     * @return The corresponding ontology
     */
    public Ontology resolveOntology(String iri) {
        Ontology result = super.resolveOntology(iri);
        if (!mapEntities.containsKey(result))
            mapEntities.put(result, new HashMap<String, Entity>());
        return result;
    }

    /**
     * Resolves an entity for the specified IRI
     *
     * @param iri An IRI
     * @return The corresponding entity
     */
    public Entity resolveEntity(IRI iri) {
        return resolveEntity(iri.getHasValue());
    }

    /**
     * Resolves an entity for the specified IRI
     *
     * @param iriValue An IRI
     * @return The corresponding entity
     */
    public Entity resolveEntity(String iriValue) {
        String[] parts = iriValue.split("#");
        Ontology ontology = null;
        String name;
        Map<String, Entity> sub;
        if (parts.length > 1) {
            ontology = resolveOntology(parts[0]);
            name = parts[1];
            sub = mapEntities.get(ontology);
        } else {
            sub = mapEntities.get(null);
            if (sub == null) {
                sub = new HashMap<>();
                mapEntities.put(null, sub);
            }
            name = iriValue;
        }

        Entity entity = sub.get(name);
        if (entity != null)
            return entity;
        entity = RuntimeFactory.newEntity();
        IRI iri = Owl2Factory.newIRI();
        iri.setHasValue(iriValue);
        entity.setHasIRI(iri);
        if (ontology != null)
            ontology.addContains(entity);
        sub.put(name, entity);
        return entity;
    }

    /**
     * Interprets an entity as a Class
     *
     * @param entity An entity
     * @return The interpretation
     */
    public org.xowl.infra.lang.runtime.Class interpretAsClass(Entity entity) {
        for (org.xowl.infra.lang.runtime.Interpretation interpretation : entity.getAllInterpretedAs()) {
            if (interpretation instanceof org.xowl.infra.lang.runtime.Class)
                return (org.xowl.infra.lang.runtime.Class) interpretation;
        }
        org.xowl.infra.lang.runtime.Class interpretation = RuntimeFactory.newClass();
        entity.addInterpretedAs(interpretation);
        return interpretation;
    }

    /**
     * Interprets an entity as a NamedIndividual
     *
     * @param entity An entity
     * @return The interpretation
     */
    public org.xowl.infra.lang.runtime.NamedIndividual interpretAsIndividual(Entity entity) {
        for (org.xowl.infra.lang.runtime.Interpretation interpretation : entity.getAllInterpretedAs()) {
            if (interpretation instanceof org.xowl.infra.lang.runtime.NamedIndividual)
                return (org.xowl.infra.lang.runtime.NamedIndividual) interpretation;
        }
        org.xowl.infra.lang.runtime.NamedIndividual interpretation = RuntimeFactory.newNamedIndividual();
        entity.addInterpretedAs(interpretation);
        return interpretation;
    }

    /**
     * Interprets an entity as an ObjectProperty
     *
     * @param entity An entity
     * @return The interpretation
     */
    public org.xowl.infra.lang.runtime.ObjectProperty interpretAsObjectProperty(Entity entity) {
        for (org.xowl.infra.lang.runtime.Interpretation interpretation : entity.getAllInterpretedAs()) {
            if (interpretation instanceof org.xowl.infra.lang.runtime.ObjectProperty)
                return (org.xowl.infra.lang.runtime.ObjectProperty) interpretation;
        }
        org.xowl.infra.lang.runtime.ObjectProperty interpretation = RuntimeFactory.newObjectProperty();
        entity.addInterpretedAs(interpretation);
        return interpretation;
    }

    /**
     * Interprets an entity as a DataProperty
     *
     * @param entity An entity
     * @return The interpretation
     */
    public org.xowl.infra.lang.runtime.DataProperty interpretAsDataProperty(Entity entity) {
        for (org.xowl.infra.lang.runtime.Interpretation interpretation : entity.getAllInterpretedAs()) {
            if (interpretation instanceof org.xowl.infra.lang.runtime.DataProperty)
                return (org.xowl.infra.lang.runtime.DataProperty) interpretation;
        }
        org.xowl.infra.lang.runtime.DataProperty interpretation = RuntimeFactory.newDataProperty();
        entity.addInterpretedAs(interpretation);
        return interpretation;
    }

    /**
     * Interprets an entity as a Datatype
     *
     * @param entity An entity
     * @return The interpretation
     */
    public org.xowl.infra.lang.runtime.Datatype interpretAsDatatype(Entity entity) {
        for (org.xowl.infra.lang.runtime.Interpretation interpretation : entity.getAllInterpretedAs()) {
            if (interpretation instanceof org.xowl.infra.lang.runtime.Datatype)
                return (org.xowl.infra.lang.runtime.Datatype) interpretation;
        }
        org.xowl.infra.lang.runtime.Datatype interpretation = RuntimeFactory.newDatatype();
        entity.addInterpretedAs(interpretation);
        return interpretation;
    }

    /**
     * Interprets an entity as a Function
     *
     * @param entity An entity
     * @return The interpretation
     */
    public org.xowl.infra.lang.runtime.Function interpretAsFunction(Entity entity) {
        for (org.xowl.infra.lang.runtime.Interpretation interpretation : entity.getAllInterpretedAs()) {
            if (interpretation instanceof org.xowl.infra.lang.runtime.Function)
                return (org.xowl.infra.lang.runtime.Function) interpretation;
        }
        org.xowl.infra.lang.runtime.Function interpretation = RuntimeFactory.newFunction();
        entity.addInterpretedAs(interpretation);
        return interpretation;
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    The axiom
     * @param negative Whether to add or remove the axiom
     */
    private void apply(Axiom axiom, boolean negative) {
        if (axiom instanceof org.xowl.infra.lang.owl2.Declaration)
            applyAxiomDeclaration((org.xowl.infra.lang.owl2.Declaration) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.DatatypeDefinition)
            applyAxiomDatatypeDefinition((org.xowl.infra.lang.owl2.DatatypeDefinition) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.SubClassOf)
            applyAxiomSubClassOf((org.xowl.infra.lang.owl2.SubClassOf) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.EquivalentClasses)
            applyAxiomEquivalentClasses((org.xowl.infra.lang.owl2.EquivalentClasses) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.DisjointClasses)
            applyAxiomDisjointClasses((org.xowl.infra.lang.owl2.DisjointClasses) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.DisjointUnion)
            applyAxiomDisjointUnion((org.xowl.infra.lang.owl2.DisjointUnion) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.SubObjectPropertyOf)
            applyAxiomSubObjectPropertyOf((org.xowl.infra.lang.owl2.SubObjectPropertyOf) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.EquivalentObjectProperties)
            applyAxiomEquivalentObjectProperties((org.xowl.infra.lang.owl2.EquivalentObjectProperties) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.DisjointObjectProperties)
            applyAxiomDisjointObjectProperties((org.xowl.infra.lang.owl2.DisjointObjectProperties) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.InverseObjectProperties)
            applyAxiomInverseObjectProperties((org.xowl.infra.lang.owl2.InverseObjectProperties) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.ObjectPropertyDomain)
            applyAxiomObjectPropertyDomain((org.xowl.infra.lang.owl2.ObjectPropertyDomain) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.ObjectPropertyRange)
            applyAxiomObjectPropertyRange((org.xowl.infra.lang.owl2.ObjectPropertyRange) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.FunctionalObjectProperty)
            applyAxiomFunctionalObjectProperty((org.xowl.infra.lang.owl2.FunctionalObjectProperty) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.InverseFunctionalObjectProperty)
            applyAxiomInverseFunctionalObjectProperty((org.xowl.infra.lang.owl2.InverseFunctionalObjectProperty) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.ReflexiveObjectProperty)
            applyAxiomReflexiveObjectProperty((org.xowl.infra.lang.owl2.ReflexiveObjectProperty) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.IrreflexiveObjectProperty)
            applyAxiomIrreflexiveObjectProperty((org.xowl.infra.lang.owl2.IrreflexiveObjectProperty) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.SymmetricObjectProperty)
            applyAxiomSymmetricObjectProperty((org.xowl.infra.lang.owl2.SymmetricObjectProperty) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.AsymmetricObjectProperty)
            applyAxiomAsymmetricObjectProperty((org.xowl.infra.lang.owl2.AsymmetricObjectProperty) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.TransitiveObjectProperty)
            applyAxiomTransitiveObjectProperty((org.xowl.infra.lang.owl2.TransitiveObjectProperty) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.SubDataPropertyOf)
            applyAxiomSubDataPropertyOf((org.xowl.infra.lang.owl2.SubDataPropertyOf) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.EquivalentDataProperties)
            applyAxiomEquivalentDataProperties((org.xowl.infra.lang.owl2.EquivalentDataProperties) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.DisjointDataProperties)
            applyAxiomDisjointDataProperties((org.xowl.infra.lang.owl2.DisjointDataProperties) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.DataPropertyDomain)
            applyAxiomDataPropertyDomain((org.xowl.infra.lang.owl2.DataPropertyDomain) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.DataPropertyRange)
            applyAxiomDataPropertyRange((org.xowl.infra.lang.owl2.DataPropertyRange) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.FunctionalDataProperty)
            applyAxiomFunctionalDataProperty((org.xowl.infra.lang.owl2.FunctionalDataProperty) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.SameIndividual)
            applyAxiomSameIndividual((org.xowl.infra.lang.owl2.SameIndividual) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.DifferentIndividuals)
            applyAxiomDifferentIndividuals((org.xowl.infra.lang.owl2.DifferentIndividuals) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.ClassAssertion)
            applyAxiomClassAssertion((org.xowl.infra.lang.owl2.ClassAssertion) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.ObjectPropertyAssertion)
            applyAxiomObjectPropertyAssertion((org.xowl.infra.lang.owl2.ObjectPropertyAssertion) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.NegativeObjectPropertyAssertion)
            applyAxiomNegativeObjectPropertyAssertion((org.xowl.infra.lang.owl2.NegativeObjectPropertyAssertion) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.DataPropertyAssertion)
            applyAxiomDataPropertyAssertion((org.xowl.infra.lang.owl2.DataPropertyAssertion) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.NegativeDataPropertyAssertion)
            applyAxiomNegativeDataPropertyAssertion((org.xowl.infra.lang.owl2.NegativeDataPropertyAssertion) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.HasKey)
            applyAxiomHasKey((org.xowl.infra.lang.owl2.HasKey) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.SubAnnotationPropertyOf)
            applyAxiomSubAnnotationPropertyOf((org.xowl.infra.lang.owl2.SubAnnotationPropertyOf) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.AnnotationPropertyDomain)
            applyAxiomAnnotationPropertyDomain((org.xowl.infra.lang.owl2.AnnotationPropertyDomain) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.AnnotationPropertyRange)
            applyAxiomAnnotationPropertyRange((org.xowl.infra.lang.owl2.AnnotationPropertyRange) axiom, negative);
        else if (axiom instanceof org.xowl.infra.lang.owl2.AnnotationAssertion)
            applyAxiomAnnotationAssertion((org.xowl.infra.lang.owl2.AnnotationAssertion) axiom, negative);
        else if (axiom instanceof FunctionDefinitionAxiom)
            applyAxiomFunctionDefinition((FunctionDefinitionAxiom) axiom, negative);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomDeclaration(org.xowl.infra.lang.owl2.Declaration axiom, boolean negative) {
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomDatatypeDefinition(org.xowl.infra.lang.owl2.DatatypeDefinition axiom, boolean negative) {
        org.xowl.infra.lang.runtime.Datatype datatype = evalDatatype(axiom.getDatatype());
        org.xowl.infra.lang.runtime.Datatype datarange = evalDatatype(axiom.getDatatype());
        if (!negative)
            datatype.setDataBase(datarange);
        else if (datatype.getDataBase() == datarange)
            datatype.setDataBase(null);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomSubClassOf(org.xowl.infra.lang.owl2.SubClassOf axiom, boolean negative) {
        org.xowl.infra.lang.runtime.Class sub = evalClass(axiom.getClasse());
        org.xowl.infra.lang.runtime.Class sup = evalClass(axiom.getSuperClass());
        if (sup.getInterpretationOf() == null && sup.getAllClassRestrictions().size() == 1) {
            // this is an anonymous class representing a class restriction
            if (!negative) sub.addClassRestrictions(sup.getAllClassRestrictions().iterator().next());
            else sub.removeClassRestrictions(sup.getAllClassRestrictions().iterator().next());
        } else {
            if (!negative) sub.addSubClassOf(sup);
            else sub.removeSubClassOf(sup);
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomEquivalentClasses(org.xowl.infra.lang.owl2.EquivalentClasses axiom, boolean negative) {
        List<org.xowl.infra.lang.runtime.Class> classes = new ArrayList<>();
        for (org.xowl.infra.lang.runtime.Class exp : toEvaluatedList(axiom.getClassSeq())) {
            for (org.xowl.infra.lang.runtime.Class c : classes) {
                if (!negative) c.addClassEquivalentTo(exp);
                else c.removeClassEquivalentTo(exp);
            }
            classes.add(exp);
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomDisjointClasses(org.xowl.infra.lang.owl2.DisjointClasses axiom, boolean negative) {
        List<org.xowl.infra.lang.runtime.Class> classes = new ArrayList<>();
        for (org.xowl.infra.lang.runtime.Class exp : toEvaluatedList(axiom.getClassSeq())) {
            for (org.xowl.infra.lang.runtime.Class c : classes) {
                if (!negative) c.addClassDisjointWith(exp);
                else c.removeClassDisjointWith(exp);
            }
            classes.add(exp);
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomDisjointUnion(org.xowl.infra.lang.owl2.DisjointUnion axiom, boolean negative) {
        org.xowl.infra.lang.runtime.Class main = evalClass(axiom.getClasse());
        for (org.xowl.infra.lang.runtime.Class exp : toEvaluatedList(axiom.getClassSeq())) {
            if (!negative) main.addClassDisjointWith(exp);
            else main.removeClassDisjointWith(exp);
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomSubObjectPropertyOf(org.xowl.infra.lang.owl2.SubObjectPropertyOf axiom, boolean negative) {
        org.xowl.infra.lang.runtime.ObjectProperty sub = evalObjectProperty(axiom.getObjectProperty());
        org.xowl.infra.lang.runtime.ObjectProperty sup = evalObjectProperty(axiom.getSuperObjectProperty());
        if (!negative) sub.addSubPropertyOf(sup);
        else sub.removeSubPropertyOf(sup);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomEquivalentObjectProperties(org.xowl.infra.lang.owl2.EquivalentObjectProperties axiom, boolean negative) {
        List<org.xowl.infra.lang.runtime.ObjectProperty> properties = new ArrayList<>();
        for (org.xowl.infra.lang.runtime.ObjectProperty exp : toEvaluatedList(axiom.getObjectPropertySeq())) {
            for (org.xowl.infra.lang.runtime.ObjectProperty p : properties) {
                if (!negative) p.addPropertyEquivalentTo(exp);
                else p.removePropertyEquivalentTo(exp);
            }
            properties.add(exp);
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomDisjointObjectProperties(org.xowl.infra.lang.owl2.DisjointObjectProperties axiom, boolean negative) {
        List<org.xowl.infra.lang.runtime.ObjectProperty> properties = new ArrayList<>();
        for (org.xowl.infra.lang.runtime.ObjectProperty exp : toEvaluatedList(axiom.getObjectPropertySeq())) {
            for (org.xowl.infra.lang.runtime.ObjectProperty p : properties) {
                if (!negative) p.addPropertyDisjointWith(exp);
                else p.removePropertyDisjointWith(exp);
            }
            properties.add(exp);
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomInverseObjectProperties(org.xowl.infra.lang.owl2.InverseObjectProperties axiom, boolean negative) {
        org.xowl.infra.lang.runtime.ObjectProperty property = evalObjectProperty(axiom.getObjectProperty());
        org.xowl.infra.lang.runtime.ObjectProperty inverse = evalObjectProperty(axiom.getInverse());
        if (!negative) inverse.setInverseOf(property);
        else if (inverse.getInverseOf() == property) inverse.setInverseOf(null);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomObjectPropertyDomain(org.xowl.infra.lang.owl2.ObjectPropertyDomain axiom, boolean negative) {
        org.xowl.infra.lang.runtime.ObjectProperty property = evalObjectProperty(axiom.getObjectProperty());
        org.xowl.infra.lang.runtime.Class c = evalClass(axiom.getClasse());
        if (!negative) property.setDomain(c);
        else if (property.getDomain() == c) property.setDomain(null);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomObjectPropertyRange(org.xowl.infra.lang.owl2.ObjectPropertyRange axiom, boolean negative) {
        org.xowl.infra.lang.runtime.ObjectProperty property = evalObjectProperty(axiom.getObjectProperty());
        org.xowl.infra.lang.runtime.Class c = evalClass(axiom.getClasse());
        if (!negative) property.setRange(c);
        else if (property.getRangeAs(null) == c) property.setRange(null);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomFunctionalObjectProperty(org.xowl.infra.lang.owl2.FunctionalObjectProperty axiom, boolean negative) {
        org.xowl.infra.lang.runtime.ObjectProperty property = evalObjectProperty(axiom.getObjectProperty());
        if (!negative) property.setIsFunctional(true);
        else property.setIsFunctional(false);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomInverseFunctionalObjectProperty(org.xowl.infra.lang.owl2.InverseFunctionalObjectProperty axiom, boolean negative) {
        org.xowl.infra.lang.runtime.ObjectProperty property = evalObjectProperty(axiom.getObjectProperty());
        if (!negative) property.setIsInverseFunctional(true);
        else property.setIsInverseFunctional(false);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomReflexiveObjectProperty(org.xowl.infra.lang.owl2.ReflexiveObjectProperty axiom, boolean negative) {
        org.xowl.infra.lang.runtime.ObjectProperty property = evalObjectProperty(axiom.getObjectProperty());
        if (!negative) property.setIsReflexive(true);
        else property.setIsReflexive(false);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomIrreflexiveObjectProperty(org.xowl.infra.lang.owl2.IrreflexiveObjectProperty axiom, boolean negative) {
        org.xowl.infra.lang.runtime.ObjectProperty property = evalObjectProperty(axiom.getObjectProperty());
        if (!negative) property.setIsIrreflexive(true);
        else property.setIsIrreflexive(false);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomSymmetricObjectProperty(org.xowl.infra.lang.owl2.SymmetricObjectProperty axiom, boolean negative) {
        org.xowl.infra.lang.runtime.ObjectProperty property = evalObjectProperty(axiom.getObjectProperty());
        if (!negative) property.setIsSymmetric(true);
        else property.setIsSymmetric(false);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomAsymmetricObjectProperty(org.xowl.infra.lang.owl2.AsymmetricObjectProperty axiom, boolean negative) {
        org.xowl.infra.lang.runtime.ObjectProperty property = evalObjectProperty(axiom.getObjectProperty());
        if (!negative) property.setIsAsymmetric(true);
        else property.setIsAsymmetric(false);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomTransitiveObjectProperty(org.xowl.infra.lang.owl2.TransitiveObjectProperty axiom, boolean negative) {
        org.xowl.infra.lang.runtime.ObjectProperty property = evalObjectProperty(axiom.getObjectProperty());
        if (!negative) property.setIsTransitive(true);
        else property.setIsTransitive(false);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomSubDataPropertyOf(org.xowl.infra.lang.owl2.SubDataPropertyOf axiom, boolean negative) {
        org.xowl.infra.lang.runtime.DataProperty sub = evalDataProperty(axiom.getDataProperty());
        org.xowl.infra.lang.runtime.DataProperty sup = evalDataProperty(axiom.getSuperDataProperty());
        if (!negative) sub.addSubPropertyOf(sup);
        else sub.removeSubPropertyOf(sup);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomEquivalentDataProperties(org.xowl.infra.lang.owl2.EquivalentDataProperties axiom, boolean negative) {
        List<org.xowl.infra.lang.runtime.DataProperty> properties = new ArrayList<>();
        for (org.xowl.infra.lang.runtime.DataProperty exp : toEvaluatedList(axiom.getDataPropertySeq())) {
            for (org.xowl.infra.lang.runtime.DataProperty p : properties) {
                if (!negative) p.addPropertyEquivalentTo(exp);
                else p.removePropertyEquivalentTo(exp);
            }
            properties.add(exp);
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomDisjointDataProperties(org.xowl.infra.lang.owl2.DisjointDataProperties axiom, boolean negative) {
        List<org.xowl.infra.lang.runtime.DataProperty> properties = new ArrayList<>();
        for (org.xowl.infra.lang.runtime.DataProperty exp : toEvaluatedList(axiom.getDataPropertySeq())) {
            for (org.xowl.infra.lang.runtime.DataProperty p : properties) {
                if (!negative) p.addPropertyDisjointWith(exp);
                else p.removePropertyDisjointWith(exp);
            }
            properties.add(exp);
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomDataPropertyDomain(org.xowl.infra.lang.owl2.DataPropertyDomain axiom, boolean negative) {
        org.xowl.infra.lang.runtime.DataProperty property = evalDataProperty(axiom.getDataProperty());
        org.xowl.infra.lang.runtime.Class c = evalClass(axiom.getClasse());
        if (!negative) property.setDomain(c);
        else if (property.getDomain() == c) property.setDomain(null);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomDataPropertyRange(org.xowl.infra.lang.owl2.DataPropertyRange axiom, boolean negative) {
        org.xowl.infra.lang.runtime.DataProperty property = evalDataProperty(axiom.getDataProperty());
        org.xowl.infra.lang.runtime.Datatype range = evalDatatype(axiom.getDatarange());
        if (!negative) property.setRange(range);
        else if (property.getRangeAs(null) == range) property.setRange(null);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomFunctionalDataProperty(org.xowl.infra.lang.owl2.FunctionalDataProperty axiom, boolean negative) {
        org.xowl.infra.lang.runtime.DataProperty property = evalDataProperty(axiom.getDataProperty());
        if (!negative) property.setIsFunctional(true);
        else property.setIsFunctional(false);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomSameIndividual(org.xowl.infra.lang.owl2.SameIndividual axiom, boolean negative) {
        List<org.xowl.infra.lang.runtime.Individual> individuals = new ArrayList<>();
        for (org.xowl.infra.lang.runtime.Individual exp : toEvaluatedList(axiom.getIndividualSeq())) {
            for (org.xowl.infra.lang.runtime.Individual p : individuals) {
                if (!negative) p.addSameAs(exp);
                else p.removeSameAs(exp);
            }
            individuals.add(exp);
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomDifferentIndividuals(org.xowl.infra.lang.owl2.DifferentIndividuals axiom, boolean negative) {
        List<org.xowl.infra.lang.runtime.Individual> individuals = new ArrayList<>();
        for (org.xowl.infra.lang.runtime.Individual exp : toEvaluatedList(axiom.getIndividualSeq())) {
            for (org.xowl.infra.lang.runtime.Individual p : individuals) {
                if (!negative) p.addDifferentFrom(exp);
                else p.removeDifferentFrom(exp);
            }
            individuals.add(exp);
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomClassAssertion(org.xowl.infra.lang.owl2.ClassAssertion axiom, boolean negative) {
        org.xowl.infra.lang.runtime.Individual individual = evalIndividual(axiom.getIndividual());
        org.xowl.infra.lang.runtime.Class c = evalClass(axiom.getClasse());
        if (!negative) individual.addClassifiedBy(c);
        else individual.removeClassifiedBy(c);
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomObjectPropertyAssertion(org.xowl.infra.lang.owl2.ObjectPropertyAssertion axiom, boolean negative) {
        org.xowl.infra.lang.runtime.Individual individual = evalIndividual(axiom.getIndividual());
        org.xowl.infra.lang.runtime.ObjectProperty property = evalObjectProperty(axiom.getObjectProperty());
        org.xowl.infra.lang.runtime.Individual value = evalIndividual(axiom.getValueIndividual());

        if (!negative) {
            org.xowl.infra.lang.runtime.ObjectPropertyAssertion assertion = RuntimeFactory.newObjectPropertyAssertion();
            assertion.setProperty(property);
            assertion.setIsNegative(false);
            assertion.setValueIndividual(value);
            individual.addAsserts(assertion);
        } else {
            for (org.xowl.infra.lang.runtime.PropertyAssertion assertion : individual.getAllAsserts()) {
                if (assertion instanceof org.xowl.infra.lang.runtime.ObjectPropertyAssertion) {
                    org.xowl.infra.lang.runtime.ObjectPropertyAssertion temp = (org.xowl.infra.lang.runtime.ObjectPropertyAssertion) assertion;
                    if ((temp.getPropertyAs(null) == property) && (temp.getValueIndividual() == value) && !temp.getIsNegative()) {
                        individual.removeAsserts(assertion);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomNegativeObjectPropertyAssertion(org.xowl.infra.lang.owl2.NegativeObjectPropertyAssertion axiom, boolean negative) {
        org.xowl.infra.lang.runtime.Individual individual = evalIndividual(axiom.getIndividual());
        org.xowl.infra.lang.runtime.ObjectProperty property = evalObjectProperty(axiom.getObjectProperty());
        org.xowl.infra.lang.runtime.Individual value = evalIndividual(axiom.getValueIndividual());

        if (!negative) {
            org.xowl.infra.lang.runtime.ObjectPropertyAssertion assertion = RuntimeFactory.newObjectPropertyAssertion();
            assertion.setProperty(property);
            assertion.setIsNegative(true);
            assertion.setValueIndividual(value);
            individual.addAsserts(assertion);
        } else {
            for (org.xowl.infra.lang.runtime.PropertyAssertion assertion : individual.getAllAsserts()) {
                if (assertion instanceof org.xowl.infra.lang.runtime.ObjectPropertyAssertion) {
                    org.xowl.infra.lang.runtime.ObjectPropertyAssertion temp = (org.xowl.infra.lang.runtime.ObjectPropertyAssertion) assertion;
                    if ((temp.getPropertyAs(null) == property) && (temp.getValueIndividual() == value) && !temp.getIsNegative()) {
                        individual.removeAsserts(assertion);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomDataPropertyAssertion(org.xowl.infra.lang.owl2.DataPropertyAssertion axiom, boolean negative) {
        org.xowl.infra.lang.runtime.Individual individual = evalIndividual(axiom.getIndividual());
        org.xowl.infra.lang.runtime.DataProperty property = evalDataProperty(axiom.getDataProperty());
        org.xowl.infra.lang.runtime.Literal value = evalLiteral(axiom.getValueLiteral());

        if (!negative) {
            org.xowl.infra.lang.runtime.DataPropertyAssertion assertion = RuntimeFactory.newDataPropertyAssertion();
            assertion.setProperty(property);
            assertion.setIsNegative(false);
            assertion.setValueLiteral(value);
            individual.addAsserts(assertion);
        } else {
            for (org.xowl.infra.lang.runtime.PropertyAssertion assertion : individual.getAllAsserts()) {
                if (assertion instanceof org.xowl.infra.lang.runtime.DataPropertyAssertion) {
                    org.xowl.infra.lang.runtime.DataPropertyAssertion temp = (org.xowl.infra.lang.runtime.DataPropertyAssertion) assertion;
                    if ((temp.getPropertyAs(null) == property) && (temp.getValueLiteral() == value) && !temp.getIsNegative()) {
                        individual.removeAsserts(assertion);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomNegativeDataPropertyAssertion(org.xowl.infra.lang.owl2.NegativeDataPropertyAssertion axiom, boolean negative) {
        org.xowl.infra.lang.runtime.Individual individual = evalIndividual(axiom.getIndividual());
        org.xowl.infra.lang.runtime.DataProperty property = evalDataProperty(axiom.getDataProperty());
        org.xowl.infra.lang.runtime.Literal value = evalLiteral(axiom.getValueLiteral());

        if (!negative) {
            org.xowl.infra.lang.runtime.DataPropertyAssertion assertion = RuntimeFactory.newDataPropertyAssertion();
            assertion.setProperty(property);
            assertion.setIsNegative(true);
            assertion.setValueLiteral(value);
            individual.addAsserts(assertion);
        } else {
            for (org.xowl.infra.lang.runtime.PropertyAssertion assertion : individual.getAllAsserts()) {
                if (assertion instanceof org.xowl.infra.lang.runtime.DataPropertyAssertion) {
                    org.xowl.infra.lang.runtime.DataPropertyAssertion temp = (org.xowl.infra.lang.runtime.DataPropertyAssertion) assertion;
                    if ((temp.getPropertyAs(null) == property) && (temp.getValueLiteral() == value) && !temp.getIsNegative()) {
                        individual.removeAsserts(assertion);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomHasKey(org.xowl.infra.lang.owl2.HasKey axiom, boolean negative) {
        //TODO: complete
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomSubAnnotationPropertyOf(org.xowl.infra.lang.owl2.SubAnnotationPropertyOf axiom, boolean negative) {
        //TODO: complete
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomAnnotationPropertyDomain(org.xowl.infra.lang.owl2.AnnotationPropertyDomain axiom, boolean negative) {
        //TODO: complete
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomAnnotationPropertyRange(org.xowl.infra.lang.owl2.AnnotationPropertyRange axiom, boolean negative) {
        //TODO: complete
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomAnnotationAssertion(org.xowl.infra.lang.owl2.AnnotationAssertion axiom, boolean negative) {
        //TODO: complete
    }

    /**
     * Applies the specified axiom
     *
     * @param axiom    An axiom
     * @param negative Whether to add or remove the axiom
     */
    private void applyAxiomFunctionDefinition(FunctionDefinitionAxiom axiom, boolean negative) {
        org.xowl.infra.lang.runtime.Function function = evalFunction(axiom.getFunction());
        if (negative) {
            if (function.getDefinedAs() == axiom.getDefinition())
                function.setDefinedAs(null);
        } else {
            function.setDefinedAs(axiom.getDefinition());
        }
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalClass(ClassExpression expression) {
        if (expression == null)
            return null;
        if (expression instanceof IRI)
            return evalExpClass((IRI) expression);
        if (expression instanceof ObjectUnionOf)
            return evalExpObjectUnionOf((ObjectUnionOf) expression);
        if (expression instanceof ObjectIntersectionOf)
            return evalExpObjectIntersectionOf((ObjectIntersectionOf) expression);
        if (expression instanceof ObjectOneOf)
            return evalExpObjectOneOf((ObjectOneOf) expression);
        if (expression instanceof ObjectComplementOf)
            return evalExpObjectComplementOf((ObjectComplementOf) expression);
        if (expression instanceof DataAllValuesFrom)
            return evalExpDataAllValuesFrom((DataAllValuesFrom) expression);
        if (expression instanceof DataExactCardinality)
            return evalExpDataExactCardinality((DataExactCardinality) expression);
        if (expression instanceof DataHasValue)
            return evalExpDataHasValue((DataHasValue) expression);
        if (expression instanceof DataMaxCardinality)
            return evalExpDataMaxCardinality((DataMaxCardinality) expression);
        if (expression instanceof DataMinCardinality)
            return evalExpDataMinCardinality((DataMinCardinality) expression);
        if (expression instanceof DataSomeValuesFrom)
            return evalExpDataSomeValuesFrom((DataSomeValuesFrom) expression);
        if (expression instanceof ObjectAllValuesFrom)
            return evalExpObjectAllValuesFrom((ObjectAllValuesFrom) expression);
        if (expression instanceof ObjectExactCardinality)
            return evalExpObjectExactCardinality((ObjectExactCardinality) expression);
        if (expression instanceof ObjectHasSelf)
            return evalExpObjectHasSelf((ObjectHasSelf) expression);
        if (expression instanceof ObjectHasValue)
            return evalExpObjectHasValue((ObjectHasValue) expression);
        if (expression instanceof ObjectMaxCardinality)
            return evalExpObjectMaxCardinality((ObjectMaxCardinality) expression);
        if (expression instanceof ObjectMinCardinality)
            return evalExpObjectMinCardinality((ObjectMinCardinality) expression);
        if (expression instanceof ObjectSomeValuesFrom)
            return evalExpObjectSomeValuesFrom((ObjectSomeValuesFrom) expression);
        return null;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpClass(IRI expression) {
        return interpretAsClass(resolveEntity(expression));
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpObjectUnionOf(ObjectUnionOf expression) {
        List<org.xowl.infra.lang.runtime.Class> unified = new ArrayList<>();
        for (org.xowl.infra.lang.runtime.Class exp : toEvaluatedList(expression.getClassSeq()))
            unified.add(exp);
        // Try to find previously resolved class
        for (org.xowl.infra.lang.runtime.Class previous : classUnions) {
            if (previous.getAllClassUnionOf().size() != unified.size())
                continue;
            boolean equal = true;
            for (org.xowl.infra.lang.runtime.Class c : previous.getAllClassUnionOf()) {
                if (!unified.contains(c)) {
                    equal = false;
                    break;
                }
            }
            if (equal)
                return previous;
        }
        // New union
        org.xowl.infra.lang.runtime.Class union = RuntimeFactory.newClass();
        for (org.xowl.infra.lang.runtime.Class c : unified)
            union.addClassUnionOf(c);
        classUnions.add(union);
        return union;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpObjectIntersectionOf(ObjectIntersectionOf expression) {
        List<org.xowl.infra.lang.runtime.Class> intersected = new ArrayList<>();
        for (org.xowl.infra.lang.runtime.Class exp : toEvaluatedList(expression.getClassSeq()))
            intersected.add(exp);
        // Try to find previously resolved class
        for (org.xowl.infra.lang.runtime.Class previous : classIntersections) {
            if (previous.getAllClassIntersectionOf().size() != intersected.size())
                continue;
            boolean equal = true;
            for (org.xowl.infra.lang.runtime.Class c : previous.getAllClassIntersectionOf()) {
                if (!intersected.contains(c)) {
                    equal = false;
                    break;
                }
            }
            if (equal)
                return previous;
        }
        // New union
        org.xowl.infra.lang.runtime.Class intersection = RuntimeFactory.newClass();
        for (org.xowl.infra.lang.runtime.Class c : intersected)
            intersection.addClassUnionOf(c);
        classIntersections.add(intersection);
        return intersection;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpObjectOneOf(ObjectOneOf expression) {
        List<org.xowl.infra.lang.runtime.Individual> individuals = new ArrayList<>();
        for (org.xowl.infra.lang.runtime.Individual exp : toEvaluatedList(expression.getIndividualSeq()))
            individuals.add(exp);
        // Try to find previously resolved class
        for (org.xowl.infra.lang.runtime.Class previous : classOneOfs) {
            if (previous.getAllClassOneOf().size() != individuals.size())
                continue;
            boolean equal = true;
            for (org.xowl.infra.lang.runtime.Individual i : previous.getAllClassOneOf()) {
                if (!individuals.contains(i)) {
                    equal = false;
                    break;
                }
            }
            if (equal)
                return previous;
        }
        org.xowl.infra.lang.runtime.Class oneOf = RuntimeFactory.newClass();
        for (org.xowl.infra.lang.runtime.Individual i : individuals)
            oneOf.addClassOneOf(i);
        classOneOfs.add(oneOf);
        return oneOf;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpObjectComplementOf(ObjectComplementOf expression) {
        org.xowl.infra.lang.runtime.Class complement = evalClass(expression.getClasse());
        if (classComplements.contains(complement))
            return complement.getClassComplementOf();
        org.xowl.infra.lang.runtime.Class complementOf = RuntimeFactory.newClass();
        complementOf.setClassComplementOf(complement);
        classComplements.add(complement);
        classComplements.add(complementOf);
        return complementOf;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpDataAllValuesFrom(DataAllValuesFrom expression) {
        org.xowl.infra.lang.runtime.Class classe = RuntimeFactory.newClass();
        org.xowl.infra.lang.runtime.DataAllValuesFrom restriction = RuntimeFactory.newDataAllValuesFrom();
        for (org.xowl.infra.lang.runtime.DataProperty prop : toEvaluatedList(expression.getDataPropertySeq()))
            restriction.addDataProperties(prop);
        restriction.setDatatype(evalDatatype(expression.getDatarange()));
        classe.addClassRestrictions(restriction);
        return classe;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpDataExactCardinality(DataExactCardinality expression) {
        org.xowl.infra.lang.runtime.Class classe = RuntimeFactory.newClass();
        org.xowl.infra.lang.runtime.DataExactCardinality restriction = RuntimeFactory.newDataExactCardinality();
        restriction.setDataProperty(evalDataProperty(expression.getDataProperty()));
        restriction.setCardinality(Integer.parseInt(evalLiteral(expression.getCardinality()).getLexicalValue()));
        if (expression.getDatarange() != null)
            restriction.setDatatype(evalDatatype(expression.getDatarange()));
        classe.addClassRestrictions(restriction);
        return classe;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpDataHasValue(DataHasValue expression) {
        org.xowl.infra.lang.runtime.Class classe = RuntimeFactory.newClass();
        org.xowl.infra.lang.runtime.DataHasValue restriction = RuntimeFactory.newDataHasValue();
        restriction.setDataProperty(evalDataProperty(expression.getDataProperty()));
        restriction.setLiteral(evalLiteral(expression.getLiteral()));
        classe.addClassRestrictions(restriction);
        return classe;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpDataMaxCardinality(DataMaxCardinality expression) {
        org.xowl.infra.lang.runtime.Class classe = RuntimeFactory.newClass();
        org.xowl.infra.lang.runtime.DataMaxCardinality restriction = RuntimeFactory.newDataMaxCardinality();
        restriction.setDataProperty(evalDataProperty(expression.getDataProperty()));
        restriction.setCardinality(Integer.parseInt(evalLiteral(expression.getCardinality()).getLexicalValue()));
        if (expression.getDatarange() != null)
            restriction.setDatatype(evalDatatype(expression.getDatarange()));
        classe.addClassRestrictions(restriction);
        return classe;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpDataMinCardinality(DataMinCardinality expression) {
        org.xowl.infra.lang.runtime.Class classe = RuntimeFactory.newClass();
        org.xowl.infra.lang.runtime.DataMinCardinality restriction = RuntimeFactory.newDataMinCardinality();
        restriction.setDataProperty(evalDataProperty(expression.getDataProperty()));
        restriction.setCardinality(Integer.parseInt(evalLiteral(expression.getCardinality()).getLexicalValue()));
        if (expression.getDatarange() != null)
            restriction.setDatatype(evalDatatype(expression.getDatarange()));
        classe.addClassRestrictions(restriction);
        return classe;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpDataSomeValuesFrom(DataSomeValuesFrom expression) {
        org.xowl.infra.lang.runtime.Class classe = RuntimeFactory.newClass();
        org.xowl.infra.lang.runtime.DataSomeValuesFrom restriction = RuntimeFactory.newDataSomeValuesFrom();
        for (org.xowl.infra.lang.runtime.DataProperty prop : toEvaluatedList(expression.getDataPropertySeq()))
            restriction.addDataProperties(prop);
        restriction.setDatatype(evalDatatype(expression.getDatarange()));
        classe.addClassRestrictions(restriction);
        return classe;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpObjectAllValuesFrom(ObjectAllValuesFrom expression) {
        org.xowl.infra.lang.runtime.Class classe = RuntimeFactory.newClass();
        org.xowl.infra.lang.runtime.ObjectAllValuesFrom restriction = RuntimeFactory.newObjectAllValuesFrom();
        restriction.setObjectProperty(evalObjectProperty(expression.getObjectProperty()));
        restriction.setClasse(evalClass(expression.getClasse()));
        classe.addClassRestrictions(restriction);
        return classe;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpObjectExactCardinality(ObjectExactCardinality expression) {
        org.xowl.infra.lang.runtime.Class classe = RuntimeFactory.newClass();
        org.xowl.infra.lang.runtime.ObjectExactCardinality restriction = RuntimeFactory.newObjectExactCardinality();
        restriction.setObjectProperty(evalObjectProperty(expression.getObjectProperty()));
        restriction.setCardinality(Integer.parseInt(evalLiteral(expression.getCardinality()).getLexicalValue()));
        if (expression.getClasse() != null)
            restriction.setClasse(evalClass(expression.getClasse()));
        classe.addClassRestrictions(restriction);
        return classe;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpObjectHasSelf(ObjectHasSelf expression) {
        org.xowl.infra.lang.runtime.Class classe = RuntimeFactory.newClass();
        org.xowl.infra.lang.runtime.ObjectHasSelf restriction = RuntimeFactory.newObjectHasSelf();
        restriction.setObjectProperty(evalObjectProperty(expression.getObjectProperty()));
        classe.addClassRestrictions(restriction);
        return classe;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpObjectHasValue(ObjectHasValue expression) {
        org.xowl.infra.lang.runtime.Class classe = RuntimeFactory.newClass();
        org.xowl.infra.lang.runtime.ObjectHasValue restriction = RuntimeFactory.newObjectHasValue();
        restriction.setObjectProperty(evalObjectProperty(expression.getObjectProperty()));
        restriction.setIndividual(evalIndividual(expression.getIndividual()));
        classe.addClassRestrictions(restriction);
        return classe;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpObjectMaxCardinality(ObjectMaxCardinality expression) {
        org.xowl.infra.lang.runtime.Class classe = RuntimeFactory.newClass();
        org.xowl.infra.lang.runtime.ObjectMaxCardinality restriction = RuntimeFactory.newObjectMaxCardinality();
        restriction.setObjectProperty(evalObjectProperty(expression.getObjectProperty()));
        restriction.setCardinality(Integer.parseInt(evalLiteral(expression.getCardinality()).getLexicalValue()));
        if (expression.getClasse() != null)
            restriction.setClasse(evalClass(expression.getClasse()));
        classe.addClassRestrictions(restriction);
        return classe;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpObjectMinCardinality(ObjectMinCardinality expression) {
        org.xowl.infra.lang.runtime.Class classe = RuntimeFactory.newClass();
        org.xowl.infra.lang.runtime.ObjectMinCardinality restriction = RuntimeFactory.newObjectMinCardinality();
        restriction.setObjectProperty(evalObjectProperty(expression.getObjectProperty()));
        restriction.setCardinality(Integer.parseInt(evalLiteral(expression.getCardinality()).getLexicalValue()));
        if (expression.getClasse() != null)
            restriction.setClasse(evalClass(expression.getClasse()));
        classe.addClassRestrictions(restriction);
        return classe;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Class evalExpObjectSomeValuesFrom(ObjectSomeValuesFrom expression) {
        org.xowl.infra.lang.runtime.Class classe = RuntimeFactory.newClass();
        org.xowl.infra.lang.runtime.ObjectSomeValuesFrom restriction = RuntimeFactory.newObjectSomeValuesFrom();
        restriction.setObjectProperty(evalObjectProperty(expression.getObjectProperty()));
        restriction.setClasse(evalClass(expression.getClasse()));
        classe.addClassRestrictions(restriction);
        return classe;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.ObjectProperty evalObjectProperty(ObjectPropertyExpression expression) {
        if (expression == null)
            return null;
        if (expression instanceof IRI)
            return evalExpObjectProperty((IRI) expression);
        if (expression instanceof ObjectInverseOf)
            return evalExpObjectInverseOf((ObjectInverseOf) expression);
        return null;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.ObjectProperty evalExpObjectProperty(IRI expression) {
        return interpretAsObjectProperty(resolveEntity(expression));
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.ObjectProperty evalExpObjectInverseOf(ObjectInverseOf expression) {
        org.xowl.infra.lang.runtime.ObjectProperty inverse = evalObjectProperty(expression.getInverse());
        if (propInverses.contains(inverse))
            return inverse.getInverseOf();
        org.xowl.infra.lang.runtime.ObjectProperty inverseOf = RuntimeFactory.newObjectProperty();
        inverseOf.setInverseOf(inverse);
        propInverses.add(inverse);
        propInverses.add(inverseOf);
        return inverseOf;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.DataProperty evalDataProperty(DataPropertyExpression expression) {
        if (expression == null)
            return null;
        if (expression instanceof IRI)
            return evalExpDataProperty((IRI) expression);
        return null;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.DataProperty evalExpDataProperty(IRI expression) {
        return interpretAsDataProperty(resolveEntity(expression));
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Datatype evalDatatype(Datarange expression) {
        if (expression == null)
            return null;
        if (expression instanceof IRI)
            return evalExpDatatype((IRI) expression);
        if (expression instanceof DataComplementOf)
            return evalExpDataComplementOf((DataComplementOf) expression);
        if (expression instanceof DataIntersectionOf)
            return evalExpDataIntersectionOf((DataIntersectionOf) expression);
        if (expression instanceof DataOneOf)
            return evalExpDataOneOf((DataOneOf) expression);
        if (expression instanceof DatatypeRestriction)
            return evalExpDatatypeRestriction((DatatypeRestriction) expression);
        if (expression instanceof DataUnionOf)
            return evalExpDataUnionOf((DataUnionOf) expression);
        return null;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Datatype evalExpDatatype(IRI expression) {
        return interpretAsDatatype(resolveEntity(expression));
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Datatype evalExpDataComplementOf(DataComplementOf expression) {
        org.xowl.infra.lang.runtime.Datatype complement = evalDatatype(expression.getDatarange());
        if (dataComplements.contains(complement))
            return complement.getDataComplementOf();
        org.xowl.infra.lang.runtime.Datatype complementOf = RuntimeFactory.newDatatype();
        complementOf.setDataComplementOf(complement);
        dataComplements.add(complement);
        dataComplements.add(complementOf);
        return complementOf;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Datatype evalExpDataIntersectionOf(DataIntersectionOf expression) {
        List<org.xowl.infra.lang.runtime.Datatype> intersected = new ArrayList<>();
        for (org.xowl.infra.lang.runtime.Datatype exp : toEvaluatedList(expression.getDatarangeSeq()))
            intersected.add(exp);
        // Try to find previously resolved datatype
        for (org.xowl.infra.lang.runtime.Datatype previous : dataIntersections) {
            if (previous.getAllDataIntersectionOf().size() != intersected.size())
                continue;
            boolean equal = true;
            for (org.xowl.infra.lang.runtime.Datatype c : previous.getAllDataIntersectionOf()) {
                if (!intersected.contains(c)) {
                    equal = false;
                    break;
                }
            }
            if (equal)
                return previous;
        }
        // New union
        org.xowl.infra.lang.runtime.Datatype intersection = RuntimeFactory.newDatatype();
        for (org.xowl.infra.lang.runtime.Datatype c : intersected)
            intersection.addDataIntersectionOf(c);
        dataIntersections.add(intersection);
        return intersection;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Datatype evalExpDataOneOf(DataOneOf expression) {
        List<org.xowl.infra.lang.runtime.Literal> literals = toEvaluatedList(expression.getLiteralSeq());
        // Try to find previously resolved class
        for (org.xowl.infra.lang.runtime.Datatype previous : dataOneOfs) {
            if (previous.getAllDataOneOf().size() != literals.size())
                continue;
            boolean equal = true;
            for (org.xowl.infra.lang.runtime.Literal i : previous.getAllDataOneOf()) {
                if (!literals.contains(i)) {
                    equal = false;
                    break;
                }
            }
            if (equal)
                return previous;
        }
        org.xowl.infra.lang.runtime.Datatype oneOf = RuntimeFactory.newDatatype();
        for (org.xowl.infra.lang.runtime.Literal i : literals)
            oneOf.addDataOneOf(i);
        dataOneOfs.add(oneOf);
        return oneOf;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Datatype evalExpDatatypeRestriction(DatatypeRestriction expression) {
        org.xowl.infra.lang.runtime.Datatype datatype = RuntimeFactory.newDatatype();
        for (FacetRestriction restriction : expression.getAllFacetRestrictions()) {
            org.xowl.infra.lang.runtime.DatatypeRestriction restric = RuntimeFactory.newDatatypeRestriction();
            restric.setFacet(restriction.getConstrainingFacet());
            restric.setValueLiteral(evalLiteral(restriction.getConstrainingValue()));
            datatype.addDataRestrictions(restric);
        }
        return datatype;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Datatype evalExpDataUnionOf(DataUnionOf expression) {
        List<org.xowl.infra.lang.runtime.Datatype> unified = new ArrayList<>();
        for (org.xowl.infra.lang.runtime.Datatype exp : toEvaluatedList(expression.getDatarangeSeq()))
            unified.add(exp);
        // Try to find previously resolved datatype
        for (org.xowl.infra.lang.runtime.Datatype previous : dataUnions) {
            if (previous.getAllDataUnionOf().size() != unified.size())
                continue;
            boolean equal = true;
            for (org.xowl.infra.lang.runtime.Datatype c : previous.getAllDataUnionOf()) {
                if (!unified.contains(c)) {
                    equal = false;
                    break;
                }
            }
            if (equal)
                return previous;
        }
        // New union
        org.xowl.infra.lang.runtime.Datatype union = RuntimeFactory.newDatatype();
        for (org.xowl.infra.lang.runtime.Datatype c : unified)
            union.addDataUnionOf(c);
        dataUnions.add(union);
        return union;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Individual evalIndividual(IndividualExpression expression) {
        if (expression == null)
            return null;
        if (expression instanceof IRI)
            return evalExpNamedIndividual((IRI) expression);
        if (expression instanceof AnonymousIndividual)
            return evalExpAnonymousIndividual((AnonymousIndividual) expression);
        return null;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Individual evalExpNamedIndividual(IRI expression) {
        return interpretAsIndividual(resolveEntity(expression));
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Individual evalExpAnonymousIndividual(AnonymousIndividual expression) {
        anonymousIndividuals.add(expression);
        return expression;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Literal evalLiteral(LiteralExpression expression) {
        if (expression == null)
            return null;
        if (expression instanceof Literal)
            return evalExpLiteral((Literal) expression);
        return null;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Literal evalExpLiteral(Literal expression) {
        org.xowl.infra.lang.runtime.Literal literal = RuntimeFactory.newLiteral();
        literal.setLexicalValue(expression.getLexicalValue());
        literal.setMemberOf(interpretAsDatatype(resolveEntity(expression.getMemberOf())));
        return literal;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Function evalFunction(FunctionExpression expression) {
        if (expression == null)
            return null;
        if (expression instanceof IRI)
            return evalExpFunction((IRI) expression);
        return null;
    }

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    private org.xowl.infra.lang.runtime.Function evalExpFunction(IRI expression) {
        return interpretAsFunction(resolveEntity(expression));
    }

    /**
     * Gets the evaluated list for the specified sequence expression
     *
     * @param expression A sequence expresison
     * @return The evaluated list
     */
    private List<org.xowl.infra.lang.runtime.Class> toEvaluatedList(ClassSequenceExpression expression) {
        List<org.xowl.infra.lang.runtime.Class> result = new ArrayList<>();
        List<ClassElement> elements = new ArrayList<>(((ClassSequence) expression).getAllClassElements());
        sortElements(elements);
        for (ClassElement elem : elements)
            result.add(evalClass(elem.getClasse()));
        return result;
    }

    /**
     * Gets the evaluated list for the specified sequence expression
     *
     * @param expression A sequence expresison
     * @return The evaluated list
     */
    private List<org.xowl.infra.lang.runtime.ObjectProperty> toEvaluatedList(ObjectPropertySequenceExpression expression) {
        List<org.xowl.infra.lang.runtime.ObjectProperty> result = new ArrayList<>();
        List<ObjectPropertyElement> elements = new ArrayList<>(((ObjectPropertySequence) expression).getAllObjectPropertyElements());
        sortElements(elements);
        for (ObjectPropertyElement elem : elements)
            result.add(evalObjectProperty(elem.getObjectProperty()));
        return result;
    }

    /**
     * Gets the evaluated list for the specified sequence expression
     *
     * @param expression A sequence expresison
     * @return The evaluated list
     */
    private List<org.xowl.infra.lang.runtime.DataProperty> toEvaluatedList(DataPropertySequenceExpression expression) {
        List<org.xowl.infra.lang.runtime.DataProperty> result = new ArrayList<>();
        List<DataPropertyElement> elements = new ArrayList<>(((DataPropertySequence) expression).getAllDataPropertyElements());
        sortElements(elements);
        for (DataPropertyElement elem : elements)
            result.add(evalDataProperty(elem.getDataProperty()));
        return result;
    }

    /**
     * Gets the evaluated list for the specified sequence expression
     *
     * @param expression A sequence expresison
     * @return The evaluated list
     */
    private List<org.xowl.infra.lang.runtime.Individual> toEvaluatedList(IndividualSequenceExpression expression) {
        List<org.xowl.infra.lang.runtime.Individual> result = new ArrayList<>();
        List<IndividualElement> elements = new ArrayList<>(((IndividualSequence) expression).getAllIndividualElements());
        sortElements(elements);
        for (IndividualElement elem : elements)
            result.add(evalIndividual(elem.getIndividual()));
        return result;
    }

    /**
     * Gets the evaluated list for the specified sequence expression
     *
     * @param expression A sequence expresison
     * @return The evaluated list
     */
    private List<org.xowl.infra.lang.runtime.Datatype> toEvaluatedList(DatarangeSequenceExpression expression) {
        List<org.xowl.infra.lang.runtime.Datatype> result = new ArrayList<>();
        List<DatarangeElement> elements = new ArrayList<>(((DatarangeSequence) expression).getAllDatarangeElements());
        sortElements(elements);
        for (DatarangeElement elem : elements)
            result.add(evalDatatype(elem.getDatarange()));
        return result;
    }

    /**
     * Gets the evaluated list for the specified sequence expression
     *
     * @param expression A sequence expresison
     * @return The evaluated list
     */
    private List<org.xowl.infra.lang.runtime.Literal> toEvaluatedList(LiteralSequenceExpression expression) {
        List<org.xowl.infra.lang.runtime.Literal> result = new ArrayList<>();
        List<LiteralElement> elements = new ArrayList<>(((LiteralSequence) expression).getAllLiteralElements());
        sortElements(elements);
        for (LiteralElement elem : elements)
            result.add(evalLiteral(elem.getLiteral()));
        return result;
    }

    /**
     * Sorts the sequence elements by indices
     *
     * @param elements The elements to sort
     */
    private void sortElements(List<? extends SequenceElement> elements) {
        Collections.sort(elements, new Comparator<SequenceElement>() {
            @Override
            public int compare(SequenceElement left, SequenceElement right) {
                return Integer.compare(left.getIndex(), right.getIndex());
            }
        });
    }
}
