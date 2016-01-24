/*******************************************************************************
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
 ******************************************************************************/

package org.xowl.infra.store.sparql;

import org.apache.xerces.parsers.DOMParser;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xowl.infra.store.*;
import org.xowl.infra.store.loaders.SPARQLLoader;
import org.xowl.infra.store.loaders.W3CTestSuite;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.storage.BaseStore;
import org.xowl.infra.store.storage.StoreFactory;
import org.xowl.infra.utils.logging.Logger;
import org.xowl.infra.utils.collections.Couple;

import java.io.*;
import java.util.*;

/**
 * Base class for the SPARQL tests
 *
 * @author Laurent Wouters
 */
public abstract class BaseSPARQLTest {
    /**
     * An IRI as the value of a binding in a solution
     */
    private static final int VALUE_TYPE_IRI = 0;
    /**
     * A blank as the value of a binding in a solution
     */
    private static final int VALUE_TYPE_BLANK = 1;
    /**
     * A literal as the value of a binding in a solution
     */
    private static final int VALUE_TYPE_LITERAL = 2;

    /**
     * A value for a binding in a solution
     */
    private static class Value {
        /**
         * The value type
         */
        public final int type;
        /**
         * The value content
         */
        public final String value;
        /**
         * The value datatype, if any
         */
        public final String datatype;
        /**
         * The value language tag, if any
         */
        public final String lang;

        /**
         * Initializes this value
         *
         * @param type  The value type
         * @param value The value content
         */
        public Value(int type, String value) {
            this.type = type;
            this.value = value;
            this.datatype = null;
            this.lang = null;
        }

        /**
         * Initializes this value as a literal
         *
         * @param value    The lexical value
         * @param datatype The datatype, if any
         * @param lang     The language tag, if any
         */
        public Value(String value, String datatype, String lang) {
            this.type = VALUE_TYPE_LITERAL;
            this.value = value;
            this.datatype = datatype;
            this.lang = lang;
        }
    }

    /**
     * Tests the correct loading of the specified SPARQL resource
     *
     * @param resource The resource to load
     */
    protected void testPositiveSyntax(String resource) {
        TestLogger logger = new TestLogger();
        BaseStore store = StoreFactory.create().make();
        IRIMapper mapper = new IRIMapper();
        mapper.addRegexpMap("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/(.*)", "/sparql/\\1");
        SPARQLLoader loader = new SPARQLLoader(store);
        try (InputStream stream = BaseSPARQLTest.class.getResourceAsStream(mapper.get(resource))) {
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            List<Command> commands = loader.load(logger, reader);
            Assert.assertFalse("Errors while loading", logger.isOnError());
            Assert.assertNotNull("Errors while loading", commands);
            Assert.assertFalse("No command loaded", commands.isEmpty());
        } catch (IOException exception) {
            Assert.fail(exception.getMessage());
        }
    }

    /**
     * Tests the reporting of errors while loading the specified SPARQL resource
     *
     * @param resource The resource to load
     */
    protected void testNegativeSyntax(String resource) {
        TestLogger logger = new TestLogger();
        BaseStore store = StoreFactory.create().make();
        IRIMapper mapper = new IRIMapper();
        mapper.addRegexpMap("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/(.*)", "/sparql/\\1");
        SPARQLLoader loader = new SPARQLLoader(store);
        try (InputStream stream = BaseSPARQLTest.class.getResourceAsStream(mapper.get(resource))) {
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            List<Command> commands = loader.load(logger, reader);
            Assert.assertTrue("Failed to report error while loading", logger.isOnError());
            Assert.assertNull("Failed to return null on incorrect loading", commands);
        } catch (IOException exception) {
            Assert.fail(exception.getMessage());
        }
    }

    /**
     * Tests the correct execution of an update request
     *
     * @param resource The resource containing the request
     * @param inputs   The data before the request's execution
     * @param outputs  The data after the request's execution
     */
    protected void testUpdateEvaluation(String resource, Couple<String, String>[] inputs, Couple<String, String>[] outputs) {
        TestLogger logger = new TestLogger();
        Repository before = prepare(logger, inputs);
        Assert.assertFalse("Failed to prepare the repository", logger.isOnError());
        Repository after = prepare(logger, outputs);
        Assert.assertFalse("Failed to prepare the repository", logger.isOnError());
        String request = before.getIRIMapper().get(resource);
        request = request.substring(AbstractRepository.SCHEME_RESOURCE.length());
        request = readResource(request);
        Result result = before.execute(logger, request);
        Assert.assertFalse("Error while executing the request", logger.isOnError());
        Assert.assertTrue("Error while executing the request", result.isSuccess());
        W3CTestSuite.matchesQuads(getQuads(after), getQuads(before));
    }

