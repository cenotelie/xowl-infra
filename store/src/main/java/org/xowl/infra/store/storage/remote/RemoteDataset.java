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

package org.xowl.infra.store.storage.remote;

import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.store.storage.cache.CachedNodes;
import org.xowl.infra.store.storage.impl.DatasetImpl;
import org.xowl.infra.store.storage.impl.MQuad;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a dataset accessed to through a remote host
 *
 * @author Laurent Wouters
 */
public class RemoteDataset extends DatasetImpl implements Closeable {
    /**
     * The nodes for this dataset
     */
    private final CachedNodes nodes;
    /**
     * The connection to use
     */
    private final Connection connection;

    /**
     * Initializes this dataset
     *
     * @param nodes      The nodes for this dataset
     * @param connection The connection to use
     */
    public RemoteDataset(CachedNodes nodes, Connection connection) {
        this.nodes = nodes;
        this.connection = connection;
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

    }

    @Override
    public void doClear(GraphNode graph, List<MQuad> buffer) {

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

    @Override
    public void close() throws IOException {
        connection.close();
    }
}
