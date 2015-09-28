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
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class SeriesDef.
 * 
 * @version $Revision$ $Date$
 */
@XmlRootElement(name = "series-def")
@XmlAccessorType(XmlAccessType.FIELD)
public class SeriesDef implements Serializable {
    private static final long serialVersionUID = 7045547409014895632L;

    private static final boolean DEFAULT_USE_LABELS = true;

    /**
     * Field _number.
     */
    @XmlAttribute(name="number")
    private Integer _number;

    /**
     * Field _seriesName.
     */
    @XmlAttribute(name="series-name")
    private String _seriesName;

    /**
     * Field _useLabels.
     */
    @XmlAttribute(name="use-labels")
    private Boolean _useLabels;

    /**
     * Field _jdbcDataSet.
     */
    @XmlElement(name="jdbc-data-set")
    private JdbcDataSet _jdbcDataSet;

    /**
     * Field _rgb.
     */
    @XmlElement(name="rgb")
    private Rgb _rgb;

    /**
     */
    public void deleteNumber() {
        _number = null;
    }

    /**
     */
    public void deleteUseLabels() {
        _useLabels = null;
    }

    /**
     * Overrides the Object.equals method.
     * 
     * @param obj
     * @return true if the objects are equal.
     */
    @Override()
    public boolean equals(final Object obj) {
        if ( this == obj )
            return true;
        
        if (obj instanceof SeriesDef) {
        
            SeriesDef temp = (SeriesDef)obj;
            if (_number != temp._number)
                return false;
            if (_seriesName != null) {
                if (temp._seriesName == null) return false;
                else if (!(_seriesName.equals(temp._seriesName))) 
                    return false;
            }
            else if (temp._seriesName != null)
                return false;
            if (!Objects.equals(_useLabels, _useLabels))
                return false;
            if (_jdbcDataSet != null) {
                if (temp._jdbcDataSet == null) return false;
                else if (!(_jdbcDataSet.equals(temp._jdbcDataSet))) 
                    return false;
            }
            else if (temp._jdbcDataSet != null)
                return false;
            if (_rgb != null) {
                if (temp._rgb == null) return false;
                else if (!(_rgb.equals(temp._rgb))) 
                    return false;
            }
            else if (temp._rgb != null)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of field 'jdbcDataSet'.
     * 
     * @return the value of field 'JdbcDataSet'.
     */
    public JdbcDataSet getJdbcDataSet() {
        return _jdbcDataSet;
    }

    /**
     * Returns the value of field 'number'.
     * 
     * @return the value of field 'Number'.
     */
    public int getNumber() {
        return _number;
    }

    /**
     * Returns the value of field 'rgb'.
     * 
     * @return the value of field 'Rgb'.
     */
    public Rgb getRgb() {
        return _rgb;
    }

    /**
     * Returns the value of field 'seriesName'.
     * 
     * @return the value of field 'SeriesName'.
     */
    public String getSeriesName() {
        return _seriesName;
    }

    /**
     * Returns the value of field 'useLabels'.
     * 
     * @return the value of field 'UseLabels'.
     */
    public boolean getUseLabels() {
        return _useLabels != null ? _useLabels : DEFAULT_USE_LABELS;
    }

    /**
     * Method hasNumber.
     * 
     * @return true if at least one Number has been added
     */
    public boolean hasNumber() {
        return _number != null;
    }

    /**
     * Method hasUseLabels.
     * 
     * @return true if at least one UseLabels has been added
     */
    public boolean hasUseLabels() {
        return _useLabels != null;
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

        result = 37 * result + _number;
        if (_seriesName != null) {
           result = 37 * result + _seriesName.hashCode();
        }
        result = 37 * result + (_useLabels?0:1);
        if (_jdbcDataSet != null) {
           result = 37 * result + _jdbcDataSet.hashCode();
        }
        if (_rgb != null) {
           result = 37 * result + _rgb.hashCode();
        }
        
        return result;
    }

    /**
     * Returns the value of field 'useLabels'.
     * 
     * @return the value of field 'UseLabels'.
     */
    public boolean isUseLabels() {
        return _useLabels;
    }

    /**
     * Sets the value of field 'jdbcDataSet'.
     * 
     * @param jdbcDataSet the value of field 'jdbcDataSet'.
     */
    public void setJdbcDataSet(final JdbcDataSet jdbcDataSet) {
        _jdbcDataSet = jdbcDataSet;
    }

    /**
     * Sets the value of field 'number'.
     * 
     * @param number the value of field 'number'.
     */
    public void setNumber(final int number) {
        _number = number;
    }

    /**
     * Sets the value of field 'rgb'.
     * 
     * @param rgb the value of field 'rgb'.
     */
    public void setRgb(final Rgb rgb) {
        _rgb = rgb;
    }

    /**
     * Sets the value of field 'seriesName'.
     * 
     * @param seriesName the value of field 'seriesName'.
     */
    public void setSeriesName(final String seriesName) {
        _seriesName = seriesName;
    }

    /**
     * Sets the value of field 'useLabels'.
     * 
     * @param useLabels the value of field 'useLabels'.
     */
    public void setUseLabels(final boolean useLabels) {
        _useLabels = useLabels;
    }

}
