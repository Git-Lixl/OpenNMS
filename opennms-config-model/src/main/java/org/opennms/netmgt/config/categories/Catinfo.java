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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Top-level element for the categories.xml configuration
 *  file.
 *
 * <p>This class was previously generated using Castor.</p>
 */
@XmlRootElement(name = "catinfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class Catinfo implements java.io.Serializable {
    private static final long serialVersionUID = 2034568803488337863L;

    /**
     * Header for this file.
     */
    @XmlElement(name = "header")
    private Header m_header;

    /**
     * The category groups.
     */
    @XmlElement(name = "categorygroup")
    private List<Categorygroup> m_categorygroupList = new ArrayList<>();

    public void setHeader(Header header) {
        m_header = header;
    }

    public Header getHeader() {
        return m_header;
    }

    public int getCategorygroupCount() {
        return m_categorygroupList != null ? m_categorygroupList.size() : 0;
    }

    public List<Categorygroup> getCategorygroup() {
        return m_categorygroupList;
    }

    public Categorygroup getCategorygroup(int idx) {
        return m_categorygroupList.get(idx);
    }

    public void setCategorygroup(int idx, Categorygroup categoryGroup) {
        m_categorygroupList.set(idx, categoryGroup);
    }

    public boolean removeCategorygroup(Categorygroup categoryGroup) {
        return m_categorygroupList.remove(categoryGroup);
    }

    public Categorygroup removeCategorygroup(int idx) {
        return m_categorygroupList.remove(idx);
    }

    public Enumeration<Categorygroup> enumerateCategorygroup() {
        return Collections.enumeration(m_categorygroupList);
    }

    @XmlTransient
    public List<Categorygroup> getCategorygroupCollection() {
        return m_categorygroupList;
    }
    
    /**
     * 
     * 
     * @param vCategorygroup
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCategorygroup(final Categorygroup vCategorygroup) {
        m_categorygroupList.add(vCategorygroup);
    }

    /**
     * 
     * 
     * @param index
     * @param vCategorygroup
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCategorygroup(final int index, final Categorygroup vCategorygroup) {
        m_categorygroupList.add(index, vCategorygroup);
    }

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
        
        if (obj instanceof Catinfo) {
        
            Catinfo temp = (Catinfo)obj;
            if (this.m_header != null) {
                if (temp.m_header == null) return false;
                else if (!(this.m_header.equals(temp.m_header))) 
                    return false;
            }
            else if (temp.m_header != null)
                return false;
            if (this.m_categorygroupList != null) {
                if (temp.m_categorygroupList == null) return false;
                else if (!(this.m_categorygroupList.equals(temp.m_categorygroupList))) 
                    return false;
            }
            else if (temp.m_categorygroupList != null)
                return false;
            return true;
        }
        return false;
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

        if (m_header != null) {
           result = 37 * result + m_header.hashCode();
        }
        if (m_categorygroupList != null) {
           result = 37 * result + m_categorygroupList.hashCode();
        }
        
        return result;
    }
}
