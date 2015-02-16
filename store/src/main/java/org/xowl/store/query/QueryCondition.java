/**********************************************************************
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
 **********************************************************************/

package org.xowl.store.query;

import org.xowl.store.rdf.Quad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents the matching conditions of a query
 *
 * @author Laurent Wouters
 */
public class QueryCondition {
    /**
     * The positive conditions
     */
    private List<Quad> positives;
    /**
     * The list of conjunctive negative conditions
     */
    private List<Collection<Quad>> negatives;

    /**
     * Initializes this condition
     */
    public QueryCondition() {
        this.positives = new ArrayList<>();
        this.negatives = new ArrayList<>();
    }

    /**
     * Adds a positive condition
     *
     * @param condition A positive condition
     */
    public void addPositiveCondition(Quad condition) {
        positives.add(condition);
    }

    /**
     * Adds a negative set of conjunctive conditions
     *
     * @param conditions A set of conjunctive conditions
     */
    public void addNegativeConsitions(Collection<Quad> conditions) {
        negatives.add(conditions);
    }

    /**
     * Gets the positive conditions
     *
     * @return The positive conditions of this rule
     */
    public Collection<Quad> getPositives() {
        return positives;
    }

    /**
     * Gets all the negative conjunctions of conditions
     *
     * @return The negative conjunctions of conditions
     */
    public Collection<Collection<Quad>> getNegatives() {
        return negatives;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof QueryCondition))
            return false;
        QueryCondition c = (QueryCondition) obj;
        // match the sizes
        if (this.positives.size() != c.positives.size())
            return false;
        if (this.negatives.size() != c.negatives.size())
            return false;
        // match the positives
        for (Quad quad : this.positives)
            if (!c.positives.contains(quad))
                return false;
        // match the negatives
        List<Collection<Quad>> temp = new ArrayList<>(negatives);
        for (Collection<Quad> conjunction : c.negatives) {
            boolean matches = false;
            for (int i = 0; i != temp.size(); i++) {
                if (conjunction.size() != temp.get(i).size())
                    continue;
                boolean innerMatch = true;
                for (Quad quad : conjunction) {
                    if (!temp.get(i).contains(quad)) {
                        innerMatch = false;
                        break;
                    }
                }
                if (!innerMatch)
                    continue;
                // found a match
                temp.remove(i);
                matches = true;
                break;
            }
            // no match at all
            if (!matches)
                return false;
        }
        // all matches
        return true;
    }
}
