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

import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.lang.owl2.IRI;

/**
 * Represents a node associated to an IRI in a RDF graph
 *
 * @author Laurent Wouters
 */
public class RDFIRIReference implements RDFSubjectNode, RDFProperty {
    /**
     * The key used to retrieve the IRI
     */
    private Key key;
    /**
     * The IRI associated to this node
     */
    private IRI iRI;

    RDFIRIReference(Key key, IRI iri) {
        this.key = key;
        this.iRI = iri;
    }

    /**
     * Gets the IRI associated to this node
     *
     * @return The IRI associated to this node
     */
    public IRI getIRI() {
        return iRI;
    }

    @Override
    public RDFNodeType getNodeType() {
        return RDFNodeType.IRIReference;
    }

    @Override
    public Key getStoreKey() {
        return key;
    }

    @Override
    public RDFLiteral getLiteralValue() {
        return null;
    }

    @Override
    public int getBlankID() {
        return 0;
    }

    @Override
    public AnonymousIndividual getAnonymous() {
        return null;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RDFIRIReference) {
            RDFIRIReference node = (RDFIRIReference) obj;
            return (key == node.key);
        }
        return false;
    }

    @Override
    public String toString() {
        return iRI.getHasValue();
    }
}
