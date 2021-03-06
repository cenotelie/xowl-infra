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

package org.xowl.infra.store;

import fr.cenotelie.commons.utils.TextUtils;
import fr.cenotelie.commons.utils.collections.Couple;
import fr.cenotelie.commons.utils.logging.Logger;
import fr.cenotelie.commons.utils.logging.Logging;
import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.lang.actions.QueryVariable;
import org.xowl.infra.lang.owl2.*;
import org.xowl.infra.lang.runtime.Entity;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.Evaluator;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.store.writers.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * Utility APIs for RDF
 *
 * @author Laurent Wouters
 */
public class RDFUtils {
    /**
     * Determines whether two RDF nodes are equivalent
     *
     * @param node1 A first node
     * @param node2 A second node
     * @return true of the two nodes are equivalent
     */
    public static boolean same(Node node1, Node node2) {
        return (node1 == node2 || (node1 != null && node2 != null
                && node1.getNodeType() == node2.getNodeType()
                && node1.equals(node2)));
    }

    /**
     * Gets the native value for the specified RDF node
     *
     * @param node A RDF node
     * @return The native value
     */
    public static Object getNative(Node node) {
        if (node == null)
            return null;
        switch (node.getNodeType()) {
            case Node.TYPE_IRI: {
                IRI iri = Owl2Factory.newIRI();
                iri.setHasValue(((IRINode) node).getIRIValue());
                return iri;
            }
            case Node.TYPE_BLANK: {
                return node;
            }
            case Node.TYPE_LITERAL: {
                return Datatypes.toNative((LiteralNode) node);
            }
            case Node.TYPE_ANONYMOUS: {
                return ((AnonymousNode) node).getIndividual();
            }
        }
        throw new IllegalArgumentException("Illegal unevaluated node");
    }

    /**
     * Gets the OWL element represented by the specified RDF node
     *
     * @param node A RDF node
     * @return The represented OWL element
     */
    public static Object getOWL(Node node) {
        if (node == null)
            return null;
        switch (node.getNodeType()) {
            case Node.TYPE_IRI: {
                IRI iri = Owl2Factory.newIRI();
                iri.setHasValue(((IRINode) node).getIRIValue());
                return iri;
            }
            case Node.TYPE_LITERAL: {
                LiteralNode literalNode = (LiteralNode) node;
                Literal result = Owl2Factory.newLiteral();
                String value = literalNode.getLexicalValue();
                if (value != null)
                    result.setLexicalValue(value);
                value = literalNode.getDatatype();
                if (value != null) {
                    IRI iri = Owl2Factory.newIRI();
                    iri.setHasValue(value);
                    result.setMemberOf(iri);
                }
                value = literalNode.getLangTag();
                if (value != null)
                    result.setLangTag(value);
                return result;
            }
            case Node.TYPE_ANONYMOUS: {
                return ((AnonymousNode) node).getIndividual();
            }
            case Node.TYPE_DYNAMIC: {
                return ((DynamicNode) node).getEvaluable();
            }
        }
        throw new IllegalArgumentException("RDF node " + node.getClass().getName() + " cannot be mapped to an OWL element");
    }

    /**
     * Gets the RDF node representing the specified OWL element
     *
     * @param store   The node store to look into
     * @param element An OWL element
     * @return The representing RDF node
     */
    public static Node getRDF(NodeManager store, Object element) {
        if (element == null)
            return null;
        if (element instanceof Node)
            return ((Node) element);
        if (element instanceof IRI) {
            return store.getIRINode(((IRI) element).getHasValue());
        } else if (element instanceof Entity) {
            return store.getIRINode(((Entity) element).getHasIRI().getHasValue());
        } else if (element instanceof Ontology) {
            return store.getIRINode(((Ontology) element).getHasIRI().getHasValue());
        } else if (element instanceof AnonymousIndividual) {
            return store.getAnonNode((AnonymousIndividual) element);
        } else if (element instanceof Literal) {
            Literal literal = (Literal) element;
            return store.getLiteralNode(literal.getLexicalValue(), literal.getMemberOf().getHasValue(), literal.getLangTag());
        } else if (element instanceof org.xowl.infra.lang.runtime.Literal) {
            org.xowl.infra.lang.runtime.Literal literal = (org.xowl.infra.lang.runtime.Literal) element;
            return store.getLiteralNode(literal.getLexicalValue(), literal.getMemberOf().getInterpretationOf().getHasIRI().getHasValue(), literal.getLangTag());
        } else if (element instanceof QueryVariable) {
            return new VariableNode(((QueryVariable) element).getName());
        } else if (element instanceof EvaluableExpression) {
            return store.getDynamicNode((EvaluableExpression) element);
        } else {
            Couple<String, String> data = Datatypes.toLiteral(element);
            return store.getLiteralNode(data.x, data.y, null);
        }
    }

