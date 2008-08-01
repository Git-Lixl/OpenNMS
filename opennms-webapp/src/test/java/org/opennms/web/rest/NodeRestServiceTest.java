package org.opennms.web.rest;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class NodeRestServiceTest extends AbstractSpringJerseyRestTestCase {
    
    
    public NodeRestServiceTest() {
        super();
        contextPath = "/opennms/rest";
    }

    public void testPostGetDelete() throws Exception {
        // POST
        MockHttpServletRequest request = createRequest(POST, "/nodes");
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
        request.setContentType("application/xml");
        request.setContent(node.getBytes());
        MockHttpServletResponse response = createResponse();        
        dispatch(request, response);
        assertEquals(200, response.getStatus());

        // GET
        request = createRequest(GET, "/nodes/1");
        response = createResponse();
        dispatch(request, response);
        assertEquals(200, response.getStatus());
        String xml = response.getContentAsString();
        System.err.println(xml);
        assertTrue(xml.contains("Darwin TestMachine 9.4.0 Darwin Kernel Version 9.4.0"));
        
        // DELETE
        request = createRequest(DELETE, "/nodes/1");
        response = createResponse();
        dispatch(request, response);
        assertEquals(200, response.getStatus());
        
        // GET
        request = createRequest(GET, "/nodes/1");
        response = createResponse();
        dispatch(request, response);
        assertEquals(204, response.getStatus());

    }

}
