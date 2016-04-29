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

package org.xowl.infra.store.writers;

import org.xowl.infra.lang.owl2.Axiom;
import org.xowl.infra.utils.logging.Logger;

import java.util.Iterator;

/**
 * Represents a writer of OWL data
 *
 * @author Laurent Wouters
 */
public interface OWLSerializer {
    /**
     * Serializes the specified quads
     *
     * @param logger The logger to use
     * @param axioms The axioms to serialize
     */
    void serialize(Logger logger, Iterator<Axiom> axioms);
}
