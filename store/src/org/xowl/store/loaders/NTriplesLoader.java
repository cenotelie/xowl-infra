/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 **********************************************************************/

package org.xowl.store.loaders;

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.rdf.*;
import org.xowl.store.voc.OWLDatatype;
import org.xowl.utils.Files;
import org.xowl.utils.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Loader for N-Triples sources
 *
 * @author Laurent Wouters
 */
public class NTriplesLoader implements Loader {
    private static final String defaultRDFGraphs = "http://www.org.xowl.org/interpreter/rdfgraphs/";

    /**
     * The RDF graph to load in
     */
    private RDFGraph graph;
    /**
     * The current ontology
     */
    private Ontology ontology;
    /**
     * Maps of blanks nodes
     */
    private Map<String, RDFBlankNode> blanks;

    /**
     * Initializes this loader
     *
     * @param graph The RDF graph to load in
     */
    public NTriplesLoader(RDFGraph graph) {
        this.graph = graph;
        this.blanks = new HashMap<>();
    }

    /**
     * Gets the current ontology
     *
     * @return The current ontology
     */
    public Ontology getOntology() {
        return ontology;
    }

    @Override
    public void load(Logger logger, String name, Reader reader) {
        java.util.Random rand = new java.util.Random();
        String value = defaultRDFGraphs + Integer.toHexString(rand.nextInt());
        IRI iri = new IRI();
        iri.setHasValue(value);
        ontology = new Ontology();
        ontology.setHasIRI(iri);

        ParseResult result = null;
        try {
            String content = Files.read(reader);
            NTriplesLexer lexer = new NTriplesLexer(content);
            NTriplesParser parser = new NTriplesParser(lexer);
            result = parser.parse();
        } catch (IOException ex) {
            logger.error(ex);
            return;
        }
        if (result == null)
            return;
        for (ParseError error : result.getErrors()) {
            logger.error(error);
            String[] context = result.getInput().getContext(error.getPosition());
            logger.error(context[0]);
            logger.error(context[1]);
        }
        if (!result.isSuccess())
            return;
        for (ASTNode triple : result.getRoot().getChildren()) {
            RDFNode n1 = getRDFNode(triple.getChildren().get(0));
            RDFNode n2 = getRDFNode(triple.getChildren().get(1));
            RDFNode n3 = getRDFNode(triple.getChildren().get(2));
            try {
                graph.add(ontology, (RDFSubjectNode) n1, (RDFProperty) n2, n3);
            } catch (UnsupportedNodeType ex) {
                // cannot happen
            }
        }
    }

    /**
     * Gets the RDF node represented by the specified AST node
     *
     * @param node An AST node representing an RDF node
     * @return The represented RDF node
     */
    private RDFNode getRDFNode(ASTNode node) {
        String value = node.getSymbol().getValue();
        switch (node.getSymbol().getID()) {
            case NTriplesLexer.ID.FULLIRI:
                return graph.getNodeIRI(value.substring(1, value.length() - 1));
            case NTriplesLexer.ID.NODEID:
                String id = value.substring(2);
                RDFBlankNode blank = blanks.get(id);
                if (blank != null)
                    return blank;
                blank = graph.getBlankNode();
                blanks.put(id, blank);
                return blank;
            case NTriplesLexer.ID.LIT_STRING:
                return graph.getLiteralNode(unescape(value), OWLDatatype.xsdString);
            case NTriplesLexer.ID.LIT_TYPED:
                int index = value.lastIndexOf("^^");
                return graph.getLiteralNode(unescape(value.substring(0, index + 1)), value.substring(index + 3, value.length() - 1));
            case NTriplesLexer.ID.LIT_LING:
                index = value.lastIndexOf("@");
                return graph.getLiteralNode(unescape(value.substring(0, index + 1)), OWLDatatype.xsdString);
            case NTriplesLexer.ID.LIT_NUM:
                if (value.contains("."))
                    return graph.getLiteralNode(value, OWLDatatype.xsdFloat);
                else
                    return graph.getLiteralNode(value, OWLDatatype.xsdInteger);
        }
        return null;
    }

    private String unescape(String content) {
        char[] buffer = new char[content.length() - 2];
        int next = 0;
        for (int i = 1; i != content.length() - 1; i++) {
            char c = content.charAt(i);
            if (c != '\\') {
                buffer[next++] = c;
            } else {
                char n = content.charAt(i + 1);
                if (n == 't') {
                    buffer[next++] = '\t';
                    i++;
                } else if (n == 'n') {
                    buffer[next++] = '\n';
                    i++;
                } else if (n == 'r') {
                    buffer[next++] = '\r';
                    i++;
                } else if (n == '\\') {
                    buffer[next++] = '\\';
                    i++;
                } else if (n == '"') {
                    buffer[next++] = '"';
                    i++;
                } else if (n == 'u') {
                    int codepoint = Integer.parseInt(content.substring(i + 2, i + 6), 16);
                    String str = new String(new int[]{codepoint}, 0, 1);
                    for (int j = 0; j != str.length(); j++)
                        buffer[next++] = str.charAt(j);
                    i += 5;
                } else if (n == 'U') {
                    int codepoint = Integer.parseInt(content.substring(i + 2, i + 10), 16);
                    String str = new String(new int[]{codepoint}, 0, 1);
                    for (int j = 0; j != str.length(); j++)
                        buffer[next++] = str.charAt(j);
                    i += 9;
                }
            }
        }
        return new String(buffer, 0, next - 1);
    }
}