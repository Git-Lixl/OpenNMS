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

//---------------------------------/
//- Imported classes and packages -/
//---------------------------------/

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class Title.
 * 
 * @version $Revision$ $Date$
 */
@XmlRootElement(name = "title")
@XmlAccessorType(XmlAccessType.FIELD)
public class Title implements Serializable {
    private static final long serialVersionUID = 2747919136691598913L;

    private static final int DEFAULT_PITCH = 1;

    /**
     * Field _value.
     */
    @XmlAttribute(name="value")
    private String _value;

    /**
     * Field _font.
     */
    @XmlAttribute(name="font")
    private String _font;

    /**
     * Field _pitch.
     */
    @XmlAttribute(name="pitch")
    private Integer _pitch;

    /**
     * Field _style.
     */
    @XmlAttribute(name="style")
    private String _style;

    /**
     * Field _rgb.
     */
    @XmlElement(name="rgb")
    private Rgb _rgb;

    /**
     */
    public void deletePitch(
    ) {
        this._pitch= null;
    }

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
        
        if (obj instanceof Title) {
        
            Title temp = (Title)obj;
            if (this._value != null) {
                if (temp._value == null) return false;
                else if (!(this._value.equals(temp._value))) 
                    return false;
            }
            else if (temp._value != null)
                return false;
            if (this._font != null) {
                if (temp._font == null) return false;
                else if (!(this._font.equals(temp._font))) 
                    return false;
            }
            else if (temp._font != null)
                return false;
            if (this._pitch != temp._pitch)
                return false;
            if (this._style != null) {
                if (temp._style == null) return false;
                else if (!(this._style.equals(temp._style))) 
                    return false;
            }
            else if (temp._style != null)
                return false;
            if (this._rgb != null) {
                if (temp._rgb == null) return false;
                else if (!(this._rgb.equals(temp._rgb))) 
                    return false;
            }
            else if (temp._rgb != null)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of field 'font'.
     * 
     * @return the value of field 'Font'.
     */
    public String getFont(
    ) {
        return this._font;
    }

    /**
     * Returns the value of field 'pitch'.
     * 
     * @return the value of field 'Pitch'.
     */
    public int getPitch(
    ) {
        return this._pitch != null ? this._pitch : DEFAULT_PITCH;
    }

    /**
     * Returns the value of field 'rgb'.
     * 
     * @return the value of field 'Rgb'.
     */
    public Rgb getRgb(
    ) {
        return this._rgb;
    }

    /**
     * Returns the value of field 'style'.
     * 
     * @return the value of field 'Style'.
     */
    public String getStyle(
    ) {
        return this._style;
    }

    /**
     * Returns the value of field 'value'.
     * 
     * @return the value of field 'Value'.
     */
    public String getValue(
    ) {
        return this._value;
    }

    /**
     * Method hasPitch.
     * 
     * @return true if at least one Pitch has been added
     */
    public boolean hasPitch(
    ) {
        return this._pitch != null;
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

        if (_value != null) {
           result = 37 * result + _value.hashCode();
        }
        if (_font != null) {
           result = 37 * result + _font.hashCode();
        }
        result = 37 * result + _pitch;
        if (_style != null) {
           result = 37 * result + _style.hashCode();
        }
        if (_rgb != null) {
           result = 37 * result + _rgb.hashCode();
        }
        
        return result;
    }

    /**
     * Sets the value of field 'font'.
     * 
     * @param font the value of field 'font'.
     */
    public void setFont(
            final String font) {
        this._font = font;
    }

    /**
     * Sets the value of field 'pitch'.
     * 
     * @param pitch the value of field 'pitch'.
     */
    public void setPitch(
            final int pitch) {
        this._pitch = pitch;
    }

    /**
     * Sets the value of field 'rgb'.
     * 
     * @param rgb the value of field 'rgb'.
     */
    public void setRgb(
            final Rgb rgb) {
        this._rgb = rgb;
    }

    /**
     * Sets the value of field 'style'.
     * 
     * @param style the value of field 'style'.
     */
    public void setStyle(
            final String style) {
        this._style = style;
    }

    /**
     * Sets the value of field 'value'.
     * 
     * @param value the value of field 'value'.
     */
    public void setValue(
            final String value) {
        this._value = value;
    }

}
