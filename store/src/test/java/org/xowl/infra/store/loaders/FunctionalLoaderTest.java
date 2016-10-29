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
package org.xowl.infra.store.loaders;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.utils.logging.SinkLogger;

/**
 * Tests for the functional loaders
 *
 * @author Laurent Wouters
 */
public class FunctionalLoaderTest {

    /**
     * Tests that the specified resource is correctly loaded
     *
     * @param uri The resource's URI
     */
    protected void testLoading(String uri) {
        SinkLogger logger = new SinkLogger();
        RepositoryRDF repository = new RepositoryRDF();
        try {
            repository.load(logger, uri);
        } catch (Exception exception) {
            Assert.fail("Failed to load resource " + exception.getMessage());
        }
        Assert.assertFalse("Failed to load resource " + uri, logger.isOnError());
    }

    @Test
    public void testLoadingOfDefinitionOWL2() {
        testLoading("http://xowl.org/lang/owl2");
    }
}
