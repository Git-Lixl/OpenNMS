package org.opennms.web.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsSnmpInterface;
import org.opennms.netmgt.model.OnmsSnmpInterfaceList;

public class OnmsSnmpInterfaceResource {

    private OnmsNode m_node;

    public OnmsSnmpInterfaceResource(OnmsNode node) {
        m_node = node;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public OnmsSnmpInterfaceList getSnmpInterfaces() {
        return new OnmsSnmpInterfaceList(m_node.getSnmpInterfaces());
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{ifIndex}")
    public OnmsSnmpInterface getSnmpInterface(@PathParam("ifIndex") int ifIndex) {
        return m_node.getSnmpInterfaceWithIfIndex(ifIndex);
    }
    
}
