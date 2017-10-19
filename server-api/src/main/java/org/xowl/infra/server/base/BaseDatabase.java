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

package org.xowl.infra.server.base;

import fr.cenotelie.commons.utils.TextUtils;
import fr.cenotelie.commons.utils.api.Reply;
import fr.cenotelie.commons.utils.api.ReplyUnsupported;
import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.server.api.*;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.sparql.Command;

import java.util.Collection;
import java.util.List;

/**
 * Base implementation of a database
 *
 * @author Laurent Wouters
 */
public class BaseDatabase implements XOWLDatabase {
    /**
     * The database's identifier
     */
    protected final String identifier;

    /**
     * Initializes this database
     *
     * @param identifier The database's identifier
     */
    public BaseDatabase(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Initializes this database
     *
     * @param root The database's definition
     */
    public BaseDatabase(ASTNode root) {
        String value = null;
        for (ASTNode child : root.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            if (name.equals("name")) {
                ASTNode nodeValue = child.getChildren().get(1);
                value = TextUtils.unescape(nodeValue.getValue());
                value = value.substring(1, value.length() - 1);
            }
        }
        this.identifier = value;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return identifier;
    }

    @Override
    public Reply getMetric() {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply getMetricSnapshot() {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply sparql(String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply sparql(Command sparql) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply getEntailmentRegime() {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply setEntailmentRegime(EntailmentRegime regime) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply getPrivileges() {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply grant(XOWLUser user, int privilege) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply grant(String user, int privilege) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply revoke(XOWLUser user, int privilege) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply revoke(String user, int privilege) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply getRules() {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply getRule(String ruleId) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply addRule(String content, boolean activate) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply removeRule(XOWLRule rule) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply removeRule(String ruleId) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply activateRule(XOWLRule rule) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply activateRule(String ruleId) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply deactivateRule(XOWLRule rule) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply deactivateRule(String ruleId) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply getRuleStatus(XOWLRule rule) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply getRuleStatus(String ruleId) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply getStoredProcedures() {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply getStoreProcedure(String iri) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply addStoredProcedure(String iri, String sparql, Collection<String> parameters) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply removeStoredProcedure(XOWLStoredProcedure procedure) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply removeStoredProcedure(String procedureId) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply executeStoredProcedure(XOWLStoredProcedure procedure, XOWLStoredProcedureContext context) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply executeStoredProcedure(String procedureId, XOWLStoredProcedureContext context) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply upload(String syntax, String content) {
        return ReplyUnsupported.instance();
    }

    @Override
    public Reply upload(Collection<Quad> quads) {
        return ReplyUnsupported.instance();
    }

    @Override
    public String serializedString() {
        return getIdentifier();
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" +
                TextUtils.escapeStringJSON(XOWLDatabase.class.getCanonicalName()) +
                "\", \"identifier\": \"" +
                TextUtils.escapeStringJSON(getIdentifier()) +
                "\", \"name\": \"" +
                TextUtils.escapeStringJSON(getName()) +
                "\"}";
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof XOWLDatabase && identifier.equals(((XOWLDatabase) obj).getIdentifier());
    }
}
