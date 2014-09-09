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

package org.xowl.store.query;

import org.xowl.store.rdf.Change;
import org.xowl.store.rdf.ChangeListener;
import org.xowl.store.rdf.Changeset;
import org.xowl.store.rdf.RDFStore;
import org.xowl.store.rete.RETENetwork;

import java.io.IOException;

/**
 * Represents a query engine for a RDF store
 */
public abstract class Engine implements ChangeListener {
    /**
     * The RDF store to query
     */
    protected RDFStore store;
    /**
     * A RETE network for the pattern matching of queries
     */
    protected RETENetwork rete;


    public Engine(RDFStore store) throws IOException {
        this.store = store;
        this.rete = new RETENetwork();
    }

    @Override
    public void onChange(Change change) {

    }

    @Override
    public void onChange(Changeset changeset) {

    }
}
