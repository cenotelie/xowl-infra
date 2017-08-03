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

package org.xowl.infra.lsp.xowlserver;

import fr.cenotelie.hime.redist.Text;
import fr.cenotelie.hime.redist.TextPosition;
import org.xowl.infra.lsp.engine.*;
import org.xowl.infra.lsp.structures.Diagnostic;
import org.xowl.infra.lsp.structures.DiagnosticSeverity;
import org.xowl.infra.lsp.structures.Position;
import org.xowl.infra.lsp.structures.Range;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.loaders.RDFXMLLoader;
import org.xowl.infra.store.rdf.IRINode;
import org.xowl.infra.store.rdf.LiteralNode;
import org.xowl.infra.store.rdf.SubjectNode;
import org.xowl.infra.utils.Identifiable;
import org.xowl.infra.utils.collections.Couple;
import org.xowl.infra.utils.xml.Xml;
import org.xowl.infra.utils.xml.XmlElement;

import java.util.*;

/**
 * The analyzer for the rdf-xml language
 *
 * @author Laurent Wouters
 */
public class XowlLsRdfXmlAnalyzer implements Identifiable, DocumentAnalyzer {
    /**
     * The context for this analyzer
     */
    private static class Context extends XowlLsAnalysisContext {
        /**
         * List of all the known IDs so far
         */
        public final List<String> knownIDs;

        /**
         * Initializes this context
         *
         * @param resourceUri The URI of the resource
         * @param input       The text input that was parsed
         * @param factory     The factory for symbols
         * @param symbols     The symbols for the current document
         * @param diagnostics The buffer for diagnostics
         */
        public Context(String resourceUri, Text input, SymbolFactory factory, DocumentSymbols symbols, Collection<Diagnostic> diagnostics) {
            super(resourceUri, input, factory, symbols, diagnostics);
            this.knownIDs = new ArrayList<>();
        }
    }

    @Override
    public String getIdentifier() {
        return XowlLsRdfXmlAnalyzer.class.getCanonicalName();
    }

    @Override
    public String getName() {
        return "RDF (XML) Analyzer";
    }

    @Override
    public int getPriorityFor(Document document) {
        if (Objects.equals(document.getLanguageId(), "rdf-xml"))
            return PRIORITY_HIGH;
        return PRIORITY_NONE;
    }

    @Override
    public DocumentAnalysis analyze(SymbolFactory factory, Document document) {
        org.w3c.dom.Document xmlDocument;
        try {
            xmlDocument = Xml.parse(document.getCurrentVersion().getContent().getReader());
        } catch (Exception exception) {
            return new DocumentAnalysis(null, new Diagnostic[]{
                    new Diagnostic(
                            new Range(new Position(0, 0), new Position(0, 0)),
                            DiagnosticSeverity.ERROR,
                            CODE_PARSER_FAILURE,
                            getName(),
                            "The analysis failed"
                    )
            });
        }

        Collection<Diagnostic> diagnostics = new ArrayList<>();
        DocumentSymbols symbols = new DocumentSymbols();
        Context context = new Context(document.getUri(), null, factory, symbols, diagnostics);

        XmlElement root = new XmlElement(xmlDocument.getDocumentElement(), document.getUri());
        if (Vocabulary.rdfRDF.equals(root.getNodeIRI()))
            inspectDocument(context, root);
        else
            inspectElement(context, root);
        return new DocumentAnalysis(symbols, diagnostics.toArray(new Diagnostic[diagnostics.size()]));
    }


    /**
     * Inspects the specified document node (rdf:RDF node)
     *
     * @param context The current context
     * @param element A RDF document node
     */
    private void inspectDocument(Context context, XmlElement element) {
        for (XmlElement child : element)
            inspectElement(context, child);
    }

