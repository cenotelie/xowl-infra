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

package org.xowl.store.rdf;

import org.xowl.lang.owl2.Ontology;

/**
 * Represents a triple in an ontology
 *
 * @author Laurent Wouters
 */
public class Triple {
    /**
     * The containing ontology
     */
    private Ontology ontology;
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
     * Initializes this triple fact
     *
     * @param ontology The containing ontology
     * @param subject  The subject node
     * @param property The property
     * @param object   The object node
     */
    public Triple(Ontology ontology, SubjectNode subject, Property property, Node object) {
        this.ontology = ontology;
        this.subject = subject;
        this.property = property;
        this.object = object;
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
     * Sets the containing ontology
     *
     * @param ontology The containing ontology
     */
    void setOntology(Ontology ontology) {
        this.ontology = ontology;
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
    public Node getField(TripleField field) {
        switch (field) {
            case SUBJECT:
                return subject;
            case PROPERTY:
                return property;
            case VALUE:
                return object;
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.subject != null ? this.subject.hashCode() : 0);
        hash = 53 * hash + (this.property != null ? this.property.hashCode() : 0);
        hash = 53 * hash + (this.object != null ? this.object.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Triple) {
            Triple triple = (Triple) obj;
            if (!ontology.equals(triple.ontology)) return false;
            if (!subject.equals(triple.subject)) return false;
            if (!property.equals(triple.property)) return false;
            return (object.equals(triple.object));
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(subject.toString());
        builder.append(" ");
        builder.append(property.toString());
        builder.append(" ");
        builder.append(object.toString());
        builder.append("]");
        return builder.toString();
    }
}
