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
package org.opennms.netmgt.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * GroupItem rappresent a Single Group Item.
 * A GroupItem can contains:
 * - Node
 * - Service
 * - Group
 *
 * Is not necessary to create GroupItem in your code.
 * GroupItems are managed by the Group class that has convenients methods
 * to create GroupItems.
 *
 * @author Daniele Piras for Pronetics S.p.A
 */
@javax.persistence.Entity
@Table(name="group_items")
public class GroupItem {

    /**
     * Enumeration rappresent the TYPE of GroupItem availabled.
     */
    public static enum ItemType { GROUP,NODE, SERVICE }

    private Integer m_id;

    /** Owner Group **/
    private Group m_owner;

    private String m_description;

    /* NOTE:
     * Initially I've use the same field to store the contentId.
     * I've modify that because with a singleId I can't use the foreign key
     * on different tables (groups, nodes).
     * This is a little dirty solution but is the price to pay :-)
     */
    private Integer m_contentGroupId;
    private Integer m_contentNodeId;
    private Integer m_contentServiceId;

    private ItemType m_type;

    protected GroupItem() {
    }


    /**
     * Constructor that create a GroupItem from a Node
     *
     * @param node to use
     */
    public GroupItem( OnmsNode node )
    {
        setType( ItemType.NODE );
        setContentNodeId( node.getId() );
        setDescription( node.getLabel() );
    }

    /**
     * Constructor that create a GroupItem from a Group
     *
     * @param group to use
     */
    public GroupItem( Group group )
    {
      setType( ItemType.GROUP );
      setContentGroupId(  group.getId() );
      setDescription( group.getDescription() );
    }


    /**
     * Constructor that create a GroupItem from a Service
     *
     * @param service to use
     */
    public GroupItem( OnmsMonitoredService service )
    {
      setType( ItemType.SERVICE );
      //setContentNodeId( service.getNodeId() );
      setContentServiceId( service.getId() );
      setDescription( service.getServiceName() + " on " +  service.getIpAddress() );
    }

    @Id
    @Column(name="itemId")
    @SequenceGenerator(name="groupItemsNxtId", sequenceName="groupItemsNxtId")
    @GeneratedValue(generator="groupItemsNxtId")
    public Integer getId() {
        return m_id;
    }

    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="groupId")
    public Group getOwner()
    {
      return m_owner;
    }


    public void setDescription( String description )
    {
      m_description = description;
    }

    @Column(name="description")
    public String getDescription()
    {
      return m_description;
    }



    public void setOwner( Group group )
    {
      m_owner = group;
    }

    public void setId(Integer nodeid) {
        m_id = nodeid;
    }


    @Enumerated(EnumType.STRING)
    @Column(name="itemType")
    public ItemType getType()
    {
      return m_type;
    }

    public void setType( ItemType type )
    {
      m_type = type;
    }

    @Column(name="contentGroupId")
    public Integer getContentGroupId()
    {
      return m_contentGroupId;
    }

    public void setContentGroupId( Integer id )
    {
      m_contentGroupId = id;
    }

    @Column(name="contentServiceId")
    public Integer getContentServiceId()
    {
      return m_contentServiceId;
    }

    public void setContentServiceId( Integer id )
    {
      m_contentServiceId = id;
    }

    @Column(name="contentNodeId")
    public Integer getContentNodeId()
    {
      return m_contentNodeId;
    }

    public void setContentNodeId( Integer id )
    {
      m_contentNodeId = id;
    }
}