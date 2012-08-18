/*
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 2007 The OpenNMS Group, Inc. All rights reserved.
 * OpenNMS(R) is a derivative work, containing both original code, included code and modified
 * code that was published under the GNU General Public License. Copyrights for modified
 * and included code are below.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * Modifications:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * For more information contact:
 *      OpenNMS Licensing <license@opennms.org>
 *      http://www.opennms.org/
 *      http://www.opennms.com/
 */
package org.opennms.netmgt.ticketer.remedy;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;


import org.opennms.core.utils.ThreadCategory;
import org.opennms.integration.remedy.ticketservice.AuthenticationInfo;
import org.opennms.integration.remedy.ticketservice.CreateInputMap;
import org.opennms.integration.remedy.ticketservice.GetInputMap;
import org.opennms.integration.remedy.ticketservice.GetOutputMap;
import org.opennms.integration.remedy.ticketservice.HPD_IncidentInterface_Create_WSPortTypePortType;
import org.opennms.integration.remedy.ticketservice.HPD_IncidentInterface_Create_WSServiceLocator;
import org.opennms.integration.remedy.ticketservice.HPD_IncidentInterface_WSPortTypePortType;
import org.opennms.integration.remedy.ticketservice.HPD_IncidentInterface_WSServiceLocator;
import org.opennms.integration.remedy.ticketservice.ImpactType;
import org.opennms.integration.remedy.ticketservice.Reported_SourceType;
import org.opennms.integration.remedy.ticketservice.Service_TypeType;
import org.opennms.integration.remedy.ticketservice.SetInputMap;
import org.opennms.integration.remedy.ticketservice.StatusType;
import org.opennms.integration.remedy.ticketservice.UrgencyType;


import org.opennms.api.integration.ticketing.*;
import org.opennms.api.integration.ticketing.Ticket.State;

/**
 * OpenNMS Trouble Ticket Plugin API implementation for Remedy
 *
 * @author <a href="mailto:jonathan@opennms.org">Jonathan Sartin</a>
 * @author <a href="antonio@opennms.it">Antonio Russo</a>
 * @version $Id: $
 */
public class RemedyTicketerPlugin implements Plugin {
    
	private DefaultRemedyConfigDao m_configDao; 
	
	private String m_serverurl;
	private String m_server;
	private String m_wsName; 
	private String m_createWsName; 
	
	private final static String ACTION_CREATE="CREATE";

	// Remember:
	// Summary ---> alarm logmsg
	// Details ---> alarm descr
	// State   ---> OPEN,CLOSE, CANCELLED
	// User    ---> The owner of the ticket --who create the ticket
	// Attributes --->list of free form attributes in the Ticket.  Typically, from
    // the OnmsAlarm attributes.
	
	/**
	 * <p>Constructor for OtrsTicketerPlugin.</p>
	 */
	public RemedyTicketerPlugin() {
		
		m_configDao = new DefaultRemedyConfigDao();
		m_serverurl = m_configDao.getServerUrl();
		m_server = m_configDao.getServer();
		m_wsName = m_configDao.getWSName();
		m_createWsName = m_configDao.getCreateWSName();
	}

	/** {@inheritDoc} */
	public Ticket get(String ticketId) throws PluginException {

		Ticket opennmsTicket = new Ticket();

		if (ticketId == null)  {
		    
		    log().error("No Remedy ticketID available in OpenNMS Ticket");
		    throw new PluginException("No Remedy ticketID available in OpenNMS Ticket");
		    
		} else {
		    
		    HPD_IncidentInterface_WSPortTypePortType port = getTicketServicePort(m_serverurl,m_server,m_wsName);
	   
		    if (port != null) {
			    try {
					GetOutputMap outputmap = port.helpDesk_Query_Service(getRemedyInputMap(ticketId) , getRemedyAuthenticationHeader());
					opennmsTicket.setId(ticketId);
					opennmsTicket.setSummary(outputmap.getSummary());
					opennmsTicket.setDetails(outputmap.getNotes());
					opennmsTicket.setState(remedyToOpenNMSState(outputmap.getStatus()));
				} catch (RemoteException e) {
					e.printStackTrace();
				}    
		    }		
		}

		// add ticket basics from the Remedy ticket
		return opennmsTicket;

	}


