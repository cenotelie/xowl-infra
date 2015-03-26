/**********************************************************************
 * Copyright (c) 2015 Laurent Wouters
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3
 * of the License; or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful;
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not; see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 **********************************************************************/
package org.xowl.utils.data;

/**
 * Represents the type of an attribute
 *
 * @author Laurent Wouters
 */
class AttributeType {
    public static final int EMPTY = 0x00;
    public static final int BOOLEAN = 0x01;
    public static final int INT8 = 0x02;
    public static final int INT16 = 0x03;
    public static final int INT32 = 0x04;
    public static final int FLOAT = 0x05;
    public static final int STRING = 0x06;
    public static final int BYTES = 0x07;
}
