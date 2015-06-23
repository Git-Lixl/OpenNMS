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

package org.opennms.netmgt.config.categories;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Header containing information about this configuration
 *  file.
 *
 * <p>This class was previously generated using Castor.</p>
 */
@XmlRootElement(name = "header")
@XmlAccessorType(XmlAccessType.FIELD)
public class Header implements java.io.Serializable {
    private static final long serialVersionUID = 8986797727636367768L;

    /**
     * Revision of this file.
     */
    @XmlElement(name = "rev")
    private String m_rev;

    /**
     * Creation time in the 'dow mon dd hh:mm:ss zzz yyyy'
     *  format.
     */
    @XmlElement(name = "created")
    private String m_created;

    /**
     * Monitoring station? This is seemingly
     *  unused.
     */
    @XmlElement(name = "mstation")
    private String m_mstation;

    /**
     * Overrides the java.lang.Object.equals method.
     * 
     * @param obj
     * @return true if the objects are equal.
     */
    @Override()
    public boolean equals(final java.lang.Object obj) {
        if ( this == obj )
            return true;
        
        if (obj instanceof Header) {
        
            Header temp = (Header)obj;
            if (this.m_rev != null) {
                if (temp.m_rev == null) return false;
                else if (!(this.m_rev.equals(temp.m_rev))) 
                    return false;
            }
            else if (temp.m_rev != null)
                return false;
            if (this.m_created != null) {
                if (temp.m_created == null) return false;
                else if (!(this.m_created.equals(temp.m_created))) 
                    return false;
            }
            else if (temp.m_created != null)
                return false;
            if (this.m_mstation != null) {
                if (temp.m_mstation == null) return false;
                else if (!(this.m_mstation.equals(temp.m_mstation))) 
                    return false;
            }
            else if (temp.m_mstation != null)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of field 'created'. The field 'created'
     * has the following description: Creation time in the 'dow mon
     * dd hh:mm:ss zzz yyyy'
     *  format.
     * 
     * @return the value of field 'Created'.
     */
    public String getCreated() {
        return this.m_created;
    }

    /**
     * Returns the value of field 'mstation'. The field 'mstation'
     * has the following description: Monitoring station? This is
     * seemingly
     *  unused.
     * 
     * @return the value of field 'Mstation'.
     */
    public String getMstation() {
        return this.m_mstation;
    }

    /**
     * Returns the value of field 'rev'. The field 'rev' has the
     * following description: Revision of this file.
     * 
     * @return the value of field 'Rev'.
     */
    public String getRev() {
        return this.m_rev;
    }

    /**
     * Overrides the java.lang.Object.hashCode method.
     * <p>
     * The following steps came from <b>Effective Java Programming
     * Language Guide</b> by Joshua Bloch, Chapter 3
     * 
     * @return a hash code value for the object.
     */
    public int hashCode() {
        int result = 17;

        if (m_rev != null) {
           result = 37 * result + m_rev.hashCode();
        }
        if (m_created != null) {
           result = 37 * result + m_created.hashCode();
        }
        if (m_mstation != null) {
           result = 37 * result + m_mstation.hashCode();
        }
        
        return result;
    }

    /**
     * Sets the value of field 'created'. The field 'created' has
     * the following description: Creation time in the 'dow mon dd
     * hh:mm:ss zzz yyyy'
     *  format.
     * 
     * @param created the value of field 'created'.
     */
    public void setCreated(final String created) {
        this.m_created = created;
    }

    /**
     * Sets the value of field 'mstation'. The field 'mstation' has
     * the following description: Monitoring station? This is
     * seemingly
     *  unused.
     * 
     * @param mstation the value of field 'mstation'.
     */
    public void setMstation(final String mstation) {
        this.m_mstation = mstation;
    }

    /**
     * Sets the value of field 'rev'. The field 'rev' has the
     * following description: Revision of this file.
     * 
     * @param rev the value of field 'rev'.
     */
    public void setRev(final String rev) {
        this.m_rev = rev;
    }

}
