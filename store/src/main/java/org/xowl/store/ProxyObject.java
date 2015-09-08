/*******************************************************************************
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
 ******************************************************************************/
package org.xowl.store;

import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.owl.DynamicNode;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.NodeManager;
import org.xowl.store.storage.UnsupportedNodeType;
import org.xowl.utils.collections.Adapter;
import org.xowl.utils.collections.AdaptingIterator;
import org.xowl.utils.collections.Couple;
import org.xowl.utils.collections.SkippableIterator;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

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
     * The represented entity as a subject RDF node
     */
    protected SubjectNode subject;

    /**
     * Initializes this object
     *
     * @param repository The parent repository
     * @param ontology   The containing ontology
     * @param subject    The represented entity as a subject RDF node
     */
    protected ProxyObject(Repository repository, Ontology ontology, SubjectNode subject) {
        this.repository = repository;
        this.ontology = ontology;
        this.subject = subject;
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
    protected SubjectNode getNode() {
        return subject;
    }

    /**
     * Gets the IRI of this object
     *
     * @return The IRI of this object
     */
    public IRI getIRI() {
        return subject.getNodeType() == IRINode.TYPE ? repository.getIRI(((IRINode) subject).getIRIValue()) : null;
    }

    /**
     * Gets the value of the IRI of this object
     *
     * @return The value of the IRI of this object
     */
    public String getIRIString() {
        return subject.getNodeType() == IRINode.TYPE ? ((IRINode) subject).getIRIValue() : null;
    }

    /**
     * Gets the classifiers of this object
     *
     * @return The classifiers of this object
     */
    public Collection<ProxyObject> getClassifiers() {
        return queryObjects(node(Vocabulary.rdfType));
    }

    /**
     * Gets the instances of this objects, i.e. objects classified by this object
     *
     * @return the instances of this object
     */
    public Collection<ProxyObject> getInstances() {
        return queryInverseObjects(node(Vocabulary.rdfType));
    }

    /**
     * Gets the value for the specified object property
     *
     * @param property An object property
     * @return The value
     */
    public ProxyObject getObjectValue(String property) {
        Collection<ProxyObject> result = queryObjects(node(property));
        if (result.isEmpty())
            return null;
        return result.iterator().next();
    }

    /**
     * Gets the object for which this one is a value for the specified property
     *
     * @param property An object property
     * @return The object
     */
    public ProxyObject getObjectFrom(String property) {
        Collection<ProxyObject> result = queryInverseObjects(node(property));
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
        Collection<Object> result = queryData(node(property));
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
        return queryObjects(node(property));
    }

    /**
     * Gets the values for all the set object properties
     *
     * @return The couple of (property IRI, value)
     */
    public Collection<Couple<String, ProxyObject>> getObjectValues() {
        List<Couple<String, ProxyObject>> result = new ArrayList<>();
        Iterator<Couple<String, Object>> iterator = queryProperties();
        while (iterator.hasNext()) {
            Couple<String, Object> couple = iterator.next();
            if (couple.y instanceof ProxyObject)
                result.add(new Couple<>(couple.x, (ProxyObject) couple.y));
        }
        return result;
    }

    /**
     * Gets the objects for which this one is a value for the specified property
     *
     * @param property An object property
     * @return The objects
     */
    public Collection<ProxyObject> getObjectsFrom(String property) {
        return queryInverseObjects(node(property));
    }

    /**
     * Gets the values for the specified data property
     *
     * @param property A data property
     * @return The values
     */
    public Collection<Object> getDataValues(String property) {
        return queryData(node(property));
    }

    /**
     * Gets the values for all the set data properties
     *
     * @return The couple of (property IRI, value)
     */
    public Collection<Couple<String, Object>> getDataValues() {
        List<Couple<String, Object>> result = new ArrayList<>();
        Iterator<Couple<String, Object>> iterator = queryProperties();
        while (iterator.hasNext()) {
            Couple<String, Object> couple = iterator.next();
            if (!(couple.y instanceof ProxyObject))
                result.add(new Couple<>(couple.x, couple.y));
        }
        return result;
    }

    /**
     * Gets the values for all the set properties
     *
     * @return The couple of (property IRI, value)
     */
    public Collection<Couple<String, Object>> getValues() {
        List<Couple<String, Object>> result = new ArrayList<>();
        Iterator<Couple<String, Object>> iterator = queryProperties();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    /**
     * Sets the value of the property
     *
     * @param property The property
     * @param value    The new value
     */
    public void setValue(String property, ProxyObject value) {
        IRINode propertyNode = node(property);
        if (isFunctional(propertyNode)) {
            removeAllValues(propertyNode);
        }
        addValue(propertyNode, value.subject);
    }

    /**
     * Sets the value of the property
     *
     * @param property The property
     * @param value    The new value
     */
    public void setValue(String property, Object value) {
        IRINode propertyNode = node(property);
        if (isFunctional(propertyNode)) {
            removeAllValues(propertyNode);
        }
        addValue(propertyNode, encode(propertyNode, value));
    }

    /**
     * Unsets all values of the property
     *
     * @param property The property
     */
    public void unset(String property) {
        removeAllValues(node(property));
    }

    /**
     * Unsets the value of a property
     *
     * @param property The property
     * @param value    The value
     */
    public void unset(String property, ProxyObject value) {
        removeValue(node(property), value.subject);
    }

    /**
     * Unsets the value of a property
     *
     * @param property The property
     * @param value    The value
     */
    public void unset(String property, Object value) {
        IRINode propertyNode = node(property);
        removeValue(propertyNode, encode(propertyNode, value));
    }

    /**
     * Deletes this object from the repository
     */
    public void delete() {
        List<Quad> toRemove = new ArrayList<>();
        try {
            // get all triple of the form
            // [entity ? ?]
            Iterator<Quad> iterator = repository.getStore().getAll(subject, null, null);
            while (iterator.hasNext()) {
                toRemove.add(iterator.next());
            }
            // get all triple of the form
            // [? ? entity]
            iterator = repository.getStore().getAll(null, null, subject);
            while (iterator.hasNext()) {
                toRemove.add(iterator.next());
            }
            // remove
            repository.getStore().insert(new Changeset(new ArrayList<Quad>(0), toRemove));
        } catch (UnsupportedNodeType ex) {
            // cannot happen
        }
        // clear the internal data
        repository.remove(this);
        repository = null;
        ontology = null;
        subject = null;
    }

    /**
     * Queries all the set properties
     *
     * @return The properties and the values
     */
    private Iterator<Couple<String, Object>> queryProperties() {
        return new SkippableIterator<>(new AdaptingIterator<>(repository.getStore().getAll(subject, null, null), new Adapter<Couple<String, Object>>() {
            @Override
            public <X> Couple<String, Object> adapt(X element) {
                Quad quad = (Quad) element;
                Node nodeProperty = quad.getProperty();
                if (nodeProperty.getNodeType() != IRINode.TYPE)
                    return null;
                String property = ((IRINode) nodeProperty).getIRIValue();
                Node nodeValue = quad.getObject();
                if (nodeValue.getNodeType() == IRINode.TYPE) {
                    return new Couple<String, Object>(property, repository.resolveProxy(((IRINode) nodeValue).getIRIValue()));
                } else if (nodeValue.getNodeType() == LiteralNode.TYPE) {
                    return new Couple<>(property, decode((LiteralNode) nodeValue));
                }
                return null;
            }
        }));
    }

    /**
     * Queries the values for the specified property
     *
     * @param property The RDF node for the property
     * @return The corresponding values
     */
    private Collection<ProxyObject> queryObjects(IRINode property) {
        Collection<ProxyObject> result = new ArrayList<>();
        // get all triple of the form
        // [entity property ?]
        Iterator<Quad> iterator = repository.getStore().getAll(subject, property, null);
        while (iterator.hasNext()) {
            Node node = iterator.next().getObject();
            if (node.getNodeType() == IRINode.TYPE) {
                result.add(repository.resolveProxy(((IRINode) node).getIRIValue()));
            } else if (node.getNodeType() == BlankNode.TYPE) {
                result.add(repository.resolveProxy(repository.getOntology(NodeManager.DEFAULT_GRAPH), (SubjectNode) node));
            }
        }
        return result;
    }

    /**
     * Queries the object for which this on is a value for the specified property
     *
     * @param property The RDF node for the property
     * @return The corresponding objects
     */
    private Collection<ProxyObject> queryInverseObjects(IRINode property) {
        Collection<ProxyObject> result = new ArrayList<>();
        // get all triple of the form
        // [entity property ?]
        Iterator<Quad> iterator = repository.getStore().getAll(null, property, subject);
        while (iterator.hasNext()) {
            Node node = iterator.next().getSubject();
            if (node.getNodeType() == IRINode.TYPE) {
                result.add(repository.resolveProxy(((IRINode) node).getIRIValue()));
            } else if (node.getNodeType() == BlankNode.TYPE) {
                result.add(repository.resolveProxy(repository.getOntology(NodeManager.DEFAULT_GRAPH), (SubjectNode) node));
            }
        }
        return result;
    }

    /**
     * Queries the values for the specified property
     *
     * @param property The RDF node for the property
     * @return The corresponding values
     */
    private Collection<Object> queryData(IRINode property) {
        Collection<Object> result = new ArrayList<>();
        // get all triple of the form
        // [entity property ?]
        Iterator<Quad> iterator = repository.getStore().getAll(subject, property, null);
        while (iterator.hasNext()) {
            Node node = iterator.next().getObject();
            switch (node.getNodeType()) {
                case LiteralNode.TYPE:
                    result.add(decode((LiteralNode) node));
                    break;
                case DynamicNode.TYPE:
                    result.add(((DynamicNode) node).getDynamicExpression());
                    break;
            }
        }
        return result;
    }

    /**
     * Adds the triple for the specified property and value
     *
     * @param property The RDF node for the property
     * @param value    The value
     */
    private void addValue(IRINode property, Node value) {
        try {
            repository.getStore().add(repository.getGraph(ontology), subject, property, value);
        } catch (UnsupportedNodeType ex) {
            // cannot happen
        }
    }

    /**
     * Removes the triple for the specified property and value
     *
     * @param property The RDF node for the property
     * @param value    The value
     */
    private void removeValue(IRINode property, Node value) {
        try {
            repository.getStore().remove(repository.getGraph(ontology), subject, property, value);
        } catch (UnsupportedNodeType ex) {
            // cannot happen
        }
    }

    /**
     * Removes all the triple for the specified property
     *
     * @param property The RDF node for the property
     */
    private void removeAllValues(IRINode property) {
        try {
            // get all triple of the form
            // [entity property ?]
            Iterator<Quad> iterator = repository.getStore().getAll(subject, property, null);
            List<Quad> toRemove = new ArrayList<>();
            while (iterator.hasNext()) {
                toRemove.add(iterator.next());
            }
            repository.getStore().insert(new Changeset(new ArrayList<Quad>(0), toRemove));
        } catch (UnsupportedNodeType ex) {
            // cannot happen
        }
    }

    /**
     * Gets whether the specified property is functional
     *
     * @param property The RDF node for the property
     * @return true if the property is functional, false otherwise
     */
    private boolean isFunctional(IRINode property) {
        // do we have the triple:
        // [property rdf:type owl:FunctionalProperty]
        long count = repository.getStore().count(null, property, node(Vocabulary.rdfType), node(Vocabulary.owlFunctionalProperty));
        return count > 1;
    }

    /**
     * Gets the RDF IRI node for the specified IRI
     *
     * @param iri An IRI
     * @return The associated IRI node
     */
    private IRINode node(String iri) {
        return repository.getStore().getIRINode(iri);
    }

    /**
     * Gets the data corresponding to the specified node
     *
     * @param node A RDF Literal node
     * @return The corresponding data
     */
    private Object decode(LiteralNode node) {
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
            case Vocabulary.xsdDuration:
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
    private LiteralNode encode(IRINode property, Object value) {
        String range = getRangeOf(property);
        switch (range) {
            case Vocabulary.xsdTime:
            case Vocabulary.xsdDateTime:
            case Vocabulary.xsdDate:
                return repository.getStore().getLiteralNode(DateFormat.getInstance().format((Date) value), range, null);
            case Vocabulary.xsdBoolean:
                return repository.getStore().getLiteralNode(Boolean.toString((boolean) value), range, null);
            case Vocabulary.xsdDecimal:
                return repository.getStore().getLiteralNode(DecimalFormat.getInstance().format(value), range, null);
            case Vocabulary.xsdFloat:
                return repository.getStore().getLiteralNode(Float.toString((float) value), range, null);
            case Vocabulary.xsdDouble:
                return repository.getStore().getLiteralNode(Double.toString((double) value), range, null);
            case Vocabulary.xsdDuration:
            case Vocabulary.xsdUnsignedLong:
            case Vocabulary.xsdLong:
                return repository.getStore().getLiteralNode(Long.toString((long) value), range, null);
            case Vocabulary.xsdNegativeInteger:
            case Vocabulary.xsdPositiveInteger:
            case Vocabulary.xsdNonPositiveinteger:
            case Vocabulary.xsdNonNegativeInteger:
            case Vocabulary.xsdUnsignedInteger:
            case Vocabulary.xsdInteger:
            case Vocabulary.xsdInt:
                return repository.getStore().getLiteralNode(Integer.toString((int) value), range, null);
            case Vocabulary.xsdUnsignedShort:
            case Vocabulary.xsdShort:
                return repository.getStore().getLiteralNode(Short.toString((short) value), range, null);
            case Vocabulary.xsdUnsigedByte:
            case Vocabulary.xsdByte:
                return repository.getStore().getLiteralNode(Byte.toString((byte) value), range, null);
            default:
                return repository.getStore().getLiteralNode(value.toString(), range, null);
        }
    }

    /**
     * Gets the range of the specified property
     *
     * @param property The RDF node for the property
     * @return The range's IRI
     */
    private String getRangeOf(IRINode property) {
        // get all the triple like
        // [property rdfs:range ?]
        Iterator<Quad> iterator = repository.getStore().getAll(property, node(Vocabulary.rdfsRange), null);
        if (!iterator.hasNext())
            // range is undefined, return xsd:String
            return Vocabulary.xsdString;
        Node rangeNode = iterator.next().getObject();
        if (rangeNode.getNodeType() == IRINode.TYPE)
            return ((IRINode) rangeNode).getIRIValue();
        // range is defined, but is either a blank, or an anonymous node, return xsd:String
        return Vocabulary.xsdString;
    }

    @Override
    public int hashCode() {
        return subject.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ProxyObject) {
            ProxyObject proxy = (ProxyObject) object;
            return this.subject == proxy.subject;
        }
        return false;
    }

    @Override
    public String toString() {
        return subject.toString();
    }
}
