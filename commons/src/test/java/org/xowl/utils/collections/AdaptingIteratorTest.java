/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 **********************************************************************/

package org.xowl.utils.collections;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Tests for the adapting iterator
 */
public class AdaptingIteratorTest {
    private static final Integer[] content = new Integer[] {
        0, 1, 2, 3, 4 ,5 , 6, 7, 8, 9
    };

    @Test
    public void test_same_size() {
        Iterator<Integer> origin = Arrays.asList(content).iterator();
        AdaptingIterator<Integer, Integer> iterator = new AdaptingIterator<>(origin, new Adapter<Integer>() {
            @Override
            public <X> Integer adapt(X element) {
                return (Integer)element;
            }
        });
        int count = 0;
        while (iterator.hasNext()) {
            count++;
            iterator.next();
        }
        Assert.assertEquals(content.length, count);
    }

    @Test
    public void test_identity() {
        Iterator<Integer> origin = Arrays.asList(content).iterator();
        AdaptingIterator<Integer, Integer> iterator = new AdaptingIterator<>(origin, new Adapter<Integer>() {
            @Override
            public <X> Integer adapt(X element) {
                return (Integer)element;
            }
        });
        int index = 0;
        while (iterator.hasNext()) {
            Assert.assertEquals(content[index], iterator.next());
            index++;
        }
    }

    @Test
    public void test_adaptation() {
        Iterator<Integer> origin = Arrays.asList(content).iterator();
        AdaptingIterator<String, Integer> iterator = new AdaptingIterator<>(origin, new Adapter<String>() {
            @Override
            public <X> String adapt(X element) {
                return element.toString();
            }
        });
        int index = 0;
        while (iterator.hasNext()) {
            Assert.assertEquals(content[index].toString(), iterator.next());
            index++;
        }
    }
}
