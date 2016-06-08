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

package org.xowl.infra.server.api;

import org.xowl.infra.server.xsp.XSPReply;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.Serializable;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.sparql.Command;

import java.util.Collection;
import java.util.List;

/**
 * Represents a database hosted on this server
 *
 * @author Laurent Wouters
 */
public interface XOWLDatabase extends Serializable {
    /**
     * Gets the name of this database
     *
     * @return The name of this database
     */
    String getName();

    /**
     * Executes a SPARQL command
     *
     * @param sparql      The SPARQL command(s)
     * @param defaultIRIs The context's default IRIs
     * @param namedIRIs   The context's named IRIs
     * @return The protocol reply
     */
    XSPReply sparql(String sparql, List<String> defaultIRIs, List<String> namedIRIs);

    /**
     * Executes a SPARQL command
     *
     * @param sparql The SPARQL command(s)
     * @return The protocol reply
     */
    XSPReply sparql(Command sparql);

    /**
     * Gets the entailment regime
     *
     * @return The protocol reply
     */
    XSPReply getEntailmentRegime();

    /**
     * Sets the entailment regime
     *
     * @param regime The entailment regime
     * @return The protocol reply
     */
    XSPReply setEntailmentRegime(EntailmentRegime regime);

    /**
     * Gets the rule for the specified name
     *
     * @param name The name (IRI) of a rule
     * @return The protocol reply
     */
    XSPReply getRule(String name);

    /**
     * Gets the rules in this database
     *
     * @return The protocol reply
     */
    XSPReply getRules();

    /**
     * Adds a new rule to this database
     *
     * @param content  The rule's content
     * @param activate Whether to readily activate the rule
     * @return The protocol reply
     */
    XSPReply addRule(String content, boolean activate);

    /**
     * Removes a rule from this database
     *
     * @param rule The rule to remove
     * @return The protocol reply
     */
    XSPReply removeRule(XOWLRule rule);

    /**
     * Activates an existing rule in this database
     *
     * @param rule The rule to activate
     * @return The protocol reply
     */
    XSPReply activateRule(XOWLRule rule);

    /**
     * Deactivates an existing rule in this database
     *
     * @param rule The rule to deactivate
     * @return The protocol reply
     */
    XSPReply deactivateRule(XOWLRule rule);

    /**
     * Gets the matching status of a rule in this database
     *
     * @param rule The rule to inquire
     * @return The protocol reply
     */
    XSPReply getRuleStatus(XOWLRule rule);

    /**
     * Uploads some content to this database
     *
     * @param syntax  The content's syntax
     * @param content The content
     * @return The protocol reply
     */
    XSPReply upload(String syntax, String content);

    /**
     * Uploads quads to this database
     *
     * @param quads The quads to upload
     * @return The protocol reply
     */
    XSPReply upload(Collection<Quad> quads);
}
