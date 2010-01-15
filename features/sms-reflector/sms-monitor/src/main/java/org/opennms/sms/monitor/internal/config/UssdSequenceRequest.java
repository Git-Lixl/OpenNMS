package org.opennms.sms.monitor.internal.config;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.monitor.SequencerException;
import org.opennms.sms.monitor.internal.MobileMsgTransaction;
import org.opennms.sms.monitor.internal.MobileMsgTransaction.UssdTransaction;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;

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
	public MobileMsgTransaction createTransaction(MobileSequenceConfig sequenceConfig, MobileSequenceTransaction transaction, MobileSequenceSession session, MobileMsgResponseMatcher match) throws SequencerException {
		return new UssdTransaction(sequenceConfig, transaction, session, match);
	}

}
