package org.opennms.web.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.hibernate.criterion.Order;
import org.opennms.netmgt.dao.EventDao;
import org.opennms.netmgt.model.OnmsCriteria;
import org.opennms.netmgt.model.OnmsEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.resource.PerRequest;

@Component
@PerRequest
@Scope("prototype")
@Path("events")
public class EventRestService {

    @Autowired
    private EventDao m_eventDao;

    @GET
    @Produces("text/xml")
    /*
     * It could be great add something like criteria.add(Restrictions.like(parameterName, parameterRegex)
     */
    public OnmsEventList getEvents(@QueryParam("filter") String filter,
            @QueryParam("offset") String offset,
            @QueryParam("limit") String limit,
            @QueryParam("orderBy") String orderBy,
            @QueryParam("orderAscending") String orderAscending) {
        
        OnmsCriteria criteria = new OnmsCriteria(OnmsEvent.class);
        if (limit != null)
            criteria.setMaxResults(new Integer(limit));
        if (offset != null)
            criteria.setFirstResult(new Integer(offset));
        boolean ascending = orderAscending != null ? Boolean.parseBoolean(orderAscending) : false;
        if (orderBy != null)
            criteria.addOrder(ascending ? Order.asc(orderBy) : Order.desc(orderBy));
        return new OnmsEventList(m_eventDao.findMatching(criteria));
    }
}
