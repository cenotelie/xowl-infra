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

import org.xowl.infra.store.rete.*;
import org.xowl.infra.store.storage.Dataset;

import java.util.*;

/**
 * Represents a query engine for a RDF store
 *
 * @author Laurent Wouters
 */
public class QueryEngine implements ChangeListener {
    /**
     * The number of queries in the cache above which the cache starts to clear itself by removing less used queries
     */
    private static final int CACHE_CLEAR_THRESHOLD = 300;
    /**
     * The number of times a query has been used under which it is susceptible to be dropped from the cache
     */
    private static final int CACHE_DROP_THRESHOLD = 5;

    /**
     * Represents a cached query that continues being executed
     */
    private static class CacheElem implements TokenActivable {
        /**
         * The original query
         */
        private final Query query;
        /**
         * The associated RETE rule
         */
        private final RETERule rule;
        /**
         * The solution tokens
         */
        private final List<Token> tokens;
        /**
         * The number of times this query has been used
         */
        private int hitCount;

        /**
         * Initializes this cache element
         *
         * @param query The original query
         */
        public CacheElem(Query query) {
            this.query = query;
            this.rule = new RETERule(this);
            this.rule.getPositives().addAll(query.getPositives());
            this.rule.getNegatives().addAll(query.getNegatives());
            this.tokens = new ArrayList<>();
            this.hitCount = 0;
        }

        /**
         * Gets the RETE rule associated to this query
         *
         * @return The associated RETE rule
         */
        public RETERule getRule() {
            return rule;
        }

        /**
         * Gets the hit count for this cache element
         *
         * @return The hit count
         */
        public int getHitCount() {
            return hitCount;
        }

        @Override
        public void activateToken(Token token) {
            tokens.add(token);
        }

        @Override
        public void deactivateToken(Token token) {
            tokens.remove(token);
        }

        @Override
        public void activateTokens(Collection<Token> tokens) {
            for (Token token : tokens)
                this.tokens.add(token);
        }

        @Override
        public void deactivateTokens(Collection<Token> tokens) {
            for (Token token : tokens)
                this.tokens.remove(token);
        }

        /**
         * Determines whether this cache element matches the specified query
         *
         * @param candidate A query candidate
         * @return true if the candidate matches the query represented by this cache element
         */
        public boolean matches(Query candidate) {
            return query.equals(candidate);
        }

        /**
         * Gets the current solutions to the query represented by this cache element
         *
         * @return the current solutions
         */
        public List<QuerySolution> getSolutions() {
            hitCount++;
            List<QuerySolution> results = new ArrayList<>();
            for (Token token : tokens)
                results.add(new QuerySolution(token.getBindings()));
            return results;
        }
    }

    /**
     * Represents a observed query that continues being executed
     */
    private static class ObservedElem implements TokenActivable {
        /**
         * The original query
         */
        private final Query query;
        /**
         * The associated RETE rule
         */
        private final RETERule rule;
        /**
         * The solution
         */
        private final Map<Token, QuerySolution> solutions;
        /**
         * The observers for this query
         */
        private final List<QueryObserver> observers;

        /**
         * Initializes this observed query
         *
         * @param query    The original query
         * @param observer The first observer
         */
        public ObservedElem(Query query, QueryObserver observer) {
            this.query = query;
            this.rule = new RETERule(this);
            this.rule.getPositives().addAll(query.getPositives());
            this.rule.getNegatives().addAll(query.getNegatives());
            this.solutions = new HashMap<>();
            this.observers = new ArrayList<>();
            this.observers.add(observer);
        }

        /**
         * Gets the RETE rule associated to this query
         *
         * @return The associated RETE rule
         */
        public RETERule getRule() {
            return rule;
        }

        /**
         * Adds a new observer
         *
         * @param observer The new observer
         */
        public void addObserver(QueryObserver observer) {
            observers.add(observer);
            for (QuerySolution solution : solutions.values()) {
                observer.onNewSolution(solution);
            }
        }

        /**
         * Removes an observer
         *
         * @param observer The observer to remove
         * @return Whether this was the last observer
         */
        public boolean removeObserver(QueryObserver observer) {
            observers.remove(observer);
            return observers.isEmpty();
        }

        @Override
        public void activateToken(Token token) {
            QuerySolution solution = new QuerySolution(token.getBindings());
            solutions.put(token, solution);
            for (QueryObserver observer : observers)
                observer.onNewSolution(solution);
        }

        @Override
        public void deactivateToken(Token token) {
            QuerySolution solution = solutions.remove(token);
            if (solution == null)
                return;
            for (QueryObserver observer : observers)
                observer.onSolutionRevoked(solution);
        }

        @Override
        public void activateTokens(Collection<Token> tokens) {
            for (Token token : tokens)
                activateToken(token);
        }

        @Override
        public void deactivateTokens(Collection<Token> tokens) {
            for (Token token : tokens)
                deactivateToken(token);
        }

        /**
         * Determines whether this cache element matches the specified query
         *
         * @param candidate A query candidate
         * @return true if the candidate matches the query represented by this cache element
         */
        public boolean matches(Query candidate) {
            return query.equals(candidate);
        }
    }


