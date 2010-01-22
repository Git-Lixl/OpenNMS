package org.opennms.sms.monitor;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.opennms.core.tasks.Callback;
import org.opennms.core.utils.PropertiesUtils;
import org.opennms.sms.monitor.internal.config.SequenceSessionVariable;
import org.opennms.sms.monitor.session.SessionVariableGenerator;
import org.opennms.sms.reflector.smsservice.MobileMsgCallbackAdapter;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseCallback;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatchers;
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;
import org.smslib.OutboundMessage;
import org.smslib.USSDRequest;
import org.smslib.USSDSessionStatus;

public class MobileSequenceSession {
	
	private static final int DEFAULT_RETRIES = 0;
	private static final long DEFAULT_TIMEOUT = 10000L;
	
    private List<SequenceSessionVariable> m_sessionVariables;
	

    public MobileSequenceSession() {
        this(new HashMap<String, Object>(), Collections.<SequenceSessionVariable>emptyList());
    }

    public MobileSequenceSession(Map<String, Object> parameters, List<SequenceSessionVariable> sessionVariables) {
        
        m_sessionVariables = sessionVariables; 
        
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
	
    public void setTimeout(long timeout) {
        getProperties().setProperty("timeout", String.valueOf(timeout));
    }

    public void setRetries(int retries) {
        getProperties().setProperty("retry", String.valueOf(retries));
    }



	public String substitute(String string) {
		return PropertiesUtils.substitute(string, getProperties());
	}

	void checkinVariables() {
	    
	    for (SequenceSessionVariable var : m_sessionVariables) {
	        var.checkIn(getProperties());
	    }

	}

    void checkoutVariables() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    
	    for (SequenceSessionVariable var : m_sessionVariables) {
			var.checkOut(getProperties());
		}
	}

    public void sendSms(MobileMsgTracker tracker, String gatewayId, String recipient, String text, MobileMsgResponseMatcher matcher, Callback<MobileMsgResponse> cb) {
    
        MobileMsgResponseCallback mmrc = new MobileMsgCallbackAdapter(cb);
    
    	try {
            OutboundMessage msg = new OutboundMessage(substitute(recipient), substitute(text));
            msg.setGatewayId(substitute(gatewayId));
            tracker.sendSmsRequest(msg, getTimeout(), getRetries(), mmrc, matcher);
    	} catch (Exception e) {
    		cb.handleException(e);
    	}
    
    }

    public void sendUssd(MobileMsgTracker tracker, String gatewayId, String text, MobileMsgResponseMatcher matcher, final Callback<MobileMsgResponse> cb) {
        MobileMsgResponseCallback mmrc = new MobileMsgCallbackAdapter(cb);
    
    	try {
            USSDRequest ussdRequest = new USSDRequest(substitute(text));
            ussdRequest.setGatewayId(substitute(gatewayId));
            tracker.sendUssdRequest(ussdRequest, getTimeout(), getRetries(), mmrc, matcher);
    	} catch (Exception e) {
    		cb.handleException(e);
    	}
    }

	public boolean eqOrMatches(String expected, String actual) {
		return MobileMsgResponseMatchers.isAMatch(substitute(expected), actual);
	}

	public boolean matches(String expected, String actual) {
		return actual.matches(substitute(expected));
	}

	public boolean ussdStatusMatches(String expected, USSDSessionStatus actual) {
		USSDSessionStatus status;
	
		try {
			int statusVal = Integer.parseInt(substitute(expected));
			status = USSDSessionStatus.getByNumeric(statusVal);
		} catch (NumberFormatException e) {
			status = USSDSessionStatus.valueOf(substitute(expected));
		}
		
		return status.equals(actual);
	}

}
