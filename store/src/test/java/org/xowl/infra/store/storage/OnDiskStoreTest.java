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

package org.xowl.infra.store.storage;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.infra.store.IRIs;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.TestLogger;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.utils.logging.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Basic tests for the on-disk store
 *
 * @author Laurent Wouters
 */
public class OnDiskStoreTest {

    @Test
    public void testCreation() throws Exception {
        Path p = Files.createTempDirectory("testCreation");
        OnDiskStore store = new OnDiskStore(p.toFile(), false);
        Assert.assertNotNull(store);
        store.close();
    }

    @Test
    public void testInsert() throws Exception {
        Path p = Files.createTempDirectory("testInsert");
        OnDiskStore store = new OnDiskStore(p.toFile(), false);
        Logger logger = new TestLogger();
        Repository repo = new Repository(store);
        repo.load(logger, IRIs.RDF);
        store.commit();
        store.close();

        store = new OnDiskStore(p.toFile(), true);
        Iterator<Quad> iterator = store.getAll();
        while (iterator.hasNext()) {
            Quad quad = iterator.next();
            System.out.println(quad);
        }
        store.close();
    }

    @Test
    public void testInsert2() throws Exception {
        Path p = Files.createTempDirectory("testInsert");
        OnDiskStore store = new OnDiskStore(p.toFile(), false);
        Logger logger = new TestLogger();
        Repository repo = new Repository(store);

        Quad quad1 = new Quad(
                store.getIRINode("http://xowl.org/tests/g"),
                store.getIRINode("http://xowl.org/tests/x"),
                store.getIRINode("http://xowl.org/tests/p"),
                store.getIRINode("http://xowl.org/tests/y1")
        );
        Quad quad2 = new Quad(
                store.getIRINode("http://xowl.org/tests/g"),
                store.getIRINode("http://xowl.org/tests/x"),
                store.getIRINode("http://xowl.org/tests/p"),
                store.getIRINode("http://xowl.org/tests/y2")
        );

        repo.getStore().add(quad1);
        repo.getStore().add(quad2);
        Iterator<Quad> iterator = store.getAll(quad1.getSubject(), quad1.getProperty(), null);
        while (iterator.hasNext()) {
            Quad quad = iterator.next();
            System.out.println(quad);
        }
        store.commit();
        store.close();
    }
}
