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
	
	String m_ticketId;
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
            m_ticketId = m_ticket.getId();
            System.out.println("Got TicketId");
        } catch (PluginException e) {
            e.printStackTrace();
        }		
	}	
	
	public void testGet() {
		if (m_ticketId == null)
			testSave();
		try {
			Ticket ticket = m_ticketer.get(m_ticketId);
			assertEquals(m_ticketId, ticket.getId());
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
	
	public void testOpenCloseStatus() {
		if (m_ticketId == null)
			testSave();
		try {
			// Close the Ticket
			m_ticket.setState(State.CLOSED);
			m_ticketer.saveOrUpdate(m_ticket);
			
			Ticket ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.CLOSED, ticket.getState());

			//Reopen The Ticket
			m_ticket.setState(State.OPEN);
			m_ticketer.saveOrUpdate(m_ticket);
			
			ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.OPEN, ticket.getState());			
		} catch (PluginException e) {
			e.printStackTrace();
		}
	}

	public void testOpenToCancelledStatus() {
		if (m_ticketId == null)
			testSave();
		try {
			Ticket ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.OPEN, ticket.getState());

			//Cancel the Ticket
			m_ticket.setState(State.CANCELLED);
			m_ticketer.saveOrUpdate(m_ticket);
			
			ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.CANCELLED, ticket.getState());
			
			// try to re open
			m_ticket.setState(State.OPEN);
			m_ticketer.saveOrUpdate(m_ticket);
			// but still cancelled
			ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.CANCELLED, ticket.getState());

			// try to close
			m_ticket.setState(State.CLOSED);
			m_ticketer.saveOrUpdate(m_ticket);
			// but still cancelled
			ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.CANCELLED, ticket.getState());
		} catch (PluginException e) {
			e.printStackTrace();
		}
	}

	public void testClosedToCancelledStatus() {
		if (m_ticketId == null)
			testSave();
		try {
			Ticket ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.OPEN, ticket.getState());

			//Close the Ticket
			m_ticket.setState(State.CLOSED);
			m_ticketer.saveOrUpdate(m_ticket);
			
			ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.CLOSED, ticket.getState());

			//Cancel the Ticket
			m_ticket.setState(State.CANCELLED);
			m_ticketer.saveOrUpdate(m_ticket);
			
			ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.CANCELLED, ticket.getState());
			
			// try to re open
			m_ticket.setState(State.OPEN);
			m_ticketer.saveOrUpdate(m_ticket);
			// but still cancelled
			ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.CANCELLED, ticket.getState());

			// try to close
			m_ticket.setState(State.CLOSED);
			m_ticketer.saveOrUpdate(m_ticket);
			// but still cancelled
			ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.CANCELLED, ticket.getState());
		} catch (PluginException e) {
			e.printStackTrace();
		}
	}

}
