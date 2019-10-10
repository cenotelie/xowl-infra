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

package org.xowl.infra.store.loaders;

import fr.cenotelie.commons.utils.TextUtils;
import fr.cenotelie.commons.utils.json.Json;
import fr.cenotelie.commons.utils.json.JsonParser;
import fr.cenotelie.commons.utils.logging.Logger;
import fr.cenotelie.hime.redist.ASTNode;
import fr.cenotelie.hime.redist.ParseResult;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.rdf.*;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implements a loader of xOWL ontologies serialized in JSON
 *
 * @author Laurent Wouters
 */
public class JsonLoader implements Loader {

    /**
     * The repository to use
     */
    private final Repository repository;
    /**
     * The node manager to use
     */
    private final DatasetNodes nodes;

    /**
     * Initializes this loader
     */
    public JsonLoader() {
        this(new RepositoryRDF());
    }

    /**
     * Initializes this loader
     *
     * @param repository The repository to use
     */
    public JsonLoader(RepositoryRDF repository) {
        this(repository, repository.getStore().getTransaction().getDataset());
    }

    /**
     * Initializes this loader
     *
     * @param repository The repository to use
     * @param nodes      The node manager to use
     */
    public JsonLoader(Repository repository, DatasetNodes nodes) {
        this.repository = repository;
        this.nodes = nodes;
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        return Json.doParse(logger, reader);
    }

    @Override
    public RDFLoaderResult loadRDF(Logger logger, Reader reader, String resourceIRI, String graphIRI) {
        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return null;
        try {
            return loadDocument(parseResult.getRoot());
        } catch (IllegalArgumentException exception) {
            logger.error(exception);
            return null;
        }
    }

    @Override
    public OWLLoaderResult loadOWL(Logger logger, Reader reader, String uri) {
        throw new UnsupportedOperationException();
    }

    /**
     * Loads the document represented by the specified AST node
     *
     * @param node An AST node
     * @return The loaded elements
     */
    private RDFLoaderResult loadDocument(ASTNode node) {
        RDFLoaderResult result = new RDFLoaderResult();
        loadGraphs(node, result.getQuads());
        return result;
    }

    /**
     * Loads the graphs form the specified AST node
     *
     * @param node   An AST node
     * @param buffer A buffer for the produced quads
     */
    public void loadGraphs(ASTNode node, Collection<Quad> buffer) {
        if (node.getSymbol().getID() == JsonParser.ID.array) {
            for (ASTNode child : node.getChildren())
                loadGraph(child, buffer);
        } else {
            loadGraph(node, buffer);
        }
    }

    /**
     * Loads the graph form the specified AST node
     *
     * @param node   An AST node
     * @param buffer A buffer for the produced quads
     */
    private void loadGraph(ASTNode node, Collection<Quad> buffer) {
        ASTNode astGraphNode = null;
        ASTNode astEntities = null;
        for (ASTNode member : node.getChildren()) {
            String head = TextUtils.unescape(member.getChildren().get(0).getValue());
            head = head.substring(1, head.length() - 1);
            if ("graph".equals(head)) {
                astGraphNode = member.getChildren().get(1);
            } else if ("entities".equals(head)) {
                astEntities = member.getChildren().get(1);
            }
        }
        if (astGraphNode == null || astEntities == null)
            return;
        GraphNode graph = (GraphNode) RDFUtils.deserializeJSON(repository, astGraphNode);
        for (ASTNode child : astEntities.getChildren()) {
            loadEntity(child, graph, buffer);
        }
    }

    /**
     * Loads the entity from the specified AST node
     *
     * @param node   An AST node
     * @param graph  The current graph
     * @param buffer A buffer for the produced quads
     */
    private void loadEntity(ASTNode node, GraphNode graph, Collection<Quad> buffer) {
        ASTNode astSubjectNode = null;
        ASTNode astProperties = null;
        for (ASTNode member : node.getChildren()) {
            String head = TextUtils.unescape(member.getChildren().get(0).getValue());
            head = head.substring(1, head.length() - 1);
            if ("subject".equals(head)) {
                astSubjectNode = member.getChildren().get(1);
            } else if ("properties".equals(head)) {
                astProperties = member.getChildren().get(1);
            }
        }
        if (astSubjectNode == null || astProperties == null)
            return;
        SubjectNode subject = (SubjectNode) RDFUtils.deserializeJSON(repository, astSubjectNode);
        for (ASTNode child : astProperties.getChildren()) {
            loadProperty(child, graph, subject, buffer);
        }
    }

    /**
     * Loads the property from the specified AST node
     *
     * @param node    An AST node
     * @param graph   The current graph
     * @param subject The current subject
     * @param buffer  A buffer for the produced quads
     */
    private void loadProperty(ASTNode node, GraphNode graph, SubjectNode subject, Collection<Quad> buffer) {
        ASTNode astPropertyNode = null;
        ASTNode astValues = null;
        for (ASTNode member : node.getChildren()) {
            String head = TextUtils.unescape(member.getChildren().get(0).getValue());
            head = head.substring(1, head.length() - 1);
            if ("property".equals(head)) {
                astPropertyNode = member.getChildren().get(1);
            } else if ("values".equals(head)) {
                astValues = member.getChildren().get(1);
            }
        }
        if (astPropertyNode == null || astValues == null)
            return;
        Property property = (Property) RDFUtils.deserializeJSON(repository, astValues);
        for (ASTNode child : astValues.getChildren()) {
            loadValue(child, graph, subject, property, buffer);
        }
    }

    /**
     * Loads the property value from the specified AST node
     *
     * @param node     An AST node
     * @param graph    The current graph
     * @param subject  The current subject
     * @param property The current property
     * @param buffer   A buffer for the produced quads
     */
    private void loadValue(ASTNode node, GraphNode graph, SubjectNode subject, Property property, Collection<Quad> buffer) {
        if (node.getSymbol().getID() == JsonParser.ID.array) {
            List<Node> values = new ArrayList<>(node.getChildren().size());
            for (ASTNode child : node.getChildren())
                values.add(RDFUtils.deserializeJSON(repository, child));
            Node value = BaseTurtleLoader.buildRdfList(values, graph, nodes, buffer);
            buffer.add(new Quad(graph, subject, property, value));
        } else {
            Node value = RDFUtils.deserializeJSON(repository, node);
            buffer.add(new Quad(graph, subject, property, value));
        }
    }
}
