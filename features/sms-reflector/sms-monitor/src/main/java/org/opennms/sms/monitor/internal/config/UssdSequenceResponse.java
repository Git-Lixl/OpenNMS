package org.opennms.sms.monitor.internal.config;

import static org.opennms.sms.reflector.smsservice.MobileMsgResponseMatchers.isUssd;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;

@XmlRootElement(name="ussd-response")
public class UssdSequenceResponse extends MobileSequenceResponse {

	public UssdSequenceResponse() {
		super();
	}
	
	public UssdSequenceResponse(String label) {
		super(label);
	}
	
	public UssdSequenceResponse(String gatewayId, String label) {
		super(gatewayId, label);
	}

	protected MobileMsgResponseMatcher getResponseTypeMatcher() {
		return isUssd();
	}
}
