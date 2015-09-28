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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class JdbcDataSet.
 * 
 * @version $Revision$ $Date$
 */
@XmlRootElement(name = "jdbc-data-set")
@XmlAccessorType(XmlAccessType.FIELD)
public class JdbcDataSet implements Serializable {
    private static final long serialVersionUID = 543387091015023447L;

    /**
     * Field _dbName.
     */
    @XmlAttribute(name="db-name")
    private String _dbName;

    /**
     * Field _sql.
     */
    @XmlAttribute(name="sql")
    private String _sql;

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
        
        if (obj instanceof JdbcDataSet) {
        
            JdbcDataSet temp = (JdbcDataSet)obj;
            if (_dbName != null) {
                if (temp._dbName == null) return false;
                else if (!(_dbName.equals(temp._dbName))) 
                    return false;
            }
            else if (temp._dbName != null)
                return false;
            if (_sql != null) {
                if (temp._sql == null) return false;
                else if (!(_sql.equals(temp._sql))) 
                    return false;
            }
            else if (temp._sql != null)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of field 'dbName'.
     * 
     * @return the value of field 'DbName'.
     */
    public String getDbName() {
        return _dbName;
    }

    /**
     * Returns the value of field 'sql'.
     * 
     * @return the value of field 'Sql'.
     */
    public String getSql() {
        return _sql;
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

        if (_dbName != null) {
           result = 37 * result + _dbName.hashCode();
        }
        if (_sql != null) {
           result = 37 * result + _sql.hashCode();
        }
        
        return result;
    }

    /**
     * Sets the value of field 'dbName'.
     * 
     * @param dbName the value of field 'dbName'.
     */
    public void setDbName(final String dbName) {
        _dbName = dbName;
    }

    /**
     * Sets the value of field 'sql'.
     * 
     * @param sql the value of field 'sql'.
     */
    public void setSql(final String sql) {
        _sql = sql;
    }

}
