package org.opennms.netmgt.sampler.config.snmp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

public class SnmpMetricRepositoryTest {
	
	private SnmpMetricRepository m_repository;

	private static URL url(String path) throws MalformedURLException {
		return new URL("file:src/main/resources/" + path);
	}
	
	@Before
	public void setUp() throws Exception {
		m_repository = new SnmpMetricRepository(
				url("datacollection-config.xml"), 
				url("datacollection/mib2.xml"), 
				url("datacollection/netsnmp.xml"),
				url("datacollection/dell.xml")
				);
	}

	@Test
	public void testBogusAgent() throws Exception {
		
		// bogus agent... no collection should match
		SnmpAgent agent = new SnmpAgent(new InetSocketAddress("10.1.1.1", 161), ".666");
		
		SnmpCollectionRequest request = m_repository.createRequestForAgent(agent);
		assertNotNull(request);
		
		assertSame(agent, request.getAgent());
		
		// gets the mib2 data since that's configured for everything
		assertEquals(2, request.getResourceTypes().size());
		assertEquals(2, request.getTables().size());
		assertEquals(2, request.getGroups().size());
		
	}
	
	@Test
	public void testBrokenNetSNMPAgent() throws Exception {
		
		SnmpAgent agent = new SnmpAgent(new InetSocketAddress("10.1.1.1", 161), ".0.1");

		SnmpCollectionRequest request = m_repository.createRequestForAgent(agent);
		assertNotNull(request);
		
		assertSame(agent, request.getAgent());
		
		System.err.println(request);
		
		assertEquals(9, request.getResourceTypes().size());
		assertEquals(14, request.getTables().size());
		assertEquals(11, request.getGroups().size());
}

}
