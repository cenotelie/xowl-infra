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

import org.xowl.store.rdf.*;
import org.xowl.store.rete.RETENetwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    /**
     * The new changes since the last application
     */
    protected List<Change> newChanges;
    /**
     * The new changesets since the last application
     */
    protected List<Changeset> newChangesets;
    /**
     * Flag whether outstanding changes are currently being applied
     */
    protected boolean isApplying;
    /**
     * Buffer of positive quads
     */
    protected Collection<Quad> bufferPositives;
    /**
     * Buffer of negative quads
     */
    protected Collection<Quad> bufferNegatives;

    /**
     * Initializes this engine
     *
     * @param store The RDF store to query
     */
    public Engine(RDFStore store) {
        this.store = store;
        this.rete = new RETENetwork(store);
        this.newChanges = new ArrayList<>();
        this.newChangesets = new ArrayList<>();
        this.bufferPositives = new ArrayList<>();
        this.bufferNegatives = new ArrayList<>();
        this.store.addListener(this);
    }

    /**
     * Executes the specified query and gets the solutions
     *
     * @param query A query
     * @return The solutions
     */
    public abstract Collection<Solution> execute(Query query);

    @Override
    public void onChange(Change change) {
        newChanges.add(change);
    }

    @Override
    public void onChange(Changeset changeset) {
        newChangesets.add(changeset);
    }

    /**
     * Applies all outstanding changes
     */
    protected void apply() {
        if (isApplying)
            return;
        isApplying = true;
        while (newChanges.size() > 0 || newChangesets.size() > 0) {
            for (Change change : newChanges) {
                if (change.isPositive())
                    bufferPositives.add(change.getValue());
                else
                    bufferNegatives.add(change.getValue());
            }
            newChanges.clear();
            for (Changeset changeset : newChangesets) {
                bufferPositives.addAll(changeset.getPositives());
                bufferNegatives.addAll(changeset.getNegatives());
            }
            newChangesets.clear();
            rete.injectPositives(bufferPositives);
            rete.injectNegatives(bufferNegatives);
            bufferPositives.clear();
            bufferNegatives.clear();
        }
        isApplying = false;
    }
}
