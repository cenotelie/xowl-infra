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

package org.xowl.infra.server.standalone;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.collections.Couple;
import org.xowl.infra.utils.http.HttpConstants;
import org.xowl.infra.utils.logging.Logging;
import org.xowl.infra.utils.product.EmbeddedDependency;
import org.xowl.infra.utils.product.Product;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Utility APIs for the HTTP server
 *
 * @author Laurent Wouters
 */
class Utils {
    /**
     * The product description for this server
     */
    private static Product PRODUCT;

    /**
     * Gets the product description for this server
     *
     * @return The product description
     */
    public static synchronized Product getProduct() {
        if (PRODUCT == null) {
            try {
                PRODUCT = new Product(
                        "org.xowl.infra.server.TripleStoreServer",
                        "xOWL Triple Store Server",
                        Program.class);
            } catch (IOException exception) {
                Logging.getDefault().error(exception);
            }
        }
        return PRODUCT;
    }

    /**
     * The dependencies embedded into this product
     */
    private static Collection<EmbeddedDependency> DEPENDENCIES;

    /**
     * Gets the dependencies embedded into this product
     *
     * @return The embedded dependencies
     */
    public static synchronized Collection<EmbeddedDependency> getDependencies() {
        if (DEPENDENCIES == null) {
            try {
                DEPENDENCIES = Collections.unmodifiableCollection(EmbeddedDependency.getDependenciesFor(Program.class));
            } catch (IOException exception) {
                Logging.getDefault().error(exception);
            }
        }
        return DEPENDENCIES;
    }

    /**
     * Gets the request body of the specified request
     *
     * @param exchange The exchange
     * @return The request body
     * @throws IOException When reading failed
     */
    public static String getRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream stream = exchange.getRequestBody()) {
            InputStream input = stream;
            if ("gzip".equals(exchange.getRequestHeaders().getFirst(HttpConstants.HEADER_CONTENT_ENCODING))) {
                input = new GZIPInputStream(stream);
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = input.read(buffer);
            while (read > 0) {
                output.write(buffer, 0, read);
                read = input.read(buffer);
            }
            buffer = output.toByteArray();
            output.close();
            return new String(buffer, Files.CHARSET);
        }
    }

    /**
     * Gets the content type of a request
     *
     * @param headers The request headers
     * @return The content type of a request
     */
    public static String getRequestContentType(Headers headers) {
        String type = headers.getFirst(HttpConstants.HEADER_CONTENT_TYPE);
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
        Map<String, List<String>> result = new HashMap<>();
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
                    key = URLDecoder.decode(param[0], Files.CHARSET.name());
                } catch (UnsupportedEncodingException exception) {
                    Logging.getDefault().error(exception);
                }
            }
            if (param.length > 1) {
                try {
                    value = URLDecoder.decode(param[1], Files.CHARSET.name());
                } catch (UnsupportedEncodingException exception) {
                    Logging.getDefault().error(exception);
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
        String header = headers.getFirst(HttpConstants.HEADER_ACCEPT);
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
     * Setups the headers of the specified HTTP response in order to enable Cross-Origin Resource Sharing
     *
     * @param requestHeaders  The response headers
     * @param responseHeaders The response headers
     */
    public static void enableCORS(Headers requestHeaders, Headers responseHeaders) {
        String origin = requestHeaders.getFirst(HttpConstants.HEADER_ORIGIN);
        if (origin == null) {
            // the request is from the same host
            origin = requestHeaders.getFirst(HttpConstants.HEADER_HOST);
        }
        responseHeaders.put(HttpConstants.HEADER_ACCESS_CONTROL_ALLOW_METHODS, Arrays.asList(
                HttpConstants.METHOD_OPTIONS,
                HttpConstants.METHOD_GET,
                HttpConstants.METHOD_POST,
                HttpConstants.METHOD_PUT,
                HttpConstants.METHOD_DELETE));
        responseHeaders.put(HttpConstants.HEADER_ACCESS_CONTROL_ALLOW_HEADERS, Arrays.asList(
                HttpConstants.HEADER_ACCEPT,
                HttpConstants.HEADER_CONTENT_TYPE,
                HttpConstants.HEADER_COOKIE,
                HttpConstants.HEADER_CACHE_CONTROL));
        responseHeaders.put(HttpConstants.HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, Collections.singletonList(origin));
        responseHeaders.put(HttpConstants.HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, Collections.singletonList("true"));
    }
}
