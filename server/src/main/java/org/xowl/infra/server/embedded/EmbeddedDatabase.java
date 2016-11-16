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
import org.xowl.infra.server.impl.DatabaseController;
import org.xowl.infra.server.impl.DatabaseImpl;
import org.xowl.infra.server.xsp.*;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.ProxyObject;
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
class EmbeddedDatabase extends DatabaseImpl {
    /**
     * The logger for this database
     */
    private final Logger logger;

    /**
     * Initializes this structure
     *
     * @param logger     The logger for this database
     * @param controller The associated database controller
     * @param proxy      The proxy object that represents the database in the administration database
     */
    public EmbeddedDatabase(Logger logger, DatabaseController controller, ProxyObject proxy) {
        super(controller, proxy);
        this.logger = logger;
    }

    /**
     * Initializes this structure
     *
     * @param logger     The logger for this database
     * @param controller The associated database controller
     * @param proxy      The proxy object that represents the database in the administration database
     * @param name       The name of the database
     */
    public EmbeddedDatabase(Logger logger, DatabaseController controller, ProxyObject proxy, String name) {
        super(controller, proxy, name);
        this.logger = logger;
    }

    @Override
    public XSPReply sparql(String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        Result result = controller.sparql(sparql, defaultIRIs, namedIRIs, false);
        if (result.isFailure())
            return new XSPReplyFailure(((ResultFailure) result).getMessage());
        return new XSPReplyResult<>(result);
    }

    @Override
    public XSPReply sparql(Command sparql) {
        Result result = controller.sparql(sparql, false);
        if (result.isFailure())
            return new XSPReplyFailure(((ResultFailure) result).getMessage());
        return new XSPReplyResult<>(result);
    }

    @Override
    public XSPReply getEntailmentRegime() {
        return new XSPReplyResult<>(controller.getEntailmentRegime());
    }

    @Override
    public XSPReply setEntailmentRegime(EntailmentRegime regime) {
        try {
            controller.setEntailmentRegime(regime);
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
        return XSPReplySuccess.instance();
    }

    @Override
    public XSPReply getRule(String name) {
        try {
            XOWLRule rule = controller.getRule(name);
            if (rule == null)
                return XSPReplyNotFound.instance();
            return new XSPReplyResult<>(rule);
        } catch (IOException exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply getRules() {
        try {
            Collection<XOWLRule> rules = controller.getRules();
            return new XSPReplyResultCollection<>(rules);
        } catch (IOException exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply addRule(String content, boolean activate) {
        try {
            XOWLRule rule = controller.addRule(content, activate);
            return new XSPReplyResult<>(rule);
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply removeRule(XOWLRule rule) {
        try {
            controller.removeRule(rule.getName());
            return XSPReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply activateRule(XOWLRule rule) {
        try {
            controller.activateRule(rule.getName());
            return XSPReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply deactivateRule(XOWLRule rule) {
        try {
            controller.deactivateRule(rule.getName());
            return XSPReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply getRuleStatus(XOWLRule rule) {
        try {
            RDFRuleStatus status = controller.getRuleStatus(rule.getName());
            if (status == null)
                return new XSPReplyFailure("The rule is not active");
            return new XSPReplyResult<>(status);
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply getStoreProcedure(String iri) {
        try {
            XOWLStoredProcedure procedure = controller.getStoredProcedure(iri);
            if (procedure == null)
                return XSPReplyNotFound.instance();
            return new XSPReplyResult<>(procedure);
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply getStoredProcedures() {
        try {
            Collection<XOWLStoredProcedure> procedures = controller.getStoredProcedures();
            return new XSPReplyResultCollection<>(procedures);
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply addStoredProcedure(String iri, String sparql, Collection<String> parameters) {
        try {
            XOWLStoredProcedure procedure = controller.addStoredProcedure(iri, sparql, parameters);
            return new XSPReplyResult<>(procedure);
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply removeStoredProcedure(XOWLStoredProcedure procedure) {
        try {
            controller.removeStoredProcedure(procedure.getName());
            return XSPReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply executeStoredProcedure(XOWLStoredProcedure procedure, XOWLStoredProcedureContext context) {
        try {
            Result result = controller.executeStoredProcedure(procedure.getName(), context, false);
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
            controller.upload(logger, syntax, content);
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
            controller.upload(quads);
            return XSPReplySuccess.instance();
        } catch (Exception exception) {
            logger.error(exception);
            return new XSPReplyFailure(exception.getMessage());
        }
    }

    @Override
    public XSPReply getMetric() {
        return new XSPReplyResult<>(controller.getMetric());
    }

    @Override
    public XSPReply getMetricSnapshot() {
        return new XSPReplyResult<>(controller.getMetricSnapshot());
    }
}
