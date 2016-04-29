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
import org.xowl.infra.utils.collections.ConcatenatedIterator;
import org.xowl.infra.utils.collections.SingleIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A set of grouped solutions to a SPARQL query
 *
 * @author Laurent Wouters
 */
class SolutionsGroup implements Solutions {
    /**
     * A group within this set
     */
    private static class Group implements Solutions {
        /**
         * The key for this group
         */
        private final Object key;
        /**
         * The sub groups
         */
        private Collection<Group> subGroups;
        /**
         * The solutions in this group
         */
        private SolutionsMultiset content;

        /**
         * Gets the key for this group
         *
         * @return The key for this group
         */
        public Object getKey() {
            return key;
        }

        /**
         * Initializes this group
         *
         * @param key The key for this group
         */
        public Group(Object key) {
            this.key = key;
        }

        @Override
        public int size() {
            int acc = (content != null ? content.size() : 0);
            if (subGroups != null) {
                for (Group sub : subGroups)
                    acc += sub.size();
            }
            return acc;
        }

        @Override
        public Iterator<QuerySolution> iterator() {
            List<Iterator<QuerySolution>> iterators = new ArrayList<>();
            if (content != null)
                iterators.add(content.iterator());
            if (subGroups != null) {
                for (Group sub : subGroups)
                    iterators.add(sub.iterator());
            }
            if (iterators.isEmpty())
                return new SingleIterator<>(null);
            if (iterators.size() == 1)
                return iterators.get(0);
            Iterator<QuerySolution>[] buffer = new Iterator[iterators.size()];
            for (int i = 0; i != iterators.size(); i++)
                buffer[i] = iterators.get(i);
            return new ConcatenatedIterator<>(buffer);
        }

        /**
         * Adds a new solution
         *
         * @param keys     The keys for the solution
         * @param solution The solution to add
         */
        public void add(List<Object> keys, QuerySolution solution) {
            Group target = this;
            for (int i = 0; i != keys.size(); i++) {
                if (target.subGroups == null) {
                    Group sub = new Group(keys.get(i));
                    target.subGroups = new ArrayList<>();
                    target.subGroups.add(sub);
                    target = sub;
                } else {
                    Group match = null;
                    for (Group sub : subGroups) {
                        boolean isEqual = false;
                        try {
                            isEqual = (ExpressionOperator.equals(sub.key, keys.get(i)));
                        } catch (EvalException exception) {
                            // do nothing
                        }
                        if (isEqual) {
                            match = sub;
                            break;
                        }
                    }
                    if (match == null) {
                        match = new Group(keys.get(i));
                        target.subGroups.add(match);
                    }
                    target = match;
                }
            }
            if (target.content == null)
                target.content = new SolutionsMultiset();
            target.content.add(solution);
        }
    }

    /**
     * The top group in this set
     */
    private final Group top;

    /**
     * Initializes this solution set
     */
    public SolutionsGroup() {
        this.top = new Group(null);
    }

    /**
     * Adds a new solution
     *
     * @param keys     The keys for the solution
     * @param solution The solution to add
     */
    public void add(List<Object> keys, QuerySolution solution) {
        top.add(keys, solution);
    }

    @Override
    public int size() {
        return top.size();
    }

    @Override
    public Iterator<QuerySolution> iterator() {
        return top.iterator();
    }
}
