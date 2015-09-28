/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2002-2015 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2015 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.config.charts;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class Green.
 * 
 * @version $Revision$ $Date$
 */
@XmlRootElement(name = "green")
@XmlAccessorType(XmlAccessType.FIELD)
public class Green implements Serializable {
    private static final long serialVersionUID = 4941711150783407102L;

    /**
     * Field _rgbColor.
     */
    @XmlElement(name="rgb-color")
    private Integer _rgbColor;

    /**
     */
    public void deleteRgbColor() {
        _rgbColor = null;
    }

    /**
     * Overrides the Object.equals method.
     * 
     * @param obj
     * @return true if the objects are equal.
     */
    @Override
    public boolean equals(final Object obj) {
        if ( this == obj )
            return true;

        if (obj instanceof Green) {
            Green temp = (Green)obj;
            if (!Objects.equals(this._rgbColor, temp._rgbColor))
                return false;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of field 'rgbColor'.
     * 
     * @return the value of field 'RgbColor'.
     */
    public int getRgbColor() {
        return _rgbColor;
    }

    /**
     * Method hasRgbColor.
     * 
     * @return true if at least one RgbColor has been added
     */
    public boolean hasRgbColor() {
        return _rgbColor != null;
    }

    /**
     * Overrides the Object.hashCode method.
     * <p>
     * The following steps came from <b>Effective Java Programming
     * Language Guide</b> by Joshua Bloch, Chapter 3
     * 
     * @return a hash code value for the object.
     */
    public int hashCode() {
        int result = 17;

        result = 37 * result + _rgbColor;
        
        return result;
    }

    /**
     * Sets the value of field 'rgbColor'.
     * 
     * @param rgbColor the value of field 'rgbColor'.
     */
    public void setRgbColor(final int rgbColor) {
        _rgbColor = rgbColor;
    }

}
