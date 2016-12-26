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

package org.xowl.infra.utils.product;

import org.xowl.infra.utils.TextUtils;

/**
 * Implements a plain license
 *
 * @author Laurent Wouters
 */
public class LicensePlain implements License {
    /**
     * The license's name
     */
    private final String name;
    /**
     * The full text for the license
     */
    private final String fullText;

    /**
     * Initializes this license
     *
     * @param name     The license's name
     * @param fullText The full text for the license
     */
    public LicensePlain(String name, String fullText) {
        this.name = name;
        this.fullText = fullText;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public synchronized String getFullText() {
        return fullText;
    }

    @Override
    public String serializedString() {
        return name;
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" +
                TextUtils.escapeStringJSON(License.class.getCanonicalName()) +
                "\", \"name\": \"" +
                TextUtils.escapeStringJSON(name) +
                "\", \"fullText\": \"" +
                TextUtils.escapeStringJSON(fullText) +
                "\"}";
    }
}