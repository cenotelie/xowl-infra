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

/**
 * Represents the matching conditions of a RDF query
 *
 * @author Laurent Wouters
 */
public class RDFQuery {
    /**
     * The pattern to match for this query
     */
    private final RDFPattern pattern;

    /**
     * Initializes this condition
     */
    public RDFQuery() {
        this.pattern = new RDFPattern();
    }

    /**
     * Initializes this condition
     *
     * @param pattern The pattern to match for this query
     */
    public RDFQuery(RDFPattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Gets the positive conditions
     *
     * @return The positive conditions of this rule
     */
    public Collection<Quad> getPositives() {
        return pattern.getPositives();
    }

    /**
     * Gets all the negative conjunctions of conditions
     *
     * @return The negative conjunctions of conditions
     */
    public Collection<Collection<Quad>> getNegatives() {
        return pattern.getNegatives();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RDFQuery))
            return false;
        RDFQuery query = (RDFQuery) obj;
        return query == this || (this.pattern.equals(query.pattern));
    }
}
