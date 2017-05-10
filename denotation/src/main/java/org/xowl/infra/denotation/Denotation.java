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

import org.xowl.infra.denotation.rules.DenotationRule;
import org.xowl.infra.utils.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents the user's denotation of a specific phrases
 *
 * @author Laurent Wouters
 */
public class Denotation implements Serializable {
    /**
     * The denotation rules for this denotation
     */
    private final Collection<DenotationRule> rules;

    /**
     * Initializes an empty denotation
     */
    public Denotation() {
        this.rules = new ArrayList<>();
    }

    /**
     * Gets the denotation rules for this denotation
     *
     * @return The denotation rules for this denotation
     */
    public Collection<DenotationRule> getRules() {
        return Collections.unmodifiableCollection(rules);
    }

    /**
     * Adds a rule to this denotation
     *
     * @param rule The rule to add
     */
    public void addRule(DenotationRule rule) {
        rules.add(rule);
    }

    /**
     * Removes a rule from this denotation
     *
     * @param rule The rule to remove
     */
    public void removeRule(DenotationRule rule) {
        rules.remove(rule);
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"type\": \"");
        builder.append(Denotation.class.getCanonicalName());
        builder.append("\", \"rules\": [");
        boolean first = true;
        for (DenotationRule rule : rules) {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append(rule.serializedJSON());
        }
        builder.append("]}");
        return builder.toString();
    }
}
