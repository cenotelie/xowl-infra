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

package org.xowl.store.rdf;

/**
 * Represents a triple in an graph
 *
 * @author Laurent Wouters
 */
public class Quad {
    /**
     * The containing graph
     */
    private GraphNode graph;
    /**
     * The subject node
     */
    private SubjectNode subject;
    /**
     * The property
     */
    private Property property;
    /**
     * The object node
     */
    private Node object;

    /**
     * Initializes this quad
     *
     * @param graph    The containing graph
     * @param subject  The subject node
     * @param property The property
     * @param object   The object node
     */
    public Quad(GraphNode graph, SubjectNode subject, Property property, Node object) {
        this.graph = graph;
        this.subject = subject;
        this.property = property;
        this.object = object;
    }

    /**
     * Gets the containing graph
     *
     * @return The containing graph
     */
    public GraphNode getGraph() {
        return graph;
    }

    /**
     * Gets the subject node
     *
     * @return The subject node
     */
    public SubjectNode getSubject() {
        return subject;
    }

    /**
     * Sets the subject node
     *
     * @param subject The subject node
     */
    void setSubject(SubjectNode subject) {
        this.subject = subject;
    }

    /**
     * Gets the property
     *
     * @return The property
     */
    public Property getProperty() {
        return property;
    }

    /**
     * Gets the property
     *
     * @param property The property
     */
    void setProperty(Property property) {
        this.property = property;
    }

    /**
     * Gets the object node
     *
     * @return The object node
     */
    public Node getObject() {
        return object;
    }

    /**
     * Sets the object node
     *
     * @param object The object node
     */
    void setObject(Node object) {
        this.object = object;
    }

    /**
     * Gets the value of the specified field
     *
     * @param field A field
     * @return The value of the specified field
     */
    public Node getField(QuadField field) {
        switch (field) {
            case SUBJECT:
                return subject;
            case PROPERTY:
                return property;
            case VALUE:
                return object;
            case GRAPH:
                return graph;
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.graph != null ? this.graph.hashCode() : 0);
        hash = 53 * hash + (this.subject != null ? this.subject.hashCode() : 0);
        hash = 53 * hash + (this.property != null ? this.property.hashCode() : 0);
        hash = 53 * hash + (this.object != null ? this.object.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Quad) {
            Quad quad = (Quad) obj;
            if (!graph.equals(quad.graph)) return false;
            if (!subject.equals(quad.subject)) return false;
            if (!property.equals(quad.property)) return false;
            return (object.equals(quad.object));
        }
        return false;
    }

    @Override
    public String toString() {
        return "[" + subject.toString() + " " + property.toString() + " " + object.toString() + "]";
    }
}
