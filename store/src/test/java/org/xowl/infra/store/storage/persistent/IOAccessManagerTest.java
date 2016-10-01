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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Tests for the correct management of the IO accesses
 *
 * @author Laurent Wouters
 */
public class IOAccessManagerTest {
    /**
     * Numbers of threads
     */
    private static final int THREAD_COUNT = 16;
    /**
     * The number of entries to insert
     */
    private static final int ACCESSES_COUNT = 8192;


    /**
     * Tests for the management of concurrent accesses
     */
    @Test
    public void testConcurrentAccesses() {
        Collection<Thread> threads = new ArrayList<>();
        final boolean successes[] = new boolean[THREAD_COUNT];
        final Random random = new Random();
        final IOAccessManager manager = new IOAccessManager(new IOBackend() {
            @Override
            public IOElement onAccessRequested(IOAccess access) throws StorageException {
                return null;
            }

            @Override
            public void onAccessTerminated(IOAccess access, IOElement element) throws StorageException {
                // do nothing
            }
        });

        for (int i = 0; i != THREAD_COUNT; i++) {
            final int index = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean success = true;
                    for (int i = 0; i != ACCESSES_COUNT; i++) {
                        int location = random.nextInt() & 0xFFFF;
                        int length = random.nextInt() & 0x00FF;
                        try (IOAccess access = manager.get(location, length, false)) {
                            Assert.assertEquals(location, access.getLocation());
                            Assert.assertEquals(length, access.getLength());
                        } catch (StorageException exception) {
                            exception.printStackTrace();
                            success = false;
                        }
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
        for (int i = 0; i != THREAD_COUNT; i++) {
            Assert.assertTrue(successes[i]);
        }
    }
}