    /**
     * Instantiate of template of quads given a solution, i.e. a set of bindings for the variable nodes.
     * This replaces the variable nodes in the template by their value.
     *
     * @param nodes     The node manager
     * @param evaluator The current evaluator
     * @param resolver  The variable resolver to use
     * @param solution  The solution
     * @param cache     The cache of evaluated nodes
     * @param template  The template
     * @param buffer    The buffer for the realized quads
     * @param mapBlanks Whether to map blank nodes
     */
    public static void instantiateQuads(NodeManager nodes, Evaluator evaluator, VariableResolver resolver, RDFPatternSolution solution, Map<Node, Node> cache, Collection<Quad> template, Collection<Quad> buffer, boolean mapBlanks) {
        for (Quad quad : template) {
            Quad instantiated = instantiateQuad(nodes, evaluator, resolver, solution, cache, quad, mapBlanks);
            if (instantiated != null)
                buffer.add(instantiated);
        }
    }

    /**
     * Processes the specified quad
     *
     * @param nodes     The node manager
     * @param evaluator The current evaluator
     * @param resolver  The variable resolver to use
     * @param solution  The solution
     * @param cache     The cache of evaluated nodes
     * @param quad      The quad to process
     * @param mapBlanks Whether to map blank nodes
     * @return The processed quad
     */
    public static Quad instantiateQuad(NodeManager nodes, Evaluator evaluator, VariableResolver resolver, RDFPatternSolution solution, Map<Node, Node> cache, Quad quad, boolean mapBlanks) {
        Node nodeGraph = instantiateNode(nodes, evaluator, resolver, solution, cache, quad.getGraph(), true, mapBlanks);
        Node nodeSubject = instantiateNode(nodes, evaluator, resolver, solution, cache, quad.getSubject(), false, mapBlanks);
        Node nodeProperty = instantiateNode(nodes, evaluator, resolver, solution, cache, quad.getProperty(), false, mapBlanks);
        Node nodeObject = instantiateNode(nodes, evaluator, resolver, solution, cache, quad.getObject(), false, mapBlanks);
        if ((!(nodeGraph instanceof GraphNode)) || (!(nodeSubject instanceof SubjectNode)) || (!(nodeProperty instanceof Property)))
            return null;
        return new Quad((GraphNode) nodeGraph, (SubjectNode) nodeSubject, (Property) nodeProperty, nodeObject);
    }

    /**
     * Processes the specified node
     *
     * @param nodes     The node manager
     * @param evaluator The current evaluator
     * @param resolver  The variable resolver to use
     * @param solution  The solution
     * @param cache     The cache of evaluated nodes
     * @param node      The node to process
     * @param isGraph   Whether the node to resolve is a graph
     * @param mapBlanks Whether to map blank nodes
     * @return The processed node
     */
    private static Node instantiateNode(NodeManager nodes, Evaluator evaluator, VariableResolver resolver, RDFPatternSolution solution, Map<Node, Node> cache, Node node, boolean isGraph, boolean mapBlanks) {
        if (node == null)
            return instantiateNodeVariable(nodes, evaluator, resolver, solution, cache, null, isGraph);
        switch (node.getNodeType()) {
            case Node.TYPE_BLANK:
                return instantiateNodeBlank(nodes, cache, (BlankNode) node, mapBlanks);
            case Node.TYPE_VARIABLE:
                return instantiateNodeVariable(nodes, evaluator, resolver, solution, cache, (VariableNode) node, isGraph);
            case Node.TYPE_DYNAMIC:
                return instantiateNodeDynamic(nodes, evaluator, solution, cache, (DynamicNode) node);
            default:
                return node;
        }
    }

