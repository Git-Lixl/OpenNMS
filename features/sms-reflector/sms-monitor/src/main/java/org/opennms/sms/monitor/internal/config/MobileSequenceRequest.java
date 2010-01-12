package org.opennms.sms.monitor.internal.config;

import java.util.Properties;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.opennms.sms.monitor.SequencerException;
import org.opennms.sms.monitor.MobileMsgSequenceBuilder.MobileMsgTransactionBuilder;
import org.opennms.sms.reflector.smsservice.MobileMsgSequence;

@XmlRootElement(name="request")
public abstract class MobileSequenceRequest extends MobileSequenceOperation {
	private String m_text;

	public MobileSequenceRequest() {
		super();
	}
	
	public MobileSequenceRequest(String label, String text) {
		super(label);
		setText(text);
	}

	public MobileSequenceRequest(String gatewayId, String label, String text) {
		super(gatewayId, label);
		setText(text);
	}

	@XmlAttribute(name="text")
	public String getText() {
		return m_text;
	}
	
	public void setText(String text) {
		m_text = text;
	}
	
	public String toString() {
		return new ToStringBuilder(this)
			.append("gatewayId", getGatewayId())
			.append("label", getLabel())
			.append("text", getText())
			.toString();
	}

	public abstract MobileMsgTransactionBuilder getRequestTransaction(MobileMsgSequence sequence, Properties session, String defaultLabel, String defaultGatewayId, long defaultTimeout, int defaultRetries) throws SequencerException;
}