    /**
     * Inspects the specified RDF resource node
     *
     * @param context The current context
     * @param element A RDF resource node
     */
    private void inspectElement(Context context, XmlElement element) {
        if (!RDFXMLLoader.isValidElement(element.getNodeIRI())) {
            context.diagnostics.add(new Diagnostic(
                    getRangeFor(element),
                    DiagnosticSeverity.ERROR,
                    "rdf-xml.0",
                    getName(),
                    "Unexpected resource element " + element.getNodeIRI()
            ));
            return;
        }

        String symbolId = null;

        String attribute = element.getAttribute(Vocabulary.rdfID);
        boolean hasID = false;
        if (attribute != null) {
            if (!RDFXMLLoader.isValidXMLName(attribute)) {
                context.diagnostics.add(new Diagnostic(
                        getRangeFor(element),
                        DiagnosticSeverity.ERROR,
                        "rdf-xml.1",
                        getName(),
                        "Illegal rdf:ID " + attribute
                ));
                return;
            }
            String iri = element.resolve("#" + attribute);
            if (context.knownIDs.contains(iri)) {
                context.diagnostics.add(new Diagnostic(
                        getRangeFor(element),
                        DiagnosticSeverity.ERROR,
                        "rdf-xml.2",
                        getName(),
                        "Duplicate rdf:ID " + iri
                ));
                return;
            }
            context.knownIDs.add(iri);
            symbolId = iri;
            hasID = true;
        }
        attribute = element.getAttribute(Vocabulary.rdfNodeID);
        if (attribute != null) {
            if (hasID) {
                context.diagnostics.add(new Diagnostic(
                        getRangeFor(element),
                        DiagnosticSeverity.ERROR,
                        "rdf-xml.3",
                        getName(),
                        "Node cannot have both rdf:ID and rdf:nodeID attributes"
                ));
                return;
            }
            if (!RDFXMLLoader.isValidXMLName(attribute)) {
                context.diagnostics.add(new Diagnostic(
                        getRangeFor(element),
                        DiagnosticSeverity.ERROR,
                        "rdf-xml.4",
                        getName(),
                        "Illegal rdf:nodeID " + attribute
                ));
                return;
            }
            symbolId = context.resource + "#" + attribute;
        }
        attribute = element.getAttribute(Vocabulary.rdfAbout);
        if (attribute != null) {
            if (hasID) {
                context.diagnostics.add(new Diagnostic(
                        getRangeFor(element),
                        DiagnosticSeverity.ERROR,
                        "rdf-xml.5",
                        getName(),
                        "Node cannot have both rdf:ID and rdf:about attributes"
                ));
                return;
            }
            if (symbolId != null) {
                context.diagnostics.add(new Diagnostic(
                        getRangeFor(element),
                        DiagnosticSeverity.ERROR,
                        "rdf-xml.6",
                        getName(),
                        "Node cannot have both rdf:nodeID and rdf:about attributes"
                ));
                return;
            }
            symbolId = element.resolve(attribute);
        }

        if (symbolId == null)
            symbolId = context.resource + "#" + UUID.randomUUID().toString();
        onSymbol(context, element, symbolId, true);

        if (!Vocabulary.rdfDescription.equals(element.getNodeIRI())) {
            String typeIri = element.getNodeIRI();
            onSymbol(context, element, typeIri, false);
        }

        attribute = element.getAttribute(Vocabulary.rdfType);
        if (attribute != null) {
            String typeIri = element.resolve(attribute);
            onSymbol(context, element, typeIri, false);
        }

        Iterator<Couple<String, String>> attributes = element.getAttributes();
        while (attributes.hasNext()) {
            Couple<String, String> couple = attributes.next();
            if (!RDFXMLLoader.isValidPropertyAttribute(couple.x)) {
                context.diagnostics.add(new Diagnostic(
                        getRangeFor(element),
                        DiagnosticSeverity.ERROR,
                        "rdf-xml.7",
                        getName(),
                        "Unexpected property attribute node " + couple.x
                ));
                return;
            }
            String propertyIri = couple.x;
            onSymbol(context, element, propertyIri, false);
        }

        for (XmlElement child : element)
            inspectElementProperty(context, child);
    }

