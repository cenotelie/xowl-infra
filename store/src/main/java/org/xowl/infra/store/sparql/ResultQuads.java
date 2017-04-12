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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.utils.http.HttpConstants;
import org.xowl.infra.utils.logging.Logging;

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
        RDFUtils.serialize(writer, Logging.get(), quads.iterator(), syntax);
    }

    @Override
    public String serializedString() {
        return RDFUtils.serialize(quads.iterator(), Repository.SYNTAX_XRDF);
    }

    @Override
    public String serializedJSON() {
        return RDFUtils.serialize(quads.iterator(), HttpConstants.MIME_JSON);
    }
}
