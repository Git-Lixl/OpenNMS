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
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class Report.
 * 
 * @version $Revision$ $Date$
 */
@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
public class Report implements Serializable {
    private static final long serialVersionUID = 5315489016109822356L;

    /**
     * the name of this report as defined in engine
     *  configuration
     */
    @XmlAttribute(name="id")
    private String _id;

    /**
     * the name of this report as displayed in the webui
     *  
     */
    @XmlAttribute(name="display-name")
    private String _displayName;

    /**
     * the name of the engine to use to process and
     *  render this report
     */
    @XmlAttribute(name="report-service")
    private String _reportService;

    /**
     * report description
     */
    @XmlAttribute(name="description")
    private String _description;

    /**
     * determines if the report may be executed and immediately
     *  displayed in the browser. If not set OpenNMS assumes that
     * the report
     *  must be executed in batch mode.
     */
    @XmlAttribute(name="online")
    private Boolean _online;

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
        
        if (obj instanceof Report) {
        
            Report temp = (Report)obj;
            if (_id != null) {
                if (temp._id == null) return false;
                else if (!(_id.equals(temp._id))) 
                    return false;
            }
            else if (temp._id != null)
                return false;
            if (_displayName != null) {
                if (temp._displayName == null) return false;
                else if (!(_displayName.equals(temp._displayName))) 
                    return false;
            }
            else if (temp._displayName != null)
                return false;
            if (_reportService != null) {
                if (temp._reportService == null) return false;
                else if (!(_reportService.equals(temp._reportService))) 
                    return false;
            }
            else if (temp._reportService != null)
                return false;
            if (_description != null) {
                if (temp._description == null) return false;
                else if (!(_description.equals(temp._description))) 
                    return false;
            }
            else if (temp._description != null)
                return false;
            if (!Objects.equals(_online, temp._online))
                return false;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of field 'description'. The field
     * 'description' has the following description: report
     * description
     * 
     * @return the value of field 'Description'.
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Returns the value of field 'displayName'. The field
     * 'displayName' has the following description: the name of
     * this report as displayed in the webui
     *  
     * 
     * @return the value of field 'DisplayName'.
     */
    public String getDisplayName() {
        return _displayName;
    }

    /**
     * Returns the value of field 'id'. The field 'id' has the
     * following description: the name of this report as defined in
     * engine
     *  configuration
     * 
     * @return the value of field 'Id'.
     */
    public String getId() {
        return _id;
    }

    /**
     * Returns the value of field 'online'. The field 'online' has
     * the following description: determines if the report may be
     * executed and immediately
     *  displayed in the browser. If not set OpenNMS assumes that
     * the report
     *  must be executed in batch mode.
     * 
     * @return the value of field 'Online'.
     */
    public boolean getOnline() {
        return _online;
    }

    /**
     * Returns the value of field 'reportService'. The field
     * 'reportService' has the following description: the name of
     * the engine to use to process and
     *  render this report
     * 
     * @return the value of field 'ReportService'.
     */
    public String getReportService() {
        return _reportService;
    }

    /**
     * Method hasOnline.
     * 
     * @return true if at least one Online has been added
     */
    public boolean hasOnline() {
        return _online != null;
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

        if (_id != null) {
           result = 37 * result + _id.hashCode();
        }
        if (_displayName != null) {
           result = 37 * result + _displayName.hashCode();
        }
        if (_reportService != null) {
           result = 37 * result + _reportService.hashCode();
        }
        if (_description != null) {
           result = 37 * result + _description.hashCode();
        }
        if (_online != null) {
            result = 37 * result + (_online?0:1);
        }

        return result;
    }

    /**
     * Returns the value of field 'online'. The field 'online' has
     * the following description: determines if the report may be
     * executed and immediately
     *  displayed in the browser. If not set OpenNMS assumes that
     * the report
     *  must be executed in batch mode.
     * 
     * @return the value of field 'Online'.
     */
    public boolean isOnline() {
        return _online;
    }

    /**
     * Sets the value of field 'description'. The field
     * 'description' has the following description: report
     * description
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(final String description) {
        _description = description;
    }

    /**
     * Sets the value of field 'displayName'. The field
     * 'displayName' has the following description: the name of
     * this report as displayed in the webui
     *  
     * 
     * @param displayName the value of field 'displayName'.
     */
    public void setDisplayName(final String displayName) {
        _displayName = displayName;
    }

    /**
     * Sets the value of field 'id'. The field 'id' has the
     * following description: the name of this report as defined in
     * engine
     *  configuration
     * 
     * @param id the value of field 'id'.
     */
    public void setId(final String id) {
        _id = id;
    }

    /**
     * Sets the value of field 'online'. The field 'online' has the
     * following description: determines if the report may be
     * executed and immediately
     *  displayed in the browser. If not set OpenNMS assumes that
     * the report
     *  must be executed in batch mode.
     * 
     * @param online the value of field 'online'.
     */
    public void setOnline(final boolean online) {
        _online = online;
    }

    /**
     * Sets the value of field 'reportService'. The field
     * 'reportService' has the following description: the name of
     * the engine to use to process and
     *  render this report
     * 
     * @param reportService the value of field 'reportService'.
     */
    public void setReportService(final String reportService) {
        _reportService = reportService;
    }

}
