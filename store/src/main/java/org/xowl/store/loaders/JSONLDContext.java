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

package org.xowl.store.loaders;

import org.xowl.hime.redist.ASTNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a context for a JSON-LD loader
 *
 * @author Laurent Wouters
 */
class JSONLDContext {
    /**
     * The parent loader
     */
    private final JSONLDLoader loader;
    /**
     * The parent context
     */
    private final JSONLDContext parent;
    /**
     * The fragments in this context
     */
    private final List<JSONLDContextFragment> fragments;

    /**
     * Initializes an empty context
     *
     * @param loader The parent loader
     */
    public JSONLDContext(JSONLDLoader loader) {
        this.loader = loader;
        this.parent = null;
        this.fragments = Collections.emptyList();
    }

    /**
     * Initializes this context with the specified parent
     *
     * @param parent     The parent context
     * @param definition The AST node to load from
     */
    public JSONLDContext(JSONLDContext parent, ASTNode definition) throws JSONLDLoadingException {
        this.loader = parent.loader;
        this.parent = definition.getSymbol().getID() == JSONLDLexer.ID.LITERAL_NULL ? null : parent;
        this.fragments = new ArrayList<>();
        List<ASTNode> definitions = new ArrayList<>();
        definitions.add(definition);
        for (int i = 0; i != definitions.size(); i++) {
            definition = definitions.get(i);
            if (definition.getSymbol().getID() == JSONLDLexer.ID.LITERAL_STRING) {
                // external document
                definition = loader.getExternalContextDefinition(JSONLDLoader.getValue(definition));
                if (definition != null)
                    definitions.add(definition);
            } else if (definition.getSymbol().getID() == JSONLDParser.ID.array) {
                // combined definitions
                definitions.addAll(definition.getChildren());
            } else if (definition.getSymbol().getID() == JSONLDParser.ID.object) {
                // inline definition
                JSONLDContextFragment fragment = new JSONLDContextFragment(definition);
                fragments.add(fragment);
                fragment.loadNames(definition);
            }
        }
    }

    /**
     * Expands a name into an IRI
     *
     * @param name The name to resolve
     * @return The corresponding IRI, or null if it cannot be expanded
     */
    public String expandName(String name) {
        return doExpandIRI(name, true, false, true, false);
    }

    /**
     * Expands a JSON-LD ID into an IRI
     *
     * @param name The name to resolve
     * @return The corresponding IRI, or null if it cannot be expanded
     */
    public String expandID(String name) {
        return doExpandIRI(name, false, true, false, true);
    }

    /**
     * Expands a resource identifier into an IRI
     *
     * @param name The name to resolve
     * @return The corresponding IRI, or null if it cannot be expanded
     */
    public String expandResource(String name) {
        return doExpandIRI(name, true, true, true, true);
    }

    /**
     * Expands the specified name into an IRI
     *
     * @param name          The name to resolve
     * @param useTerm       Whether terms can be used in the expansion of the IRI
     * @param useBaseURI    Whether the base URI definition can be used in the expansion of the IRI
     * @param useVocabulary Whether the vocabulary definition can be used in the expansion of the IRI
     * @param useResource   Whether the resource URI can be used in the expansion of the IRI
     * @return The corresponding IRI, or null if it cannot be expanded
     */
    private String doExpandIRI(final String name, final boolean useTerm, final boolean useBaseURI, final boolean useVocabulary, final boolean useResource) {
        if (name == null)
            return null;
        if (JSONLDLoader.KEYWORDS.contains(name))
            // do not map keywords
            return name;
        // look for a fix point in expansion
        String current = name;
        String result = doExpandIRIOneTime(name, useTerm, useBaseURI, useVocabulary, useResource);
        while (!current.equals(result)) {
            current = result;
            result = doExpandIRIOneTime(current, useTerm, useBaseURI, useVocabulary, useResource);
        }
        return result;
    }

    /**
     * Expands a single time the specified name
     *
     * @param name          The name to resolve
     * @param useTerm       Whether terms can be used in the expansion of the IRI
     * @param useBaseURI    Whether the base URI definition can be used in the expansion of the IRI
     * @param useVocabulary Whether the vocabulary definition can be used in the expansion of the IRI
     * @param useResource   Whether the resource URI can be used in the expansion of the IRI
     * @return The corresponding IRI
     */
    private String doExpandIRIOneTime(final String name, final boolean useTerm, final boolean useBaseURI, final boolean useVocabulary, final boolean useResource) {
        if (name.startsWith("_:"))
            // blank node identifier, return as is
            return name;
        // if we can use terms, try to match the complete term
        String result = useTerm ? doExpandUsingTerms(name) : null;
        if (result != null && !name.equals(result)) {
            if (JSONLDLoader.MARKER_NULL.equals(result))
                return name;
            return result;
        }
        result = doExpandCompactIRI(name);
        if (result != null)
            return result;
        // if we can use the vocabulary, try to look for one
        result = useVocabulary ? doExpandUsingVocabulary(name) : null;
        if (result != null && !JSONLDLoader.MARKER_NULL.equals(result))
            return result;
        // now term is supposed to be a relative IRI
        // if we can use the base URI, try to look for one
        result = useBaseURI ? doExpandUsingBaseURI(name) : null;
        if (result != null) {
            if (JSONLDLoader.MARKER_NULL.equals(result))
                return name;
            return result;
        }
        // if we can use the resource URI, resolve against it
        if (useResource)
            return Utils.uriResolveRelative(loader.getCurrentResource(), Utils.quote(name));
        // all failed :( return as is
        return name;
    }

