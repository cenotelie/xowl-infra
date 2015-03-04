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

package org.xowl.generator.model;

/**
 * Represents the interface of a property
 *
 * @author Laurent Wouters
 */
public class PropertyInterface extends PropertyData {
    /**
     * Initializes this property data
     *
     * @param classe   The parent class model
     * @param property The associated property
     */
    public PropertyInterface(ClassModel classe, PropertyModel property) {
        super(classe, property);
    }

    /**
     * Sets that this property is in a type restriction chain
     */
    public void setInTypeRestrictionChain() {
        inTypeRestrictionChain = true;
    }

    /**
     * Gets whether this property is in a type restriction chain
     *
     * @return Whether this property is in a type restriction chain
     */
    public boolean isInTypeRestrictionChain() {
        return inTypeRestrictionChain;
    }

    /**
     * Gets whether this property interface is the same as the specified one
     *
     * @param propertyInterface Another property interface
     * @return true if the two property interfaces matches
     */
    public boolean sameAs(PropertyInterface propertyInterface) {
        if (this.property != propertyInterface.property)
            return false;
        if (this.rangeClass != propertyInterface.rangeClass)
            return false;
        if (this.rangeDatatype != propertyInterface.rangeDatatype)
            return false;
        return (this.isVector() == propertyInterface.isVector());
    }
}
