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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Utility APIs for URIs
 *
 * @author Laurent Wouters
 */
public class URIUtils {
    /**
     * Identifier of the scheme component of an URI
     */
    public static final int COMPONENT_SCHEME = 0;
    /**
     * Identifier of the scheme component of an URI
     */
    public static final int COMPONENT_AUTHORITY = 1;
    /**
     * Identifier of the scheme component of an URI
     */
    public static final int COMPONENT_PATH = 2;
    /**
     * Identifier of the scheme component of an URI
     */
    public static final int COMPONENT_QUERY = 3;
    /**
     * Identifier of the scheme component of an URI
     */
    public static final int COMPONENT_FRAGMENT = 4;

    /**
     * Determines whether the specified URI is absolute.
     * An URI is considered absolute if it has a scheme component, as well as either an authority or path component
     * (See <a href="http://tools.ietf.org/html/rfc3986#section-4.3">RFC 3986 - 4.3</a>)
     *
     * @param uri An URI
     * @return true
     */
    public static boolean isAbsolute(String uri) {
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
    public static String resolveRelative(String base, String reference) {
        if (reference == null || reference.isEmpty())
            return base;
        if (base == null || base.isEmpty())
            return reference;
        // RFC 3986: 5.2.1 - Pre-parse the Base URI
        String[] uriBase = parse(base);
        if (uriBase == null)
            return null;
        // RFC 3986: 5.2.2 - Transform References
        // (R.scheme, R.authority, R.path, R.query, R.fragment) = parse(R);
        String[] uriReference = parse(reference);
        if (uriReference == null)
            return null;

        String targetScheme;
        String targetAuthority;
        String targetPath;
        String targetQuery;
        String targetFragment;
        if (uriReference[COMPONENT_SCHEME] != null) {
            targetScheme = uriReference[COMPONENT_SCHEME];
            targetAuthority = uriReference[COMPONENT_AUTHORITY];
            targetPath = removeDotSegments(uriReference[COMPONENT_PATH]);
            targetQuery = uriReference[COMPONENT_QUERY];
        } else {
            if (uriReference[COMPONENT_AUTHORITY] != null) {
                targetAuthority = uriReference[COMPONENT_AUTHORITY];
                targetPath = removeDotSegments(uriReference[COMPONENT_PATH]);
                targetQuery = uriReference[COMPONENT_QUERY];
            } else {
                if (uriReference[COMPONENT_PATH] == null || uriReference[COMPONENT_PATH].isEmpty()) {
                    targetPath = uriBase[COMPONENT_PATH];
                    targetQuery = uriReference[COMPONENT_QUERY] != null ? uriReference[COMPONENT_QUERY] : uriBase[COMPONENT_QUERY];
                } else {
                    if (uriReference[COMPONENT_PATH].startsWith("/")) {
                        targetPath = removeDotSegments(uriReference[COMPONENT_PATH]);
                    } else {
                        targetPath = mergePaths(uriBase[COMPONENT_AUTHORITY], uriBase[COMPONENT_PATH], uriReference[COMPONENT_PATH]);
                        targetPath = removeDotSegments(targetPath);
                    }
                    targetQuery = uriReference[COMPONENT_QUERY];
                }
                targetAuthority = uriBase[COMPONENT_AUTHORITY];
            }
            targetScheme = uriBase[COMPONENT_SCHEME];
        }
        targetFragment = uriReference[COMPONENT_FRAGMENT];

        // RFC 3986: 5.3 - Transform References
        return recompose(targetScheme, targetAuthority, targetPath, targetQuery, targetFragment);
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
    public static String[] parse(String uri) {
        if (!allLegalCharacters(uri))
            throw new IllegalArgumentException("URI contains illegal characters");
        String[] components = new String[5];

        // retrieve the scheme
        int start = uri.indexOf(":");
        if (start == -1) {
            // no scheme
            start = 0;
        } else {
            components[COMPONENT_SCHEME] = uri.substring(0, start);
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
            components[COMPONENT_AUTHORITY] = uri.substring(start, min);
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
        if (components[COMPONENT_AUTHORITY] != null) {
            // uri has authority component
            // path must be empty, or start with /
            if (min == start) {
                // path is empty
                components[COMPONENT_PATH] = "";
                start = min;
            } else if (uri.charAt(start) != '/') {
                // error, must start with /
                throw new IllegalArgumentException("Path in URI with authority must start by / or be empty");
            } else {
                components[COMPONENT_PATH] = uri.substring(start, min);
                start = min;
            }
        } else if (uri.startsWith("//", start)) {
            // error, with no authority, the path cannot start with //
            throw new IllegalArgumentException("Path in URI with authority cannot start by //");
        } else {
            components[COMPONENT_PATH] = uri.substring(start, min);
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
            components[COMPONENT_QUERY] = uri.substring(start, min);
            start = min;
            if (start == uri.length())
                return components;
        }

        // at this point we are facing a fragment
        start++;
        components[COMPONENT_FRAGMENT] = start == uri.length() ? "" : uri.substring(start);
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
    public static String recompose(String scheme, String authority, String path, String query, String fragment) {
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
    private static String removeDotSegments(String path) {
        if (path == null)
            return null;
        if (path.isEmpty())
            return path;
        boolean isAbsolute = path.startsWith("/");
        List<String> input = splitSegments(path);
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
    private static List<String> splitSegments(String path) {
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
    private static String mergePaths(String baseAuthority, String basePath, String refPath) {
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
     * Determines whether the specified URI only contains valid characters.
     * This method does the check whether the URI is well-formed
     *
     * @param uri An URI
     * @return true if the URI only contains valid characters
     */
    private static boolean allLegalCharacters(String uri) {
        for (int i = 0; i != uri.length(); i++)
            if (!isLegalCharacter(uri.charAt(i)))
                return false;
        return true;
    }

    /**
     * Determines whether the specified character is a valid character in an URI
     *
     * @param c A character
     * @return true if the character is valid
     */
    private static boolean isLegalCharacter(char c) {
        return (!Character.isWhitespace(c) && !Character.isISOControl(c) && c != '<' && c != '>');
    }
}
