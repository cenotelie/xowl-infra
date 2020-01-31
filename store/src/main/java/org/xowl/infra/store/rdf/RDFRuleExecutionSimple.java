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

package org.xowl.infra.store.rdf;

/**
 * Represents the data of a simple RDF rule execution
 *
 * @author Laurent Wouters
 */
public class RDFRuleExecutionSimple extends RDFRuleExecution {
    /**
     * The pattern match that triggered this execution
     */
    private final RDFPatternMatch match;
    /**
     * The solution that triggered this execution
     */
    private final RDFPatternSolution solution;

    /**
     * Initializes this data
     *
     * @param rule  The original rule
     * @param match The pattern match that triggered this execution
     */
    public RDFRuleExecutionSimple(RDFRuleSimple rule, RDFPatternMatch match) {
        super(rule);
        this.match = match;
        this.solution = match.getSolution();
    }

    /**
     * Gets the pattern match that triggered this execution
     *
     * @return The pattern match that triggered this execution
     */
    public RDFPatternMatch getMatch() {
        return match;
    }

    @Override
    public RDFPatternSolution getSolution() {
        return solution;
    }
}
