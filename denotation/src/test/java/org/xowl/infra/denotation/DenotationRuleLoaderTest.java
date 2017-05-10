/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.denotation;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.infra.denotation.rules.DenotationRule;
import org.xowl.infra.denotation.rules.DenotationRuleLoader;
import org.xowl.infra.utils.AutoReader;
import org.xowl.infra.utils.logging.BufferedLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;

/**
 * Test suite for the denotation rule test loader
 *
 * @author Laurent Wouters
 */
public class DenotationRuleLoaderTest {

    /**
     * Tests the correct parsing of denotation rules
     */
    @Test
    public void testParse() throws IOException {
        DenotationRuleLoader loader = new DenotationRuleLoader();
        try (InputStream stream = DenotationRuleLoader.class.getResourceAsStream("/org/xowl/infra/denotation/test.denotation")) {
            Reader reader = new AutoReader(stream);
            BufferedLogger logger = new BufferedLogger();
            Collection<DenotationRule> rules = loader.load(reader, logger);
            Assert.assertTrue("Errors while loading the rules", logger.getErrorMessages().isEmpty());
            Assert.assertTrue("Failed to load the rules", rules.size() == 2);
        }
    }
}
