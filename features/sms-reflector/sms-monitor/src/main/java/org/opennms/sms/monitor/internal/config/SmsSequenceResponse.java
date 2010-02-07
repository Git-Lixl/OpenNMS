package org.opennms.sms.monitor.internal.config;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.sms.reflector.smsservice.MobileMsgRequest;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;
import org.opennms.sms.reflector.smsservice.SmsResponse;

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

	@Override
    protected boolean matchesResponseType(MobileMsgRequest request, MobileMsgResponse response) {
        return response instanceof SmsResponse;
    }
}
