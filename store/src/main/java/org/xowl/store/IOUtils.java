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

package org.xowl.store;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.store.owl.AnonymousNode;
import org.xowl.store.rdf.*;
import org.xowl.store.sparql.Result;
import org.xowl.store.storage.NodeManager;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility APIs for reading and writing data
 *
 * @author Laurent Wouters
 */
public class IOUtils {
    /**
     * String containing the escaped glyphs in absolute uris
     */
    private static final String ESCAPED_GLYPHS_ABSOLUTE_URIS = "<>\"{}|^`\\";

    /**
     * Replaces special sequences in the specified input value by the corresponding value.
     * This method is general purpose in that it supports all form of escape sequences used by various syntaxes.
     * The double double-quote escape sequence ("") representing a single double-quote character (") for the CSV syntax is not supported.
     * The supported escape sequences:
     * - \ u XXXX for unicode characters in the BMP with codepoint XXXX.
     * - \ U XXXXXXXX for unicode characters outside the BMP with codepoint XXXXXXXX.
     * - \t, \b, \r, \n, \f for the corresponding control characters (tab, backspace, carriage return, line feed, form feed).
     * - \C for C, where C is any character other than t, b, r, n, f, u and U.
     *
     * @param value A string that can contain escape sequences
     * @return The equivalent string with the escape sequences replaced by their value
     */
    public static String unescape(String value) {
        char[] buffer = new char[value.length()];
        int next = 0;
        for (int i = 0; i != value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\') {
                char n = value.charAt(i + 1);
                if (n == 't') {
                    buffer[next++] = '\t';
                    i++;
                } else if (n == 'b') {
                    buffer[next++] = '\b';
                    i++;
                } else if (n == 'n') {
                    buffer[next++] = '\n';
                    i++;
                } else if (n == 'r') {
                    buffer[next++] = '\r';
                    i++;
                } else if (n == 'f') {
                    buffer[next++] = '\f';
                    i++;
                } else if (n == 'u') {
                    // \ u XXXX for unicode characters in the BMP
                    // note that any unicode character is encoded in UTF-16 in at most 2 Java char
                    // therefore the length of str cannot be more that 2
                    // therefore buffer[next++] cannot overflow
                    int codepoint = Integer.parseInt(value.substring(i + 2, i + 6), 16);
                    String str = new String(new int[]{codepoint}, 0, 1);
                    for (int j = 0; j != str.length(); j++)
                        buffer[next++] = str.charAt(j);
                    i += 5;
                } else if (n == 'U') {
                    // \ U XXXXXXXX for unicode characters outside the BMP
                    // note that any unicode character is encoded in UTF-16 in at most 2 Java char
                    // therefore the length of str cannot be more that 2
                    // therefore buffer[next++] cannot overflow
                    int codepoint = Integer.parseInt(value.substring(i + 2, i + 10), 16);
                    String str = new String(new int[]{codepoint}, 0, 1);
                    for (int j = 0; j != str.length(); j++)
                        buffer[next++] = str.charAt(j);
                    i += 9;
                } else {
                    // \C for C, where C is any character other than t, b, r, n, f, u and U
                    buffer[next++] = n;
                    i++;
                }
            } else {
                // not the start of an escape sequence, replace as is
                buffer[next++] = c;
            }
        }
        return new String(buffer, 0, next);
    }

    /**
     * Escapes special characters in the specified absolute URI according to the common W3C requirements for Turtle, N-Triples, N-quads, etc.
     * All characters are copied as-is, except for the following, which are changed for a unicode escape sequence \ u XXXX:
     * characters in range U+0000 to U+0020 and &lt;, &gt;, ", {, }, |, ^, `, \.
     * This method assumes that the result will be surrounded with angle brackets (&lt; and &gt;).
     *
     * @param value The absolute URI to escape
     * @return The escaped URI
     */
    public static String escapeAbsoluteURIW3C(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != value.length(); i++) {
            char c = value.charAt(i);
            if (c < 0x20 || ESCAPED_GLYPHS_ABSOLUTE_URIS.contains(Character.toString(c))) {
                String s = Integer.toHexString(c);
                while (s.length() < 4)
                    s = "0" + s;
                builder.append("\\u");
                builder.append(s);
            } else
                builder.append(c);
        }
        return builder.toString();
    }

    /**
     * Escapes special characters in the specified string according to the common W3C requirements for Turtle, N-Triples, N-quads, etc.
     * All characters are copied as-is, except for the following, which are escaped with a reverse solidus (\) prefix:
     * ", \ and special control characters \t, \r, \n, \b, \f.
     * This method assumes that the result will be quoted with the double quotes characters (").
     *
     * @param value The value to escape
     * @return The escaped value
     */
    public static String escapeStringW3C(String value) {
        return escapeStringBaseDoubleQuote(value);
    }

    /**
     * Escapes special characters in the specified string according to the CSV requirements
     * (See <a href="http://www.ietf.org/rfc/rfc4180.txt">CSV</a>)
     * All characters are copied as-is, except the double quote ("), which is doubled:
     * input    : output
     * a        : a
     * 'a'      : 'a'
     * "b"c     : ""b""c
     * This method assumes that the result will be quoted with the double quotes characters (").
     *
     * @param value The value to escape
     * @return The escaped value
     */
    public static String escapeStringCSV(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"')
                builder.append('"');
            builder.append(c);
        }
        return builder.toString();
    }

    /**
     * Escapes special characters in the specified string according to the TSV requirements
     * All characters are copied as-is, except for the following, which are escaped with a reverse solidus (\) prefix:
     * ", \ and special control characters \t, \r, \n, \b, \f.
     * This method assumes that the result will be quoted with the double quotes characters (").
     *
     * @param value The value to escape
     * @return The escaped value
     */
    public static String escapeStringTSV(String value) {
        return escapeStringBaseDoubleQuote(value);
    }

    /**
     * Escapes special characters in the specified string according to the JSON requirements
     * All characters are copied as-is, except for the following, which are escaped with a reverse solidus (\) prefix:
     * ", \ and special control characters \t, \r, \n, \b, \f.
     * This method assumes that the result will be quoted with the double quotes characters (").
     *
     * @param value The value to escape
     * @return The escaped value
     */
    public static String escapeStringJSON(String value) {
        return escapeStringBaseDoubleQuote(value);
    }

    /**
     * Escapes basic special characters in the specified string assuming the result will be quoted with the double quotes characters (")
     * All characters are copied as-is, except for the following, which are escaped with a reverse solidus (\) prefix:
     * ", \ and special control characters \t, \r, \n, \b, \f.
     *
     * @param value The value to escape
     * @return The escaped value
     */
    public static String escapeStringBaseDoubleQuote(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"')
                builder.append("\\\"");
            else if (c == '\\')
                builder.append("\\\\");
            else if (c == '\t')
                builder.append("\\t");
            else if (c == '\r')
                builder.append("\\r");
            else if (c == '\n')
                builder.append("\\n");
            else if (c == '\b')
                builder.append("\\b");
            else if (c == '\f')
                builder.append("\\f");
            else
                builder.append(c);
        }
        return builder.toString();
    }

    /**
     * Serializes a RDF node in the JSON format
     *
     * @param writer The writer to write to
     * @param node   The RDF node to serialize
     * @throws IOException When an IO error occurs
     */
    public static void serializeXML(Writer writer, Node node) throws IOException {
        switch (node.getNodeType()) {
            case Node.TYPE_IRI:
                writer.write("<uri>");
                writer.write(((IRINode) node).getIRIValue());
                writer.write("</uri>");
                break;
            case Node.TYPE_BLANK:
                writer.write("<bnode>");
                writer.write(Long.toString(((BlankNode) node).getBlankID()));
                writer.write("</bnode>");
                break;
            case Node.TYPE_LITERAL:
                writer.write("<literal");
                LiteralNode lit = (LiteralNode) node;
                if (lit.getLangTag() != null) {
                    writer.write(" xml:lang=\"");
                    writer.write(escapeStringW3C(lit.getLangTag()));
                    writer.write("\">");
                } else if (lit.getDatatype() != null) {
                    writer.write(" datatype=\"");
                    writer.write(escapeStringW3C(lit.getDatatype()));
                    writer.write("\">");
                }
                writer.write(lit.getLexicalValue());
                writer.write("</literal>");
                break;
            case Node.TYPE_VARIABLE:
                writer.write("<variable>");
                writer.write(escapeStringW3C(((VariableNode) node).getName()));
                writer.write("</variable>");
                break;
            case Node.TYPE_ANONYMOUS:
                writer.write("<anon>");
                writer.write(escapeStringW3C(((AnonymousNode) node).getIndividual().getNodeID()));
                writer.write("</anon>");
                break;
            case Node.TYPE_DYNAMIC:
                writer.write("<dynamic/>");
                break;
        }
    }

    /**
     * Serializes a RDF node in the JSON format
     *
     * @param writer The writer to write to
     * @param node   The RDF node to serialize
     * @throws IOException When an IO error occurs
     */
    public static void serializeJSON(Writer writer, Node node) throws IOException {
        if (node == null) {
            writer.write("null");
            return;
        }
        switch (node.getNodeType()) {
            case Node.TYPE_IRI:
                writer.write("{\"type\": \"uri\", \"value\": \"");
                writer.write(escapeStringJSON(((IRINode) node).getIRIValue()));
                writer.write("\"}");
                break;
            case Node.TYPE_BLANK:
                writer.write("{\"type\": \"bnode\", \"value\": \"");
                writer.write(Long.toString(((BlankNode) node).getBlankID()));
                writer.write("\"}");
                break;
            case Node.TYPE_LITERAL:
                LiteralNode lit = (LiteralNode) node;
                writer.write("{\"type\": \"literal\", \"value\": \"");
                writer.write(escapeStringJSON(lit.getLexicalValue()));
                writer.write("\"");
                if (lit.getLangTag() != null) {
                    writer.write(", \"xml:lang\": \"");
                    writer.write(escapeStringJSON(lit.getLangTag()));
                    writer.write("\"");
                } else if (lit.getDatatype() != null) {
                    writer.write(", \"datatype\": \"");
                    writer.write(escapeStringJSON(lit.getDatatype()));
                    writer.write("\"");
                }
                writer.write("}");
                break;
            case Node.TYPE_VARIABLE:
                writer.write("{\"type\": \"variable\", \"value\": \"");
                writer.write(escapeStringJSON(((VariableNode) node).getName()));
                writer.write("\"}");
                break;
            case Node.TYPE_ANONYMOUS:
                writer.write("{\"type\": \"anon\", \"value\": \"");
                writer.write(escapeStringJSON(((AnonymousNode) node).getIndividual().getNodeID()));
                writer.write("\"}");
                break;
            case Node.TYPE_DYNAMIC:
                writer.write("{\"type\": \"dynamic\"}");
                break;
        }
    }

    /**
     * De-serializes the RDF node from the specified JSON AST node
     *
     * @param nodeManager The node manager to use
     * @param astNode     The AST node to de-serialize from
     * @return The RDF node
     */
    public static Node deserializeJSON(NodeManager nodeManager, ASTNode astNode) {
        Map<String, String> properties = new HashMap<>();
        for (ASTNode child : astNode.getChildren()) {
            String name = child.getChildren().get(0).getValue();
            String value = child.getChildren().get(1).getValue();
            name = name.substring(1, name.length() - 1);
            value = value.substring(1, value.length() - 1);
            properties.put(name, value);
        }
        String type = properties.get("type");
        if (type == null)
            return null;
        switch (type) {
            case "uri":
                return nodeManager.getIRINode(properties.get("value"));
            case "bnode":
                return new BlankNode(Long.parseLong(properties.get("value")));
            case "literal": {
                String lexical = properties.get("value");
                String datatype = properties.get("datatype");
                String langTag = properties.get("xml:lang");
                return nodeManager.getLiteralNode(lexical, datatype, langTag);
            }
            case "variable":
                return new VariableNode(properties.get("value"));
            case "anon": {
                AnonymousIndividual individual = new AnonymousIndividual();
                individual.setNodeID(properties.get("value"));
                return nodeManager.getAnonNode(individual);
            }
        }
        return null;
    }

    /**
     * Negotiates the content type from the specified requested ones
     *
     * @param contentTypes The requested content types by order of preference
     * @return The accepted content type
     */
    public static String httpNegotiateContentType(List<String> contentTypes) {
        for (String contentType : contentTypes) {
            switch (contentType) {
                // The SPARQL result syntaxes
                case Result.SYNTAX_CSV:
                case Result.SYNTAX_TSV:
                case Result.SYNTAX_XML:
                case Result.SYNTAX_JSON:
                    // The RDF syntaxes for quads
                case AbstractRepository.SYNTAX_NTRIPLES:
                case AbstractRepository.SYNTAX_NQUADS:
                case AbstractRepository.SYNTAX_TURTLE:
                case AbstractRepository.SYNTAX_RDFXML:
                case AbstractRepository.SYNTAX_JSON_LD:
                    return contentType;
            }
        }
        return AbstractRepository.SYNTAX_NQUADS;
    }
}
