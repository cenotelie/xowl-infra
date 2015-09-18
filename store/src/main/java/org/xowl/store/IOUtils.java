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

/**
 * Utility APIs for reading and writing data
 *
 * @author Laurent Wouters
 */
public class IOUtils {
    /**
     * String containing the escaped glyphs in text
     */
    private static final String ESCAPED_GLYHPS_TEXT = "\\'\"_~.!$&()*+,;=/?#@%-";
    /**
     * String containing the escaped glyphs in uris
     */
    private static final String ESCAPED_GLYPHS_URIS = "<>\"{}|^`\\";

    /**
     * Translates the specified string into a new one by replacing the escape sequences by their value
     *
     * @param content A string that can contain escape sequences
     * @return The translated string with the escape sequences replaced by their value
     */
    public static String unescape(String content) {
        char[] buffer = new char[content.length()];
        int next = 0;
        for (int i = 0; i != content.length(); i++) {
            char c = content.charAt(i);
            if (c != '\\') {
                buffer[next++] = c;
            } else {
                char n = content.charAt(i + 1);
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
                } else if (ESCAPED_GLYHPS_TEXT.contains(Character.toString(n))) {
                    buffer[next++] = n;
                    i++;
                }
            }
        }
        return new String(buffer, 0, next);
    }

    /**
     * Translates the specified URI into a new one by replacing character that should be escaped by their escape sequence
     *
     * @param content A string that can contain escape sequences
     * @return The escaped URI
     */
    public static String escapeURI(String content) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != content.length(); i++) {
            char c = content.charAt(i);
            if (c < 0x20 || ESCAPED_GLYPHS_URIS.contains(Character.toString(c))) {
                String s = Integer.toHexString(c);
                while (s.length() < 4)
                    s = "0" + s;
                builder.append("u");
                builder.append(s);
            } else if (Character.isHighSurrogate(c)) {
                char c2 = content.charAt(i + 1);
                i++;
                int cp = ((c2 - 0xDC00) | ((c - 0xD800) << 10)) + 0x10000;
                String s = Integer.toHexString(cp);
                while (s.length() < 8)
                    s = "0" + s;
                builder.append("U");
                builder.append(s);
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * Escapes special characters in the specified string according to the common W3C requirements for Turtle, N-Triples, N-quads, etc.
     * All characters are copied as-is, except for the following, which are escaped with a back-slash (\) prefix:
     * ", ', \ and special control characters \t, \r, \n, \b, \f
     *
     * @param value The value to escape
     * @return The escaped value
     */
    public static String escapeStringW3C(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"')
                builder.append("\\\"");
            else if (c == '\'')
                builder.append("\\\'");
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
            else if (Character.isHighSurrogate(c)) {
                char c2 = value.charAt(i + 1);
                i++;
                int cp = ((c2 - 0xDC00) | ((c - 0xD800) << 10)) + 0x10000;
                String s = Integer.toHexString(cp);
                while (s.length() < 8)
                    s = "0" + s;
                builder.append("U");
                builder.append(s);
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * Escapes special characters in the specified string according to the CSV requirements
     * (See <a href="http://www.ietf.org/rfc/rfc4180.txt">CSV</a>)
     * All characters are copied as-is, except the double quote ("), which is doubled:
     * input    -> output
     * a        -> a
     * 'a'      -> 'a'
     * "b"c     -> ""b""c
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
     * All characters are copied as-is, except for the following, which are escaped with a back-slash (\) prefix:
     * ", \ and special control characters \t, \r, \n, \b, \f
     *
     * @param value The value to escape
     * @return The escaped value
     */
    public static String escapeStringTSV(String value) {
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
     * Escapes special characters in the specified string according to the JSON requirements
     * All characters are copied as-is, except for the following, which are escaped with a back-slash (\) prefix:
     * ", ', \ and special control characters \t, \r, \n, \b, \f
     *
     * @param value The value to escape
     * @return The escaped value
     */
    public static String escapeStringJSON(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"')
                builder.append("\\\"");
            else if (c == '\'')
                builder.append("\\\'");
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
}
