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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.Serializable;

import java.io.IOException;
import java.io.Writer;

/**
 * Represents the result of a SPARQL command
 *
 * @author Laurent Wouters
 */
public interface Result extends Serializable {
    /**
     * XML syntax for SPARQL results
     */
    String SYNTAX_XML = "application/sparql-results+xml";
    /**
     * JSON syntax for SPARQL results
     */
    String SYNTAX_JSON = "application/sparql-results+json";
    /**
     * CSV syntax for SPARQL results
     */
    String SYNTAX_CSV = "text/csv";
    /**
     * TSV syntax for SPARQL results
     */
    String SYNTAX_TSV = "text/tab-separated-values";

    /**
     * Gets whether the result is a failure
     *
     * @return Whether the result is a failure
     */
    boolean isFailure();

    /**
     * Gets whether the result is a success
     *
     * @return Whether the result is a success
     */
    boolean isSuccess();

    /**
     * Prints this result to the specified writer
     *
     * @param writer A writer
     * @param syntax The syntax to user
     * @throws IOException When writing to an incorrect writer
     */
    void print(Writer writer, String syntax) throws IOException;
}
