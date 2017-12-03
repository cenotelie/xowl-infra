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

import fr.cenotelie.commons.utils.Identifiable;
import fr.cenotelie.commons.utils.Serializable;
import fr.cenotelie.commons.utils.api.Reply;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.sparql.Command;

import java.util.Collection;
import java.util.List;

/**
 * Represents a database hosted on this server
 *
 * @author Laurent Wouters
 */
public interface XOWLDatabase extends Identifiable, Serializable {
    /**
     * Executes a SPARQL command
     *
     * @param sparql      The SPARQL command(s)
     * @param defaultIRIs The context's default IRIs
     * @param namedIRIs   The context's named IRIs
     * @return The protocol reply
     */
    Reply sparql(String sparql, List<String> defaultIRIs, List<String> namedIRIs);

    /**
     * Executes a SPARQL command
     *
     * @param sparql The SPARQL command(s)
     * @return The protocol reply
     */
    Reply sparql(Command sparql);

    /**
     * Gets the entailment regime
     *
     * @return The protocol reply
     */
    Reply getEntailmentRegime();

    /**
     * Sets the entailment regime
     *
     * @param regime The entailment regime
     * @return The protocol reply
     */
    Reply setEntailmentRegime(EntailmentRegime regime);

    /**
     * Gets the privileges assigned to users on a database
     *
     * @return The protocol reply
     */
    Reply getPrivileges();

    /**
     * Grants a privilege to a user on a database
     *
     * @param user      The target user
     * @param privilege The privilege to grant
     * @return The protocol reply
     */
    Reply grant(XOWLUser user, int privilege);

    /**
     * Grants a privilege to a user on a database
     *
     * @param user      The target user
     * @param privilege The privilege to grant
     * @return The protocol reply
     */
    Reply grant(String user, int privilege);

    /**
     * Revokes a privilege from a user on a database
     *
     * @param user      The target user
     * @param privilege The privilege to revoke
     * @return The protocol reply
     */
    Reply revoke(XOWLUser user, int privilege);

    /**
     * Revokes a privilege from a user on a database
     *
     * @param user      The target user
     * @param privilege The privilege to revoke
     * @return The protocol reply
     */
    Reply revoke(String user, int privilege);

    /**
     * Gets the rules in this database
     *
     * @return The protocol reply
     */
    Reply getRules();

    /**
     * Gets the rule for the specified identifier
     *
     * @param ruleId The identifier (IRI) of a rule
     * @return The protocol reply
     */
    Reply getRule(String ruleId);

    /**
     * Adds a new rule to this database
     *
     * @param content  The rule's content
     * @param activate Whether to readily activate the rule
     * @return The protocol reply
     */
    Reply addRule(String content, boolean activate);

    /**
     * Removes a rule from this database
     *
     * @param rule The rule to remove
     * @return The protocol reply
     */
    Reply removeRule(XOWLRule rule);

    /**
     * Removes a rule from this database
     *
     * @param ruleId The identifier of the rule to remove
     * @return The protocol reply
     */
    Reply removeRule(String ruleId);

    /**
     * Activates an existing rule in this database
     *
     * @param rule The rule to activate
     * @return The protocol reply
     */
    Reply activateRule(XOWLRule rule);

    /**
     * Activates an existing rule in this database
     *
     * @param ruleId The identifier of the rule to activate
     * @return The protocol reply
     */
    Reply activateRule(String ruleId);

    /**
     * Deactivates an existing rule in this database
     *
     * @param rule The rule to deactivate
     * @return The protocol reply
     */
    Reply deactivateRule(XOWLRule rule);

    /**
     * Deactivates an existing rule in this database
     *
     * @param ruleId The identifier of the rule to deactivate
     * @return The protocol reply
     */
    Reply deactivateRule(String ruleId);

    /**
     * Gets the matching status of a rule in this database
     *
     * @param rule The rule to inquire
     * @return The protocol reply
     */
    Reply getRuleStatus(XOWLRule rule);

    /**
     * Gets the matching status of a rule in this database
     *
     * @param ruleId The identifier of the rule to inquire
     * @return The protocol reply
     */
    Reply getRuleStatus(String ruleId);

    /**
     * Gets the stored procedures for this database
     *
     * @return The protocol reply
     */
    Reply getStoredProcedures();

    /**
     * Gets the stored procedure for the specified name (iri)
     *
     * @param iri The identifier (iri) of a stored procedure
     * @return The protocol reply
     */
    Reply getStoreProcedure(String iri);

    /**
     * Adds a stored procedure in the form of a SPARQL command
     *
     * @param iri        The identifier (iri) for this procedure
     * @param sparql     The SPARQL command(s)
     * @param parameters The names of the parameters for this procedure
     * @return The protocol reply
     */
    Reply addStoredProcedure(String iri, String sparql, Collection<String> parameters);

    /**
     * Remove a stored procedure
     *
     * @param procedure The procedure to remove
     * @return The protocol reply
     */
    Reply removeStoredProcedure(XOWLStoredProcedure procedure);

    /**
     * Remove a stored procedure
     *
     * @param procedureId The identifier procedure to remove
     * @return The protocol reply
     */
    Reply removeStoredProcedure(String procedureId);

    /**
     * Executes a stored procedure
     *
     * @param procedure The procedure to execute
     * @param context   The execution context to use
     * @return The protocol reply
     */
    Reply executeStoredProcedure(XOWLStoredProcedure procedure, XOWLStoredProcedureContext context);

    /**
     * Executes a stored procedure
     *
     * @param procedureId The identifier of the procedure to execute
     * @param context     The execution context to use
     * @return The protocol reply
     */
    Reply executeStoredProcedure(String procedureId, XOWLStoredProcedureContext context);

    /**
     * Uploads some content to this database
     *
     * @param syntax  The content's syntax
     * @param content The content
     * @return The protocol reply
     */
    Reply upload(String syntax, String content);

    /**
     * Uploads quads to this database
     *
     * @param quads The quads to upload
     * @return The protocol reply
     */
    Reply upload(Collection<Quad> quads);
}
