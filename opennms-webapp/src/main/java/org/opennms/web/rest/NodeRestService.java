package org.opennms.web.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsNodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.sun.jersey.spi.resource.PerRequest;

@Component
@PerRequest
@Scope("prototype")
@Path("nodes")
public class NodeRestService {
    
    @Autowired
    private NodeDao m_nodeDao;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public OnmsNodeList getNodes() {
        return new OnmsNodeList(m_nodeDao.findAll());
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public void addNode(OnmsNode node) {
        m_nodeDao.save(node);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{nodeId}")
    public OnmsNode getNode(@PathParam("nodeId") int nodeId) {
        return m_nodeDao.get(nodeId);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Path("{nodeId}")
    public void updateNode(OnmsNode node) {
        m_nodeDao.saveOrUpdate(node);
    }

    @Path("{nodeId}/ipinterfaces")
    public OnmsIpInterfaceResource getIpInterfaceResource(@PathParam("nodeId") int nodeId) {
        return new OnmsIpInterfaceResource(m_nodeDao.load(nodeId));
    }

    @Path("{nodeId}/snmpinterfaces")
    public OnmsSnmpInterfaceResource getSnmpInterfaceResource(@PathParam("nodeId") int nodeId) {
        return new OnmsSnmpInterfaceResource(m_nodeDao.load(nodeId));
    }

}
