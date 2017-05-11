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

package org.xowl.infra.denotation.rules;

import fr.cenotelie.hime.redist.*;
import org.xowl.infra.denotation.phrases.PhraseVocabulary;
import org.xowl.infra.denotation.phrases.SignProperty;
import org.xowl.infra.denotation.phrases.SignRelation;
import org.xowl.infra.lang.owl2.IRI;
import org.xowl.infra.lang.owl2.Owl2Factory;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.http.URIUtils;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * A loader of denotation rules
 *
 * @author Laurent Wouters
 */
public class DenotationRuleLoader {
    /**
     * The base URI for rules
     */
    private static final String RULE_URI = "http://xowl.org/infra/denotation/rules/";

    /**
     * Map of the current namespaces
     */
    private final Map<String, String> namespaces;
    /**
     * The base URI for relative URIs
     */
    private String baseURI;
    /**
     * The current logger
     */
    private Logger logger;
    /**
     * The parsed input
     */
    private Text input;
    /**
     * The counter of loaded rules
     */
    private int ruleCounter;
    /**
     * The counter of explicit seme
     */
    private int semeCounter;

    /**
     * Initializes this loader
     */
    public DenotationRuleLoader() {
        this.namespaces = new HashMap<>();
    }

    /**
     * Parses the denotations rules from the specified input stream
     *
     * @param reader The reader to load from
     * @param logger The logger to use
     * @return The parse result
     */
    private ParseResult parse(Reader reader, Logger logger) {
        ParseResult result;
        try {
            String content = IOUtils.read(reader);
            DenotationLexer lexer = new DenotationLexer(content);
            DenotationParser parser = new DenotationParser(lexer);
            parser.setModeRecoverErrors(false);
            result = parser.parse();
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
        for (ParseError error : result.getErrors()) {
            logger.error(error);
            TextContext context = result.getInput().getContext(error.getPosition(), error.getLength());
            logger.error(context.getContent());
            logger.error(context.getPointer());
        }
        return result;
    }

    /**
     * Loads denotations rules from the specified input stream
     *
     * @param reader The reader to load from
     * @param logger The logger to use
     * @return The loaded denotation rules
     */
    public Collection<DenotationRule> load(Reader reader, Logger logger) {
        this.logger = logger;
        ParseResult result = parse(reader, logger);
        if (result == null)
            return Collections.emptyList();
        if (!result.getErrors().isEmpty())
            return Collections.emptyList();
        this.input = result.getInput();
        this.ruleCounter = 0;
        this.semeCounter = 0;

        Collection<DenotationRule> rules = new ArrayList<>();
        ASTNode root = result.getRoot();
        loadPrologue(root.getChildren().get(0));
        for (ASTNode nodeRule : root.getChildren().get(1).getChildren()) {
            DenotationRule rule = loadRule(nodeRule);
            if (rule != null)
                rules.add(rule);
        }
        return rules;
    }

    /**
     * Loads the prologue from the corresponding AST node
     *
     * @param node The AST node
     */
    private void loadPrologue(ASTNode node) {
        for (ASTNode child : node.getChildren()) {
            switch (child.getSymbol().getID()) {
                case DenotationParser.ID.decl_base:
                    loadBase(child);
                    break;
                case DenotationParser.ID.decl_prefix:
                    loadPrefixID(child);
                    break;
            }
        }
    }

    /**
     * Loads a prefix and its associated namespace represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadPrefixID(ASTNode node) {
        String prefix = node.getChildren().get(0).getValue();
        String uri = node.getChildren().get(1).getValue();
        prefix = prefix.substring(0, prefix.length() - 1);
        uri = TextUtils.unescape(uri.substring(1, uri.length() - 1));
        namespaces.put(prefix, uri);
    }

    /**
     * Loads the base URI represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadBase(ASTNode node) {
        String value = node.getChildren().get(0).getValue();
        value = TextUtils.unescape(value.substring(1, value.length() - 1));
        baseURI = URIUtils.resolveRelative(baseURI, value);
    }

    /**
     * Loads the denotation rule from the specified AST node
     *
     * @param node An AST node
     * @return The corresponding denotation rule
     */
    private DenotationRule loadRule(ASTNode node) {
        String title = node.getChildren().get(0).getValue();
        title = TextUtils.unescape(title.substring(1, title.length() - 1));
        DenotationRule rule = new DenotationRule(RULE_URI + ruleCounter, title);
        ruleCounter++;
        Map<String, SignAntecedent> mapAntecedents = new HashMap<>();
        Map<String, SemeConsequent> mapConsequents = new HashMap<>();

        if (node.getChildren().get(1).getSymbol().getID() == DenotationParser.ID.sign_static) {
            rule.addAntecedent(loadSignSpecificReference(node.getChildren().get(1)));
        } else {
            // sign patterns
            SignPattern[] patterns = new SignPattern[node.getChildren().get(1).getChildren().size()];
            int i = 0;
            for (ASTNode nodePattern : node.getChildren().get(1).getChildren()) {
                SignPattern pattern = loadSignPatternInitial(nodePattern);
                patterns[i++] = pattern;
                rule.addAntecedent(pattern);
                mapAntecedents.put(pattern.getIdentifier(), pattern);
            }
            i = 0;
            for (ASTNode nodePattern : node.getChildren().get(1).getChildren()) {
                SignPattern pattern = patterns[i++];
                loadSignPatternContent(nodePattern, pattern, mapAntecedents, mapConsequents);
            }
        }

        SemeConsequent[] consequents = new SemeConsequent[node.getChildren().get(2).getChildren().size()];
        int i = 0;
        for (ASTNode nodeSeme : node.getChildren().get(2).getChildren()) {
            SemeConsequent consequent = loadSemeInitial(nodeSeme, mapConsequents);
            consequents[i++] = consequent;
            if (consequent == null)
                return null;
            rule.addConsequent(consequent);
        }
        i = 0;
        for (ASTNode nodeSeme : node.getChildren().get(2).getChildren()) {
            SemeConsequent consequent = consequents[i++];
            if (consequent == null)
                continue;
            loadSemeConsequentProperties(nodeSeme, consequent, mapAntecedents, mapConsequents);
            loadSemeConsequentBindings(nodeSeme, consequent, mapAntecedents);
        }

        return rule;
    }

    /**
     * Loads a reference to a specific sign
     *
     * @param node An AST node
     * @return The reference
     */
    private SignReference loadSignSpecificReference(ASTNode node) {
        String signId = node.getChildren().get(0).getValue();
        signId = TextUtils.unescape(signId.substring(1, signId.length() - 1));
        return new SignReference(signId);
    }

    /**
     * Initial loading of a sign pattern
     *
     * @param node An AST node
     * @return The sign pattern
     */
    private SignPattern loadSignPatternInitial(ASTNode node) {
        String patternId = node.getChildren().get(0).getValue().substring(1);
        return new SignPattern(patternId);
    }

    /**
     * Loads the content of sign pattern
     *
     * @param node           An AST node
     * @param pattern        The pattern to load
     * @param mapAntecedents The current antecedents
     * @param mapConsequents The current consequents
     */
    private void loadSignPatternContent(ASTNode node, SignPattern pattern, Map<String, SignAntecedent> mapAntecedents, Map<String, SemeConsequent> mapConsequents) {
        for (ASTNode nodeProperty : node.getChildren().get(1).getChildren()) {
            loadSignPatternProperty(nodeProperty, pattern);
        }
        for (ASTNode nodeRelation : node.getChildren().get(2).getChildren()) {
            loadSignPatternRelation(nodeRelation, pattern, mapAntecedents);
        }
        loadSignPatternBinding(node.getChildren().get(3), pattern, mapConsequents);
    }

    /**
     * Loads the property constraint for a sign pattern
     *
     * @param node    An AST node
     * @param pattern The pattern to load
     */
    private void loadSignPatternProperty(ASTNode node, SignPattern pattern) {
        String propertyIri = getIri(node.getChildren().get(0));
        SignProperty property = PhraseVocabulary.REGISTER.getProperty(propertyIri);
        if (property == null) {
            logger.error("Sign property " + propertyIri + " is unknown.");
            TextContext context = input.getContext(node.getChildren().get(0).getSpan());
            logger.error(context.getContent());
            logger.error(context.getPointer());
            return;
        }
        boolean isPositive = node.getChildren().get(1).getSymbol().getID() == DenotationLexer.ID.OP_EQ;
        Object value = loadSignPropertyValue(node.getChildren().get(2), property);
        if (value == null) {
            logger.error("Failed to load value " + node.getChildren().get(2).getValue());
            TextContext context = input.getContext(node.getChildren().get(2).getSpan());
            logger.error(context.getContent());
            logger.error(context.getPointer());
            return;
        }
        pattern.addPropertiesConstraint(new SignPropertyConstraint(property, value, isPositive));
    }

    /**
     * Loads the value for a property
     *
     * @param node     An AST node
     * @param property The current property
     * @return The value
     */
    private Object loadSignPropertyValue(ASTNode node, SignProperty property) {
        switch (node.getSymbol().getID()) {
            case DenotationLexer.ID.IRIREF:
                return encapsulate(getIriRef(node));
            case DenotationLexer.ID.PNAME_LN:
                return encapsulate(getIriPNameLN(node));
            case DenotationLexer.ID.PNAME_NS:
                return encapsulate(getIriPNameNS(node));
            case DenotationLexer.ID.TRUE:
                return true;
            case DenotationLexer.ID.FALSE:
                return false;
            case DenotationLexer.ID.INTEGER:
                return Integer.parseInt(node.getValue());
            case DenotationLexer.ID.DECIMAL:
                return Double.parseDouble(node.getValue());
            case DenotationLexer.ID.DOUBLE:
                return Double.parseDouble(node.getValue());
            case DenotationLexer.ID.STRING: {
                String value = node.getValue();
                value = value.substring(1, value.length() - 1);
                return TextUtils.unescape(value);
            }
            case DenotationParser.ID.json_object:
                return property.deserializeValueJson(node);
        }
        return null;
    }

    /**
     * Encapsulate an IRI
     *
     * @param iri The iri value
     * @return The IRI object
     */
    private IRI encapsulate(String iri) {
        if (iri == null)
            return null;
        IRI result = Owl2Factory.newIRI();
        result.setHasValue(iri);
        return result;
    }

    /**
     * Loads the relation constraint for a sign pattern
     *
     * @param node           An AST node
     * @param pattern        The pattern to load
     * @param mapAntecedents The current antecedents
     */
    private void loadSignPatternRelation(ASTNode node, SignPattern pattern, Map<String, SignAntecedent> mapAntecedents) {
        String relationIri = getIri(node.getChildren().get(0));
        SignRelation relation = PhraseVocabulary.REGISTER.getRelation(relationIri);
        if (relation == null) {
            logger.error("Sign relation " + relationIri + " is unknown.");
            TextContext context = input.getContext(node.getChildren().get(0).getSpan());
            logger.error(context.getContent());
            logger.error(context.getPointer());
            return;
        }

        if (node.getChildren().get(1).getSymbol().getID() == DenotationLexer.ID.VARIABLE) {
            // a sign pattern
            String signId = node.getChildren().get(1).getValue().substring(1);
            SignAntecedent related = mapAntecedents.get(signId);
            if (related == null) {
                logger.error("Sign pattern " + signId + " is unknown.");
                TextContext context = input.getContext(node.getChildren().get(1).getSpan());
                logger.error(context.getContent());
                logger.error(context.getPointer());
                return;
            }
            pattern.addRelationConstraint(new SignRelationConstraint(relation, related));
        } else {
            // an explicit sign
            String signIri = getIri(node.getChildren().get(1));
            SignAntecedent related = mapAntecedents.get(signIri);
            if (related == null) {
                related = new SignReference(signIri);
                mapAntecedents.put(signIri, related);
            }
            pattern.addRelationConstraint(new SignRelationConstraint(relation, related));
        }
    }

    /**
     * Loads the binding of sign pattern
     *
     * @param node           An AST node
     * @param pattern        The pattern to load
     * @param mapConsequents The current consequents
     */
    private void loadSignPatternBinding(ASTNode node, SignPattern pattern, Map<String, SemeConsequent> mapConsequents) {
        if (node.getChildren().isEmpty())
            return;
        ASTNode nodeChild = node.getChildren().get(0);
        if (nodeChild.getSymbol().getID() == DenotationLexer.ID.VARIABLE) {
            // bound to seme to match
            String semeId = nodeChild.getValue().substring(1);
            SemeConsequent consequent = mapConsequents.get(semeId);
            SemeMatched matched;
            if (consequent == null) {
                matched = new SemeMatched(semeId);
                mapConsequents.put(semeId, matched);
            } else {
                matched = (SemeMatched) consequent;
            }
            pattern.setBoundSeme(matched);
        } else {
            // bound to a static seme
            String semeIri = getIri(nodeChild);
            SemeConsequent consequent = mapConsequents.get(semeIri);
            SemeStatic semeStatic;
            if (consequent == null) {
                semeStatic = new SemeStatic(semeIri);
                mapConsequents.put(semeIri, semeStatic);
            } else {
                semeStatic = (SemeStatic) consequent;
            }
            pattern.setBoundSeme(semeStatic);
        }
    }

    /**
     * Loads the initial definition of a seme
     *
     * @param node           An AST node
     * @param mapConsequents The current consequents
     * @return The seme
     */
    private SemeConsequent loadSemeInitial(ASTNode node, Map<String, SemeConsequent> mapConsequents) {
        if (node.getSymbol().getID() == DenotationParser.ID.seme_static) {
            String iri = getIri(node.getChildren().get(0));
            SemeConsequent result = mapConsequents.get(iri);
            if (result != null)
                return result;
            result = new SemeStatic(iri);
            mapConsequents.put(iri, result);
            return result;
        } else if (node.getSymbol().getID() == DenotationParser.ID.seme_matched) {
            String identifier = node.getChildren().get(0).getValue().substring(1);
            SemeConsequent result = mapConsequents.get(identifier);
            if (result == null || !(result instanceof SemeMatched)) {
                logger.error("Matched seme " + identifier + " is unknown.");
                TextContext context = input.getContext(node.getChildren().get(1).getSpan());
                logger.error(context.getContent());
                logger.error(context.getPointer());
                return null;
            }
            return result;
        } else {
            // a seme template
            String typeIri = getIri(node.getChildren().get(0));
            String alias;
            if (!node.getChildren().get(1).getChildren().isEmpty())
                alias = node.getChildren().get(1).getChildren().get(0).getValue().substring(1);
            else {
                alias = "__seme" + semeCounter;
                semeCounter++;
            }
            SemeConsequent result = new SemeTemplate(alias, typeIri);
            mapConsequents.put(alias, result);
            return result;
        }
    }

    /**
     * Loads the property of a seme consequent
     *
     * @param node           An AST node
     * @param consequent     The seme consequent to load
     * @param mapAntecedents The current antecedents
     * @param mapConsequents The current consequents
     */
    private void loadSemeConsequentProperties(ASTNode node, SemeConsequent consequent, Map<String, SignAntecedent> mapAntecedents, Map<String, SemeConsequent> mapConsequents) {
        for (ASTNode nodeProperty : node.getChildren().get(node.getChildren().size() - 2).getChildren()) {
            loadSemeConsequentProperty(nodeProperty, consequent, mapAntecedents, mapConsequents);
        }
    }

    /**
     * Loads the property of a seme consequent
     *
     * @param node           An AST node
     * @param consequent     The seme consequent to load
     * @param mapAntecedents The current antecedents
     * @param mapConsequents The current consequents
     */
    private void loadSemeConsequentProperty(ASTNode node, SemeConsequent consequent, Map<String, SignAntecedent> mapAntecedents, Map<String, SemeConsequent> mapConsequents) {
        String propertyIri = getIri(node.getChildren().get(0));
        SemeTemplateProperty property = loadSemeConsequentPropertyValue(node.getChildren().get(1), propertyIri, mapAntecedents, mapConsequents);
        if (property != null)
            consequent.addProperty(property);
    }

    /**
     * Loads the value of a property of a seme consequent
     *
     * @param node           An AST node
     * @param propertyIri    The iri for the property
     * @param mapAntecedents The current antecedents
     * @param mapConsequents The current consequents
     */
    private SemeTemplateProperty loadSemeConsequentPropertyValue(ASTNode node, String propertyIri, Map<String, SignAntecedent> mapAntecedents, Map<String, SemeConsequent> mapConsequents) {
        switch (node.getSymbol().getID()) {
            case DenotationLexer.ID.IRIREF: {
                String value = getIriRef(node);
                return new SemeTemplatePropertyIRI(propertyIri, value);
            }
            case DenotationLexer.ID.PNAME_LN: {
                String value = getIriPNameLN(node);
                return new SemeTemplatePropertyIRI(propertyIri, value);
            }
            case DenotationLexer.ID.PNAME_NS: {
                String value = getIriPNameNS(node);
                return new SemeTemplatePropertyIRI(propertyIri, value);
            }
            case DenotationLexer.ID.TRUE:
                return new SemeTemplatePropertyLiteral(propertyIri, true);
            case DenotationLexer.ID.FALSE:
                return new SemeTemplatePropertyLiteral(propertyIri, false);
            case DenotationLexer.ID.INTEGER:
                return new SemeTemplatePropertyLiteral(propertyIri, Integer.parseInt(node.getValue()));
            case DenotationLexer.ID.DECIMAL:
                return new SemeTemplatePropertyLiteral(propertyIri, Double.parseDouble(node.getValue()));
            case DenotationLexer.ID.DOUBLE:
                return new SemeTemplatePropertyLiteral(propertyIri, Double.parseDouble(node.getValue()));
            case DenotationLexer.ID.STRING: {
                String value = TextUtils.unescape(node.getValue());
                value = value.substring(1, value.length() - 1);
                return new SemeTemplatePropertyLiteral(propertyIri, value);
            }
            case DenotationLexer.ID.VARIABLE: {
                String templateId = node.getValue().substring(1);
                SemeConsequent referenced = mapConsequents.get(templateId);
                if (referenced == null) {
                    logger.error("Seme template " + templateId + " is unknown.");
                    TextContext context = input.getContext(node.getSpan());
                    logger.error(context.getContent());
                    logger.error(context.getPointer());
                    return null;
                }
                return new SemeTemplatePropertySemeRef(propertyIri, referenced);
            }
            case DenotationLexer.ID.OP_MEMBER: {
                String signId = node.getChildren().get(0).getValue().substring(1);
                SignAntecedent sign = mapAntecedents.get(signId);
                if (sign == null) {
                    logger.error("Sign pattern " + signId + " is unknown.");
                    TextContext context = input.getContext(node.getChildren().get(1).getSpan());
                    logger.error(context.getContent());
                    logger.error(context.getPointer());
                    return null;
                }
                String signProperty = getIri(node.getChildren().get(1));
                SignProperty property = PhraseVocabulary.REGISTER.getProperty(signProperty);
                if (property == null) {
                    logger.error("Sign property " + signProperty + " is unknown.");
                    TextContext context = input.getContext(node.getChildren().get(1).getSpan());
                    logger.error(context.getContent());
                    logger.error(context.getPointer());
                    return null;
                }
                return new SemeTemplatePropertySignProperty(propertyIri, sign, property);
            }
        }
        return null;
    }

    /**
     * Loads the bindings of a seme consequent
     *
     * @param node           An AST node
     * @param consequent     The seme consequent to load
     * @param mapAntecedents The current antecedents
     */
    private void loadSemeConsequentBindings(ASTNode node, SemeConsequent consequent, Map<String, SignAntecedent> mapAntecedents) {
        for (ASTNode nodeBinding : node.getChildren().get(node.getChildren().size() - 1).getChildren()) {
            loadSemeConsequentBinding(nodeBinding, consequent, mapAntecedents);
        }
    }

    /**
     * Loads the binding of a seme consequent
     *
     * @param node           An AST node
     * @param consequent     The seme consequent to load
     * @param mapAntecedents The current antecedents
     */
    private void loadSemeConsequentBinding(ASTNode node, SemeConsequent consequent, Map<String, SignAntecedent> mapAntecedents) {
        if (node.getSymbol().getID() == DenotationLexer.ID.VARIABLE) {
            String signId = node.getValue().substring(1);
            SignAntecedent pattern = mapAntecedents.get(signId);
            if (pattern == null) {
                logger.error("Sign pattern " + signId + " is unknown.");
                TextContext context = input.getContext(node.getChildren().get(1).getSpan());
                logger.error(context.getContent());
                logger.error(context.getPointer());
            }
            consequent.addBindings(pattern);
        } else {
            // an explicit iri
            String signId = getIri(node);
            SignAntecedent antecedent = mapAntecedents.get(signId);
            if (antecedent == null) {
                antecedent = new SignReference(signId);
                mapAntecedents.put(signId, antecedent);
            }
            consequent.addBindings(antecedent);
        }
    }

    /**
     * Loads an IRI
     *
     * @param node An AST node
     * @return The IRI
     */
    private String getIri(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case DenotationLexer.ID.IRIREF:
                return getIriRef(node);
            case DenotationLexer.ID.PNAME_LN:
                return getIriPNameLN(node);
            case DenotationLexer.ID.PNAME_NS:
                return getIriPNameNS(node);
        }
        return null;
    }

    /**
     * Gets the RDF IRI equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF IRI
     */
    private String getIriRef(ASTNode node) {
        String value = node.getValue();
        value = TextUtils.unescape(value.substring(1, value.length() - 1));
        return value;
    }

    /**
     * Gets the RDF IRI equivalent to the specified AST node (local name)
     *
     * @param node An AST node
     * @return The equivalent RDF IRI
     */
    private String getIriPNameLN(ASTNode node) {
        String value = node.getValue();
        value = TextUtils.unescape(value);
        int index = 0;
        while (index != value.length()) {
            if (value.charAt(index) == ':') {
                String prefix = value.substring(0, index);
                String uri = namespaces.get(prefix);
                if (uri != null) {
                    String name = value.substring(index + 1);
                    return URIUtils.resolveRelative(baseURI, uri + name);
                }
            }
            index++;
        }
        return null;
    }

    /**
     * Gets the RDF IRI equivalent to the specified AST node (namespace)
     *
     * @param node An AST node
     * @return The equivalent RDF IRI
     */
    private String getIriPNameNS(ASTNode node) {
        String value = node.getValue();
        value = TextUtils.unescape(value.substring(0, value.length() - 1));
        return namespaces.get(value);
    }
}
