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

import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.hime.redist.TextContext;
import org.xowl.hime.redist.parsers.InitializationException;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Reader;

/**
 * Loader of xOWL ontologies serialized
 *
 * @author Laurent Wouters
 */
public class XOWLLoader implements Loader {
    /**
     * The current execution manager
     */
    private final ExecutionManager executionManager;

    /**
     * Initializes this loader
     *
     * @param executionManager The current execution manager
     */
    public XOWLLoader(ExecutionManager executionManager) {
        this.executionManager = executionManager;
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result;
        try {
            String content = IOUtils.read(reader);
            XOWLLexer lexer = new XOWLLexer(content);
            XOWLParser parser = new XOWLParser(lexer);
            parser.setModeRecoverErrors(false);
            result = parser.parse();
        } catch (IOException | InitializationException exception) {
            logger.error(exception);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public OWLLoaderResult loadOWL(Logger logger, Reader reader, String uri) {
        ParseResult result = parse(logger, reader);
        if (result == null || !result.isSuccess() || result.getErrors().size() > 0)
            return null;
        return executionManager.getDeserializer().deserialize(uri, result.getRoot());
    }
}
