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

package org.xowl.infra.store.rete;

import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.storage.Dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents the alpha part of a RETE graph
 *
 * @author Laurent Wouters
 */
class AlphaGraph extends AlphaMemoryBucket {
    /**
     * Sub-bucket matching the object field of quads
     */
    private static class BucketObject extends AlphaMemoryBucket {
        @Override
        protected Node getNode(Quad quad) {
            return quad.getObject();
        }

        @Override
        protected AlphaMemoryBucketElement createSub(Quad pattern, Dataset store) {
            return new AlphaMemory(pattern, store);
        }
    }

    /**
     * Sub-bucket matching the property field of quads
     */
    private static class BucketProperty extends AlphaMemoryBucket {
        @Override
        protected Node getNode(Quad quad) {
            return quad.getProperty();
        }

        @Override
        protected AlphaMemoryBucketElement createSub(Quad pattern, Dataset store) {
            return new BucketObject();
        }
    }

    /**
     * Sub-bucket matching the subject field of quads
     */
    private static class BucketSubject extends AlphaMemoryBucket {
        @Override
        protected Node getNode(Quad quad) {
            return quad.getSubject();
        }

        @Override
        protected AlphaMemoryBucketElement createSub(Quad pattern, Dataset store) {
            return new BucketProperty();
        }
    }

    /**
     * Initializes this buffer
     */
    public AlphaGraph() {
    }

    @Override
    protected Node getNode(Quad quad) {
        return quad.getGraph();
    }

    @Override
    protected AlphaMemoryBucketElement createSub(Quad pattern, Dataset store) {
        return new BucketSubject();
    }

    /**
     * Fires the specified quad in this graph
     *
     * @param quad A quad
     */
    public void fire(Quad quad) {
        AlphaMemoryBuffer buffer = new AlphaMemoryBuffer();
        matchMemories(buffer, quad);
        for (int i = 0; i != buffer.size(); i++)
            buffer.get(i).activateFact(quad);
    }

    /**
     * Unfires the specified quad from this graph
     *
     * @param quad A quad
     */
    public void unfire(Quad quad) {
        AlphaMemoryBuffer buffer = new AlphaMemoryBuffer();
        matchMemories(buffer, quad);
        for (int i = 0; i != buffer.size(); i++)
            buffer.get(i).deactivateFact(quad);
    }

    /**
     * Fires the specified collection of quads in this graph
     *
     * @param quads A collection of quads
     */
    public void fire(Collection<Quad> quads) {
        if (quads.size() == 1) {
            fire(quads.iterator().next());
        } else {
            Map<AlphaMemory, Collection<Quad>> dispatch = buildDispatch(quads);
            for (Entry<AlphaMemory, Collection<Quad>> entry : dispatch.entrySet())
                entry.getKey().activateFacts(new FastBuffer<>(entry.getValue()));
        }
    }

    /**
     * Unfires the specified collection of quads from this graph
     *
     * @param quads A collection of quads
     */
    public void unfire(Collection<Quad> quads) {
        if (quads.size() == 1) {
            unfire(quads.iterator().next());
        } else {
            Map<AlphaMemory, Collection<Quad>> dispatch = buildDispatch(quads);
            for (Entry<AlphaMemory, Collection<Quad>> entry : dispatch.entrySet())
                entry.getKey().deactivateFacts(new FastBuffer<>(entry.getValue()));
        }
    }

    /**
     * Builds the dispatching data for the specified collection of quads
     *
     * @param quads A collection of quads
     * @return The dispatching data associating alpha memory to the relevant collections of quads
     */
    private Map<AlphaMemory, Collection<Quad>> buildDispatch(Collection<Quad> quads) {
        AlphaMemoryBuffer buffer = new AlphaMemoryBuffer();
        Map<AlphaMemory, Collection<Quad>> map = new IdentityHashMap<>();
        for (Quad quad : quads) {
            matchMemories(buffer, quad);
            for (int i = 0; i != buffer.size(); i++) {
                AlphaMemory memory = buffer.get(i);
                Collection<Quad> collec = map.get(memory);
                if (collec == null) {
                    collec = new ArrayList<>();
                    map.put(memory, collec);
                }
                collec.add(quad);
            }
            buffer.clear();
        }
        return map;
    }
}
