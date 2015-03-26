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
import java.lang.reflect.Array;

/**
 * Represents an attribute to a node
 *
 * @author Laurent Wouters
 */
public class Attribute {
    /**
     * The parent dataset
     */
    private final Dataset dataset;
    /**
     * The attribute's name
     */
    private String name;
    /**
     * The type of this attribute
     */
    private int type;
    /**
     * The raw value of this attribute
     */
    private Object value;

    /**
     * Gets the parent dataset
     *
     * @return The parent dataset
     */
    public Dataset getDataset() {
        return dataset;
    }

    /**
     * Gets the name of this attribute
     *
     * @return The name of this attribute
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this attribute
     *
     * @param name The name of this attribute
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the type of this attribute
     *
     * @return The type of this attribute
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the raw value of this attribute
     *
     * @return The raw value of this attribute
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the raw value of this attribute
     *
     * @param value The raw value of this attribute
     */
    public void setValue(Object value) {
        this.value = value;
        if (value == null) {
            this.type = AttributeType.EMPTY;
        } else {
            if (value instanceof Boolean) {
                type = AttributeType.BOOLEAN;
            } else if (value instanceof Byte) {
                type = AttributeType.INT8;
            } else if (value instanceof Character) {
                type = AttributeType.INT16;
            } else if (value instanceof Integer) {
                type = AttributeType.INT32;
            } else if (value instanceof Float) {
                type = AttributeType.FLOAT;
            } else if (value instanceof String) {
                type = AttributeType.STRING;
            } else if (value instanceof Array) {
                type = AttributeType.BYTES;
            }
        }
    }

    /**
     * Initializes this attribute as a clone of the specified one
     *
     * @param dataset The parent dataset
     * @param copy    The attribute to copy
     */
    private Attribute(Dataset dataset, Attribute copy) {
        this.dataset = dataset;
        this.name = copy.name;
        this.type = copy.type;
        this.value = copy.value;
    }

    /**
     * Loads this attribute
     *
     * @param dataset The parent dataset
     * @param input   The input to read from
     * @throws IOException            on reading
     * @throws AttributeTypeException on unexpected attribute type
     */
    public Attribute(Dataset dataset, DataInput input) throws IOException, AttributeTypeException {
        this.dataset = dataset;
        this.name = dataset.getIdentifierValue(input.readInt());
        this.type = input.readInt();
        switch (type) {
            case AttributeType.EMPTY:
                break;
            case AttributeType.INT8:
                value = input.readByte();
                break;
            case AttributeType.INT16:
                value = input.readChar();
                break;
            case AttributeType.INT32:
                value = input.readInt();
                break;
            case AttributeType.FLOAT:
                value = input.readFloat();
                break;
            case AttributeType.BOOLEAN:
                value = (input.readByte() != 0);
                break;
            case AttributeType.STRING:
                value = Utils.readString(input);
                break;
            case AttributeType.BYTES: {
                int length = input.readInt();
                value = Utils.readBytes(input, length);
                break;
            }
            default:
                throw new AttributeTypeException(type);
        }
    }

    /**
     * Initializes this attribute as empty
     *
     * @param dataset The parent dataset
     * @param name    The attribute's name
     */
    public Attribute(Dataset dataset, String name) {
        this.dataset = dataset;
        this.name = name;
        this.type = AttributeType.STRING;
        this.value = "";
    }

    /**
     * Clones this attribute for the specified dataset
     *
     * @param dataset The target dataset
     * @return A clone of this attribute for the specified dataset
     */
    public Attribute clone(Dataset dataset) {
        return new Attribute(dataset, this);
    }

    /**
     * Writes this attribute to the specified writer
     *
     * @param output The output to write to
     * @throws IOException on writing
     */
    public void write(DataOutput output) throws IOException {
        output.writeInt(dataset.resolveIdentifier(name));
        output.writeInt(type);
        switch (type) {
            case AttributeType.EMPTY:
                break;
            case AttributeType.INT8:
                output.writeByte((byte) value);
                break;
            case AttributeType.INT16:
                output.writeChar((char) value);
                break;
            case AttributeType.INT32:
                output.writeInt((int) value);
                break;
            case AttributeType.FLOAT:
                output.writeFloat((float) value);
                break;
            case AttributeType.BOOLEAN:
                output.writeByte((boolean) value ? 1 : 0);
                break;
            case AttributeType.STRING: {
                byte[] bytes = Utils.getStringBytes((String) value);
                output.writeInt(bytes.length);
                output.write(bytes);
                break;
            }
            case AttributeType.BYTES: {
                byte[] bytes = (byte[]) value;
                output.writeInt(bytes.length);
                output.write(bytes);
                break;
            }
        }
    }

    @Override
    public String toString() {
        String valueString = null;
        switch (type) {
            case AttributeType.EMPTY:
                valueString = "<null>";
                break;
            case AttributeType.INT8:
            case AttributeType.INT16:
            case AttributeType.INT32:
                valueString = value.toString();
                break;
            case AttributeType.FLOAT:
                valueString = value.toString();
                break;
            case AttributeType.BOOLEAN:
                valueString = ((boolean) value ? "true" : "false");
                break;
            case AttributeType.STRING: {
                valueString = "\"" + value + "\"";
                break;
            }
            case AttributeType.BYTES: {
                valueString = "<raw bytes>";
                break;
            }
        }
        return name + " = " + valueString;
    }
}
