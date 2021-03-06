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

package org.xowl.infra.server;

import fr.cenotelie.commons.utils.api.Reply;
import fr.cenotelie.commons.utils.api.ReplyResult;
import fr.cenotelie.commons.utils.api.ReplyResultCollection;
import fr.cenotelie.commons.utils.logging.BufferedLogger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xowl.infra.server.api.XOWLUser;
import org.xowl.infra.server.embedded.EmbeddedServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Integration test suite for the Embedded Server
 *
 * @author Laurent Wouters
 */
public class EmbeddedServerTest {
    /**
     * The logger to use
     */
    private static BufferedLogger logger;
    /**
     * The embedded server for the tests
     */
    private static EmbeddedServer server;

    @BeforeClass
    public static void setup() throws Exception {
        Path p = Files.createTempDirectory("EmbeddedServerTest");
        logger = new BufferedLogger();
        server = new EmbeddedServer(logger, new ServerConfiguration(p.toAbsolutePath().toString()));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (server != null) {
            server.close();
        }
    }

    @Test
    public void testIsLoggedIn() {
        Assert.assertTrue("Expected a user to be logged in", server.isLoggedIn());
    }

    @Test
    public void testGetLoggedIn() {
        XOWLUser reply = server.getLoggedInUser();
        Assert.assertTrue("Expected a user to be logged in", reply != null);
        Assert.assertTrue("Expected admin as logged-in user", reply.getIdentifier().equals("admin"));
    }

    @Test
    public void testLogin() {
        Reply reply = server.login("admin", "admin");
        Assert.assertTrue("Expected a successful reply", reply.isSuccess());
    }

    @Test
    public void testLogout() {
        Reply reply = server.logout();
        Assert.assertTrue("Expected logout to fail", !reply.isSuccess());
    }

    @Test
    public void testShutdown() {
        Reply reply = server.serverShutdown();
        Assert.assertTrue("Expected shutdown to fail", !reply.isSuccess());
    }

    @Test
    public void testRestart() {
        Reply reply = server.serverRestart();
        Assert.assertTrue("Expected restart to fail", !reply.isSuccess());
    }

    @Test
    public void testGetUser() {
        Reply reply = server.getUser("admin");
        Assert.assertTrue("Expected a successful reply", reply.isSuccess());
        XOWLUser user = ((ReplyResult<XOWLUser>) reply).getData();
        Assert.assertTrue("Expected admin as user", user.getIdentifier().equals("admin"));
    }

    @Test
    public void testCreateUser() {
        Reply reply = server.createUser("test", "test1234567890");
        Assert.assertTrue("Failed to create the user", reply.isSuccess());
        XOWLUser user = ((ReplyResult<XOWLUser>) reply).getData();
        Assert.assertTrue("Expected admin as user", user.getIdentifier().equals("test"));

        reply = server.getUsers();
        Assert.assertTrue("Failed to get the users", reply.isSuccess());
        Collection<XOWLUser> users = ((ReplyResultCollection<XOWLUser>) reply).getData();
        Assert.assertEquals("Invalid users", 2, users.size());
    }
}
