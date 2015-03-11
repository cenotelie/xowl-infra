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
import org.xowl.utils.Files;

import java.io.IOException;
import java.io.Writer;

/**
 * Generator for the OWL2 test suite
 *
 * @author Laurent Wouters
 */
public class OWLTestSuiteGenerator {

    /**
     * Generates the OWL2 test suite
     */
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

        ProxyObject classPositiveEntailment = repository.getProxy("http://www.w3.org/2007/OWL/testOntology#PositiveEntailmentTest");
        for (ProxyObject test : classPositiveEntailment.getInstances()) {
            generatePositiveEntailmentTest(test);
        }
    }

    /**
     * Generates a positive entailment test from the specified specification
     *
     * @param test A test specification
     */
    private void generatePositiveEntailmentTest(ProxyObject test) {
        String id = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#identifier");
        String name = id.replace("-", "_").replace(".", "_");

        ProxyObject objSyntax = test.getObjectValue("http://www.w3.org/2007/OWL/testOntology#normativeSyntax");
        String syntax = null;
        String premise = null;
        String conclusion = null;
        switch (objSyntax.getIRIString()) {
            case "http://www.w3.org/2007/OWL/testOntology#RDFXML":
                syntax = "rdf";
                premise = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#rdfXmlPremiseOntology");
                conclusion = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#rdfXmlConclusionOntology");
                break;
            case "http://www.w3.org/2007/OWL/testOntology#FUNCTIONAL":
                syntax = "fs";
                premise = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#fsPremiseOntology");
                conclusion = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#fsConclusionOntology");
                break;
        }
        try {
            Writer writer = Files.getWriter("src/test/resources/entailment/" + id + ".premise." + syntax);
            writer.write(premise);
            writer.close();
            writer = Files.getWriter("src/test/resources/entailment/" + id + ".conclusion." + syntax);
            writer.write(conclusion);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("@Test public void test_" + name + "() { testPositiveEntailment(\"" + id + ".premise." + syntax + "\", \"" + id + ".conclusion." + syntax + "\"); }");
    }
}
