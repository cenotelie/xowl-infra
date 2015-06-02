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
package org.xowl.store.loaders;

import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.hime.redist.TextContext;
import org.xowl.hime.redist.parsers.InitializationException;
import org.xowl.utils.Files;
import org.xowl.utils.Logger;

import java.io.IOException;
import java.io.Reader;

/**
 * Loader of xOWL ontologies serialized in the Functional syntax
 *
 * @author Laurent Wouters
 */
public class XOWLLoader implements Loader {
    /**
     * The current lexical context
     */
    protected LexicalContext context;

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result;
        try {
            String content = Files.read(reader);
            XOWLLexer lexer = new XOWLLexer(content);
            XOWLParser parser = new XOWLParser(lexer);
            parser.setModeRecoverErrors(false);
            result = parser.parse();
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        } catch (InitializationException ex) {
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
    public RDFLoaderResult loadRDF(Logger logger, Reader reader, String uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OWLLoaderResult loadOWL(Logger logger, Reader reader, String uri) {
        throw new UnsupportedOperationException();
    }
}
