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

import org.xowl.infra.store.rdf.QuerySolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A set of possibly duplicated solutions to a SPARQL query
 *
 * @author Laurent Wouters
 */
class SolutionsMultiset implements Solutions {
    /**
     * The contained solutions
     */
    private final List<QuerySolution> content;

    /**
     * Initializes an empty set
     */
    public SolutionsMultiset() {
        this.content = new ArrayList<>();
    }

    /**
     * Initializes an empty set provisioning a specified number of solutions
     *
     * @param size The number of solutions to provision space for
     */
    public SolutionsMultiset(int size) {
        this.content = new ArrayList<>(size);
    }

    /**
     * Initializes this set with the specified content
     *
     * @param content The initial content
     */
    public SolutionsMultiset(Collection<QuerySolution> content) {
        this.content = new ArrayList<>(content);
    }

    /**
     * Initializes this set as a copy of the specified original
     *
     * @param original The original set
     */
    public SolutionsMultiset(Solutions original) {
        this.content = new ArrayList<>(original.size());
        for (QuerySolution solution : original)
            content.add(solution);
    }

    /**
     * Initializes this set as a copy of the specified original
     *
     * @param original The original set
     * @param distinct Whether only distinct solutions are added
     */
    public SolutionsMultiset(Solutions original, boolean distinct) {
        this.content = new ArrayList<>(original.size());
        for (QuerySolution solution : original)
            if (!distinct || (!content.contains(solution)))
                content.add(solution);
    }

    /**
     * Adds a new solution to this set
     *
     * @param solution A solution
     */
    void add(QuerySolution solution) {
        content.add(solution);
    }

    /**
     * Adds a solution if it is not already present in this set
     *
     * @param solution A solution
     */
    void addDistinct(QuerySolution solution) {
        if (!content.contains(solution))
            content.add(solution);
    }

    @Override
    public int size() {
        return content.size();
    }

    @Override
    public Iterator<QuerySolution> iterator() {
        return content.iterator();
    }
}
