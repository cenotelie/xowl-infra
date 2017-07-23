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

import org.xowl.infra.server.api.*;
import org.xowl.infra.server.impl.ControllerDatabase;
import org.xowl.infra.server.impl.ControllerServer;
import org.xowl.infra.server.impl.DatabaseImpl;
import org.xowl.infra.server.impl.UserImpl;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.RDFRuleStatus;
import org.xowl.infra.store.sparql.Command;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.utils.api.*;
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
    public Reply getMetric() {
        return new ReplyResult<>(dbController.getMetric());
    }

    @Override
    public Reply getMetricSnapshot() {
        return new ReplyResult<>(dbController.getMetricSnapshot());
    }

    @Override
    public Reply sparql(String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        Result result = dbController.sparql(sparql, defaultIRIs, namedIRIs, false);
        return new ReplyResult<>(result);
    }

    @Override
    public Reply sparql(Command sparql) {
        Result result = dbController.sparql(sparql, false);
        return new ReplyResult<>(result);
    }

    @Override
    public Reply getEntailmentRegime() {
        return new ReplyResult<>(dbController.getEntailmentRegime());
    }

    @Override
    public Reply setEntailmentRegime(EntailmentRegime regime) {
        try {
            dbController.setEntailmentRegime(regime);
        } catch (Exception exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
        return ReplySuccess.instance();
    }

    @Override
    public Reply getPrivileges() {
        return serverController.getDatabasePrivileges(getAdminUser(), identifier);
    }

    @Override
    public Reply grant(XOWLUser user, int privilege) {
        return grant(user.getIdentifier(), privilege);
    }

    @Override
    public Reply grant(String user, int privilege) {
        return serverController.grantDatabase(getAdminUser(), user, identifier, privilege);
    }

    @Override
    public Reply revoke(XOWLUser user, int privilege) {
        return revoke(user.getIdentifier(), privilege);
    }

    @Override
    public Reply revoke(String user, int privilege) {
        return serverController.revokeDatabase(getAdminUser(), user, identifier, privilege);
    }

    @Override
    public Reply getRules() {
        try {
            Collection<XOWLRule> rules = dbController.getRules();
            return new ReplyResultCollection<>(rules);
        } catch (IOException exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }

    @Override
    public Reply getRule(String ruleId) {
        try {
            XOWLRule rule = dbController.getRule(ruleId);
            if (rule == null)
                return ReplyNotFound.instance();
            return new ReplyResult<>(rule);
        } catch (IOException exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }

    @Override
    public Reply addRule(String content, boolean activate) {
        try {
            XOWLRule rule = dbController.addRule(content, activate);
            return new ReplyResult<>(rule);
        } catch (Exception exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }

    @Override
    public Reply removeRule(XOWLRule rule) {
        return removeRule(rule.getIdentifier());
    }

    @Override
    public Reply removeRule(String ruleId) {
        try {
            dbController.removeRule(ruleId);
            return ReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }

    @Override
    public Reply activateRule(XOWLRule rule) {
        return activateRule(rule.getIdentifier());
    }

    @Override
    public Reply activateRule(String ruleId) {
        try {
            dbController.activateRule(ruleId);
            return ReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }

    @Override
    public Reply deactivateRule(XOWLRule rule) {
        return deactivateRule(rule.getIdentifier());
    }

    @Override
    public Reply deactivateRule(String ruleId) {
        try {
            dbController.deactivateRule(ruleId);
            return ReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }

    @Override
    public Reply getRuleStatus(XOWLRule rule) {
        return getRuleStatus(rule.getIdentifier());
    }

    @Override
    public Reply getRuleStatus(String ruleId) {
        try {
            RDFRuleStatus status = dbController.getRuleStatus(ruleId);
            if (status == null)
                return new ReplyApiError(ApiV1.ERROR_RULE_NOT_ACTIVE);
            return new ReplyResult<>(status);
        } catch (Exception exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }

    @Override
    public Reply getStoredProcedures() {
        try {
            Collection<XOWLStoredProcedure> procedures = dbController.getStoredProcedures();
            return new ReplyResultCollection<>(procedures);
        } catch (Exception exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }

    @Override
    public Reply getStoreProcedure(String iri) {
        try {
            XOWLStoredProcedure procedure = dbController.getStoredProcedure(iri);
            if (procedure == null)
                return ReplyNotFound.instance();
            return new ReplyResult<>(procedure);
        } catch (Exception exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }

    @Override
    public Reply addStoredProcedure(String iri, String sparql, Collection<String> parameters) {
        try {
            XOWLStoredProcedure procedure = dbController.addStoredProcedure(iri, sparql, parameters);
            return new ReplyResult<>(procedure);
        } catch (Exception exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }

    @Override
    public Reply removeStoredProcedure(XOWLStoredProcedure procedure) {
        return removeStoredProcedure(procedure.getIdentifier());
    }

    @Override
    public Reply removeStoredProcedure(String procedureId) {
        try {
            dbController.removeStoredProcedure(procedureId);
            return ReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }

    @Override
    public Reply executeStoredProcedure(XOWLStoredProcedure procedure, XOWLStoredProcedureContext context) {
        return executeStoredProcedure(procedure.getIdentifier(), context);
    }

    @Override
    public Reply executeStoredProcedure(String procedureId, XOWLStoredProcedureContext context) {
        try {
            Result result = dbController.executeStoredProcedure(procedureId, context, false);
            return new ReplyResult<>(result);
        } catch (Exception exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }

    @Override
    public Reply upload(String syntax, String content) {
        try {
            BufferedLogger logger = new BufferedLogger();
            dbController.upload(logger, syntax, content);
            if (!logger.getErrorMessages().isEmpty())
                return new ReplyApiError(ApiV1.ERROR_CONTENT_PARSING_FAILED, logger.getErrorsAsString());
            return ReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }

    @Override
    public Reply upload(Collection<Quad> quads) {
        try {
            dbController.upload(quads);
            return ReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new ReplyException(exception);
        }
    }
}
