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

	String getStatusOpen() {
		//return getProperties().getString("remedy.status.open");
		return "In Progress";
	}
	
	String getStatusClosed() {
		//return getProperties().getString("remedy.status.closed");
		return "Closed";		
	}

	String getStatusCancelled() {
		//return getProperties().getString("remedy.status.cancelled");
		return "Cancelled";		
	}

}
