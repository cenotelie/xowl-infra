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

import org.xowl.infra.denotation.phrases.SignRelation;
import org.xowl.infra.utils.collections.Couple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents a pattern of a sign in a phrase as an antecedent to a denotation rule
 *
 * @author Laurent Wouters
 */
public class DenotationRuleAntecedentSignPattern implements DenotationRuleAntecedent {
    /**
     * The identifier for this pattern
     */
    private final String identifier;
    /**
     * The constraint on the sign's properties
     */
    private Expression properties;
    /**
     * The constraint on the sign's relations
     */
    private Collection<Couple<SignRelation, DenotationRuleAntecedentSignPattern>> relations;
    /**
     * The identifier of the variable for the bound domain element, if any
     */
    private String seme;

    /**
     * Initializes this pattern
     *
     * @param identifier The identifier for this pattern
     */
    public DenotationRuleAntecedentSignPattern(String identifier) {
        this.identifier = identifier;
        this.properties = null;
        this.relations = null;
        this.seme = null;
    }

    /**
     * Gets the identifier for this pattern
     *
     * @return The identifier for this pattern
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the constraint on the sign's properties
     *
     * @return The constraint on the sign's properties
     */
    public Expression getPropetiesConstraint() {
        return properties;
    }

    /**
     * Sets the constraint on the sign's properties
     *
     * @param constraint The constraint on the sign's properties
     */
    public void setPropertiesConstraint(Expression constraint) {
        this.properties = constraint;
    }

    /**
     * Gets the constraints on the sign's relations
     *
     * @return The constraints on the sign's relations
     */
    public Collection<Couple<SignRelation, DenotationRuleAntecedentSignPattern>> getRelationsConstraints() {
        if (relations == null)
            return Collections.emptyList();
        return Collections.unmodifiableCollection(relations);
    }

    /**
     * Adds a relation constraint
     *
     * @param relation The relation
     * @param pattern  The pattern of related sign
     */
    public void addRelationConstraint(SignRelation relation, DenotationRuleAntecedentSignPattern pattern) {
        if (relations == null)
            relations = new ArrayList<>();
        relations.add(new Couple<>(relation, pattern));
    }

    /**
     * Removes a relation constraint
     *
     * @param relation The relation
     * @param pattern  The pattern of related sign
     */
    public void removeRelationConstraint(SignRelation relation, DenotationRuleAntecedentSignPattern pattern) {
        if (relations == null)
            return;
        for (Couple<SignRelation, DenotationRuleAntecedentSignPattern> couple : relations) {
            if (couple.x == relation && couple.y == pattern) {
                relations.remove(couple);
                return;
            }
        }
    }

    /**
     * Gets the identifier of the variable for the bound domain element, if any
     *
     * @return The identifier of the variable for the bound domain element, if any
     */
    public String getBoundSeme() {
        return seme;
    }

    /**
     * Sets the identifier of the variable for the bound domain element, if any
     *
     * @param identifier The identifier of the variable for the bound domain element, if any
     */
    public void setBoundSeme(String identifier) {
        this.seme = identifier;
    }
}
