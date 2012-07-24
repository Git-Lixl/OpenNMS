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

import java.util.List;
import java.util.Map;

import org.jclouds.chef.ChefApi;
import org.opennms.core.utils.LogUtils;

public class ChefProvisioningConfiguration {
    private ChefProvisioningConfiguration m_defaultConfig;
    private Map<String,ChefProvisioningConfiguration> m_envConfigMap;
    
    private boolean m_makeNodes;
    private boolean m_makeInterfaces;
    private boolean m_makeRfc1918Interfaces;
    
    private ChefProvisioningConfiguration() {
        m_makeNodes = true;
        m_makeInterfaces = true;
        m_makeRfc1918Interfaces = true;
        m_defaultConfig = this;
    }
    
    public ChefProvisioningConfiguration(ChefApi api) {
        m_makeNodes = Boolean.valueOf(api.getDatabagItem("opennms", "make_nodes").toString());
        m_makeInterfaces = Boolean.valueOf(api.getDatabagItem("opennms", "make_interfaces").toString());
        m_makeRfc1918Interfaces = Boolean.valueOf(api.getDatabagItem("opennms", "make_rfc1918_interfaces").toString());
        m_defaultConfig = this;
    }
    
    public ChefProvisioningConfiguration getDefaultConfig() {
        return m_defaultConfig;
    }
    
    public ChefProvisioningConfiguration getConfigForEnvironment(String environment) {
        if (m_envConfigMap.containsKey(environment)) {
            LogUtils.debugf(getClass(), "getConfigForEnvironment: Specific config exists for Chef environment '%s', making composite config", environment);
            return makeCompositeConfig(m_envConfigMap.get(environment));
        } else {
            LogUtils.debugf(getClass(), "getConfigForEnvironment: No specific config exists for Chef environment '%s', returning default config", environment);
            return m_defaultConfig;
        }
    }
    
    private ChefProvisioningConfiguration makeCompositeConfig(ChefProvisioningConfiguration specificConfig) {
        if (specificConfig == specificConfig.getDefaultConfig()) {
            LogUtils.debugf(getClass(), "makeCompositeConfig: This config is the same as the default, returning the default config");
            return m_defaultConfig;
        }
        
        return m_defaultConfig;
    }
}
