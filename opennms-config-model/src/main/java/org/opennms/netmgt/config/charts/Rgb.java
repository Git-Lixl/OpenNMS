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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class Rgb.
 * 
 * @version $Revision$ $Date$
 */
@XmlRootElement(name = "rgb")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rgb implements Serializable {
    private static final long serialVersionUID = -8277806567962836142L;

    /**
     * Field _red.
     */
    @XmlElement(name="red")
    private Red _red;

    /**
     * Field _green.
     */
    @XmlElement(name="green")
    private Green _green;

    /**
     * Field _blue.
     */
    @XmlElement(name="blue")
    private Blue _blue;

    /**
     * Overrides the Object.equals method.
     * 
     * @param obj
     * @return true if the objects are equal.
     */
    @Override()
    public boolean equals(
            final Object obj) {
        if ( this == obj )
            return true;
        
        if (obj instanceof Rgb) {
        
            Rgb temp = (Rgb)obj;
            if (this._red != null) {
                if (temp._red == null) return false;
                else if (!(this._red.equals(temp._red))) 
                    return false;
            }
            else if (temp._red != null)
                return false;
            if (this._green != null) {
                if (temp._green == null) return false;
                else if (!(this._green.equals(temp._green))) 
                    return false;
            }
            else if (temp._green != null)
                return false;
            if (this._blue != null) {
                if (temp._blue == null) return false;
                else if (!(this._blue.equals(temp._blue))) 
                    return false;
            }
            else if (temp._blue != null)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of field 'blue'.
     * 
     * @return the value of field 'Blue'.
     */
    public Blue getBlue(
    ) {
        return this._blue;
    }

    /**
     * Returns the value of field 'green'.
     * 
     * @return the value of field 'Green'.
     */
    public Green getGreen(
    ) {
        return this._green;
    }

    /**
     * Returns the value of field 'red'.
     * 
     * @return the value of field 'Red'.
     */
    public Red getRed(
    ) {
        return this._red;
    }

    /**
     * Overrides the Object.hashCode method.
     * <p>
     * The following steps came from <b>Effective Java Programming
     * Language Guide</b> by Joshua Bloch, Chapter 3
     * 
     * @return a hash code value for the object.
     */
    public int hashCode(
    ) {
        int result = 17;

        if (_red != null) {
           result = 37 * result + _red.hashCode();
        }
        if (_green != null) {
           result = 37 * result + _green.hashCode();
        }
        if (_blue != null) {
           result = 37 * result + _blue.hashCode();
        }
        
        return result;
    }

    /**
     * Sets the value of field 'blue'.
     * 
     * @param blue the value of field 'blue'.
     */
    public void setBlue(
            final Blue blue) {
        this._blue = blue;
    }

    /**
     * Sets the value of field 'green'.
     * 
     * @param green the value of field 'green'.
     */
    public void setGreen(
            final Green green) {
        this._green = green;
    }

    /**
     * Sets the value of field 'red'.
     * 
     * @param red the value of field 'red'.
     */
    public void setRed(
            final Red red) {
        this._red = red;
    }

}
