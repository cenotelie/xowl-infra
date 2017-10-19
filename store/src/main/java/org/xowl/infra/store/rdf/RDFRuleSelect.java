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

import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.EvaluationException;
import org.xowl.infra.store.execution.Evaluator;
import org.xowl.infra.store.sparql.*;
import org.xowl.infra.store.storage.NodeManager;

import java.util.*;

/**
 * Represents a rule that uses a SPARQL SELECT query as an antecedent
 *
 * @author Laurent Wouters
 */
public class RDFRuleSelect extends RDFRule {
    /**
     * The antecedent graph pattern representing the SPARQL SELECT
     */
    private final GraphPattern antecedent;
    /**
     * The pattern for the rule consequents
     */
    private final RDFPattern consequents;
    /**
     * The RDF patterns on the antecedents of this rule
     */
    private final List<RDFPattern> patterns;
    /**
     * The state of this rule in various engine
     */
    private final Map<RDFRuleEngine.ProductionHandler, State> states;

    /**
     * The state of this rule
     */
    private static class State implements EvalContext {
        /**
         * The evaluator
         */
        private final Evaluator evaluator;
        /**
         * The node manager for the output
         */
        private final NodeManager nodes;
        /**
         * The known pattern matches
         */
        private final Map<RDFPattern, Collection<RDFPatternMatch>> matches;

        /**
         * Initializes this state
         *
         * @param handler The production handler to initialize from
         */
        public State(RDFRuleEngine.ProductionHandler handler) {
            this.evaluator = handler.getEvaluator();
            this.nodes = handler.getNodes();
            this.matches = new HashMap<>();
        }

        /**
         * Adds a pattern match
         *
         * @param pattern The matched pattern
         * @param match   The match
         */
        public void addMatch(RDFPattern pattern, RDFPatternMatch match) {
            synchronized (matches) {
                Collection<RDFPatternMatch> patternMatches = matches.get(pattern);
                if (patternMatches == null) {
                    patternMatches = new ArrayList<>();
                    matches.put(pattern, patternMatches);
                }
                patternMatches.add(match);
            }
        }

        /**
         * Removes a pattern match
         *
         * @param pattern The matched pattern
         * @param match   The match
         */
        public void removeMatch(RDFPattern pattern, RDFPatternMatch match) {
            synchronized (matches) {
                Collection<RDFPatternMatch> patternMatches = matches.get(pattern);
                if (patternMatches != null)
                    patternMatches.remove(match);
            }
        }

        @Override
        public Evaluator getEvaluator() {
            return evaluator;
        }

        @Override
        public NodeManager getNodes() {
            return nodes;
        }

        @Override
        public Solutions getSolutions(RDFPattern pattern) {
            synchronized (matches) {
                Collection<RDFPatternMatch> patternMatches = matches.get(pattern);
                if (patternMatches == null)
                    return new SolutionsMultiset(0);
                SolutionsMultiset solutions = new SolutionsMultiset(patternMatches.size());
                for (RDFPatternMatch match : patternMatches) {
                    solutions.add(match.getSolution());
                }
                return solutions;
            }
        }
    }

    /**
     * Initializes this rule
     *
     * @param iri        The rule's identifying iri
     * @param antecedent The antecedent graph pattern representing the SPARQL SELECT
     * @param guard      The rule's guard, if any
     */
    public RDFRuleSelect(String iri, GraphPattern antecedent, EvaluableExpression guard) {
        this(iri, antecedent, guard, null);
    }

    /**
     * Initializes this rule
     *
     * @param iri        The rule's identifying iri
     * @param antecedent The antecedent graph pattern representing the SPARQL SELECT
     * @param guard      The rule's guard, if any
     * @param source     The source for this rule
     */
    public RDFRuleSelect(String iri, GraphPattern antecedent, EvaluableExpression guard, String source) {
        super(iri, guard, source);
        this.antecedent = antecedent;
        this.consequents = new RDFPattern();
        this.patterns = new ArrayList<>();
        this.states = new HashMap<>();
        Inspector inspector = new Inspector() {
            @Override
            public void onGraphPattern(GraphPattern pattern) {
                if (pattern instanceof GraphPatternQuads) {
                    GraphPatternQuads graphPatternQuads = (GraphPatternQuads) pattern;
                    patterns.add(graphPatternQuads.getPattern());
                }
            }

            @Override
            public void onExpression(Expression expression) {
                // do nothing
            }
        };
        antecedent.inspect(inspector);
    }

