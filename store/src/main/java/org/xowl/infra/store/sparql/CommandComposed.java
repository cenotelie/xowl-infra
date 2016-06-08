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

import java.util.Arrays;
import java.util.Collection;

/**
 * Implements a SPARQL command composed of other commands
 *
 * @author Laurent Wouters
 */
public class CommandComposed implements Command {
    /**
     * The inner commands
     */
    private final Command[] commands;

    /**
     * Initializes this command
     * @param commands The inner commands
     */
    public CommandComposed(Command[] commands) {
        this.commands = Arrays.copyOf(commands, commands.length);
    }

    /**
     * Initializes this command
     * @param commands The inner commands
     */
    public CommandComposed(Collection<Command> commands) {
        this.commands = new Command[commands.size()];
        int i = 0;
        for (Command command : commands) {
            this.commands[i++] = command;
        }
    }

    @Override
    public Result execute(Repository repository) {
        for (int i = 0; i != commands.length; i++) {
            Result result = commands[i].execute(repository);
            if (result.isFailure())
                return result;
        }
        return ResultSuccess.INSTANCE;
    }
}
