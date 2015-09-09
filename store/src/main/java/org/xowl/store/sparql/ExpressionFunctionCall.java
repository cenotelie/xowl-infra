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

import org.xowl.store.Repository;
import org.xowl.store.rdf.QuerySolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the call to an external function in an expression
 *
 * @author Laurent Wouters
 */
public class ExpressionFunctionCall implements Expression {
    /**
     * The function's IRI
     */
    private final String iri;
    /**
     * The arguments to use
     */
    private final List<Expression> arguments;

    /**
     * Initializes this expression
     *
     * @param iri       The function's IRI
     * @param arguments The arguments to use
     */
    public ExpressionFunctionCall(String iri, List<Expression> arguments) {
        this.iri = iri;
        this.arguments = new ArrayList<>(arguments);
    }

    @Override
    public Object eval(Repository repository, QuerySolution bindings) {
        throw new UnsupportedOperationException();
    }
}
