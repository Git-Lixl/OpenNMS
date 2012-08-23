package org.opennms.netmgt.ticketer.remedy;

import java.util.Date;

import junit.framework.TestCase;

import org.opennms.api.integration.ticketing.PluginException;
import org.opennms.api.integration.ticketing.Ticket;
import org.opennms.api.integration.ticketing.Ticket.State;

public class RemedyTicketerPluginTest extends TestCase {

	
	// defaults for ticket	
	DefaultRemedyConfigDao m_configDao;
	
	RemedyTicketerPlugin m_ticketer;
	
	Ticket m_ticket;
	
	 @Override
	 protected void setUp() throws Exception {
	        
	        m_ticketer = new RemedyTicketerPlugin();
	        
	        m_configDao = new DefaultRemedyConfigDao();
	        
	        m_ticket = new Ticket();
	        m_ticket.setState(Ticket.State.OPEN);
	        m_ticket.setSummary("Test OpenNMS Integration");
	        m_ticket.setDetails("Created by Axis java client. Date: "+ new Date());
			m_ticket.setUser("antonio@opennms.it");
			
	}

	public void testSave() {
	    		
		try {
            m_ticketer.saveOrUpdate(m_ticket);
            
        } catch (PluginException e) {
            e.printStackTrace();
        }		
	}	
	
	public void testGet() {
		String ticketId = "INC000000073001";
		try {
			Ticket ticket = m_ticketer.get(ticketId);
			assertEquals(ticketId, ticket.getId());
			System.out.println("TicketId: "+ticket.getId());
			System.out.println("Summary: "+ticket.getSummary());
			System.out.println("Details: "+ticket.getDetails());
			System.out.println("User: " + ticket.getUser());
			State state = ticket.getState();
			System.out.println("State: "+ state);
		} catch (PluginException e) {
			e.printStackTrace();
		}
		
	}
	
	public void testUpdate() {
		String ticketId = "INC000000073001";
		m_ticket.setId(ticketId);
		m_ticket.setDetails(m_ticket.getDetails() + " - Modified by Antonio@opennms.it: " + new Date());
		try {
			m_ticketer.saveOrUpdate(m_ticket);
		} catch (PluginException e) {
			e.printStackTrace();
		}
	}
	
}
