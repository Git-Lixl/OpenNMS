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
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.dao.SnmpInterfaceDao;
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
    public void addSnmpInterface(@PathParam("nodeId") int nodeId, OnmsSnmpInterface snmpInterface) {
        log().debug("addSnmpInterface: adding interface " + snmpInterface);
        OnmsNode node = m_nodeDao.get(nodeId);
        node.addSnmpInterface(snmpInterface);
        m_snmpInterfaceDao.save(snmpInterface);
    }
    
    @DELETE
    @Transactional(readOnly=false)
    @Path("{ifIndex}")
    public void deleteSnmpInterface(@PathParam("nodeId") int nodeId, @PathParam("ifIndex") int ifIndex) {
        log().debug("deletSnmpInterface: deleting interface with ifIndex " + ifIndex + " from node " + nodeId);
        OnmsNode node = m_nodeDao.get(nodeId);
        OnmsSnmpInterface intf = node.getSnmpInterfaceWithIfIndex(ifIndex);
        m_snmpInterfaceDao.delete(intf);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Path("{ifIndex}")
    @Transactional(readOnly=false)
    public void updateSnmpInterface(OnmsSnmpInterface snmpInterface, @PathParam("ifIndex") int ifIndex) {
        if (snmpInterface.getIfIndex().equals(ifIndex)) {
            log().debug("updateSnmpInterface: updating snmpInterface with ifIndex " + ifIndex);
            m_snmpInterfaceDao.saveOrUpdate(snmpInterface);
        } else {
            log().warn("updateSnmpInterface: invalid ifIndex " + ifIndex + " for snmpInterface " + snmpInterface);
        }
    }

    protected Category log() {
        return ThreadCategory.getInstance(getClass());
    }

}
