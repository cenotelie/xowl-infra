/**********************************************************************
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
 **********************************************************************/
package org.xowl.store.loaders;

import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the data de-serialized by a RDF loader
 *
 * @author Laurent Wouters
 */
public class RDFLoaderResult {
    /**
     * The loaded quads
     */
    private List<Quad> quads;
    /**
     * The loaded rules
     */
    private List<Rule> rules;

    /**
     * Gets the loaded quads
     *
     * @return The loaded quads
     */
    public List<Quad> getQuads() {
        return quads;
    }

    /**
     * Gets the loaded rules
     *
     * @return The loaded rules
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * Initializes an empty result
     */
    public RDFLoaderResult() {
        quads = new ArrayList<>();
        rules = new ArrayList<>();
    }
}