    /**
     * Expand a name using term definitions
     *
     * @param name The name to resolve
     * @return The expanded name, or null if the expansion failed
     */
    private String doExpandUsingTerms(final String name) {
        JSONLDContext current = this;
        while (current != null) {
            for (int i = current.fragments.size() - 1; i != -1; i--) {
                JSONLDContextFragment fragment = current.fragments.get(i);
                String result = fragment.expand(name);
                if (result != null) {
                    if (JSONLDLoader.MARKER_NULL.equals(result))
                        // explicitly forbids the expansion
                        return JSONLDLoader.MARKER_NULL;
                    return result;
                }
            }
            current = current.parent;
        }
        return null;
    }

    /**
     * Expand a name as a compact IRI
     *
     * @param name The name to resolve
     * @return The expanded name, or null if the expansion failed
     */
    private String doExpandCompactIRI(final String name) {
        int colonIndex = name.indexOf(':');
        while (colonIndex != -1) {
            String prefix = name.substring(0, colonIndex);
            String suffix = (colonIndex == name.length() - 1) ? "" : name.substring(colonIndex + 1, name.length());
            if (suffix.startsWith("//"))
                // this matches absolute URIs as per JSON-LD spec
                return prefix + ":" + suffix;
            String expandedPrefix = resolveNamespace(prefix);
            if (!prefix.equals(expandedPrefix))
                // prefix was expanded
                return expandedPrefix + suffix;
            if (colonIndex >= name.length() + 1)
                break;
            colonIndex = name.indexOf(':', colonIndex + 1);
        }
        return null;
    }

    /**
     * Expand a name using a vocabulary
     *
     * @param name The name to resolve
     * @return The expanded name, or null if the expansion failed
     */
    private String doExpandUsingVocabulary(final String name) {
        JSONLDContext current = this;
        while (current != null) {
            for (int i = current.fragments.size() - 1; i != -1; i--) {
                JSONLDContextFragment fragment = current.fragments.get(i);
                if (fragment.getVocabulary() != null) {
                    // found a vocabulary
                    if (JSONLDLoader.MARKER_NULL.equals(fragment.getVocabulary()))
                        // explicitly forbids the expansion
                        return JSONLDLoader.MARKER_NULL;
                    return fragment.getVocabulary() + name;
                }
            }
            current = current.parent;
        }
        return null;
    }

    /**
     * Expand a name using a base URI
     *
     * @param name The name to resolve
     * @return The expanded name, or null if the expansion failed
     */
    private String doExpandUsingBaseURI(final String name) {
        JSONLDContext current = this;
        while (current != null) {
            for (int i = current.fragments.size() - 1; i != -1; i--) {
                JSONLDContextFragment fragment = current.fragments.get(i);
                if (fragment.getBaseURI() != null) {
                    // found a base IRI
                    if (JSONLDLoader.MARKER_NULL.equals(fragment.getBaseURI()))
                        // explicitly forbids the expansion
                        return JSONLDLoader.MARKER_NULL;
                    return Utils.uriResolveRelative(fragment.getBaseURI(), Utils.quote(name));
                }
            }
            current = current.parent;
        }
        return null;
    }

    /**
     * Resolves the specified namespace
     *
     * @param name A name
     * @return The associated namespace
     */
    private String resolveNamespace(String name) {
        JSONLDContext current = this;
        while (current != null) {
            for (int i = current.fragments.size() - 1; i != -1; i--) {
                JSONLDContextFragment fragment = current.fragments.get(i);
                String result = fragment.expand(name);
                if (result != null) {
                    if (JSONLDLoader.MARKER_NULL.equals(result))
                        // explicitly forbids the expansion
                        return name;
                    return result;
                }
            }
            current = current.parent;
        }
        return name;
    }

    /**
     * Gets the information about the specified name
     *
     * @param term A name
     * @return The associated information
     */
    public JSONLDNameInfo getInfoFor(String term) {
        JSONLDNameInfo result = new JSONLDNameInfo();
        result.fullIRI = expandName(term);
        JSONLDContext current = this;
        while (current != null) {
            for (int i = current.fragments.size() - 1; i != -1; i--) {
                result.mergeWith(current.fragments.get(i).getAttributes(term));
                result.mergeWith(current.fragments.get(i).getAttributes(result.fullIRI));
            }
            current = current.parent;
        }
        if (result.valueType != null && !JSONLDLoader.KEYWORD_ID.equals(result.valueType))
            result.valueType = expandName(result.valueType);
        if (result.reversed != null)
            result.reversed = expandName(result.reversed);
        return result;
    }

    /**
     * Gets the current language
     *
     * @return The current language, if any
     */
    public String getLanguage() {
        JSONLDContext current = this;
        while (current != null) {
            for (int i = current.fragments.size() - 1; i != -1; i--) {
                String language = current.fragments.get(i).getLanguage();
                if (language != null) {
                    if (JSONLDLoader.MARKER_NULL.equals(language)) {
                        // explicit reset
                        return null;
                    } else {
                        // found  a defined language
                        return language;
                    }
                }
            }
            current = current.parent;
        }
        return null;
    }
}
