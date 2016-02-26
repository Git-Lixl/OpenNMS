package org.opennms.integrations.odl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NamingUtilsTest {
    @Test
    public void canParseForeignId() {
        assertEquals("openflow:1", NamingUtils.getNodeIdFromForeignId("openflow_1"));
        assertEquals("openflow:6", NamingUtils.getNodeIdFromForeignId("openflow_6"));
        assertEquals("host:00:00:00:00:00:06", NamingUtils.getNodeIdFromForeignId("host_00_00_00_00_00_06"));
    }

    @Test
    public void canGenerateIpAddressForForeignId() {
        assertEquals("127.0.10.1", NamingUtils.generateIpAddressForForeignId("openflow_1"));
        assertEquals("127.0.10.6", NamingUtils.generateIpAddressForForeignId("openflow_6"));
        assertEquals("127.0.20.1", NamingUtils.generateIpAddressForForeignId("host_00_00_00_00_00_01"));
        assertEquals("127.0.20.6", NamingUtils.generateIpAddressForForeignId("host_00_00_00_00_00_06"));
    }
}
