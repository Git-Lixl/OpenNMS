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

import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.web.rest.api.model.ApiVersion;
import org.opennms.web.rest.api.model.ResourceLocation;
import org.opennms.web.rest.client.RemoteHandler;
import org.opennms.web.rest.client.ResponseCallback;
import org.opennms.web.rest.client.exceptions.RestClientException;

public class RequisitionAPI extends AbstractAPI {

    private final ResourceLocation location = new ResourceLocation(ApiVersion.Version1, "requisitions");

    public RequisitionAPI(RemoteHandler remoteHandler) {
        super(remoteHandler);
    }

    public Requisition create(Requisition requisition) {
        return remoteHandler.remoteInvoke(HttpMethod.POST, location.toString(), Requisition.class, createEntity(requisition), new ResponseCallback<Requisition>() {
            @Override
            public Requisition callback(Response response) throws RestClientException {
                return remoteHandler.remoteInvoke(HttpMethod.GET, response.getLocation().toString(), Requisition.class, null);
            }
        }, null);
    }

    public Requisition startImport(String requisitionName) {
        return remoteHandler.remoteInvoke(HttpMethod.PUT, location.toString() + "/" + requisitionName + "/import", Requisition.class, null, new ResponseCallback<Requisition>() {
            @Override
            public Requisition callback(Response response) throws RestClientException {
                return remoteHandler.remoteInvoke(HttpMethod.GET, response.getLocation().toString(), Requisition.class, null);
            }
        }, null);
    }
}
