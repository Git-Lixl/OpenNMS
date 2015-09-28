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
 * Class ImageSize.
 * 
 * @version $Revision$ $Date$
 */
@XmlRootElement(name = "image-size")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImageSize implements Serializable {
    private static final long serialVersionUID = -1522407392573439425L;

    /**
     * Field _hzSize.
     */
    @XmlElement(name="hz-size")
    private HzSize _hzSize;

    /**
     * Field _vtSize.
     */
    @XmlElement(name="vt-size")
    private VtSize _vtSize;

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
        
        if (obj instanceof ImageSize) {
        
            ImageSize temp = (ImageSize)obj;
            if (_hzSize != null) {
                if (temp._hzSize == null) return false;
                else if (!(_hzSize.equals(temp._hzSize))) 
                    return false;
            }
            else if (temp._hzSize != null)
                return false;
            if (_vtSize != null) {
                if (temp._vtSize == null) return false;
                else if (!(_vtSize.equals(temp._vtSize))) 
                    return false;
            }
            else if (temp._vtSize != null)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of field 'hzSize'.
     * 
     * @return the value of field 'HzSize'.
     */
    public HzSize getHzSize() {
        return _hzSize;
    }

    /**
     * Returns the value of field 'vtSize'.
     * 
     * @return the value of field 'VtSize'.
     */
    public VtSize getVtSize() {
        return _vtSize;
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

        if (_hzSize != null) {
           result = 37 * result + _hzSize.hashCode();
        }
        if (_vtSize != null) {
           result = 37 * result + _vtSize.hashCode();
        }

        return result;
    }

    /**
     * Sets the value of field 'hzSize'.
     * 
     * @param hzSize the value of field 'hzSize'.
     */
    public void setHzSize(final HzSize hzSize) {
        _hzSize = hzSize;
    }

    /**
     * Sets the value of field 'vtSize'.
     * 
     * @param vtSize the value of field 'vtSize'.
     */
    public void setVtSize(final VtSize vtSize) {
        _vtSize = vtSize;
    }

}
