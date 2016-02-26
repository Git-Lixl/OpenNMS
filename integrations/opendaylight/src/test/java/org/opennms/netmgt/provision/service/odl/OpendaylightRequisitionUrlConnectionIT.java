/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.provision.service.odl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.opennms.core.utils.url.GenericURLFactory;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class OpendaylightRequisitionUrlConnectionIT {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig()
            .withRootDirectory(Paths.get("src", "test", "resources").toString())
            .dynamicPort());

    @Before
    public void setUp() {
        GenericURLFactory.initialize();
    }

    @Test
    public void canGenerateRequisition() throws Exception {
        stubFor(get(urlEqualTo("/restconf/operational/network-topology:network-topology/"))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "Content-Type: application/yang.data+json; charset=utf-8")
                    .withBodyFile("operational-network-topology.json")));

        // Generate the requisition
        Resource resource = new UrlResource(String.format("odl://localhost:%d/fs", wireMockRule.port()));
        Requisition requisition = JaxbUtils.unmarshal(Requisition.class, resource);

        // Verify
        assertNotNull(requisition);
        // Top level attributes
        assertEquals("fs", requisition.getForeignSource());
        // Node details
        List<RequisitionNode> nodes = requisition.getNodes();
        assertEquals(15, nodes.size());
        Set<String> foreignIds = nodes.stream()
                .map(n -> n.getForeignId())
                .collect(Collectors.toSet());
        assertEquals(15, foreignIds.size());
        String expectedFid = "openflow_5";
        assertTrue("No node with foreign id " + expectedFid + " was present in the requisition.", 
                foreignIds.contains(expectedFid));
    }
}
