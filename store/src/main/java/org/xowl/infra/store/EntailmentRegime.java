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

package org.xowl.infra.store;

/**
 * Represents an entailment regime
 *
 * @author Laurent Wouters
 */
public enum EntailmentRegime {
    /**
     * Entailment regime: No entailment
     */
    none,
    /**
     * Entailment regime: Simple interpretation
     */
    simple,
    /**
     * Entailment regime: RDF interpretation
     */
    RDF,
    /**
     * Entailment regime: RDFS interpretation
     */
    RDFS,
    /**
     * Entailment regime: OWL2-RDF based interpretation
     */
    OWL2_RDF,
    /**
     * Entailment regime: OWL2 Direct interpretation
     */
    OWL2_DIRECT,
}
