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

package org.xowl.store;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.store.storage.persistent.PersistedFile;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

/**
 * Tests the PersistedFile class
 *
 * @author Laurent Wouters
 */
public class PersistedFileTest {

    @Test
    public void testWriteSimpleByte() throws IOException {
        File file = File.createTempFile("test", ".bin");
        PersistedFile pf = new PersistedFile(file);
        pf.writeByte((byte) 5);
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) PersistedFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", PersistedFile.BLOCK_SIZE, content.length);
        Assert.assertEquals("Unexpected content", 5, content[0]);
    }

    @Test
    public void testWriteSimpleChar() throws IOException {
        File file = File.createTempFile("test", ".bin");
        PersistedFile pf = new PersistedFile(file);
        pf.writeChar('a');
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) PersistedFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", PersistedFile.BLOCK_SIZE, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertEquals("Unexpected content", 'a', buffer.getChar());
    }

    @Test
    public void testWriteSimpleInt() throws IOException {
        File file = File.createTempFile("test", ".bin");
        PersistedFile pf = new PersistedFile(file);
        pf.writeInt(55);
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) PersistedFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", PersistedFile.BLOCK_SIZE, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertEquals("Unexpected content", 55, buffer.getInt());
    }

    @Test
    public void testWriteSimpleLong() throws IOException {
        File file = File.createTempFile("test", ".bin");
        PersistedFile pf = new PersistedFile(file);
        pf.writeLong(0x00BB00AA00FF00EEl);
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) PersistedFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", PersistedFile.BLOCK_SIZE, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertEquals("Unexpected content", 0x00BB00AA00FF00EEl, buffer.getLong());
    }

    @Test
    public void testWriteSimpleFloat() throws IOException {
        File file = File.createTempFile("test", ".bin");
        PersistedFile pf = new PersistedFile(file);
        pf.writeFloat(5.5f);
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) PersistedFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", PersistedFile.BLOCK_SIZE, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertTrue("Unexpected content", 5.5f == buffer.getFloat());
    }

    @Test
    public void testWriteSimpleDouble() throws IOException {
        File file = File.createTempFile("test", ".bin");
        PersistedFile pf = new PersistedFile(file);
        pf.writeDouble(5.5f);
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) PersistedFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", PersistedFile.BLOCK_SIZE, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertTrue("Unexpected content", 5.5f == buffer.getDouble());
    }

    @Test
    public void testWriteWithinBlock() throws IOException {
        File file = File.createTempFile("test", ".bin");
        PersistedFile pf = new PersistedFile(file);
        pf.writeInt(55);
        pf.seek(8);
        pf.writeInt(66);
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) PersistedFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", PersistedFile.BLOCK_SIZE, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertEquals("Unexpected content", 55, buffer.getInt());
        Assert.assertEquals("Unexpected content", 0, buffer.getInt());
        Assert.assertEquals("Unexpected content", 66, buffer.getInt());
    }

    @Test
    public void testWriteTwoBlock() throws IOException {
        File file = File.createTempFile("test", ".bin");
        PersistedFile pf = new PersistedFile(file);
        pf.writeInt(55);
        pf.seekNextBlock();
        pf.writeInt(66);
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) PersistedFile.BLOCK_SIZE * 2, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", PersistedFile.BLOCK_SIZE * 2, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertEquals("Unexpected content", 55, buffer.getInt());
        buffer.position(PersistedFile.BLOCK_SIZE);
        Assert.assertEquals("Unexpected content", 66, buffer.getInt());
    }

    @Test
    public void testReadWrittenData() throws IOException {
        File file = File.createTempFile("test", ".bin");
        PersistedFile pf = new PersistedFile(file);
        pf.writeInt(55);
        pf.seekNextBlock();
        pf.writeInt(66);
        pf.seek(0);
        Assert.assertEquals("Unexpected content", 55, pf.readInt());
        pf.seek(PersistedFile.BLOCK_SIZE);
        Assert.assertEquals("Unexpected content", 66, pf.readInt());
        pf.close();
    }

    @Test
    public void testReload() throws IOException {
        File file = File.createTempFile("test", ".bin");
        PersistedFile pf = new PersistedFile(file);
        pf.writeInt(55);
        pf.seekNextBlock();
        pf.writeInt(66);
        pf.seek(0);
        pf.close();

        pf = new PersistedFile(file);
        Assert.assertEquals("Unexpected content", 55, pf.readInt());
        pf.seek(PersistedFile.BLOCK_SIZE);
        Assert.assertEquals("Unexpected content", 66, pf.readInt());
        pf.close();
    }

    @Test
    public void testWriteOverBlockBoundary() throws IOException {
        File file = File.createTempFile("test", ".bin");
        PersistedFile pf = new PersistedFile(file);
        pf.seek(PersistedFile.BLOCK_SIZE - 2);
        pf.writeInt(55);
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) PersistedFile.BLOCK_SIZE * 2, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", PersistedFile.BLOCK_SIZE * 2, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        buffer.position(PersistedFile.BLOCK_SIZE - 2);
        Assert.assertEquals("Unexpected content", 55, buffer.getInt());
    }

    @Test
    public void testReadOverBlockBoundary() throws IOException {
        File file = File.createTempFile("test", ".bin");
        PersistedFile pf = new PersistedFile(file);
        pf.seek(PersistedFile.BLOCK_SIZE - 2);
        pf.writeInt(55);
        pf.close();

        pf = new PersistedFile(file);
        pf.seek(PersistedFile.BLOCK_SIZE - 2);
        Assert.assertEquals("Unexpected content", 55, pf.readInt());
        pf.close();
    }
}
