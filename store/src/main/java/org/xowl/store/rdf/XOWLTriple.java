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
import org.xowl.store.rdf.RDFNode;
import org.xowl.store.rdf.RDFProperty;
import org.xowl.store.rdf.RDFSubjectNode;
import org.xowl.store.rdf.RDFTriple;

/**
 * Represents a triple fact in a RETE graph
 *
 * @author Laurent Wouters
 */
public class XOWLTriple extends RDFTriple {
    /**
     * The containing ontology
     */
    private Ontology ontology;

    /**
     * Initializes this triple fact
     *
     * @param ontology The containing ontology
     * @param subject  The subject node
     * @param property The property
     * @param object   The object node
     */
    public XOWLTriple(Ontology ontology, RDFSubjectNode subject, RDFProperty property, RDFNode object) {
        super(subject, property, object);
        this.ontology = ontology;
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
}
