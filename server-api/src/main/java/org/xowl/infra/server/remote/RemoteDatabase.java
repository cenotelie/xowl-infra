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

package org.xowl.infra.server.remote;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.server.api.XOWLRule;
import org.xowl.infra.server.api.XOWLStoredProcedure;
import org.xowl.infra.server.api.XOWLStoredProcedureContext;
import org.xowl.infra.server.api.XOWLUser;
import org.xowl.infra.server.base.BaseDatabase;
import org.xowl.infra.server.xsp.XSPReply;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.rdf.Quad;

import java.util.Collection;
import java.util.List;

/**
 * Represents a database on a remote xOWL Server
 *
 * @author Laurent Wouters
 */
class RemoteDatabase extends BaseDatabase {
    /**
     * The parent server
     */
    private final RemoteServer server;

    /**
     * Initializes this database
     *
     * @param server The parent server
     * @param name   The database's name
     */
    public RemoteDatabase(RemoteServer server, String name) {
        super(name);
        this.server = server;
    }

    /**
     * Initializes this database
     *
     * @param server The parent server
     * @param root   The database's definition
     */
    public RemoteDatabase(RemoteServer server, ASTNode root) {
        super(root);
        this.server = server;
    }

    @Override
    public XSPReply getMetric() {
        return server.dbGetMetric(identifier);
    }

    @Override
    public XSPReply getMetricSnapshot() {
        return server.dbGetMetricSnapshot(identifier);
    }

    @Override
    public XSPReply sparql(String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        return server.dbSPARQL(identifier, sparql, defaultIRIs, namedIRIs);
    }

    @Override
    public XSPReply getEntailmentRegime() {
        return server.dbGetEntailmentRegime(identifier);
    }

    @Override
    public XSPReply setEntailmentRegime(EntailmentRegime regime) {
        return server.dbSetEntailmentRegime(identifier, regime);
    }

    @Override
    public XSPReply getPrivileges() {
        return server.dbGetPrivileges(identifier);
    }

    @Override
    public XSPReply grant(XOWLUser user, int privilege) {
        return server.dbGrant(identifier, user.getIdentifier(), privilege);
    }

    @Override
    public XSPReply grant(String user, int privilege) {
        return server.dbGrant(identifier, user, privilege);
    }

    @Override
    public XSPReply revoke(XOWLUser user, int privilege) {
        return server.dbRevoke(identifier, user.getIdentifier(), privilege);
    }

    @Override
    public XSPReply revoke(String user, int privilege) {
        return server.dbRevoke(identifier, user, privilege);
    }

    @Override
    public XSPReply getRules() {
        return server.dbGetRules(identifier);
    }

    @Override
    public XSPReply getRule(String ruleId) {
        return server.dbGetRule(this.identifier, ruleId);
    }

    @Override
    public XSPReply addRule(String content, boolean activate) {
        return server.dbAddRule(identifier, content, activate);
    }

    @Override
    public XSPReply removeRule(XOWLRule rule) {
        return server.dbRemoveRule(identifier, rule.getIdentifier());
    }

    @Override
    public XSPReply removeRule(String ruleId) {
        return server.dbRemoveRule(identifier, ruleId);
    }

    @Override
    public XSPReply activateRule(XOWLRule rule) {
        return server.dbActivateRule(identifier, rule.getIdentifier());
    }

    @Override
    public XSPReply activateRule(String ruleId) {
        return server.dbActivateRule(identifier, ruleId);
    }

    @Override
    public XSPReply deactivateRule(XOWLRule rule) {
        return server.dbDeactivateRule(identifier, rule.getIdentifier());
    }

    @Override
    public XSPReply deactivateRule(String ruleId) {
        return server.dbDeactivateRule(identifier, ruleId);
    }

    @Override
    public XSPReply getRuleStatus(XOWLRule rule) {
        return server.dbGetRuleStatus(identifier, rule.getIdentifier());
    }

    @Override
    public XSPReply getRuleStatus(String ruleId) {
        return server.dbGetRuleStatus(identifier, ruleId);
    }

    @Override
    public XSPReply getStoredProcedures() {
        return server.dbGetStoredProcedures(identifier);
    }

    @Override
    public XSPReply getStoreProcedure(String iri) {
        return server.dbGetStoreProcedure(identifier, iri);
    }

    @Override
    public XSPReply addStoredProcedure(String iri, String sparql, Collection<String> parameters) {
        return server.dbAddStoredProcedure(identifier, iri, sparql, parameters);
    }

    @Override
    public XSPReply removeStoredProcedure(XOWLStoredProcedure procedure) {
        return server.dbRemoveRule(identifier, procedure.getIdentifier());
    }

    @Override
    public XSPReply removeStoredProcedure(String procedureId) {
        return server.dbRemoveStoredProcedure(identifier, procedureId);
    }

    @Override
    public XSPReply executeStoredProcedure(XOWLStoredProcedure procedure, XOWLStoredProcedureContext context) {
        return server.dbExecuteStoredProcedure(identifier, procedure.getIdentifier(), context);
    }

    @Override
    public XSPReply executeStoredProcedure(String procedureId, XOWLStoredProcedureContext context) {
        return server.dbExecuteStoredProcedure(identifier, procedureId, context);
    }

    @Override
    public XSPReply upload(String syntax, String content) {
        return server.dbUpload(identifier, syntax, content);
    }

    @Override
    public XSPReply upload(Collection<Quad> quads) {
        return server.dbUpload(identifier, quads);
    }
}
