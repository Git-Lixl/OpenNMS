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

/**
 * OpenNMS Trouble Ticket Plugin API implementation for Remedy
 *
 * @author <a href="mailto:jonathan@opennms.org">Jonathan Sartin</a>
 * @author <a href="antonio@opennms.it">Antonio Russo</a>
 * @version $Id: $
 */
public class RemedyTicketerPlugin implements Plugin {
    
	private DefaultRemedyConfigDao m_configDao; 
	
	private String m_endpoint; 
	private String m_createendpoint; 
	
	private final static String ACTION_CREATE="CREATE";

	
	/**
	 * <p>Constructor for OtrsTicketerPlugin.</p>
	 */
	public RemedyTicketerPlugin() {
		
		m_configDao = new DefaultRemedyConfigDao();
		m_endpoint = m_configDao.getEndpoint();
		m_createendpoint = m_configDao.getCreateEndpoint();
	}

	/** {@inheritDoc} */
	public Ticket get(String ticketId) throws PluginException {

		Ticket opennmsTicket = new Ticket();

		if (ticketId == null)  {
		    
		    log().error("No Remedy ticketID available in OpenNMS Ticket");
		    throw new PluginException("No Remedy ticketID available in OpenNMS Ticket");
		    
		} else {
		    
		    HPD_IncidentInterface_WSPortTypePortType port = getTicketServicePort(m_endpoint);
	   
		    if (port != null) {
			    try {
					GetOutputMap outputmap = port.helpDesk_Query_Service(getRemedyInputMap(ticketId) , getRemedyAuthenticationHeader());
					opennmsTicket.setId(ticketId);
					opennmsTicket.setSummary(outputmap.getSummary());
					opennmsTicket.setDetails(outputmap.getNotes());
				} catch (RemoteException e) {
					e.printStackTrace();
				}    
		    }		
		}

		// add ticket basics from the Remedy ticket
		return opennmsTicket;

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
		HPD_IncidentInterface_WSPortTypePortType port = getTicketServicePort(m_endpoint);
		try {
			port.helpDesk_Modify_Service(getRemedySetInputMap(newTicket) , getRemedyAuthenticationHeader());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}

    private SetInputMap getRemedySetInputMap(Ticket newTicket) {
		SetInputMap parameters = new SetInputMap();
		parameters.setIncident_Number(newTicket.getId());
		parameters.setSummary(newTicket.getSummary());
		parameters.setNotes(newTicket.getDetails());
		parameters.setStatus(StatusType.value2);
		return parameters;
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
//		request_header.setAuthentication(m_configDao.getAuthentication());
//		request_header.setLocale(m_configDao.getLocale());
//		request_header.setTimeZone(m_configDao.getTimeZone());
		return request_header;
    }

    
    private CreateInputMap getRemedyCreateInputMap(Ticket newTicket) {

    	String first_Name=m_configDao.getFirstName();
		String last_Name=m_configDao.getLastName();
		
		String categorization_Tier_1=m_configDao.getCategorizationtier1();		
		String categorization_Tier_2=m_configDao.getCategorizationtier2();
		String categorization_Tier_3=m_configDao.getCategorizationtier3();
		
		String summary=newTicket.getSummary();
		
		String notes=newTicket.getDetails();

		String serviceCI=m_configDao.getServiceCI();
		String serviceCI_ReconID=m_configDao.getServiceCIReconID();

		ImpactType impact=ImpactType.value4;
		Reported_SourceType reported_Source=Reported_SourceType.value1;
		Service_TypeType service_Type=Service_TypeType.value1;
		StatusType status=StatusType.value1;
		UrgencyType urgency=UrgencyType.value4;


		CreateInputMap createInputMap = new CreateInputMap();
		
		createInputMap.setFirst_Name(first_Name);
		createInputMap.setLast_Name(last_Name);
		createInputMap.setSummary(summary);
		createInputMap.setNotes(notes);
		createInputMap.setServiceCI(serviceCI);
		createInputMap.setServiceCI_ReconID(serviceCI_ReconID);
		createInputMap.setImpact(impact);
		createInputMap.setReported_Source(reported_Source);
		createInputMap.setService_Type(service_Type);
		createInputMap.setUrgency(urgency);
		createInputMap.setStatus(status);
		createInputMap.setAction(ACTION_CREATE);
		createInputMap.setCategorization_Tier_1(categorization_Tier_1);
		createInputMap.setCategorization_Tier_2(categorization_Tier_2);
		createInputMap.setCategorization_Tier_3(categorization_Tier_3);
		
				
		return createInputMap;

		
    }

    private void save(Ticket newTicket) throws PluginException {
    	HPD_IncidentInterface_Create_WSPortTypePortType port = getCreateTicketServicePort(m_createendpoint);
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
    
    private HPD_IncidentInterface_WSPortTypePortType getTicketServicePort(String address) throws PluginException {
        
        HPD_IncidentInterface_WSServiceLocator service = new HPD_IncidentInterface_WSServiceLocator();
        
        HPD_IncidentInterface_WSPortTypePortType port = null;

        try {
           service.setEndpointAddress("HPD_IncidentInterface_WSPortTypeSoap", address);
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
    
    private HPD_IncidentInterface_Create_WSPortTypePortType getCreateTicketServicePort(String address) throws PluginException {
        
		HPD_IncidentInterface_Create_WSServiceLocator service = new HPD_IncidentInterface_Create_WSServiceLocator();
        
        HPD_IncidentInterface_Create_WSPortTypePortType port = null;

        try {
           service.setEndpointAddress("HPD_IncidentInterface_Create_WSPortTypeSoap", address);
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

    /**
     * <p>getEndpoint</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getEndpoint() {
        return m_endpoint;
    }

    /**
     * <p>setEndpoint</p>
     *
     * @param endpoint a {@link java.lang.String} object.
     */
    public void setEndpoint(String endpoint) {
        m_endpoint = endpoint;
    }
	
	
}
