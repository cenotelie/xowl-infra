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

package org.xowl.store.sparql;

import org.xowl.store.AbstractRepository;
import org.xowl.store.rdf.Quad;
import org.xowl.store.writers.*;
import org.xowl.utils.SinkLogger;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents the result of a SPARQL command as a set of quads
 *
 * @author Laurent Wouters
 */
public class ResultQuads implements Result {
    /**
     * The quads
     */
    private final Collection<Quad> quads;

    /**
     * Gets the quads
     *
     * @return The quads
     */
    public Collection<Quad> getQuads() {
        return Collections.unmodifiableCollection(quads);
    }

    /**
     * Initializes this result
     *
     * @param quads The quads
     */
    public ResultQuads(Collection<Quad> quads) {
        this.quads = quads;
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public void print(Writer writer, String syntax) throws IOException {
        RDFSerializer serializer;
        switch (syntax) {
            case AbstractRepository.SYNTAX_NTRIPLES:
                serializer = new NTripleSerializer(writer);
                break;
            case AbstractRepository.SYNTAX_NQUADS:
                serializer = new NQuadsSerializer(writer);
                break;
            case AbstractRepository.SYNTAX_TURTLE:
                serializer = new TurtleSerializer(writer);
                break;
            case AbstractRepository.SYNTAX_RDFXML:
                serializer = new RDFXMLSerializer(writer);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format " + syntax);
        }
        SinkLogger logger = new SinkLogger();
        serializer.serialize(logger, quads.iterator());
    }
}