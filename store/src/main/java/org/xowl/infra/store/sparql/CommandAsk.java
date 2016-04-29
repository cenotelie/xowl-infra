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

import org.xowl.infra.store.Repository;

/**
 * Represents the SPARQL ASK command.
 * Applications can use the ASK form to test whether or not a query pattern has a solution.
 * No information is returned about the possible query solutions, just whether or not a solution exists.
 *
 * @author Laurent Wouters
 */
public class CommandAsk implements Command {
    /**
     * The graph pattern for this command
     */
    private final GraphPattern pattern;

    /**
     * Initializes this command
     *
     * @param pattern The graph pattern for this command
     */
    public CommandAsk(GraphPattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Result execute(Repository repository) {
        try {
            Solutions solutions = pattern.match(repository);
            return new ResultYesNo(solutions.size() > 0);
        } catch (EvalException exception) {
            return new ResultFailure(exception.getMessage());
        }
    }
}
