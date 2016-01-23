/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.server.api.base;

import org.xowl.hime.redist.ASTNode;
import org.xowl.server.api.XOWLDatabase;
import org.xowl.server.api.XOWLRule;
import org.xowl.server.xsp.XSPReply;
import org.xowl.server.xsp.XSPReplyFailure;
import org.xowl.store.EntailmentRegime;
import org.xowl.store.IOUtils;
import org.xowl.store.rdf.Quad;

import java.util.List;

/**
 * Base implementation of a database
 *
 * @author Laurent Wouters
 */
public class BaseDatabase implements XOWLDatabase {
    /**
     * The database's name
     */
    protected final String name;

    /**
     * Initializes this database
     *
     * @param name The database's name
     */
    public BaseDatabase(String name) {
        this.name = name;
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
            String name = IOUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            if (name.equals("name")) {
                ASTNode nodeValue = child.getChildren().get(1);
                value = IOUtils.unescape(nodeValue.getValue());
                value = value.substring(1, value.length() - 1);
            }
        }
        this.name = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public XSPReply sparql(XOWLDatabase database, String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        return XSPReplyFailure.instance();
    }

    @Override
    public XSPReply getEntailmentRegime() {
        return XSPReplyFailure.instance();
    }

    @Override
    public XSPReply setEntailmentRegime(EntailmentRegime regime) {
        return XSPReplyFailure.instance();
    }

    @Override
    public XSPReply getRule(String name) {
        return XSPReplyFailure.instance();
    }

    @Override
    public XSPReply getRules() {
        return XSPReplyFailure.instance();
    }

    @Override
    public XSPReply addRule(String content, boolean activate) {
        return XSPReplyFailure.instance();
    }

    @Override
    public XSPReply removeRule(XOWLRule rule) {
        return XSPReplyFailure.instance();
    }

    @Override
    public XSPReply activateRule(XOWLRule rule) {
        return XSPReplyFailure.instance();
    }

    @Override
    public XSPReply deactivateRule(XOWLRule rule) {
        return XSPReplyFailure.instance();
    }

    @Override
    public XSPReply getRuleStatus(XOWLRule rule) {
        return XSPReplyFailure.instance();
    }

    @Override
    public XSPReply getQuadExplanation(Quad quad) {
        return XSPReplyFailure.instance();
    }

    @Override
    public XSPReply upload(String syntax, String content) {
        return XSPReplyFailure.instance();
    }

    @Override
    public String serializedString() {
        return getName();
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" + IOUtils.escapeStringJSON(BaseDatabase.class.getCanonicalName()) + "\", \"name\": \"" + IOUtils.escapeStringJSON(getName()) + "\"}";
    }
}
