/*******************************************************************************
 * This file is part of OpenNMS(R).
 * <p>
 * Copyright (C) 2015 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2015 The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 * http://www.gnu.org/licenses/
 * <p>
 * For more information contact:
 * OpenNMS(R) Licensing <license@opennms.org>
 * http://www.opennms.org/
 * http://www.opennms.com/
 *******************************************************************************/

package org.opennms.web.rest.api.model;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="info")
@XmlAccessorType(XmlAccessType.NONE)
public class Info {

    @XmlElement(name="displayVersion")
    private String displayVersion;

    @XmlElement(name="version")
    private String version;

    @XmlElement(name="packageName")
    private String packageName;

    @XmlElement(name="packageDescription")
    private String packageDescription;

    public void setDisplayVersion(String displayVersion) {
        this.displayVersion = displayVersion;
    }

    public void setPackageDescription(String packageDescription) {
        this.packageDescription = packageDescription;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDisplayVersion() {
        return displayVersion;
    }

    public String getPackageDescription() {
        return packageDescription;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayVersion, version, packageDescription, packageName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() == obj.getClass()) {
            Info other = (Info) obj;
            boolean equals = Objects.equals(displayVersion, other.displayVersion)
                    && Objects.equals(version, other.version)
                    && Objects.equals(packageName, other.packageName)
                    && Objects.equals(packageDescription, other.packageDescription);
            return equals;
        }
        return false;
    }
}
