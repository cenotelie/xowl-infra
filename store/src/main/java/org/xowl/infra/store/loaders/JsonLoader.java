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

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.hime.redist.TextContext;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Implements a loader of xOWL ontologies serialized in JSON
 *
 * @author Laurent Wouters
 */
public class JsonLoader implements Loader {
    /**
     * Parses the JSON content
     *
     * @param logger  The logger to use
     * @param content The content to parse
     * @return The AST root node, or null of the parsing failed
     */
    public static ASTNode parseJson(Logger logger, String content) {
        return parseJson(logger, new StringReader(content));
    }

    /**
     * Parses the JSON content
     *
     * @param logger The logger to use
     * @param reader The reader with the content to parse
     * @return The AST root node, or null of the parsing failed
     */
    public static ASTNode parseJson(Logger logger, Reader reader) {
        ParseResult result = doParseJson(logger, reader);
        if (result == null)
            return null;
        if (!result.getErrors().isEmpty()) {
            for (ParseError error : result.getErrors())
                logger.error(error);
            return null;
        }
        return result.getRoot();
    }

    /**
     * Parses the JSON content
     *
     * @param logger The logger to use
     * @param reader The reader with the content to parse
     * @return The parse result
     */
    private static ParseResult doParseJson(Logger logger, Reader reader) {
        ParseResult result;
        try {
            String content = IOUtils.read(reader);
            JsonLexer lexer = new JsonLexer(content);
            JsonParser parser = new JsonParser(lexer);
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

    /**
     * The repository to use
     */
    private final Repository repository;

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
    public JsonLoader(Repository repository) {
        this.repository = repository;
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        return doParseJson(logger, reader);
    }

    @Override
    public RDFLoaderResult loadRDF(Logger logger, Reader reader, String resourceIRI, String graphIRI) {
        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return null;
        try {

            return loadDocument(parseResult.getRoot(), parseResult.getInput());
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
    }

    @Override
    public OWLLoaderResult loadOWL(Logger logger, Reader reader, String uri) {
        throw new UnsupportedOperationException();
    }
}
