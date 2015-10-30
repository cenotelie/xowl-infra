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

package org.xowl.store.storage;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.store.IRIs;
import org.xowl.store.Repository;
import org.xowl.store.TestLogger;
import org.xowl.store.storage.persistent.StorageException;
import org.xowl.utils.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Basic tests for the on-disk store
 *
 * @author Laurent Wouters
 */
public class OnDiskStoreTest {

    @Test
    public void testCreation() throws IOException, StorageException {
        Path p = Files.createTempDirectory("testCreation");
        OnDiskStore store = new OnDiskStore(p.toFile(), false);
        Assert.assertNotNull(store);
    }

    @Test
    public void testInsert() throws IOException, StorageException {
        Path p = Files.createTempDirectory("testInsert");
        OnDiskStore store = new OnDiskStore(p.toFile(), false);
        Logger logger = new TestLogger();
        Repository repo = new Repository(store);
        repo.load(logger, IRIs.RDF);
    }
}
