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

package org.opennms.netmgt.poller.monitors.odl;

import java.util.Map;

import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opennms.core.spring.BeanUtils;
import org.opennms.integrations.odl.NamingUtils;
import org.opennms.integrations.odl.OpendaylightRestconfClient;
import org.opennms.netmgt.dao.api.NodeDao;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.poller.Distributable;
import org.opennms.netmgt.poller.DistributionContext;
import org.opennms.netmgt.poller.MonitoredService;
import org.opennms.netmgt.poller.PollStatus;
import org.opennms.netmgt.poller.monitors.AbstractServiceMonitor;

@Distributable(DistributionContext.DAEMON)
public class OpendaylightMonitor extends AbstractServiceMonitor {

    private NodeDao m_nodeDao;

    @Override
    public PollStatus poll(MonitoredService svc, Map<String, Object> parameters) {
        if (m_nodeDao == null) {
            m_nodeDao = BeanUtils.getBean("daoContext", "nodeDao", NodeDao.class);
        }
        OnmsNode node = m_nodeDao.get(svc.getNodeId());
        String foreignId = node.getForeignId();
        String odlTopologyId = node.getAssetRecord().getBuilding();
        String odlNodeId = NamingUtils.getNodeIdFromForeignId(foreignId);

        OpendaylightRestconfClient odlClient = new OpendaylightRestconfClient("127.0.0.1");
        try {
            Node odlNode = odlClient.getNodeFromOperationalTopology(odlTopologyId, odlNodeId);
            if (odlNode == null) {
                return PollStatus.unavailable("Node was not found.");
            }
            return PollStatus.available();
        } catch (Exception e) {
            return PollStatus.unresponsive(e.getMessage());
        }
    }
}
