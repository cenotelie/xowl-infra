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
package org.xowl.infra.store.owl;

import org.xowl.infra.lang.owl2.Axiom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents the matching conditions of an OWL query
 *
 * @author Laurent Wouters
 */
public class OWLQuery {
    /**
     * The positive conditions
     */
    private final List<Axiom> positives;
    /**
     * The list of conjunctive negative conditions
     */
    private final List<Collection<Axiom>> negatives;

    /**
     * Initializes this condition
     */
    public OWLQuery() {
        this.positives = new ArrayList<>();
        this.negatives = new ArrayList<>();
    }

    /**
     * Gets the positive conditions
     *
     * @return The positive conditions of this rule
     */
    public Collection<Axiom> getPositives() {
        return positives;
    }

    /**
     * Gets all the negative conjunctions of conditions
     *
     * @return The negative conjunctions of conditions
     */
    public Collection<Collection<Axiom>> getNegatives() {
        return negatives;
    }
}
