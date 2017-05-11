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

package org.xowl.infra.store;

/**
 * Constant IRIs
 *
 * @author Laurent Wouters
 */
public class IRIs {
    /**
     * The IRI of the RDF schema
     */
    public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns";
    /**
     * The IRI of the RDFS schema
     */
    public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema";
    /**
     * The IRI of the XSD schema
     */
    public static final String XSD = "http://www.w3.org/2001/XMLSchema";
    /**
     * The IRI of the OWL2 schema
     */
    public static final String OWL2 = "http://www.w3.org/2002/07/owl";
    /**
     * The IRI of the Dublin Core schema
     */
    public static final String DC = "http://purl.org/dc/elements/1.1/";

    /**
     * The base IRI of the XOWL schema
     */
    public static final String XOWL_LANG = "http://xowl.org/infra/lang/";
    /**
     * The base IRI of the XOWL entailment rules
     */
    public static final String XOWL_RULES = "http://xowl.org/infra/store/rules/";
    /**
     * The IRI of the XOWL entailment rules
     */
    public static final String XOWL_RULES_XOWL = "http://xowl.org/infra/store/rules/xowl";

    /**
     * URI of the default graph
     */
    public static final String GRAPH_DEFAULT = "http://xowl.org/infra/store/rdfgraphs/default";
    /**
     * URI of the graph used for inferred data
     */
    public static final String GRAPH_INFERENCE = "http://xowl.org/infra/store/rdfgraphs/inferred";
    /**
     * URI of the graph used for meta-data
     */
    public static final String GRAPH_META = "http://xowl.org/infra/store/rdfgraphs/meta";
}
