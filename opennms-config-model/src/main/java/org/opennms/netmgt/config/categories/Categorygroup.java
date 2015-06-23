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
 * A category group containing categories. The only parts of
 *  the category group that seem to be used are the common element
 * and the
 *  list of categories.
 *
 * <p>This class was previously generated using Castor.</p>
 */
@XmlRootElement(name = "categorygroup")
@XmlAccessorType(XmlAccessType.FIELD)
public class Categorygroup implements java.io.Serializable {
    private static final long serialVersionUID = 1920236941652726768L;

    /**
     * The name of the category group. This is seemingly
     *  unused.
     */
    @XmlElement(name = "name")
    private String m_name;

    /**
     * A comment describing the category group. This is
     *  seemingly unused.
     */
    @XmlElement(name = "comment")
    private String m_comment;

    /**
     * Common attributes that apply to all categories in
     *  the group.
     */
    @XmlElement(name = "common")
    private Common m_common;

    /**
     * The categories belonging to this category
     *  group.
     */
    @XmlElement(name = "categories")
    private Categories m_categories;


      //----------------/
     //- Constructors -/
    //----------------/

    public Categorygroup() {
        super();
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
        
        if (obj instanceof Categorygroup) {
        
            Categorygroup temp = (Categorygroup)obj;
            if (this.m_name != null) {
                if (temp.m_name == null) return false;
                else if (!(this.m_name.equals(temp.m_name))) 
                    return false;
            }
            else if (temp.m_name != null)
                return false;
            if (this.m_comment != null) {
                if (temp.m_comment == null) return false;
                else if (!(this.m_comment.equals(temp.m_comment))) 
                    return false;
            }
            else if (temp.m_comment != null)
                return false;
            if (this.m_common != null) {
                if (temp.m_common == null) return false;
                else if (!(this.m_common.equals(temp.m_common))) 
                    return false;
            }
            else if (temp.m_common != null)
                return false;
            if (this.m_categories != null) {
                if (temp.m_categories == null) return false;
                else if (!(this.m_categories.equals(temp.m_categories))) 
                    return false;
            }
            else if (temp.m_categories != null)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of field 'categories'. The field
     * 'categories' has the following description: The categories
     * belonging to this category
     *  group.
     * 
     * @return the value of field 'Categories'.
     */
    public Categories getCategories() {
        return this.m_categories;
    }

    /**
     * Returns the value of field 'comment'. The field 'comment'
     * has the following description: A comment describing the
     * category group. This is
     *  seemingly unused.
     * 
     * @return the value of field 'Comment'.
     */
    public String getComment() {
        return this.m_comment;
    }

    /**
     * Returns the value of field 'common'. The field 'common' has
     * the following description: Common attributes that apply to
     * all categories in
     *  the group.
     * 
     * @return the value of field 'Common'.
     */
    public Common getCommon() {
        return this.m_common;
    }

    /**
     * Returns the value of field 'name'. The field 'name' has the
     * following description: The name of the category group. This
     * is seemingly
     *  unused.
     * 
     * @return the value of field 'Name'.
     */
    public String getName() {
        return this.m_name;
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

        if (m_name != null) {
           result = 37 * result + m_name.hashCode();
        }
        if (m_comment != null) {
           result = 37 * result + m_comment.hashCode();
        }
        if (m_common != null) {
           result = 37 * result + m_common.hashCode();
        }
        if (m_categories != null) {
           result = 37 * result + m_categories.hashCode();
        }
        
        return result;
    }

    /**
     * Sets the value of field 'categories'. The field 'categories'
     * has the following description: The categories belonging to
     * this category
     *  group.
     * 
     * @param categories the value of field 'categories'.
     */
    public void setCategories(final Categories categories) {
        this.m_categories = categories;
    }

    /**
     * Sets the value of field 'comment'. The field 'comment' has
     * the following description: A comment describing the category
     * group. This is
     *  seemingly unused.
     * 
     * @param comment the value of field 'comment'.
     */
    public void setComment(final String comment) {
        this.m_comment = comment;
    }

    /**
     * Sets the value of field 'common'. The field 'common' has the
     * following description: Common attributes that apply to all
     * categories in
     *  the group.
     * 
     * @param common the value of field 'common'.
     */
    public void setCommon(final Common common) {
        this.m_common = common;
    }

    /**
     * Sets the value of field 'name'. The field 'name' has the
     * following description: The name of the category group. This
     * is seemingly
     *  unused.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(final String name) {
        this.m_name = name;
    }
}
