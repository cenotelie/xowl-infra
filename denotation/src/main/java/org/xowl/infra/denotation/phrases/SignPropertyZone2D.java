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

package org.xowl.infra.denotation.phrases;

/**
 * Represents the "zone2d" property for a visual sign in a 2D graph
 * The zone is expected to be an identifier that corresponds to an element in the original phrases's representation
 *
 * @author Laurent Wouters
 */
public class SignPropertyZone2D extends SignProperty {
    /**
     * The URI for this property
     */
    public static final String URI = "http://xowl.org/infra/denotation/property/zone2d";

    /**
     * The singleton instance
     */
    public static final SignProperty INSTANCE = new SignPropertyZone2D();

    /**
     * Initializes this property
     */
    private SignPropertyZone2D() {
        super(URI, "zone2d", false);
    }

    @Override
    public boolean isValidValue(Object value) {
        return value != null;
    }
}
