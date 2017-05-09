/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.denotation;

/**
 * Represents the "texture" property for a symbol
 *
 * @author Laurent Wouters
 */
public class SymbolPropertyTexture extends SymbolProperty {
    /**
     * The URI for this property
     */
    public static final String URI = "http://xowl.org/infra/denotation/property/texture";

    /**
     * The singleton instance
     */
    public static final SymbolProperty INSTANCE = new SymbolPropertyTexture();

    /**
     * Initializes this property
     */
    private SymbolPropertyTexture() {
        super(URI, "texture", true);
    }
}
