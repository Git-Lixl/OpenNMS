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

package org.opennms.netmgt.bsm.service.model;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.opennms.core.test.xml.JsonTest;
import org.opennms.web.rest.api.model.ApiVersion;
import org.opennms.web.rest.api.model.ResourceLocation;

public class BusinessServiceDTOJsonTest {

    @Test
    public void testSerializeAndDeserialize() throws IOException {
        // Object to serialize
        BusinessServiceDTO bs = new BusinessServiceDTO();
        bs.setId(1L);
        bs.setName("Web Servers");
        bs.setAttribute("dc", "RDU");
        bs.setLocation(new ResourceLocation(ApiVersion.Version2, "business-services", "1"));

        IpServiceDTO ipService = new IpServiceDTO();
        ipService.setId("1");
        ipService.setLocation(new ResourceLocation(ApiVersion.Version1, "ifservices", "1"));
        bs.addIpService(ipService);

        // serialize object
        String json = JsonTest.marshalToJson(bs);
        String expectedJson = JsonTest.read(getClass().getResourceAsStream("/expected-business-service.json"));
        JsonTest.assertJsonEquals(json, expectedJson);

        // seserialize object
        BusinessServiceDTO deserializedObject = JsonTest.unmarshalFromJson(json, BusinessServiceDTO.class);
        Assert.assertEquals(bs, deserializedObject);
    }
}
