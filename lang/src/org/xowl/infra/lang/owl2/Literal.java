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
 * Represents the base interface for Literal
 * Original OWL class is http://xowl.org/infra/lang/owl2#Literal
 *
 * @author xOWL code generator
 */
public interface Literal extends AnnotationValue, LiteralExpression {
    /**
     * Sets the value for the property LangTag
     * Original OWL property is http://xowl.org/infra/lang/owl2#langTag
     *
     * @param elem The value to set
     */
    void setLangTag(String elem);

    /**
     * Gets the value for the property LangTag
     * Original OWL property is http://xowl.org/infra/lang/owl2#langTag
     *
     * @return The value for the property LangTag
     */
    String getLangTag();

    /**
     * Sets the value for the property LexicalValue
     * Original OWL property is http://xowl.org/infra/lang/owl2#lexicalValue
     *
     * @param elem The value to set
     */
    void setLexicalValue(String elem);

    /**
     * Gets the value for the property LexicalValue
     * Original OWL property is http://xowl.org/infra/lang/owl2#lexicalValue
     *
     * @return The value for the property LexicalValue
     */
    String getLexicalValue();

    /**
     * Sets the value for the property MemberOf
     * Original OWL property is http://xowl.org/infra/lang/owl2#memberOf
     *
     * @param elem The value to set
     */
    void setMemberOf(IRI elem);

    /**
     * Gets the value for the property MemberOf
     * Original OWL property is http://xowl.org/infra/lang/owl2#memberOf
     *
     * @return The value for the property MemberOf
     */
    IRI getMemberOf();

}
