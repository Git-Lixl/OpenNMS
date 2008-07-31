package org.opennms.web.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Category;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.EventConstants;
import org.opennms.netmgt.dao.IpInterfaceDao;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.netmgt.model.OnmsIpInterfaceList;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.utils.EventProxy;
import org.opennms.netmgt.utils.EventProxyException;
import org.opennms.netmgt.xml.event.Event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.api.core.ResourceContext;
import com.sun.jersey.spi.resource.PerRequest;

@Component
@PerRequest
@Scope("prototype")
public class OnmsIpInterfaceResource {

    @Autowired
    private NodeDao m_nodeDao;

    @Autowired
    private IpInterfaceDao m_ipInterfaceDao;

    @Autowired
    private EventProxy m_eventProxy;

    @Context
    ResourceContext m_context;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public OnmsIpInterfaceList getIpInterfaces(@PathParam("nodeId") int nodeId) {
        log().debug("getIpInterfaces: reading interfaces for node " + nodeId);
        OnmsNode node = m_nodeDao.get(nodeId);
        return new OnmsIpInterfaceList(node.getIpInterfaces());
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{ipAddress}")
    public OnmsIpInterface getIpInterface(@PathParam("nodeId") int nodeId, @PathParam("ipAddress") String ipAddress) {
        OnmsNode node = m_nodeDao.get(nodeId);
        return node.getIpInterfaceByIpAddress(ipAddress);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Transactional(readOnly=false)
    public Response addIpInterface(@PathParam("nodeId") int nodeId, OnmsIpInterface ipInterface) {
        OnmsNode node = m_nodeDao.get(nodeId);
        if (node == null)
            throwException(Status.BAD_REQUEST, "addIpInterface: can't find node with id " + nodeId);
        if (ipInterface == null)
            throwException(Status.BAD_REQUEST, "addIpInterface: ip interface object cannot be null");
        log().debug("addIpInterface: adding interface " + ipInterface);
        node.addIpInterface(ipInterface);
        m_ipInterfaceDao.save(ipInterface);
        Event e = new Event();
        e.setUei(EventConstants.NODE_GAINED_INTERFACE_EVENT_UEI);
        e.setNodeid(node.getId());
        e.setInterface(ipInterface.getIpAddress());
        e.setSource(getClass().getName());
        e.setTime(EventConstants.formatToString(new java.util.Date()));
        try {
            m_eventProxy.send(e);
        } catch (EventProxyException ex) {
            throwException(Status.BAD_REQUEST, ex.getMessage());
        }
        return Response.ok().build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Path("{ipAddress}")
    @Transactional(readOnly=false)
    public Response updateIpInterface(OnmsIpInterface ipInterface, @PathParam("ipAddress") String ipAddress) {
        if (ipInterface.getIpAddress().equals(ipAddress) == false)
            throwException(Status.CONFLICT, "updateIpInterface: invalid IP Address for ipInterface " + ipInterface);
        log().debug("updateIpInterface: updating ipInterface " + ipInterface);
        m_ipInterfaceDao.saveOrUpdate(ipInterface);
        return Response.ok().build();
    }

    @DELETE
    @Path("{ipAddress}")
    @Transactional(readOnly=false)
    public Response deleteIpInterface(@PathParam("nodeId") int nodeId, @PathParam("ipAddress") String ipAddress) {
        OnmsNode node = m_nodeDao.get(nodeId);
        if (node == null)
            throwException(Status.BAD_REQUEST, "deleteIpInterface: can't find node with id " + nodeId);
        OnmsIpInterface intf = node.getIpInterfaceByIpAddress(ipAddress);
        if (intf == null)
            throwException(Status.CONFLICT, "deleteIpInterface: can't find interface with ip address " + ipAddress + " for node with id " + nodeId);
        log().debug("deleteIpInterface: deleting interface " + ipAddress + " from node " + nodeId);
        node.getIpInterfaces().remove(intf);
        m_nodeDao.save(node);
        Event e = new Event();
        e.setUei(EventConstants.INTERFACE_DELETED_EVENT_UEI);
        e.setNodeid(nodeId);
        e.setInterface(ipAddress);
        e.setSource(getClass().getName());
        e.setTime(EventConstants.formatToString(new java.util.Date()));
        try {
            m_eventProxy.send(e);
        } catch (EventProxyException ex) {
            throwException(Status.BAD_REQUEST, ex.getMessage());
        }
        return Response.ok().build();
    }
    
    @Path("{ipAddress}/services")
    public OnmsMonitoredServiceResource getServices() {
        return m_context.getResource(OnmsMonitoredServiceResource.class);
    }

    private void throwException(Status status, String msg) {
        log().error(msg);
        throw new WebApplicationException(Response.status(status).tag(msg).build());
    }

    protected Category log() {
        return ThreadCategory.getInstance(getClass());
    }

}