    /**
     * Resolves a blank node
     *
     * @param nodes     The node manager
     * @param cache     The cache of evaluated nodes
     * @param node      A blank node
     * @param mapBlanks Whether to map blank nodes
     * @return The instantiated blank node
     */
    private static Node instantiateNodeBlank(NodeManager nodes, Map<Node, Node> cache, BlankNode node, boolean mapBlanks) {
        if (!mapBlanks) {
            return node;
        }
        Node result = cache.get(node);
        if (result != null)
            return result;
        result = nodes.getBlankNode();
        cache.put(node, result);
        return result;
    }

    /**
     * Resolves the specified variable node
     *
     * @param nodes     The node manager
     * @param evaluator The current evaluator
     * @param resolver  The variable resolver to use
     * @param solution  The solution
     * @param cache     The cache of evaluated nodes
     * @param variable  A variable node
     * @param isGraph   Whether the node to resolve is a graph
     * @return The variable value
     */
    private static Node instantiateNodeVariable(NodeManager nodes, Evaluator evaluator, VariableResolver resolver, RDFPatternSolution solution, Map<Node, Node> cache, VariableNode variable, boolean isGraph) {
        Node result = solution.get(variable);
        if (result != null)
            return result;
        result = cache.get(variable);
        if (result != null)
            return result;
        result = resolver.resolve(variable, solution, nodes, isGraph);
        cache.put(variable, result);
        return result;
    }

    /**
     * Resolves the specified dynamic node node
     *
     * @param nodes       The node manager
     * @param evaluator   The current evaluator
     * @param solution    The solution
     * @param cache       The cache of evaluated nodes
     * @param dynamicNode A dynamic node
     * @return The variable value
     */
    private static Node instantiateNodeDynamic(NodeManager nodes, Evaluator evaluator, RDFPatternSolution solution, Map<Node, Node> cache, DynamicNode dynamicNode) {
        Node result = cache.get(dynamicNode);
        if (result != null)
            return result;
        if (evaluator == null)
            return dynamicNode;
        result = RDFUtils.getRDF(nodes, evaluator.eval(instantiateNodeDynamicGetBindings(solution, cache), dynamicNode.getEvaluable()));
        cache.put(dynamicNode, result);
        return result;
    }

    /**
     * Creates bindings for the evaluator
     *
     * @param solution The current solution
     * @param cache    The current evaluation cache
     * @return The bindings
     */
    private static Map<String, Object> instantiateNodeDynamicGetBindings(RDFPatternSolution solution, Map<Node, Node> cache) {
        Map<String, Object> bindings = new HashMap<>();
        for (Couple<VariableNode, Node> entry : solution) {
            if (!bindings.containsKey(entry.x.getName()))
                bindings.put(entry.x.getName(), RDFUtils.getNative(entry.y));
        }
        for (Map.Entry<Node, Node> entry : cache.entrySet()) {
            if (entry.getKey().getNodeType() == Node.TYPE_VARIABLE) {
                bindings.put(((VariableNode) entry.getValue()).getName(), RDFUtils.getNative(entry.getValue()));
            }
        }
        return bindings;
    }

