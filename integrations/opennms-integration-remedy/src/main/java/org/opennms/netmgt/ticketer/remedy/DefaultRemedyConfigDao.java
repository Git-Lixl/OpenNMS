package org.opennms.netmgt.ticketer.remedy;

import java.util.ArrayList;
import java.util.List;

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

	Configuration m_config = null;
	/**
	 * Retrieves the properties defined in the remedy.properties file.
	 * 
	 * @param remedyTicketerPlugin
	 * @return a
	 *         <code>java.util.Properties object containing remedy plugin defined properties
	 */
	
	private Configuration getProperties() {
		if (m_config != null) return m_config;
		String propsFile = new String(System.getProperty("opennms.home") + "/etc/remedy.properties");
		
		LogUtils.debugf(this, "loading properties from: %s", propsFile);
		
		Configuration config = null;
		
		try {
			config = new PropertiesConfiguration(propsFile);
		} catch (final ConfigurationException e) {
		    LogUtils.debugf(this, e, "Unable to load properties from %s", propsFile);
		}
		m_config = config;
		return config;
	
	}
	
	/**
	 * <p>getUserName</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUserName() {
		return getProperties().getString("remedy.username");
		//return "opennmstnn";
	}

	
	String getPassword() {
		return getProperties().getString("remedy.password");
		//return "TNNwsC4ll";
	}

	String getAuthentication() {
		return getProperties().getString("remedy.authentication");
		//return "ARSystem";
	}

	String getLocale() {
		return getProperties().getString("remedy.locale");		
		//return "it_IT";
	}
	
	String getTimeZone() {
		return getProperties().getString("remedy.timezone");
		//return "CET";
	}
	
	String getEndPoint() {
		return getProperties().getString("remedy.endpoint");
		//return "http://172.20.0.76:8180/arsys/services/ARService?server=itts3h&webService=HPD_IncidentInterface_WS";
	}

	String getPortName() {
		return getProperties().getString("remedy.portname");
		//return "HPD_IncidentInterface_WSPortTypeSoap";
	}

	String getCreateEndPoint() {
		return getProperties().getString("remedy.createendpoint");
		//return "http://172.20.0.76:8180/arsys/services/ARService?server=itts3h&webService=HPD_IncidentInterface_Create_WS";
	}

	String getCreatePortName() {
		return getProperties().getString("remedy.createportname");
		//return "HPD_IncidentInterface_Create_WSPortTypeSoap";
	}

	List<String> getTargetGroups() {
		List<String> targetGroups=new ArrayList<String>();
		for (String group: 	getProperties().getString("remedy.targetgroups").split(":")) {
			targetGroups.add(group);
		}
		return targetGroups;
	}
	
	String getAssignedGroup() {
		return getProperties().getString("remedy.assignedgroup");
		//return "TNnet";
	}

	String getFirstName() {
		return getProperties().getString("remedy.firstname");
		//return "Opennms";
	}

	String getLastName() {
		return getProperties().getString("remedy.lastname");
		//return "Tnn";
	}

	String getServiceCI() {
		return getProperties().getString("remedy.serviceCI");
		//return "Trentino Network Event Management [I.TNEVT]";
		//return "Trentino Network Connettivit� [C.TNNCN]";
	}

	String getServiceCIReconID() {
		return getProperties().getString("remedy.serviceCIReconID");
		//return "RE00505688005eP8Z3UAsLJIHg6EoR";
		//return "RE00505688005e3s-nTg4KEI5gFSov";
	}
		
	String getAssignedSupportCompany() {
		return getProperties().getString("remedy.assignedsupportcompany");
		//return "Trentino Network srl";
	}

	String getAssignedSupportOrganization() {
		return getProperties().getString("remedy.assignedsupportorganization");
		//return "Centro Gestione Rete";
	}

	String getCategorizationtier1() {
		return getProperties().getString("remedy.categorizationtier1");
		//return "Incident";
	}

	String getCategorizationtier2() {
		return getProperties().getString("remedy.categorizationtier2");
		//return "Generic";
	}

	String getCategorizationtier3() {
		return getProperties().getString("remedy.categorizationtier3");
		//return "Non bloccante";
	}
	
	String getServiceType() {
		return getProperties().getString("remedy.serviceType");
		//return "Infrastructure Event";
	}
	
	String getReportedSource() {
		return getProperties().getString("remedy.reportedSource");
		//return "Systems Management";
	}

	String getImpact() {
		return getProperties().getString("remedy.impact");
		//return "4-Minor/Localized";
	}

	String getUrgency() {
		return getProperties().getString("remedy.urgency");
		//return "4-Low";
	}
	
	String getResolution() {
		return getProperties().getString("remedy.resolution");
		//return "Chiusura da OpenNMS Web Service";
	}

	String getReOpenStatusReason() {
		return getProperties().getString("remedy.reason.reopen");
		//return "Pending Original Incident";
	}
	
	String getResolvedStatusReason() {
		return getProperties().getString("remedy.reason.resolved");
		//return "Automated Resolution Reported";
	}
	
	String getCancelledStatusReason() {
		return getProperties().getString("remedy.reason.cancelled");
		//return "No longer a Causal CI";
	}	
}
