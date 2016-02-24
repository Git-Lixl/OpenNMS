package org.opennms.integrations.odl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NamingUtilsTest {
    @Test
    public void canParseForeignId() {
        assertEquals("flow:1", NamingUtils.getTopologyIdFromForeignId("flow_1-openflow_1"));
        assertEquals("openflow:1", NamingUtils.getNodeIdFromForeignId("flow_1-openflow_1"));

        assertEquals("flow:1", NamingUtils.getTopologyIdFromForeignId("flow_1-openflow_6"));
        assertEquals("openflow:6", NamingUtils.getNodeIdFromForeignId("flow_1-openflow_6"));

        assertEquals("flow:2", NamingUtils.getTopologyIdFromForeignId("flow_2-host_00_00_00_00_00_06"));
        assertEquals("host:00:00:00:00:00:06", NamingUtils.getNodeIdFromForeignId("flow_2-host_00_00_00_00_00_06"));
    }

    @Test
    public void canGenerateIpAddressForForeignId() {
        assertEquals("127.0.10.1", NamingUtils.generateIpAddressForForeignId("flow_1-openflow_1"));
        assertEquals("127.0.10.6", NamingUtils.generateIpAddressForForeignId("flow_1-openflow_6"));
        assertEquals("127.0.20.1", NamingUtils.generateIpAddressForForeignId("flow_1-host_00_00_00_00_00_01"));
        assertEquals("127.0.20.6", NamingUtils.generateIpAddressForForeignId("flow_1-host_00_00_00_00_00_06"));
    }
}
