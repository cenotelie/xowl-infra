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
 * Represents a provider of IO elements for accesses
 *
 * @author Laurent Wouters
 */
public interface IOBackend {
    /**
     * Requests the IO element that can support the specified access
     *
     * @param access An access request
     * @return The supporting IO element
     * @throws StorageException When the backend is in a bad state
     */
    IOElement onAccessRequested(IOAccess access) throws StorageException;

    /**
     * Event when an access is terminated for this backend
     *
     * @param access  The access being terminated
     * @param element The used supporting IO element
     * @throws StorageException When the backend is in a bad state
     */
    void onAccessTerminated(IOAccess access, IOElement element) throws StorageException;
}
