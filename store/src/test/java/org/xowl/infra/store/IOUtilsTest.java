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

package org.xowl.infra.store;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for IOUtils
 *
 * @author Laurent Wouters
 */
public class IOUtilsTest {

    @Test
    public void testEncodeBase64() {
        Assert.assertEquals("YW55IGNhcm5hbCBwbGVhc3VyZS4=", IOUtils.encodeBase64("any carnal pleasure."));
        Assert.assertEquals("YW55IGNhcm5hbCBwbGVhc3VyZQ==", IOUtils.encodeBase64("any carnal pleasure"));
        Assert.assertEquals("YW55IGNhcm5hbCBwbGVhc3Vy", IOUtils.encodeBase64("any carnal pleasur"));
        Assert.assertEquals("YW55IGNhcm5hbCBwbGVhc3U=", IOUtils.encodeBase64("any carnal pleasu"));
        Assert.assertEquals("YW55IGNhcm5hbCBwbGVhcw==", IOUtils.encodeBase64("any carnal pleas"));
    }

    @Test
    public void testDecodeBase64() {
        Assert.assertEquals("any carnal pleasure.", IOUtils.decodeBase64("YW55IGNhcm5hbCBwbGVhc3VyZS4="));
        Assert.assertEquals("any carnal pleasure", IOUtils.decodeBase64("YW55IGNhcm5hbCBwbGVhc3VyZQ=="));
        Assert.assertEquals("any carnal pleasur", IOUtils.decodeBase64("YW55IGNhcm5hbCBwbGVhc3Vy"));
        Assert.assertEquals("any carnal pleasu", IOUtils.decodeBase64("YW55IGNhcm5hbCBwbGVhc3U="));
        Assert.assertEquals("any carnal pleas", IOUtils.decodeBase64("YW55IGNhcm5hbCBwbGVhcw=="));
    }
}
