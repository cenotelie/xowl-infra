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

package org.xowl.infra.lang.owl2;

import java.util.*;

/**
 * Represents the base interface for the OWL class AnnotationPropertyDomain
 *
 * @author xOWL code generator
 */
public interface AnnotationPropertyDomain extends org.xowl.infra.lang.owl2.AnnotationAxiom, org.xowl.infra.lang.owl2.Annotation_OR_AnnotationAssertion_OR_AnnotationPropertyDomain_OR_AnnotationPropertyRange_OR_SubAnno1 {
    /**
     * Sets the value for the property AnnotDomain
     *
     * @param elem The value to set
     */
    void setAnnotDomain(org.xowl.infra.lang.owl2.IRI elem);

    /**
     * Gets the value for the property AnnotDomain
     *
     * @return The value for the property AnnotDomain
     */
    org.xowl.infra.lang.owl2.IRI getAnnotDomain();

}
