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

package org.xowl.infra.server.impl;

import org.xowl.infra.server.base.BaseDatabase;

/**
 * Implementation of a XOWL database
 *
 * @author Laurent Wouters
 */
public class DatabaseImpl extends BaseDatabase {
    /**
     * The parent server controller
     */
    protected final ControllerServer serverController;
    /**
     * The associated database controller
     */
    protected final ControllerDatabase dbController;

    /**
     * Initializes this structure
     *
     * @param serverController The parent server controller
     * @param dbController     The associated database controller
     */
    public DatabaseImpl(ControllerServer serverController, ControllerDatabase dbController) {
        super(dbController.getName());
        this.serverController = serverController;
        this.dbController = dbController;
    }
}
