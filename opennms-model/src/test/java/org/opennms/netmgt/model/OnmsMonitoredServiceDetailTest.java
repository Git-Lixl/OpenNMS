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

package org.opennms.netmgt.model;

import java.io.IOException;
import java.net.InetAddress;

import org.junit.Assert;
import org.junit.Test;
import org.opennms.core.test.xml.JsonTest;
import org.opennms.core.test.xml.XmlTest;

public class OnmsMonitoredServiceDetailTest {

    @Test
    public void testMarshalAndUnmarshalJson() throws IOException {
        OnmsMonitoredServiceDetail detail = createServiceDetail();
        String jsonString = JsonTest.marshalToJson(detail);
        JsonTest.assertJsonEquals("{\n" +
                "  \"id\" : \"1\",\n" +
                "  \"status\" : \"some status\",\n" +
                "  \"statusCode\" : \"1234\",\n" +
                "  \"nodeLabel\" : \"some label\",\n" +
                "  \"serviceName\" : \"some service\",\n" +
                "  \"ipAddress\" : \"localhost\",\n" +
                "  \"isMonitored\" : true,\n" +
                "  \"isDown\" : true\n" +
                "}", jsonString);

        OnmsMonitoredServiceDetail unmarshalled = JsonTest.unmarshalFromJson(jsonString, OnmsMonitoredServiceDetail.class);
        Assert.assertEquals(detail, unmarshalled);
    }

    @Test
    public void testMarshalAndUnmarshalXml() {
        final OnmsMonitoredServiceDetail detail = createServiceDetail();
        final String xmlString = XmlTest.marshalToXmlWithJaxb(detail);
        XmlTest.assertXmlEquals("<monitored-service isDown=\"true\" id=\"1\" isMonitored=\"true\" status=\"1234\">\n" +
                "    <ipAddress>127.0.0.1</ipAddress>\n" +
                "    <node>some label</node>\n" +
                "    <serviceName>some service</serviceName>\n" +
                "    <statusDescription>some status</statusDescription>\n" +
                "</monitored-service>", xmlString);

        OnmsMonitoredServiceDetail unmarshalled = XmlTest.unmarshalFromXmlWithJaxb(xmlString, OnmsMonitoredServiceDetail.class);
        Assert.assertEquals(detail, unmarshalled);
    }

    private OnmsMonitoredServiceDetail createServiceDetail() {
        OnmsMonitoredServiceDetail serviceDetail = new OnmsMonitoredServiceDetail();
        serviceDetail.setIpAddress(InetAddress.getLoopbackAddress());
        serviceDetail.setId("1");
        serviceDetail.setNodeLabel("some label");
        serviceDetail.setServiceName("some service");
        serviceDetail.setStatus("some status");
        serviceDetail.setStatusCode("1234");
        serviceDetail.setMonitored(true);
        serviceDetail.setDown(true);
        return serviceDetail;
    }
}
