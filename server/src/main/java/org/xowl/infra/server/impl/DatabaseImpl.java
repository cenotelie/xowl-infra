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
import org.xowl.infra.store.ProxyObject;

/**
 * Implementation of a XOWL database
 *
 * @author Laurent Wouters
 */
public class DatabaseImpl extends BaseDatabase {
    /**
     * The associated database controller
     */
    protected final DatabaseController controller;
    /**
     * The proxy object that represents the database in the administration database
     */
    protected final ProxyObject proxy;

    /**
     * Initializes this structure
     *
     * @param controller The associated database controller
     * @param proxy      The proxy object that represents the database in the administration database
     */
    public DatabaseImpl(DatabaseController controller, ProxyObject proxy) {
        super((String) proxy.getDataValue(Schema.ADMIN_NAME));
        this.controller = controller;
        this.proxy = proxy;
    }

    /**
     * Initializes this structure
     *
     * @param controller The associated database controller
     * @param proxy      The proxy object that represents the database in the administration database
     * @param name       The name of the database
     */
    public DatabaseImpl(DatabaseController controller, ProxyObject proxy, String name) {
        super(name);
        this.controller = controller;
        this.proxy = proxy;
    }
}
