/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.denotation.phrases;

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.utils.Identifiable;
import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.collections.Couple;

import java.util.*;

/**
 * Represents a phrase produced by a parser from an input artifact
 *
 * @author Laurent Wouters
 */
public class Phrase implements Identifiable, Serializable {
    /**
     * The identifier of this phrase
     */
    private final String identifier;
    /**
     * The human-readable name of this phrase
     */
    private final String name;
    /**
     * The signs found in the phrase
     */
    private final List<Sign> signs;
    /**
     * The signs by identifier
     */
    private Map<String, Sign> signsById;

    /**
     * Initializes this phrase
     *
     * @param identifier The identifier of this phrase
     * @param name       The human-readable name of this phrase
     * @param signs      The signs found in the phrase
     */
    public Phrase(String identifier, String name, List<Sign> signs) {
        this.identifier = identifier;
        this.name = name;
        this.signs = Collections.unmodifiableList(signs);
    }

    /**
     * Initializes this phrase
     *
     * @param definition The serialized definition
     */
    public Phrase(ASTNode definition) {
        String identifier = null;
        String name = null;
        Map<String, Sign> signs = new HashMap<>();
        List<Couple<Sign, ASTNode>> signDefinitions = new ArrayList<>();
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeHeader = child.getChildren().get(0);
            String memberName = TextUtils.unescape(nodeHeader.getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            switch (memberName) {
                case "identifier": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    identifier = TextUtils.unescape(nodeValue.getValue());
                    identifier = identifier.substring(1, identifier.length() - 1);
                    break;
                }
                case "name": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    name = TextUtils.unescape(nodeValue.getValue());
                    name = name.substring(1, name.length() - 1);
                    break;
                }
                case "signs": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    for (ASTNode sub : nodeValue.getChildren()) {
                        Sign sign = new Sign(sub);
                        signs.put(sign.getIdentifier(), sign);
                        signDefinitions.add(new Couple<>(sign, sub));
                    }
                    break;
                }
            }
        }
        this.identifier = identifier;
        this.name = name;
        this.signs = Collections.unmodifiableList(new ArrayList<>(signs.values()));
        for (Couple<Sign, ASTNode> couple : signDefinitions)
            couple.x.loadRelations(couple.y, signs);
    }

    /**
     * Gets the signs found in the phrase
     *
     * @return The signs found in the phrase
     */
    public Collection<Sign> getSigns() {
        return signs;
    }

    /**
     * Gets the signs for the specified identifier
     *
     * @param identifier The identifier to look for
     * @return The corresponding sign, or null if there is none
     */
    public Sign getSign(String identifier) {
        if (signsById == null) {
            signsById = new HashMap<>();
            for (Sign sign : signs)
                signsById.put(sign.getIdentifier(), sign);
        }
        return signsById.get(identifier);
    }

    /**
     * Builds the RDF serialization of this phrase
     *
     * @param nodes  The node manager to use
     * @param graph  The target graph
     * @param buffer The buffer for the produced quads
     */
    public void serializeRdf(NodeManager nodes, GraphNode graph, Collection<Quad> buffer) {
        for (Sign sign : signs) {
            sign.serializeRdf(nodes, graph, buffer);
        }
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String serializedString() {
        return identifier;
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"type\": \"");
        builder.append(Phrase.class.getCanonicalName());
        builder.append("\", \"identifier\": \"");
        builder.append(TextUtils.serializeJSON(identifier));
        builder.append("\", \"name\": \"");
        builder.append(TextUtils.serializeJSON(name));
        builder.append("\", \"signs\": [");
        boolean first = true;
        for (Sign sign : signs) {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append(sign.serializedJSON());
        }
        builder.append("]}");
        return builder.toString();
    }
}
