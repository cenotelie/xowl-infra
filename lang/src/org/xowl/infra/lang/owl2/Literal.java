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
 * Represents the base interface for the OWL class Literal
 *
 * @author xOWL code generator
 */
public interface Literal extends org.xowl.infra.lang.owl2.AnnotationValue, org.xowl.infra.lang.owl2.LiteralExpression {
    /**
     * Sets the value for the property LangTag
     *
     * @param elem The value to set
     */
    void setLangTag(String elem);

    /**
     * Gets the value for the property LangTag
     *
     * @return The value for the property LangTag
     */
    String getLangTag();

    /**
     * Sets the value for the property LexicalValue
     *
     * @param elem The value to set
     */
    void setLexicalValue(String elem);

    /**
     * Gets the value for the property LexicalValue
     *
     * @return The value for the property LexicalValue
     */
    String getLexicalValue();

    /**
     * Sets the value for the property MemberOf
     *
     * @param elem The value to set
     */
    void setMemberOf(org.xowl.infra.lang.owl2.IRI elem);

    /**
     * Gets the value for the property MemberOf
     *
     * @return The value for the property MemberOf
     */
    org.xowl.infra.lang.owl2.IRI getMemberOf();

}
