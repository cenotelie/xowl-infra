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

package org.xowl.store.storage.cache;

import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.store.IRIs;
import org.xowl.store.owl.AnonymousNode;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.NodeManager;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Represented a cached store of nodes
 *
 * @author Laurent Wouters
 */
public class CachedNodes implements NodeManager {
    /**
     * The map of cached IRI nodes
     */
    private final Map<String, WeakReference<IRINode>> iris;
    /**
     * The map of cached literals (per lexical value)
     */
    private final Map<String, LiteralBucket> literals;
    /**
     * The map of cached anonymous individuals
     */
    private final Map<String, WeakReference<AnonymousNode>> anonymous;
    /**
     * The next blank identifier
     */
    private long nextBlank;

    /**
     * Initializes this store
     */
    public CachedNodes() {
        iris = new HashMap<>();
        literals = new HashMap<>();
        anonymous = new HashMap<>();
        nextBlank = 0;
    }

    @Override
    public IRINode getIRINode(GraphNode graph) {
        if (graph != null && graph.getNodeType() == Node.TYPE_IRI) {
            String value = ((IRINode) graph).getIRIValue();
            return getIRINode(value + "#" + UUID.randomUUID().toString());
        } else {
            return getIRINode(IRIs.GRAPH_DEFAULT + "#" + UUID.randomUUID().toString());
        }
    }

    @Override
    public IRINode getIRINode(String iri) {
        WeakReference<IRINode> ref = iris.get(iri);
        if (ref == null) {
            IRINode result = new CachedIRINode(iri);
            iris.put(iri, new WeakReference<>(result));
            return result;
        } else {
            IRINode result = ref.get();
            if (result != null)
                return result;
            result = new CachedIRINode(iri);
            iris.put(iri, new WeakReference<>(result));
            return result;
        }
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        WeakReference<IRINode> ref = iris.get(iri);
        return (ref == null ? null : ref.get());
    }

    @Override
    public BlankNode getBlankNode() {
        BlankNode result = new BlankNode(nextBlank);
        nextBlank++;
        return result;
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        LiteralBucket bucket = literals.get(lex);
        if (bucket == null) {
            bucket = new LiteralBucket();
            literals.put(lex, bucket);
        }
        return bucket.get(lex, datatype, lang);
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        WeakReference<AnonymousNode> ref = anonymous.get(individual.getNodeID());
        if (ref == null) {
            AnonymousNode result = new CachedAnonNode(individual);
            anonymous.put(individual.getNodeID(), new WeakReference<>(result));
            return result;
        } else {
            AnonymousNode result = ref.get();
            if (result != null)
                return result;
            result = new CachedAnonNode(individual);
            anonymous.put(individual.getNodeID(), new WeakReference<>(result));
            return result;
        }
    }

    /**
     * Cleanup dead entries in this cache
     */
    public void cleanup() {
        List<String> deads = new ArrayList<>();
        for (Map.Entry<String, WeakReference<IRINode>> entry : iris.entrySet()) {
            if (entry.getValue().get() == null)
                deads.add(entry.getKey());
        }
        for (String deadIRI : deads)
            iris.remove(deadIRI);

        deads.clear();
        for (Map.Entry<String, LiteralBucket> entry : literals.entrySet()) {
            entry.getValue().cleanup();
            if (entry.getValue().getSize() == 0)
                deads.add(entry.getKey());
        }
        for (String deadLiteral : deads)
            literals.remove(deadLiteral);

        deads.clear();
        for (Map.Entry<String, WeakReference<AnonymousNode>> entry : anonymous.entrySet()) {
            if (entry.getValue().get() == null)
                deads.add(entry.getKey());
        }
        for (String deadAnon : deads)
            anonymous.remove(deadAnon);
    }
}
