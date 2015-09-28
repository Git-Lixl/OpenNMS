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

package org.opennms.netmgt.config.databaseReports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "database-reports")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseReports implements Serializable {
    private static final long serialVersionUID = 8308827694749051779L;

    /**
     * A report definition
     */
    @XmlElement(name="report")
    private List<Report> _reportList = new ArrayList<>();

    /**
     * 
     * 
     * @param vReport
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addReport(final Report vReport) throws IndexOutOfBoundsException {
        _reportList.add(vReport);
    }

    /**
     * 
     * 
     * @param index
     * @param vReport
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addReport(final int index, final Report vReport) throws IndexOutOfBoundsException {
        _reportList.add(index, vReport);
    }

    /**
     * Method enumerateReport.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public Enumeration<Report> enumerateReport() {
        return Collections.enumeration(_reportList);
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
        
        if (obj instanceof DatabaseReports) {

            DatabaseReports temp = (DatabaseReports)obj;
            if (_reportList != null) {
                if (temp._reportList == null) return false;
                else if (!(_reportList.equals(temp._reportList))) 
                    return false;
            }
            else if (temp._reportList != null)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Method getReport.
     * 
     * @param index
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * Report at the
     * given index
     */
    public Report getReport(final int index) throws IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= _reportList.size()) {
            throw new IndexOutOfBoundsException("getReport: Index value '" + index + "' not in range [0.." + (_reportList.size() - 1) + "]");
        }
        
        return (Report) _reportList.get(index);
    }

    /**
     * Method getReport.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public Report[] getReport() {
        Report[] array = new Report[0];
        return (Report[]) _reportList.toArray(array);
    }

    /**
     * Method getReportCollection.Returns a reference to
     * '_reportList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public List<Report> getReportCollection() {
        return _reportList;
    }

    /**
     * Method getReportCount.
     * 
     * @return the size of this collection
     */
    public int getReportCount() {
        return _reportList.size();
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

        if (_reportList != null) {
           result = 37 * result + _reportList.hashCode();
        }

        return result;
    }

    /**
     * Method iterateReport.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public Iterator<Report> iterateReport() {
        return _reportList.iterator();
    }

    /**
     */
    public void removeAllReport() {
        _reportList.clear();
    }

    /**
     * Method removeReport.
     * 
     * @param vReport
     * @return true if the object was removed from the collection.
     */
    public boolean removeReport(final Report vReport) {
        boolean removed = _reportList.remove(vReport);
        return removed;
    }

    /**
     * Method removeReportAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public Report removeReportAt(final int index) {
        Object obj = _reportList.remove(index);
        return (Report) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vReport
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setReport(final int index, final Report vReport) throws IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= _reportList.size()) {
            throw new IndexOutOfBoundsException("setReport: Index value '" + index + "' not in range [0.." + (_reportList.size() - 1) + "]");
        }

        _reportList.set(index, vReport);
    }

    /**
     * 
     * 
     * @param vReportArray
     */
    public void setReport(final Report[] vReportArray) {
        //-- copy array
        _reportList.clear();
        
        for (int i = 0; i < vReportArray.length; i++) {
                _reportList.add(vReportArray[i]);
        }
    }

    /**
     * Sets the value of '_reportList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vReportList the Vector to copy.
     */
    public void setReport(final List<Report> vReportList) {
        // copy vector
        _reportList.clear();
        
        _reportList.addAll(vReportList);
    }

}
