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

package org.xowl.server.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.xowl.store.AbstractRepository;
import org.xowl.store.sparql.Result;
import org.xowl.store.sparql.ResultQuads;
import org.xowl.utils.collections.Couple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;

/**
 * Utility APIs for the HTTP server
 *
 * @author Laurent Wouters
 */
class Utils {
    /**
     * Gets the request body of the specified request
     *
     * @param exchange The exchange
     * @return The request body
     * @throws IOException When reading failed
     */
    public static String getRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream stream = exchange.getRequestBody()) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = stream.read(buffer);
            while (read > 0) {
                output.write(buffer, 0, read);
                read = stream.read(buffer);
            }
            buffer = output.toByteArray();
            output.close();
            return new String(buffer, "UTF-8");
        }
    }

    /**
     * Gets the content type of a request
     *
     * @param headers The request headers
     * @return The content type of a request
     */
    public static String getRequestContentType(Headers headers) {
        String type = headers.getFirst("Content-Type");
        if (type == null || type.isEmpty())
            return null;
        int index = type.indexOf(";");
        if (index != -1)
            type = type.substring(0, index);
        type = type.trim();
        return type;
    }

    /**
     * Gets the parameters of a request
     *
     * @param uri The requested URI
     * @return The parameters
     */
    public static Map<String, List<String>> getRequestParameters(URI uri) {
        Map<String, List<java.lang.String>> result = new HashMap<>();
        String query = uri.getRawQuery();
        if (query == null || query.isEmpty())
            return result;

        String pairs[] = query.split("[&]");
        for (String pair : pairs) {
            String param[] = pair.split("[=]");
            String key = null;
            String value = null;
            if (param.length > 0) {
                try {
                    key = URLDecoder.decode(param[0], "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    exception.printStackTrace();
                }
            }
            if (param.length > 1) {
                try {
                    value = URLDecoder.decode(param[1], "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    exception.printStackTrace();
                }
            }
            if (key != null && !key.isEmpty() && value != null && !value.isEmpty()) {
                List<String> sub = result.get(key);
                if (sub == null) {
                    sub = new ArrayList<>(1);
                    result.put(key, sub);
                }
                sub.add(value);
            }
        }
        return result;
    }

    /**
     * Retrieves the accepted content types by order of preference
     *
     * @param headers The request headers
     * @return The content types by order of preference
     */
    public static List<String> getAcceptTypes(Headers headers) {
        String header = headers.getFirst("Accept");
        if (header == null || header.isEmpty())
            return Collections.emptyList();
        List<Couple<String, Float>> contentTypes = new ArrayList<>();
        String[] parts = header.split(",");
        for (String part : parts) {
            String value = part.trim();
            if (value.contains(";")) {
                String[] subs = value.split(";");
                contentTypes.add(new Couple<>(subs[0], Float.parseFloat(subs[1].substring(2))));
            } else {
                contentTypes.add(new Couple<>(value, 1.0f));
            }
        }
        if (contentTypes.isEmpty())
            return Collections.emptyList();
        Collections.sort(contentTypes, new Comparator<Couple<String, Float>>() {
            @Override
            public int compare(Couple<String, Float> c1, Couple<String, Float> c2) {
                return Float.compare(c2.y, c1.y);
            }
        });
        List<String> result = new ArrayList<>(contentTypes.size());
        for (Couple<String, Float> couple : contentTypes)
            result.add(couple.x);
        return result;
    }

    /**
     * Negotiates the content type from the specified requested ones
     *
     * @param contentTypes The requested content types by order of preference
     * @return The accepted content type
     */
    public static String negotiateType(List<String> contentTypes) {
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

    /**
     * Setups the headers of the specified HTTP response in order to enable Cross-Origin Resource Sharing
     *
     * @param headers The response headers
     */
    public static void enableCORS(Headers headers) {
        headers.put("Access-Control-Allow-Methods", Arrays.asList("GET", "POST", "OPTIONS"));
        headers.put("Access-Control-Allow-Headers", Arrays.asList("Accept", "Content-Type", "Cache-Control"));
        headers.put("Access-Control-Allow-Origin", Arrays.asList("*"));
        headers.put("Access-Control-Allow-Credentials", Arrays.asList("true"));
        headers.put("Cache-Control", Arrays.asList("no-cache"));
    }

    /**
     * Coerce the content type of a SPARQL response depending on the result type
     *
     * @param result The SPARQL result
     * @param type   The negotiated content type
     * @return The coerced content type
     */
    public static String coerceContentType(Result result, String type) {
        if (result instanceof ResultQuads) {
            switch (type) {
                case AbstractRepository.SYNTAX_NTRIPLES:
                case AbstractRepository.SYNTAX_NQUADS:
                case AbstractRepository.SYNTAX_TURTLE:
                case AbstractRepository.SYNTAX_RDFXML:
                case AbstractRepository.SYNTAX_JSON_LD:
                    return type;
                default:
                    return AbstractRepository.SYNTAX_NQUADS;
            }
        } else {
            switch (type) {
                case Result.SYNTAX_CSV:
                case Result.SYNTAX_TSV:
                case Result.SYNTAX_XML:
                case Result.SYNTAX_JSON:
                    return type;
                default:
                    return Result.SYNTAX_JSON;
            }
        }
    }

    /**
     * The size of buffers for loading content
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Loads all the content from the specified input stream
     *
     * @param stream The stream to load from
     * @return The loaded content
     * @throws IOException When the reading the stream fails
     */
    public static byte[] load(InputStream stream) throws IOException {
        List<byte[]> content = new ArrayList<>();
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = 0;
        int read;
        int size = 0;
        while (true) {
            read = stream.read(buffer, length, BUFFER_SIZE - length);
            if (read == -1) {
                if (length != 0) {
                    content.add(buffer);
                    size += length;
                }
                break;
            }
            length += read;
            if (length == BUFFER_SIZE) {
                content.add(buffer);
                size += BUFFER_SIZE;
                buffer = new byte[BUFFER_SIZE];
                length = 0;
            }
        }

        byte[] result = new byte[size];
        int current = 0;
        for (int i = 0; i != content.size(); i++) {
            if (i == content.size() - 1) {
                // the last buffer
                System.arraycopy(content.get(i), 0, result, current, size - current);
            } else {
                System.arraycopy(content.get(i), 0, result, current, BUFFER_SIZE);
                current += BUFFER_SIZE;
            }
        }
        return result;
    }
}