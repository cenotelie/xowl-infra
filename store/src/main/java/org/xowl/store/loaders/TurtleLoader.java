/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 **********************************************************************/

package org.xowl.store.loaders;

import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.rdf.RDFGraph;
import org.xowl.utils.Files;
import org.xowl.utils.Logger;

import java.io.IOException;
import java.io.Reader;

/**
 * Represents a loader of Turtle syntax
 *
 * @author Laurent Wouters
 */
public class TurtleLoader extends Loader {

    public TurtleLoader(RDFGraph graph) {

    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result = null;
        try {
            String content = Files.read(reader);
            TurtleLexer lexer = new TurtleLexer(content);
            TurtleParser parser = new TurtleParser(lexer);
            result = parser.parse();
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
        for (ParseError error : result.getErrors()) {
            logger.error(error);
            String[] context = result.getInput().getContext(error.getPosition());
            logger.error(context[0]);
            logger.error(context[1]);
        }
        return result;
    }

    @Override
    public Ontology load(Logger logger,  Reader reader) {
        return null;
    }
}
