/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.provision.service.chef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.domain.DatabagItem;

import com.google.common.collect.Lists;

public class ChefProvisioningConfiguration {
    private JSONObject m_configJSON;
    private JSONObject m_configCloudJSON;

    private final String PARM_NODE_LABEL_SOURCE = "node_label_source";
    private String m_nodeLabelSource;
    
    private final String PARM_CRITICAL_PATH_DEFAULT_GW = "critical_path_default_gateway";
    private boolean m_criticalPathDefaultGateway;
    
    private final String PARM_SNMP_PRIMARY_INTERFACE = "snmp_primary_interface";
    private final String PARM_VAL_SNMP_PRIMARY_AUTOMATIC = "automatic";
    private final String PARM_VAL_SNMP_PRIMARY_CLOUD_PUBLIC = "cloud-public";
    private final String PARM_VAL_SNMP_PRIMARY_CLOUD_PRIVATE = "cloud-private";
    private String m_snmpPrimaryInterface;
    
    private final String PARM_SUPPRESS_NON_CLOUD_INTERFACES = "suppress_non_cloud_interfaces";
    private Map<String,Boolean> m_suppressNonCloudInterfaces = new HashMap<String,Boolean>();
    
    private final String PARM_SUPPRESS_CLOUD_INTERFACES = "suppress_cloud_interfaces";
    private Map<String,Boolean> m_suppressCloudInterfaces = new HashMap<String,Boolean>();

    private final String PARM_SUPPRESS_CLOUD_LOCAL_INTERFACES = "suppress_cloud_local_interfaces";
    private Map<String,Boolean> m_suppressCloudLocalInterfaces = new HashMap<String,Boolean>();
    
    private final String PARM_SUPPRESS_CLOUD_PUBLIC_INTERFACES = "suppress_cloud_public_interfaces";
    private Map<String,Boolean> m_suppressCloudPublicInterfaces = new HashMap<String,Boolean>();
    
    public ChefProvisioningConfiguration(ChefApi api) {
        m_configJSON = null;
        DatabagItem reqConfig = api.getDatabagItem("opennms", "requisitions");
        if (reqConfig != null) {
            try {
                m_configJSON = new JSONObject(reqConfig.toString());
            } catch (JSONException e) {
                // Well, we tried, didn't we?
            }
        }
        
        if (m_configJSON != null) {
            m_configCloudJSON = m_configJSON.optJSONObject("cloud");
        }
        
        m_nodeLabelSource = safeGetStringFromJSONConfig(PARM_NODE_LABEL_SOURCE, "name");
        m_criticalPathDefaultGateway = safeGetBooleanFromJSONConfig(PARM_CRITICAL_PATH_DEFAULT_GW, true);
        m_snmpPrimaryInterface = safeGetStringFromJSONConfig(PARM_SNMP_PRIMARY_INTERFACE, "automatic");

        List<String> knownCloudProviders = Lists.newArrayList("default", "ec2", "rackspace", "eucalyptus");
        for (String cloudProvider : knownCloudProviders) {
            m_suppressNonCloudInterfaces.put(cloudProvider, safeGetBooleanFromCloudJSONConfig("ec2", PARM_SUPPRESS_NON_CLOUD_INTERFACES, true));
            m_suppressCloudInterfaces.put(cloudProvider, safeGetBooleanFromCloudJSONConfig(cloudProvider, PARM_SUPPRESS_CLOUD_INTERFACES, false));
            m_suppressCloudLocalInterfaces.put(cloudProvider, safeGetBooleanFromCloudJSONConfig(cloudProvider, PARM_SUPPRESS_CLOUD_LOCAL_INTERFACES, true));
            m_suppressCloudPublicInterfaces.put(cloudProvider, safeGetBooleanFromCloudJSONConfig(cloudProvider, PARM_SUPPRESS_CLOUD_PUBLIC_INTERFACES, false));
        }
    }

    /**
     * 
     * @return the setting of nodeLabelSource
     */
    public String getNodeLabelSource() {
        return m_nodeLabelSource;
    }
    
    /**
     * @return the setting of criticalPathDefaultGateway
     */
    public boolean isCriticalPathDefaultGateway() {
        return m_criticalPathDefaultGateway;
    }

    /**
     * @return One of "automatic", "cloud-public", or "cloud-private"
     */
    public String getSnmpPrimaryInterface() {
        return m_snmpPrimaryInterface;
    }
    
    /**
     * @param cloudProvider the name of a cloud provider
     * @return the setting of suppressNonCloudInterfaces for that provider
     */
    public boolean isSuppressNonCloudInterfaces(String cloudProvider) {
        if (m_suppressNonCloudInterfaces.containsKey(cloudProvider)) {
            return m_suppressNonCloudInterfaces.get(cloudProvider);
        } else {
            return m_suppressNonCloudInterfaces.get("default");
        }
    }

    /**
     * @param cloudProvider the name of a cloud provider
     * @return the setting of suppressCloudLocalInterfaces for that provider
     */
    public boolean isSuppressCloudLocalInterfaces(String cloudProvider) {
        if (m_suppressCloudLocalInterfaces.containsKey(cloudProvider)) {
            return m_suppressCloudLocalInterfaces.get(cloudProvider);
        } else {
            return m_suppressCloudLocalInterfaces.get("default");
        }
    }

    /**
     * @param cloudProvider the name of a cloud provider
     * @return the setting of suppressCloudPublicInterfaces for that provider
     */
    public boolean isSuppressCloudPublicInterfaces(String cloudProvider) {
        if (m_suppressCloudPublicInterfaces.containsKey(cloudProvider)) {
            return m_suppressCloudPublicInterfaces.get(cloudProvider);
        } else {
            return m_suppressCloudPublicInterfaces.get("default");
        }
    }

    /**
     * @param cloudProvider the name of a cloud provider
     * @return the setting of suppressCloudInterfaces for that provider
     */
    public boolean isSuppressCloudInterfaces(String cloudProvider) {
        if (m_suppressCloudInterfaces.containsKey(cloudProvider)) {
            return m_suppressCloudInterfaces.get(cloudProvider);
        } else {
            return m_suppressCloudInterfaces.get("default");
        }
    }
    
    private boolean safeGetBooleanFromCloudJSONConfig(String cloudProvider, String key, boolean defaultValue) {
        return defaultValue;
    }

    private boolean safeGetBooleanFromJSONConfig(String key, boolean defaultValue) {
        boolean retValue;
        try {
            retValue = m_configJSON.getBoolean(key);
        } catch (Throwable t) {
            retValue = defaultValue;
        }
        return retValue;
    }
    
    private String safeGetStringFromJSONConfig(String key, String defaultValue) {
        String retValue;
        try {
            retValue = m_configJSON.getString(key);
        } catch (Throwable t) {
            retValue = defaultValue;
        }
        return retValue;
    }

}
