package org.opennms.web.rest;

import java.util.Date;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

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

    @Context
    SecurityContext m_securityContext;
    
    @GET
    @Produces("text/xml")
    @Path("{eventId}")
    @Transactional
    public OnmsEvent getEvent(@PathParam("eventId") String eventId) {
    	OnmsEvent result= m_eventDao.get(new Integer(eventId));
    	return result;
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
    
    @PUT
    @Path("{eventId}")
    @Transactional
    public void updateEvent(@PathParam("eventId") String eventId, @FormParam("ack") Boolean ack) {
    	OnmsEvent event=m_eventDao.get(new Integer(eventId));
    	if(ack==null) {
    		throw new  IllegalArgumentException("Must supply the 'ack' parameter, set to either 'true' or 'false'");
    	}
       	if(ack) {
    		event.setEventAckTime(new Date());
    		event.setEventAckUser(m_securityContext.getUserPrincipal().getName());
    	} else {
    		event.setEventAckTime(null);
    		event.setEventAckUser(null);
    	}
    	m_eventDao.save(event);
    }
}

