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
import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.server.api.XOWLStoredProcedureContext;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.rdf.Node;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * Base implementation of a stored procedure's context
 *
 * @author Laurent Wouters
 */
public class BaseStoredProcedureContext implements XOWLStoredProcedureContext {
    /**
     * The context's default IRIs
     */
    private final List<String> defaultIRIs;
    /**
     * The context's named IRIs
     */
    private final List<String> namedIRIs;
    /**
     * The parameters for this procedure
     */
    private final Map<String, Node> parameters;

    /**
     * Initializes this context
     *
     * @param defaultIRIs The context's default IRIs
     * @param namedIRIs   The context's named IRIs
     * @param parameters  The parameters for this procedure
     */
    public BaseStoredProcedureContext(List<String> defaultIRIs, List<String> namedIRIs, Map<String, Node> parameters) {
        this.defaultIRIs = new ArrayList<>(defaultIRIs);
        this.namedIRIs = new ArrayList<>(namedIRIs);
        this.parameters = new HashMap<>(parameters);
    }

    /**
     * Initializes this context
     *
     * @param root       The context's definition
     * @param repository The repository to use
     */
    public BaseStoredProcedureContext(ASTNode root, Repository repository) {
        this.defaultIRIs = new ArrayList<>();
        this.namedIRIs = new ArrayList<>();
        this.parameters = new HashMap<>();
        for (ASTNode child : root.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            switch (name) {
                case "defaultIRIs": {
                    for (ASTNode nodeValue : child.getChildren().get(1).getChildren()) {
                        String value = TextUtils.unescape(nodeValue.getValue());
                        value = value.substring(1, value.length() - 1);
                        defaultIRIs.add(value);
                    }
                    break;
                }
                case "namedIRIs": {
                    for (ASTNode nodeValue : child.getChildren().get(1).getChildren()) {
                        String value = TextUtils.unescape(nodeValue.getValue());
                        value = value.substring(1, value.length() - 1);
                        namedIRIs.add(value);
                    }
                    break;
                }
                case "parameters": {
                    for (ASTNode nodeMap : child.getChildren().get(1).getChildren()) {
                        ASTNode nodeProperty = nodeMap.getChildren().get(0);
                        String parameterName = TextUtils.unescape(nodeProperty.getChildren().get(0).getValue());
                        parameterName = parameterName.substring(1, parameterName.length() - 1);
                        Node parameterValue = RDFUtils.deserializeJSON(repository, nodeProperty.getChildren().get(1));
                        parameters.put(parameterName, parameterValue);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public List<String> getDefaultIRIs() {
        return Collections.unmodifiableList(defaultIRIs);
    }

    @Override
    public List<String> getNamedIRIs() {
        return Collections.unmodifiableList(namedIRIs);
    }

    @Override
    public Map<String, Node> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        StringWriter buffer = new StringWriter();
        buffer.append("{\"type\": \"");
        buffer.append(TextUtils.escapeStringJSON(XOWLStoredProcedureContext.class.getCanonicalName()));
        buffer.append("\", \"defaultIRIs\": [");
        for (int i = 0; i != defaultIRIs.size(); i++) {
            if (i != 0)
                buffer.append(", ");
            buffer.append("\"");
            buffer.append(TextUtils.escapeStringJSON(defaultIRIs.get(i)));
            buffer.append("\"");
        }
        buffer.append("], \"namedIRIs\": [");
        for (int i = 0; i != namedIRIs.size(); i++) {
            if (i != 0)
                buffer.append(", ");
            buffer.append("\"");
            buffer.append(TextUtils.escapeStringJSON(namedIRIs.get(i)));
            buffer.append("\"");
        }
        buffer.append("], \"parameters\": [");
        boolean first = true;
        for (Map.Entry<String, Node> entry : parameters.entrySet()) {
            if (!first) {
                buffer.append(", ");
            }
            buffer.append("{\"");
            buffer.append(TextUtils.escapeStringJSON(entry.getKey()));
            buffer.append("\": ");
            try {
                RDFUtils.serializeJSON(buffer, entry.getValue());
            } catch (IOException exception) {
                // cannot happen
            }
            buffer.append("}");
            first = false;
        }
        buffer.append("]}");
        return buffer.toString();
    }
}
