package org.opennms.sms.monitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.netmgt.model.PollStatus;
import org.opennms.netmgt.poller.IPv4NetworkInterface;
import org.opennms.netmgt.poller.MonitoredService;
import org.opennms.netmgt.poller.NetworkInterface;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath*:/META-INF/spring/bundle-context.xml",
        "classpath*:/META-INF/opennms/bundle-context-opennms.xml",
        "classpath:/testContext.xml"
})
public class MobileMsgSequenceMonitorTest {

	MonitoredService m_service;
	private MobileMsgSequenceMonitor m_monitor;
	
	@Before
	public void setUp() {
		m_service = new MonitoredService() {
			public InetAddress getAddress() {
				try {
					return InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
					return null;
				}
			}

			public String getIpAddr() {
				return "127.0.0.1";
			}

			public NetworkInterface getNetInterface() {
				return new IPv4NetworkInterface(getAddress());
			}

			public int getNodeId() {
				return 1;
			}

			public String getNodeLabel() {
				return "localhost";
			}

			public String getSvcName() {
				return "SMS";
			}
		};
		
		m_monitor = createAndInitializeMonitor();
	}
	
	@Test
	@DirtiesContext
	public void testBrokenConfiguration() throws Exception {
		
		String mobileConfig = "<mobile-sequence xmlns=\"http://xmlns.opennms.org/xsd/config/mobile-sequence\"><octagon sides=\"8\" /></mobile-sequence>";
		Map<String, Object> parameters = createConfigParameters(mobileConfig);

		PollStatus s = m_monitor.poll(m_service, parameters);
		assertEquals("monitor should fail", PollStatus.SERVICE_UNAVAILABLE, s.getStatusCode());
	}

	@Test
	@DirtiesContext
	public void testInlineSequence() throws Exception {
		
		Map<String, Object> parameters = createConfigParameters("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<mobile-sequence xmlns=\"http://xmlns.opennms.org/xsd/config/mobile-sequence\">\n" +
				"<transaction label=\"sms-ping\">\n" +
				"<sms-request recipient=\"${recipient}\" text=\"You suck!\"/>\n" +
				"<sms-response>\n" +
				"<from-recipient/>\n" +
				"<matches>^[Nn]o$</matches>\n" +
				"</sms-response>\n" +
				"</transaction>\n" +
				"</mobile-sequence>");
		
		assertPollAvailable(parameters);
	}

	@Test
	@DirtiesContext
	public void testParseConfiguration() throws Exception {

		Map<String, Object> parameters = createConfigParameters("<mobile-sequence xmlns=\"http://xmlns.opennms.org/xsd/config/mobile-sequence\" />");

		PollStatus s = assertPollUnavailable(parameters);
		assertEquals("No transactions were configured for host 127.0.0.1", s.getReason());
	}

	@Test
	@DirtiesContext
	public void testSimpleSequence() throws Exception {
		
		Map<String, Object> parameters = createConfigParameters(getXmlBuffer("sms-ping-sequence.xml"));

		PollStatus s = assertPollAvailable(parameters);
		assertTrue(s.getProperty("sms-ping").longValue() > 10);
	}

	@Test
	@DirtiesContext
	public void testUssdSequence() throws Exception {
		
		Map<String, Object> parameters = createConfigParameters(getXmlBuffer("tmobile-balance-sequence.xml"));
		
		assertPollAvailable(parameters);
	}

	private PollStatus assertPollAvailable(Map<String, Object> parameters) {
		return assertPollStatus(parameters, PollStatus.SERVICE_AVAILABLE);
	}

	private PollStatus assertPollStatus(Map<String, Object> parameters, int expectedStatus) {
		PollStatus s = m_monitor.poll(m_service, parameters);
		assertEquals("unaccepted poll status", expectedStatus, s.getStatusCode());
		return s;
	}

	private PollStatus assertPollUnavailable(Map<String, Object> parameters) {
		return assertPollStatus(parameters, PollStatus.SERVICE_UNAVAILABLE);
	}

	private MobileMsgSequenceMonitor createAndInitializeMonitor() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(MobileMsgSequenceMonitor.CONTEXT_KEY, "testMobileMessagePollerContext");
		
		MobileMsgSequenceMonitor m = new MobileMsgSequenceMonitor();
		m.initialize(params);
		return m;
	}
	
	private Map<String, Object> createConfigParameters(String mobileConfig) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("retry", "0");
		parameters.put("timeout", "3000");
		parameters.put("sequence", mobileConfig);
		return parameters;
	}
	
    private String getXmlBuffer(String fileName) throws IOException {
        StringBuffer xmlBuffer = new StringBuffer();
        File xmlFile = new File(ClassLoader.getSystemResource(fileName).getFile());
        assertTrue("xml file is readable", xmlFile.canRead());

        BufferedReader reader = new BufferedReader(new FileReader(xmlFile));
        String line;
        while (true) {
            line = reader.readLine();
            if (line == null) {
                reader.close();
                break;
            }
            xmlBuffer.append(line).append("\n");
        }
        return xmlBuffer.toString();
    }

}
