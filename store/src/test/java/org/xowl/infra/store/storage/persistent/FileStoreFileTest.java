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

package org.xowl.infra.store.storage.persistent;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

/**
 * Tests the PersistedFile class
 *
 * @author Laurent Wouters
 */
public class FileStoreFileTest {
    @Test
    public void testGetSizeEmpty() throws IOException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        Assert.assertEquals("Unexpected length", 0, pf.getSize());
        pf.close();
    }

    @Test
    public void testGetSizeSingleBlock() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeByte((byte) 5);
        Assert.assertEquals("Unexpected length", FileStoreFile.BLOCK_SIZE, pf.getSize());
        pf.close();
    }

    @Test
    public void testGetSizeDoubleBlock() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeByte((byte) 5);
        pf.seek(FileStoreFile.BLOCK_SIZE);
        pf.writeByte((byte) 6);
        Assert.assertEquals("Unexpected length", FileStoreFile.BLOCK_SIZE * 2, pf.getSize());
        pf.close();
    }

    @Test
    public void testGetSizeAfterSeekAndNoWrite() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.seek(FileStoreFile.BLOCK_SIZE);
        Assert.assertEquals("Unexpected length", 0, pf.getSize());
        pf.close();
    }

    @Test
    public void testGetSizeAfterReload() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeByte((byte) 5);
        pf.commit();
        pf.close();

        pf = new FileStoreFile(file);
        Assert.assertEquals("Unexpected length", FileStoreFile.BLOCK_SIZE, pf.getSize());
        pf.close();
    }

    @Test
    public void testGetSizeAfterReloadEmpty() throws IOException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.commit();
        pf.close();

        pf = new FileStoreFile(file);
        Assert.assertEquals("Unexpected length", 0, pf.getSize());
        pf.close();
    }


    @Test
    public void testGetIndexOnCreation() throws IOException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        Assert.assertEquals("Unexpected index", 0, pf.getIndex());
        pf.close();
    }

    @Test
    public void testGetIndexAfterSeek() throws IOException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.seek(FileStoreFile.BLOCK_SIZE + 4);
        Assert.assertEquals("Unexpected index", FileStoreFile.BLOCK_SIZE + 4, pf.getIndex());
        pf.close();
    }

    @Test
    public void testGetIndexAfterWriteByte() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.seek(4);
        pf.writeByte((byte) 5);
        Assert.assertEquals("Unexpected index", 5, pf.getIndex());
        pf.close();
    }

    @Test
    public void testGetIndexAfterWriteBytes() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.seek(4);
        pf.writeBytes(new byte[]{0x5, 0x6, 0x7});
        Assert.assertEquals("Unexpected index", 7, pf.getIndex());
        pf.close();
    }

    @Test
    public void testGetIndexAfterWriteChar() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.seek(4);
        pf.writeChar('a');
        Assert.assertEquals("Unexpected index", 6, pf.getIndex());
        pf.close();
    }

    @Test
    public void testGetIndexAfterWriteInt() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.seek(4);
        pf.writeInt(55);
        Assert.assertEquals("Unexpected index", 8, pf.getIndex());
        pf.close();
    }

    @Test
    public void testGetIndexAfterWriteLong() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.seek(4);
        pf.writeLong(0x00BB00AA00FF00EEL);
        Assert.assertEquals("Unexpected index", 12, pf.getIndex());
        pf.close();
    }

    @Test
    public void testGetIndexAfterWriteFloat() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.seek(4);
        pf.writeFloat(5.5f);
        Assert.assertEquals("Unexpected index", 8, pf.getIndex());
        pf.close();
    }

    @Test
    public void testGetIndexAfterWriteDouble() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.seek(4);
        pf.writeDouble(5.5f);
        Assert.assertEquals("Unexpected index", 12, pf.getIndex());
        pf.close();
    }


    @Test
    public void testWriteSimpleByte() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeByte((byte) 5);
        pf.commit();
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) FileStoreFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", FileStoreFile.BLOCK_SIZE, content.length);
        Assert.assertEquals("Unexpected content", 5, content[0]);
    }

    @Test
    public void testReadSimpleByte() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeByte((byte) 5);
        pf.commit();
        pf.close();

        pf = new FileStoreFile(file);
        Assert.assertEquals("Unexpected content", 5, pf.readByte());
        pf.close();
    }

    @Test
    public void testWriteSimpleBytes() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeBytes(new byte[]{0x5, 0x6, 0x7});
        pf.commit();
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) FileStoreFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", FileStoreFile.BLOCK_SIZE, content.length);
        Assert.assertEquals("Unexpected content", 5, content[0]);
        Assert.assertEquals("Unexpected content", 6, content[1]);
        Assert.assertEquals("Unexpected content", 7, content[2]);
    }

    @Test
    public void testReadSimpleBytes() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeBytes(new byte[]{0x5, 0x6, 0x7});
        pf.commit();
        pf.close();

        pf = new FileStoreFile(file);
        Assert.assertEquals("Unexpected content", 5, pf.readByte());
        Assert.assertEquals("Unexpected content", 6, pf.readByte());
        Assert.assertEquals("Unexpected content", 7, pf.readByte());
        pf.close();
    }

    @Test
    public void testWriteSimpleChar() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeChar((char) 0xBBCC);
        pf.commit();
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) FileStoreFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", FileStoreFile.BLOCK_SIZE, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertEquals("Unexpected content", (char) 0xBBCC, buffer.getChar());
    }

    @Test
    public void testReadSimpleChar() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeChar((char) 0xBBCC);
        pf.commit();
        pf.close();

        pf = new FileStoreFile(file);
        Assert.assertEquals("Unexpected content", (char) 0xBBCC, pf.readChar());
        pf.close();
    }

    @Test
    public void testWriteSimpleInt() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeInt(55);
        pf.commit();
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) FileStoreFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", FileStoreFile.BLOCK_SIZE, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertEquals("Unexpected content", 55, buffer.getInt());
    }

    @Test
    public void testReadSimpleInt() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeInt(55);
        pf.commit();
        pf.close();

        pf = new FileStoreFile(file);
        Assert.assertEquals("Unexpected content", 55, pf.readInt());
        pf.close();
    }

    @Test
    public void testWriteSimpleLong() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeLong(0x00BB00AA00FF00EEL);
        pf.commit();
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) FileStoreFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", FileStoreFile.BLOCK_SIZE, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertEquals("Unexpected content", 0x00BB00AA00FF00EEL, buffer.getLong());
    }

    @Test
    public void testReadSimpleLong() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeLong(0x00BB00AA00FF00EEL);
        pf.commit();
        pf.close();

        pf = new FileStoreFile(file);
        Assert.assertEquals("Unexpected content", 0x00BB00AA00FF00EEL, pf.readLong());
        pf.close();
    }

    @Test
    public void testWriteSimpleFloat() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeFloat(5.5f);
        pf.commit();
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) FileStoreFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", FileStoreFile.BLOCK_SIZE, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertTrue("Unexpected content", 5.5f == buffer.getFloat());
    }

    @Test
    public void testReadSimpleFloat() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeFloat(5.5f);
        pf.commit();
        pf.close();

        pf = new FileStoreFile(file);
        Assert.assertTrue("Unexpected content", 5.5f == pf.readFloat());
        pf.close();
    }

    @Test
    public void testWriteSimpleDouble() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeDouble(5.5d);
        pf.commit();
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) FileStoreFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", FileStoreFile.BLOCK_SIZE, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertTrue("Unexpected content", 5.5f == buffer.getDouble());
    }

    @Test
    public void testReadSimpleDouble() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeDouble(5.5d);
        pf.commit();
        pf.close();

        pf = new FileStoreFile(file);
        Assert.assertTrue("Unexpected content", 5.5d == pf.readDouble());
        pf.close();
    }

    @Test
    public void testWriteWithinBlock() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeInt(55);
        pf.seek(8);
        pf.writeInt(66);
        pf.commit();
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) FileStoreFile.BLOCK_SIZE, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", FileStoreFile.BLOCK_SIZE, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertEquals("Unexpected content", 55, buffer.getInt());
        Assert.assertEquals("Unexpected content", 0, buffer.getInt());
        Assert.assertEquals("Unexpected content", 66, buffer.getInt());
    }

    @Test
    public void testWriteTwoBlock() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeInt(55);
        pf.seekNextBlock();
        pf.writeInt(66);
        pf.commit();
        pf.close();
        Assert.assertTrue("File has not been created", file.exists());
        Assert.assertEquals("Unexpected file length", (long) FileStoreFile.BLOCK_SIZE * 2, file.length());
        byte[] content = Files.readAllBytes(file.toPath());
        Assert.assertEquals("Unexpected content length", FileStoreFile.BLOCK_SIZE * 2, content.length);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        Assert.assertEquals("Unexpected content", 55, buffer.getInt());
        buffer.position(FileStoreFile.BLOCK_SIZE);
        Assert.assertEquals("Unexpected content", 66, buffer.getInt());
    }

    @Test
    public void testReadWrittenData() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeInt(55);
        pf.seekNextBlock();
        pf.writeInt(66);
        pf.seek(0);
        Assert.assertEquals("Unexpected content", 55, pf.readInt());
        pf.seek(FileStoreFile.BLOCK_SIZE);
        Assert.assertEquals("Unexpected content", 66, pf.readInt());
        pf.close();
    }

    @Test
    public void testReload() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeInt(55);
        pf.seekNextBlock();
        pf.writeInt(66);
        pf.seek(0);
        pf.commit();
        pf.close();

        pf = new FileStoreFile(file);
        Assert.assertEquals("Unexpected content", 55, pf.readInt());
        pf.seek(FileStoreFile.BLOCK_SIZE);
        Assert.assertEquals("Unexpected content", 66, pf.readInt());
        pf.close();
    }

    @Test
    public void testRollback() throws IOException, StorageException {
        File file = File.createTempFile("test", ".bin");
        FileStoreFile pf = new FileStoreFile(file);
        pf.writeInt(55);
        pf.rollback();
        pf.seek(0);
        Assert.assertEquals("Unexpected content", 55, pf.readInt());
        pf.close();
    }
}
