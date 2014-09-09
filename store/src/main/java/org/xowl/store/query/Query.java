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

package org.xowl.store.query;

import org.xowl.store.rdf.Quad;
import org.xowl.store.rete.RETERule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a query on a RDF dataset
 *
 * @author Laurent Wouters
 */
public class Query {
    /**
     * Embedded RETE rule implementing the query
     */
    private RETERule rule;
    /**
     * The RETE output for this query
     */
    private QueryOutput output;

    /**
     * Initializes this query
     */
    public Query() {
        this.output = new QueryOutput();
        this.rule = new RETERule(output);
    }

    /**
     * Gets the output for this query
     *
     * @return The output for this query
     */
    QueryOutput getOutput() {
        return output;
    }

    /**
     * Adds a single positive condition
     *
     * @param quad A positive condition
     */
    public void addCondition(Quad quad) {
        rule.addPositiveCondition(quad);
    }

    /**
     * Adds conjunctive positive conditions
     *
     * @param quads A collection of conjunctive positive conditions
     */
    public void addConsitions(Collection<Quad> quads) {
        for (Quad quad : quads)
            rule.addPositiveCondition(quad);
    }

    /**
     * Adss a single negative condition
     *
     * @param quad A negative condition
     */
    public void addNegativeCondition(Quad quad) {
        List<Quad> single = new ArrayList<>(1);
        single.add(quad);
        rule.addNegativeConsitions(single);
    }

    /**
     * Adds a negative conjunction of conditions
     *
     * @param quads A negative conjunction of conditions
     */
    public void addNegativeConjunction(Collection<Quad> quads) {
        rule.addNegativeConsitions(quads);
    }
}
