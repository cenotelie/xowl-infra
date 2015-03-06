/**********************************************************************
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
 **********************************************************************/
package org.xowl.store.rdf;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a RDF rule for a RDf rule engine
 * A rule operates over 3 ontologies: the source, the target and the meta.
 * A rule matches antecedents in a source and a meta ontology.
 * It outputs consequents in a target and the same meta ontology.
 *
 * @author Laurent Wouters
 */
public class Rule {
    /**
     * The rule's identifying IRI
     */
    private String iri;
    /**
     * The positive antecedents to match in the source ontology
     */
    private List<Quad> antecedentSourcePositives;
    /**
     * The negative antecedents conjunctions to NOT match in the source ontology
     */
    private List<List<Quad>> antecedentSourceNegatives;
    /**
     * The positive antecedents to match in the meta ontology
     */
    private List<Quad> antecedentMetaPositives;
    /**
     * The negative antecedents conjunctions to NOT match in the meta ontology
     */
    private List<List<Quad>> antecedentMetaNegatives;
    /**
     * The positive consequents for the target ontology
     */
    private List<Quad> consequentTargetPositives;
    /**
     * The negative consequents for the target ontology
     */
    private List<Quad> consequentTargetNegatives;
    /**
     * The positive consequents for the meta ontology
     */
    private List<Quad> consequentMetaPositives;
    /**
     * The negative consequents for the meta ontology
     */
    private List<Quad> consequentMetaNegatives;
    /**
     * The variables found in the antecedents
     */
    private List<VariableNode> antecedentVariables;
    /**
     * The variables found in the consequents
     */
    private List<VariableNode> consequentVariables;

    /**
     * Gets the rule's identifying IRI
     *
     * @return The rule's identifying IRI
     */
    public String getIRI() {
        return iri;
    }

    /**
     * Gets the positive antecedents to match in the source ontology
     *
     * @return The positive antecedents to match in the source ontology
     */
    public List<Quad> getAntecedentSourcePositives() {
        return antecedentSourcePositives;
    }

    /**
     * Gets the negative antecedents conjunctions to NOT match in the source ontology
     *
     * @return The negative antecedents conjunctions to NOT match in the source ontology
     */
    public List<List<Quad>> getAntecedentSourceNegatives() {
        return antecedentSourceNegatives;
    }

    /**
     * Gets the positive antecedents to match in the meta ontology
     *
     * @return The positive antecedents to match in the meta ontology
     */
    public List<Quad> getAntecedentMetaPositives() {
        return antecedentMetaPositives;
    }

    /**
     * Gets the negative antecedents conjunctions to NOT match in the meta ontology
     *
     * @return The negative antecedents conjunctions to NOT match in the meta ontology
     */
    public List<List<Quad>> getAntecedentMetaNegatives() {
        return antecedentMetaNegatives;
    }

    /**
     * Gets the positive consequents for the target ontology
     *
     * @return The positive consequents for the target ontology
     */
    public List<Quad> getConsequentTargetPositives() {
        return consequentTargetPositives;
    }

    /**
     * Gets the negative consequents for the target ontology
     *
     * @return The negative consequents for the target ontology
     */
    public List<Quad> getConsequentTargetNegatives() {
        return consequentTargetNegatives;
    }

    /**
     * Gets the positive consequents for the meta ontology
     *
     * @return The positive consequents for the meta ontology
     */
    public List<Quad> getConsequentMetaPositives() {
        return consequentMetaPositives;
    }

    /**
     * Gets the negative consequents for the meta ontology
     *
     * @return The negative consequents for the meta ontology
     */
    public List<Quad> getConsequentMetaNegatives() {
        return consequentMetaNegatives;
    }

    /**
     * Initializes this rule
     *
     * @param iri The rule's identifying iri
     */
    public Rule(String iri) {
        this.iri = iri;
        this.antecedentSourcePositives = new ArrayList<>();
        this.antecedentSourceNegatives = new ArrayList<>();
        this.antecedentMetaPositives = new ArrayList<>();
        this.antecedentMetaNegatives = new ArrayList<>();
        this.consequentTargetPositives = new ArrayList<>();
        this.consequentTargetNegatives = new ArrayList<>();
        this.consequentMetaPositives = new ArrayList<>();
        this.consequentMetaNegatives = new ArrayList<>();
        this.antecedentVariables = new ArrayList<>();
        this.consequentVariables = new ArrayList<>();
    }
}
