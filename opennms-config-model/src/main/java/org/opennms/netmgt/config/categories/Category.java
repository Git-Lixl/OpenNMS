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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A category.
 *
 * <p>This class was previously generated using Castor.</p>
 */
@XmlRootElement(name = "category")
@XmlAccessorType(XmlAccessType.FIELD)
public class Category implements java.io.Serializable {
    private static final long serialVersionUID = -1934433252108257137L;

    /**
     * The category label. NOTE: category labels will need
     *  to be unique across category groups.
     */
    @XmlElement(name = "label")
    private String m_label;

    /**
     * A comment describing the category.
     */
    @XmlElement(name = "comment")
    private String m_comment;

    /**
     * The normal threshold value for the category in
     *  percent. The UI displays the category in green if the
     * overall
     *  availability for the category is equal to or greater than
     * this
     *  value.
     */
    @XmlElement(name = "normal")
    private Double m_normal;

    /**
     * The warning threshold value for the category in
     *  percent. The UI displays the category in yellow if the
     * overall
     *  availability for the category is equal to or greater than
     * this
     *  value but less than the normal threashold. If availability
     * is less
     *  than this value, category is displayed in red.
     */
    @XmlElement(name = "warning")
    private Double m_warning;

    /**
     * A service relevant to this category. For a
     *  nodeid/ip/service tuple to be added to a category, it will
     * need to
     *  pass the rule(categorygroup rule & category rule) and the
     *  service will need to be in the category service list. If
     * there are
     *  no services defined, all tuples that pass the rule are
     * added to
     *  the category.
     */
    @XmlElement(name = "service")
    private List<String> m_serviceList = new ArrayList<>();

    /**
     * The category rule.
     */
    @XmlElement(name = "rule")
    private String m_rule;

