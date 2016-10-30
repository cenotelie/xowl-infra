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
package org.xowl.infra.generator;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.infra.utils.config.Configuration;
import org.xowl.infra.utils.logging.SinkLogger;

/**
 * Tests for the generator
 *
 * @author Laurent Wouters
 */
public class GeneratorTest {

    /**
     * Tests the correct re-generation of the xOWL abstract syntax implementation
     */
    @Test
    public void reflectiveXOWLGeneration() {
        SinkLogger logger = new SinkLogger();
        Configuration config = new Configuration();
        config.add(null, "input", "http://xowl.org/lang/owl2");
        config.add(null, "input", "http://xowl.org/lang/actions");
        config.add(null, "input", "http://xowl.org/lang/rules");
        config.add(null, "input", "http://xowl.org/lang/runtime");
        config.add(null, "input", "http://xowl.org/lang/instrumentation");
        config.add(null, "basePackage", "org.xowl.lang");
        config.add(null, "output", "target/tests/");
        config.add(null, "jarName", "XOWLModel");
        Program program = new Program(logger, config);
        program.execute();
        Assert.assertFalse("Failed to re-generate xOWL abstract syntax", logger.isOnError());
    }
}