    /**
     * Computes the difference between two sets of quads
     * The result is changeset of positive and negative quads in the set difference: left - right.
     * This means that positive (added) quads are present on the left but not the right.
     * Conversely, negative (removed) quads are present on the right but not on the left.
     * The two sets are assumed to not contain duplicated quads, i.e. the same quad does not appear twice or more in the same set.
     *
     * @param left        The set of quads on the left
     * @param right       The set of quads on the right
     * @param ignoreGraph Whether to ignore the graph when comparing quads (compare as triples)
     * @return The changeset representing the difference
     */
    public static Changeset diff(Collection<Quad> left, Collection<Quad> right, boolean ignoreGraph) {
        Quad[] leftArray = left.toArray(new Quad[left.size()]);
        Quad[] rightArray = right.toArray(new Quad[right.size()]);

        Map<BlankNode, BlankNode> blanks = new HashMap<>();
        int countLeft = leftArray.length;
        int countRight = rightArray.length;

        for (int i = 0; i != leftArray.length; i++) {
            if (leftArray[i].getSubject().getNodeType() != Node.TYPE_BLANK) {
                // ignore blank nodes at this time
                for (int j = 0; j != rightArray.length; j++) {
                    if (rightArray[j] != null && diffSameQuads(leftArray[i], rightArray[j], blanks, ignoreGraph)) {
                        leftArray[i] = null;
                        rightArray[j] = null;
                        countLeft--;
                        countRight--;
                        break;
                    }
                }
            }
        }

        boolean modified = true;
        while (modified && countLeft > 0 && countRight > 0) {
            modified = false;
            for (int i = 0; i != leftArray.length; i++) {
                if (leftArray[i] == null)
                    continue;
                for (int j = 0; j != rightArray.length; j++) {
                    if (rightArray[j] != null && diffSameQuads(leftArray[i], rightArray[j], blanks, ignoreGraph)) {
                        leftArray[i] = null;
                        rightArray[j] = null;
                        countLeft--;
                        countRight--;
                        modified = true;
                        break;
                    }
                }
            }
        }

        Collection<Quad> remainingLeft = new ArrayList<>(countLeft);
        Collection<Quad> remainingRight = new ArrayList<>(countRight);
        if (countLeft > 0) {
            for (int i = 0; i != leftArray.length; i++) {
                if (leftArray[i] != null)
                    remainingLeft.add(leftArray[i]);
            }
        }
        if (countRight > 0) {
            for (int i = 0; i != rightArray.length; i++) {
                if (rightArray[i] != null)
                    remainingRight.add(rightArray[i]);
            }
        }
        return Changeset.fromAddedRemoved(remainingLeft, remainingRight);
    }

    /**
     * Determines whether the specified quads are equivalent, using the given blank node mapping
     *
     * @param quad1       A quad
     * @param quad2       Another quad
     * @param blanks      A map of blank nodes
     * @param ignoreGraph Whether to ignore the graph when comparing quads (compare as triples)
     * @return <code>true</code> if the two quads are equivalent
     */
    private static boolean diffSameQuads(Quad quad1, Quad quad2, Map<BlankNode, BlankNode> blanks, boolean ignoreGraph) {
        GraphNode graph = quad1.getGraph();
        SubjectNode subject = quad1.getSubject();
        Property property = quad1.getProperty();
        Node object = quad1.getObject();
        if (graph.getNodeType() == Node.TYPE_BLANK)
            graph = blanks.get(graph);
        if (subject.getNodeType() == Node.TYPE_BLANK)
            subject = blanks.get(subject);
        if (object.getNodeType() == Node.TYPE_BLANK)
            object = blanks.get(object);
        if (!RDFUtils.same(property, quad2.getProperty()))
            return false;
        if (!ignoreGraph && graph != null && !RDFUtils.same(graph, quad2.getGraph()))
            return false;
        if (subject != null && !RDFUtils.same(subject, quad2.getSubject()))
            return false;
        if (object != null && !RDFUtils.same(object, quad2.getObject()))
            return false;
        if (!ignoreGraph && graph == null && quad2.getGraph().getNodeType() != Node.TYPE_BLANK)
            return false;
        if (subject == null && quad2.getSubject().getNodeType() != Node.TYPE_BLANK)
            return false;
        if (object == null && quad2.getObject().getNodeType() != Node.TYPE_BLANK)
            return false;
        if (graph == null)
            blanks.put((BlankNode) quad1.getGraph(), (BlankNode) quad2.getGraph());
        if (subject == null)
            blanks.put((BlankNode) quad1.getSubject(), (BlankNode) quad2.getSubject());
        if (object == null)
            blanks.put((BlankNode) quad1.getObject(), (BlankNode) quad2.getObject());
        return true;
    }

    /**
     * Coerces the content type for RDF quads
     *
     * @param types The requested types
     * @return The coerced content type
     */
    public static String coerceContentTypeQuads(List<String> types) {
        if (types == null)
            return Repository.SYNTAX_NQUADS;
        for (String type : types) {
            switch (type) {
                case Repository.SYNTAX_NTRIPLES:
                case Repository.SYNTAX_NQUADS:
                case Repository.SYNTAX_TURTLE:
                case Repository.SYNTAX_TRIG:
                case Repository.SYNTAX_RDFXML:
                case Repository.SYNTAX_JSON_LD:
                case Repository.SYNTAX_JSON:
                case Repository.SYNTAX_XRDF:
                    return type;
            }
        }
        return Repository.SYNTAX_XRDF;
    }

