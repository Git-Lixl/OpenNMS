package org.opennms.sms.monitor.internal.config;

import java.util.Properties;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.opennms.core.utils.PropertiesUtils;
import org.opennms.sms.monitor.SequencerException;
import org.opennms.sms.monitor.MobileMsgSequenceBuilder.MobileMsgTransactionBuilder;
import org.opennms.sms.monitor.MobileMsgSequenceBuilder.SmsTransactionBuilder;
import org.opennms.sms.monitor.MobileMsgSequenceBuilder.UssdTransactionBuilder;
import org.opennms.sms.reflector.smsservice.MobileMsgSequence;

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
	public MobileMsgTransactionBuilder getRequestTransaction(MobileMsgSequence sequence, Properties session, String defaultLabel, String defaultGatewayId, long defaultTimeout, int defaultRetries) throws SequencerException {
				
				String gatewayId = PropertiesUtils.substitute(getGatewayId(), session);
			
				return  new SmsTransactionBuilder(this, sequence, PropertiesUtils.substitute(getLabel() == null ? defaultLabel : getLabel(), session), gatewayId == null? defaultGatewayId : gatewayId, defaultTimeout, defaultRetries, PropertiesUtils.substitute(getRecipient(), session), PropertiesUtils.substitute(getText(), session));
			}
}
