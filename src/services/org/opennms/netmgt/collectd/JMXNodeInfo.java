/*
 * Created on Mar 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.opennms.netmgt.collectd;

import java.util.*;

/**
 * This class encapsulates all of the node-level data required by the SNMP data
 * collector in order to successfully perform data collection for a scheduled
 * primary SNMP interface.
 * 
 * @author <a href="mailto:mike@opennms.org">Mike Davidson </a>
 * @author <a href="http://www.opennms.org/">OpenNMS </a>
 */
public class JMXNodeInfo {
    private int m_nodeId;

    private int m_primarySnmpIfIndex;

    private List m_oidList;
    private HashMap m_mbeans;

    private HashMap m_dsList;

    public JMXNodeInfo(int nodeId, int primaryIfIndex) {
        m_nodeId = nodeId;
        m_primarySnmpIfIndex = primaryIfIndex;
        m_oidList = null;
        m_dsList = null;
        m_mbeans = new HashMap();
    }

    public int getNodeId() {
        return m_nodeId;
    }
    
    public void setMBeans(HashMap map) {
        m_mbeans = map;
    }
    
    public HashMap getMBeans() {
        return m_mbeans;
    }

    public void setNodeId(int nodeId) {
        m_nodeId = nodeId;
    }

    public int getPrimarySnmpIfIndex() {
        return m_primarySnmpIfIndex;
    }

    public void setDsMap(HashMap dsList) {
        m_dsList = dsList;
    }

    public void setAttributeList(List oidList) {
        m_oidList = oidList;
    }

    public HashMap getDsMap() {
        return m_dsList;
    }

    public List getAttributeList() {
        return m_oidList;
    }
} // end class
