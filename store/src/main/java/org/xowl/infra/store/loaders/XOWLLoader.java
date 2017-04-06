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
import org.xowl.infra.store.Evaluator;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Loader of xOWL ontologies serialized
 *
 * @author Laurent Wouters
 */
public class XOWLLoader implements Loader {
    /**
     * The loader of XOWL deserialization services
     */
    private static ServiceLoader<XOWLDeserializerProvider> SERVICE_PROVIDER = ServiceLoader.load(XOWLDeserializerProvider.class);

    /**
     * The current evaluator
     */
    private final Evaluator evaluator;

    /**
     * The current evaluator
     *
     * @param evaluator The current evaluator
     */
    public XOWLLoader(Evaluator evaluator) {
        this.evaluator = evaluator;
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
        Iterator<XOWLDeserializerProvider> services = SERVICE_PROVIDER.iterator();
        XOWLDeserializer deserializer = services.hasNext() ? services.next().newDeserializer(evaluator) : new DefaultXOWLDeserializer(evaluator);
        return deserializer.deserialize(uri, result.getRoot());
    }
}
