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

/**
 * Utility class that rappresent an Alarm.
 * This class is used for Monitoring VirtualGroup.
 *
 * @author Daniele Piras for Pronetics S.p.A
 */
public class SimpleAlarm {
  public Integer id;
  public String severityLevel;
  public String nodeLabel;
  public String description;
  public int count;
  public Date firstTime;
  public Date lastTime;
  public String logMessage;

  public SimpleAlarm() {
    super();
    this.id = 0;
  }

  public SimpleAlarm(Integer id, String severity, String nodeLabel, String logMessage, String description, int count, Date firstTime, Date lastTime) {
    super();
    this.id = id;
    this.severityLevel = severity;
    this.nodeLabel = nodeLabel;
    this.description = description;
    this.count = count;
    this.firstTime = firstTime;
    this.lastTime = lastTime;
    this.logMessage = logMessage;
  }

  @Override
  public boolean equals(Object obj) {
    if ( ! ( obj instanceof SimpleAlarm) )
    {
      return false;
    }
    return id.equals(((SimpleAlarm)obj).id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }



  public int getId()
  {
    return id;
  }


  public String getSeverityLevel() {
    return severityLevel;
  }

  public void setSeverityLevel(String severityLevel) {
    this.severityLevel = severityLevel;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getFirstTime() {
    return firstTime;
  }

  public void setFirstTime(Date firstTime) {
    this.firstTime = firstTime;
  }

  public Date getLastTime() {
    return lastTime;
  }

  public void setLastTime(Date lastTime) {
    this.lastTime = lastTime;
  }

  public String getNodeLabel() {
    return nodeLabel;
  }

  public void setNodeLabel(String nodeLabel) {
    this.nodeLabel = nodeLabel;
  }
}
