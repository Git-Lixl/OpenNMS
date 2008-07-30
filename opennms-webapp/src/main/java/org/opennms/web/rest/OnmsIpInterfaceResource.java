package org.opennms.web.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Category;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.dao.IpInterfaceDao;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.netmgt.model.OnmsIpInterfaceList;
import org.opennms.netmgt.model.OnmsNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.spi.resource.PerRequest;

@Component
@PerRequest
@Scope("prototype")
public class OnmsIpInterfaceResource {

    @Autowired
    private NodeDao m_nodeDao;

    @Autowired
    private IpInterfaceDao m_ipInterfaceDao;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public OnmsIpInterfaceList getIpInterfaces(@PathParam("nodeId") int nodeId) {
        log().debug("getIpInterfaces: reading interfaces for node " + nodeId);
        OnmsNode node = m_nodeDao.get(nodeId);
        return new OnmsIpInterfaceList(node.getIpInterfaces());
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{ipAddress}")
    public OnmsIpInterface getIpInterface(@PathParam("nodeId") int nodeId, @PathParam("ipAddress") String ipAddress) {
        OnmsNode node = m_nodeDao.get(nodeId);
        return node.getIpInterfaceByIpAddress(ipAddress);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Transactional(readOnly=false)
    public void addIpInterface(@PathParam("nodeId") int nodeId, OnmsIpInterface ipInterface) {
        log().debug("addIpInterface: adding interface " + ipInterface);
        OnmsNode node = m_nodeDao.get(nodeId);
        node.addIpInterface(ipInterface);
        m_ipInterfaceDao.save(ipInterface);
        //m_nodeDao.saveOrUpdate(node);
    }
    
    @DELETE
    @Transactional(readOnly=false)
    public void deleteIpInterface(@PathParam("nodeId") int nodeId, @PathParam("ipAddress") String ipAddress) {
        log().debug("deleteIpInterface: de;eting interface " + ipAddress + " from node " + nodeId);
        OnmsNode node = m_nodeDao.get(nodeId);
        OnmsIpInterface intf = node.getIpInterfaceByIpAddress(ipAddress);
        m_ipInterfaceDao.delete(intf);
    }
    
    @Path("{ipAddress}/services")
    public OnmsMonitoredServiceResource getServices(@PathParam("nodeId") int nodeId, @PathParam("ipAddress") String ipAddress) {
        OnmsNode node = m_nodeDao.get(nodeId);
        return new OnmsMonitoredServiceResource(node, ipAddress);
    }

    protected Category log() {
        return ThreadCategory.getInstance(getClass());
    }

}
