/*******************************************************************************
 * This file is part of OpenNMS(R).
 * <p>
 * Copyright (C) 2015 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2015 The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 * http://www.gnu.org/licenses/
 * <p>
 * For more information contact:
 * OpenNMS(R) Licensing <license@opennms.org>
 * http://www.opennms.org/
 * http://www.opennms.com/
 *******************************************************************************/

package org.opennms.web.rest.client.api;

import javax.ws.rs.HttpMethod;

import org.opennms.web.rest.api.model.IpInterfaceDTO;
import org.opennms.web.rest.api.model.IpInterfaceListDTO;
import org.opennms.web.rest.api.model.ResourceLocation;
import org.opennms.web.rest.client.RemoteHandler;

public class IpInterfaceAPI extends AbstractAPI {

    private final ResourceLocation location = NodeAPI.LOCATION;

    public IpInterfaceAPI(RemoteHandler remoteHandler) {
        super(remoteHandler);
    }

    public IpInterfaceListDTO list(String nodeCriteria) {
        return remoteHandler.remoteInvoke(HttpMethod.GET, location.toString() + "/" + nodeCriteria + "/ipinterfaces", IpInterfaceListDTO.class, null);
    }

    public IpInterfaceDTO get(String nodeCriteria, String ipAddress) {
        return remoteHandler.remoteInvoke(HttpMethod.GET, location.toString() + "/" + nodeCriteria + "/ipinterfaces/" + ipAddress, IpInterfaceDTO.class, null);
    }

    public void delete(String nodeCriteria, String ipAddress) {
        remoteHandler.remoteInvoke(
                HttpMethod.DELETE,
                location.toString() + "/" + nodeCriteria + "/ipinterfaces/" + ipAddress,
                (Class) null,
                null
        );
    }

    public IpInterfaceListDTO list(String foreignSource, String foreignId) {
        return list(String.format("%s:%s", foreignSource, foreignId));
    }

    public IpInterfaceDTO get(String foreignSource, String foreignId, String ipAddress) {
        return get(String.format("%s:%s", foreignSource, foreignId), ipAddress);
    }

    public void delete(String foreignSource, String foreignId, String ipAddress) {
        delete(String.format("%s:%s", foreignSource, foreignId), ipAddress);
    }

}
