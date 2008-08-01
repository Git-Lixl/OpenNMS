package org.opennms.web.rest;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/*
 * TODO
 * 1. Need to figure it out how to create a Mock for EventProxy to validate events sent by RESTful service
 * 2. Test object change (PUT)
 */
public class NodeRestServiceTest extends AbstractSpringJerseyRestTestCase {
    
    public NodeRestServiceTest() {
        super();
        contextPath = "/opennms/rest";
    }

    public void testNodePostGetDelete() throws Exception {
        createNode();
        String url = "/nodes/1";
        sendRequest(GET, url, 200, "Darwin TestMachine 9.4.0 Darwin Kernel Version 9.4.0");
        sendRequest(DELETE, url, 200, null);
        sendRequest(GET, url, 204, null);
    }

    public void testIpInterfacePostGetDelete() throws Exception {
        createIpInterface();
        String url = "/nodes/1/ipinterfaces/10.10.10.10";
        sendRequest(GET, url, 200, null);
        sendRequest(DELETE, url, 200, null);
        sendRequest(GET, url, 204, null);
    }

    public void testSnmpInterfacePostGetDelete() throws Exception {
        createSnmpInterface();
        String url = "/nodes/1/snmpinterfaces/6";
        sendRequest(GET, url, 200, null);
        sendRequest(DELETE, url, 200, null);
        sendRequest(GET, url, 204, null);
    }

    public void testMonitoredServicePostGetDelete() throws Exception {
        createService();
        String url = "/nodes/1/ipinterfaces/10.10.10.10/services/ICMP";
        sendRequest(GET, url, 200, null);
        sendRequest(DELETE, url, 200, null);
        sendRequest(GET, url, 204, null);
    }
    
    private void sendPost(String url, String xml) throws Exception {
        MockHttpServletRequest request = createRequest(POST, url);
        request.setContentType("application/xml");
        request.setContent(xml.getBytes());
        MockHttpServletResponse response = createResponse();        
        dispatch(request, response);
        assertEquals(200, response.getStatus());
    }
    
    private void sendRequest(String requestType, String url, int spectedStatus, String spectedOutput) throws Exception {
        MockHttpServletRequest request = createRequest(requestType, url);
        MockHttpServletResponse response = createResponse();
        dispatch(request, response);
        assertEquals(spectedStatus, response.getStatus());
        if (spectedOutput != null) {
            String xml = response.getContentAsString();
            System.err.println(xml);
            assertTrue(xml.contains(spectedOutput));
        }
    }
    
    private void createNode() throws Exception {
        String node = "<node>" +            
        "<label>TestMachine</label>" +
        "<labelSource>H</labelSource>" +
        "<sysContact>The Owner</sysContact>" +
        "<sysDescription>" +
        "Darwin TestMachine 9.4.0 Darwin Kernel Version 9.4.0: Mon Jun  9 19:30:53 PDT 2008; root:xnu-1228.5.20~1/RELEASE_I386 i386" +
        "</sysDescription>" +
        "<sysLocation>DevJam</sysLocation>" +
        "<sysName>TestMachine</sysName>" +
        "<sysObjectId>.1.3.6.1.4.1.8072.3.2.255</sysObjectId>" +
        "<type>A</type>" +
        "</node>";
        sendPost("/nodes", node);
    }
    
    private void createIpInterface() throws Exception {
        createNode();
        String ipInterface = "<ipInterface>" +
        "<ipAddress>10.10.10.10</ipAddress>" +
        "<ipHostName>TestMachine</ipHostName>" +
        "<ipStatus>1</ipStatus>" +
        "<isManaged>M</isManaged>" +
        "<isSnmpPrimary>" +
        "<charCode>80</charCode>" +
        "</isSnmpPrimary>" +
        "</ipInterface>";
        sendPost("/nodes/1/ipinterfaces", ipInterface);
    }

    private void createSnmpInterface() throws Exception {
        createIpInterface();
        String snmpInterface = "<snmpInterface>" +
        "<ifAdminStatus>1</ifAdminStatus>" +
        "<ifDescr>en1</ifDescr>" +
        "<ifIndex>6</ifIndex>" +
        "<ifName>en1</ifName>" +
        "<ifOperStatus>1</ifOperStatus>" +
        "<ifSpeed>10000000</ifSpeed>" +
        "<ifType>6</ifType>" +
        "<ipAddress>10.10.10.10</ipAddress>" +
        "<netMask>255.255.255.0</netMask>" +
        "<physAddr>001e5271136d</physAddr>" +
        "</snmpInterface>";
        sendPost("/nodes/1/snmpinterfaces", snmpInterface);
    }
    
    private void createService() throws Exception {
        createIpInterface();
        String service = "<service>" +
        "<notify>Y</notify>" +
        "<serviceType>" +
        "<name>ICMP</name>" +
        "</serviceType>" +
        "<source>P</source>" +
        "<status>N</status>" +
        "</service>";
        sendPost("/nodes/1/ipinterfaces/10.10.10.10/services", service);
    }

}
