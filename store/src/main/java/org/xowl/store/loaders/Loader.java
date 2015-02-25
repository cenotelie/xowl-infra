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

import org.xowl.hime.redist.ParseResult;
import org.xowl.store.rdf.Quad;
import org.xowl.utils.Logger;

import java.io.Reader;
import java.util.List;

/**
 * Represents a loader of serialized data
 *
 * @author Laurent Wouters
 */
public interface Loader {
    /**
     * Parses the specified input
     *
     * @param logger The logger to use
     * @param reader The input to parse
     * @return The result of the parsing operation
     */
    public ParseResult parse(Logger logger, Reader reader);

    /**
     * Loads quads from the specified input
     *
     * @param logger The logger to use
     * @param reader The resource's reader
     * @param uri    The resource's URI
     * @return The loaded quads, or <code>null</code> if an error occured
     */
    public List<Quad> loadQuads(Logger logger, Reader reader, String uri);

    /**
     * Loads axioms from the specified input
     *
     * @param logger The logger to use
     * @param reader The resource's reader
     * @param uri    The resource's URI
     * @return The loaded data
     */
    public LoaderResult loadAxioms(Logger logger, Reader reader, String uri);
}
