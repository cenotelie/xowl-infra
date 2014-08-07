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

/**
 * Represents a triple in RDF graph
 *
 * @author Laurent Wouters
 */
public class RDFTriple {
    /**
     * The subject node
     */
    private RDFSubjectNode subject;
    /**
     * The property
     */
    private RDFProperty property;
    /**
     * The object node
     */
    private RDFNode object;

    /**
     * Initializes this triple
     *
     * @param subject  The subject node
     * @param property The property
     * @param object   The object node
     */
    public RDFTriple(RDFSubjectNode subject, RDFProperty property, RDFNode object) {
        this.subject = subject;
        this.property = property;
        this.object = object;
    }

    /**
     * Gets the subject node
     *
     * @return The subject node
     */
    public RDFSubjectNode getSubject() {
        return subject;
    }

    /**
     * Gets the property
     *
     * @return The property
     */
    public RDFProperty getProperty() {
        return property;
    }

    /**
     * Gets the object node
     *
     * @return The object node
     */
    public RDFNode getObject() {
        return object;
    }

    /**
     * Gets the value of the specified field
     *
     * @param field A field
     * @return The value of the specified field
     */
    public RDFNode getField(RDFTripleField field) {
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
        if (obj instanceof RDFTriple) {
            RDFTriple triple = (RDFTriple) obj;
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