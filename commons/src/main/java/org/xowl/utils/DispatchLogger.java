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

package org.xowl.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Logger that dispatches its messages to other loggers
 *
 * @author Laurent Wouters
 */
public class DispatchLogger implements Logger {
    /**
     * The inner loggers
     */
    private final List<Logger> inners;

    public DispatchLogger(Logger... inners) {
        this.inners = Arrays.asList(inners);
    }

    @Override
    public void debug(Object message) {
        for (Logger inner : inners)
            inner.debug(message);
    }

    @Override
    public void info(Object message) {
        for (Logger inner : inners)
            inner.info(message);
    }

    @Override
    public void warning(Object message) {
        for (Logger inner : inners)
            inner.warning(message);
    }

    @Override
    public void error(Object message) {
        for (Logger inner : inners)
            inner.error(message);
    }
}
