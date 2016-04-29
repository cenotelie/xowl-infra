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

package org.xowl.infra.store.storage.persistent;

/**
 * Represents the failure to meet the expectations of the storage layer
 *
 * @author Laurent Wouters
 */
public class StorageException extends Exception {
    /**
     * Initializes this exception
     *
     * @param message The exception's message
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Initializes this exception
     *
     * @param cause   The original exception
     * @param message The exception's message
     */
    public StorageException(Exception cause, String message) {
        super(message, cause);
    }
}
