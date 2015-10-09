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

package org.xowl.store.storage.persistent;

import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.store.IRIs;
import org.xowl.store.owl.AnonymousNode;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.NodeManager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Represents a persistent store of nodes
 *
 * @author Laurent Wouters
 */
public class PersistentNodes implements NodeManager {
    /**
     * The backing file
     */
    private final PersistedFile file;
    /**
     * Map of the IRIs
     */
    private final PersistedHashMap mapIRIs;
    /**
     * Cache for persistable strings
     */
    private final PersistableFastString persistableString;

    public PersistentNodes() throws IOException {
        this.file = new PersistedFile(new File("nodes"));
        this.mapIRIs = new PersistedHashMap(file);
        this.persistableString = new PersistableFastString();
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
        persistableString.set(iri);
        try {
            long location = mapIRIs.put(persistableString, PersistableEmptyValue.instance());
            return new PersistedIRINode(file, location - persistableString.persistedLength());
        } catch (IOException exception) {
            return null;
        }
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        persistableString.set(iri);
        try {
            long location = mapIRIs.get(persistableString);
            if (location != -1)
                return new PersistedIRINode(file, location - persistableString.persistedLength());
            return null;
        } catch (IOException exception) {
            return null;
        }
    }

    @Override
    public BlankNode getBlankNode() {
        return null;
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        return null;
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        return null;
    }
}
