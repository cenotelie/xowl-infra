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

package org.xowl.store.storage.persistent;

import java.io.IOException;

/**
 * Implements an empty value that can be persisted
 *
 * @author Laurent Wouters
 */
class PersistableEmptyValue implements Persistable {
    /**
     * The singleton instance
     */
    private static PersistableEmptyValue INSTANCE = null;

    /**
     * Gets the instance for this value
     *
     * @return The instance for this value
     */
    public static synchronized Persistable instance() {
        if (INSTANCE == null)
            INSTANCE = new PersistableEmptyValue();
        return INSTANCE;
    }

    /**
     * Initializes this value
     */
    private PersistableEmptyValue() {
    }

    @Override
    public int persistedLength() {
        return 0;
    }

    @Override
    public void persist(PersistedFile file) throws IOException {

    }

    @Override
    public boolean isPersistedIn(PersistedFile file) throws IOException {
        return true;
    }
}
