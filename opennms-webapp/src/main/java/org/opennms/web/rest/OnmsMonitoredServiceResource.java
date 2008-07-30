package org.opennms.web.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.opennms.netmgt.model.OnmsMonitoredService;
import org.opennms.netmgt.model.OnmsNode;

public class OnmsMonitoredServiceResource {
    
    private OnmsNode m_node;
    private String m_ipAddress;

    public OnmsMonitoredServiceResource(OnmsNode node, String ipAddress) {
        m_node = node;
        m_ipAddress = ipAddress;
    }
    
    @GET
    @Produces("text/xml")
    public OnmsMonitoredServiceList getServices() {
        return new OnmsMonitoredServiceList(m_node.getIpInterfaceByIpAddress(m_ipAddress).getMonitoredServices());
    }

    @GET
    @Produces("text/xml")
    @Path("{service}")
    public OnmsMonitoredService getService(@PathParam("service") String service) {
        return m_node.getIpInterfaceByIpAddress(m_ipAddress).getMonitoredServiceByServiceType(service);
    }
    
}
