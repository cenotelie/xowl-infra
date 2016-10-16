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

import org.xowl.infra.store.rdf.LiteralNode;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Represents a bucket of literals with the same lexical value in a RDF graph
 *
 * @author Laurent Wouters
 */
class LiteralBucket {
    /**
     * Initial size of a bucket
     */
    private static final int INIT_SIZE = 4;

    /**
     * The existing literal nodes
     * Do not store strong references to the literals so that they can be garbage-collected if not used
     */
    private AtomicReferenceArray<WeakReference<LiteralNode>> nodes;
    /**
     * The number of literals in this bucket
     */
    private AtomicInteger size;

    /**
     * Initializes this bucket
     */
    public LiteralBucket() {
        this.nodes = new AtomicReferenceArray<>(INIT_SIZE);
        this.size = new AtomicInteger(0);
    }

    /**
     * Try to retrieve a literal with the specified type and language tag
     *
     * @param lexical  The original lexical value
     * @param datatype The datatype to match
     * @param langTag  The language tag to match
     * @return The matching literal, or null if the operation failed
     */
    private LiteralNode tryGet(String lexical, String datatype, String langTag) {
        int currentSize = size.get();
        int insertIndex = -1;
        WeakReference<LiteralNode> insertRef = null;
        for (int i = 0; i != currentSize; i++) {
            WeakReference<LiteralNode> ref = nodes.get(i);
            LiteralNode candidate = ref.get();
            if (candidate == null) {
                insertIndex = i;
                insertRef = ref;
                continue;
            }
            if (Objects.equals(datatype, candidate.getDatatype()) && Objects.equals(langTag, candidate.getLangTag()))
                return candidate;
        }
        if (insertIndex == -1) {
            CachedLiteralNode result = new CachedLiteralNode(lexical, datatype, langTag);
            if (nodes.compareAndSet(insertIndex, insertRef, new WeakReference<LiteralNode>(result)))
                return result;
            return null;
        }
        // no more space, do not cache ...
        return new CachedLiteralNode(lexical, datatype, langTag);
    }

    /**
     * Gets the literal with the specified type and language tag
     *
     * @param lexical  The original lexical value
     * @param datatype The datatype to match
     * @param langTag  The language tag to match
     * @return The matching literal node
     */
    public LiteralNode get(String lexical, String datatype, String langTag) {
        LiteralNode result = null;
        while (result == null) {
            result = tryGet(lexical, datatype, langTag);
        }
        return result;
    }
}