    /**
     * A RETE network for the pattern matching of queries
     */
    private final RETENetwork rete;
    /**
     * The new added quads since the last application
     */
    private final List<Quad> newAdded;
    /**
     * The new removed quads since the last application
     */
    private final List<Quad> newRemoved;
    /**
     * The new changesets since the last application
     */
    private final List<Changeset> newChangesets;
    /**
     * Flag whether outstanding changes are currently being applied
     */
    private boolean isApplying;
    /**
     * Buffer of positive quads
     */
    private final Collection<Quad> bufferPositives;
    /**
     * Buffer of negative quads
     */
    private final Collection<Quad> bufferNegatives;
    /**
     * The cache of queries
     */
    private final List<CacheElem> cache;
    /**
     * The observed queries
     */
    private final List<ObservedElem> observedQueries;

    /**
     * Initializes this engine
     *
     * @param store The RDF store to query
     */
    public QueryEngine(Dataset store) {
        this.rete = new RETENetwork(store);
        this.newAdded = new ArrayList<>();
        this.newRemoved = new ArrayList<>();
        this.newChangesets = new ArrayList<>();
        this.bufferPositives = new ArrayList<>();
        this.bufferNegatives = new ArrayList<>();
        this.cache = new ArrayList<>();
        this.observedQueries = new ArrayList<>();
        store.addListener(this);
    }

    /**
     * Executes the specified query and gets the solutions
     *
     * @param query A query
     * @return The solutions
     */
    public Collection<QuerySolution> execute(Query query) {
        // try from the cache
        for (final CacheElem element : cache) {
            if (element.matches(query)) {
                Collection<QuerySolution> result = element.getSolutions();
                // re-sort the cache by hit count (hottest on top)
                Collections.sort(cache, new Comparator<CacheElem>() {
                    @Override
                    public int compare(CacheElem element1, CacheElem element2) {
                        return Integer.compare(element2.getHitCount(), element1.getHitCount());
                    }
                });
                return result;
            }
        }

        // shall we clear the cache
        if (cache.size() > CACHE_CLEAR_THRESHOLD) {
            for (int i = cache.size() - 1; i != -1; i--) {
                CacheElem element = cache.get(i);
                if (element.getHitCount() < CACHE_DROP_THRESHOLD) {
                    rete.removeRule(element.getRule());
                    cache.remove(i);
                } else {
                    // stop here because we go increasing
                    break;
                }
            }
        }

        // build the new query and register it in the cache
        CacheElem element = new CacheElem(query);
        cache.add(element);
        rete.addRule(element.getRule());
        return element.getSolutions();
    }

    /**
     * Observes the specified query
     *
     * @param query    The query to observe
     * @param observer The observer
     */
    public void observe(Query query, QueryObserver observer) {
        for (ObservedElem elem : observedQueries) {
            if (elem.matches(query)) {
                elem.addObserver(observer);
                return;
            }
        }
        ObservedElem element = new ObservedElem(query, observer);
        observedQueries.add(element);
        rete.addRule(element.getRule());
    }

    /**
     * Stops the observation of a query
     *
     * @param query    The query to stop observing
     * @param observer The observer that stops its observation
     */
    public void unobserve(Query query, QueryObserver observer) {
        for (ObservedElem elem : observedQueries) {
            if (elem.matches(query)) {
                if (elem.removeObserver(observer)) {
                    rete.removeRule(elem.getRule());
                    observedQueries.remove(elem);
                }
                return;
            }
        }
    }

    /**
     * Gets the matching status of the specified query
     *
     * @param query A query
     * @return The matching status
     */
    public MatchStatus getMatchStatus(Query query) {
        // try from the cache
        for (final CacheElem element : cache) {
            if (element.matches(query)) {
                return rete.getStatus(element.getRule());
            }
        }
        // the query is not in the cache
        return null;
    }

    @Override
    public void onIncremented(Quad quad) {
        // do nothing
    }

    @Override
    public void onDecremented(Quad quad) {
        // do nothing
    }

    @Override
    public void onAdded(Quad quad) {
        newAdded.remove(quad);
        apply();
    }

    @Override
    public void onRemoved(Quad quad) {
        newRemoved.add(quad);
        apply();
    }

    @Override
    public void onChange(Changeset changeset) {
        newChangesets.add(changeset);
        apply();
    }

    /**
     * Applies all outstanding changes
     */
    protected void apply() {
        if (isApplying)
            return;
        isApplying = true;
        while (!newAdded.isEmpty() || !newRemoved.isEmpty() || !newChangesets.isEmpty()) {
            bufferPositives.addAll(newAdded);
            bufferNegatives.addAll(newRemoved);
            newAdded.clear();
            newRemoved.clear();
            for (Changeset changeset : newChangesets) {
                bufferPositives.addAll(changeset.getAdded());
                bufferNegatives.addAll(changeset.getRemoved());
            }
            newChangesets.clear();
            rete.injectPositives(bufferPositives);
            rete.injectNegatives(bufferNegatives);
            bufferPositives.clear();
            bufferNegatives.clear();
        }
        isApplying = false;
    }
}