    /**
     * Tests the correct execution of an query request
     *
     * @param resource The resource containing the request
     * @param inputs   The data before the request's execution
     */
    protected void testQueryEvaluation(String resource, String[] inputs, String output) {
        TestLogger logger = new TestLogger();
        Repository before = prepare(logger, inputs);
        Assert.assertFalse("Failed to prepare the repository", logger.isOnError());
        String request = before.getIRIMapper().get(resource);
        request = request.substring(AbstractRepository.SCHEME_RESOURCE.length());
        request = readResource(request);
        Result result = before.execute(logger, request);
        Assert.assertFalse("Error while executing the request", logger.isOnError());
        Assert.assertTrue("Error while executing the request", result.isSuccess());
        compare(result, output);
    }

    /**
     * Compares the result of a SPARQL query to an expected one
     *
     * @param result   The SPARQL result
     * @param expected The expected result
     */
    private void compare(Result result, String expected) {
        if (result instanceof ResultYesNo) {

        } else if (result instanceof ResultSolutions) {
            compareSolutions((ResultSolutions) result, expected);
        } else if (result instanceof ResultQuads) {
            compareQuads((ResultQuads) result, expected);
        } else {
            Assert.fail("Unknown type of result");
        }
    }

    /**
     * Compares the result of a SPARQL query to an expected one
     *
     * @param result   The SPARQL result
     * @param expected The expected result
     */
    private void compareSolutions(ResultSolutions result, String expected) {
        String expectedResource = "/sparql/" + expected.substring("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/".length());
        String expectedContent = readResource(expectedResource);
        if (expected.endsWith(".srx")) {
            compareSolutionsXML(serialize(result, Result.SYNTAX_XML), expectedContent);
        } else if (expected.endsWith(".csv")) {
            compareSolutionsCSV(serialize(result, Result.SYNTAX_CSV), expectedContent);
        } else if (expected.endsWith(".tsv")) {
            compareSolutionsTSV(serialize(result, Result.SYNTAX_TSV), expectedContent);
        } else if (expected.endsWith(".srj")) {
            compareSolutionsJSON(serialize(result, Result.SYNTAX_JSON), expectedContent);
        } else {
            Assert.fail("Unknown result syntax");
        }
    }

    /**
     * Serializes solutions in a syntax
     *
     * @param result The result solutions
     * @param syntax the syntax
     * @return The serailized solutions
     */
    private String serialize(ResultSolutions result, String syntax) {
        try (StringWriter writer = new StringWriter()) {
            result.print(writer, syntax);
            return writer.toString();
        } catch (IOException exception) {
            Assert.fail(exception.getMessage());
            return null;
        }
    }

    /**
     * Compares the result of a SPARQL query to an expected one
     *
     * @param result   The SPARQL result
     * @param expected The expected result
     */
    private void compareSolutionsXML(String result, String expected) {
        List<Map<String, Value>> testedSolutions = loadSolutionsXML(result);
        List<Map<String, Value>> expectedSolutions = loadSolutionsXML(expected);
        compareSolutions(testedSolutions, expectedSolutions);
    }

