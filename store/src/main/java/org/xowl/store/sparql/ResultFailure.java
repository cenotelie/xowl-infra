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

/**
 * Represents the failing result of a SPARQL command
 *
 * @author Laurent Wouters
 */
public class ResultFailure implements Result {
    /**
     * The singleton instance with an empty message
     */
    public static final ResultFailure INSTANCE = new ResultFailure(null);

    /**
     * The message, if any
     */
    private final String message;

    /**
     * Gets the message, if any
     *
     * @return The message, if any
     */
    public String getMessage() {
        return message;
    }

    /**
     * Initializes this result
     *
     * @param message The message, if any
     */
    public ResultFailure(String message) {
        this.message = message;
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}
