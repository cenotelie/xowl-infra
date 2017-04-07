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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.execution.Evaluator;
import org.xowl.infra.store.rdf.RDFPattern;
import org.xowl.infra.store.rdf.RDFPatternSolution;
import org.xowl.infra.store.rdf.RDFQuery;
import org.xowl.infra.store.storage.NodeManager;

import java.util.Collection;

/**
 * An evaluation context based on a repository
 *
 * @author Laurent Wouters
 */
public class EvalContextRepository implements EvalContext {
    /**
     * The repository
     */
    private final RepositoryRDF repository;

    /**
     * Initializes this context
     *
     * @param repository The repository
     */
    public EvalContextRepository(RepositoryRDF repository) {
        this.repository = repository;
    }

    @Override
    public Evaluator getEvaluator() {
        return repository.getExecutionManager();
    }

    @Override
    public NodeManager getNodes() {
        return repository.getStore();
    }

    @Override
    public Solutions getSolutions(RDFPattern pattern) {
        Collection<RDFPatternSolution> results = repository.getRDFQueryEngine().execute(new RDFQuery(pattern));
        return new SolutionsMultiset(results);
    }
}
