package org.opennms.web.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.opennms.netmgt.dao.EventDao;
import org.opennms.netmgt.model.OnmsCriteria;
import org.opennms.netmgt.model.OnmsEvent;
import org.opennms.netmgt.model.OnmsEventCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.spi.resource.PerRequest;

@Component
@PerRequest
@Scope("prototype")
@Path("events")
public class EventRestService {
    @Autowired
    private EventDao m_eventDao;
    
    @Context 
    UriInfo m_uriInfo;

    
    @GET
    @Produces("text/xml")
    @Path("{eventId}")
    @Transactional
    public OnmsEvent getEvent(@PathParam("eventId") String eventId) {
        return m_eventDao.get(new Integer(eventId));
    }
    
    @GET
    @Produces("text/plain")
    @Path("count")
    @Transactional
    public String getCount() {
    	return Integer.toString(m_eventDao.countAll());
    }

    @GET
    @Produces("text/xml")
    @Transactional
    public OnmsEventCollection getEvents() {
    	MultivaluedMap<java.lang.String,java.lang.String> params=m_uriInfo.getQueryParameters();
		OnmsCriteria criteria=new OnmsCriteria(OnmsEvent.class);

    	int limit=10; //Default limit to 10
    	if(params.containsKey("limit")) {
    		limit=Integer.parseInt(params.getFirst("limit"));
    		params.remove("limit");
    	}
		criteria.setMaxResults(limit);

    	if(params.containsKey("offset")) {
    		criteria.setFirstResult(Integer.parseInt(params.getFirst("offset")));
    		params.remove("offset");
    	}
    	
		for(String key: params.keySet()) {
    		String thisValue=params.getFirst(key);
    		criteria.add(Restrictions.eq(key, thisValue));
    	}
        return new OnmsEventCollection(m_eventDao.findMatching(criteria));
    }
}

