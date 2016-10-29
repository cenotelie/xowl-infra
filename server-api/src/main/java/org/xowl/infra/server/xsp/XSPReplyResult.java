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

package org.xowl.infra.server.xsp;

import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;

/**
 * Implements a successful reply to a xOWL server request with an object of type T as a response
 *
 * @param <T> The type of return data
 * @author Laurent Wouters
 */
public class XSPReplyResult<T> implements XSPReply {
    /**
     * The payload
     */
    private final T data;

    /**
     * Gets the payload
     *
     * @return The payload
     */
    public T getData() {
        return data;
    }

    /**
     * Initializes this result
     *
     * @param data The payload
     */
    public XSPReplyResult(T data) {
        this.data = data;
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
        return data == null ? "NO DATA" : data.toString();
    }

    @Override
    public String serializedJSON() {
        if (data == null)
            return "{ \"isSuccess\": false, \"message\": \"NO DATA\", \"payload\": \"\" }";
        if (data instanceof Serializable)
            return "{ \"isSuccess\": true, \"message\": \"\", \"payload\": " + ((Serializable) data).serializedJSON() + " }";
        return "{ \"isSuccess\": true, \"message\": \"\", \"payload\": \"" + TextUtils.escapeStringJSON(data.toString()) + "\" }";
    }
}
