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

package org.xowl.infra.engine;

import org.xowl.infra.store.Evaluator;
import org.xowl.infra.store.EvaluatorProvider;
import org.xowl.infra.store.Repository;

/**
 * Implements a provider of evaluators for xOWL dynamic expressions expressed in Clojure
 *
 * @author Laurent Wouters
 */
public class ClojureEvaluatorProvider implements EvaluatorProvider {
    @Override
    public Evaluator newEvaluator(Repository repository) {
        return new ClojureEvaluator(repository);
    }
}
