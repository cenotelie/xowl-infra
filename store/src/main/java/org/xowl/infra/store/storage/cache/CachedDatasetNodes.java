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

package org.xowl.infra.store.storage.cache;

import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.DatasetNodesImpl;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represented an in-memory store of RDF nodes
 * This structure is thread-safe.
 *
 * @author Laurent Wouters
 */
public class CachedDatasetNodes extends DatasetNodesImpl {
    /**
     * The map of cached IRI nodes
     */
    private final ConcurrentHashMap<String, WeakReference<IRINode>> iris;
    /**
     * The map of cached literals (per lexical value)
     */
    private final ConcurrentHashMap<String, LiteralBucket> literals;
    /**
     * The map of cached anonymous individuals
     */
    private final ConcurrentHashMap<String, WeakReference<AnonymousNode>> anonymous;
    /**
     * The next blank identifier
     */
    private AtomicLong nextBlank;

    /**
     * Initializes this store
     */
    public CachedDatasetNodes() {
        iris = new ConcurrentHashMap<>();
        literals = new ConcurrentHashMap<>();
        anonymous = new ConcurrentHashMap<>();
        nextBlank = new AtomicLong(0);
    }

    @Override
    public IRINode getIRINode(String iri) {
        while (true) {
            WeakReference<IRINode> ref = iris.get(iri);
            if (ref == null) {
                IRINode result = new CachedIRINode(iri);
                WeakReference<IRINode> previous = iris.putIfAbsent(iri, new WeakReference<>(result));
                if (previous == null)
                    return result;
            } else {
                IRINode result = ref.get();
                if (result != null)
                    return result;
                result = new CachedIRINode(iri);
                if (iris.replace(iri, ref, new WeakReference<>(result)))
                    return result;
            }
        }
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        while (true) {
            WeakReference<IRINode> ref = iris.get(iri);
            if (ref == null)
                return null;
            IRINode result = ref.get();
            if (result != null)
                return result;
            result = new CachedIRINode(iri);
            if (iris.replace(iri, ref, new WeakReference<>(result)))
                return result;
        }
    }

    @Override
    public BlankNode getBlankNode() {
        return new BlankNode(nextBlank.getAndIncrement());
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        while (true) {
            LiteralBucket bucket = literals.get(lex);
            if (bucket != null)
                return bucket.get(lex, datatype, lang);
            bucket = new LiteralBucket();
            LiteralBucket value = literals.putIfAbsent(lex, bucket);
            if (value == null)
                return bucket.get(lex, datatype, lang);
        }
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        while (true) {
            WeakReference<AnonymousNode> ref = anonymous.get(individual.getNodeID());
            if (ref == null) {
                AnonymousNode result = new CachedAnonNode(individual);
                WeakReference<AnonymousNode> previous = anonymous.putIfAbsent(individual.getNodeID(), new WeakReference<>(result));
                if (previous == null)
                    return result;
            } else {
                AnonymousNode result = ref.get();
                if (result != null)
                    return result;
                result = new CachedAnonNode(individual);
                if (anonymous.replace(individual.getNodeID(), ref, new WeakReference<>(result)))
                    return result;
            }
        }
    }

    @Override
    public DynamicNode getDynamicNode(EvaluableExpression evaluable) {
        return new CachedDynamicNode(evaluable);
    }
}
