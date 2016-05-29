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

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Tests for the persisted map implementation
 *
 * @author Laurent Wouters
 */
public class PersistedMapTest {
    /**
     * Numbers of threads
     */
    private static final int THREAD_COUNT = 8;
    /**
     * The number of entries to insert
     */
    private static final int ENTRIES = 1024;

    /**
     * Tests the map for simple insertions and atomic replace
     *
     * @throws StorageException When an IO operation fails
     */
    @Test
    public void testInserts() throws StorageException, IOException {
        try (FileStore store = new FileStore(Files.createTempDirectory("PersistedMapTest_testInserts").toFile(), "store", false)) {
            PersistedMap map = PersistedMap.create(store);
            for (int i = 0; i != ENTRIES; i++) {
                Assert.assertTrue("Failed at " + i, map.tryPut(i, i));
            }
            store.flush();
            for (int i = 0; i != ENTRIES; i++) {
                Assert.assertTrue("Failed at " + i, map.compareAndSet(i, i, i + 1));
            }
            store.flush();
            for (int i = 0; i != ENTRIES; i++) {
                Assert.assertFalse("Failed at " + i, map.compareAndSet(i, i, i + 1));
            }
            for (int i = 0; i != ENTRIES; i++) {
                Assert.assertEquals("Wrong mapping", i + 1, map.get(i));
            }
        }
    }

    /**
     * Tests the map for concurrent insertions and atomic replace
     *
     * @throws IOException
     * @throws StorageException When an IO operation fails
     */
    @Test
    public void testConcurrentInserts() throws IOException, StorageException {
        Collection<Thread> threads = new ArrayList<>();
        final boolean successes[] = new boolean[THREAD_COUNT];
        try (final FileStore store = new FileStore(Files.createTempDirectory("PersistedMapTest_testConcurrentInserts").toFile(), "store", false)) {
            final PersistedMap map = PersistedMap.create(store);
            for (int i = 0; i != THREAD_COUNT; i++) {
                final int index = i;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean success = true;
                        try {
                            for (int i = index; i < ENTRIES; i += THREAD_COUNT) {
                                if (!map.tryPut(i, i)) {
                                    success = false;
                                    break;
                                }
                            }
                            for (int i = index; i < ENTRIES; i += THREAD_COUNT) {
                                if (!map.compareAndSet(i, i, i + 1)) {
                                    success = false;
                                    break;
                                }
                            }
                            for (int i = index; i < ENTRIES; i += THREAD_COUNT) {
                                if (map.compareAndSet(i, i, i + 1)) {
                                    success = false;
                                    break;
                                }
                            }
                        } catch (StorageException exception) {
                            success = false;
                        }
                        successes[index] = success;
                    }
                }, "Test Thread " + i);
                threads.add(thread);
                thread.start();
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
            store.flush();
            for (int i = 0; i != THREAD_COUNT; i++) {
                Assert.assertTrue(successes[i]);
            }
            for (int i = 0; i != ENTRIES; i++) {
                Assert.assertEquals("Wrong mapping", i + 1, map.get(i));
            }
        }
    }
}