    /**
     * Inspects the specified RDF property
     *
     * @param context The current context
     * @param element The XML node representing the property
     */
    private void inspectElementProperty(Context context, XmlElement element) {
        if (!RDFXMLLoader.isValidPropertyElement(element.getNodeIRI())) {
            context.diagnostics.add(new Diagnostic(
                    getRangeFor(element),
                    DiagnosticSeverity.ERROR,
                    "rdf-xml.8",
                    getName(),
                    "Unexpected property element node " + element.getNodeIRI()
            ));
            return;
        }

        String attribute = element.getAttribute(Vocabulary.rdfParseType);
        if (attribute == null) {
            if (element.isEmpty()) {
                inspectElementPropertyEmpty(context, element);
            } else {
                Iterator<XmlElement> children = element.getChildren();
                if (children.hasNext()) {
                    inspectElementPropertyResource(context, element);
                } else {
                    inspectElementPropertyLiteral(context, element);
                }
            }
        } else {
            if ("Literal".equals(attribute)) {
                loadElementPropertyLiteralParseType(context, element);
            } else if ("Resource".equals(attribute)) {
                loadElementPropertyResourceParseType(context, element);
            } else if ("Collection".equals(attribute)) {
                loadElementPropertyCollectionParseType(context, element);
            } else {
                loadElementPropertyLiteralParseType(context, element);
            }
        }
    }

    /**
     * Inspects the property IRI node for the specified XML node representing an element property
     *
     * @param element An XML node representing an RDF property
     */
    private void inspectProperty(Context context, XmlElement element) {
        if (Vocabulary.rdfLI.equals(element.getNodeIRI())) {
            int index = element.getIndex();
            String iri = Vocabulary.rdf + "_" + Integer.toString(index);
            onSymbol(context, element, iri, false);
        } else {
            onSymbol(context, element, element.getNodeIRI(), false);
        }
    }

    /**
     * Inspects the specified RDF property pointing to a resource
     *
     * @param context The current context
     * @param element The XML node representing the property
     */
    private void inspectElementPropertyResource(Context context, XmlElement element) {
        inspectProperty(context, element);
        Iterator<XmlElement> children = element.getChildren();
        inspectElement(context, children.next());

        String attribute = element.getAttribute(Vocabulary.rdfID);
        if (attribute != null) {
            if (!RDFXMLLoader.isValidXMLName(attribute)) {
                context.diagnostics.add(new Diagnostic(
                        getRangeFor(element),
                        DiagnosticSeverity.ERROR,
                        "rdf-xml.1",
                        getName(),
                        "Illegal rdf:ID " + attribute
                ));
                return;
            }
            String iri = element.resolve("#" + attribute);
            if (context.knownIDs.contains(iri)) {
                context.diagnostics.add(new Diagnostic(
                        getRangeFor(element),
                        DiagnosticSeverity.ERROR,
                        "rdf-xml.2",
                        getName(),
                        "Duplicate rdf:ID " + iri
                ));
                return;
            }
            onSymbol(context, element, iri, true);
            context.knownIDs.add(iri);
        }
    }

    /**
     * Inspects the specified RDF literal property
     *
     * @param context The current context
     * @param element The XML node representing the property
     */
    private void inspectElementPropertyLiteral(Context context, XmlElement element) {
        inspectProperty(context, element);

        String attribute = element.getAttribute(Vocabulary.rdfID);
        if (attribute != null) {
            if (!RDFXMLLoader.isValidXMLName(attribute)) {
                context.diagnostics.add(new Diagnostic(
                        getRangeFor(element),
                        DiagnosticSeverity.ERROR,
                        "rdf-xml.1",
                        getName(),
                        "Illegal rdf:ID " + attribute
                ));
                return;
            }
            String iri = element.resolve("#" + attribute);
            if (context.knownIDs.contains(iri)) {
                context.diagnostics.add(new Diagnostic(
                        getRangeFor(element),
                        DiagnosticSeverity.ERROR,
                        "rdf-xml.2",
                        getName(),
                        "Duplicate rdf:ID " + iri
                ));
                return;
            }
            onSymbol(context, element, iri, true);
            context.knownIDs.add(iri);
        }
    }

