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

package org.xowl.infra.denotation;

/**
 * Implements the "is-a" denotation rule that associates the pattern of symbols to concepts in the domain.
 * The operational interpretation of this rule is that the domain concepts can be ontologically instantiated for
 * each solution to the pattern of symbols.
 * An example of such rule is "Each red box in the diagram is a system function" where:
 * - the pattern of symbols is a single symbol with the "box" shape property and the "red" color property
 * - the instantiable domain concept is "system function"
 * When this rule is matched, a new instance of the domain concepts is created and associated to the symbols.
 *
 * @author Laurent Wouters
 */
public class DenotationRuleIsA {

}
