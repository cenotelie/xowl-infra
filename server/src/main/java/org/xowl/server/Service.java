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

package org.xowl.server;

import org.xowl.store.AbstractRepository;
import org.xowl.store.sparql.Result;
import org.xowl.utils.collections.Couple;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a service for the server
 *
 * @author Laurent Wouters
 */
public abstract class Service {

    /**
     * Gets the message body of the specified request
     *
     * @param request A request
     * @return The message body
     */
    protected String getMessageBody(HttpServletRequest request) {
        if (request.getContentLength() <= 0)
            return "";
        try (BufferedReader reader = request.getReader()) {
            char[] buffer = new char[1024];
            StringBuilder builder = new StringBuilder();
            int read = reader.read(buffer);
            while (read > 0) {
                builder.append(buffer, 0, read);
                read = reader.read(buffer);
            }
            return builder.toString();
        } catch (IOException exception) {
            return "";
        }
    }

    /**
     * Retrieves the requested content types by order of preference
     *
     * @param request The request
     * @return The content types by order of preference
     */
    protected List<String> getContentTypes(HttpServletRequest request) {
        String header = request.getHeader("Accept");
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
    protected String negotiateType(List<String> contentTypes) {
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
                    return contentType;
            }
        }
        return AbstractRepository.SYNTAX_NQUADS;
    }

    /**
     * Setups the headers of the specified HTTP response in order to enable Cross-Origin Resource Sharing
     *
     * @param response The response to setup
     */
    protected void enableCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Accept, Content-Type, Cache-Control");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "false");
        response.setHeader("Cache-Control", "no-cache");
    }

    /**
     * Responds to a GET request
     *
     * @param request  The request
     * @param response The response to build
     */
    public void onGet(HttpServletRequest request, HttpServletResponse response) {
    }

    /**
     * Responds to a POST request
     *
     * @param request  The request
     * @param response The response to build
     */
    public void onPost(HttpServletRequest request, HttpServletResponse response) {
    }

    /**
     * Responds to an OPTIONS request
     *
     * @param request  The request
     * @param response The response to build
     */
    public void onOptions(HttpServletRequest request, HttpServletResponse response) {
        enableCORS(response);
    }
}
