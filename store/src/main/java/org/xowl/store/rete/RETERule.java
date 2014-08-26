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

package org.xowl.store.rete;

import org.xowl.store.rdf.RDFTriple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a rule to compile in a RETE network
 *
 * @author Laurent Wouters
 */
public class RETERule {
    /**
     * The positive conditions to this rule
     */
    private List<RDFTriple> positives;
    /**
     * The list of conjunctive negative conditions to this rule
     */
    private List<Collection<RDFTriple>> negatives;
    /**
     * The output node in the RETE network for elements matching this rule
     */
    private TokenActivable output;

    /**
     * Initializes this rule
     *
     * @param output The output node in the RETE network for elements matching this rule
     */
    public RETERule(TokenActivable output) {
        this.positives = new ArrayList<>();
        this.negatives = new ArrayList<>();
        this.output = output;
    }

    /**
     * Adds a positive condition to this rule
     *
     * @param condition A positive condition
     */
    public void addPositiveCondition(RDFTriple condition) {
        positives.add(condition);
    }

    /**
     * Adds a negative set of conjunctive conditions to this rule
     *
     * @param conditions A set of conjunctive conditions
     */
    public void addNegativeConsitions(Collection<RDFTriple> conditions) {
        negatives.add(conditions);
    }

    /**
     * Gets the positive conditions of this rule
     *
     * @return The positive conditions of this rule
     */
    public Collection<RDFTriple> getPositives() {
        return positives;
    }

    /**
     * Gets all the negative conjunctions of conditions
     *
     * @return The negative conjunctions of conditions
     */
    public Collection<Collection<RDFTriple>> getNegatives() {
        return negatives;
    }

    /**
     * Gets the output node in the RETE network for elements matching this rule
     *
     * @return The output node in the RETE network for elements matching this rule
     */
    public TokenActivable getOutput() {
        return output;
    }
}
