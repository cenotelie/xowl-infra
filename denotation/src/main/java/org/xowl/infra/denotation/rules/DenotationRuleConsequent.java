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

package org.xowl.infra.denotation.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents a consequent in a denotation rule
 *
 * @author Laurent Wouters
 */
public abstract class DenotationRuleConsequent {
    /**
     * The antecedent elements to bind to this consequent
     */
    protected Collection<DenotationRuleAntecedent> bindings;

    /**
     * Gets the antecedent elements to bind to this consequent
     *
     * @return The antecedent elements to bind to this consequent
     */
    public Collection<DenotationRuleAntecedent> getBindings() {
        if (bindings == null)
            return Collections.emptyList();
        return Collections.unmodifiableCollection(bindings);
    }

    /**
     * Adds an antecedent to bind to this consequent
     *
     * @param antecedent The antecedent to bind
     */
    public void addBindings(DenotationRuleAntecedent antecedent) {
        if (bindings == null)
            bindings = new ArrayList<>();
        bindings.add(antecedent);
    }

    /**
     * Removes an antecedent from the bindings to this consequent
     *
     * @param antecedent The antecedent to un-bind
     */
    public void removeBinding(DenotationRuleAntecedent antecedent) {
        if (bindings == null)
            return;
        bindings.remove(antecedent);
    }
}
