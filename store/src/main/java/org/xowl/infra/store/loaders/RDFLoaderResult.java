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
package org.xowl.infra.store.loaders;

import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.RDFRule;

import java.util.ArrayList;
import java.util.Collection;
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
    private final List<Quad> quads;
    /**
     * The loaded rules
     */
    private final List<RDFRule> rules;
    /**
     * The imported IRIs
     */
    private final List<String> imports;

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
    public List<RDFRule> getRules() {
        return rules;
    }

    /**
     * Gets the imported documents from the loaded ontology
     *
     * @return The imported documents
     */
    public Collection<String> getImports() {
        return imports;
    }

    /**
     * Initializes an empty result
     */
    public RDFLoaderResult() {
        quads = new ArrayList<>();
        rules = new ArrayList<>();
        imports = new ArrayList<>();
    }
}
