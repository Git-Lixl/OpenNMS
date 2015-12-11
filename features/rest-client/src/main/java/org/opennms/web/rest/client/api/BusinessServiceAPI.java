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
 * MERCHANTABILITY or FITNESS FOR RemoteHandler PARTICULAR PURPOSE.  See the
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

import org.opennms.netmgt.bsm.service.model.BusinessServiceDTO;
import org.opennms.web.rest.api.model.ApiVersion;
import org.opennms.web.rest.api.model.ResourceLocation;
import org.opennms.web.rest.client.RemoteHandler;
import org.opennms.web.rest.client.ResponseCallback;
import org.opennms.web.rest.v2.bsm.model.BusinessServiceListDTO;

public class BusinessServiceAPI extends AbstractAPI {

    private ResourceLocation location = new ResourceLocation(ApiVersion.Version2, "business-services");

    public BusinessServiceAPI(RemoteHandler remoteHandler) {
        super(remoteHandler);
    }

    public BusinessServiceDTO create(BusinessServiceDTO businessService) {
        return remoteHandler.remoteInvoke(
                HttpMethod.POST,
                location.toString(),
                BusinessServiceDTO.class,
                createEntity(businessService),
                new ResponseCallback<BusinessServiceDTO>() {
                    @Override
                    public BusinessServiceDTO callback(Response response) {
                        return remoteHandler.remoteInvoke(HttpMethod.GET, response.getLocation().toString(), BusinessServiceDTO.class, null);
                    }
                },
                null);
    }

    public BusinessServiceListDTO list() {
        return remoteHandler.remoteInvoke(HttpMethod.GET, location.toString(), BusinessServiceListDTO.class, null);
    }

    public BusinessServiceDTO get(String id) {
        return remoteHandler.remoteInvoke(HttpMethod.GET, location.toString() + "/" + id, BusinessServiceDTO.class, null);
    }

    public void delete(BusinessServiceDTO businessService) {
        delete(businessService.getId());
    }

    public void delete(Long id) {
        remoteHandler.remoteInvoke(HttpMethod.DELETE, location.toString() + "/" + id, (Class) null, null);
    }

    public void attachIpService(String businessServiceId, String ipServiceId) {
        remoteHandler.remoteInvoke(HttpMethod.POST, location.toString() + "/" + businessServiceId + "/ip-service/" + ipServiceId, (Class) null, null);
    }

    public void detachIpService(String businessServiceId, String ipServiceId) {
        remoteHandler.remoteInvoke(HttpMethod.DELETE, location.toString() + "/" + businessServiceId + "/ip-service/" + ipServiceId, (Class) null, null);
    }
}
