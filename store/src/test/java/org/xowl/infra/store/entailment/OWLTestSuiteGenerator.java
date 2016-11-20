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
package org.xowl.infra.store.entailment;

import org.xowl.infra.store.ProxyObject;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.logging.SinkLogger;

import java.io.IOException;
import java.io.Writer;
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
    public void generate() {
        SinkLogger logger = new SinkLogger();
        RepositoryRDF repository = new RepositoryRDF();
        repository.getIRIMapper().addRegexpMap("http://owl.semanticweb.org/exports/(.*)", "resource:///org/w3c/tests/\\1");
        try {
            repository.load(logger, "http://owl.semanticweb.org/exports/testOntology.rdf");
            repository.load(logger, "http://owl.semanticweb.org/exports/all.rdf");
        } catch (Exception exception) {
            logger.error(exception);
        }

        ProxyObject classTestCase = repository.resolveProxy("http://www.w3.org/2007/OWL/testOntology#TestCase");
        for (ProxyObject test : classTestCase.getInstances()) {
            exportResources(test);
            generateCode(test);
        }
    }

    /**
     * Export the resources for the specified test
     *
     * @param test A test specification
     */
    private void exportResources(ProxyObject test) {
        String id = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#identifier");
        String name = getName(id);
        ProxyObject objSyntax = test.getObjectValue("http://www.w3.org/2007/OWL/testOntology#normativeSyntax");
        String syntax = null;
        String premise = null;
        String conclusion = null;
        switch (objSyntax.getIRIString()) {
            case "http://www.w3.org/2007/OWL/testOntology#RDFXML":
                syntax = "rdf";
                premise = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#rdfXmlPremiseOntology");
                conclusion = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#rdfXmlConclusionOntology");
                if (conclusion == null)
                    conclusion = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#rdfXmlNonConclusionOntology");
                break;
            case "http://www.w3.org/2007/OWL/testOntology#FUNCTIONAL":
                syntax = "fs";
                premise = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#fsPremiseOntology");
                conclusion = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#fsConclusionOntology");
                if (conclusion == null)
                    conclusion = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#fsNonConclusionOntology");
                break;
        }
        try {
            Writer writer = Files.getWriter("src/test/resources/entailment/" + name + ".premise." + syntax);
            writer.write(premise);
            writer.close();
            if (conclusion != null) {
                writer = Files.getWriter("src/test/resources/entailment/" + name + ".conclusion." + syntax);
                writer.write(conclusion);
                writer.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Generates the code for the specified test
     *
     * @param test A test specification
     */
    private void generateCode(ProxyObject test) {
        String id = (String) test.getDataValue("http://www.w3.org/2007/OWL/testOntology#identifier");
        String name = getName(id);
        ProxyObject objSyntax = test.getObjectValue("http://www.w3.org/2007/OWL/testOntology#normativeSyntax");
        String syntax = null;
        switch (objSyntax.getIRIString()) {
            case "http://www.w3.org/2007/OWL/testOntology#RDFXML":
                syntax = "rdf";
                break;
            case "http://www.w3.org/2007/OWL/testOntology#FUNCTIONAL":
                syntax = "fs";
                break;
        }

        Collection<ProxyObject> types = test.getClassifiers();
        for (ProxyObject type : types) {
            switch (type.getIRIString()) {
                case "http://www.w3.org/2007/OWL/testOntology#PositiveEntailmentTest":
                    System.out.println("@Test public void testPositiveEntailment_" + name + "() { testPositiveEntailment(\"" + name + ".premise." + syntax + "\", \"" + name + ".conclusion." + syntax + "\"); }");
                    break;
                case "http://www.w3.org/2007/OWL/testOntology#NegativeEntailmentTest":
                    System.out.println("@Test public void testNegativeEntailment_" + name + "() { testNegativeEntailment(\"" + name + ".premise." + syntax + "\", \"" + name + ".conclusion." + syntax + "\"); }");
                    break;
                case "http://www.w3.org/2007/OWL/testOntology#ConsistencyTest":
                    System.out.println("@Test public void testConsistency_" + name + "() { testConsistency(\"" + name + ".premise." + syntax + "\"); }");
                    break;
                case "http://www.w3.org/2007/OWL/testOntology#InconsistencyTest":
                    System.out.println("@Test public void testInconsistency_" + name + "() { testInconsistency(\"" + name + ".premise." + syntax + "\"); }");
                    break;
            }
        }
    }

    /**
     * Gets the sanitized name for the specified original name
     *
     * @param original The original name
     * @return The sanitized name
     */
    private String getName(String original) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != original.length(); i++) {
            char c = original.charAt(i);
            if (c >= 'a' && c <= 'z') {
                builder.append(c);
            } else if (c >= 'A' && c <= 'Z') {
                builder.append(c);
            } else if (c >= '0' && c <= '9') {
                builder.append(c);
            } else {
                builder.append('_');
            }
        }
        return builder.toString();
    }
}
