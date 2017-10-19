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
import fr.cenotelie.commons.utils.logging.Logger;
import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.server.api.XOWLStoredProcedure;
import org.xowl.infra.store.loaders.SPARQLLoader;
import org.xowl.infra.store.sparql.Command;
import org.xowl.infra.store.storage.NodeManager;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The base implementation of a stored procedure
 *
 * @author Laurent Wouters
 */
public class BaseStoredProcedure implements XOWLStoredProcedure {
    /**
     * The procedure's identifier (IRI)
     */
    private final String identifier;
    /**
     * The procedure's definition
     */
    private final String definition;
    /**
     * The parameters for this procedure
     */
    private final List<String> parameters;
    /**
     * The loaded SPARQL command
     */
    private final Command sparql;

    /**
     * Initializes this procedure
     *
     * @param identifier The procedure's identifier (IRI)
     * @param definition The procedure's definition
     * @param parameters The parameters for this procedure
     * @param sparql     The loaded SPARQL command
     */
    public BaseStoredProcedure(String identifier, String definition, Collection<String> parameters, Command sparql) {
        this.identifier = identifier;
        this.definition = definition;
        this.parameters = new ArrayList<>(parameters);
        this.sparql = sparql;
    }

    /**
     * Initializes this procedure
     *
     * @param root   The procedure's definition
     * @param nodes  The node manager to use for parsing the SPARQL command
     * @param logger The logger to use when parsing the SPARQL command
     */
    public BaseStoredProcedure(ASTNode root, NodeManager nodes, Logger logger) {
        String vName = null;
        String vDef = "";
        this.parameters = new ArrayList<>();
        for (ASTNode child : root.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            switch (name) {
                case "name": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    vName = TextUtils.unescape(nodeValue.getValue());
                    vName = vName.substring(1, vName.length() - 1);
                    break;
                }
                case "definition": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    vDef = TextUtils.unescape(nodeValue.getValue());
                    vDef = vDef.substring(1, vDef.length() - 1);
                    break;
                }
                case "parameters": {
                    for (ASTNode nodeValue : child.getChildren().get(1).getChildren()) {
                        String value = TextUtils.unescape(nodeValue.getValue());
                        value = value.substring(1, value.length() - 1);
                        parameters.add(value);
                    }
                    break;
                }
            }
        }
        this.identifier = vName;
        this.definition = vDef;
        Command vSPARQL = null;
        if (nodes != null) {
            SPARQLLoader loader = new SPARQLLoader(nodes);
            vSPARQL = loader.load(logger, new StringReader(this.definition));
        }
        this.sparql = vSPARQL;
    }

    /**
     * Gets the SPARQL command for this procedure
     *
     * @return The SPARQL command
     */
    public Command getSPARQL() {
        return sparql;
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
    public String getDefinition() {
        return definition;
    }

    @Override
    public Collection<String> getParameters() {
        return Collections.unmodifiableCollection(parameters);
    }

    @Override
    public String serializedString() {
        return identifier;
    }

    @Override
    public String serializedJSON() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{\"type\": \"");
        buffer.append(TextUtils.escapeStringJSON(XOWLStoredProcedure.class.getCanonicalName()));
        buffer.append("\", \"identifier\": \"");
        buffer.append(TextUtils.escapeStringJSON(getIdentifier()));
        buffer.append("\", \"name\": \"");
        buffer.append(TextUtils.escapeStringJSON(getName()));
        buffer.append("\", \"definition\": \"");
        buffer.append(TextUtils.escapeStringJSON(definition));
        buffer.append("\", \"parameters\": [");
        for (int i = 0; i != parameters.size(); i++) {
            if (i != 0)
                buffer.append(", ");
            buffer.append("\"");
            buffer.append(TextUtils.escapeStringJSON(parameters.get(i)));
            buffer.append("\"");
        }
        buffer.append("]}");
        return buffer.toString();
    }
}
