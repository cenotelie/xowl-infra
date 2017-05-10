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
import org.xowl.infra.store.Vocabulary;
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
        Map<String, SignPattern> signPatterns = new HashMap<>();
        Map<String, SemeTemplate> semeTemplates = new HashMap<>();

        if (node.getChildren().get(1).getSymbol().getID() == DenotationParser.ID.sign_specific) {
            rule.addAntecedent(loadSignSpecificReference(node.getChildren().get(1)));
        } else {
            // sign patterns
            SignPattern[] patterns = new SignPattern[node.getChildren().get(1).getChildren().size()];
            int i = 0;
            for (ASTNode nodePattern : node.getChildren().get(1).getChildren()) {
                SignPattern pattern = loadSignPatternInitial(nodePattern);
                patterns[i++] = pattern;
                rule.addAntecedent(pattern);
                signPatterns.put(pattern.getIdentifier(), pattern);
            }
            i = 0;
            for (ASTNode nodePattern : node.getChildren().get(1).getChildren()) {
                SignPattern pattern = patterns[i++];
                loadSignPatternContent(nodePattern, pattern, signPatterns);
                if (pattern.getBoundSeme() != null)
                    semeTemplates.put(pattern.getBoundSeme(), new SemeTemplate(pattern.getBoundSeme(), Vocabulary.owlThing));
            }
        }

        SemeTemplate[] templates = new SemeTemplate[node.getChildren().get(2).getChildren().size()];
        int i = 0;
        for (ASTNode nodeSeme : node.getChildren().get(2).getChildren()) {
            DenotationRuleConsequent consequent = loadSemeInitial(nodeSeme, signPatterns);
            rule.addConsequent(consequent);
            if (consequent instanceof SemeTemplate) {
                SemeTemplate template = (SemeTemplate) consequent;
                templates[i++] = template;
                semeTemplates.put(template.getIdentifier(), template);
            }
        }
        i = 0;
        for (ASTNode nodeSeme : node.getChildren().get(2).getChildren()) {
            if (nodeSeme.getSymbol().getID() == DenotationParser.ID.seme_pattern) {
                SemeTemplate template = templates[i++];
                for (ASTNode nodeProperty : nodeSeme.getChildren().get(2).getChildren()) {
                    loadSemeTemplateProperty(nodeProperty, template, signPatterns, semeTemplates);
                }
            }
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
     * @param node         An AST node
     * @param pattern      The pattern to load
     * @param signPatterns The dictionary of sign patterns
     */
    private void loadSignPatternContent(ASTNode node, SignPattern pattern, Map<String, SignPattern> signPatterns) {
        for (ASTNode nodeProperty : node.getChildren().get(1).getChildren()) {
            loadSignPatternProperty(nodeProperty, pattern);
        }
        for (ASTNode nodeRelation : node.getChildren().get(2).getChildren()) {
            loadSignPatternRelation(nodeRelation, pattern, signPatterns);
        }
        if (!node.getChildren().get(3).getChildren().isEmpty()) {
            String semeId = node.getChildren().get(3).getChildren().get(0).getValue().substring(1);
            pattern.setBoundSeme(semeId);
        }
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
        }
        boolean isPositive = node.getChildren().get(1).getSymbol().getID() == DenotationLexer.ID.OP_EQ;
        Object value = loadSignPropertyValue(node.getChildren().get(2), property);
        if (value == null) {
            logger.error("Failed to load value " + node.getChildren().get(2).getValue());
            TextContext context = input.getContext(node.getChildren().get(2).getSpan());
            logger.error(context.getContent());
            logger.error(context.getPointer());
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
     * @param node         An AST node
     * @param pattern      The pattern to load
     * @param signPatterns The dictionary of sign patterns
     */
    private void loadSignPatternRelation(ASTNode node, SignPattern pattern, Map<String, SignPattern> signPatterns) {
        String relationIri = getIri(node.getChildren().get(0));
        String signId = node.getChildren().get(1).getValue().substring(1);
        SignRelation relation = PhraseVocabulary.REGISTER.getRelation(relationIri);
        SignPattern related = signPatterns.get(signId);
        if (relation == null) {
            logger.error("Sign relation " + relationIri + " is unknown.");
            TextContext context = input.getContext(node.getChildren().get(0).getSpan());
            logger.error(context.getContent());
            logger.error(context.getPointer());
        }
        if (related == null) {
            logger.error("Sign pattern " + signId + " is unknown.");
            TextContext context = input.getContext(node.getChildren().get(1).getSpan());
            logger.error(context.getContent());
            logger.error(context.getPointer());
        }
        pattern.addRelationConstraint(new SignRelationConstraint(relation, related));
    }

    /**
     * Loads the initial definition of a seme
     *
     * @param node         An AST node
     * @param signPatterns The current sign patterns
     * @return The seme
     */
    private DenotationRuleConsequent loadSemeInitial(ASTNode node, Map<String, SignPattern> signPatterns) {
        DenotationRuleConsequent result;

        if (node.getSymbol().getID() == DenotationParser.ID.seme_explicit) {
            String iri = getIri(node.getChildren().get(0));
            result = new SemeExplicit(iri);
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
            result = new SemeTemplate(alias, typeIri);
        }

        for (ASTNode nodeBinding : node.getChildren().get(node.getChildren().size() - 1).getChildren()) {
            if (nodeBinding.getSymbol().getID() == DenotationLexer.ID.VARIABLE) {
                String signId = nodeBinding.getValue().substring(1);
                SignPattern pattern = signPatterns.get(signId);
                if (pattern == null) {
                    logger.error("Sign pattern " + signId + " is unknown.");
                    TextContext context = input.getContext(node.getChildren().get(1).getSpan());
                    logger.error(context.getContent());
                    logger.error(context.getPointer());
                }
                result.addBindings(pattern);
            } else if (nodeBinding.getSymbol().getID() == DenotationLexer.ID.STRING) {
                // an explicit sign
                String signId = nodeBinding.getValue().substring(1, nodeBinding.getValue().length() - 1);
                signId = TextUtils.unescape(signId);
                result.addBindings(new SignReference(signId));
            }
        }
        return result;
    }

    /**
     * Loads the property of a seme template
     *
     * @param node          An AST node
     * @param template      The seme template to load
     * @param signPatterns  The dictionary of known sign patterns
     * @param semeTemplates The dictionary of seme templates
     */
    private void loadSemeTemplateProperty(ASTNode node, SemeTemplate template, Map<String, SignPattern> signPatterns, Map<String, SemeTemplate> semeTemplates) {
        String propertyIri = getIri(node.getChildren().get(0));
        SemeTemplateProperty property = loadSemeTemplatePropertyValue(node.getChildren().get(2), propertyIri, signPatterns, semeTemplates);
        if (property != null)
            template.addProperty(property);
    }

    /**
     * Loads the value of a property of a seme template
     *
     * @param node          An AST node
     * @param propertyIri   The iri for the property
     * @param signPatterns  The dictionary of known sign patterns
     * @param semeTemplates The dictionary of seme templates
     */
    private SemeTemplateProperty loadSemeTemplatePropertyValue(ASTNode node, String propertyIri, Map<String, SignPattern> signPatterns, Map<String, SemeTemplate> semeTemplates) {
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
                SemeTemplate referenced = semeTemplates.get(templateId);
                if (referenced == null) {
                    logger.error("Seme template " + templateId + " is unknown.");
                    TextContext context = input.getContext(node.getSpan());
                    logger.error(context.getContent());
                    logger.error(context.getPointer());
                    return null;
                }
                return new SemeTemplatePropertyTemplate(propertyIri, referenced);
            }
            case DenotationLexer.ID.OP_MEMBER: {
                String signId = node.getChildren().get(0).getValue().substring(1);
                SignPattern sign = signPatterns.get(signId);
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
