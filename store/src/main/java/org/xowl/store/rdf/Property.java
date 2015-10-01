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
 *     Stephen Creff - stephen.creff@gmail.com
 ******************************************************************************/

package org.xowl.store.rdf;

import org.xowl.store.Vocabulary;

/**
 * Represents a property in a RDF triple
 *
 * @author Laurent Wouters
 * modified to add default tests
 * @author Stephen Creff
 */
public interface Property extends Node {

    /**
     * Test whether the property is a rdf:First IRI one
     */
    default boolean isRdfFirst(){
        return (this.getNodeType() == Node.TYPE_IRI) && Vocabulary.rdfFirst.equals(((IRINode) this).getIRIValue());
    }
    /**
     * Test whether the property is a rdf:Rest IRI one
     */
    default boolean isRdfRest(){
        return (this.getNodeType() == Node.TYPE_IRI) && Vocabulary.rdfRest.equals(((IRINode) this).getIRIValue());
    }
    /**
     * Test whether the property is a rdf:Type IRI one
     */
    default boolean isRdfType(){
        return (this.getNodeType() == Node.TYPE_IRI) && Vocabulary.rdfType.equals(((IRINode) this).getIRIValue());
    }

}
