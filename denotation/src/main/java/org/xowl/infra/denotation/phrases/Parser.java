/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.denotation.phrases;

import org.xowl.infra.utils.Identifiable;

/**
 * Represents a parser for phrase produced by users
 * A parser reads an input phrase and produces a phrase, i.e. a list of signs.
 *
 * @param <T> The type of input for the parser
 * @author Laurent Wouters
 */
public interface Parser<T> extends Identifiable {
    /**
     * Parses the specified input
     *
     * @param input The input to parse
     * @return The resulting phrase
     */
    Phrase parse(T input);
}
