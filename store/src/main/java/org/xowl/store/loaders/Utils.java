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

package org.xowl.store.loaders;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.xs.DoubleDV;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Utilities for the loaders
 *
 * @author Laurent Wouters
 */
public class Utils {
    /**
     * Strings containing the escaped glyphs
     */
    private static final String ESCAPED_GLYHPS = "\\'\"_~.!$&()*+,;=/?#@%-";
    /**
     * Utility for the validation of double values
     */
    private static final DoubleDV CANONICAL_DOUBLE = new DoubleDV();

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
                } else if (ESCAPED_GLYHPS.contains(Character.toString(n))) {
                    buffer[next++] = n;
                    i++;
                }
            }
        }
        return new String(buffer, 0, next);
    }

    /**
     * Determines whether the specified URI is absolute.
     * An URI is considered absolute if it has a scheme component, as well as either an authority or path component
     * (See <a href="http://tools.ietf.org/html/rfc3986#section-4.3>RFC 3986 - 4.3</a>)
     *
     * @param uri An URI
     * @return true
     */
    public static boolean uriIsAbsolute(String uri) {
        // look for a scheme
        int start = uri.indexOf(":");
        if (start == -1) {
            // no scheme
            return false;
        } else {
            start++;
        }
        // look for the authority
        if (uri.startsWith("//", start)) {
            // found an authority
            return true;
        }
        // no authority
        if (start == uri.length())
            // at the end
            return false;
        char c = uri.charAt(start);
        // we expect a path, if a query or fragment is found instead the path is missing (no authority)
        return (c != '?' && c != '#');
    }

    /**
     * Resolves a relative URI against a base
     * Implements the RFC 3986 relative resolution algorithm
     * (See <a href="http://tools.ietf.org/html/rfc3986#section-5.2">RFC 3986 - 5.2</a>)
     *
     * @param base      The base URI to resolve against
     * @param reference The URI to resolve
     * @return The resolved URI
     */
    public static String uriResolveRelative(String base, String reference) {
        if (reference == null || reference.isEmpty())
            return base;
        if (base == null || base.isEmpty())
            return reference;
        // RFC 3986: 5.2.1 - Pre-parse the Base URI
        String[] uriBase = uriParse(base);
        if (uriBase == null)
            return null;
        String baseScheme = uriBase[0];
        String baseAuthority = uriBase[1];
        String basePath = uriBase[2];
        String baseQuery = uriBase[3];
        // base fragment is ignored, no need to get it

        // RFC 3986: 5.2.2 - Transform References
        // (R.scheme, R.authority, R.path, R.query, R.fragment) = parse(R);
        String[] uriReference = uriParse(reference);
        if (uriReference == null)
            return null;
        String refScheme = uriReference[0];
        String refAuthority = uriReference[1];
        String refPath = uriReference[2];
        String refQuery = uriReference[3];
        String refFragment = uriReference[4];
        // if ((not strict) and (R.scheme == Base.scheme)) then
        //    undefine(R.scheme);
        // endif;

        String targetScheme;
        String targetAuthority;
        String targetPath;
        String targetQuery;
        String targetFragment;
        if (refScheme != null) {
            targetScheme = refScheme;
            targetAuthority = refAuthority;
            targetPath = uriRemoveDotSegments(refPath);
            targetQuery = refQuery;
        } else {
            if (refAuthority != null) {
                targetAuthority = refAuthority;
                targetPath = uriRemoveDotSegments(refPath);
                targetQuery = refQuery;
            } else {
                if (refPath == null || refPath.isEmpty()) {
                    targetPath = basePath;
                    targetQuery = refQuery != null ? refQuery : baseQuery;
                } else {
                    if (refPath.startsWith("/")) {
                        targetPath = uriRemoveDotSegments(refPath);
                    } else {
                        targetPath = uriMergePaths(baseAuthority, basePath, refPath);
                        targetPath = uriRemoveDotSegments(targetPath);
                    }
                    targetQuery = refQuery;
                }
                targetAuthority = baseAuthority;
            }
            targetScheme = baseScheme;
        }
        targetFragment = refFragment;

        // RFC 3986: 5.3 - Transform References
        return uriRecompose(targetScheme, targetAuthority, targetPath, targetQuery, targetFragment);
    }

    /**
     * Parses an URI to decomposes it into the 5 components (scheme, authority, path, query and fragment)
     * An undefined component is represented by the null value.
     * An empty component is represented by the empty string
     * (See <a href="http://tools.ietf.org/html/rfc3986#section-3">RFC 3986 - 3</a>)
     *
     * @param uri The URI to compose
     * @return The 5 URI components in order in an array, or null if the syntax is incorrect
     */
    private static String[] uriParse(String uri) {
        String[] components = new String[5];

        // retrieve the scheme
        int start = uri.indexOf(":");
        if (start == -1) {
            // no scheme
            start = 0;
        } else {
            components[0] = uri.substring(0, start);
            start++;
        }

        // retrieve the authority
        int nextNumber;
        int nextQMark;
        int min;
        if (uri.startsWith("//", start)) {
            start += 2;
            int nextSlash = uri.indexOf("/", start);
            nextNumber = uri.indexOf("#", start);
            nextQMark = uri.indexOf("?", start);
            min = uri.length();
            if (nextSlash >= 0 && nextSlash < min)
                min = nextSlash;
            if (nextNumber >= 0 && nextNumber < min)
                min = nextNumber;
            if (nextQMark >= 0 && nextQMark < min)
                min = nextQMark;
            components[1] = uri.substring(start, min);
            start = min;
        }
        if (start == uri.length())
            return components;

        // retrieve the path
        nextNumber = uri.indexOf("#", start);
        nextQMark = uri.indexOf("?", start);
        min = uri.length();
        if (nextNumber >= 0 && nextNumber < min)
            min = nextNumber;
        if (nextQMark >= 0 && nextQMark < min)
            min = nextQMark;
        if (components[1] != null) {
            // uri has authority component
            // path must be empty, or start with /
            if (min == start) {
                // path is empty
                components[2] = "";
                start = min;
            } else if (uri.charAt(start) != '/') {
                // error, must start with /
                return null;
            } else {
                components[2] = uri.substring(start, min);
                start = min;
            }
        } else if (uri.startsWith("//", start)) {
            // error, with no authority, the path cannot start with //
            return null;
        } else {
            components[2] = uri.substring(start, min);
            start = min;
        }
        if (start == uri.length())
            return components;

        if (uri.startsWith("?", start)) {
            start++;
            // this is a query component
            nextNumber = uri.indexOf("#", start);
            min = uri.length();
            if (nextNumber >= 0 && nextNumber < min)
                min = nextNumber;
            components[3] = uri.substring(start, min);
            start = min;
            if (start == uri.length())
                return components;
        }

        // at this point we are facing a fragment
        start++;
        components[4] = start == uri.length() ? "" : uri.substring(start);
        return components;
    }

    /**
     * Recomposes the components of a URI to form the full one
     * Implements the RFC 3986 component re-composition algorithm
     * (See <a href="http://tools.ietf.org/html/rfc3986#section-5.3">RFC 3986 - 5.3</a>)
     *
     * @param scheme    The scheme component
     * @param authority The authority component
     * @param path      The path component
     * @param query     The query component
     * @param fragment  The fragment component
     * @return The recomposed URI
     */
    private static String uriRecompose(String scheme, String authority, String path, String query, String fragment) {
        StringBuilder builder = new StringBuilder();
        if (scheme != null) {
            builder.append(scheme);
            builder.append(":");
        }
        if (authority != null) {
            builder.append("//");
            builder.append(authority);
        }
        if (path != null)
            builder.append(path);
        if (query != null) {
            builder.append("?");
            builder.append(query);
        }
        if (fragment != null) {
            builder.append("#");
            builder.append(fragment);
        }
        return builder.toString();
    }

    /**
     * Removes the dot segments from the path component of an URI
     * Implements the RFC 3986 remove dot segments algorithm
     * (See <a href="http://tools.ietf.org/html/rfc3986#section-5.2.4">RFC 3986 - 5.2.4</a>)
     *
     * @param path The original path component
     * @return The resulting path component
     */
    private static String uriRemoveDotSegments(String path) {
        if (path == null)
            return null;
        if (path.isEmpty())
            return path;
        boolean isAbsolute = path.startsWith("/");
        List<String> input = uriSplitSegments(path);
        Stack<String> output = new Stack<>();
        for (int i = 0; i != input.size(); i++) {
            String head = input.get(i);
            if ("..".equals(head)) {
                if (output.isEmpty())
                    continue;
                if (isAbsolute && output.size() == 1 && output.get(0).isEmpty())
                    // cannot remove the first empty segment for absolute paths
                    continue;
                output.pop();
                if (i == input.size() - 1)
                    // the last one, to not lose the trailing / push an empty segment
                    output.push("");
            } else if (".".equals(head)) {
                if (i == input.size() - 1)
                    // the last one, to not lose the trailing / push an empty segment
                    output.push("");
            } else {
                output.push(head);
            }
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i != output.size(); i++) {
            if (i != 0)
                result.append("/");
            result.append(output.get(i));
        }
        return result.toString();
    }

    /**
     * Splits the path component of an URI into segments
     * This method leaves empty segments at the beginning (resp. end) when the path begins (resp. ends) with a /, as well as in the middle
     *
     * @param path The path component of an URI
     * @return The list of segments
     */
    private static List<String> uriSplitSegments(String path) {
        List<String> result = new ArrayList<>();
        int start = 0;
        int index = path.indexOf("/");
        while (index != -1) {
            if (start == index)
                result.add("");
            else
                result.add(path.substring(start, index));
            start = index + 1;
            if (start == path.length()) {
                result.add("");
                return result;
            }
            index = path.indexOf("/", start);
        }
        if (start < path.length())
            result.add(path.substring(start));
        return result;
    }

    /**
     * Merges the paths components of two URIs
     * Implements the RFC 3986 merge paths algorithm
     * (See <a href="http://tools.ietf.org/html/rfc3986#section-5.2.3">RFC 3986 - 5.2.3</a>)
     *
     * @param baseAuthority The authority component of the base URI
     * @param basePath      The path component of the base URI
     * @param refPath       The path component of the reference URI
     * @return The merged target path
     */
    private static String uriMergePaths(String baseAuthority, String basePath, String refPath) {
        if (baseAuthority != null && (basePath == null || basePath.isEmpty())) {
            return "/" + refPath;
        } else if (basePath == null || basePath.isEmpty()) {
            return refPath;
        } else {
            int index = basePath.lastIndexOf("/");
            if (index == -1)
                return refPath;
            return basePath.substring(0, index + 1) + refPath;
        }
    }

    /**
     * Quotes illegal URI characters in the specified term
     *
     * @param term A term
     * @return The term with the illegal characters quoted
     */
    public static String quote(String term) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != term.length(); i++) {
            char c = term.charAt(i);
            if (Character.isWhitespace(c))
                builder.append("+");
            else
                builder.append(c);
        }
        return builder.toString();
    }

    /**
     * Gets the canonical lexical form of a double value
     *
     * @param value A serialized double value
     * @return The canonical lexical form
     */
    public static String canonicalDouble(String value) {
        try {
            Object x = CANONICAL_DOUBLE.getActualValue(value, null);
            return x.toString();
        } catch (InvalidDatatypeValueException exception) {
            // do nothing
            return value;
        }
    }
}
