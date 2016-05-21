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

import org.xowl.infra.store.Serializable;

import java.util.Collection;

/**
 * Represents the current status of a RDF rule
 */
public class RDFRuleStatus implements Serializable {
    /**
     * The rule's execution
     */
    private final Collection<RDFRuleExecution> executions;

    /**
     * Initializes this status
     *
     * @param executions The known executions
     */
    public RDFRuleStatus(Collection<RDFRuleExecution> executions) {
        this.executions = executions;
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder("{\"executions\": [");
        boolean first = true;
        for (RDFRuleExecution execution : executions) {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append(execution.serializedJSON());
        }
        builder.append("]}");
        return builder.toString();
    }
}
