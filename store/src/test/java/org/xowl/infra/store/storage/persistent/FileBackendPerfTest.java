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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Performance tests the FileStoreFile class
 *
 * @author Laurent Wouters
 */
public class FileBackendPerfTest {
    /**
     * Numbers of threads
     */
    private static final int THREAD_COUNT = 8;
    /**
     * The number of write per batch
     */
    private static final int WRITE_COUNT = 1024;
    /**
     * The number of batch per thread
     */
    private static final int BATCH_COUNT = 128;
    /**
     * The size of each write
     */
    private static final int WRITE_SIZE = 100;

    public static void main(String[] args) {
        FileBackendPerfTest program = new FileBackendPerfTest();
        try {
            program.testWritePerf();
        } catch (IOException | StorageException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Performance test
     *
     * @throws IOException      When an IO exception occurs
     * @throws StorageException When an IO exception occurs
     */
    public void testWritePerf() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        Collection<Thread> threads = new ArrayList<>();
        try (final FileStoreFile pf = new FileStoreFile(file, false, false)) {
            long begin = System.nanoTime();

            for (int i = 0; i != THREAD_COUNT; i++) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i != BATCH_COUNT; i++) {
                                for (int j = 0; j != WRITE_COUNT; j++) {
                                    long key = pf.allocateDirect(WRITE_SIZE);
                                    try (IOAccess access = pf.access(key, true)) {
                                        for (int k = 0; k != WRITE_SIZE >> 2; k++) {
                                            access.writeInt(j);
                                        }
                                    }
                                }
                                pf.flush();
                            }
                        } catch (StorageException exception) {
                            exception.printStackTrace();
                        }
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

            long end = System.nanoTime();

            float totalSize = ((float) (THREAD_COUNT * BATCH_COUNT * WRITE_COUNT * WRITE_SIZE)) / 1000000f;
            float time = ((float) (end - begin)) / 1000000000f;
            System.out.println("Threads: " + THREAD_COUNT);
            System.out.println("Total data size: " + totalSize + " Mo");
            System.out.println("Spent time: " + time + " s");
            System.out.println("Performance: " + (totalSize / time) + " Mo/s");
        }
    }
}
