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

package org.xowl.infra.store.loaders;

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.hime.redist.TextContext;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.http.URIUtils;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Loader for N-Triples sources
 *
 * @author Laurent Wouters
 */
public class NTriplesLoader implements Loader {
    /**
     * The RDF store to create nodes from
     */
    private final NodeManager store;
    /**
     * Maps of blanks nodes
     */
    private Map<String, BlankNode> blanks;

    /**
     * Initializes this loader
     *
     * @param store The RDF store used to create nodes
     */
    public NTriplesLoader(NodeManager store) {
        this.store = store;
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result;
        try {
            String content = IOUtils.read(reader);
            NTriplesLexer lexer = new NTriplesLexer(content);
            NTriplesParser parser = new NTriplesParser(lexer);
            parser.setModeRecoverErrors(false);
            result = parser.parse();
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
        for (ParseError error : result.getErrors()) {
            logger.error(error);
            TextContext context = result.getInput().getContext(error.getPosition(), error.getLength());
            logger.error(context.getContent());
            logger.error(context.getPointer());
        }
        return result;
    }

    @Override
    public RDFLoaderResult loadRDF(Logger logger, Reader reader, String resourceIRI, String graphIRI) {
        blanks = new HashMap<>();
        RDFLoaderResult result = new RDFLoaderResult();
        GraphNode graph = store.getIRINode(graphIRI);

        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return null;

        try {
            for (ASTNode triple : parseResult.getRoot().getChildren()) {
                Node n1 = getRDFNode(triple.getChildren().get(0));
                Node n2 = getRDFNode(triple.getChildren().get(1));
                Node n3 = getRDFNode(triple.getChildren().get(2));
                result.getQuads().add(new Quad(graph, (SubjectNode) n1, (Property) n2, n3));
            }
        } catch (LoaderException exception) {
            logger.error(exception);
            logger.error("@" + exception.getOrigin().getPosition());
            TextContext context = exception.getOrigin().getContext();
            logger.error(context.getContent());
            logger.error(context.getPointer());
            return null;
        } catch (IllegalArgumentException exception) {
            logger.error(exception);
            return null;
        }

        return result;
    }

    @Override
    public OWLLoaderResult loadOWL(Logger logger, Reader reader, String uri) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the RDF node represented by the specified AST node
     *
     * @param node An AST node representing an RDF node
     * @return The represented RDF node
     */
    private Node getRDFNode(ASTNode node) throws LoaderException {
        switch (node.getSymbol().getID()) {
            case NTriplesLexer.ID.IRIREF:
                return translateIRIREF(node);
            case NTriplesLexer.ID.BLANK_NODE_LABEL:
                return translateBlankNode(node);
            case NTriplesLexer.ID.STRING_LITERAL_QUOTE:
                return translateLiteral(node);
        }
        return null;
    }

    /**
     * Translates the specified AST node into a IRI RDF node
     *
     * @param node An IRIREF AST node
     * @return The corresponding RDF node
     */
    private Node translateIRIREF(ASTNode node) throws LoaderException {
        String value = node.getValue();
        value = TextUtils.unescape(value.substring(1, value.length() - 1));
        if (!URIUtils.isAbsolute(value))
            throw new LoaderException("IRI must be absolute", node);
        return store.getIRINode(value);
    }

    /**
     * Translates the specified AST node into a Blank RDF node
     *
     * @param node An BLANK_NODE_LABEL AST node
     * @return The corresponding RDF node
     */
    private Node translateBlankNode(ASTNode node) {
        String key = node.getValue();
        key = key.substring(2);
        BlankNode blank = blanks.get(key);
        if (blank != null)
            return blank;
        blank = store.getBlankNode();
        blanks.put(key, blank);
        return blank;
    }

    /**
     * Translates the specified AST node into a literal RDF node
     *
     * @param node An STRING_LITERAL_QUOTE AST node
     * @return The corresponding RDF node
     */
    private Node translateLiteral(ASTNode node) throws LoaderException {
        String value = node.getValue();
        value = TextUtils.unescape(value.substring(1, value.length() - 1));
        if (node.getChildren().size() == 0) {
            return store.getLiteralNode(value, Vocabulary.xsdString, null);
        }
        ASTNode child = node.getChildren().get(0);
        if (child.getSymbol().getID() == NTriplesLexer.ID.IRIREF) {
            String type = child.getValue();
            type = TextUtils.unescape(type.substring(1, type.length() - 1));
            if (!URIUtils.isAbsolute(type))
                throw new LoaderException("IRI must be absolute", node);
            return store.getLiteralNode(value, type, null);
        } else if (child.getSymbol().getID() == NTriplesLexer.ID.LANGTAG) {
            String lang = child.getValue();
            lang = lang.substring(1);
            return store.getLiteralNode(value, Vocabulary.rdfLangString, lang);
        }
        return null;
    }
}