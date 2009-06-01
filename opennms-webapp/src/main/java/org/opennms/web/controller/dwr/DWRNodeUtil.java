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
package org.opennms.web.controller.dwr;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.opennms.netmgt.dao.AlarmDao;
import org.opennms.netmgt.dao.GroupDao;
import org.opennms.netmgt.dao.IpInterfaceDao;
import org.opennms.netmgt.dao.MonitoredServiceDao;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.model.Group;
import org.opennms.netmgt.model.GroupItem;
import org.opennms.netmgt.model.OnmsAlarm;
import org.opennms.netmgt.model.OnmsCriteria;
import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.netmgt.model.OnmsMonitoredService;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsSeverity;

/**
 * Utility class to retrieve information using DWR.
 *
 * @author Daniele Piras for Pronetics S.p.A
 */
public class DWRNodeUtil {

  public NodeDao nodeDao;
  public MonitoredServiceDao monitoredServiceDao;
  public GroupDao groupDao;
  public AlarmDao alarmDao;
  public IpInterfaceDao ipInterfaceDao;

  public GroupDao getGroupDao() {
    return groupDao;
  }

  public void setGroupDao(GroupDao groupDao) {
    this.groupDao = groupDao;
  }

  public IpInterfaceDao getIpInterfaceDao() {
    return ipInterfaceDao;
  }

  public AlarmDao getAlarmDao() {
    return alarmDao;
  }

  public void setAlarmDao(AlarmDao alarmDao) {
    this.alarmDao = alarmDao;
  }

  public void setIpInterfaceDao(IpInterfaceDao ipInterfaceDao) {
    this.ipInterfaceDao = ipInterfaceDao;
  }

  public NodeDao getNodeDao() {
    return nodeDao;
  }

  public void setNodeDao(NodeDao nodeDao) {
    this.nodeDao = nodeDao;
  }

  public MonitoredServiceDao getMonitoredServiceDao() {
    return monitoredServiceDao;
  }

  public void setMonitoredServiceDao(MonitoredServiceDao monitoredServiceDao) {
    this.monitoredServiceDao = monitoredServiceDao;
  }

  /**
   * Method to retrieve the interfaces of a given node with their Ids.
   *
   * @param nodeId - ID of node
   * @return a map with all the interfaces
   */
  public Map<String, String> getInterfaceList(String nodeId) {
    OnmsNode node = nodeDao.load(Integer.parseInt(nodeId));
    Set<OnmsIpInterface> its = node.getIpInterfaces();
    Map res = new TreeMap<String, String>();

    for (OnmsIpInterface it : its) {
      res.put(it.getId(), it.getIpAddress());
    }
    return res;
  }

  /**
   * Return the severity string of a given severity integer level.
   *
   * @param severity - severity integer level
   * @return severity string
   */
  private String getSeverityString(OnmsSeverity severity) {
     return severity.getLabel();    
  }

  /**
   * Retrieve all the ids for all nodes contained into a specified group.
   *
   * @param g - group to check
   * @return a set with all node identifiers
   */
  private Set<Integer> getNodes(Group g) {
    Set<Integer> res = new TreeSet<Integer>();
    for (GroupItem item : g.getItems()) {
      switch (item.getType()) {
        case NODE:
          res.add(item.getContentNodeId());
          break;
        case GROUP:
          res.addAll(getNodes(groupDao.get(item.getContentGroupId())));
          break;
      }
    }
    return res;
  }

  /**
   * Retrieve all the ids for all services contained into a specified group.
   *
   * @param g - group to check
   * @return a set with all services identifiers
   */
  private Set<Integer> getServices(Group g) {
    Set<Integer> res = new TreeSet<Integer>();
    for (GroupItem item : g.getItems()) {
      switch (item.getType()) {
        case SERVICE:
          res.add(item.getContentServiceId());
          break;
        case GROUP:
          res.addAll(getServices(groupDao.get(item.getContentGroupId())));
          break;
      }
    }
    return res;
  }

  /**
   * Return a alarms list for a given node
   *
   * @param nodeId - Identifier for the node
   * @return the alarms list
   */
  public Set<SimpleAlarm> getAlarmsForNode(int nodeId) {
    Set<SimpleAlarm> res = new TreeSet<SimpleAlarm>();
    OnmsCriteria criteria = new OnmsCriteria(OnmsAlarm.class, "alarm");
    OnmsCriteria nodeCriteria = criteria.createCriteria("node");
    nodeCriteria.add(Restrictions.sqlRestriction("{alias}.nodeId =  " + nodeId + " "));
    nodeCriteria.add(Restrictions.ne("type", "D"));
    criteria.addOrder(Order.desc("alarm.severityId"));
    List<OnmsAlarm> alarms = alarmDao.findMatching(criteria);
    for (OnmsAlarm alarm : alarms) {
      res.add(new SimpleAlarm(alarm.getId(),getSeverityString(alarm.getSeverity()), alarm.getNode().getLabel(), alarm.getLogMsg(), alarm.getDescription(), alarm.getCounter(), new Date(alarm.getFirstEventTime().getTime()), new Date(alarm.getLastEventTime().getTime())));
    }
    return res;
  }

