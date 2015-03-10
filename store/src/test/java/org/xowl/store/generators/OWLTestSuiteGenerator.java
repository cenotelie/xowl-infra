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
package org.xowl.store.generators;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.store.ProxyObject;
import org.xowl.store.Repository;
import org.xowl.store.TestLogger;

import java.io.IOException;
import java.util.Collection;

/**
 * Generator for the OWL2 test suite
 *
 * @author Laurent Wouters
 */
public class OWLTestSuiteGenerator {

    /**
     * Generates the OWL2 test suite
     */
    @Test
    public void generate() {
        TestLogger logger = new TestLogger();
        Repository repository = null;
        try {
            repository = new Repository();
        } catch (IOException e) {
            Assert.fail("Failed to initialize the repository");
        }

        repository.getIRIMapper().addRegexpMap("http://owl.semanticweb.org/exports/(.*)", "resource:///tests/\\1");
        repository.load(logger, "http://owl.semanticweb.org/exports/testOntology.rdf");
        repository.load(logger, "http://owl.semanticweb.org/exports/all.rdf");

        ProxyObject classTest = repository.getProxy("http://www.w3.org/2007/OWL/testOntology#TestCase");
        ProxyObject classPositiveEntailment = repository.getProxy("http://www.w3.org/2007/OWL/testOntology#PositiveEntailmentTest");
        for (ProxyObject test : classTest.getInstances()) {
            Collection<ProxyObject> types = test.getClassifiers();
            String id = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#identifier");
            if (types.contains(classPositiveEntailment)) {
                System.out.println(id);
            }
        }
    }
}