    /**
     * Adds a positive consequent
     *
     * @param quad A positive consequent
     */
    public void addConsequentPositive(Quad quad) {
        consequents.getPositives().add(quad);
    }

    /**
     * Adds a negative consequent
     *
     * @param quad A negative consequent
     */
    public void addConsequentNegative(Quad quad) {
        Collection<Collection<Quad>> negatives = consequents.getNegatives();
        if (negatives.isEmpty())
            negatives.add(new ArrayList<Quad>());
        negatives.iterator().next().add(quad);
    }

    /**
     * Gets the state object of this rule for a rule engine
     *
     * @param handler The production handler of a rule engine
     * @return The associated state
     */
    private State getState(RDFRuleEngine.ProductionHandler handler) {
        synchronized (states) {
            State state = states.get(handler);
            if (state == null) {
                state = new State(handler);
                states.put(handler, state);
            }
            return state;
        }
    }

    @Override
    public Collection<RDFPattern> getPatterns() {
        return patterns;
    }

    @Override
    public Collection<VariableNode> getAntecedentVariables() {
        if (antecedent instanceof GraphPatternSelect) {
            Collection<VariableNode> result = ((GraphPatternSelect) antecedent).getProjectedVariables();
            if (!result.isEmpty())
                return result;
        }
        Collection<VariableNode> result = new ArrayList<>();
        for (RDFPattern pattern : patterns)
            findVariables(result, pattern);
        return result;
    }

    @Override
    public void onPatternMatched(RDFRuleEngine.ProductionHandler handler, RDFPattern pattern, RDFPatternMatch match) {
        State state = getState(handler);
        state.addMatch(pattern, match);
        try {
            onSolutionsChanged(handler, antecedent.eval(state));
        } catch (EvaluationException exception) {
            Logging.get().error(exception);
        }
    }

    @Override
    public void onPatternDematched(RDFRuleEngine.ProductionHandler handler, RDFPattern pattern, RDFPatternMatch match) {
        State state = getState(handler);
        state.removeMatch(pattern, match);
        try {
            onSolutionsChanged(handler, antecedent.eval(state));
        } catch (EvaluationException exception) {
            Logging.get().error(exception);
        }
    }

    /**
     * When the solutions to the antecedent may have changed
     *
     * @param handler   The current production handler
     * @param solutions The matched solutions
     */
    private void onSolutionsChanged(RDFRuleEngine.ProductionHandler handler, Solutions solutions) {
        // build the data
        List<RDFRuleExecutionSelect> executions = new ArrayList<>();
        Iterator<RDFRuleExecution> iterator = handler.getExecutions();
        while (iterator.hasNext()) {
            RDFRuleExecution execution = iterator.next();
            if (execution != null)
                executions.add((RDFRuleExecutionSelect) execution);
        }
        List<RDFPatternSolution> newSolutions = new ArrayList<>(solutions.size());
        for (RDFPatternSolution solution : solutions)
            newSolutions.add(solution);

        // diff the two sets
        for (int i = 0; i != executions.size(); i++) {
            RDFRuleExecutionSelect execution = executions.get(i);
            int matchFound = -1;
            for (int j = 0; j != newSolutions.size(); j++) {
                if (execution.getSolution().equals(newSolutions.get(j))) {
                    matchFound = j;
                    break;
                }
            }
            if (matchFound > 0) {
                executions.set(i, null);
                newSolutions.set(matchFound, null);
            }
        }

        // ok, remaining execution must be invalidated and new solutions triggered
        for (int i = 0; i != executions.size(); i++) {
            RDFRuleExecutionSelect execution = executions.get(i);
            if (execution != null)
                handler.onInvalidate(execution);
        }
        for (int i = 0; i != newSolutions.size(); i++) {
            RDFPatternSolution solution = newSolutions.get(i);
            if (solution != null) {
                RDFRuleExecutionSelect execution = new RDFRuleExecutionSelect(this, solution);
                if (!canFire(execution, handler.getEvaluator()))
                    continue;
                handler.onTrigger(execution);
            }
        }
    }

    @Override
    public Changeset produce(RDFRuleExecution execution, NodeManager nodes, Evaluator evaluator) {
        return produceQuads(execution, nodes, evaluator, consequents);
    }
}
