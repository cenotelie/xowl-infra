/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.store;

import fr.cenotelie.commons.utils.IOUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.jar.JarFile;

/**
 * Represents a way to access a resource according to a scheme
 *
 * @author Laurent Wouters
 */
public abstract class ResourceAccess {
    /**
     * Supported http scheme for the physical resources to load
     */
    public static final String SCHEME_HTTP = "http://";
    /**
     * Supported resource scheme for the physical resources to load
     */
    public static final String SCHEME_RESOURCE = "resource://";
    /**
     * Supported jar scheme for the physical resources to load
     */
    public static final String SCHEME_JAR = "jar://";
    /**
     * Supported file scheme for the physical resources to load
     */
    public static final String SCHEME_FILE = "file://";

    /**
     * The registry of resource accessors
     */
    private static final Collection<ResourceAccess> REGISTRY = new ArrayList<>();

    static {
        REGISTRY.add(new ResourceAccess(SCHEME_HTTP) {
            @Override
            public Reader getReader(String resource) throws IOException {
                URL url = new URL(resource);
                URLConnection connection = url.openConnection();
                return new InputStreamReader(connection.getInputStream(), IOUtils.CHARSET);
            }

            @Override
            public Writer getWriter(String resource) throws IOException {
                URL url = new URL(resource);
                URLConnection connection = url.openConnection();
                return new OutputStreamWriter(connection.getOutputStream(), IOUtils.CHARSET);
            }
        });
        REGISTRY.add(new ResourceAccess(SCHEME_RESOURCE) {
            @Override
            public Reader getReader(String resource) throws IOException {
                InputStream stream = Repository.class.getResourceAsStream(resource.substring(SCHEME_RESOURCE.length()));
                return new InputStreamReader(stream, IOUtils.CHARSET);
            }

            @Override
            public Writer getWriter(String resource) throws IOException {
                // cannot write to resources
                return null;
            }
        });
        REGISTRY.add(new ResourceAccess(SCHEME_JAR) {
            @Override
            public Reader getReader(String resource) throws IOException {
                String parts[] = resource.substring(SCHEME_JAR.length()).split("!");
                JarFile jar = new JarFile(parts[0]);
                InputStream stream = jar.getInputStream(jar.getEntry(parts[1]));
                return new InputStreamReader(stream, IOUtils.CHARSET);
            }

            @Override
            public Writer getWriter(String resource) throws IOException {
                // cannot write to jar
                return null;
            }
        });
        REGISTRY.add(new ResourceAccess(SCHEME_FILE) {
            @Override
            public Reader getReader(String resource) throws IOException {
                return IOUtils.getReader(resource.substring(SCHEME_FILE.length()));
            }

            @Override
            public Writer getWriter(String resource) throws IOException {
                return IOUtils.getWriter(resource.substring(SCHEME_FILE.length()));
            }
        });
    }

    /**
     * Gets the accessor for the specified resource
     *
     * @param resource The resource
     * @return The accessor for the resource, or null if there is none
     */
    public static ResourceAccess getAccessFor(String resource) {
        for (ResourceAccess access : REGISTRY) {
            if (resource.startsWith(access.scheme))
                return access;
        }
        return null;
    }

    /**
     * The scheme for this kind of access
     */
    private final String scheme;

    /**
     * Initializes this kind of access
     *
     * @param scheme The scheme for this kind of access
     */
    public ResourceAccess(String scheme) {
        this.scheme = scheme;
    }

    /**
     * Gets a reader for the specified resource
     *
     * @param resource The resource
     * @return The reader
     * @throws IOException When the resource cannot be accessed
     */
    public abstract Reader getReader(String resource) throws IOException;

    /**
     * Gets a writer for the specified resource
     *
     * @param resource The resource
     * @return The writer
     * @throws IOException When the resource cannot be accessed
     */
    public abstract Writer getWriter(String resource) throws IOException;
}
