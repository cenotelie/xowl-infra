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

import org.xowl.store.rete.Token;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Represents an explanation of how a fact has been produced by the engine
 *
 * @author Laurent Wouters
 */
public class RuleEngineExplanation {
    /**
     * The rule the caused a fact to be produced
     */
    private final Rule rule;
    /**
     * The bindings used for firing the rule
     */
    private final Map<VariableNode, Node> bindings;
    /**
     * The fact that has been produced
     */
    private final Quad produced;
    /**
     * The parent explanations
     */
    private final List<RuleEngineExplanation> parents;

    /**
     * Gets the rule the caused a fact to be produced
     *
     * @return The rule the caused a fact to be produced
     */
    public Rule getRule() {
        return rule;
    }

    /**
     * Gets the bindings used for firing the rule
     *
     * @return The bindings used for firing the rule
     */
    public Map<VariableNode, Node> getBindings() {
        return bindings;
    }

    /**
     * Gets the fact that has been produced
     *
     * @return The fact that has been produced
     */
    public Quad getProducedFact() {
        return produced;
    }

    /**
     * Gets the parent explanations
     *
     * @return The parent explanations
     */
    public Collection<RuleEngineExplanation> getParents() {
        return parents;
    }

    /**
     * Initializes this explanation
     *
     * @param rule     The rule the caused a fact to be produced
     * @param token    The token used for firing the rule
     * @param produced The fact that has been produced
     */
    public RuleEngineExplanation(Rule rule, Token token, Quad produced) {
        this.rule = rule;
        this.bindings = new HashMap<>();
        this.produced = produced;
        this.parents = new ArrayList<>();
        Map<VariableNode, Node> originals = token.getBindings();
        addBindings(rule.getAntecedentSourcePositives(), originals);
        addBindings(rule.getAntecedentMetaPositives(), originals);
        for (Collection<Quad> quads : rule.getAntecedentSourceNegatives())
            addBindings(quads, originals);
        for (Collection<Quad> quads : rule.getAntecedentMetaNegatives())
            addBindings(quads, originals);
        addBindings(rule.getConsequentTargetPositives(), originals);
        addBindings(rule.getConsequentMetaPositives(), originals);
        addBindings(rule.getConsequentTargetNegatives(), originals);
        addBindings(rule.getConsequentMetaNegatives(), originals);
    }

    /**
     * Adds the bindings
     *
     * @param quads     A collection of facts
     * @param originals The original bindings
     */
    private void addBindings(Collection<Quad> quads, Map<VariableNode, Node> originals) {
        for (Quad quad : quads)
            addBindings(quad, originals);
    }

    /**
     * Adds the bindings
     *
     * @param quad      A fact
     * @param originals The original bindings
     */
    private void addBindings(Quad quad, Map<VariableNode, Node> originals) {
        addBindings(quad.getGraph(), originals);
        addBindings(quad.getSubject(), originals);
        addBindings(quad.getProperty(), originals);
        addBindings(quad.getObject(), originals);
    }

    /**
     * Adds the bindings
     *
     * @param node      A node
     * @param originals The original bindings
     */
    private void addBindings(Node node, Map<VariableNode, Node> originals) {
        if (node.getNodeType() == VariableNode.TYPE) {
            VariableNode variableNode = (VariableNode) node;
            Node value = bindings.get(variableNode);
            if (value == null) {
                bindings.put(variableNode, originals.get(variableNode));
            }
        }
    }

    /**
     * Gets the instantiated antecedents that triggered the rule
     *
     * @return The instantiated antecedents
     */
    protected List<Quad> getAntecedents() {
        List<Quad> result = new ArrayList<>();
        for (Quad quad : rule.getAntecedentSourcePositives())
            result.add(instantiate(quad));
        for (Quad quad : rule.getAntecedentMetaPositives())
            result.add(instantiate(quad));
        return result;
    }

    /**
     * Prints this explanation using the specified writer
     *
     * @param writer A writer
     */
    public void print(Writer writer) throws IOException {
        for (RuleEngineExplanation parent : parents) {
            parent.print(writer);
        }

        writer.write(System.lineSeparator());
        writer.write("rule " + rule.getIRI() + " {" + System.lineSeparator());
        for (Quad quad : rule.getAntecedentSourcePositives()) {
            writer.write("\t");
            writer.write(instantiate(quad).toString());
            writer.write(System.lineSeparator());
        }
        for (Quad quad : rule.getAntecedentMetaPositives()) {
            writer.write("\tmeta( ");
            writer.write(instantiate(quad).toString());
            writer.write(" )" + System.lineSeparator());
        }
        writer.write("} => {" + System.lineSeparator());
        writer.write(produced.toString());
        writer.write("}" + System.lineSeparator());
    }

    /**
     * Instantiate a quad
     *
     * @param pattern The quad pattern
     * @return The instantiated quad
     */
    private Quad instantiate(Quad pattern) {
        return new Quad((GraphNode) instantiate(pattern.getGraph()),
                (SubjectNode) instantiate(pattern.getSubject()),
                (Property) instantiate(pattern.getProperty()),
                instantiate(pattern.getObject()));
    }

    /**
     * Instantiate a node
     *
     * @param node A node
     * @return The instantiated node
     */
    private Node instantiate(Node node) {
        if (node.getNodeType() == VariableNode.TYPE)
            return bindings.get(node);
        return node;
    }
}
