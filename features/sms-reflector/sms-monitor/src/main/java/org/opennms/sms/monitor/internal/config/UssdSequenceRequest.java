package org.opennms.sms.monitor.internal.config;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.core.tasks.Async;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.monitor.internal.UssdAsync;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;

@XmlRootElement(name="ussd-request")
public class UssdSequenceRequest extends MobileSequenceRequest {

	public UssdSequenceRequest() {
		super();
	}
	
	public UssdSequenceRequest(String label, String text) {
		super(label, text);
	}

	public UssdSequenceRequest(String gatewayId, String label, String text) {
		super(gatewayId, label, text);
	}

	@Override
    public Async<MobileMsgResponse> createAsync(MobileSequenceConfig sequenceConfig, MobileSequenceTransaction transaction, MobileSequenceSession session,
            MobileMsgTracker tracker) {
                return new UssdAsync(tracker, sequenceConfig, session.substitute(getGatewayId(transaction.getDefaultGatewayId())), 
            	        session.getTimeout(), 
            	        session.getRetries(), 
            	        session.substitute(getText()), 
            	        transaction.getResponseMatcher(session));
            }

}