    /**
     * Inspects the specified RDF empty property
     *
     * @param context The current context
     * @param element The XML node representing the property
     */
    private void inspectElementPropertyEmpty(Context context, XmlElement element) {
        inspectProperty(context, element);
        String attributeID = element.getAttribute(Vocabulary.rdfID);
        String attribute;
        Iterator<Couple<String, String>> attributes = element.getAttributes();

        if (!attributes.hasNext()) {
            if (attributeID != null) {
                if (!RDFXMLLoader.isValidXMLName(attributeID)) {
                    context.diagnostics.add(new Diagnostic(
                            getRangeFor(element),
                            DiagnosticSeverity.ERROR,
                            "rdf-xml.1",
                            getName(),
                            "Illegal rdf:ID " + attributeID
                    ));
                    return;
                }
                String iri = element.resolve("#" + attributeID);
                if (context.knownIDs.contains(iri)) {
                    context.diagnostics.add(new Diagnostic(
                            getRangeFor(element),
                            DiagnosticSeverity.ERROR,
                            "rdf-xml.2",
                            getName(),
                            "Duplicate rdf:ID " + iri
                    ));
                    return;
                }
                context.knownIDs.add(iri);
                onSymbol(context, element, attributeID, true);
            }
        } else {
            String value = null;
            attribute = element.getAttribute(Vocabulary.rdfResource);
            if (attribute != null) {
                value = element.resolve(attribute);
            }
            attribute = element.getAttribute(Vocabulary.rdfNodeID);
            if (attribute != null) {
                if (!RDFXMLLoader.isValidXMLName(attribute)) {
                    context.diagnostics.add(new Diagnostic(
                            getRangeFor(element),
                            DiagnosticSeverity.ERROR,
                            "rdf-xml.1",
                            getName(),
                            "Illegal rdf:ID " + attribute
                    ));
                    return;
                }
                if (value != null) {
                    context.diagnostics.add(new Diagnostic(
                            getRangeFor(element),
                            DiagnosticSeverity.ERROR,
                            "rdf-xml.7",
                            getName(),
                            "Node cannot have both rdf:nodeID and rdf:resource attributes"
                    ));
                    return;
                }
                value = context.resource + "#" + attribute;
            }

            if (value == null)
                value = context.resource + "#" + UUID.randomUUID().toString();
            onSymbol(context, element, value, false);

            attribute = element.getAttribute(Vocabulary.rdfType);
            if (attribute != null) {
                String typeIri = element.resolve(attribute);
                onSymbol(context, element, typeIri, false);
            }

            attributes = element.getAttributes();
            while (attributes.hasNext()) {
                Couple<String, String> att = attributes.next();
                if (!RDFXMLLoader.isValidPropertyAttribute(att.x)) {
                    context.diagnostics.add(new Diagnostic(
                            getRangeFor(element),
                            DiagnosticSeverity.ERROR,
                            "rdf-xml.7",
                            getName(),
                            "Unexpected property attribute node " + att.x
                    ));
                    return;
                }
                String subProperty = att.x;
                onSymbol(context, element, subProperty, false);
            }
        }
    }

    /**
     * Loads the specified XML literal RDF property
     *
     * @param element The XML node representing the property
     * @param subject The current RDF subject
     */
    private void loadElementPropertyLiteralParseType(XmlElement element, SubjectNode subject) {
        IRINode property = getProperty(element);
        String attributeID = element.getAttribute(Vocabulary.rdfID);
        Iterator<Couple<String, String>> attributes = element.getAttributes();
        if (attributes.hasNext()) {
            // cannot have any more attribute
            throw new IllegalArgumentException("Unsupported attributes on a literal property node");
        }

        String lexem = element.getXMLLiteral();
        LiteralNode value = store.getLiteralNode(lexem, Vocabulary.rdfXMLLiteral, null);

        register(subject, property, value);
        if (attributeID != null) {
            // reify the triple
            IRINode proxy = store.getIRINode(element.resolve("#" + attributeID));
            register(proxy, Vocabulary.rdfType, store.getIRINode(Vocabulary.rdfStatement));
            register(proxy, Vocabulary.rdfSubject, subject);
            register(proxy, Vocabulary.rdfPredicate, property);
            register(proxy, Vocabulary.rdfObject, value);
        }
    }

