package org.opennms.sms.monitor.internal.config;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.monitor.SequencerException;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;
import org.opennms.sms.reflector.smsservice.MobileMsgTransaction;
import org.opennms.sms.reflector.smsservice.MobileMsgTransaction.UssdTransaction;

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
	public MobileMsgTransaction createTransaction(MobileSequenceConfig sequenceConfig, MobileSequenceSession session, MobileMsgResponseMatcher match, String defaultLabel, String defaultGatewayId) throws SequencerException {
				return new UssdTransaction(sequenceConfig.getSequence(), session.substitute(getLabel(defaultLabel)), session.substitute(getGatewayId(defaultGatewayId)), session.getTimeout(), session.getRetries(), session.substitute(getText()), match);
			}

}
