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

package org.xowl.infra.server.api;

import org.xowl.hime.redist.ASTNode;

/**
 * Represents a factory of xOWL Server API objects
 *
 * @author Laurent Wouters
 */
public interface XOWLFactory {
    /**
     * Creates a new object
     *
     * @param type       The object's type
     * @param definition The definition
     * @return The new object
     */
    Object newObject(String type, ASTNode definition);
}