    /**
     * Loads the specified resource RDF property
     *
     * @param element The XML node representing the property
     * @param subject The current RDF subject
     */
    private void loadElementPropertyResourceParseType(XmlElement element, SubjectNode subject) {
        IRINode property = getProperty(element);
        SubjectNode value = store.getBlankNode();
        register(subject, property, value);
        String attributeID = element.getAttribute(Vocabulary.rdfID);
        if (attributeID != null) {
            if (!RDFXMLLoader.isValidXMLName(attributeID))
                throw new IllegalArgumentException("Illegal rdf:ID " + attributeID);
            String iri = element.resolve("#" + attributeID);
            if (context.knownIDs.contains(iri))
                throw new IllegalArgumentException("Duplicate rdf:ID " + iri);
            context.knownIDs.add(iri);
            // reify the triple
            IRINode proxy = store.getIRINode(iri);
            register(proxy, Vocabulary.rdfType, store.getIRINode(Vocabulary.rdfStatement));
            register(proxy, Vocabulary.rdfSubject, subject);
            register(proxy, Vocabulary.rdfPredicate, property);
            register(proxy, Vocabulary.rdfObject, value);
        }

        for (XmlElement child : element)
            inspectElementProperty(child, value);
    }

    /**
     * Loads the specified collection RDF property
     *
     * @param element The XML node representing the property
     * @param subject The current RDF subject
     */
    private void loadElementPropertyCollectionParseType(XmlElement element, SubjectNode subject) {
        IRINode property = getProperty(element);
        String attributeID = element.getAttribute(Vocabulary.rdfID);
        Iterator<XmlElement> children = element.getChildren();

        SubjectNode head;
        if (!children.hasNext()) {
            // no children
            head = store.getIRINode(Vocabulary.rdfNil);
        } else {
            head = store.getBlankNode();
        }
        register(subject, property, head);
        if (attributeID != null) {
            if (!RDFXMLLoader.isValidXMLName(attributeID))
                throw new IllegalArgumentException("Illegal rdf:ID " + attributeID);
            String iri = element.resolve("#" + attributeID);
            if (context.knownIDs.contains(iri))
                throw new IllegalArgumentException("Duplicate rdf:ID " + iri);
            context.knownIDs.add(iri);
            // reify the triple
            IRINode proxy = store.getIRINode(iri);
            register(proxy, Vocabulary.rdfType, store.getIRINode(Vocabulary.rdfStatement));
            register(proxy, Vocabulary.rdfSubject, subject);
            register(proxy, Vocabulary.rdfPredicate, property);
            register(proxy, Vocabulary.rdfObject, head);
        }

        List<SubjectNode> values = new ArrayList<>();
        while (children.hasNext())
            values.add(inspectElement(children.next()));
        for (int i = 0; i != values.size() - 1; i++) {
            register(head, Vocabulary.rdfFirst, values.get(i));
            SubjectNode next = store.getBlankNode();
            register(head, Vocabulary.rdfRest, next);
            head = next;
        }
        register(head, Vocabulary.rdfFirst, values.get(values.size() - 1));
        register(head, Vocabulary.rdfRest, store.getIRINode(Vocabulary.rdfNil));
    }

    /**
     * When a symbol is found
     *
     * @param context      The current context
     * @param element      The XML element for the element
     * @param identifier   The identifier for the symbol
     * @param isDefinition Whether this is a subject (definition)
     */
    private void onSymbol(Context context, XmlElement element, String identifier, boolean isDefinition) {
        Symbol symbol = context.factory.resolve(identifier);
        if (symbol.getKind() == 0)
            symbol.setKind(XowlLsWorkspace.SYMBOL_ENTITY);
        if (isDefinition)
            context.symbols.addDefinition(new DocumentSymbolReference(
                    symbol,
                    getRangeFor(element)));
        else
            context.symbols.addReference(new DocumentSymbolReference(
                    symbol,
                    getRangeFor(element)));
    }

    /**
     * Gets the range for the XML element
     *
     * @param element The XML element
     * @return The corresponding range
     */
    private Range getRangeFor(XmlElement element) {
        TextPosition start = element.getPositionOpeningStart();
        TextPosition end = element.getPositionOpeningEnd();
        return new Range(
                new Position(start.getLine() - 1, start.getColumn() - 1),
                new Position(end.getLine() - 1, end.getColumn() - 1)
        );
    }
}