	private State remedyToOpenNMSState(StatusType status) {
		State state = State.OPEN;
		if (status.toString().equals(m_configDao.getStatusClosed())) {
			state = State.CLOSED;
		} else if (status.toString().equals(m_configDao.getStatusCancelled()))
			state = State.CANCELLED;
		return state;
	}

	/** {@inheritDoc} */
	public void saveOrUpdate(Ticket newTicket) throws PluginException {
		
		if ((newTicket.getId() == null) ) {
			save(newTicket);
		} else {
    		update(newTicket);
		}					
	}
    
    private void update(Ticket newTicket) throws PluginException {
    	
    	HPD_IncidentInterface_WSPortTypePortType port = getTicketServicePort(m_serverurl,m_server,m_wsName);
    	if (port != null) {
    		try {
    			GetOutputMap remedy = port.helpDesk_Query_Service(getRemedyInputMap(newTicket.getId()), getRemedyAuthenticationHeader());
    			if (remedy == null)
					log().debug("update: Remedy: Cannot find incident with incindent_number: " + newTicket.getId());
				else
					port.helpDesk_Modify_Service(getRemedySetInputMap(newTicket,remedy) , getRemedyAuthenticationHeader());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
		
	}

    private SetInputMap getRemedySetInputMap(Ticket newTicket,GetOutputMap output) {
		SetInputMap parameters = new SetInputMap();
		parameters.setIncident_Number(newTicket.getId());
		parameters.setSummary(newTicket.getSummary());
		parameters.setNotes(newTicket.getDetails());
		State remedyState = remedyToOpenNMSState(output.getStatus());
		if (newTicket.getState().name().equals(remedyState.name()))
			parameters.setStatus(output.getStatus());
		else 
			parameters.setStatus(opennmsToRemedyState(newTicket.getState()));
		return parameters;
    }
    
    private StatusType opennmsToRemedyState(State state) {
		StatusType remedyStatus;
		
		log().debug("getting otrs state from OpenNMS State " + state.toString());

        switch (state) {
        
            case OPEN:
            	// ticket is new
            	remedyStatus = StatusType.fromValue(m_configDao.getStatusOpen());
            	break;
            case CANCELLED:
            	// not sure how often we see this
            	remedyStatus = StatusType.fromValue(m_configDao.getStatusCancelled());
            	break;
            case CLOSED:
                // closed successful
            	remedyStatus = StatusType.fromValue(m_configDao.getStatusClosed());
                break;
            default:
            	log().debug("No valid OpenNMS state on ticket");
            	remedyStatus = StatusType.fromValue(m_configDao.getStatusOpen());
        }
        
        log().debug("OpenNMS state was        " + state.toString());
        log().debug("setting Remedy state ID to " + remedyStatus.toString());
        
        return remedyStatus;
	}

	private GetInputMap getRemedyInputMap(String ticketId) {
		GetInputMap parameters = new GetInputMap();
		parameters.setIncident_Number(ticketId);
		return parameters;

    }
    
    private AuthenticationInfo getRemedyAuthenticationHeader() {
		AuthenticationInfo request_header = new AuthenticationInfo();
		request_header.setUserName(m_configDao.getUserName());
		request_header.setPassword(m_configDao.getPassword());
		String authentication = m_configDao.getAuthentication();
		if (authentication != null )
			request_header.setAuthentication(authentication);
		String locale = m_configDao.getLocale();
		if (locale != null) 
			request_header.setLocale(locale);
		String timezone = m_configDao.getTimeZone();
		if (timezone != null)
			request_header.setTimeZone(timezone);
		return request_header;
    }

    
    private CreateInputMap getRemedyCreateInputMap(Ticket newTicket) {

		CreateInputMap createInputMap = new CreateInputMap();
		
		// the only data setted by the opennms ticket alarm
		createInputMap.setSummary(newTicket.getSummary());
		createInputMap.setNotes(newTicket.getDetails());
		
		// all this is mandatory and set using the configuration file
		createInputMap.setFirst_Name(m_configDao.getFirstName());
		createInputMap.setLast_Name(m_configDao.getLastName());		
		createInputMap.setServiceCI(m_configDao.getServiceCI());
		createInputMap.setServiceCI_ReconID(m_configDao.getServiceCIReconID());
		createInputMap.setImpact(ImpactType.value4);
		createInputMap.setReported_Source(Reported_SourceType.value1);
		createInputMap.setService_Type(Service_TypeType.value1);
		createInputMap.setUrgency(UrgencyType.value4);
		createInputMap.setStatus(StatusType.value1);
		createInputMap.setAction(ACTION_CREATE);
		createInputMap.setCategorization_Tier_1(m_configDao.getCategorizationtier1());
		createInputMap.setCategorization_Tier_2(m_configDao.getCategorizationtier2());
		createInputMap.setCategorization_Tier_3(m_configDao.getCategorizationtier3());
						
		return createInputMap;

		
    }

    private void save(Ticket newTicket) throws PluginException {
    	HPD_IncidentInterface_Create_WSPortTypePortType port = getCreateTicketServicePort(m_serverurl,m_server,m_createWsName);
    	try {
			String incident_number = port.helpDesk_Submit_Service(getRemedyAuthenticationHeader(), getRemedyCreateInputMap(newTicket)).getIncident_Number();
			log().debug("created new remedy ticket with reported incident number: " + incident_number);
			newTicket.setId(incident_number);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    	
    }
    
	/**
     * Convenience method for initialising the ticketServicePort and correctly setting the endpoint.
     *
     * @return TicketServicePort to connect to the remote service.
     */
    
    private HPD_IncidentInterface_WSPortTypePortType getTicketServicePort(String address, String servername, String wsname) throws PluginException {
        
        HPD_IncidentInterface_WSServiceLocator service = new HPD_IncidentInterface_WSServiceLocator();
        
        HPD_IncidentInterface_WSPortTypePortType port = null;

        try {
           service.setEndpointAddress(wsname, address+"?server="+servername+"&webService="+wsname);
           port = service.getHPD_IncidentInterface_WSPortTypeSoap();
        } catch (ServiceException e) {
            log().error("Failed initialzing Remedy TicketServicePort" + e);
            throw new PluginException("Failed initialzing Remedy TicketServicePort");
        }
        
        return port;
    }


    /**
     * Convenience method for initialising the ticketServicePort and correctly setting the endpoint.
     *
     * @return TicketServicePort to connect to the remote service.
     */
    
    private HPD_IncidentInterface_Create_WSPortTypePortType getCreateTicketServicePort(String address, String servername, String wsname) throws PluginException {
        
		HPD_IncidentInterface_Create_WSServiceLocator service = new HPD_IncidentInterface_Create_WSServiceLocator();
        
        HPD_IncidentInterface_Create_WSPortTypePortType port = null;

        try {
           service.setEndpointAddress(wsname, address+"?server="+servername+"&webService="+wsname);
           port = service.getHPD_IncidentInterface_Create_WSPortTypeSoap();
        } catch (ServiceException e) {
            log().error("Failed initialzing Remedy TicketServicePort" + e);
            throw new PluginException("Failed initialzing Remedy TicketServicePort");
        }
        
        return port;
    }

    /**
	 * Convenience logging.
	 * 
	 * @return a log4j Category for this class
	 */
	ThreadCategory log() {
		return ThreadCategory.getInstance(getClass());
	}
	
}
