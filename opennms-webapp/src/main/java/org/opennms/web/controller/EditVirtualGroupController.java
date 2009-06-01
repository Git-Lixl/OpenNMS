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

import org.opennms.netmgt.dao.support.DAOUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opennms.netmgt.dao.GroupDao;
import org.opennms.netmgt.dao.MonitoredServiceDao;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.model.Group;
import org.opennms.netmgt.model.GroupItem;
import org.opennms.netmgt.model.OnmsMonitoredService;
import org.opennms.netmgt.model.OnmsNode;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * Controller that manage the Virtual Groups editing.
 *
 * @author Daniele Piras for Pronetics S.p.A
 */
public class EditVirtualGroupController extends SimpleFormController {

  private GroupDao groupDao;
  private NodeDao nodeDao;
  private MonitoredServiceDao monitoredServiceDao;
  private DAOUtils daoUtils;

  public EditVirtualGroupController() {
    setCommandClass(EditCommand.class);
    setCommandName("groupEditForm");
    setFormView("virtualgroups/editVirtualGroup");
  }

  /**
   * Dispatching method that, in base on the action, invoke the appropriate action.
   *
   * @param request http request
   * @param response http response
   * @param command EditCommand
   * @param errors errors map
   * @return a ModelAndView
   * @throws java.lang.Exception
   */
  @Override
  protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
    EditCommand cmd = (EditCommand) command;
    String action = cmd.getAction();
    if (action == null) {
      return doShow(request, response, cmd, errors);
    } else if ("addGroup".equalsIgnoreCase(action)) {
      return doAddGroup(request, response, cmd, errors);
    } else if ("addNode".equalsIgnoreCase(action)) {
      return doAddNode(request, response, cmd, errors);
    } else if ("addService".equalsIgnoreCase(action)) {
      return doAddService(request, response, cmd, errors);
    } else if ("delete".equalsIgnoreCase(action)) {
      return doDelete(request, response, cmd, errors);
    } else {
      errors.reject("Unrecognized action: " + action);
      return showForm(request, response, errors);
    }


  }

  /**
   * Method to initialize the EditCommand from the request.
   *
   * @param request to use
   * @return an EditCommand object
   * @throws java.lang.Exception
   */
  @Override
  protected Object formBackingObject(HttpServletRequest request) throws Exception {
    EditCommand formCommand = new EditCommand();
    initializeCommand(request, formCommand);
    return formCommand;
  }

  /**
   * Retrieve a group in base on the request.
   * This method use the "groupName" parameter to retrieve the group.
   *
   * @param request to use
   * @return the Group
   */
  protected Group loadGroupFromRequest(HttpServletRequest request) {
    String groupName = request.getParameter("groupName");
    if (groupName == null) {
      throw new IllegalArgumentException("groupName required");
    }

    return groupDao.load(Integer.parseInt(groupName));
  }

  /**
   * Initialize an EditCommand using the given group.
   *
   * @param g to use to initialize the command
   * @param formCommand the command to initialize
   */
  private void initializeCommandFromGroup(Group g, EditCommand formCommand) {
    if (g != null && formCommand != null) {
      formCommand.setItems(g.getItems());
      formCommand.setDescription(g.getDescription());
      formCommand.setGroupName("" + g.getId());
      formCommand.setFormData(g);
    }
  }

  private void initializeCommand(HttpServletRequest request, EditCommand formCommand) throws Exception {
    initializeCommandFromGroup(loadGroupFromRequest(request), formCommand);
  }

  /**
   * Method that add other useful objects to the views.
   * This method add:
   * - allgroups : with a list of all group that is possible to add to this group.
   * - allnodes : with a list of all existing nodes
   * - filterednodes : with a list of all the nodes that is possible to add
   *   to this group.
   * @param request
   * @return the parameters map
   * @throws java.lang.Exception
   */
  @Override
  protected Map referenceData(HttpServletRequest request) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    List<Group> groups = groupDao.getAllGroups();
    Group currentGroup = loadGroupFromRequest(request);
    filterGroups(currentGroup, groups);
    map.put("allgroups", groups);
    ArrayList<OnmsNode> nodes = new ArrayList<OnmsNode>(new LinkedHashSet(nodeDao.findAll()));
    Collections.sort(nodes, new Comparator<OnmsNode>() {

      public int compare(OnmsNode o1, OnmsNode o2) {
        return o1.getId().compareTo(o2.getId());
      }
    });
    ArrayList<OnmsNode> filterNodes = new ArrayList<OnmsNode>();
    filterNodes.addAll(nodes);
    filterNodes(currentGroup, filterNodes);
    map.put("allnodes", nodes);
    map.put("filterednodes", filterNodes);
    return map;
  }

  /**
   * This class is used to pass parameters from the page to the model and from
   * the model to the page.
   *
   * In base on the action different methods are called.
   */
  public static class EditCommand {

    private String m_action;
    private Object m_formData;
    private String m_groupName = "hardcoded";
    private String m_description;
    private String m_groupToAdd;
    private String m_nodeToAdd;
    private String m_serviceToAdd;
    private Set<GroupItem> m_items;

    /**
     * Return the service to add
     * @return the serviceId
     */
    public String getServiceToAdd() {
      return m_serviceToAdd;
    }

    public void setServiceToAdd(String serviceToAdd) {
      this.m_serviceToAdd = serviceToAdd;
    }

    public String getDescription() {
      return m_description;
    }

    /**
     * Return the group to add to this group
     * @return groupId to add
     */
    public String getGroupToAdd() {
      return m_groupToAdd;
    }

    public void setGroupToAdd(String groupToAdd) {
      this.m_groupToAdd = groupToAdd;
    }

    public void setDescription(String description) {
      this.m_description = description;
    }

    /**
     * Return the action to perform.
     * Valid actions are:
     * 'addGroup' to add a group
     * 'addNode' to add a node
     * 'addService' to add a service
     * 'delete' to delete the item
     *
     * @return
     */
    public String getAction() {
      return m_action;
    }

    public void setAction(String action) {
      m_action = action;
    }

    /**
     * Return the name of the group
     * @return
     */
    public String getGroupName() {
      return m_groupName;
    }

    public void setGroupName(String groupName) {
      m_groupName = groupName;
    }

    /**
     * Return the associate Group
     * @return the Group Object
     */
    public Object getFormData() {
      return m_formData;
    }

    public void setFormData(Object importData) {
      m_formData = importData;
    }

    public Set<GroupItem> getItems() {
      return m_items;
    }

    public void setItems(Set<GroupItem> items) {
      m_items = items;
    }

    /**
     * Return the node to add
     * @return nodeId to add
     */
    public String getNodeToAdd() {
      return m_nodeToAdd;
    }

    public void setNodeToAdd(String nodeToAdd) {
      this.m_nodeToAdd = nodeToAdd;
    }
  }

  /**
   * Method to add a group to this group.
   *
   * @param request - http request
   * @param response - http response
   * @param cmd - EditCommand with all parameters
   * @param errors - error map
   * @return a ModelAndView
   * @throws java.lang.Exception
   */
  private ModelAndView doAddGroup(HttpServletRequest request, HttpServletResponse response, EditCommand cmd, BindException errors) throws Exception {
    String gta = cmd.getGroupToAdd();
    if (gta != null && gta.trim().length() > 0) {
      Group gToAdd = groupDao.load(Integer.parseInt(gta));
      Group g = (Group) cmd.getFormData();
      if (groupContaints(g, gToAdd) || groupContaints(gToAdd, g)) {
        errors.reject("Impossible to add this group. Circular dependence found.");
      } else {
        daoUtils.saveObject(g.addGroup(gToAdd));
        daoUtils.mergeObject(g);
        initializeCommandFromGroup(groupDao.load(g.getId()), cmd);
      }
    }
    return showForm(request, response, errors);
  }

  /**
   * Method to add a node to this group.
   *
   * @param request - http request
   * @param response - http response
   * @param cmd - EditCommand with all parameters
   * @param errors - error map
   * @return a ModelAndView
   * @throws java.lang.Exception
   */
  private ModelAndView doAddNode(HttpServletRequest request, HttpServletResponse response, EditCommand cmd, BindException errors) throws Exception {
    String nta = cmd.getNodeToAdd();
    if (nta != null && nta.trim().length() > 0) {
      OnmsNode nodeToAdd = nodeDao.get(Integer.parseInt(nta));
      Group g = (Group) cmd.getFormData();
      daoUtils.saveObject(g.addNode(nodeToAdd));
      daoUtils.mergeObject(g);
      // Force reloading
      initializeCommandFromGroup(groupDao.load(g.getId()), cmd);
    }
    return showForm(request, response, errors);
  }

  /**
   * Method to add a service to this group.
   *
   * @param request - http request
   * @param response - http response
   * @param cmd - EditCommand with all parameters
   * @param errors - error map
   * @return a ModelAndView
   * @throws java.lang.Exception
   */
  private ModelAndView doAddService(HttpServletRequest request, HttpServletResponse response, EditCommand cmd, BindException errors) throws Exception {
    String srv = cmd.getServiceToAdd();
    if (srv != null && srv.trim().length() > 0) {
      OnmsMonitoredService service = monitoredServiceDao.load(Integer.parseInt(srv));
      Group g = (Group) cmd.getFormData();
      daoUtils.saveObject(g.addService(service));
      daoUtils.mergeObject(g);
      initializeCommandFromGroup(groupDao.load(g.getId()), cmd);
    }
    return showForm(request, response, errors);
  }

  /**
   * Method to delete an item inside a group
   *
   * @param request - http request
   * @param response - http response
   * @param cmd - EditCommand with all parameters
   * @param errors - error map
   * @return a ModelAndView
   * @throws java.lang.Exception
   */
  private ModelAndView doDelete(HttpServletRequest request, HttpServletResponse response, EditCommand cmd, BindException errors) throws Exception {
    Integer itemId = ServletRequestUtils.getRequiredIntParameter(request, "itemToDelete");
    if (itemId != -1) {
      Group g = (Group) cmd.getFormData();
      Iterator<GroupItem> gi = g.getItems().iterator();
      while (gi.hasNext()) {
        GroupItem item = gi.next();
        if (item.getId().equals(itemId)) {
          gi.remove();
          daoUtils.deleteObject(item);
        }
      }
      daoUtils.mergeObject(g);
      initializeCommand(request, cmd);
    }
    return showForm(request, response, errors);
  }

  /**
   * Null action with no-real-action
   * 
   * @param request - http request
   * @param response - http response
   * @param cmd - EditCommand with all parameters
   * @param errors - error map
   * @return a ModelAndView
   * @throws java.lang.Exception
   */
  private ModelAndView doShow(HttpServletRequest request, HttpServletResponse response, EditCommand cmd, BindException errors) throws Exception {
    return showForm(request, response, errors);
  }

  /**
   * Verify if the destination group is already presents in the source group or
   * into its children.
   * 
   * @param source group to use
   * @param dest group to verify
   * @return true if dest is already presents into source.
   */
  protected boolean groupContaints(Group source, Group dest) {
    boolean res = false;
    if (source == null || dest == null) {
      return false;
    }
    Set<GroupItem> itms = source.getItems();
    for (GroupItem gi : itms) {
      if (gi.getType() == GroupItem.ItemType.GROUP) {
        if (gi.getContentGroupId().equals(dest.getId())) {
          res = true;
        } else {
          res = groupContaints(groupDao.load(gi.getContentGroupId()), dest);
        }
        if (res) {
          break;
        }
      }
    }
    return res;
  }

  /**
   * Verify if the given node is already presents on the given group.
   *
   * @param source group to use
   * @param node to verify
   * @return true if the node is already presents
   */
  protected boolean groupContaints(Group source, OnmsNode node) {
    boolean res = false;
    if (source == null || node == null) {
      return false;
    }
    Set<GroupItem> itms = source.getItems();
    for (GroupItem gi : itms) {
      if (gi.getType() == GroupItem.ItemType.NODE) {
        if (gi.getContentNodeId().equals(node.getId())) {
          res = true;
          break;
        }
      }
    }
    return res;
  }

  /**
   * Filter a list of groups deleting all groups that could cause a
   * circular reference problem.
   *
   * @param currentGroup group to use
   * @param source a list of groups to filter
   */
  protected void filterGroups(Group currentGroup, List<Group> source) {
    Iterator<Group> i = source.iterator();
    while (i.hasNext()) {

      Group g = i.next();
      if ((g.getId() != null && g.getId().equals(currentGroup.getId())) // Eliminate the selected group from the list..
        ||
        (groupContaints(currentGroup, g)) // Eliminate all group already inserted.
        ||
        (groupContaints(g, currentGroup)) // Eliminate all group that can cause circular reference.
        ) {
        i.remove();
      }
    }
  }

  /**
   * Filter a list of nodes deleting all nodes that are already inserted in the
   * given group.
   *
   * @param currentGroup group to use
   * @param source a list of nodes.
   */
  protected void filterNodes(Group currentGroup, List<OnmsNode> source) {
    Iterator<OnmsNode> i = source.iterator();
    while (i.hasNext()) {
      OnmsNode g = i.next();
      if (groupContaints(currentGroup, g)) // Eliminate nodes already inserted
      {
        i.remove();
      }
    }
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

  public GroupDao getGroupDao() {
    return groupDao;
  }

  public void setGroupDao(GroupDao groupDao) {
    this.groupDao = groupDao;
  }

  public DAOUtils getDaoUtils() {
    return daoUtils;
  }

  public void setDaoUtils(DAOUtils daoUtils) {
    this.daoUtils = daoUtils;
  }
}
