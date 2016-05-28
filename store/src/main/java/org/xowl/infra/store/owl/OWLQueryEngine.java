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
package org.xowl.infra.store.owl;

import org.xowl.infra.lang.owl2.Axiom;
import org.xowl.infra.store.Evaluator;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.BaseStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a query engine for a OWL store
 *
 * @author Laurent Wouters
 */
public class OWLQueryEngine {
    /**
     * The xOWL store
     */
    private final BaseStore store;
    /**
     * The current evaluator
     */
    private final Evaluator evaluator;
    /**
     * The underlying RDF engine
     */
    private final RDFQueryEngine rdfEngine;
    /**
     * The graph for the translated quads
     */
    private final GraphNode graph;

    /**
     * Gets the RDF backend
     *
     * @return The RDF backend
     */
    public RDFQueryEngine getBackend() {
        return rdfEngine;
    }

    /**
     * Initializes this engine
     *
     * @param store     The OWL store to query
     * @param evaluator The current evaluator
     */
    public OWLQueryEngine(BaseStore store, Evaluator evaluator) {
        this.store = store;
        this.evaluator = evaluator;
        this.rdfEngine = new RDFQueryEngine(store);
        this.graph = new VariableNode("__graph__");
    }

    /**
     * Executes the specified query and gets the solutions
     *
     * @param query A query
     * @return The solutions
     * @throws TranslationException When a runtime entity is not named
     */
    public Collection<Bindings> execute(OWLQuery query) throws TranslationException {
        TranslationContext context = new TranslationContext();
        RDFQuery rdfQuery = translate(query, context);
        Collection<RDFPatternSolution> rdfSolutions = rdfEngine.execute(rdfQuery);
        List<Bindings> owlSolutions = new ArrayList<>();
        for (RDFPatternSolution rdfSolution : rdfSolutions)
            owlSolutions.add(translate(rdfSolution, context));
        return owlSolutions;
    }

    /**
     * Translates the specified query to a RDF query
     *
     * @param query   A OWL query
     * @param context The translation context
     * @return The corresponding RDF query
     * @throws TranslationException When a runtime entity is not named
     */
    private RDFQuery translate(OWLQuery query, TranslationContext context) throws TranslationException {
        RDFQuery result = new RDFQuery();
        Translator translator = new Translator(context, store, evaluator);
        // translate the positive axioms
        result.getPositives().addAll(translator.translate(query.getPositives(), graph));
        // translate the negative conjunctions
        for (Collection<Axiom> conjunction : query.getNegatives()) {
            result.getNegatives().add(translator.translate(conjunction, graph));
        }
        return result;
    }

    /**
     * Translate a RDF solution to an OWL solution
     *
     * @param solution A RDF solution
     * @param context  The translation context
     * @return The corresponding OWL solution
     */
    private Bindings translate(RDFPatternSolution solution, TranslationContext context) {
        Bindings bindings = new Bindings();
        for (VariableNode var : solution.getVariables()) {
            Object value = RDFUtils.getOWL(solution.get(var));
            bindings.bind(context.get(var), value);
        }
        return bindings;
    }
}
