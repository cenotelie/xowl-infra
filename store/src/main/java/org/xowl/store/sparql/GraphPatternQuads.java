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

package org.xowl.store.sparql;

import org.xowl.store.Repository;
import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.Query;
import org.xowl.store.rdf.QuerySolution;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A graph pattern represented by a template of quads
 *
 * @author Laurent Wouters
 */
public class GraphPatternQuads implements GraphPattern {
    /**
     * The RDF query
     */
    private final Query query;

    /**
     * Initializes this pattern
     *
     * @param quads The quads to match
     */
    public GraphPatternQuads(Collection<Quad> quads) {
        this.query = new Query();
        this.query.getPositives().addAll(quads);
    }

    /**
     * Initializes this pattern
     *
     * @param positives The quads to match
     * @param negatives The quads taht must not be matched
     */
    public GraphPatternQuads(Collection<Quad> positives, Collection<Quad> negatives) {
        this.query = new Query();
        this.query.getPositives().addAll(positives);
        this.query.getNegatives().add(new ArrayList<>(negatives));
    }

    @Override
    public Collection<QuerySolution> match(final Repository repository) throws EvalException {
        return repository.getRDFQueryEngine().execute(query);
    }
}
