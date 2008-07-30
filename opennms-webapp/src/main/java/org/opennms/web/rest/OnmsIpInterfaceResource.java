package org.opennms.web.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.netmgt.model.OnmsIpInterfaceList;
import org.opennms.netmgt.model.OnmsNode;

public class OnmsIpInterfaceResource {

    private OnmsNode m_node;
    
    public OnmsIpInterfaceResource(OnmsNode node) {
        m_node = node;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public OnmsIpInterfaceList getIpInterfaces(@PathParam("nodeId") String nodeId) {
        return new OnmsIpInterfaceList(m_node.getIpInterfaces());
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{ipAddress}")
    public OnmsIpInterface getIpInterface(@PathParam("ipAddress") String ipAddress) {
        return m_node.getIpInterfaceByIpAddress(ipAddress);
    }

    @Path("{ipAddress}/services")
    public OnmsMonitoredServiceResource getServices(@PathParam("ipAddress") String ipAddress) {
        return new OnmsMonitoredServiceResource(m_node, ipAddress);
    }

}
