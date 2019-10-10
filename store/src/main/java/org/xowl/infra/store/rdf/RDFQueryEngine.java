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

import fr.cenotelie.commons.utils.collections.FastBuffer;
import org.xowl.infra.store.rete.RETENetwork;
import org.xowl.infra.store.rete.RETERule;
import org.xowl.infra.store.rete.Token;
import org.xowl.infra.store.rete.TokenActivable;
import org.xowl.infra.store.storage.Store;

import java.util.*;

/**
 * Represents a query engine for a RDF store
 * This structure is thread-safe.
 *
 * @author Laurent Wouters
 */
public class RDFQueryEngine implements ChangeListener {
    /**
     * The number of queries in the cache above which the cache starts to clear itself by removing less used queries
     */
    private static final int CACHE_MAX_SIZE = 64;
    /**
     * A RETE network for the pattern matching of queries
     */
    private final RETENetwork rete;
    /**
     * The cache of queries
     */
    private final List<CacheElem> cache;
    /**
     * The thread-specific engine inputs and outputs
     */
    private final ThreadLocal<EngineIO> threadIO;

    /**
     * Initializes this engine
     *
     * @param store The RDF store to query
     */
    public RDFQueryEngine(Store store) {
        this.rete = new RETENetwork(store);
        this.cache = new ArrayList<>();
        this.threadIO = new ThreadLocal<>();
        store.addListener(this);
    }

    /**
     * Gets the thread-specific engine inputs and outputs
     *
     * @return The engine inputs and outputs for this thread
     */
    private EngineIO getIO() {
        EngineIO result = threadIO.get();
        if (result == null) {
            result = new EngineIO();
            threadIO.set(result);
        }
        return result;
    }

    /**
     * Executes the specified query and gets the solutions
     *
     * @param query A query
     * @return The solutions
     */
    public Collection<RDFPatternSolution> execute(RDFQuery query) {
        // try from the cache

        CacheElem target = null;
        synchronized (cache) {
            for (CacheElem element : cache) {
                if (element.matches(query)) {
                    target = element;
                    break;
                }
            }
        }
        if (target != null) {
            Collection<RDFPatternSolution> result = target.getSolutions();
            // re-sort the cache by hit count (hottest on top)
            synchronized (cache) {
                cache.sort(Comparator.comparingInt(CacheElem::getHitCount));
            }
            return result;
        }

        synchronized (cache) {
            while (cache.size() >= CACHE_MAX_SIZE) {
                int index = cache.size() - 1;
                try {
                    CacheElem toDrop = cache.remove(index);
                    rete.removeRule(toDrop.getRule());
                } catch (IndexOutOfBoundsException exception) {
                    // ignore
                }
            }
            // build the new query and register it in the cache
            target = new CacheElem(query);
            cache.add(target);
        }
        rete.addRule(target.getRule());
        return target.getSolutions();
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
        EngineIO io = getIO();
        io.addAddedQuad(quad);
        flush(io);
    }

    @Override
    public void onRemoved(Quad quad) {
        EngineIO io = getIO();
        io.addRemovedQuad(quad);
        flush(io);
    }

    @Override
    public void onChange(Changeset changeset) {
        EngineIO io = getIO();
        io.addChangeset(changeset);
        flush(io);
    }

    /**
     * Flushes any outstanding changes in the input or the output
     *
     * @param io The thread-specific inputs and outputs
     */
    public void flush(EngineIO io) {
        if (io.isFlushing)
            return;
        io.isFlushing = true;
        while (io.hasOutstandingChanges()) {
            // inject in the RETE network
            rete.injectPositives(io.checkoutPositivesQuads());
            rete.injectNegatives(io.checkoutNegativeQuads());
        }
        io.isFlushing = false;
    }

    /**
     * Represents a cached query that continues being executed
     */
    private static class CacheElem implements TokenActivable {
        /**
         * The original query
         */
        private final RDFQuery query;
        /**
         * The associated RETE rule
         */
        private final RETERule rule;
        /**
         * The solution tokens
         */
        private final FastBuffer<Token> tokens;
        /**
         * The number of times this query has been used
         * The count is approximate in multi-threading environment but this should be good enough
         */
        private int hitCount;