    /**
     * Loads a collection of results on the XML format from the specified string
     *
     * @param input The serialized XML document
     * @return The de-serialized results
     */
    private List<Map<String, Value>> loadSolutionsXML(String input) {
        try {
            List<Map<String, Value>> results = new ArrayList<>();
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(new StringReader(input)));
            Document document = parser.getDocument();
            Element root = document.getDocumentElement();
            for (int i = 0; i != root.getChildNodes().getLength(); i++) {
                Node node = root.getChildNodes().item(i);
                if (node.getNodeName().equals("results")) {
                    return loadSolutionXMLResults(node);
                }
            }
            return results;
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
            return null;
        }
    }

    /**
     * Loads a collection of results from a XML solution node
     *
     * @param nodeResults The XML solution node representing the results
     * @return The results
     */
    private List<Map<String, Value>> loadSolutionXMLResults(Node nodeResults) {
        List<Map<String, Value>> results = new ArrayList<>();
        for (int i = 0; i != nodeResults.getChildNodes().getLength(); i++) {
            Node nodeResult = nodeResults.getChildNodes().item(i);
            if (nodeResult.getNodeName().equals("result")) {
                results.add(loadSolutionXMLResult(nodeResult));
            }
        }
        return results;
    }

    /**
     * Loads a result from a XML solution node
     *
     * @param nodeResult The XML solution node representing the result
     * @return The result
     */
    private Map<String, Value> loadSolutionXMLResult(Node nodeResult) {
        Map<String, Value> bindings = new HashMap<>();
        for (int i = 0; i != nodeResult.getChildNodes().getLength(); i++) {
            Node nodeBinding = nodeResult.getChildNodes().item(i);
            if (nodeBinding.getNodeName().equals("binding")) {
                Couple<String, Value> binding = loadSolutionXMLBinding(nodeBinding);
                bindings.put(binding.x, binding.y);
            }
        }
        return bindings;
    }

    /**
     * Loads a solution binding from a XML solution node
     *
     * @param nodeBinding The XML solution node representing the binding
     * @return The binding
     */
    private Couple<String, Value> loadSolutionXMLBinding(Node nodeBinding) {
        String name = nodeBinding.getAttributes().getNamedItem("name").getNodeValue();
        return new Couple<>(name, loadSolutionXMLValue(nodeBinding));
    }

    /**
     * Loads a solution value from a XML solution binding
     *
     * @param nodeBinding The XML node to load from
     * @return The value
     */
    private Value loadSolutionXMLValue(Node nodeBinding) {
        for (int i = 0; i != nodeBinding.getChildNodes().getLength(); i++) {
            Node child = nodeBinding.getChildNodes().item(i);
            switch (child.getNodeName()) {
                case "uri":
                    return new Value(VALUE_TYPE_IRI, child.getTextContent());
                case "bnode":
                    return new Value(VALUE_TYPE_BLANK, child.getTextContent());
                case "literal":
                    String datatype = null;
                    String lang = null;
                    if (child.getAttributes().getNamedItem("datatype") != null)
                        datatype = child.getAttributes().getNamedItem("datatype").getNodeValue();
                    if (child.getAttributes().getNamedItem("xml:lang") != null)
                        lang = child.getAttributes().getNamedItem("xml:lang").getNodeValue();
                    return new Value(child.getTextContent(), datatype, lang);
            }
        }
        return null;
    }

    /**
     * Compares the result of a SPARQL query to an expected one
     *
     * @param result   The SPARQL result
     * @param expected The expected result
     */
    private void compareSolutionsCSV(String result, String expected) {
        Assert.fail("Not implemented");
    }

    /**
     * Compares the result of a SPARQL query to an expected one
     *
     * @param result   The SPARQL result
     * @param expected The expected result
     */
    private void compareSolutionsTSV(String result, String expected) {
        Assert.fail("Not implemented");
    }

    /**
     * Compares the result of a SPARQL query to an expected one
     *
     * @param result   The SPARQL result
     * @param expected The expected result
     */
    private void compareSolutionsJSON(String result, String expected) {
        Assert.fail("Not implemented");
    }

    /**
     * Compares two set of solutions
     *
     * @param solutions The result solutions
     * @param expected  The expected solutions
     */
    private void compareSolutions(List<Map<String, Value>> solutions, List<Map<String, Value>> expected) {
        Map<String, String> blanks = new HashMap<>();
        for (int i = 0; i != expected.size(); i++) {
            Map<String, Value> bindings = expected.get(i);
            boolean found = false;
            for (Map<String, Value> potential : solutions) {
                if (compareSolution(potential, bindings, blanks)) {
                    found = true;
                    solutions.remove(potential);
                    break;
                }
            }
            if (found) {
                expected.remove(i);
                i--;
            } else {
                Assert.fail("Expected solution not produced");
            }
        }
        if (!solutions.isEmpty())
            Assert.fail("Unexpected solution produced");
    }

    /**
     * Determines whether two soliutions are the same
     *
     * @param s1     A solution
     * @param s2     Another solution
     * @param blanks The current mapping of blank nodes
     * @return true if the solutions are the same
     */
    private boolean compareSolution(Map<String, Value> s1, Map<String, Value> s2, Map<String, String> blanks) {
        // check the size
        if (s1.size() != s2.size())
            return false;
        // check the values and mapped blank nodes
        for (Map.Entry<String, Value> entry : s1.entrySet()) {
            Value value2 = s2.get(entry.getKey());
            if (value2 == null)
                // missing the variable
                return false;
            if (entry.getValue().type != value2.type)
                // different type
                return false;
            if (entry.getValue().type == VALUE_TYPE_BLANK) {
                String mapped = blanks.get(entry.getValue().value);
                if (mapped != null && !mapped.equals(value2.value))
                    // mapped to a different blank node
                    return false;
            } else {
                if (!Objects.equals(entry.getValue().value, value2.value))
                    return false;
                if (entry.getValue().type == VALUE_TYPE_LITERAL) {
                    if (!Objects.equals(entry.getValue().datatype, value2.datatype))
                        return false;
                    if (!Objects.equals(entry.getValue().lang, value2.lang))
                        return false;
                }
            }
        }
        // map the unmapped blank nodes
        for (Map.Entry<String, Value> entry : s1.entrySet()) {
            if (entry.getValue().type != VALUE_TYPE_BLANK)
                continue;
            String mapped = blanks.get(entry.getValue().value);
            if (mapped == null) {
                // not mapped yet, do it
                Value value2 = s2.get(entry.getKey());
                blanks.put(entry.getValue().value, value2.value);
            }
        }
        return true;
    }

    /**
     * Compares the result of a SPARQL query to an expected one
     *
     * @param result   The SPARQL result
     * @param expected The expected result
     */
    private void compareQuads(ResultQuads result, String expected) {
        TestLogger logger = new TestLogger();
        Repository repository = new Repository();
        repository.getIRIMapper().addRegexpMap("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/(.*)", "resource:///sparql/\\1");
        repository.load(logger, expected, IRIs.GRAPH_DEFAULT, true);
        Assert.assertFalse("Failed to load the expected results", logger.isOnError());
        W3CTestSuite.matchesQuads(getQuads(repository), new ArrayList<>(result.getQuads()));
    }

    /**
     * Gets all the quads in the specified repository
     *
     * @param repository The repository
     * @return The extracted quads
     */
    private List<Quad> getQuads(Repository repository) {
        List<Quad> quads = new ArrayList<>();
        Iterator<Quad> iterator = repository.getStore().getAll();
        while (iterator.hasNext())
            quads.add(iterator.next());
        return quads;
    }

    /**
     * Prepares a repository with the specified inputs in it
     *
     * @param logger The logger to use
     * @param inputs The inputs
     * @return The repository
     */
    private Repository prepare(Logger logger, Couple<String, String>[] inputs) {
        Repository repository = new Repository();
        repository.getIRIMapper().addRegexpMap("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/(.*)", "resource:///sparql/\\1");
        for (Couple<String, String> input : inputs) {
            repository.load(logger, input.x, input.y == null ? IRIs.GRAPH_DEFAULT : input.y, true);
        }
        return repository;
    }

    /**
     * Prepares a repository with the specified inputs in it
     *
     * @param logger The logger to use
     * @param inputs The inputs
     * @return The repository
     */
    private Repository prepare(Logger logger, String[] inputs) {
        Repository repository = new Repository();
        repository.getIRIMapper().addRegexpMap("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/(.*)", "resource:///sparql/\\1");
        for (String input : inputs)
            repository.load(logger, input, IRIs.GRAPH_DEFAULT, true);
        return repository;
    }

    /**
     * Reads a resource as a string
     *
     * @param resource The resource
     * @return The resource's content
     */
    private String readResource(String resource) {
        try (InputStream stream = BaseSPARQLTest.class.getResourceAsStream(resource)) {
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[1024];
            StringBuilder builder = new StringBuilder();
            int read = reader.read(buffer);
            while (read > 0) {
                builder.append(buffer, 0, read);
                read = reader.read(buffer);
            }
            return builder.toString();
        } catch (IOException exception) {
            Assert.fail(exception.getMessage());
            return null; // cannot happen
        }
    }
}
