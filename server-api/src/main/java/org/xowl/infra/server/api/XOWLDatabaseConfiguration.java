/*******************************************************************************
 * Copyright (c) 2020 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.server.api;

import fr.cenotelie.commons.utils.Serializable;
import fr.cenotelie.commons.utils.TextUtils;
import fr.cenotelie.commons.utils.logging.Logging;
import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.server.base.BaseRule;
import org.xowl.infra.server.base.BaseStoredProcedure;
import org.xowl.infra.store.EntailmentRegime;
import org.xowl.infra.store.storage.cache.CachedNodes;

import java.util.ArrayList;
import java.util.List;

/**
 * The initial configuration for a database
 */
public class XOWLDatabaseConfiguration implements Serializable {
    /**
     * Whether to use in memory store
     */
    private final boolean inMemory;
    /**
     * The entailment regime to set
     */
    private final EntailmentRegime entailmentRegime;
    /**
     * The initial rules
     */
    private final List<XOWLRule> rules;
    /**
     * The initial procedures
     */
    private final List<XOWLStoredProcedure> procedures;

    /**
     * Initializes this configuration
     *
     * @param inMemory         Whether to use in memory store
     * @param entailmentRegime The entailment regime to set
     */
    public XOWLDatabaseConfiguration(boolean inMemory, EntailmentRegime entailmentRegime) {
        this.inMemory = inMemory;
        this.entailmentRegime = entailmentRegime;
        this.rules = new ArrayList<>();
        this.procedures = new ArrayList<>();
    }

    /**
     * Initializes this configuration
     *
     * @param definition The definition
     */
    public XOWLDatabaseConfiguration(ASTNode definition) {
        this.rules = new ArrayList<>();
        this.procedures = new ArrayList<>();
        boolean inMemory = false;
        EntailmentRegime entailmentRegime = EntailmentRegime.none;
        for (ASTNode member : definition.getChildren()) {
            String name = TextUtils.unescape(member.getChildren().get(0).getValue());
            name = name.substring(1, name.length() - 1);
            switch (name) {
                case "inMemory": {
                    String value = member.getChildren().get(1).getValue();
                    if (value.startsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    inMemory = value.equals("true");
                    break;
                }
                case "entailmentRegime": {
                    String value = member.getChildren().get(1).getValue();
                    value = value.substring(1, value.length() - 1);
                    entailmentRegime = EntailmentRegime.valueOf(value);
                    break;
                }
                case "rules":
                    for (ASTNode value : member.getChildren().get(1).getChildren()) {
                        this.rules.add(new BaseRule(value));
                    }
                    break;
                case "procedures":
                    for (ASTNode value : member.getChildren().get(1).getChildren()) {
                        this.procedures.add(new BaseStoredProcedure(value, new CachedNodes(), Logging.get()));
                    }
                    break;
            }
        }
        this.inMemory = inMemory;
        this.entailmentRegime = entailmentRegime;
    }

    public boolean isInMemory() {
        return inMemory;
    }

    public EntailmentRegime getEntailmentRegime() {
        return entailmentRegime;
    }

    public List<XOWLRule> getRules() {
        return rules;
    }

    public List<XOWLStoredProcedure> getProcedures() {
        return procedures;
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder("{\"type\": \"");
        builder.append(TextUtils.escapeStringJSON(XOWLDatabaseConfiguration.class.getCanonicalName()));
        builder.append("\", \"inMemory\": ");
        builder.append(inMemory);
        builder.append(" \"entailmentRegime\": \"");
        builder.append(entailmentRegime.toString());
        builder.append("\", \"rules\": [");
        for (int i = 0; i != rules.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(rules.get(i).serializedJSON());
        }
        builder.append("], \"procedures\": [");
        for (int i = 0; i != procedures.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(procedures.get(i).serializedJSON());
        }
        builder.append("]}");
        return builder.toString();
    }
}
