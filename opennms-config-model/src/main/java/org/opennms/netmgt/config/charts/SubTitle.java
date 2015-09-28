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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class SubTitle.
 * 
 * @version $Revision$ $Date$
 */
@XmlRootElement(name = "sub-title")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubTitle implements Serializable {
    private static final long serialVersionUID = 8213354243664518953L;

    /**
     * Field _position.
     */
    @XmlAttribute(name="position")
    private String _position;

    /**
     * Field _horizontalAlignment.
     */
    @XmlAttribute(name="horizontal-alignment")
    private String _horizontalAlignment;

    /**
     * Field _title.
     */
    @XmlElement(name="title")
    private Title _title;

    /**
     * Overrides the Object.equals method.
     * 
     * @param obj
     * @return true if the objects are equal.
     */
    @Override
    public boolean equals(
            final Object obj) {
        if ( this == obj )
            return true;
        
        if (obj instanceof SubTitle) {
            SubTitle temp = (SubTitle)obj;
            if (this._position != null) {
                if (temp._position == null) return false;
                else if (!(this._position.equals(temp._position))) 
                    return false;
            }
            else if (temp._position != null)
                return false;
            if (this._horizontalAlignment != null) {
                if (temp._horizontalAlignment == null) return false;
                else if (!(this._horizontalAlignment.equals(temp._horizontalAlignment))) 
                    return false;
            }
            else if (temp._horizontalAlignment != null)
                return false;
            if (this._title != null) {
                if (temp._title == null) return false;
                else if (!(this._title.equals(temp._title))) 
                    return false;
            }
            else if (temp._title != null)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of field 'horizontalAlignment'.
     * 
     * @return the value of field 'HorizontalAlignment'.
     */
    public String getHorizontalAlignment() {
        return this._horizontalAlignment;
    }

    /**
     * Returns the value of field 'position'.
     * 
     * @return the value of field 'Position'.
     */
    public String getPosition() {
        return this._position;
    }

    /**
     * Returns the value of field 'title'.
     * 
     * @return the value of field 'Title'.
     */
    public Title getTitle() {
        return this._title;
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

        if (_position != null) {
           result = 37 * result + _position.hashCode();
        }
        if (_horizontalAlignment != null) {
           result = 37 * result + _horizontalAlignment.hashCode();
        }
        if (_title != null) {
           result = 37 * result + _title.hashCode();
        }
        
        return result;
    }

    /**
     * Sets the value of field 'horizontalAlignment'.
     * 
     * @param horizontalAlignment the value of field
     * 'horizontalAlignment'.
     */
    public void setHorizontalAlignment(final String horizontalAlignment) {
        this._horizontalAlignment = horizontalAlignment;
    }

    /**
     * Sets the value of field 'position'.
     * 
     * @param position the value of field 'position'.
     */
    public void setPosition(final String position) {
        this._position = position;
    }

    /**
     * Sets the value of field 'title'.
     * 
     * @param title the value of field 'title'.
     */
    public void setTitle(final Title title) {
        this._title = title;
    }

}
