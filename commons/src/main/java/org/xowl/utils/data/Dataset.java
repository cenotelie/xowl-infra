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
package org.xowl.utils.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * New code
 *
 * @author Laurent Wouters
 */
public class Dataset {
    /**
     * Loads a dataset from the specified uri
     *
     * @param uri The uri to load from
     * @return The loaded dataset
     * @throws IOException            on reading
     * @throws AttributeTypeException on unexpected attribute type
     */
    public static Dataset load(String uri) throws IOException, AttributeTypeException {
        File file = new File(uri);
        FileInputStream stream = new FileInputStream(file);
        DataInputStream input = new DataInputStream(stream);
        Dataset dataset = new Dataset(input);
        input.close();
        return dataset;
    }

    /**
     * The map of identifiers
     */
    private final List<String> identifiers;
    /**
     * The trees in this dataset
     */
    private final List<Node> trees;

    /**
     * Gets the trees in this dataset
     *
     * @return The trees in this dataset
     */
    public List<Node> getTrees() {
        return trees;
    }

    /**
     * Gets the tree with the specified name
     *
     * @param name The name of a tree
     * @return The tree with the specified name, or null if there is none
     */
    public Node tree(String name) {
        for (Node child : trees)
            if (child.getName().equals(name))
                return child;
        return null;
    }

    /**
     * Initializes an empty dataset
     */
    public Dataset() {
        this.identifiers = new ArrayList<>();
        this.trees = new ArrayList<>();
    }

    /**
     * Loads this dataset
     *
     * @param input The input to load from
     * @throws IOException            on reading
     * @throws AttributeTypeException on unexpected attribute type
     */
    private Dataset(DataInput input) throws IOException, AttributeTypeException {
        this.identifiers = new ArrayList<>();
        this.trees = new ArrayList<>();
        int nbIDs = input.readInt();
        int nbTrees = input.readInt();
        for (int i = 0; i != nbIDs; i++)
            this.identifiers.add(Utils.readString(input));
        for (int i = 0; i != nbTrees; i++)
            this.trees.add(new Node(this, input));
    }

    /**
     * Resolves the specified identifier's name
     *
     * @param identifier An identifier's name
     * @return The associated identifier's key
     */
    public int resolveIdentifier(String identifier) {
        for (int i = 0; i != identifiers.size(); i++) {
            if (identifiers.get(i).equals(identifier))
                return i;
        }
        identifiers.add(identifier);
        return identifiers.size() - 1;
    }

    /**
     * Gets the identifier's name for the specified key
     *
     * @param key A key to an identifier
     * @return The associated identifier's name
     */
    public String getIdentifierValue(int key) {
        return identifiers.get(key);
    }

    /**
     * Cleanup the identifiers registry
     */
    private void cleanIdentifier() {
        identifiers.clear();
        Stack<Node> stack = new Stack<>();
        for (Node tree : trees)
            stack.push(tree);
        while (!stack.isEmpty()) {
            Node current = stack.pop();
            resolveIdentifier(current.getName());
            for (Attribute attribute : current.getAttributes())
                resolveIdentifier(attribute.getName());
            for (Node child : current.getChildren())
                stack.push(child);
        }
    }

    /**
     * Writes this dataset to the specified writer
     *
     * @param output The output to write to
     * @throws IOException on writing
     */
    public void write(DataOutput output) throws IOException {
        cleanIdentifier();
        output.writeInt(identifiers.size());
        output.writeInt(trees.size());
        for (String id : identifiers) {
            byte[] bytes = Utils.getStringBytes(id);
            output.writeInt(bytes.length);
            output.write(bytes);
        }
        for (Node tree : trees)
            tree.write(output);
    }

    /**
     * Writes this dataset fo the specified uri
     *
     * @param uri The uri to write to
     * @throws IOException on writing
     */
    public void write(String uri) throws IOException {
        File file = new File(uri);
        FileOutputStream stream = new FileOutputStream(file);
        DataOutputStream output = new DataOutputStream(stream);
        write(output);
        output.close();
    }

    @Override
    public String toString() {
        return "Dataset";
    }
}
