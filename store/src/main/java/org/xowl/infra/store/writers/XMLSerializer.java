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

package org.xowl.infra.store.writers;

import java.io.IOException;
import java.io.Writer;

/**
 * Implements a trivial XML serializer
 *
 * @author Laurent Wouters
 */
class XMLSerializer {
    /**
     * The writer to use
     */
    private final Writer writer;
    /**
     * Produce XML version 1.1 (instead of 1.0)
     */
    private final boolean xmlVersion11;

    /**
     * Initializes this serializer
     *
     * @param writer       The writer to use
     * @param xmlVersion11 Whether to produce XML version 1.1 (instead of 1.0)
     */
    public XMLSerializer(Writer writer, boolean xmlVersion11) {
        this.writer = writer;
        this.xmlVersion11 = xmlVersion11;
    }

    /**
     * Writes an XML preambule
     *
     * @param encoding The encoding
     */
    public void onPreambule(String encoding) throws IOException {
        writer.write("<? version=\"");
        writer.write(xmlVersion11 ? "1.1" : "1.0");
        writer.write("\"");
        if (encoding != null && !encoding.isEmpty()) {
            writer.write(" encoding=\"");
            writer.write(encoding);
            writer.write("\"");
        }
        writer.write("?>");
    }

    /**
     * Writes the start of the opening markup of an element
     *
     * @param name The name of the markup
     */
    public void onElementOpenBegin(String name) throws IOException {
        writer.write("<");
        writer.write(name);
    }

    /**
     * Writes the end of the opening markup of an element
     */
    public void onElementOpenEnd() throws IOException {
        writer.write(">");
    }

    /**
     * Writes the end of the markup of an element that is immediately closed
     */
    public void onElementOpenEndAndClose() throws IOException {
        writer.write("/>");
    }

    /**
     * Writes the closing markup of an element
     *
     * @param name The name of the markup
     */
    public void onElementClose(String name) throws IOException {
        writer.write("</");
        writer.write(name);
        writer.write(">");
    }

    /**
     * Writes the attribute of an element
     *
     * @param name  The attribute's name
     * @param value the attribute's value
     */
    public void onAttribute(String name, String value) throws IOException {
        writer.write(" ");
        writer.write(name);
        writer.write(" =\"");
        write(value, true);
        writer.write("\"");
    }

    /**
     * Writes the plain text content of a text node
     *
     * @param content The content
     */
    public void onContent(String content) throws IOException {
        write(content, false);
    }

    /**
     * Normalizes and writes the specified string
     *
     * @param value            The string to normalize and write
     * @param isAttributeValue Whether this is the value of an attribute
     */
    private void write(String value, boolean isAttributeValue) throws IOException {
        int len = (value != null) ? value.length() : 0;
        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            write(c, isAttributeValue);
        }
    }

    /**
     * Normalizes and writes the specified character
     *
     * @param character        A character
     * @param isAttributeValue Whether this is the value of an attribute
     */
    private void write(char character, boolean isAttributeValue) throws IOException {
        switch (character) {
            case '<': {
                writer.write("&lt;");
                break;
            }
            case '>': {
                writer.write("&gt;");
                break;
            }
            case '&': {
                writer.write("&amp;");
                break;
            }
            case '"': {
                // A '"' that appears in character data
                // does not need to be escaped.
                if (isAttributeValue) {
                    writer.write("&quot;");
                } else {
                    writer.write("\"");
                }
                break;
            }
            case '\r': {
                // If CR is part of the document's content, it
                // must not be printed as a literal otherwise
                // it would be normalized to LF when the document
                // is reparsed.
                writer.write("&#xD;");
                break;
            }
            case '\n': {
                writer.write("&#xA;");
                break;
            }
            default: {
                // In XML 1.1, control chars in the ranges [#x1-#x1F, #x7F-#x9F] must be escaped.
                //
                // Escape space characters that would be normalized to #x20 in attribute values
                // when the document is reparsed.
                //
                // Escape NEL (0x85) and LSEP (0x2028) that appear in content
                // if the document is XML 1.1, since they would be normalized to LF
                // when the document is reparsed.
                if (xmlVersion11 && ((character >= 0x01 && character <= 0x1F && character != 0x09)
                        || (character >= 0x7F && character <= 0x9F) || character == 0x2028)
                        || isAttributeValue && character == 0x09) {
                    writer.write("&#x");
                    writer.write(Integer.toHexString(character).toUpperCase());
                    writer.write(";");
                } else {
                    writer.write(character);
                }
            }
        }
    }
}
