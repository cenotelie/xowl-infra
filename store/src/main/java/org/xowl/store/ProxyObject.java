/**********************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
package org.xowl.store;

import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.rdf.*;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Represents an object in an ontology
 *
 * @author Laurent Wouters
 */
public class ProxyObject {
    /**
     * The parent repository
     */
    protected Repository repository;
    /**
     * The containing ontology
     */
    protected Ontology ontology;
    /**
     * The represented entity as an RDF IRI node
     */
    protected IRINode entity;

    /**
     * Initializes this object
     *
     * @param repository The parent repository
     * @param ontology   The containing ontology
     * @param entity     The represented entity as an RDF IRI node
     */
    protected ProxyObject(Repository repository, Ontology ontology, IRINode entity) {
        this.repository = repository;
        this.ontology = ontology;
        this.entity = entity;
    }

    /**
     * Gets the parent repository
     *
     * @return The parent repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Gets the containing ontology
     *
     * @return The containing ontology
     */
    public Ontology getOntology() {
        return ontology;
    }

    /**
     * Gets the represented entity as an RDF IRI node
     *
     * @return The represented entity as an RDF IRI node
     */
    protected IRINode getNode() {
        return entity;
    }

    /**
     * Gets the IRI of this object
     *
     * @return The IRI of this object
     */
    public IRI getIRI() {
        return repository.getIRI(entity.getIRIValue());
    }

    /**
     * Gets the value of the IRI of this object
     *
     * @return The value of the IRI of this object
     */
    public String getIRIString() {
        return entity.getIRIValue();
    }

    /**
     * Gets the classifiers of this object
     *
     * @return The classifiers of this object
     */
    public Collection<ProxyObject> getClassifiers() {
        return queryObjects(Vocabulary.rdfType);
    }

    /**
     * Gets the value for the specified object property
     *
     * @param property An object property
     * @return The value
     */
    public ProxyObject getObjectValue(String property) {
        Collection<ProxyObject> result = queryObjects(property);
        if (result.isEmpty())
            return null;
        return result.iterator().next();
    }

    /**
     * Gets the value for the specified data property
     *
     * @param property A data property
     * @return The value
     */
    public Object getDataValue(String property) {
        Collection<Object> result = queryData(property);
        if (result.isEmpty())
            return null;
        return result.iterator().next();
    }

    /**
     * Gets the values for the specified object property
     *
     * @param property An object property
     * @return The values
     */
    public Collection<ProxyObject> getObjectValues(String property) {
        return queryObjects(property);
    }

    /**
     * Gets the values for the specified data property
     *
     * @param property A data property
     * @return The values
     */
    public Collection<Object> getDataValues(String property) {
        return queryData(property);
    }

    /**
     * Sets the value of the property
     *
     * @param property The property
     * @param value    The new value
     */
    public void set(String property, ProxyObject value) {
        set(property, value.entity);
    }

    /**
     * Sets the value of the property
     *
     * @param property The property
     * @param value    The new value
     */
    public void set(String property, Object value) {
        set(property, encode(property, value));
    }

    /**
     * Deletes this object from the repository
     */
    public void delete() {
        List<Quad> toRemove = new ArrayList<>();

        VariableNode varGraph = new VariableNode("graph");
        VariableNode varProperty = new VariableNode("property");
        VariableNode varValue = new VariableNode("value");

        Quad pattern = new Quad(varGraph, entity, varProperty, varValue);
        Query query = new Query();
        query.getPositives().add(pattern);
        Collection<QuerySolution> solutions = repository.getQueryEngine().getRDFBackend().execute(query);
        for (QuerySolution solution : solutions) {
            toRemove.add(new Quad((GraphNode) solution.get(varGraph), entity, (Property) solution.get(varProperty), solution.get(varValue)));
        }

        pattern = new Quad(varGraph, varValue, varProperty, entity);
        query = new Query();
        query.getPositives().add(pattern);
        solutions = repository.getQueryEngine().getRDFBackend().execute(query);
        for (QuerySolution solution : solutions) {
            toRemove.add(new Quad((GraphNode) solution.get(varGraph), (SubjectNode) solution.get(varValue), (Property) solution.get(varProperty), entity));
        }

        try {
            repository.getBackend().insert(new Changeset(new ArrayList<Quad>(0), toRemove));
        } catch (UnsupportedNodeType ex) {
            // cannot happen
        }
        repository.remove(this);
        repository = null;
        ontology = null;
        entity = null;
    }