    /**
     * Serializes quads in a syntax
     *
     * @param quads  An iterator over the quads to serialize
     * @param syntax The syntax to use
     * @return The serialized quads
     */
    public static String serialize(Iterator<Quad> quads, String syntax) {
        return serialize(Logging.get(), quads, syntax);
    }

    /**
     * Serializes quads in a syntax
     *
     * @param logger The logger to use
     * @param quads  An iterator over the quads to serialize
     * @param syntax The syntax to use
     * @return The serialized quads
     */
    public static String serialize(Logger logger, Iterator<Quad> quads, String syntax) {
        Writer writer = new StringWriter();
        serialize(writer, logger, quads, syntax);
        return writer.toString();
    }

    /**
     * Serializes quads in a syntax
     *
     * @param writer The write to write to
     * @param logger The logger to use
     * @param quads  An iterator over the quads to serialize
     * @param syntax The syntax to use
     */
    public static void serialize(Writer writer, Logger logger, Iterator<Quad> quads, String syntax) {
        RDFSerializer serializer;
        switch (syntax) {
            case Repository.SYNTAX_NTRIPLES:
                serializer = new NTripleSerializer(writer);
                break;
            case Repository.SYNTAX_NQUADS:
                serializer = new NQuadsSerializer(writer);
                break;
            case Repository.SYNTAX_TURTLE:
                serializer = new TurtleSerializer(writer);
                break;
            case Repository.SYNTAX_TRIG:
                serializer = new TriGSerializer(writer);
                break;
            case Repository.SYNTAX_RDFXML:
                serializer = new RDFXMLSerializer(writer);
                break;
            case Repository.SYNTAX_JSON_LD:
                serializer = new JsonLdSerializer(writer);
                break;
            case Repository.SYNTAX_JSON:
                serializer = new JsonSerializer(writer);
                break;
            case Repository.SYNTAX_XRDF:
                serializer = new xRDFSerializer(writer);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format " + syntax);
        }
        serializer.serialize(logger, quads);
    }

    /**
     * Serializes a RDF node in the JSON format
     *
     * @param writer The writer to write to
     * @param node   The RDF node to serialize
     * @throws IOException When an IO error occurs
     */
    public static void serializeXML(Writer writer, Node node) throws IOException {
        switch (node.getNodeType()) {
            case Node.TYPE_IRI:
                writer.write("<uri>");
                writer.write(((IRINode) node).getIRIValue());
                writer.write("</uri>");
                break;
            case Node.TYPE_BLANK:
                writer.write("<bnode>");
                writer.write(Long.toString(((BlankNode) node).getBlankID()));
                writer.write("</bnode>");
                break;
            case Node.TYPE_LITERAL:
                writer.write("<literal");
                LiteralNode lit = (LiteralNode) node;
                if (lit.getLangTag() != null) {
                    writer.write(" xml:lang=\"");
                    writer.write(TextUtils.escapeStringW3C(lit.getLangTag()));
                    writer.write("\">");
                } else if (lit.getDatatype() != null) {
                    writer.write(" datatype=\"");
                    writer.write(TextUtils.escapeStringW3C(lit.getDatatype()));
                    writer.write("\">");
                }
                writer.write(lit.getLexicalValue());
                writer.write("</literal>");
                break;
            case Node.TYPE_VARIABLE:
                writer.write("<variable>");
                writer.write(TextUtils.escapeStringW3C(((VariableNode) node).getName()));
                writer.write("</variable>");
                break;
            case Node.TYPE_ANONYMOUS:
                writer.write("<anon>");
                writer.write(TextUtils.escapeStringW3C(((AnonymousNode) node).getIndividual().getNodeID()));
                writer.write("</anon>");
                break;
            case Node.TYPE_DYNAMIC:
                writer.write("<dynamic>");
                writer.write(TextUtils.escapeStringW3C(((DynamicNode) node).getEvaluable().getSource()));
                writer.write("</dynamic>");
                break;
        }
    }

