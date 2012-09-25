package org.opennms.netmgt.ticketer.remedy;

import java.util.Date;
import java.util.Properties;

import junit.framework.TestCase;

import org.opennms.api.integration.ticketing.PluginException;
import org.opennms.api.integration.ticketing.Ticket;
import org.opennms.api.integration.ticketing.Ticket.State;
import org.opennms.integration.remedy.ticketservice.OutputMapping1;
import org.opennms.integration.remedy.ticketservice.OutputMapping1GetListValues;
import org.opennms.test.mock.MockLogAppender;

public class RemedyTicketerPluginTest extends TestCase {

	
	// defaults for ticket	
	DefaultRemedyConfigDao m_configDao;
	
	RemedyTicketerPlugin m_ticketer;
	
	Ticket m_ticket;
	
	String m_ticketId;
	 @Override
	 protected void setUp() throws Exception {
		  Properties p = new Properties();
	        p.setProperty("log4j.logger.org.apache.axis", "DEBUG");
	        MockLogAppender.setupLogging(p);
	        m_ticketer = new RemedyTicketerPlugin();
	        
	        m_configDao = new DefaultRemedyConfigDao();
	        
	        m_ticket = new Ticket();
	        m_ticket.setState(Ticket.State.OPEN);
	        m_ticket.setSummary("Test OpenNMS Integration");
	        m_ticket.setDetails("Created by Axis java client. Date: "+ new Date());
			m_ticket.setUser("antonio@opennms.it");
			
	}

	public void testQueryList() {
		try {
			OutputMapping1 destinationGroups = m_ticketer.getDestinationGroup();
			assertEquals(2, destinationGroups.getGetListValues().length);
			for (OutputMapping1GetListValues value : destinationGroups.getGetListValues()) {
				System.err.println(value.getCompany());
				System.err.println(value.getSupport_Group_Name());
				System.err.println(value.getSupport_Organization());
			}
		} catch (PluginException e) {
			e.printStackTrace();
		}
	}
	public void testSaveAndGet() {
	    		
		try {
            m_ticketer.saveOrUpdate(m_ticket);
            m_ticketId = m_ticket.getId();
			Ticket ticket = m_ticketer.get(m_ticketId);
			assertEquals(m_ticketId, ticket.getId());
			assertEquals(State.OPEN, ticket.getState());
		} catch (PluginException e) {
			e.printStackTrace();
		}
		
	}
	
	public void testOpenCloseStatus() {
		testSaveAndGet();
		try {
			assertEquals(State.OPEN, m_ticket.getState());			
			
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
			
			//Cancel the Ticket
			m_ticket.setState(State.CANCELLED);
			m_ticketer.saveOrUpdate(m_ticket);
			
			ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.CANCELLED, ticket.getState());

			// try to close
			m_ticket.setState(State.CLOSED);
			m_ticketer.saveOrUpdate(m_ticket);
			// but still cancelled
			ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.CANCELLED, ticket.getState());

			// try to re open
			m_ticket.setState(State.OPEN);
			m_ticketer.saveOrUpdate(m_ticket);
			// but still cancelled
			ticket = m_ticketer.get(m_ticketId);
			assertEquals(State.CANCELLED, ticket.getState());

		} catch (PluginException e) {
			e.printStackTrace();
		}
	}

	public void testClosedToCancelledStatus() {
		testSaveAndGet();
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