  /**
   * Return a alarms list for a given service
   *
   * @param nodeId - Identifier for the node
   * @return the alarms list
   */
  public Set<SimpleAlarm> getAlarmsForService(int serviceId) {
    Set<SimpleAlarm> res = new TreeSet<SimpleAlarm>();
    StringBuffer services = new StringBuffer();
    OnmsMonitoredService srvc = monitoredServiceDao.load(serviceId);
    services.append("({alias}.nodeId = ");
    services.append(srvc.getNodeId());
    services.append(" and {alias}.ipAddr = ");
    services.append("'" + srvc.getIpAddress() + "'");
    services.append(" and {alias}.serviceId = ");
    services.append(srvc.getServiceId());
    services.append(")");
    OnmsCriteria criteria = new OnmsCriteria(OnmsAlarm.class, "alarm");
    criteria.add(Restrictions.sqlRestriction(services.toString()));
    OnmsCriteria nodeCriteria = criteria.createCriteria("node");
    nodeCriteria.add(Restrictions.ne("type", "D"));
    //nodeCriteria.add(Restrictions.sqlRestriction("{alias}.nodeId in ( " + services.toString() + " )"));
    //nodeCriteria.add(Restrictions.ne("type", "D"));
    criteria.addOrder(Order.desc("alarm.severityId"));
    List<OnmsAlarm> alarms = alarmDao.findMatching(criteria);
    for (OnmsAlarm alarm : alarms) {
      res.add(new SimpleAlarm(alarm.getId(),getSeverityString(alarm.getSeverity()), alarm.getNode().getLabel(), alarm.getLogMsg(), alarm.getDescription(), alarm.getCounter(), new Date(alarm.getFirstEventTime().getTime()), new Date(alarm.getLastEventTime().getTime())));
    }
    return res;
  }
 
  /**
   * Return a alarms list for a given groupId
   *
   * @param groupId - Identifier for a group
   * @return the alarms list
   */
  public Set<SimpleAlarm> getAlarmsForGroup(int groupId) {
    Set<SimpleAlarm> res = new TreeSet<SimpleAlarm>();
    //List<String> res = new ArrayList<String>();
    StringBuffer nodes = new StringBuffer();
    StringBuffer services = new StringBuffer();

    Group g = groupDao.get(groupId);
      boolean firstInserted = false;
      for (Integer nodeId : getNodes(g)) {
        if (firstInserted) {
          nodes.append(",");
        }
        nodes.append(nodeId);
        firstInserted = true;
      }
      if (firstInserted) {
        OnmsCriteria criteria = new OnmsCriteria(OnmsAlarm.class, "alarm");
        OnmsCriteria nodeCriteria = criteria.createCriteria("node");
        nodeCriteria.add(Restrictions.sqlRestriction("{alias}.nodeId in ( " + nodes.toString() + " )"));
        nodeCriteria.add(Restrictions.ne("type", "D"));
        criteria.addOrder(Order.desc("alarm.severityId"));
        List<OnmsAlarm> alarms = alarmDao.findMatching(criteria);
        for (OnmsAlarm alarm : alarms) {
          res.add(new SimpleAlarm(alarm.getId(), getSeverityString(alarm.getSeverity()), alarm.getNode().getLabel(), alarm.getLogMsg(), alarm.getDescription(), alarm.getCounter(), new Date(alarm.getFirstEventTime().getTime()), new Date(alarm.getLastEventTime().getTime())));
        }
      }

      firstInserted = false;
      services.append("(");
      for (Integer serviceId : getServices(g)) {
        if (firstInserted) {
          services.append(" or ");
        }
        OnmsMonitoredService srvc = monitoredServiceDao.load(serviceId);
        services.append("({alias}.nodeId = ");
        services.append(srvc.getNodeId());
        services.append(" and {alias}.ipAddr = ");
        services.append("'" + srvc.getIpAddress() + "'");
        services.append(" and {alias}.serviceId = ");
        services.append(srvc.getServiceId());
        services.append(")");
        firstInserted = true;
      }
      services.append(")");
      if (firstInserted) {
        OnmsCriteria criteria = new OnmsCriteria(OnmsAlarm.class, "alarm");
        criteria.add(Restrictions.sqlRestriction(services.toString()));
        OnmsCriteria nodeCriteria = criteria.createCriteria("node");
        nodeCriteria.add(Restrictions.ne("type", "D"));
        //nodeCriteria.add(Restrictions.sqlRestriction("{alias}.nodeId in ( " + services.toString() + " )"));
        //nodeCriteria.add(Restrictions.ne("type", "D"));
        criteria.addOrder(Order.desc("alarm.severityId"));
        List<OnmsAlarm> alarms = alarmDao.findMatching(criteria);
        for (OnmsAlarm alarm : alarms) {
          res.add(new SimpleAlarm(alarm.getId(),getSeverityString(alarm.getSeverity()), alarm.getNode().getLabel(), alarm.getLogMsg(), alarm.getDescription(), alarm.getCounter(), new Date(alarm.getFirstEventTime().getTime()), new Date(alarm.getLastEventTime().getTime())));
        }
      }
   
    return res;
  }

  /**
   * Retrieve all services for a specified interface in a specified node.
   *
   * @param nodeId - identifier for the node
   * @param interfaceId - identifier for the interface
   * @return a map with all services
   */
  public Map<String, String> getServiceInterfaceList(String nodeId, String interfaceId) {
    Map res = new TreeMap<String, String>();

    if (interfaceId != null && interfaceId.length() > 0) {
      try {
        int iId = Integer.parseInt(interfaceId);
        OnmsIpInterface intf = ipInterfaceDao.load(iId);
        if (intf != null) {
          Set<OnmsMonitoredService> services = intf.getMonitoredServices();
          for (OnmsMonitoredService svc : services) {
            res.put(svc.getId(), svc.getServiceName());
          }
        }
      } catch (Throwable e) {
      }
    }
    return res;
  }
}
