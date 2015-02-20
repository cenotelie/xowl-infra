/**********************************************************************
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
 **********************************************************************/
package org.xowl.generator.builder;

import java.io.File;
import java.util.jar.JarOutputStream;

/**
 * Represents a node for a jar file
 *
 * @author Laurent Wouters
 */
interface JarNode {
    /**
     * Adds an entry for a file
     *
     * @param path  The full path for the entry
     * @param parts The parts within the path
     * @param index The index of the current part
     * @param file  The file for the entry
     */
    void add(String path, String[] parts, int index, File file);

    /**
     * Creates the entry in the specified stream
     *
     * @param stream The stream
     * @throws java.io.IOException When a serialization error occurs
     */
    void createEntry(JarOutputStream stream) throws java.io.IOException;
}
