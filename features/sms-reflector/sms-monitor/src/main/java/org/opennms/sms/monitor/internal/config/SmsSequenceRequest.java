package org.opennms.sms.monitor.internal.config;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.opennms.core.tasks.Async;
import org.opennms.core.tasks.Callback;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.reflector.smsservice.MobileMsgCallbackAdapter;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseCallback;
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;
import org.smslib.OutboundMessage;

@XmlRootElement(name="sms-request")
public class SmsSequenceRequest extends MobileSequenceRequest {
	private String m_recipient;

	public SmsSequenceRequest() {
	}

	public SmsSequenceRequest(String label, String text) {
		super(label, text);
	}
	
	public SmsSequenceRequest(String gatewayId, String label, String text) {
		super(gatewayId, label, text);
	}

	@XmlAttribute(name="recipient")
	public String getRecipient() {
		return m_recipient;
	}
	
	public void setRecipient(String recipient) {
		m_recipient = recipient;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("gatewayId", getGatewayId())
			.append("label", getLabel())
			.append("recipient", getRecipient())
			.append("text", getText())
			.toString();
	}

	@Override
    public Async<MobileMsgResponse> createAsync(MobileSequenceConfig sequenceConfig, MobileSequenceTransaction transaction, final MobileSequenceSession session, final MobileMsgTracker tracker) {
	    
	    return new Async<MobileMsgResponse>() {

            public void submit(Callback<MobileMsgResponse> cb) {
                doSubmit(session, tracker, cb);
            }
	        
	    };
	    
	    
    }

    public String getGatewayId(MobileSequenceTransaction transaction) {
        
        return getGatewayId(transaction.getDefaultGatewayId());
    }

    public void doSubmit(MobileSequenceSession session, MobileMsgTracker tracker, final Callback<MobileMsgResponse> cb) {
        if (getTransaction().getSequenceConfig().hasFailed()) {
    		cb.complete(null);
    	}
    	
    	MobileMsgResponseCallback mmrc = new MobileMsgCallbackAdapter(cb);
    
    	try {
            OutboundMessage msg = new OutboundMessage(session.substitute(getRecipient()), session.substitute(getText()));
            msg.setGatewayId(session.substitute(getGatewayId(getTransaction())));
            tracker.sendSmsRequest(msg, session.getTimeout(), session.getRetries(), mmrc, getTransaction().getResponseMatcher(session));
    	} catch (Exception e) {
    		cb.handleException(e);
    	}
    }

}
