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

package org.xowl.infra.store.entailment;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.Logging;

/**
 * Test suite to ensure the ability for basic reasoning
 *
 * @author Laurent Wouters
 */
public class BasicReasoningTest {
    @Test
    public void testSetEntailment() throws Exception {
        BufferedLogger logger = new BufferedLogger();
        Logging.set(logger);
        RepositoryRDF repositoryRDF = new RepositoryRDF();
        repositoryRDF.setEntailmentRegime(EntailmentRegime.OWL2_RDF);
        if (!logger.getErrorMessages().isEmpty())
            Assert.fail("Error while setting the entailment regime");
    }
}
