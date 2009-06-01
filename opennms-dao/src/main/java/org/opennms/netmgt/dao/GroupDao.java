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
package org.opennms.netmgt.dao;

import org.opennms.netmgt.model.*;
import java.util.List;

/**
 * Base interface to manage store/load Group from a persistance entity.
 *
 * @author Daniele Piras for Pronetics S.p.A
 */
public interface GroupDao extends OnmsDao<Group, Integer>{

  /**
   * Return a complete list with all saved groups
   * @return the groups list
   */
  public List<Group> getAllGroups();

   /**
   * Return a list of all groups that contains nodeId
   * @param nodeId - node identifier
   * @return the groups list
   */
   public List<Group> findGroupByNodeId( Long nodeId );

  /**
   * Return a list of all groups that contains a serviceid
   * @param nodeId - node identifier
   * @return the groups list
   */
   public List<Group> findGroupByServiceId( Long serviceId );

   /**
   * Return a list of all groups with a specified decription
   * @param description - group description
   * @return the groups list
   */
   public List<Group> findGroupByDescription( String description );

  
  /**
   * Remove a GroupItem from the persistance entity.
   * 
   * @param itemId - Unique Identifier for the GroupItem.
   */
  public void deleteGroupItem( int itemId );
}
