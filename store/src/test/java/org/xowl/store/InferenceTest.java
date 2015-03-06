/**********************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
package org.xowl.store;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.lang.owl2.Ontology;
import org.xowl.utils.Logger;

import java.util.Collection;

/**
 * Test suite for the default inference rules
 *
 * @author Laurent Wouters
 */
public class InferenceTest {
    /**
     * Tests the transitive sub classing
     */
    @Test
    public void transitiveSubClassing() {
        Logger logger = new TestLogger();
        Repository repository = getRepository(logger);
        repository.addDefaultInferenceRules(logger);
        Ontology metamodel = repository.resolveOntology("http://xowl.org/store/tests/inferences");
        ProxyObject classA = repository.newObject(metamodel);
        ProxyObject classB = repository.newObject(metamodel);
        ProxyObject individual = repository.newObject(metamodel);
        classB.setValue(Vocabulary.rdfsSubClassOf, classA);
        individual.setValue(Vocabulary.rdfType, classB);

        Collection<ProxyObject> classifiers = individual.getClassifiers();
        Assert.assertEquals(2, classifiers.size());
        Assert.assertTrue(classifiers.contains(classB));
        Assert.assertTrue(classifiers.contains(classA));
    }

    /**
     * Gets an initialized repository
     *
     * @param logger The logger
     * @return An initialized repository
     */
    private Repository getRepository(Logger logger) {
        try {
            return new Repository();
        } catch (Exception ex) {
            logger.error(ex);
            return null;
        }
    }
}
