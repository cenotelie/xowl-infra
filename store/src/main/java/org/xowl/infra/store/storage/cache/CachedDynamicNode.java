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

package org.xowl.infra.store.storage.cache;

import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.rdf.DynamicNode;

/**
 * Cached implementation of a dynamic node
 *
 * @author Laurent Wouters
 */
class CachedDynamicNode extends DynamicNode {
    /**
     * The represented evaluable expression
     */
    private final EvaluableExpression evaluable;

    /**
     * Initializes this node
     *
     * @param evaluable The represented evaluable expression
     */
    public CachedDynamicNode(EvaluableExpression evaluable) {
        this.evaluable = evaluable;
    }

    @Override
    public EvaluableExpression getEvaluable() {
        return evaluable;
    }
}
