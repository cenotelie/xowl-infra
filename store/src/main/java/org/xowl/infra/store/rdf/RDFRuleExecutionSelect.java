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
 * Represents the data of a SELECT RDF rule execution
 *
 * @author Laurent Wouters
 */
public class RDFRuleExecutionSelect extends RDFRuleExecution {
    /**
     * The solution that triggered this execution
     */
    private final RDFPatternSolution solution;

    /**
     * Initializes this data
     *
     * @param rule     The original rule
     * @param solution The solution that triggered this execution
     */
    public RDFRuleExecutionSelect(RDFRuleSelect rule, RDFPatternSolution solution) {
        super(rule);
        this.solution = solution;
    }

    @Override
    public RDFPatternSolution getSolution() {
        return solution;
    }
}
