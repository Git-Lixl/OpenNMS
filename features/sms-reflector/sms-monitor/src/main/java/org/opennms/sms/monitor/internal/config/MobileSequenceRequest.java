package org.opennms.sms.monitor.internal.config;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.monitor.SequencerException;
import org.opennms.sms.monitor.internal.MobileMsgTransaction;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;

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


	public abstract MobileMsgTransaction createTransaction(MobileSequenceConfig sequenceConfig, MobileSequenceTransaction transaction, MobileSequenceSession session, MobileMsgResponseMatcher match) throws SequencerException;

	public String getGatewayId(String defaultGatewayId) {
		return getGatewayId() == null? defaultGatewayId : getGatewayId();
	}

	public String getLabel(String defaultLabel) {
		return getLabel() == null ? defaultLabel : getLabel();
	}
}
