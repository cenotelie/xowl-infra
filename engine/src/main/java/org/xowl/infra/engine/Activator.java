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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.xowl.infra.store.execution.ExecutionManagerProvider;

/**
 * Activator for this bundle
 *
 * @author Laurent Wouters
 */
public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        ClassLoader clojureClassLoader = new ClojureClassLoader(bundleContext.getBundle());
        ClassLoader priorClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(clojureClassLoader);
            ClojureInit.initialize(clojureClassLoader);
        } finally {
            Thread.currentThread().setContextClassLoader(priorClassLoader);
        }
        bundleContext.registerService(ExecutionManagerProvider.class, new ClojureExecutionManagerProvider(), null);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {

    }
}
