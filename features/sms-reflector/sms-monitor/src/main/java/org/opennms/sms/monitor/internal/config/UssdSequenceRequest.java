package org.opennms.sms.monitor.internal.config;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.core.tasks.Async;
import org.opennms.core.tasks.Callback;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.reflector.smsservice.MobileMsgCallbackAdapter;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseCallback;
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;
import org.smslib.USSDRequest;

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
    public Async<MobileMsgResponse> createAsync(MobileSequenceConfig sequenceConfig, MobileSequenceTransaction transaction, final MobileSequenceSession session, final MobileMsgTracker tracker) {
	    return new Async<MobileMsgResponse>() {

            public void submit(Callback<MobileMsgResponse> cb) {
                doSubmit(session, tracker, cb);
            }
	        
	    };

	}

    public void doSubmit(MobileSequenceSession session, MobileMsgTracker tracker, final Callback<MobileMsgResponse> cb) {
        if (getTransaction().getSequenceConfig().hasFailed()) {
    		cb.complete(null);
    	}
    
    	MobileMsgResponseCallback mmrc = new MobileMsgCallbackAdapter(cb);
    
    	try {
            USSDRequest ussdRequest = new USSDRequest(session.substitute(getText()));
            ussdRequest.setGatewayId(session.substitute(getGatewayId(getTransaction().getDefaultGatewayId())));
            tracker.sendUssdRequest(ussdRequest, session.getTimeout(), session.getRetries(), mmrc, getTransaction().getResponseMatcher(session));
    	} catch (Exception e) {
    		cb.handleException(e);
    	}
    }

}
