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
package org.opennms.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opennms.netmgt.dao.GroupDao;
import org.opennms.netmgt.dao.MonitoredServiceDao;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.dao.support.VirtualGroupsService;
import org.opennms.netmgt.model.Group;
import org.opennms.netmgt.model.GroupItem;
import org.opennms.netmgt.model.OnmsMonitoredService;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.web.category.CategoryUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Controller that manage the dashboard created ad-hoc for the Virtual Groups.
 *
 * Using this new dashboard is possible to monitor nodes and services, navigate
 * inside the virtual groups structure, view allarms and so on...
 *
 * @author Daniele Piras for Pronetics S.p.A
 */
public class MonitorVirtualGroupController extends AbstractController {

  private GroupDao groupDao;
  private NodeDao nodeDao;
  private VirtualGroupsService groupsService;
  private MonitoredServiceDao monitoredServiceDao;

  /**
   * Controller Entry Point.
   *
   * @param request http request
   * @param response http response
   * @return a normal ModelAndView
   * @throws java.lang.Exception
   */
  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map maps = new TreeMap();
    Group g = loadGroupFromRequest(request);
    String description = "All Groups";
    if (g != null) {
      maps.put("items", g.getItems());
      description = g.getDescription();
      maps.put("groupId", g.getId());
    }
    else
    {
      maps.put("groupId", "" );
    }
    maps.put("description", description);
    
