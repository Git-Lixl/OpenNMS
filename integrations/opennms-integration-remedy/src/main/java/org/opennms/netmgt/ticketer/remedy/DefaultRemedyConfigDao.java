package org.opennms.netmgt.ticketer.remedy;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.opennms.core.utils.LogUtils;

/**
 * <p>DefaultremedyConfigDao class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
public class DefaultRemedyConfigDao {

	/**
	 * Retrieves the properties defined in the remedy.properties file.
	 * 
	 * @param remedyTicketerPlugin
	 * @return a
	 *         <code>java.util.Properties object containing remedy plugin defined properties
	 */
	
	private Configuration getProperties() {
		
		String propsFile = new String(System.getProperty("opennms.home") + "/etc/remedy.properties");
		
		LogUtils.debugf(this, "loading properties from: %s", propsFile);
		
		Configuration config = null;
		
		try {
			config = new PropertiesConfiguration(propsFile);
		} catch (final ConfigurationException e) {
		    LogUtils.debugf(this, e, "Unable to load properties from %s", propsFile);
		}
	
		return config;
	
	}
	
	/**
	 * <p>getUserName</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUserName() {
		//return getProperties().getString("remedy.username");
		return "opennmstnn";
	}

	
	String getPassword() {
		//return getProperties().getString("remedy.password");
		return "TNNwsC4ll";
	}

	String getAuthentication() {
		//return getProperties().getString("remedy.authentication");
		return "ARSystem";
	}

	String getLocale() {
		//return getProperties().getString("remedy.locale");		
		 return "it_IT";
	}
	
	String getTimeZone() {
		//return getProperties().getString("remedy.timezone");
		return "CET";
	}
	
	String getEndPoint() {
		//return getProperties().getString("remedy.endpoint");
		return "http://172.20.0.76:8180/arsys/services/ARService?server=itts3h&webService=HPD_IncidentInterface_WS";
	}

	String getPortName() {
		//return getProperties().getString("remedy.portname");
		return "HPD_IncidentInterface_WSPortTypeSoap";
	}

	String getCreateEndPoint() {
		//return getProperties().getString("remedy.createendpoint");
		return "http://172.20.0.76:8180/arsys/services/ARService?server=itts3h&webService=HPD_IncidentInterface_Create_WS";
	}

	String getCreatePortName() {
		//return getProperties().getString("remedy.createportname");
		return "HPD_IncidentInterface_Create_WSPortTypeSoap";
	}

	String getServiceCI() {
		//return getProperties().getString("remedy.serviceCI");
	 return "Trentino Network Connettivitˆ [C.TNNCN]";
	}
	
	String getServiceCIReconID() {
		//return getProperties().getString("remedy.serviceCIReconID");
		return "RE00505688005e3s-nTg4KEI5gFSov";
	}
		
	String getFirstName() {
		//return getProperties().getString("remedy.firstname");
		return "Opennms";
	}

	String getAssignedGroup() {
		//return getProperties().getString("remedy.assignedgroup");
		return "TNnet";
	}

	String getAssignedSupportCompany() {
		//return getProperties().getString("remedy.assignedsupportcompany");
		return "Trentino Network srl";
	}

	String getAssignedSupportOrganization() {
		//return getProperties().getString("remedy.assignedsupportorganization");
		return "Centro Gestione Rete";
	}

	String getLastName() {
		//return getProperties().getString("remedy.lastname");
		return "Tnn";
	}

	String getCategorizationtier1() {
		//return getProperties().getString("remedy.categorizationtier1");
		return "Incident";
	}

	String getCategorizationtier2() {
		//return getProperties().getString("remedy.categorizationtier2");
		return "Generic";
	}

	String getCategorizationtier3() {
		//return getProperties().getString("remedy.categorizationtier3");
		return "Non bloccante";
	}

	String getStatusNew() {
		//return getProperties().getString("remedy.status.new");
		return "New";
	}

	String getStatusAssigned() {
		//return getProperties().getString("remedy.status.assigned");
		return "Assigned";
	}

	String getStatusInProgress() {
		//return getProperties().getString("remedy.status.inprogress");
		return "In Progress";
	}

	String getStatusPending() {
		//return getProperties().getString("remedy.status.pending");
		return "Pending";
	}

	String getStatusResolved() {
		//return getProperties().getString("remedy.status.resolved");
		return "Resolved";
	}
	
	String getStatusClosed() {
		//return getProperties().getString("remedy.status.closed");
		return "Closed";		
	}

	String getStatusCancelled() {
		//return getProperties().getString("remedy.status.cancelled");
		return "Cancelled";		
	}
	
	String getSummary() {
		//return getProperties().getString("remedy.summary");
		return "Opennms Web Service Created Ticket";
	}
	
	String getUrgencyCritical() {
		//return getProperties().getString("remedy.urgencycritical");
		return "1-Critical";
	}

	String getUrgencyHigh() {
		//return getProperties().getString("remedy.urgencyhigh");
		return "2-High";
	}

	String getUrgencyMedium() {
		//return getProperties().getString("remedy.urgencymedium");
		return "3-Medium";
	}

	String getUrgencyLow() {
		//return getProperties().getString("remedy.urgencylow");
		return "4-Low";
	}
	
	String getResolution() {
		//return getProperties().getString("remedy.resolution");
		return "Chiusura da OpenNMS Web Service";
	}

	String getOpenStatusReason() {
		//return getProperties().getString("remedy.openstatusreason");
		return "Pending Original Incident";
	}
	
	String getResolvedStatusReason() {
		//return getProperties().getString("remedy.resolvedstatusreason");
		return "Automated Resolution Reported";
	}
	
	String getCancelledStatusReason() {
		//return getProperties().getString("remedy.cancelledstatusreason");
		return "No longer a Causal CI";
	}
}