    /**
     * 
     * 
     * @param vService
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addService(final String vService) {
        this.m_serviceList.add(vService);
    }

    /**
     * 
     * 
     * @param index
     * @param vService
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addService(final int index, final String vService) {
        this.m_serviceList.add(index, vService);
    }

    /**
     * Overrides the java.lang.Object.equals method.
     * 
     * @param obj
     * @return true if the objects are equal.
     */
    @Override()
    public boolean equals(
            final java.lang.Object obj) {
        if ( this == obj )
            return true;
        
        if (obj instanceof Category) {
        
            Category temp = (Category)obj;
            if (this.m_label != null) {
                if (temp.m_label == null) return false;
                else if (!(this.m_label.equals(temp.m_label))) 
                    return false;
            }
            else if (temp.m_label != null)
                return false;
            if (this.m_comment != null) {
                if (temp.m_comment == null) return false;
                else if (!(this.m_comment.equals(temp.m_comment))) 
                    return false;
            }
            else if (temp.m_comment != null)
                return false;
            if (this.m_normal != null) {
                if (temp.m_normal == null) return false;
                else if (!(this.m_normal.equals(temp.m_normal)))
                    return false;
            }
            if (this.m_warning != null) {
                if (temp.m_warning == null) return false;
                else if (!(this.m_warning.equals(temp.m_warning)))
                    return false;
            }
            if (this.m_serviceList != null) {
                if (temp.m_serviceList == null) return false;
                else if (!(this.m_serviceList.equals(temp.m_serviceList))) 
                    return false;
            }
            else if (temp.m_serviceList != null)
                return false;
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
     * Returns the value of field 'comment'. The field 'comment'
     * has the following description: A comment describing the
     * category.
     * 
     * @return the value of field 'Comment'.
     */
    public String getComment() {
        return this.m_comment;
    }

    /**
     * Returns the value of field 'label'. The field 'label' has
     * the following description: The category label. NOTE:
     * category labels will need
     *  to be unique across category groups.
     * 
     * @return the value of field 'Label'.
     */
    public String getLabel() {
        return this.m_label;
    }

    /**
     * Returns the value of field 'normal'. The field 'normal' has
     * the following description: The normal threshold value for
     * the category in
     *  percent. The UI displays the category in green if the
     * overall
     *  availability for the category is equal to or greater than
     * this
     *  value.
     * 
     * @return the value of field 'Normal'.
     */
    public double getNormal() {
        return this.m_normal;
    }

    /**
     * Returns the value of field 'rule'. The field 'rule' has the
     * following description: The category rule.
     * 
     * @return the value of field 'Rule'.
     */
    public String getRule() {
        return this.m_rule;
    }

    /**
     * Method getService.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the String at the given index
     */
    public String getService(final int index) {
        // check bounds for index
        if (index < 0 || index >= this.m_serviceList.size()) {
            throw new IndexOutOfBoundsException("getService: Index value '" + index + "' not in range [0.." + (this.m_serviceList.size() - 1) + "]");
        }
        
        return (String) m_serviceList.get(index);
    }

    /**
     * Method getService.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public String[] getService() {
        return this.m_serviceList.toArray(new String[0]);
    }

    /**
     * Method getServiceCollection.Returns a reference to
     * 'm_serviceList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public List<String> getServiceCollection() {
        return this.m_serviceList;
    }

    /**
     * Method getServiceCount.
     * 
     * @return the size of this collection
     */
    public int getServiceCount() {
        return this.m_serviceList.size();
    }

    /**
     * Returns the value of field 'warning'. The field 'warning'
     * has the following description: The warning threshold value
     * for the category in
     *  percent. The UI displays the category in yellow if the
     * overall
     *  availability for the category is equal to or greater than
     * this
     *  value but less than the normal threashold. If availability
     * is less
     *  than this value, category is displayed in red.
     * 
     * @return the value of field 'Warning'.
     */
    public double getWarning() {
        return this.m_warning;
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
        
        long tmp;
        if (m_label != null) {
           result = 37 * result + m_label.hashCode();
        }
        if (m_comment != null) {
           result = 37 * result + m_comment.hashCode();
        }
        tmp = java.lang.Double.doubleToLongBits(m_normal);
        result = 37 * result + (int)(tmp^(tmp>>>32));
        tmp = java.lang.Double.doubleToLongBits(m_warning);
        result = 37 * result + (int)(tmp^(tmp>>>32));
        if (m_serviceList != null) {
           result = 37 * result + m_serviceList.hashCode();
        }
        if (m_rule != null) {
           result = 37 * result + m_rule.hashCode();
        }
        
        return result;
    }

    /**
     * Method iterateService.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<String> iterateService() {
        return this.m_serviceList.iterator();
    }

    /**
     */
    public void removeAllService() {
        this.m_serviceList.clear();
    }

    /**
     * Method removeService.
     * 
     * @param vService
     * @return true if the object was removed from the collection.
     */
    public boolean removeService(final String vService) {
        return m_serviceList.remove(vService);
    }

    /**
     * Method removeServiceAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public String removeServiceAt(final int index) {
        return this.m_serviceList.remove(index);
    }

    /**
     * Sets the value of field 'comment'. The field 'comment' has
     * the following description: A comment describing the
     * category.
     * 
     * @param comment the value of field 'comment'.
     */
    public void setComment(final String comment) {
        this.m_comment = comment;
    }

    /**
     * Sets the value of field 'label'. The field 'label' has the
     * following description: The category label. NOTE: category
     * labels will need
     *  to be unique across category groups.
     * 
     * @param label the value of field 'label'.
     */
    public void setLabel(final String label) {
        this.m_label = label;
    }

    /**
     * Sets the value of field 'normal'. The field 'normal' has the
     * following description: The normal threshold value for the
     * category in
     *  percent. The UI displays the category in green if the
     * overall
     *  availability for the category is equal to or greater than
     * this
     *  value.
     * 
     * @param normal the value of field 'normal'.
     */
    public void setNormal(final double normal) {
        this.m_normal = normal;
    }

    /**
     * Sets the value of field 'rule'. The field 'rule' has the
     * following description: The category rule.
     * 
     * @param rule the value of field 'rule'.
     */
    public void setRule(final String rule) {
        this.m_rule = rule;
    }

    /**
     * 
     * 
     * @param index
     * @param vService
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setService(final int index, final String vService) {
        // check bounds for index
        if (index < 0 || index >= this.m_serviceList.size()) {
            throw new IndexOutOfBoundsException("setService: Index value '" + index + "' not in range [0.." + (this.m_serviceList.size() - 1) + "]");
        }
        
        this.m_serviceList.set(index, vService);
    }

    /**
     * 
     * 
     * @param vServiceArray
     */
    public void setService(final String[] vServiceArray) {
        //-- copy array
        m_serviceList.clear();
        
        for (int i = 0; i < vServiceArray.length; i++) {
                this.m_serviceList.add(vServiceArray[i]);
        }
    }

    /**
     * Sets the value of 'm_serviceList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vServiceList the Vector to copy.
     */
    public void setService(final List<String> vServiceList) {
        // copy vector
        this.m_serviceList.clear();
        this.m_serviceList.addAll(vServiceList);
    }

    /**
     * Sets the value of field 'warning'. The field 'warning' has
     * the following description: The warning threshold value for
     * the category in
     *  percent. The UI displays the category in yellow if the
     * overall
     *  availability for the category is equal to or greater than
     * this
     *  value but less than the normal threashold. If availability
     * is less
     *  than this value, category is displayed in red.
     * 
     * @param warning the value of field 'warning'.
     */
    public void setWarning(final double warning) {
        this.m_warning = warning;
    }
}
