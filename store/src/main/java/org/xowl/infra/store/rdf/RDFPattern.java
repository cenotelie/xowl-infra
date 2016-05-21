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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a pattern of RDF quads
 *
 * @author Laurent Wouters
 */
public class RDFPattern {
    /**
     * The positive quads in this pattern
     */
    private final Collection<Quad> positives;
    /**
     * The negatives conjunctions of quads in this pattern
     */
    private final Collection<Collection<Quad>> negatives;

    public RDFPattern() {
        this.positives = new ArrayList<>();
        this.negatives = new ArrayList<>();
    }

    /**
     * Gets the positive quads in this pattern
     *
     * @return The positive quads in this pattern
     */
    public Collection<Quad> getPositives() {
        return positives;
    }

    /**
     * Gets the negatives conjunctions of quads in this pattern
     *
     * @return The negatives conjunctions of quads in this pattern
     */
    public Collection<Collection<Quad>> getNegatives() {
        return negatives;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RDFPattern))
            return false;
        RDFPattern pattern = (RDFPattern) obj;
        // match identity?
        if (pattern == this)
            return true;
        // match the sizes
        if (this.positives.size() != pattern.positives.size())
            return false;
        if (this.negatives.size() != pattern.negatives.size())
            return false;
        // match the positives
        for (Quad quad : this.positives)
            if (!pattern.positives.contains(quad))
                return false;
        // match the negatives
        List<Collection<Quad>> temp = new ArrayList<>(negatives);
        for (Collection<Quad> conjunction : pattern.negatives) {
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