    /**
     * Serializes a RDF node in the JSON format
     *
     * @param writer The writer to write to
     * @param node   The RDF node to serialize
     * @throws IOException When an IO error occurs
     */
    public static void serializeJSON(Writer writer, Node node) throws IOException {
        if (node == null) {
            writer.write("null");
            return;
        }
        switch (node.getNodeType()) {
            case Node.TYPE_IRI:
                writer.write("{\"type\": \"uri\", \"value\": \"");
                writer.write(TextUtils.escapeStringJSON(((IRINode) node).getIRIValue()));
                writer.write("\"}");
                break;
            case Node.TYPE_BLANK:
                writer.write("{\"type\": \"bnode\", \"value\": \"");
                writer.write(Long.toString(((BlankNode) node).getBlankID()));
                writer.write("\"}");
                break;
            case Node.TYPE_LITERAL:
                LiteralNode lit = (LiteralNode) node;
                writer.write("{\"type\": \"literal\", \"value\": \"");
                writer.write(TextUtils.escapeStringJSON(lit.getLexicalValue()));
                writer.write("\"");
                if (lit.getLangTag() != null) {
                    writer.write(", \"xml:lang\": \"");
                    writer.write(TextUtils.escapeStringJSON(lit.getLangTag()));
                    writer.write("\"");
                } else if (lit.getDatatype() != null) {
                    writer.write(", \"datatype\": \"");
                    writer.write(TextUtils.escapeStringJSON(lit.getDatatype()));
                    writer.write("\"");
                }
                writer.write("}");
                break;
            case Node.TYPE_VARIABLE:
                writer.write("{\"type\": \"variable\", \"value\": \"");
                writer.write(TextUtils.escapeStringJSON(((VariableNode) node).getName()));
                writer.write("\"}");
                break;
            case Node.TYPE_ANONYMOUS:
                writer.write("{\"type\": \"anon\", \"value\": \"");
                writer.write(TextUtils.escapeStringJSON(((AnonymousNode) node).getIndividual().getNodeID()));
                writer.write("\"}");
                break;
            case Node.TYPE_DYNAMIC:
                writer.write("{\"type\": \"dynamic\", \"value\": \"");
                writer.write(TextUtils.escapeStringJSON(((DynamicNode) node).getEvaluable().getSource()));
                writer.write("\"}");
                break;
        }
    }

    /**
     * De-serializes the RDF node from the specified JSON AST node
     *
     * @param repository The repository to use
     * @param astNode    The AST node to de-serialize from
     * @return The RDF node
     */
    public static Node deserializeJSON(Repository repository, ASTNode astNode) {
        Map<String, String> properties = new HashMap<>();
        for (ASTNode child : astNode.getChildren()) {
            String name = child.getChildren().get(0).getValue();
            String value = child.getChildren().get(1).getValue();
            name = name.substring(1, name.length() - 1);
            value = value.substring(1, value.length() - 1);
            properties.put(name, value);
        }
        String type = properties.get("type");
        if (type == null)
            return null;
        switch (type) {
            case "uri":
                return repository.getNodeManager().getIRINode(properties.get("value"));
            case "bnode":
                return new BlankNode(Long.parseLong(properties.get("value")));
            case "literal": {
                String lexical = properties.get("value");
                String datatype = properties.get("datatype");
                String langTag = properties.get("xml:lang");
                return repository.getNodeManager().getLiteralNode(lexical, datatype, langTag);
            }
            case "variable":
                return new VariableNode(properties.get("value"));
            case "anon": {
                AnonymousIndividual individual = Owl2Factory.newAnonymousIndividual();
                individual.setNodeID(properties.get("value"));
                return repository.getNodeManager().getAnonNode(individual);
            }
            case "dynamic": {
                String source = properties.get("value");
                ExecutionManager executionManager = repository.getExecutionManager();
                if (executionManager == null)
                    return null;
                EvaluableExpression evaluable = executionManager.loadExpression(source);
                return repository.getNodeManager().getDynamicNode(evaluable);
            }
        }
        return null;
    }
}
