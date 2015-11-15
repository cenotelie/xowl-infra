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
package org.xowl.store.writers;

import org.xowl.store.rdf.Quad;
import org.xowl.utils.logging.Logger;

import java.util.Iterator;

/**
 * Represents a writer of RDF data
 *
 * @author Laurent Wouters
 */
public interface RDFSerializer {
    /**
     * Serializes the specified quads
     *
     * @param logger The logger to use
     * @param quads  The quads to serialize
     */
    void serialize(Logger logger, Iterator<Quad> quads);
}
