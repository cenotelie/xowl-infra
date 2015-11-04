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

import org.xowl.store.AbstractRepository;
import org.xowl.store.sparql.Result;
import org.xowl.utils.collections.Couple;

import javax.xml.ws.spi.http.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Utility APIs for the HTTP server
 *
 * @author Laurent Wouters
 */
class Utils {
    /**
     * HTTP header for a user token
     */
    public static final String HEADER_USER_TOKEN = "X-XOWL-Token";
    /**
     * HTTP header for a user login
     */
    public static final String HEADER_USER_LOGIN = "X-XOWL-Login";
    /**
     * HTTP header for a user password
     */
    public static final String HEADER_USER_PASSWORD = "X-XOWL-Password";
    /**
     * HTTP header for a user token
     */
    public static final String HEADER_DATABASE = "X-XOWL-Database";

    /**
     * Gets the request body of the specified request
     *
     * @param exchange The exchange
     * @return The request body
     */
    public static String getRequestBody(HttpExchange exchange) {
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
        } catch (IOException exception) {
            exception.printStackTrace();
            return "";
        }
    }

    /**
     * Retrieves the requested content types by order of preference
     *
     * @param exchange The exchange
     * @return The content types by order of preference
     */
    public static List<String> getContentTypes(HttpExchange exchange) {
        String header = exchange.getRequestHeader("Accept");
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
     * @param exchange The exchange
     */
    public static void enableCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().put("Access-Control-Allow-Methods", Arrays.asList("GET", "POST", "OPTIONS"));
        exchange.getResponseHeaders().put("Access-Control-Allow-Headers", Arrays.asList("Accept", "Content-Type", "Cache-Control"));
        exchange.getResponseHeaders().put("Access-Control-Allow-Origin", Arrays.asList("*"));
        exchange.getResponseHeaders().put("Access-Control-Allow-Credentials", Arrays.asList("false"));
        exchange.getResponseHeaders().put("Cache-Control", Arrays.asList("no-cache"));
    }
}
