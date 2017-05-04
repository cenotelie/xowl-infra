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

package org.xowl.infra.store.loaders;

import fr.cenotelie.hime.redist.ASTNode;

/**
 * Represents an exception that occurs while loading some data due to some error in the input data
 *
 * @author Laurent Wouters
 */
class LoaderException extends Exception {
    /**
     * The AST node from which the error originated
     */
    private final ASTNode origin;

    /**
     * Gets the AST node from which the error originated
     *
     * @return The AST node from which the error originated
     */
    public ASTNode getOrigin() {
        return origin;
    }

    /**
     * Initializes this exception
     *
     * @param description The description for this exception
     * @param origin      The AST node that from which the error originated
     */
    public LoaderException(String description, ASTNode origin) {
        super(description);
        this.origin = origin;
    }
}
