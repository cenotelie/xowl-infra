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
package org.xowl.infra.generator.builder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * Represents a jar node for a directory
 *
 * @author Laurent Wouters
 */
class JarNodeDirectory implements JarNode {
    /**
     * The separator in paths
     */
    public static final String SEPARATOR = "/";

    /**
     * The full path for this entry
     */
    private String path;
    /**
     * The child nodes
     */
    private Map<String, JarNode> children;

    /**
     * Initializes this directory node
     *
     * @param path The node's full directory
     */
    public JarNodeDirectory(String path) {
        this.path = path;
        this.children = new HashMap<>();
    }

    /**
     * Adds a new directory node for the specified full path
     *
     * @param path The full path of the new node
     */
    public void add(String path) {
        add(path, path.split(SEPARATOR), 0, null);
    }

    /**
     * Adds a new file node for the specified full path
     *
     * @param path The full path of the new node
     * @param file The file for the node
     */
    public void add(String path, File file) {
        add(path, path.split(SEPARATOR), 0, file);
    }

    @Override
    public void add(String path, String[] parts, int index, File file) {
        String current = parts[index];
        if (index == (parts.length - 1)) {
            if (current.isEmpty())
                return;
            if (children.containsKey(current))
                return;
            JarNode child;
            if (file == null)
                child = new JarNodeDirectory(path);
            else
                child = new JarNodeFile(path, file);
            children.put(current, child);
        } else {
            if (!children.containsKey(current))
                children.put(current, new JarNodeDirectory(this.path + current + SEPARATOR));
            children.get(current).add(path, parts, index + 1, file);
        }
    }

    @Override
    public void createEntry(JarOutputStream stream) throws IOException {
        if (!path.isEmpty()) {
            JarEntry fileEntry = new JarEntry(path);
            stream.putNextEntry(fileEntry);
        }
        for (JarNode child : children.values())
            child.createEntry(stream);
    }
}
