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
import javax.ws.rs.core.Response;

import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsNodeList;
import org.opennms.web.rest.api.model.ApiVersion;
import org.opennms.web.rest.api.model.ResourceLocation;
import org.opennms.web.rest.client.RemoteHandler;
import org.opennms.web.rest.client.ResponseCallback;

public class NodeAPI extends AbstractAPI {

    public static final ResourceLocation LOCATION = new ResourceLocation(ApiVersion.Version1, "nodes");

    public NodeAPI(RemoteHandler remoteHandler) {
        super(remoteHandler);
    }

    public OnmsNode get(String foreignSource, String foreignId) {
        return get(String.format("%s:%s", foreignSource, foreignId));
    }

    public OnmsNode get(String id) {
        return remoteHandler.remoteInvoke(
                HttpMethod.GET,
                LOCATION.toString() + "/" + id,
                OnmsNode.class,
                null);
    }

    public void delete(String foreignSource, String foreignId) {
        delete(String.format("%s:%s", foreignSource, foreignId));
    }

    public void delete(String id) {
        remoteHandler.remoteInvoke(HttpMethod.DELETE, LOCATION.toString() + "/" + id, (Class) null, null);
    }

    public OnmsNode create(OnmsNode node) {
        return remoteHandler.remoteInvoke(HttpMethod.POST, LOCATION.toString(), OnmsNode.class, createEntity(node), new ResponseCallback<OnmsNode>() {
            @Override
            public OnmsNode callback(Response response) {
                return remoteHandler.remoteInvoke(HttpMethod.GET, response.getLocation().toString(), OnmsNode.class, null);
            }
        },
        null);
    }

    public OnmsNodeList list() {
        return remoteHandler.remoteInvoke(HttpMethod.GET, LOCATION.toString(), OnmsNodeList.class, null);
    }
}
