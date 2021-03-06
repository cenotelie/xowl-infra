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

import fr.cenotelie.commons.utils.ini.IniDocument;
import fr.cenotelie.commons.utils.logging.SinkLogger;
import org.junit.Assert;
import org.junit.Test;

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
        IniDocument config = new IniDocument();
        config.add(null, "input", "http://xowl.org/infra/lang/owl2");
        config.add(null, "input", "http://xowl.org/infra/lang/actions");
        config.add(null, "input", "http://xowl.org/infra/lang/rules");
        config.add(null, "input", "http://xowl.org/infra/lang/runtime");
        config.add(null, "input", "http://xowl.org/infra/lang/instrumentation");
        config.add(null, "basePackage", "org.xowl.infra.lang");
        config.add(null, "output", "target/tests");
        config.add(null, "jarName", "XOWLModel");
        config.add(null, "header", "Copyright (c) 2016 Association Cénotélie (cenotelie.fr)\n" +
                "This program is free software: you can redistribute it and/or modify\n" +
                "it under the terms of the GNU Lesser General Public License as\n" +
                "published by the Free Software Foundation, either version 3\n" +
                "of the License, or (at your option) any later version.\n" +
                "\n" +
                "This program is distributed in the hope that it will be useful,\n" +
                "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                "GNU Lesser General Public License for more details.\n" +
                "\n" +
                "You should have received a copy of the GNU Lesser General\n" +
                "Public License along with this program.\n" +
                "If not, see <http://www.gnu.org/licenses/>.");
        Program program = new Program(logger, config);
        program.execute();
        Assert.assertFalse("Failed to re-generate xOWL abstract syntax", logger.isOnError());
    }
}
