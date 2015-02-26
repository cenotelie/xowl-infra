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
package org.xowl.store.owl;

import org.xowl.lang.actions.QueryVariable;
import org.xowl.lang.owl2.Axiom;
import org.xowl.store.loaders.Utils;
import org.xowl.store.rdf.VariableNode;

import java.util.*;

/**
 * Represents a query engine for a OWL store
 *
 * @author Laurent Wouters
 */
public class QueryEngine {
    /**
     * The xOWL store
     */
    private XOWLStore store;
    /**
     * The current evaluator
     */
    private Evaluator evaluator;
    /**
     * The underlying RDF engine
     */
    private org.xowl.store.rdf.QueryEngine rdfEngine;
    /**
     * The IRI of the graph for the translated quads
     */
    private String graphIRI;

    /**
     * Initializes this engine
     *
     * @param store     The OWL store to query
     * @param evaluator The current evaluator
     */
    public QueryEngine(XOWLStore store, Evaluator evaluator) {
        this.store = store;
        this.evaluator = evaluator;
        this.rdfEngine = new org.xowl.store.rdf.QueryEngine(store);
        this.graphIRI = Utils.createAnonymousGraph();
    }

    /**
     * Executes the specified query and gets the solutions
     *
     * @param query A query
     * @return The solutions
     */
    public Collection<QuerySolution> execute(Query query) {
        TranslationContext context = new TranslationContext();
        try {
            org.xowl.store.rdf.Query rdfQuery = translate(query, context);
            Collection<org.xowl.store.rdf.QuerySolution> rdfSolutions = rdfEngine.execute(rdfQuery);
            List<QuerySolution> owlSolutions = new ArrayList<>();
            for (org.xowl.store.rdf.QuerySolution rdfSolution : rdfSolutions)
                owlSolutions.add(translate(rdfSolution, context));
            return owlSolutions;
        } catch (TranslationException ex) {
            // TODO: log this somehow
            return new ArrayList<>(0);
        }
    }

    /**
     * Translates the specified query to a RDF query
     *
     * @param query   A OWL query
     * @param context The translation context
     * @return The corresponding RDF query
     */
    private org.xowl.store.rdf.Query translate(Query query, TranslationContext context) throws TranslationException {
        org.xowl.store.rdf.Query result = new org.xowl.store.rdf.Query();
        Translator translator = new Translator(context, store, evaluator);
        // translate the positive axioms
        result.getPositives().addAll(translator.translate(query.getPositives(), graphIRI));
        // translate the negative conjunctions
        for (Collection<Axiom> conjunction : query.getNegatives()) {
            result.getNegatives().add(translator.translate(conjunction, graphIRI));
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
    private QuerySolution translate(org.xowl.store.rdf.QuerySolution solution, TranslationContext context) {
        Map<QueryVariable, Object> map = new HashMap<>();
        for (VariableNode var : solution.getVariables()) {
            Object value = store.getOWL(solution.get(var));
            if (value != null)
                map.put(context.get(var), value);
        }
        return new QuerySolution(map);
    }
}
