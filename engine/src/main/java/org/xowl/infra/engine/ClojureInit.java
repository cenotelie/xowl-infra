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

import clojure.lang.Compiler;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import org.xowl.infra.utils.logging.Logging;

/**
 * Initialization code for Clojure
 *
 * @author Laurent Wouters
 */
class ClojureInit {
    /**
     * Whether Clojure has been initialized
     */
    private static boolean isInitialized = false;
    /**
     * The class loader to use for Clojure
     */
    private static ClassLoader classLoader = null;

    /**
     * Performs the initialization of Clojure
     *
     * @param loader The class loader to use
     */
    public static void initialize(ClassLoader loader) {
        if (isInitialized)
            return;
        classLoader = loader;
        (new ClojureInit()).doInit();
        isInitialized = true;
    }

    /**
     * Gets the class loader to use for Clojure
     *
     * @return he class loader to use for Clojure
     */
    public static ClassLoader getClassLoader() {
        if (classLoader != null)
            return classLoader;
        return ClojureInit.class.getClassLoader();
    }

    /**
     * Initializes this instance
     */
    private ClojureInit() {
    }

    /**
     * Performs the initialization of Clojure
     */
    private void doInit() {
        Var cljRequire = RT.var("clojure.core", "require");
        try {
            Var.pushThreadBindings(RT.map(Compiler.LOADER, getClassLoader()));
            try {
                cljRequire.invoke(Symbol.intern("clojure.main"));
                cljRequire.invoke(Symbol.intern("org.xowl.infra.engine.ClojureBindings"));
            } catch (Exception exception) {
                Logging.get().error(exception);
            }
        } finally {
            Var.popThreadBindings();
        }
    }
}
