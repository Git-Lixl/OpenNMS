package org.opennms.sms.monitor.internal.config;

import static org.opennms.sms.reflector.smsservice.MobileMsgResponseMatchers.isSms;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;

@XmlRootElement(name="sms-response")
public class SmsSequenceResponse extends MobileSequenceResponse {
	
	public SmsSequenceResponse() {
		super();
	}
	
	public SmsSequenceResponse(String label) {
		super(label);
	}
	
	public SmsSequenceResponse(String gatewayId, String label) {
		super(gatewayId, label);
	}

	protected MobileMsgResponseMatcher getResponseTypeMatcher() {
		return isSms();
	}
}
