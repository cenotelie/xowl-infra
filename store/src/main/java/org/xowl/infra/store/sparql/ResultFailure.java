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

package org.xowl.infra.store.sparql;

import fr.cenotelie.commons.utils.TextUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Represents the failing result of a SPARQL command
 *
 * @author Laurent Wouters
 */
public class ResultFailure implements Result {
    /**
     * The singleton instance with an empty message
     */
    public static final ResultFailure INSTANCE = new ResultFailure("Unknown");

    /**
     * The message, if any
     */
    private final String message;

    /**
     * Initializes this result
     *
     * @param message The message, if any
     */
    public ResultFailure(String message) {
        this.message = message;
    }

    /**
     * Gets the message, if any
     *
     * @return The message, if any
     */
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public void print(Writer writer, String syntax) throws IOException {
        switch (syntax) {
            case Result.SYNTAX_CSV:
            case Result.SYNTAX_TSV:
                writer.write("ERROR: " + message);
                break;
            case Result.SYNTAX_XML:
                printXML(writer);
                break;
            case Result.SYNTAX_JSON:
                printJSON(writer);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format " + syntax);
        }
    }

    @Override
    public String serializedString() {
        return "ERROR: " + message;
    }

    @Override
    public String serializedJSON() {
        StringWriter writer = new StringWriter();
        try {
            printJSON(writer);
        } catch (IOException exception) {
            // cannot happen
        }
        return writer.toString();
    }

    /**
     * Prints the results with the TSV format
     *
     * @param writer The writer to use
     */
    private void printXML(Writer writer) throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        writer.write("<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\">");
        writer.write("<head>");
        writer.write("</head>");
        writer.write("<boolean>false</boolean>");
        writer.write("<error>");
        writer.write(message);
        writer.write("</error>");
        writer.write("</sparql>");
    }

    /**
     * Prints the results with the JSON format
     *
     * @param writer The writer to use
     */
    private void printJSON(Writer writer) throws IOException {
        writer.write("{ \"head\": { },  \"boolean\": \"false\", \"error\": \"");
        writer.write(TextUtils.escapeStringJSON(message));
        writer.write("\" }");
    }
}
