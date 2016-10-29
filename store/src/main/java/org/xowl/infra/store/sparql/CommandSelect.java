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

import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.rdf.Node;

import java.util.Map;

/**
 * Represents the SPARQL SELECT command.
 * The SELECT form of results returns variables and their bindings directly.
 * It combines the operations of projecting the required variables with introducing new variable bindings into a query solution.
 *
 * @author Laurent Wouters
 */
public class CommandSelect implements Command {
    /**
     * The graph pattern for this command
     */
    private final GraphPattern pattern;

    /**
     * Initializes this command
     *
     * @param pattern The graph pattern for this command
     */
    public CommandSelect(GraphPattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean isUpdateCommand() {
        return false;
    }

    @Override
    public Result execute(RepositoryRDF repository) {
        try {
            return new ResultSolutions(pattern.eval(new EvalContextRepository(repository)));
        } catch (EvalException exception) {
            return new ResultFailure(exception.getMessage());
        }
    }

    @Override
    public Command clone(Map<String, Node> parameters) {
        return new CommandSelect(pattern.clone(parameters));
    }
}
