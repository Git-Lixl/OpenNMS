package org.opennms.sms.monitor.internal.config;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.core.tasks.Async;
import org.opennms.core.tasks.Callback;
import org.opennms.sms.monitor.MobileSequenceSession;
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

	public Async<MobileMsgResponse> createAsync(final MobileSequenceSession session, final MobileMsgTracker tracker) {

	    return new Async<MobileMsgResponse>() {

            public void submit(Callback<MobileMsgResponse> cb) {
                if (getTransaction().getSequenceConfig().hasFailed()) {
                	cb.complete(null);
                }
                
                session.sendUssd(tracker, getGatewayIdForRequest(), getText(), getTransaction().getResponseMatcher(session), cb);
            }
	        
	    };
    }

}
