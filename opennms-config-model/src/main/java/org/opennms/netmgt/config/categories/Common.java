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
 * Common attributes that apply to all categories in a
 *  group.
 *
 * <p>This class was previously generated using Castor.</p>
 */
@XmlRootElement(name = "common")
@XmlAccessorType(XmlAccessType.FIELD)
public class Common implements java.io.Serializable {
    private static final long serialVersionUID = -2931360204092897993L;

    /**
     * A common rule that will be applied to all
     *  categories in this group in addition to the category's
     *  rule.
     */
    @XmlElement(name = "rule")
    private String m_rule;

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
        
        if (obj instanceof Common) {
        
            Common temp = (Common)obj;
            if (this.m_rule != null) {
                if (temp.m_rule == null) return false;
                else if (!(this.m_rule.equals(temp.m_rule))) 
                    return false;
            }
            else if (temp.m_rule != null)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of field 'rule'. The field 'rule' has the
     * following description: A common rule that will be applied to
     * all
     *  categories in this group in addition to the category's
     *  rule.
     * 
     * @return the value of field 'Rule'.
     */
    public String getRule() {
        return this.m_rule;
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

        if (m_rule != null) {
           result = 37 * result + m_rule.hashCode();
        }
        
        return result;
    }

    /**
     * Sets the value of field 'rule'. The field 'rule' has the
     * following description: A common rule that will be applied to
     * all
     *  categories in this group in addition to the category's
     *  rule.
     * 
     * @param rule the value of field 'rule'.
     */
    public void setRule(final String rule) {
        this.m_rule = rule;
    }
}
