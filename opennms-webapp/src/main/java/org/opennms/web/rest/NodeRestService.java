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

import org.apache.log4j.Category;
import org.hibernate.criterion.Restrictions;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.model.OnmsCriteria;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsNodeList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.api.core.ResourceContext;
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
    
    @Context
    ResourceContext m_context;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public OnmsNodeList getNodes() {
        OnmsCriteria criteria = getQueryFilters();
        return new OnmsNodeList(m_nodeDao.findMatching(criteria));
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{nodeId}")
    public OnmsNode getNode(@PathParam("nodeId") int nodeId) {
        return m_nodeDao.get(nodeId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Transactional(readOnly=false)
    public void addNode(OnmsNode node) {
        log().debug("addNode: Adding node " + node);
        if (node != null)
            m_nodeDao.save(node);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Path("{nodeId}")
    @Transactional(readOnly=false)
    public void updateNode(OnmsNode node, @PathParam("nodeId") int nodeId) {
        if (nodeId == node.getId()) {
            log().debug("updateNode: updating node " + nodeId);
            m_nodeDao.saveOrUpdate(node);
        } else {
            log().warn("updateNode: invalid nodeId for node " + node);
        }
    }
    
    @DELETE
    @Path("{nodeId}")
    @Transactional(readOnly=false)
    public void deleteNode(@PathParam("nodeId") int nodeId) {
        log().debug("deleteNode: deleting node " + nodeId);
        OnmsNode node = m_nodeDao.get(nodeId);
        m_nodeDao.delete(node);
    }

    @Path("{nodeId}/ipinterfaces")
    public OnmsIpInterfaceResource getIpInterfaceResource() {
        return m_context.getResource(OnmsIpInterfaceResource.class);
    }

    @Path("{nodeId}/snmpinterfaces")
    public OnmsSnmpInterfaceResource getSnmpInterfaceResource() {
        return m_context.getResource(OnmsSnmpInterfaceResource.class);
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
    
    protected Category log() {
        return ThreadCategory.getInstance(getClass());
    }

}
