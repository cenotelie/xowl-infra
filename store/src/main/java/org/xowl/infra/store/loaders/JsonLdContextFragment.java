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

package org.xowl.infra.store.loaders;

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.utils.collections.Couple;

import java.util.*;

/**
 * Represents a fragment of context
 *
 * @author Laurent Wouters
 */
class JsonLdContextFragment {
    /**
     * The name mappings in this fragment
     */
    private final Map<String, String> mappings;
    /**
     * The attributes of a name
     */
    private final Map<String, List<Couple<String, Object>>> attributes;
    /**
     * The current language
     */
    private final String language;
    /**
     * The current base URI
     */
    private final String base;
    /**
     * The current vocabulary radical
     */
    private final String vocabulary;

    /**
     * Gets the attributes of the specified name
     *
     * @param name A name
     * @return The corresponding attributes
     */
    public Collection<Couple<String, Object>> getAttributes(String name) {
        List<Couple<String, Object>> list = attributes.get(name);
        if (list == null)
            return Collections.emptyList();
        return Collections.unmodifiableCollection(list);
    }

    /**
     * Gets the current language
     *
     * @return The current language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Gets the current base URI
     *
     * @return The current base URI
     */
    public String getBaseURI() {
        return base;
    }

    /**
     * Gets the current vocabulary radical
     *
     * @return The current vocabulary radical
     */
    public String getVocabulary() {
        return vocabulary;
    }

    /**
     * Initializes this fragment
     *
     * @param definition The AST node to load from
     */
    public JsonLdContextFragment(ASTNode definition) throws LoaderException {
        mappings = new HashMap<>();
        attributes = new HashMap<>();
        String tLanguage = null;
        String tBase = null;
        String tVocab = null;
        for (ASTNode member : definition.getChildren()) {
            String key = JsonLdLoader.getValue(member.getChildren().get(0));
            if (Vocabulary.JSONLD.language.equals(key)) {
                tLanguage = JsonLdLoader.getValue(member.getChildren().get(1));
                tLanguage = tLanguage == null ? Vocabulary.JSONLD.null_ : tLanguage;
            } else if (Vocabulary.JSONLD.base.equals(key)) {
                tBase = JsonLdLoader.getValue(member.getChildren().get(1));
                tBase = tBase == null ? Vocabulary.JSONLD.null_ : tBase;
            } else if (Vocabulary.JSONLD.vocab.equals(key)) {
                tVocab = JsonLdLoader.getValue(member.getChildren().get(1));
                tVocab = tVocab == null ? Vocabulary.JSONLD.null_ : tVocab;
            }
        }
        this.language = tLanguage;
        this.base = tBase;
        this.vocabulary = tVocab;
    }

    /**
     * Loads the name definitions for this fragment
     *
     * @param definition The AST node to load from
     */
    public void loadNames(ASTNode definition) throws LoaderException {
        for (ASTNode member : definition.getChildren()) {
            String key = JsonLdLoader.getValue(member.getChildren().get(0));
            if (key == null)
                throw new LoaderException("Expected valid key", definition);
            if (!JsonLdLoader.KEYWORDS.contains(key)) {
                loadName(key, member.getChildren().get(1));
            }
        }
    }

    /**
     * Loads a name definition
     *
     * @param name       The name key
     * @param definition The AST node to load from
     */
    private void loadName(String name, ASTNode definition) throws LoaderException {
        switch (definition.getSymbol().getID()) {
            case JsonLexer.ID.LITERAL_STRING:
                // this is an IRI
                mappings.put(name, JsonLdLoader.getValue(definition));
                break;
            case JsonLexer.ID.LITERAL_NULL:
                // explicitly forbids the expansion
                mappings.put(name, Vocabulary.JSONLD.null_);
            case JsonParser.ID.object:
                loadNameFromObject(name, definition);
                break;
            default:
                throw new LoaderException("Unexpected name definition", definition);
        }
    }

    /**
     * Loads a name definition from a JSON object
     *
     * @param name       The name key
     * @param definition The AST node to load from
     */
    private void loadNameFromObject(String name, ASTNode definition) throws LoaderException {
        List<Couple<String, Object>> list = new ArrayList<>();
        attributes.put(name, list);
        for (ASTNode member : definition.getChildren()) {
            String key = JsonLdLoader.getValue(member.getChildren().get(0));
            if (Vocabulary.JSONLD.id.equals(key)) {
                String value = JsonLdLoader.getValue(member.getChildren().get(1));
                value = value == null ? Vocabulary.JSONLD.null_ : value;
                mappings.put(name, value);
            } else if (Vocabulary.JSONLD.type.equals(key)) {
                String value = JsonLdLoader.getValue(member.getChildren().get(1));
                list.add(new Couple<String, Object>(Vocabulary.JSONLD.type, value));
            } else if (Vocabulary.JSONLD.container.equals(key)) {
                String value = JsonLdLoader.getValue(member.getChildren().get(1));
                if (value != null) {
                    switch (value) {
                        case Vocabulary.JSONLD.list:
                            list.add(new Couple<String, Object>(Vocabulary.JSONLD.container, JsonLdContainerType.List));
                            break;
                        case Vocabulary.JSONLD.set:
                            list.add(new Couple<String, Object>(Vocabulary.JSONLD.container, JsonLdContainerType.Set));
                            break;
                        case Vocabulary.JSONLD.index:
                            list.add(new Couple<String, Object>(Vocabulary.JSONLD.container, JsonLdContainerType.Index));
                            break;
                        case Vocabulary.JSONLD.language:
                            list.add(new Couple<String, Object>(Vocabulary.JSONLD.container, JsonLdContainerType.Language));
                            break;
                        default:
                            list.add(new Couple<String, Object>(Vocabulary.JSONLD.container, JsonLdContainerType.Undefined));
                            break;
                    }
                }
            } else if (Vocabulary.JSONLD.language.equals(key)) {
                String value = JsonLdLoader.getValue(member.getChildren().get(1));
                value = value == null ? Vocabulary.JSONLD.null_ : value;
                list.add(new Couple<String, Object>(Vocabulary.JSONLD.language, value));
            } else if (Vocabulary.JSONLD.reverse.equals(key)) {
                String value = JsonLdLoader.getValue(member.getChildren().get(1));
                list.add(new Couple<String, Object>(Vocabulary.JSONLD.reverse, value));
            }
        }
    }

    /**
     * Tries to expand the specified term with the mappings in this fragment
     *
     * @param term The term to expand
     * @return The expanded term, or null if it cannot be expanded
     */
    public String expand(String term) {
        return mappings.get(term);
    }
}
