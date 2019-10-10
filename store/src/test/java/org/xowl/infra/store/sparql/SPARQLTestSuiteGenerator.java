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

package org.xowl.infra.store.sparql;

import fr.cenotelie.commons.utils.collections.Couple;
import fr.cenotelie.commons.utils.logging.SinkLogger;
import org.xowl.infra.store.ProxyObject;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.ResourceAccess;
import org.xowl.infra.store.Vocabulary;

import java.util.ArrayList;
import java.util.List;

/**
 * The generator of the test suite for SPARQL
 *
 * @author Laurent Wouters
 */
public class SPARQLTestSuiteGenerator {
    /**
     * List of the used names for the generated tests
     */
    private final List<String> names = new ArrayList<>();

    /**
     * Generates the SPARQL test suite
     */
    public void generate() throws Exception {
        final SinkLogger logger = new SinkLogger();
        try (RepositoryRDF repositoryRdf = new RepositoryRDF()) {
            repositoryRdf.getIRIMapper().addRegexpMap("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/(.*)", ResourceAccess.SCHEME_RESOURCE + "/org/w3c/sparql/\\1");
            repositoryRdf.runAsTransaction((repository, transaction) -> {
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-fed/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-update-1/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-update-2/manifest.ttl");

                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/manifest.ttl");

                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/json-res/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/manifest.ttl");
                repository.load(logger, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/manifest.ttl");

                ProxyObject classTestCase = repository.getProxy("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#PositiveSyntaxTest11");
                for (ProxyObject test : classTestCase.getInstances())
                    generateCodePositiveSyntax(test);
                classTestCase = repository.getProxy("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#PositiveSyntaxTest");
                for (ProxyObject test : classTestCase.getInstances())
                    generateCodePositiveSyntax(test);
                classTestCase = repository.getProxy("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#NegativeSyntaxTest11");
                for (ProxyObject test : classTestCase.getInstances())
                    generateCodeNegativeSyntax(test);
                classTestCase = repository.getProxy("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#NegativeSyntaxTest");
                for (ProxyObject test : classTestCase.getInstances())
                    generateCodeNegativeSyntax(test);

                classTestCase = repository.getProxy("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#UpdateEvaluationTest");
                for (ProxyObject test : classTestCase.getInstances())
                    generateCodeUpdateEvaluation(test);
                classTestCase = repository.getProxy("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#QueryEvaluationTest");
                for (ProxyObject test : classTestCase.getInstances())
                    generateCodeQueryEvaluation(test);
                classTestCase = repository.getProxy("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#CSVResultFormatTest");
                for (ProxyObject test : classTestCase.getInstances())
                    generateCodeQueryEvaluation(test);
                return null;
            }, true);
        }
    }

    /**
     * Generates the code for the specified positive syntax test
     *
     * @param test A test specification
     */
    private void generateCodePositiveSyntax(ProxyObject test) {
        String name = getName((String) test.getDataValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#name"));
        String resource = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#action").getIRIString();
        System.out.println("@Test public void testPositiveSyntax_" + name + "() { testPositiveSyntax(\"" + resource + "\"); }");
    }

    /**
     * Generates the code for the specified negative syntax test
     *
     * @param test A test specification
     */
    private void generateCodeNegativeSyntax(ProxyObject test) {
        String name = getName((String) test.getDataValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#name"));
        String resource = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#action").getIRIString();
        System.out.println("@Test public void testNegativeSyntax_" + name + "() { testNegativeSyntax(\"" + resource + "\"); }");
    }

    /**
     * Generates the code for the specified update evaluation test
     *
     * @param test A test specification
     */
    private void generateCodeUpdateEvaluation(ProxyObject test) {
        String name = getName((String) test.getDataValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#name"));
        ProxyObject action = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#action");
        ProxyObject result = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#result");

        String request = action.getObjectValue("http://www.w3.org/2009/sparql/tests/test-update#request").getIRIString();
        List<Couple<String, String>> initialData = new ArrayList<>();
        List<Couple<String, String>> resultData = new ArrayList<>();

        for (ProxyObject data : action.getObjectValues("http://www.w3.org/2009/sparql/tests/test-update#data"))
            initialData.add(new Couple<>(data.getIRIString(), null));
        for (ProxyObject data : action.getObjectValues("http://www.w3.org/2009/sparql/tests/test-update#graphData")) {
            initialData.add(new Couple<>(
                    data.getObjectValue("http://www.w3.org/2009/sparql/tests/test-update#graph").getIRIString(),
                    data.getDataValue(Vocabulary.rdfs + "label").toString()));
        }
        for (ProxyObject data : result.getObjectValues("http://www.w3.org/2009/sparql/tests/test-update#data"))
            resultData.add(new Couple<>(data.getIRIString(), null));
        for (ProxyObject data : result.getObjectValues("http://www.w3.org/2009/sparql/tests/test-update#graphData")) {
            resultData.add(new Couple<>(
                    data.getObjectValue("http://www.w3.org/2009/sparql/tests/test-update#graph").getIRIString(),
                    data.getDataValue(Vocabulary.rdfs + "label").toString()));
        }
        StringBuilder builder = new StringBuilder("@Test public void testUpdateEvaluation_");
        builder.append(name);
        builder.append("() { testUpdateEvaluation(\"");
        builder.append(request);
        builder.append("\", new Couple[] { ");
        for (int i = 0; i != initialData.size(); i++) {
            Couple<String, String> couple = initialData.get(i);
            if (i != 0)
                builder.append(", ");
            builder.append("new Couple<String, String>(\"");
            builder.append(couple.x);
            builder.append("\", ");
            if (couple.y == null)
                builder.append("null");
            else {
                builder.append("\"");
                builder.append(couple.y);
                builder.append("\"");
            }
            builder.append(")");
        }
        builder.append(" }, new Couple[] { ");
        for (int i = 0; i != resultData.size(); i++) {
            Couple<String, String> couple = resultData.get(i);
            if (i != 0)
                builder.append(", ");
            builder.append("new Couple<String, String>(\"");
            builder.append(couple.x);
            builder.append("\", ");
            if (couple.y == null)
                builder.append("null");
            else {
                builder.append("\"");
                builder.append(couple.y);
                builder.append("\"");
            }
            builder.append(")");
        }
        builder.append(" }); }");
        System.out.println(builder.toString());
    }

    /**
     * Generates the code for the specified query evaluation test
     *
     * @param test A test specification
     */
    private void generateCodeQueryEvaluation(ProxyObject test) {
        String name = getName((String) test.getDataValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#name"));
        ProxyObject action = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#action");
        ProxyObject result = test.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#result");

        String request = action.getObjectValue("http://www.w3.org/2001/sw/DataAccess/tests/test-query#query").getIRIString();
        List<String> initialData = new ArrayList<>();
        for (ProxyObject data : action.getObjectValues("http://www.w3.org/2001/sw/DataAccess/tests/test-query#data"))
            initialData.add(data.getIRIString());
        for (ProxyObject data : action.getObjectValues("http://www.w3.org/2001/sw/DataAccess/tests/test-query#graphData"))
            initialData.add(data.getIRIString());

        StringBuilder builder = new StringBuilder("@Test public void testQueryEvaluation_");
        builder.append(name);
        builder.append("() { testQueryEvaluation(\"");
        builder.append(request);
        builder.append("\", new String[] { ");
        for (int i = 0; i != initialData.size(); i++) {
            if (i != 0)
                builder.append(", ");
            builder.append("\"");
            builder.append(initialData.get(i));
            builder.append("\"");
        }
        builder.append(" }, \"");
        builder.append(result.getIRIString());
        builder.append("\"); }");
        System.out.println(builder.toString());
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
        String name = builder.toString();
        while (names.contains(name)) {
            name += "_2";
        }
        names.add(name);
        return name;
    }
}
