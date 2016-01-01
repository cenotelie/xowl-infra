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

package org.xowl.store.xsp;

import org.xowl.store.Serializable;

import java.util.Collection;
import java.util.Collections;

/**
 * Implements a successful reply to a xOWL server request with a collection of objects of type T as a response
 *
 * @param <T> The type of return data
 * @author Laurent Wouters
 */
public class XSPReplyResultCollection<T> implements XSPReply {
    /**
     * The payload
     */
    private final Collection<T> data;

    /**
     * Gets the payload
     *
     * @return The payload
     */
    public Collection<T> getData() {
        return data;
    }

    /**
     * Initializes this result
     *
     * @param data The payload
     */
    public XSPReplyResultCollection(Collection<T> data) {
        this.data = Collections.unmodifiableCollection(data);
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public String getMessage() {
        return serializedString();
    }

    @Override
    public String serializedString() {
        StringBuilder builder = new StringBuilder("[");
        boolean first = true;
        for (T obj : data) {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append(obj.toString());
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder("{ \"isSuccess\": true, \"message\": \"\", \"payload\": [");
        boolean first = true;
        for (T obj : data) {
            if (!first)
                builder.append(", ");
            first = false;
            if (obj instanceof Serializable)
                builder.append(((Serializable) obj).serializedJSON());
            else
                builder.append(obj.toString());
        }
        builder.append("]}");
        return builder.toString();
    }
}