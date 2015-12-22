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

import org.xowl.store.IOUtils;
import org.xowl.store.Vocabulary;
import org.xowl.store.rdf.*;
import org.xowl.utils.Files;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a SPARQL command as a set of query solutions
 *
 * @author Laurent Wouters
 */
public class ResultSolutions implements Result {
    /**
     * The solutions
     */
    private final Solutions solutions;

    /**
     * Gets the solutions
     *
     * @return The solutions
     */
    public Solutions getSolutions() {
        return solutions;
    }

    /**
     * Initializes this result
     *
     * @param solutions The solutions
     */
    public ResultSolutions(Solutions solutions) {
        this.solutions = solutions;
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
                printCSV(writer);
                break;
            case Result.SYNTAX_TSV:
                printTSV(writer);
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
        return serializedJSON();
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
     * Gets the variables used in the solutions
     *
     * @return The variables used in the solutions
     */
    private List<VariableNode> getVariables() {
        List<VariableNode> variables = new ArrayList<>();
        for (QuerySolution solution : solutions) {
            for (VariableNode variable : solution.getVariables()) {
                if (!variables.contains(variable))
                    variables.add(variable);
            }
        }
        return variables;
    }

    /**
     * Prints the results with the CSV format
     *
     * @param writer The writer to use
     */
    private void printCSV(Writer writer) throws IOException {
        List<VariableNode> variables = getVariables();
        if (variables.isEmpty())
            return;
        for (int i = 0; i != variables.size(); i++) {
            if (i != 0)
                writer.write(",");
            writer.write(variables.get(i).getName());
        }
        for (QuerySolution solution : solutions) {
            writer.write(Files.LINE_SEPARATOR);
            for (int i = 0; i != variables.size(); i++) {
                if (i != 0)
                    writer.write(",");
                Node value = solution.get(variables.get(i));
                if (value != null) {
                    switch (value.getNodeType()) {
                        case Node.TYPE_IRI:
                            writer.write(((IRINode) value).getIRIValue());
                            break;
                        case Node.TYPE_BLANK:
                            writer.write("_:");
                            writer.write(Long.toString(((BlankNode) value).getBlankID()));
                            break;
                        case Node.TYPE_LITERAL:
                            LiteralNode lit = (LiteralNode) value;
                            writer.write('"');
                            writer.write(IOUtils.escapeStringCSV(lit.getLexicalValue()));
                            writer.write('"');
                            break;
                    }
                }
            }
        }
    }

    /**
     * Prints the results with the TSV format
     *
     * @param writer The writer to use
     */
    private void printTSV(Writer writer) throws IOException {
        List<VariableNode> variables = getVariables();
        if (variables.isEmpty())
            return;
        for (int i = 0; i != variables.size(); i++) {
            if (i != 0)
                writer.write('\t');
            writer.write('?');
            writer.write(variables.get(i).getName());
        }
        for (QuerySolution solution : solutions) {
            writer.write(Files.LINE_SEPARATOR);
            for (int i = 0; i != variables.size(); i++) {
                if (i != 0)
                    writer.write('\t');
                Node value = solution.get(variables.get(i));
                if (value != null) {
                    switch (value.getNodeType()) {
                        case Node.TYPE_IRI:
                            writer.write("<");
                            writer.write(IOUtils.escapeAbsoluteURIW3C(((IRINode) value).getIRIValue()));
                            writer.write(">");
                            break;
                        case Node.TYPE_BLANK:
                            writer.write("_:");
                            writer.write(Long.toString(((BlankNode) value).getBlankID()));
                            break;
                        case Node.TYPE_LITERAL:
                            LiteralNode lit = (LiteralNode) value;
                            writer.write('"');
                            writer.write(IOUtils.escapeStringTSV(lit.getLexicalValue()));
                            writer.write('"');
                            if (lit.getLangTag() != null) {
                                writer.write("@");
                                writer.write(lit.getLangTag());
                            } else if (lit.getDatatype() != null) {
                                String datatype = lit.getDatatype();
                                if (datatype.startsWith(Vocabulary.xsd)) {
                                    datatype = "xsd:" + IOUtils.escapeAbsoluteURIW3C(datatype.substring(Vocabulary.xsd.length()));
                                    writer.write("^^");
                                    writer.write(datatype);
                                } else {
                                    writer.write("^^<");
                                    writer.write(IOUtils.escapeAbsoluteURIW3C(datatype));
                                    writer.write(">");
                                }
                            }
                            break;
                    }
                }
            }
        }
    }

    /**
     * Prints the results with the TSV format
     *
     * @param writer The writer to use
     */
    private void printXML(Writer writer) throws IOException {
        List<VariableNode> variables = getVariables();
        writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        writer.write("<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\">");
        writer.write("<head>");
        for (VariableNode variable : variables) {
            writer.write("<variable name=\"");
            writer.write(variable.getName());
            writer.write("\"/>");
        }
        writer.write("</head>");
        writer.write("<results>");
        for (QuerySolution solution : solutions) {
            writer.write("<result>");
            for (int i = 0; i != variables.size(); i++) {
                Node value = solution.get(variables.get(i));
                if (value != null) {
                    writer.write("<binding name=\"");
                    writer.write(variables.get(i).getName());
                    writer.write("\">");
                    IOUtils.serializeXML(writer, value);
                    writer.write("</binding>");
                }
            }
            writer.write("</result>");
        }
        writer.write("</results>");
        writer.write("</sparql>");
    }

    /**
     * Prints the results with the JSON format
     *
     * @param writer The writer to use
     */
    private void printJSON(Writer writer) throws IOException {
        List<VariableNode> variables = getVariables();
        writer.write("{ \"head\": { \"vars\": [");
        for (int i = 0; i != variables.size(); i++) {
            if (i != 0)
                writer.write(", ");
            writer.write("\"");
            writer.write(IOUtils.escapeStringJSON(variables.get(i).getName()));
            writer.write("\"");
        }
        writer.write("] }, \"results\": { \"bindings\": [");
        boolean firstSolution = true;
        for (QuerySolution solution : solutions) {
            if (!firstSolution)
                writer.write(", ");
            firstSolution = false;
            writer.write("{");
            boolean firstBinding = true;
            for (int i = 0; i != variables.size(); i++) {
                Node value = solution.get(variables.get(i));
                if (value != null) {
                    if (!firstBinding)
                        writer.write(", ");
                    firstBinding = false;
                    writer.write("\"");
                    writer.write(IOUtils.escapeStringJSON(variables.get(i).getName()));
                    writer.write("\": ");
                    IOUtils.serializeJSON(writer, value);
                }
            }
            writer.write(" }");
        }
        writer.write("] } }");
    }
}
