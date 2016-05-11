package org.opennms.netmgt.provision.service;

/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
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

import java.net.InetAddress;

import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.provision.service.operations.ScanResource;
import org.opennms.netmgt.provision.service.snmp.SystemGroup;

public class ScanResourceSystemGroup extends SystemGroup {

    public ScanResourceSystemGroup(InetAddress address) {
        super(address);
    }

    /**
     * <p>updateSnmpDataForResource</p>
     *
     * @param sr a {@link org.opennms.netmgt.provision.service.operations.ScanResource} object.
     */
    public void updateSnmpDataForResource(ScanResource sr) {
        if (!failed()) {
            sr.setAttribute("sysName", getSysName());
            sr.setAttribute("sysContact", getSysContact());
            sr.setAttribute("sysDescription", getSysDescr());
            sr.setAttribute("sysLocation", getSysLocation());
            sr.setAttribute("sysObjectId", getSysObjectID());
        }
    }

    /**
     * <p>updateSnmpDataForNode</p>
     *
     * @param node a {@link org.opennms.netmgt.model.OnmsNode} object.
     */
    public void updateSnmpDataForNode(OnmsNode node) {
        ScanResource sr = new ScanResource("SNMP");
        sr.setNode(node);
        updateSnmpDataForResource(sr);
    }
}
