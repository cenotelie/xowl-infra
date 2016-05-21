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

/**
 * Represents a part of the pattern for matching a RDF rule
 *
 * @author Laurent Wouters
 */
public class RDFRulePatternPart {
    /**
     * The positive antecedents to match
     */
    private final Collection<Quad> positives;
    /**
     * The negative antecedents conjunctions to NOT match
     */
    private final Collection<Collection<Quad>> negatives;

    /**
     * Initializes this part
     */
    public RDFRulePatternPart() {
        this.positives = new ArrayList<>();
        this.negatives = new ArrayList<>();
    }

    /**
     * Gets the positive antecedents to match
     *
     * @return The positive antecedents to match
     */
    public Collection<Quad> getPositives() {
        return positives;
    }

    /**
     * Gets the negative antecedents conjunctions to NOT match
     *
     * @return The negative antecedents conjunctions to NOT match
     */
    public Collection<Collection<Quad>> getNegatives() {
        return negatives;
    }
}
