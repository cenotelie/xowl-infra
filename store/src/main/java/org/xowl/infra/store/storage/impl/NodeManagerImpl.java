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

package org.xowl.infra.store.storage.impl;

import org.xowl.infra.store.IRIs;
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.IRINode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.storage.NodeManager;

import java.util.UUID;

/**
 * Base implementation of a node manager
 *
 * @author Laurent Wouters
 */
public abstract class NodeManagerImpl implements NodeManager {
    @Override
    public IRINode getIRINode(GraphNode graph) {
        if (graph != null && graph.getNodeType() == Node.TYPE_IRI) {
            String value = ((IRINode) graph).getIRIValue();
            return getIRINode(value + "#" + UUID.randomUUID().toString());
        } else {
            return getIRINode(IRIs.GRAPH_DEFAULT + "#" + UUID.randomUUID().toString());
        }
    }
}
