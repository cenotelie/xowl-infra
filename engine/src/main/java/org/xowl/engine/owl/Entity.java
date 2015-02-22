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
package org.xowl.engine.owl;

import org.xowl.engine.Repository;
import org.xowl.store.rdf.IRINode;

/**
 * Represents a OWL2 entity in an ontology
 *
 * @author Laurent Wouters
 */
public class Entity {
    /**
     * The parent repository
     */
    private Repository repository;
    /**
     * The represented IRI
     */
    private IRINode node;

    /**
     * Initializes this entity
     * @param repository The parent repository
     * @param node The represented IRI
     */
    public Entity(Repository repository, IRINode node) {
        this.repository = repository;
        this.node = node;
    }
}
