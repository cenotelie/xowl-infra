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
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.LiteralNode;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Tests for the persisted nodes
 *
 * @author Laurent Wouters
 */
public class PersistedNodesTest {

    @Test
    public void testStoreShortString() throws IOException, StorageException {
        try (PersistedNodes nodes = new PersistedNodes(Files.createTempDirectory("PersistedNodesTest_testStoreShortString").toFile(), false)) {
            LiteralNode literal = nodes.getLiteralNode("toto", Vocabulary.xsdString, null);
            Assert.assertEquals("toto", literal.getLexicalValue());
        }
    }

    @Test
    public void testStoreLongString() throws IOException, StorageException {
        int length = FileBlock.BLOCK_SIZE * 2;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != length; i++)
            builder.append("x");
        String value = builder.toString();

        try (PersistedNodes nodes = new PersistedNodes(Files.createTempDirectory("PersistedNodesTest_testStoreLongString").toFile(), false)) {
            LiteralNode literal = nodes.getLiteralNode(value, Vocabulary.xsdString, null);
            Assert.assertEquals(value, literal.getLexicalValue());
        }
    }
}