    maps.putAll(getReferenceMap(g, request));
    ModelAndView mav = new ModelAndView("virtualgroups/monitorVirtualGroup", maps);
    return mav;
  }

  /**
   * Retrieve the Group passed on the request.
   * The request must contain a groupName parameter.
   *
   * @param request - request to use
   * @return the Group
   */
  protected Group loadGroupFromRequest(HttpServletRequest request) {
    String groupName = request.getParameter("groupName");
    if (groupName == null) {
      return null;
    }

    return groupDao.get(Integer.parseInt(groupName));
  }

  /**
   * Method that add other useful objects to the views.
   * This method add:
   * - allgroups : with a list of all groups
   * - entries : a list of MonitorEntry

   * @param group - selected group
   * @param request - http request
   * @return the parameter map
   * @throws java.lang.Exception
   */
  protected Map getReferenceMap(Group group, HttpServletRequest request) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    List<Group> groups = groupDao.getAllGroups();
    Group currentGroup = loadGroupFromRequest(request);
    List<MonitorEntry> entries = new ArrayList<MonitorEntry>();
    if (currentGroup != null) {
      for (GroupItem item : currentGroup.getItems()) {
        MonitorEntry m = null;
        switch (item.getType()) {
          case GROUP:
            m = new MonitorGroupEntry((item));
            break;
          case NODE:
            m = new MonitorNodeEntry((item));
            break;
          case SERVICE:
            m = new MonitorServiceEntry((item));
            break;
        }
        entries.add(m);
      }
    } else {

      for (Group g : groups) {
        GroupItem item = new GroupItem(g);

        MonitorEntry m = new MonitorGroupEntry(item);
        entries.add(m);
      }
    }
    Collections.sort(entries);
    map.put("entries", entries);
    map.put("allgroups", getGroupDao().getAllGroups());
    return map;
  }

  /**
   * MonitorEntry works as a wrapper for a monitorable entity.
   * This class has convenient methods to understand if the monitored object
   * isDown, retrieve its description, its composition and so on.
   *
   */
  public abstract class MonitorEntry implements Comparable<MonitorEntry> {

    private GroupItem item;
    private Boolean isDown;

    public MonitorEntry(GroupItem item) {
      this.item = item;
    }

    protected GroupItem getGroupItem() {
      return item;
    }

    /**
     * Return the Unique Identifier for this monitored object.
     * @return unique identifier.
     */
    public abstract Integer getId();

    /**
     * Return the internal object of this monitored object
     * @return internal object
     */
    public abstract Object getData();

    /**
     * Return the description of the internal object.
     * @return the description
     */
    public abstract String getDescription();

    /**
     * Return the groupItem.
     * @return the item.
     */
    public GroupItem getItem() {
      return item;
    }

    /**
     * This is the internal method thas is called by the isDown() public method.
     * This method must verify the internal object status and return true if it is
     * down.
     * 
     * @return true if it is down, false otherwise.
     */
    protected abstract boolean checkIsDown();

    /**
     * Check if the internal object is down.
     * @return true if it is down, false otherwise.
     */
    public boolean isDown() {
      if (isDown == null) {
        isDown = checkIsDown();
      }
      return isDown;
    }

    /**
     * Return the number of elements inDown on this item.
     * For single entity like Node or Service the number returned is always 1 but
     * for Group it could be change.
     * @return a number of down.
     */
    public abstract int getNumberOfDown();

    /**
     * Return the total number of elements that compound this item
     * @return number of elements
     */
    public abstract int getTotalNumber();

    /**
     * Return the status color
     * @return a string rappresented the color to use
     */
    public String getColorClass() {
      String res = null;
      if (isDown()) {
        res = CategoryUtil.getCategoryClass(100, 50, 0); // Force a critical
      } else {
        res = CategoryUtil.getCategoryClass(100, 50, 100); // Force a normal
      }
      return res;
    }

    public int compareTo(MonitorEntry o) {
      return getDescription().compareTo(o.getDescription());
    }

    @Override
    public boolean equals(Object obj) {
      return this == obj;
    }
  }

  /**
   * Implementation of MonitorEntry that acts as wrapper for Node entities.
   */
  class MonitorNodeEntry extends MonitorEntry {

    OnmsNode node;

    public MonitorNodeEntry(GroupItem item) {
      super(item);
      node = nodeDao.load(item.getContentNodeId());
    }

    @Override
    public Integer getId() {
      return node.getId();
    }

    @Override
    public Object getData() {
      return node;
    }

    @Override
    public String getDescription() {
      return getGroupItem().getDescription();
    }

    protected boolean checkIsDown() {

      return node.isDown();
    }

    @Override
    public int getNumberOfDown() {
      return (isDown()) ? 1 : 0;
    }

    @Override
    public int getTotalNumber() {
      return 1;
    }
  }

  /**
   * Implementation of MonitorEntry that acts as wrapper for Service entities.
   */
  class MonitorServiceEntry extends MonitorEntry {

    OnmsMonitoredService service;

    @Override
    public Integer getId() {
      return service.getId();
    }

    public MonitorServiceEntry(GroupItem item) {
      super(item);
      service = monitoredServiceDao.load(item.getContentServiceId());
    }

    @Override
    public Object getData() {
      return service;
    }

    @Override
    public String getDescription() {
      return getGroupItem().getDescription();
    }

    protected boolean checkIsDown() {

      return service.isDown();
    }

    @Override
    public int getNumberOfDown() {
      return (isDown()) ? 1 : 0;
    }

    @Override
    public int getTotalNumber() {
      return 1;
    }
  }

  /**
   * Implementation of MonitorEntry that acts as wrapper for Group entities.
   */
  class MonitorGroupEntry extends MonitorEntry {

    Group group;

    @Override
    public Integer getId() {
      return group.getId();
    }

    @Override
    public Object getData() {
      return group;
    }

    public MonitorGroupEntry(GroupItem item) {
      super(item);
      group = groupDao.get(item.getContentGroupId());
    }

    @Override
    public String getDescription() {
      return getGroupItem().getDescription();
    }

    private boolean checkIsDown(Group g) {
      return groupsService.isDown( g );
    }

    protected boolean checkIsDown() {
      return checkIsDown(group);
    }

    @Override
    public int getNumberOfDown() {
      int count = 0;
      for (GroupItem it : group.getItems()) {
        if (it.getType() == GroupItem.ItemType.NODE) {
          OnmsNode node = nodeDao.load(it.getContentNodeId());
          if (node.isDown()) {
            count++;
          }
        } else if (it.getType() == GroupItem.ItemType.GROUP) {
          Group gr = groupDao.get(it.getContentGroupId());
          if (checkIsDown(gr)) {
            count++;
          }
        } else if (it.getType() == GroupItem.ItemType.SERVICE) {
          OnmsMonitoredService service = monitoredServiceDao.load(it.getContentServiceId());
          if (service.isDown()) {
            count++;
          }
        }
      }
      return count;
    }

    @Override
    public int getTotalNumber() {
      return group.getItems().size();
    }
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
     System.out.println( "Setting node services : " + nodeDao );
    this.nodeDao = nodeDao;
  }

  public VirtualGroupsService getGroupsService() {
    return groupsService;
  }

  public void setGroupsService(VirtualGroupsService groupsService) {
    this.groupsService = groupsService;
  }

  

}
