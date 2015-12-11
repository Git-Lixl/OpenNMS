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

import org.opennms.netmgt.provision.persist.foreignsource.ForeignSource;
import org.opennms.web.rest.api.model.ApiVersion;
import org.opennms.web.rest.api.model.ResourceLocation;
import org.opennms.web.rest.client.RemoteHandler;
import org.opennms.web.rest.client.ResponseCallback;
import org.opennms.web.rest.client.exceptions.RestClientException;

public class ForeignSourceAPI extends AbstractAPI {

    private ResourceLocation location = new ResourceLocation(ApiVersion.Version1, "foreignSources");

    public ForeignSourceAPI(RemoteHandler remoteHandler) {
        super(remoteHandler);
    }

    public ForeignSource create(ForeignSource foreignSource) {
        return remoteHandler.remoteInvoke(HttpMethod.POST, location.toString(), ForeignSource.class, createEntity(foreignSource), new ResponseCallback<ForeignSource>() {
            @Override
            public ForeignSource callback(Response response) throws RestClientException {
                return remoteHandler.remoteInvoke(HttpMethod.GET,response.getLocation().toString(), ForeignSource.class, null);
            }
        }, null);
    }

    public void delete(String foreignSourceName) {
        remoteHandler.remoteInvoke(HttpMethod.DELETE, location.toString() + "/" + foreignSourceName, (Class) null, null);
    }
}
