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
 * Represents a listener of changes on a RDF dataset
 *
 * @author Laurent Wouters
 */
public interface ChangeListener {
    /**
     * Reacts to the specified quad being incremented (not a new quad)
     *
     * @param quad The incremented quad
     */
    void onIncremented(Quad quad);

    /**
     * Reacts to the specified quad being decremented (not removed yet)
     *
     * @param quad The decremented quad
     */
    void onDecremented(Quad quad);

    /**
     * Reacts to a new quad being added
     *
     * @param quad The new quad
     */
    void onAdded(Quad quad);

    /**
     * Reacts to a quad being removed
     *
     * @param quad The removed quad
     */
    void onRemoved(Quad quad);

    /**
     * Reacts to the specified changeset
     *
     * @param changeset A changeset
     */
    void onChange(Changeset changeset);
}
