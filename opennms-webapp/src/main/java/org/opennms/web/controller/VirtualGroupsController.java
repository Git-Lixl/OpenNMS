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
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.opennms.netmgt.dao.GroupDao;
import org.opennms.netmgt.model.Group;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * Controller to manage Virtual Groups. 
 * This controller is responsible to add and remove virtualgroups from the system.
 *
 * @author Daniele Piras for Pronetics S.p.A
 */
public class VirtualGroupsController extends SimpleFormController {

  private GroupDao groupDao;
  private DAOUtils daoUtils;

  /**
   * This class is used to pass parameters from the page to the model and from
   * the model to the page.
   *
   * In base on the action different methods are called.
   */
  public static class GroupAction {

    private String m_action = "show";
    private String m_groupName;

    public String getAction() {
      return m_action;
    }

    public void setAction(String action) {
      m_action = action;
    }

    public String getGroupName() {
      return m_groupName;
    }

    public void setGroupName(String groupName) {
      m_groupName = groupName;
    }
  }

  public VirtualGroupsController() {
    setCommandClass(GroupAction.class);
    setCommandName("group");
    setFormView("virtualgroups/virtualGroups");
    setSuccessView("virtualgroups/virtualGroups");
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
  protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object cmd, BindException errors) throws Exception {
    GroupAction command = (GroupAction) cmd;
    String action = command.getAction();

    if (action == null || "show".equalsIgnoreCase(action)) {
      return doShow(request, response, command, errors);
    } else if ("addGroup".equalsIgnoreCase(action)) {
      return doAddGroup(request, response, command, errors);
    } else if ("deleteGroup".equalsIgnoreCase(action)) {
      return doDeleteGroup(request, response, command, errors);

    } else {
      errors.reject("Unrecognized action: " + action);
      return super.onSubmit(request, response, command, errors);
    }
  }

  /**
   * Delete a VirtualGroup
   *
   * @param request http request
   * @param response http response
   * @param command EditCommand
   * @param errors errors map
   * @return a ModelAndView
   * @throws java.lang.Exception
   */
  private ModelAndView doDeleteGroup(HttpServletRequest request, HttpServletResponse response, GroupAction command, BindException errors) throws Exception {
    String groupName = command.getGroupName();
    if (groupName == null || groupName.trim().length() == 0) {
      errors.reject("Invalid empty group name.");
    } else {
      try {
        Group g = getGroupDao().load(Integer.parseInt(groupName));
        daoUtils.deleteObject(g);
      } catch (Exception e) {
        errors.reject(e.getMessage());
      }
    }
    return showForm(request, response, errors);

  }

  /**
   * Method that not implements any specific actions.
   *
   * @param request http request
   * @param response http response
   * @param command EditCommand
   * @param errors errors map
   * @return a ModelAndView
   * @throws java.lang.Exception
   */
  private ModelAndView doShow(HttpServletRequest request, HttpServletResponse response, GroupAction command, BindException errors) throws Exception {
    return showForm(request, response, errors);
  }

  /**
   * Create a new VirtualGroup
   *
   * @param request http request
   * @param response http response
   * @param command EditCommand
   * @param errors errors map
   * @return a ModelAndView
   * @throws java.lang.Exception
   */
  private ModelAndView doAddGroup(HttpServletRequest request, HttpServletResponse response, GroupAction command, BindException errors) throws Exception {
    String groupName = command.getGroupName();
    if (groupName == null || groupName.trim().length() == 0) {
      errors.reject("Invalid empty group name.");
    } else {
      try {
        Group g = new Group();
        g.setDescription(groupName);

        daoUtils.saveObject(g);
      } catch (Exception e) {
        errors.reject(e.getMessage());
      }
    }
    return showForm(request, response, errors);

  }

  /**
   * Method that add other useful objects to the views.
   * This method add:
   * - groups : with a list of all groups.
   * @param request the http request
   * @return the parameters map
   * @throws java.lang.Exception
   */
  @Override
  protected Map referenceData(HttpServletRequest request) throws Exception {
    Map<String, Object> refData = new HashMap<String, Object>();
    refData.put("groups", getGroupDao().getAllGroups());
    //refData.put("dbNodeCounts", m_provisioningService.getGroupDbNodeCounts());
    return refData;
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
