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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node of data
 *
 * @author Laurent Wouters
 */
public class Node {
    /**
     * The parent dataset
     */
    private final Dataset dataset;
    /**
     * The attribute's name
     */
    private String name;
    /**
     * The attributes of this node
     */
    private List<Attribute> attributes;
    /**
     * The children of this node
     */
    private List<Node> children;

    /**
     * Gets the parent dataset
     *
     * @return The parent dataset
     */
    public Dataset getDataset() {
        return dataset;
    }

    /**
     * Gets the name of this node
     *
     * @return The name of this node
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this node
     *
     * @param name The name of this node
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the attributes of this node
     *
     * @return The attributes of this node
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Gets the children of this node
     *
     * @return The children of this node
     */
    public List<Node> getChildren() {
        return children;
    }

    /**
     * Gets the attribute with the specified name
     *
     * @param name The name of an attribute
     * @return The attribute with the specified name, or null if there is none
     */
    public Attribute attribute(String name) {
        for (Attribute attribute : attributes)
            if (attribute.getName().equals(name))
                return attribute;
        return null;
    }

    /**
     * Gets the child node with the specified name
     *
     * @param name The name of a node
     * @return The child node with the specified name, or null if there is none
     */
    public Node child(String name) {
        for (Node child : children)
            if (child.getName().equals(name))
                return child;
        return null;
    }

    /**
     * Initializes this node as a clone of the specified one
     *
     * @param dataset The parent dataset
     * @param copy    The node to copy
     */
    private Node(Dataset dataset, Node copy) {
        this.dataset = dataset;
        this.name = copy.name;
        this.attributes = new ArrayList<>();
        this.children = new ArrayList<>();
        for (Attribute attribute : copy.attributes)
            this.attributes.add(attribute.clone(dataset));
        for (Node child : copy.children)
            this.children.add(new Node(dataset, child));
    }

    /**
     * Loads this node
     *
     * @param dataset The parent dataset
     * @param input   The input to read from
     * @throws IOException            on reading
     * @throws AttributeTypeException on unexpected attribute type
     */
    public Node(Dataset dataset, DataInput input) throws IOException, AttributeTypeException {
        this.dataset = dataset;
        this.name = dataset.getIdentifierValue(input.readInt());
        int nbAttributes = input.readInt();
        int nbChildren = input.readInt();
        this.attributes = new ArrayList<>();
        this.children = new ArrayList<>();
        for (int i = 0; i != nbAttributes; i++)
            this.attributes.add(new Attribute(dataset, input));
        for (int i = 0; i != nbChildren; i++)
            this.children.add(new Node(dataset, input));
    }

    /**
     * Initializes this node as empty
     *
     * @param dataset The parent dataset
     * @param name    The node's name
     */
    public Node(Dataset dataset, String name) {
        this.dataset = dataset;
        this.name = name;
        this.attributes = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    /**
     * Clears the attributes and children of this node
     */
    public void clear() {
        attributes.clear();
        children.clear();
    }

    /**
     * Clones this node for the specified dataset
     *
     * @param dataset The target dataset
     * @return A clone of this node for the specified dataset
     */
    public Node clone(Dataset dataset) {
        return new Node(dataset, this);
    }

    /**
     * Writes this node to the specified writer
     *
     * @param output The output to write to
     * @throws IOException on writing
     */
    public void write(DataOutput output) throws IOException {
        output.writeInt(dataset.resolveIdentifier(name));
        output.writeInt(attributes.size());
        output.writeInt(children.size());
        for (Attribute attribute : attributes)
            attribute.write(output);
        for (Node child : children)
            child.write(output);
    }

    @Override
    public String toString() {
        Attribute id = attribute("id");
        if (id != null)
            return name + " <<id = " + id.getValue() + ">>";
        return name;
    }
}
