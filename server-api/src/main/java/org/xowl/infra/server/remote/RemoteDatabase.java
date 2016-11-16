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
import org.xowl.infra.server.base.BaseDatabase;
import org.xowl.infra.server.xsp.XSPReply;
import org.xowl.infra.server.xsp.XSPReplyUnsupported;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.sparql.Command;

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
    public XSPReply sparql(String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        return server.sparql(name, sparql, defaultIRIs, namedIRIs);
    }

    @Override
    public XSPReply sparql(Command sparql) {
        return XSPReplyUnsupported.instance();
    }

    @Override
    public XSPReply getEntailmentRegime() {
        return server.getEntailmentRegime(name);
    }

    @Override
    public XSPReply setEntailmentRegime(EntailmentRegime regime) {
        return server.setEntailmentRegime(name, regime);
    }

    @Override
    public XSPReply getRule(String name) {
        return server.getRule(this.name, name);
    }

    @Override
    public XSPReply getRules() {
        return server.getRules(name);
    }

    @Override
    public XSPReply addRule(String content, boolean activate) {
        return server.addRule(name, content, activate);
    }

    @Override
    public XSPReply removeRule(XOWLRule rule) {
        return server.removeRule(name, rule);
    }

    @Override
    public XSPReply activateRule(XOWLRule rule) {
        return server.activateRule(name, rule);
    }

    @Override
    public XSPReply deactivateRule(XOWLRule rule) {
        return server.deactivateRule(name, rule);
    }

    @Override
    public XSPReply getRuleStatus(XOWLRule rule) {
        return server.getRuleStatus(name, rule);
    }

    @Override
    public XSPReply getStoreProcedure(String iri) {
        return server.getStoreProcedure(name, iri);
    }

    @Override
    public XSPReply getStoredProcedures() {
        return server.getStoredProcedures(name);
    }

    @Override
    public XSPReply addStoredProcedure(String iri, String sparql, Collection<String> parameters) {
        return server.addStoredProcedure(name, iri, sparql, parameters);
    }

    @Override
    public XSPReply removeStoredProcedure(XOWLStoredProcedure procedure) {
        return server.removeStoredProcedure(name, procedure);
    }

    @Override
    public XSPReply executeStoredProcedure(XOWLStoredProcedure procedure, XOWLStoredProcedureContext context) {
        return server.executeStoredProcedure(name, procedure, context);
    }

    @Override
    public XSPReply upload(String syntax, String content) {
        return server.upload(name, syntax, content);
    }

    @Override
    public XSPReply upload(Collection<Quad> quads) {
        return server.upload(name, quads);
    }

    @Override
    public XSPReply getMetric() {
        return server.getMetric(name);
    }

    @Override
    public XSPReply getMetricSnapshot() {
        return server.getMetricSnapshot(name);
    }
}
