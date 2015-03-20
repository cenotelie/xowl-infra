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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents a set of changes in a RDF database
 *
 * @author Laurent Wouters
 */
public class Changeset {
    /**
     * The quads that are added
     */
    private Collection<Quad> positives;
    /**
     * The quads that are removed
     */
    private Collection<Quad> negatives;

    /**
     * Gets the quads that are added
     *
     * @return The quads that are added
     */
    public Collection<Quad> getPositives() {
        return Collections.unmodifiableCollection(positives);
    }

    /**
     * Gets the quads that are removed
     *
     * @return The quads that are removed
     */
    public Collection<Quad> getNegatives() {
        return Collections.unmodifiableCollection(negatives);
    }

    /**
     * Initializes this changeset
     *
     * @param positives The quads that are added
     * @param negatives The quads that are removed
     */
    public Changeset(Collection<Quad> positives, Collection<Quad> negatives) {
        this.positives = new ArrayList<>(positives);
        this.negatives = new ArrayList<>(negatives);
    }
}
