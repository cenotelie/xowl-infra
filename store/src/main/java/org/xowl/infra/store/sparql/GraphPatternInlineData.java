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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.Repository;
import org.xowl.infra.store.rdf.RDFPatternSolution;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A graph pattern represented by explicit (variable, value) associations
 *
 * @author Laurent Wouters
 */
public class GraphPatternInlineData implements GraphPattern {
    /**
     * The inline data
     */
    private final Collection<RDFPatternSolution> data;

    /**
     * Initializes this graph pattern
     *
     * @param data The inline data
     */
    public GraphPatternInlineData(Collection<RDFPatternSolution> data) {
        this.data = new ArrayList<>(data);
    }

    @Override
    public Solutions match(final Repository repository) throws EvalException {
        return new SolutionsMultiset(data);
    }
}
