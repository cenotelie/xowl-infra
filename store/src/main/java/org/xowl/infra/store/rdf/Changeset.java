/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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
 ******************************************************************************/

package org.xowl.infra.store.rdf;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents a set of changes in a RDF database
 *
 * @author Laurent Wouters
 */
public class Changeset {
    /**
     * Create a changeset for added quads
     *
     * @param quads The added quads
     * @return The changeset
     */
    public static Changeset fromAdded(Collection<Quad> quads) {
        return new Changeset(Collections.emptyList(), Collections.emptyList(), quads, Collections.emptyList());
    }

    /**
     * Create a changeset for removed quads
     *
     * @param quads The removed quads
     * @return The changeset
     */
    public static Changeset fromRemoved(Collection<Quad> quads) {
        return new Changeset(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), quads);
    }

    /**
     * Creates a changeset for added and removed quads
     *
     * @param added   The added quads
     * @param removed The removed quads
     * @return The changeset
     */
    public static Changeset fromAddedRemoved(Collection<Quad> added, Collection<Quad> removed) {
        return new Changeset(Collections.emptyList(), Collections.emptyList(), added, removed);
    }

    /**
     * Creates a reversed changeset
     *
     * @param changeset The changeset to reverse
     * @return The reversed changeset
     */
    public static Changeset reverse(Changeset changeset) {
        return new Changeset(changeset.decremented, changeset.incremented, changeset.removed, changeset.added);
    }

    /**
     * The quads that are incremented
     */
    private final Collection<Quad> incremented;
    /**
     * The quads that are decremented
     */
    private final Collection<Quad> decremented;
    /**
     * The quads that are added
     */
    private final Collection<Quad> added;
    /**
     * The quads that are removed
     */
    private final Collection<Quad> removed;

    /**
     * Gets the quads that are incremented
     *
     * @return The quads that are incremented
     */
    public Collection<Quad> getIncremented() {
        return incremented;
    }

    /**
     * Gets the quads that are decremented
     *
     * @return The quads that are decremented
     */
    public Collection<Quad> getDecremented() {
        return decremented;
    }

    /**
     * Gets the quads that are added
     *
     * @return The quads that are added
     */
    public Collection<Quad> getAdded() {
        return added;
    }

    /**
     * Gets the quads that are removed
     *
     * @return The quads that are removed
     */
    public Collection<Quad> getRemoved() {
        return removed;
    }

    /**
     * Initializes this changeset
     *
     * @param incremented The quads that are incremented
     * @param decremented The quads that are decremented
     * @param positives   The quads that are added
     * @param negatives   The quads that are removed
     */
    public Changeset(Collection<Quad> incremented, Collection<Quad> decremented, Collection<Quad> positives, Collection<Quad> negatives) {
        this.incremented = Collections.unmodifiableCollection(incremented);
        this.decremented = Collections.unmodifiableCollection(decremented);
        this.added = Collections.unmodifiableCollection(positives);
        this.removed = Collections.unmodifiableCollection(negatives);
    }
}
