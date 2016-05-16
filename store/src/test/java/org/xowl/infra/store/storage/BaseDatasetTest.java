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
import org.xowl.infra.store.rdf.Quad;

import java.util.Iterator;

/**
 * Common tests for dataset implementations
 *
 * @author Laurent Wouters
 */
public class BaseDatasetTest {
    /**
     * Tests the getMultiplicity functionality on a store
     * @param store The store to test
     */
    protected void testMultiplicityEmpty(BaseStore store) {
        Quad quad = new Quad(
                store.getIRINode("http://xowl.org/tests/g"),
                store.getIRINode("http://xowl.org/tests/x"),
                store.getIRINode("http://xowl.org/tests/p"),
                store.getIRINode("http://xowl.org/tests/y")
        );
        try {
            long result = store.getMultiplicity(quad);
            Assert.assertEquals(0, result);
        } catch (UnsupportedNodeType exception) {
            Assert.fail(exception.getMessage());
        }
    }

    /**
     * Tests the getMultiplicity functionality on a store
     * @param store The store to test
     */
    protected void testMultiplicitySingle(BaseStore store) throws UnsupportedNodeType {
        Quad quad = new Quad(
                store.getIRINode("http://xowl.org/tests/g"),
                store.getIRINode("http://xowl.org/tests/x"),
                store.getIRINode("http://xowl.org/tests/p"),
                store.getIRINode("http://xowl.org/tests/y")
        );
        long result = store.getMultiplicity(quad);
        store.add(quad);
        Assert.assertEquals(1, result);
    }

    /**
     * Tests the getMultiplicity functionality on a store
     * @param store The store to test
     */
    protected void testMultiplicityMore(BaseStore store) throws UnsupportedNodeType {
        Quad quad = new Quad(
                store.getIRINode("http://xowl.org/tests/g"),
                store.getIRINode("http://xowl.org/tests/x"),
                store.getIRINode("http://xowl.org/tests/p"),
                store.getIRINode("http://xowl.org/tests/y")
        );
        long result = store.getMultiplicity(quad);
        store.add(quad);
        store.add(quad);
        store.add(quad);
        Assert.assertEquals(3, result);
    }

    /**
     * Tests the getMultiplicity functionality on a store
     * @param store The store to test
     */
    protected void testMultiplicityOther(BaseStore store) throws UnsupportedNodeType {
        Quad quad1 = new Quad(
                store.getIRINode("http://xowl.org/tests/g"),
                store.getIRINode("http://xowl.org/tests/x1"),
                store.getIRINode("http://xowl.org/tests/p"),
                store.getIRINode("http://xowl.org/tests/y1")
        );
        Quad quad2 = new Quad(
                store.getIRINode("http://xowl.org/tests/g"),
                store.getIRINode("http://xowl.org/tests/x2"),
                store.getIRINode("http://xowl.org/tests/p"),
                store.getIRINode("http://xowl.org/tests/y2")
        );
        store.add(quad1);
        long result = store.getMultiplicity(quad2);
        Assert.assertEquals(0, result);
    }

    /**
     * Tests the getAll functionality on a store
     * @param store The store to test
     */
    protected void testGetAllEmpty(BaseStore store) {
        Iterator<Quad> iterator = store.getAll();
        Assert.assertFalse(iterator.hasNext());
    }

    /**
     * Tests the getAll functionality on a store
     * @param store The store to test
     */
    protected void testGetAllSingle(BaseStore store) throws UnsupportedNodeType {
        Quad quad1 = new Quad(
                store.getIRINode("http://xowl.org/tests/g"),
                store.getIRINode("http://xowl.org/tests/x1"),
                store.getIRINode("http://xowl.org/tests/p"),
                store.getIRINode("http://xowl.org/tests/y1")
        );
    }
}
