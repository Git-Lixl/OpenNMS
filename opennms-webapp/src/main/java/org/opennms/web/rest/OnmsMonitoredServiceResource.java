package org.opennms.web.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Category;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.dao.MonitoredServiceDao;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.model.OnmsMonitoredService;
import org.opennms.netmgt.model.OnmsMonitoredServiceList;
import org.opennms.netmgt.model.OnmsNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.spi.resource.PerRequest;

@Component
@PerRequest
@Scope("prototype")
public class OnmsMonitoredServiceResource {
    
    @Autowired
    private NodeDao m_nodeDao;
    
    @Autowired
    private MonitoredServiceDao m_serviceDao;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public OnmsMonitoredServiceList getServices(@PathParam("nodeId") int nodeId, @PathParam("ipAddress") String ipAddress) {
        OnmsNode node = m_nodeDao.get(nodeId);
        return new OnmsMonitoredServiceList(node.getIpInterfaceByIpAddress(ipAddress).getMonitoredServices());
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{service}")
    public OnmsMonitoredService getService(@PathParam("nodeId") int nodeId, @PathParam("ipAddress") String ipAddress, @PathParam("service") String service) {
        OnmsNode node = m_nodeDao.get(nodeId);
        return node.getIpInterfaceByIpAddress(ipAddress).getMonitoredServiceByServiceType(service);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Transactional(readOnly=false)
    public void addService(@PathParam("nodeId") int nodeId, @PathParam("ipAddress") String ipAddress, OnmsMonitoredService service) {
        log().debug("addService: adding service " + service);
        OnmsNode node = m_nodeDao.get(nodeId);
        node.getIpInterfaceByIpAddress(ipAddress).getMonitoredServices().add(service);
        m_serviceDao.save(service);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Path("{service}")
    @Transactional(readOnly=false)
    public void updateService(OnmsMonitoredService service, @PathParam("nodeId") int nodeId, @PathParam("ipAddress") String ipAddress, @PathParam("service") String serviceName) {
        if (service.getServiceName().equals(serviceName)) {
            log().debug("updateService: updating service " + service);
            m_serviceDao.saveOrUpdate(service);
        } else {
            log().warn("updateService: invalid service name for " + service);
        }
    }

    @DELETE
    @Path("{service}")
    @Transactional(readOnly=false)
    public void deleteService(@PathParam("nodeId") int nodeId, @PathParam("ipAddress") String ipAddress, @PathParam("service") String serviceName) {
        log().debug("deleteService: deleting service " + serviceName + " from node " + nodeId);
        OnmsNode node = m_nodeDao.get(nodeId);
        OnmsMonitoredService service = node.getIpInterfaceByIpAddress(ipAddress).getMonitoredServiceByServiceType(serviceName);
        m_serviceDao.delete(service);
    }
    
    protected Category log() {
        return ThreadCategory.getInstance(getClass());
    }

}
