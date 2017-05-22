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

package org.xowl.infra.engine;

import org.osgi.framework.Bundle;

import java.net.URL;

/**
 * The class loader to use for Clojure
 *
 * @author Laurent Wouters
 */
class ClojureClassLoader extends ClassLoader {
    /**
     * The current bundle
     */
    private Bundle bundle;
    /**
     * Whether to use direct access to the bundle for resolving resources
     */
    private boolean forceDirect;

    /**
     * Initializes this class loader
     *
     * @param bundle The current bundle
     */
    public ClojureClassLoader(Bundle bundle) {
        this(bundle, false);
    }

    /**
     * Initializes this class loader
     *
     * @param bundle      The current bundle
     * @param forceDirect Whether to use direct access to the bundle for resolving resources
     */
    public ClojureClassLoader(Bundle bundle, boolean forceDirect) {
        this.bundle = bundle;
        this.forceDirect = forceDirect;
    }

    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return bundle.loadClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return bundle.loadClass(name);
    }

    @Override
    public URL getResource(String name) {
        if (forceDirect) {
            return bundle.getEntry(name);
        }
        return bundle.getResource(name);
    }
}
