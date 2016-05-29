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

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Tests for the persisted map implementation
 *
 * @author Laurent Wouters
 */
public class PersistedMapTest {

    @Test
    public void testInserts() throws StorageException {
        try (FileStore store = new FileStore(new File("/home/laurent"), "laurent", false)) {
            PersistedMap map = PersistedMap.create(store);
            for (int i = 0; i != 1024; i++) {
                Assert.assertTrue(map.tryPut(i, i));
            }
            store.flush();
            for (int i = 0; i != 1024; i++) {
                Assert.assertTrue("Failed at " + i, map.compareAndSet(i, i, i + 1));
            }
            store.flush();
        }
    }
}
