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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

//---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class ChartConfiguration.
 * 
 * @version $Revision$ $Date$
 */
@XmlRootElement(name = "chart-configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChartConfiguration implements Serializable {
    private static final long serialVersionUID = 720276275928413525L;

    @XmlElement(name = "bar-chart")
    private List<BarChart> _barChartList = new ArrayList<>();

    /**
     * 
     * 
     * @param vBarChart
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addBarChart(
            final BarChart vBarChart)
    throws IndexOutOfBoundsException {
        _barChartList.add(vBarChart);
    }

    /**
     * 
     * 
     * @param index
     * @param vBarChart
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addBarChart(
            final int index,
            final BarChart vBarChart)
    throws IndexOutOfBoundsException {
        _barChartList.add(index, vBarChart);
    }

    /**
     * Method enumerateBarChart.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public Enumeration<BarChart> enumerateBarChart(
    ) {
        return Collections.enumeration(_barChartList);
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
        
        if (obj instanceof ChartConfiguration) {
        
            ChartConfiguration temp = (ChartConfiguration)obj;
            if (_barChartList != null) {
                if (temp._barChartList == null) return false;
                else if (!(_barChartList.equals(temp._barChartList))) 
                    return false;
            }
            else if (temp._barChartList != null)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Method getBarChart.
     * 
     * @param index
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * BarChart at the given index
     */
    public BarChart getBarChart(
            final int index)
    throws IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= _barChartList.size()) {
            throw new IndexOutOfBoundsException("getBarChart: Index value '" + index + "' not in range [0.." + (_barChartList.size() - 1) + "]");
        }
        
        return (BarChart) _barChartList.get(index);
    }

    /**
     * Method getBarChart.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public BarChart[] getBarChart(
    ) {
        BarChart[] array = new BarChart[0];
        return (BarChart[]) _barChartList.toArray(array);
    }

    /**
     * Method getBarChartCollection.Returns a reference to
     * '_barChartList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public List<BarChart> getBarChartCollection(
    ) {
        return _barChartList;
    }

    /**
     * Method getBarChartCount.
     * 
     * @return the size of this collection
     */
    public int getBarChartCount(
    ) {
        return _barChartList.size();
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

        if (_barChartList != null) {
           result = 37 * result + _barChartList.hashCode();
        }
        
        return result;
    }

    /**
     * Method iterateBarChart.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public Iterator<BarChart> iterateBarChart(
    ) {
        return _barChartList.iterator();
    }

    /**
     */
    public void removeAllBarChart(
    ) {
        _barChartList.clear();
    }

    /**
     * Method removeBarChart.
     * 
     * @param vBarChart
     * @return true if the object was removed from the collection.
     */
    public boolean removeBarChart(
            final BarChart vBarChart) {
        boolean removed = _barChartList.remove(vBarChart);
        return removed;
    }

    /**
     * Method removeBarChartAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public BarChart removeBarChartAt(
            final int index) {
        Object obj = _barChartList.remove(index);
        return (BarChart) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vBarChart
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setBarChart(
            final int index,
            final BarChart vBarChart)
    throws IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= _barChartList.size()) {
            throw new IndexOutOfBoundsException("setBarChart: Index value '" + index + "' not in range [0.." + (_barChartList.size() - 1) + "]");
        }
        
        _barChartList.set(index, vBarChart);
    }

    /**
     * 
     * 
     * @param vBarChartArray
     */
    public void setBarChart(
            final BarChart[] vBarChartArray) {
        //-- copy array
        _barChartList.clear();
        
        for (int i = 0; i < vBarChartArray.length; i++) {
                _barChartList.add(vBarChartArray[i]);
        }
    }

    /**
     * Sets the value of '_barChartList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vBarChartList the Vector to copy.
     */
    public void setBarChart(
            final List<BarChart> vBarChartList) {
        // copy vector
        _barChartList.clear();
        
        _barChartList.addAll(vBarChartList);
    }

}
