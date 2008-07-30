package org.opennms.web.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.hibernate.criterion.Restrictions;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.model.OnmsCriteria;
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
    
    private static int LIMIT = 10;
    
    @Autowired
    private NodeDao m_nodeDao;
    
    @Context 
    UriInfo m_uriInfo;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public OnmsNodeList getNodes() {
        OnmsCriteria criteria = getQueryFilters();
        return new OnmsNodeList(m_nodeDao.findMatching(criteria));
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
    public void updateNode(OnmsNode node, @PathParam("nodeId") int nodeId) {
        if (nodeId == node.getId())
            m_nodeDao.saveOrUpdate(node);
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_XML)
    @Path("{nodeId}")
    public void deleteNode(@PathParam("nodeId") int nodeId) {
        OnmsNode node = m_nodeDao.get(nodeId);
        m_nodeDao.delete(node);
    }

    @Path("{nodeId}/ipinterfaces")
    public OnmsIpInterfaceResource getIpInterfaceResource(@PathParam("nodeId") int nodeId) {
        return new OnmsIpInterfaceResource(m_nodeDao.load(nodeId));
    }

    @Path("{nodeId}/snmpinterfaces")
    public OnmsSnmpInterfaceResource getSnmpInterfaceResource(@PathParam("nodeId") int nodeId) {
        return new OnmsSnmpInterfaceResource(m_nodeDao.load(nodeId));
    }
    
    private OnmsCriteria getQueryFilters() {
        MultivaluedMap<String,String> params = m_uriInfo.getQueryParameters();
        OnmsCriteria criteria = new OnmsCriteria(OnmsNode.class);

        int limit = LIMIT;
        if(params.containsKey("limit")) {
            limit = Integer.parseInt(params.getFirst("limit"));
            params.remove("limit");
        }
        criteria.setMaxResults(limit);

        if(params.containsKey("offset")) {
            criteria.setFirstResult(Integer.parseInt(params.getFirst("offset")));
            params.remove("offset");
        }

        for(String key: params.keySet()) {
            String thisValue = params.getFirst(key);
            criteria.add(Restrictions.eq(key, thisValue));
        }
        
        return criteria;
    }

}
