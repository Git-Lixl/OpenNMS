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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Category;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.dao.SnmpInterfaceDao;
import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsSnmpInterface;
import org.opennms.netmgt.model.OnmsSnmpInterfaceList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.spi.resource.PerRequest;

@Component
@PerRequest
@Scope("prototype")
public class OnmsSnmpInterfaceResource {

    @Autowired
    private NodeDao m_nodeDao;
    
    @Autowired
    private SnmpInterfaceDao m_snmpInterfaceDao;
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public OnmsSnmpInterfaceList getSnmpInterfaces(@PathParam("nodeId") int nodeId) {
        OnmsNode node = m_nodeDao.get(nodeId);
        return new OnmsSnmpInterfaceList(node.getSnmpInterfaces());
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{ifIndex}")
    public OnmsSnmpInterface getSnmpInterface(@PathParam("nodeId") int nodeId, @PathParam("ifIndex") int ifIndex) {
        OnmsNode node = m_nodeDao.get(nodeId);
        return node.getSnmpInterfaceWithIfIndex(ifIndex);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Transactional(readOnly=false)
    public Response addSnmpInterface(@PathParam("nodeId") int nodeId, OnmsSnmpInterface snmpInterface) {
        OnmsNode node = m_nodeDao.get(nodeId);
        if (node == null)
            throwException(Status.BAD_REQUEST, "addSnmpInterface: can't find node with id " + nodeId);
        if (snmpInterface == null)
            throwException(Status.BAD_REQUEST, "addSnmpInterface: snmp interface object cannot be null");
        log().debug("addSnmpInterface: adding interface " + snmpInterface);
        node.addSnmpInterface(snmpInterface);
        if (snmpInterface.getIpAddress() != null) {
            OnmsIpInterface iface = node.getIpInterfaceByIpAddress(snmpInterface.getIpAddress());
            iface.setSnmpInterface(snmpInterface);
            // TODO Add important events here
        }
        m_snmpInterfaceDao.save(snmpInterface);
        return Response.ok().build();
    }
    
    @DELETE
    @Transactional(readOnly=false)
    @Path("{ifIndex}")
    public Response deleteSnmpInterface(@PathParam("nodeId") int nodeId, @PathParam("ifIndex") int ifIndex) {
        OnmsNode node = m_nodeDao.get(nodeId);
        if (node == null)
            throwException(Status.BAD_REQUEST, "deleteSnmpInterface: can't find node with id " + nodeId);
        OnmsSnmpInterface intf = node.getSnmpInterfaceWithIfIndex(ifIndex);
        if (intf == null)
            throwException(Status.BAD_REQUEST, "deleteSnmpInterface: can't find snmp interface with ifIndex " + ifIndex + " for node with id " + nodeId);
        log().debug("deletSnmpInterface: deleting interface with ifIndex " + ifIndex + " from node " + nodeId);
        node.getSnmpInterfaces().remove(intf);
        m_nodeDao.saveOrUpdate(node);
        // TODO Add important events here
        return Response.ok().build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Path("{ifIndex}")
    @Transactional(readOnly=false)
    public Response updateSnmpInterface(OnmsSnmpInterface snmpInterface, @PathParam("ifIndex") int ifIndex) {
        if (snmpInterface.getIfIndex().equals(ifIndex) == false)
            throwException(Status.CONFLICT, "updateSnmpInterface: invalid ifIndex " + ifIndex + " for snmpInterface " + snmpInterface);
        log().debug("updateSnmpInterface: updating snmpInterface with ifIndex " + ifIndex);
        m_snmpInterfaceDao.saveOrUpdate(snmpInterface);
        return Response.ok().build();
    }

    private void throwException(Status status, String msg) {
        log().error(msg);
        throw new WebApplicationException(Response.status(status).tag(msg).build());
    }

    protected Category log() {
        return ThreadCategory.getInstance(getClass());
    }

}
