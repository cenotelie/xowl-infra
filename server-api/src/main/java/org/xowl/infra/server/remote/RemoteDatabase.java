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
        return server.dbGetMetric(name);
    }

    @Override
    public XSPReply getMetricSnapshot() {
        return server.dbGetMetricSnapshot(name);
    }

    @Override
    public XSPReply sparql(String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        return server.dbSPARQL(name, sparql, defaultIRIs, namedIRIs);
    }

    @Override
    public XSPReply getEntailmentRegime() {
        return server.dbGetEntailmentRegime(name);
    }

    @Override
    public XSPReply setEntailmentRegime(EntailmentRegime regime) {
        return server.dbSetEntailmentRegime(name, regime);
    }

    @Override
    public XSPReply getPrivileges() {
        return server.dbGetPrivileges(name);
    }

    @Override
    public XSPReply grant(XOWLUser user, int privilege) {
        return server.dbGrant(name, user.getName(), privilege);
    }

    @Override
    public XSPReply grant(String user, int privilege) {
        return server.dbGrant(name, user, privilege);
    }

    @Override
    public XSPReply revoke(XOWLUser user, int privilege) {
        return server.dbRevoke(name, user.getName(), privilege);
    }

    @Override
    public XSPReply revoke(String user, int privilege) {
        return server.dbRevoke(name, user, privilege);
    }

    @Override
    public XSPReply getRules() {
        return server.dbGetRules(name);
    }

    @Override
    public XSPReply getRule(String name) {
        return server.dbGetRule(this.name, name);
    }

    @Override
    public XSPReply addRule(String content, boolean activate) {
        return server.dbAddRule(name, content, activate);
    }

    @Override
    public XSPReply removeRule(XOWLRule rule) {
        return server.dbRemoveRule(name, rule.getName());
    }

    @Override
    public XSPReply removeRule(String rule) {
        return server.dbRemoveRule(name, rule);
    }

    @Override
    public XSPReply activateRule(XOWLRule rule) {
        return server.dbActivateRule(name, rule.getName());
    }

    @Override
    public XSPReply activateRule(String rule) {
        return server.dbActivateRule(name, rule);
    }

    @Override
    public XSPReply deactivateRule(XOWLRule rule) {
        return server.dbDeactivateRule(name, rule.getName());
    }

    @Override
    public XSPReply deactivateRule(String rule) {
        return server.dbDeactivateRule(name, rule);
    }

    @Override
    public XSPReply getRuleStatus(XOWLRule rule) {
        return server.dbGetRuleStatus(name, rule.getName());
    }

    @Override
    public XSPReply getRuleStatus(String rule) {
        return server.dbGetRuleStatus(name, rule);
    }

    @Override
    public XSPReply getStoredProcedures() {
        return server.dbGetStoredProcedures(name);
    }

    @Override
    public XSPReply getStoreProcedure(String iri) {
        return server.dbGetStoreProcedure(name, iri);
    }

    @Override
    public XSPReply addStoredProcedure(String iri, String sparql, Collection<String> parameters) {
        return server.dbAddStoredProcedure(name, iri, sparql, parameters);
    }

    @Override
    public XSPReply removeStoredProcedure(XOWLStoredProcedure procedure) {
        return server.dbRemoveRule(name, procedure.getName());
    }

    @Override
    public XSPReply removeStoredProcedure(String procedure) {
        return server.dbRemoveStoredProcedure(name, procedure);
    }

    @Override
    public XSPReply executeStoredProcedure(XOWLStoredProcedure procedure, XOWLStoredProcedureContext context) {
        return server.dbExecuteStoredProcedure(name, procedure.getName(), context);
    }

    @Override
    public XSPReply executeStoredProcedure(String procedure, XOWLStoredProcedureContext context) {
        return server.dbExecuteStoredProcedure(name, procedure, context);
    }

    @Override
    public XSPReply upload(String syntax, String content) {
        return server.dbUpload(name, syntax, content);
    }

    @Override
    public XSPReply upload(Collection<Quad> quads) {
        return server.dbUpload(name, quads);
    }
}