    /**
     * Queries the values for the specified property
     *
     * @param propertyIRI The IRI of a property
     * @return The corresponding values
     */
    private Collection<ProxyObject> queryObjects(String propertyIRI) {
        VariableNode varGraph = new VariableNode("graph");
        VariableNode varValue = new VariableNode("value");
        Quad pattern = new Quad(varGraph, entity, repository.getBackend().getNodeIRI(propertyIRI), varValue);
        Query query = new Query();
        query.getPositives().add(pattern);
        Collection<QuerySolution> solutions = repository.getQueryEngine().getRDFBackend().execute(query);
        Collection<ProxyObject> result = new ArrayList<>(solutions.size());
        for (QuerySolution solution : solutions) {
            Node node = solution.get(varValue);
            if (node.getNodeType() == IRINode.TYPE) {
                result.add(repository.getProxy(((IRINode) node).getIRIValue()));
            }
        }
        return result;
    }

    /**
     * Queries the values for the specified property
     *
     * @param propertyIRI The IRI of a property
     * @return The corresponding values
     */
    private Collection<Object> queryData(String propertyIRI) {
        VariableNode varGraph = new VariableNode("graph");
        VariableNode varValue = new VariableNode("value");
        Quad pattern = new Quad(varGraph, entity, repository.getBackend().getNodeIRI(propertyIRI), varValue);
        Query query = new Query();
        query.getPositives().add(pattern);
        Collection<QuerySolution> solutions = repository.getQueryEngine().getRDFBackend().execute(query);
        Collection<Object> result = new ArrayList<>(solutions.size());
        for (QuerySolution solution : solutions) {
            Node node = solution.get(varValue);
            if (node.getNodeType() == LiteralNode.TYPE) {
                result.add(retrieve((LiteralNode) node));
            }
        }
        return result;
    }

    /**
     * Sets the value of the specified property
     *
     * @param property The property's IRI
     * @param value    The value to set
     */
    private void set(String property, Node value) {
        // check whether the property is functional
        // TODO: implement
    }

    /**
     * Gets the data corresponding to the specified node
     *
     * @param node A RDF Literal node
     * @return The corresponding data
     */
    private Object retrieve(LiteralNode node) {
        String lexicalValue = node.getLexicalValue();
        switch (node.getDatatype()) {
            case Vocabulary.xsdTime:
            case Vocabulary.xsdDateTime:
            case Vocabulary.xsdDate:
                try {
                    return DateFormat.getInstance().parse(lexicalValue);
                } catch (ParseException ex) {
                    return null;
                }
            case Vocabulary.xsdDuration:
                return Duration.parse(lexicalValue);
            case Vocabulary.xsdBoolean:
                return Boolean.parseBoolean(lexicalValue);
            case Vocabulary.xsdDecimal:
                try {
                    return DecimalFormat.getInstance().parse(lexicalValue);
                } catch (ParseException ex) {
                    return null;
                }
            case Vocabulary.xsdFloat:
                return Float.parseFloat(lexicalValue);
            case Vocabulary.xsdDouble:
                return Double.parseDouble(lexicalValue);
            case Vocabulary.xsdUnsignedLong:
            case Vocabulary.xsdLong:
                return Long.parseLong(lexicalValue);
            case Vocabulary.xsdNegativeInteger:
            case Vocabulary.xsdPositiveInteger:
            case Vocabulary.xsdNonPositiveinteger:
            case Vocabulary.xsdNonNegativeInteger:
            case Vocabulary.xsdUnsignedInteger:
            case Vocabulary.xsdInteger:
            case Vocabulary.xsdInt:
                return Integer.parseInt(lexicalValue);
            case Vocabulary.xsdUnsignedShort:
            case Vocabulary.xsdShort:
                return Short.parseShort(lexicalValue);
            case Vocabulary.xsdUnsigedByte:
            case Vocabulary.xsdByte:
                return Byte.parseByte(lexicalValue);
            default:
                return lexicalValue;
        }
    }

