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
import java.util.Collections;
import java.util.List;

/**
 * Represents a denotation rule, i.e. a rule that associates signs in a user's phrases to meaning elements as ontological entities (semes)
 *
 * @author Laurent Wouters
 */
public class DenotationRule {
    /**
     * The rule's title
     */
    private final String title;
    /**
     * The rule's antecedents
     */
    private final List<DenotationRuleAntecedent> antecedents;
    /**
     * The rule's consequents
     */
    private final List<DenotationRuleConsequent> consequents;

    /**
     * Initializes this rule
     *
     * @param title The rule's title
     */
    public DenotationRule(String title) {
        this.title = title;
        this.antecedents = new ArrayList<>();
        this.consequents = new ArrayList<>();
    }

    /**
     * Gets the rule's title
     *
     * @return The rule's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the rule's antecedents
     *
     * @return The rule's antecedents
     */
    public List<DenotationRuleAntecedent> getAntecedents() {
        return Collections.unmodifiableList(antecedents);
    }

    /**
     * Adds an antecedent to this rule
     *
     * @param antecedent The antecedent to add
     */
    public void addAntecedent(DenotationRuleAntecedent antecedent) {
        antecedents.add(antecedent);
    }

    /**
     * Removes an antecedent from this rule
     *
     * @param antecedent The antecedent to remove
     */
    public void removeAntecedent(DenotationRuleAntecedent antecedent) {
        antecedents.remove(antecedent);
    }

    /**
     * Gets the rule's consequents
     *
     * @return The rule's consequents
     */
    public List<DenotationRuleConsequent> getConsequents() {
        return Collections.unmodifiableList(consequents);
    }

    /**
     * Adds a consequent to this rule
     *
     * @param consequent The consequent to add
     */
    public void addConsequent(DenotationRuleConsequent consequent) {
        consequents.add(consequent);
    }

    /**
     * Removes a consequent from this rule
     *
     * @param consequent The consequent to remove
     */
    public void removeConsequent(DenotationRuleConsequent consequent) {
        consequents.remove(consequent);
    }
}
