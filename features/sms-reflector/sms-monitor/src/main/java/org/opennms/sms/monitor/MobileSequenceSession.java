package org.opennms.sms.monitor;

import static org.opennms.core.utils.LogUtils.warnf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.opennms.core.utils.PropertiesUtils;
import org.opennms.sms.monitor.internal.config.SequenceSessionVariable;
import org.opennms.sms.monitor.session.SessionVariableGenerator;

public class MobileSequenceSession {
	
	private static final int DEFAULT_RETRIES = 0;
	private static final long DEFAULT_TIMEOUT = 10000L;
	
	private Properties m_properties = new Properties();
	private Map<String, SessionVariableGenerator> m_generators = new HashMap<String,SessionVariableGenerator>();
	
	public Properties getProperties() {
		return m_properties;
	}
	
	public Map<String, SessionVariableGenerator> getGenerators() {
		return m_generators;
	}

	void setRecipient(String recipient) {
		getProperties().setProperty("recipient", recipient);
	}

	public int getRetries() {
		return Integer.parseInt(getProperties().getProperty("retry", String.valueOf(DEFAULT_RETRIES)));
	}

	public long getTimeout() {
		return Long.parseLong(getProperties().getProperty("timeout", String.valueOf(DEFAULT_TIMEOUT)));
	}

	public String substitute(String string) {
		return PropertiesUtils.substitute(string, getProperties());
	}

	void checkinVariables() {
		for (Map.Entry<String, SessionVariableGenerator> generator : getGenerators().entrySet()) {
			generator.getValue().checkIn(getProperties().getProperty(generator.getKey()));
		}
	}

	void checkoutVariables(List<SequenceSessionVariable> sessionVariables)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		for (SequenceSessionVariable var : sessionVariables) {
			Class<?> c = Class.forName(var.getClassName());
			
			if (SessionVariableGenerator.class.isAssignableFrom(c)) {
				SessionVariableGenerator generator = (SessionVariableGenerator)c.newInstance();
				generator.setParameters(var.getParametersAsMap());
				getGenerators().put(var.getName(), generator);
				String value = generator.checkOut();
				if (value == null) {
					value = "";
				}
				getProperties().setProperty(var.getName(), value);
			} else {
			    warnf(this, "unable to get instance of session class: %s", c);
			}
		}
	}

	public MobileSequenceSession(Map<String, Object> parameters) {
		if (parameters.get("retry") == null) {
		    parameters.put("retry", String.valueOf(DEFAULT_RETRIES));
		}
		if (parameters.get("timeout") == null) {
		    parameters.put("timeout", String.valueOf(DEFAULT_TIMEOUT));
		}
		// first, transfer anything from the parameters to the session
		for (Map.Entry<String,Object> entry : parameters.entrySet()) {
		    if (entry.getKey() != null && entry.getValue() != null) {
		        getProperties().put(entry.getKey(), entry.getValue());
		    }
		}
	}

}
