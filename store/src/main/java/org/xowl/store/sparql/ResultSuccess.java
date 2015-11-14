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

package org.xowl.store.sparql;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Represents the successful result of a command
 *
 * @author Laurent Wouters
 */
public class ResultSuccess implements Result {
    /**
     * The singleton instance
     */
    public static final ResultSuccess INSTANCE = new ResultSuccess();

    /**
     * Initializes this result
     */
    private ResultSuccess() {
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public void print(Writer writer, String syntax) throws IOException {
        switch (syntax) {
            case Result.SYNTAX_CSV:
            case Result.SYNTAX_TSV:
                writer.write("OK");
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
        return "OK";
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
        writer.write("<boolean>true</boolean>");
        writer.write("</sparql>");
    }

    /**
     * Prints the results with the JSON format
     *
     * @param writer The writer to use
     */
    private void printJSON(Writer writer) throws IOException {
        writer.write("{ \"head\": { }, \"boolean\": \"true\" }");
    }
}
