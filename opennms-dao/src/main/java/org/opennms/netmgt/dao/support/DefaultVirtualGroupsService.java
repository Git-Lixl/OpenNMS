//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2006 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//

package org.opennms.netmgt.dao.support;

import java.util.Set;
import org.opennms.netmgt.model.Group;
import org.opennms.netmgt.dao.GroupDao;
import org.opennms.netmgt.model.GroupItem;
import org.opennms.netmgt.dao.MonitoredServiceDao;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.netmgt.model.OnmsMonitoredService;
import org.opennms.netmgt.model.OnmsNode;

/**
 *
 * @author Daniele Piras for Pronetics S.p.A
 */
public class DefaultVirtualGroupsService implements VirtualGroupsService {
  private GroupDao groupDao;
  private NodeDao nodeDao;
  private MonitoredServiceDao monitoredServiceDao;

   /**
   * Method that retrieve if an interface is up or down.
   * @param intf - interface to check
   * @return true if the interface is down
   */
  protected boolean isInterfaceDown( OnmsIpInterface intf )
  {
    boolean res = false;
    Set<OnmsMonitoredService> services = intf.getMonitoredServices();
      for (OnmsMonitoredService svc : services) {
          if ( svc.isDown()) {
              res = true;
              break;
          }
    }
    return res;
  }

  /**
   * Method that retrieve if a node is up or down.
   * @param node - node to check
   * @return true if the node is down
   */
  protected boolean isNodeDown( OnmsNode node )
  {
    boolean res = false;
    Set<OnmsIpInterface> interfaces = node.getIpInterfaces();
    for (OnmsIpInterface ipIf : interfaces) {
        if ( isInterfaceDown( ipIf ) ) {
            res = true;
            break;
        }
    }
    return res;
  }


  /**
   * Checks if a virtualgroup is down.
   * @param g - group to check
   * @return - true, if the group is down.
   */
  public boolean isDown( Group g )
  {
    boolean res = false;
      for (GroupItem it : g.getItems()) {
        switch (it.getType()) {
          case NODE:
            OnmsNode node = nodeDao.load(it.getContentNodeId());
            if (isNodeDown( node )) {
              res = true;
            }
            break;
          case GROUP:
            Group gr = groupDao.get(it.getContentGroupId());
            if (isDown(gr)) {
              res = true;
            }
            break;
          case SERVICE:
            OnmsMonitoredService service = monitoredServiceDao.load(it.getContentServiceId());
            if (service.isDown()) {
              res = true;
            }
            break;
        }
        if (res) {
          break;
        }
      }
      return res;
  }

  public GroupDao getGroupDao() {
    return groupDao;
  }

  public void setGroupDao(GroupDao groupDao) {
    this.groupDao = groupDao;
  }

  public MonitoredServiceDao getMonitoredServiceDao() {
    return monitoredServiceDao;
  }

  public void setMonitoredServiceDao(MonitoredServiceDao monitoredServiceDao) {
    this.monitoredServiceDao = monitoredServiceDao;
  }

  public NodeDao getNodeDao() {
    return nodeDao;
  }

  public void setNodeDao(NodeDao nodeDao) {
    this.nodeDao = nodeDao;
  }

  

}
