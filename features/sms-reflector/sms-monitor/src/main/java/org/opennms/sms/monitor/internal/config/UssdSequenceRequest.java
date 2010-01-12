package org.opennms.sms.monitor.internal.config;

import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.core.utils.PropertiesUtils;
import org.opennms.sms.monitor.SequencerException;
import org.opennms.sms.monitor.MobileMsgSequenceBuilder.MobileMsgTransactionBuilder;
import org.opennms.sms.monitor.MobileMsgSequenceBuilder.SmsTransactionBuilder;
import org.opennms.sms.monitor.MobileMsgSequenceBuilder.UssdTransactionBuilder;
import org.opennms.sms.reflector.smsservice.MobileMsgSequence;

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
	public MobileMsgTransactionBuilder getRequestTransaction(MobileMsgSequence sequence, Properties session, String defaultLabel,
			String defaultGatewayId, long defaultTimeout, int defaultRetries) throws SequencerException {
		
		String label1 = PropertiesUtils.substitute(getLabel() == null ? defaultLabel : getLabel(), session);
		String gatewayId = PropertiesUtils.substitute(getGatewayId(), session);
		String text = PropertiesUtils.substitute(getText(), session);
		
		return new UssdTransactionBuilder(this, sequence, label1, gatewayId == null? defaultGatewayId : gatewayId, defaultTimeout, defaultRetries, text);
	}

}
