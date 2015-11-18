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

package org.xowl.store.storage;

import org.xowl.store.rdf.*;
import org.xowl.store.storage.cache.CachedDataset;
import org.xowl.store.storage.impl.DatasetImpl;
import org.xowl.store.storage.impl.MQuad;
import org.xowl.store.storage.persistent.PersistedDataset;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Implements a persisted dataset that uses a caching layer to improve its access performances
 *
 * @author Laurent Wouters
 */
class CachingOnDiskDataset extends DatasetImpl implements AutoCloseable {
    /**
     * The base persisted dataset
     */
    private final PersistedDataset disk;
    /**
     * The cache corresponding exactly to the on-disk data
     */
    private final CachedDataset cache;
    /**
     * The current state of the cached data with the pending changes applied
     */
    private final DiffDataset current;


    public CachingOnDiskDataset(PersistedDataset disk) {
        this.disk = disk;
        this.cache = new CachedDataset();
        this.current = new DiffDataset(cache);
    }

    private void ensureInCache(SubjectNode subject) {

    }

    private void ensureInCache(GraphNode graph) {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public int doAddQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        return 0;
    }

    @Override
    public int doRemoveQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        return 0;
    }

    @Override
    public void doRemoveQuads(GraphNode graph, SubjectNode subject, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws UnsupportedNodeType {

    }

    @Override
    public void doClear(List<MQuad> buffer) {
        disk.doClear(buffer);
    }

    @Override
    public void doClear(GraphNode graph, List<MQuad> buffer) {
        disk.doClear(graph, buffer);
    }

    @Override
    public void doCopy(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {

    }

    @Override
    public void doMove(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {

    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return 0;
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return null;
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        return null;
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return 0;
    }
}