        /**
         * Initializes this cache element
         *
         * @param query The original query
         */
        public CacheElem(RDFQuery query) {
            this.query = query;
            this.rule = new RETERule(this);
            this.rule.getPositives().addAll(query.getPositives());
            this.rule.getNegatives().addAll(query.getNegatives());
            this.tokens = new FastBuffer<>(8);
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
            synchronized (tokens) {
                tokens.add(token);
            }
        }

        @Override
        public void deactivateToken(Token token) {
            synchronized (tokens) {
                tokens.remove(token);
            }
        }

        @Override
        public void activateTokens(Collection<Token> tokens) {
            synchronized (this.tokens) {
                this.tokens.addAll(tokens);
            }
        }

        @Override
        public void deactivateTokens(Collection<Token> tokens) {
            synchronized (this.tokens) {
                this.tokens.removeAll(tokens);
            }
        }

        /**
         * Determines whether this cache element matches the specified query
         *
         * @param candidate A query candidate
         * @return true if the candidate matches the query represented by this cache element
         */
        public boolean matches(RDFQuery candidate) {
            return query.equals(candidate);
        }

        /**
         * Gets the current solutions to the query represented by this cache element
         *
         * @return the current solutions
         */
        public List<RDFPatternSolution> getSolutions() {
            hitCount++;
            List<RDFPatternSolution> results = new ArrayList<>();
            for (Token token : tokens) {
                if (token != null)
                    results.add(new RDFPatternSolution(token.getBindings()));
            }
            return results;
        }
    }

    /**
     * Represents the thread-specific inputs and outputs of the engine
     */
    private static class EngineIO {
        /**
         * Flag whether outstanding changes are currently being applied
         */
        public boolean isFlushing;
        /**
         * Buffer of positive quads yet to be flushed
         */
        private Collection<Quad> bufferPositives;
        /**
         * Buffer of negative quads yet ro be flushed
         */
        private Collection<Quad> bufferNegatives;

        /**
         * Gets whether there are outstanding changes
         *
         * @return Whether there are outstanding changes
         */
        public boolean hasOutstandingChanges() {
            return (bufferPositives != null && !bufferPositives.isEmpty())
                    || (bufferNegatives != null && !bufferNegatives.isEmpty());
        }

        /**
         * Gets the positive quads to be injected (and resets the buffer)
         *
         * @return The positive quads to be injected
         */
        public Collection<Quad> checkoutPositivesQuads() {
            Collection<Quad> result = bufferPositives == null ? Collections.emptyList() : bufferPositives;
            bufferPositives = null;
            return result;
        }

        /**
         * Gets the negative quads to be injected (and resets the buffer)
         *
         * @return The negative quads to be injected
         */
        public Collection<Quad> checkoutNegativeQuads() {
            Collection<Quad> result = bufferNegatives == null ? Collections.emptyList() : bufferNegatives;
            bufferNegatives = null;
            return result;
        }

        /**
         * Adds a quad that is being added
         *
         * @param quad The added quad
         */
        public void addAddedQuad(Quad quad) {
            if (bufferPositives == null)
                bufferPositives = new ArrayList<>();
            bufferPositives.add(quad);
        }

        /**
         * Adds a quad that is being removed
         *
         * @param quad The removed quad
         */
        public void addRemovedQuad(Quad quad) {
            if (bufferNegatives == null)
                bufferNegatives = new ArrayList<>();
            bufferNegatives.add(quad);
        }

        /**
         * Adds a changeset being injected
         *
         * @param changeset The injected changeset
         */
        public void addChangeset(Changeset changeset) {
            if (!changeset.getAdded().isEmpty()) {
                if (bufferPositives == null)
                    bufferPositives = new ArrayList<>();
                bufferPositives.addAll(changeset.getAdded());
            }
            if (!changeset.getDecremented().isEmpty()) {
                if (bufferNegatives == null)
                    bufferNegatives = new ArrayList<>();
                bufferNegatives.addAll(changeset.getDecremented());
            }
            if (!changeset.getRemoved().isEmpty()) {
                if (bufferNegatives == null)
                    bufferNegatives = new ArrayList<>();
                bufferNegatives.addAll(changeset.getRemoved());
            }
        }
    }
}
