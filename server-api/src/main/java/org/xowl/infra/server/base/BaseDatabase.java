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

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.server.api.*;
import org.xowl.infra.server.xsp.XSPReply;
import org.xowl.infra.server.xsp.XSPReplyUnsupported;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.sparql.Command;
import org.xowl.infra.utils.TextUtils;

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
    public XSPReply getMetric() {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply getMetricSnapshot() {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply sparql(String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply sparql(Command sparql) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply getEntailmentRegime() {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply setEntailmentRegime(EntailmentRegime regime) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply getPrivileges() {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply grant(XOWLUser user, int privilege) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply grant(String user, int privilege) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply revoke(XOWLUser user, int privilege) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply revoke(String user, int privilege) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply getRules() {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply getRule(String ruleId) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply addRule(String content, boolean activate) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply removeRule(XOWLRule rule) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply removeRule(String ruleId) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply activateRule(XOWLRule rule) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply activateRule(String ruleId) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply deactivateRule(XOWLRule rule) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply deactivateRule(String ruleId) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply getRuleStatus(XOWLRule rule) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply getRuleStatus(String ruleId) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply getStoredProcedures() {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply getStoreProcedure(String iri) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply addStoredProcedure(String iri, String sparql, Collection<String> parameters) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply removeStoredProcedure(XOWLStoredProcedure procedure) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply removeStoredProcedure(String procedureId) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply executeStoredProcedure(XOWLStoredProcedure procedure, XOWLStoredProcedureContext context) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply executeStoredProcedure(String procedureId, XOWLStoredProcedureContext context) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply upload(String syntax, String content) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply upload(Collection<Quad> quads) {
        return XSPReplyUnsupported.instance();
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
