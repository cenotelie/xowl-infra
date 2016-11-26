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

package org.xowl.infra.server.embedded;

import org.xowl.infra.server.api.XOWLRule;
import org.xowl.infra.server.api.XOWLStoredProcedure;
import org.xowl.infra.server.api.XOWLStoredProcedureContext;
import org.xowl.infra.server.api.XOWLUser;
import org.xowl.infra.server.impl.ControllerDatabase;
import org.xowl.infra.server.impl.ControllerServer;
import org.xowl.infra.server.impl.DatabaseImpl;
import org.xowl.infra.server.impl.UserImpl;
import org.xowl.infra.server.xsp.*;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.RDFRuleStatus;
import org.xowl.infra.store.sparql.Command;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.sparql.ResultFailure;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Represents a database when embedded within another application
 * A direct access to this database bypasses the access control usually handled by the parent server controller.
 *
 * @author Laurent Wouters
 */
abstract class EmbeddedDatabase extends DatabaseImpl {
    /**
     * The logger for this database
     */
    private final Logger logger;

    /**
     * Initializes this structure
     *
     * @param logger           The logger for this database
     * @param serverController The parent server controller
     * @param dbController     The associated database controller
     */
    public EmbeddedDatabase(Logger logger, ControllerServer serverController, ControllerDatabase dbController) {
        super(serverController, dbController);
        this.logger = logger;
    }

    /**
     * Gets the administrator user
     *
     * @return The administrator user
     */
    protected abstract UserImpl getAdminUser();

    @Override
    public XSPReply getMetric() {
        return new XSPReplyResult<>(dbController.getMetric());
    }

    @Override
    public XSPReply getMetricSnapshot() {
        return new XSPReplyResult<>(dbController.getMetricSnapshot());
    }

    @Override
    public XSPReply sparql(String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        Result result = dbController.sparql(sparql, defaultIRIs, namedIRIs, false);
        if (result.isFailure())
            return new XSPReplyFailure(((ResultFailure) result).getMessage());
        return new XSPReplyResult<>(result);
    }

    @Override
    public XSPReply sparql(Command sparql) {
        Result result = dbController.sparql(sparql, false);
        if (result.isFailure())
            return new XSPReplyFailure(((ResultFailure) result).getMessage());
        return new XSPReplyResult<>(result);
    }

    @Override
    public XSPReply getEntailmentRegime() {
        return new XSPReplyResult<>(dbController.getEntailmentRegime());
    }

    @Override
    public XSPReply setEntailmentRegime(EntailmentRegime regime) {
        try {
            dbController.setEntailmentRegime(regime);
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
        return XSPReplySuccess.instance();
    }

    @Override
    public XSPReply getPrivileges() {
        return serverController.getDatabasePrivileges(getAdminUser(), name);
    }

    @Override
    public XSPReply grant(XOWLUser user, int privilege) {
        return grant(user.getName(), privilege);
    }

    @Override
    public XSPReply grant(String user, int privilege) {
        return serverController.grantDatabase(getAdminUser(), user, name, privilege);
    }

    @Override
    public XSPReply revoke(XOWLUser user, int privilege) {
        return revoke(user.getName(), privilege);
    }

    @Override
    public XSPReply revoke(String user, int privilege) {
        return serverController.revokeDatabase(getAdminUser(), user, name, privilege);
    }

    @Override
    public XSPReply getRules() {
        try {
            Collection<XOWLRule> rules = dbController.getRules();
            return new XSPReplyResultCollection<>(rules);
        } catch (IOException exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply getRule(String name) {
        try {
            XOWLRule rule = dbController.getRule(name);
            if (rule == null)
                return XSPReplyNotFound.instance();
            return new XSPReplyResult<>(rule);
        } catch (IOException exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply addRule(String content, boolean activate) {
        try {
            XOWLRule rule = dbController.addRule(content, activate);
            return new XSPReplyResult<>(rule);
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply removeRule(XOWLRule rule) {
        return removeRule(rule.getName());
    }

    @Override
    public XSPReply removeRule(String rule) {
        try {
            dbController.removeRule(rule);
            return XSPReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply activateRule(XOWLRule rule) {
        return activateRule(rule.getName());
    }

    @Override
    public XSPReply activateRule(String rule) {
        try {
            dbController.activateRule(rule);
            return XSPReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply deactivateRule(XOWLRule rule) {
        return deactivateRule(rule.getName());
    }

    @Override
    public XSPReply deactivateRule(String rule) {
        try {
            dbController.deactivateRule(rule);
            return XSPReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply getRuleStatus(XOWLRule rule) {
        return getRuleStatus(rule.getName());
    }

    @Override
    public XSPReply getRuleStatus(String rule) {
        try {
            RDFRuleStatus status = dbController.getRuleStatus(rule);
            if (status == null)
                return new XSPReplyFailure("The rule is not active");
            return new XSPReplyResult<>(status);
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply getStoredProcedures() {
        try {
            Collection<XOWLStoredProcedure> procedures = dbController.getStoredProcedures();
            return new XSPReplyResultCollection<>(procedures);
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply getStoreProcedure(String iri) {
        try {
            XOWLStoredProcedure procedure = dbController.getStoredProcedure(iri);
            if (procedure == null)
                return XSPReplyNotFound.instance();
            return new XSPReplyResult<>(procedure);
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply addStoredProcedure(String iri, String sparql, Collection<String> parameters) {
        try {
            XOWLStoredProcedure procedure = dbController.addStoredProcedure(iri, sparql, parameters);
            return new XSPReplyResult<>(procedure);
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply removeStoredProcedure(XOWLStoredProcedure procedure) {
        return removeStoredProcedure(procedure.getName());
    }

    @Override
    public XSPReply removeStoredProcedure(String procedure) {
        try {
            dbController.removeStoredProcedure(procedure);
            return XSPReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply executeStoredProcedure(XOWLStoredProcedure procedure, XOWLStoredProcedureContext context) {
        return executeStoredProcedure(procedure.getName(), context);
    }

    @Override
    public XSPReply executeStoredProcedure(String procedure, XOWLStoredProcedureContext context) {
        try {
            Result result = dbController.executeStoredProcedure(procedure, context, false);
            if (result.isFailure())
                return new XSPReplyFailure(((ResultFailure) result).getMessage());
            return new XSPReplyResult<>(result);
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply upload(String syntax, String content) {
        try {
            BufferedLogger logger = new BufferedLogger();
            dbController.upload(logger, syntax, content);
            if (!logger.getErrorMessages().isEmpty())
                return new XSPReplyFailure(logger.getErrorsAsString());
            return XSPReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply upload(Collection<Quad> quads) {
        try {
            dbController.upload(quads);
            return XSPReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }
}