    /**
     * Encodes a value as a literal node
     *
     * @param property The target property
     * @param value    A data value
     * @return The literal node
     */
    private LiteralNode encode(String property, Object value) {
        String range = getRangeOf(property);
        switch (range) {
            case Vocabulary.xsdTime:
            case Vocabulary.xsdDateTime:
            case Vocabulary.xsdDate:
                return repository.getBackend().getLiteralNode(DateFormat.getInstance().format((Date) value), range, null);
            case Vocabulary.xsdDuration:
                return repository.getBackend().getLiteralNode(value.toString(), range, null);
            case Vocabulary.xsdBoolean:
                return repository.getBackend().getLiteralNode(Boolean.toString((boolean) value), range, null);
            case Vocabulary.xsdDecimal:
                return repository.getBackend().getLiteralNode(DecimalFormat.getInstance().format(value), range, null);
            case Vocabulary.xsdFloat:
                return repository.getBackend().getLiteralNode(Float.toString((float) value), range, null);
            case Vocabulary.xsdDouble:
                return repository.getBackend().getLiteralNode(Double.toString((double) value), range, null);
            case Vocabulary.xsdUnsignedLong:
            case Vocabulary.xsdLong:
                return repository.getBackend().getLiteralNode(Long.toString((long) value), range, null);
            case Vocabulary.xsdNegativeInteger:
            case Vocabulary.xsdPositiveInteger:
            case Vocabulary.xsdNonPositiveinteger:
            case Vocabulary.xsdNonNegativeInteger:
            case Vocabulary.xsdUnsignedInteger:
            case Vocabulary.xsdInteger:
            case Vocabulary.xsdInt:
                return repository.getBackend().getLiteralNode(Integer.toString((int) value), range, null);
            case Vocabulary.xsdUnsignedShort:
            case Vocabulary.xsdShort:
                return repository.getBackend().getLiteralNode(Short.toString((short) value), range, null);
            case Vocabulary.xsdUnsigedByte:
            case Vocabulary.xsdByte:
                return repository.getBackend().getLiteralNode(Byte.toString((byte) value), range, null);
            default:
                return repository.getBackend().getLiteralNode(value.toString(), range, null);
        }
    }

    /**
     * Gets the range of the specified property
     *
     * @param property A property
     * @return The range's IRI
     */
    private String getRangeOf(String property) {
        VariableNode varGraph = new VariableNode("graph");
        VariableNode varValue = new VariableNode("value");
        Quad pattern = new Quad(varGraph, repository.getBackend().getNodeIRI(property), repository.getBackend().getNodeIRI(Vocabulary.rdfsRange), varValue);
        Query query = new Query();
        query.getPositives().add(pattern);
        Collection<QuerySolution> solutions = repository.getQueryEngine().getRDFBackend().execute(query);
        for (QuerySolution solution : solutions) {
            Node node = solution.get(varValue);
            if (node.getNodeType() == IRINode.TYPE) {
                return ((IRINode) node).getIRIValue();
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        return entity.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ProxyObject) {
            ProxyObject proxy = (ProxyObject) object;
            return this.entity == proxy.entity;
        }
        return false;
    }

    @Override
    public String toString() {
        return entity.getIRIValue();
    }
}
