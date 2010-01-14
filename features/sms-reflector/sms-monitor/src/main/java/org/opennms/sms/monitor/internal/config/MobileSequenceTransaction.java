package org.opennms.sms.monitor.internal.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.monitor.SequencerException;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;

@XmlRootElement(name="transaction")
@XmlType(propOrder={"request", "responses"})
public class MobileSequenceTransaction implements Comparable<MobileSequenceTransaction> {
	private String m_label;
	private String m_gatewayId;
	private String m_defaultGatewayId;
	private MobileSequenceRequest m_request;
	private List<MobileSequenceResponse> m_responses = Collections.synchronizedList(new ArrayList<MobileSequenceResponse>());

	public MobileSequenceTransaction() {
	}

	public MobileSequenceTransaction(String label) {
		this();
		setLabel(label);
	}
	
	public MobileSequenceTransaction(String gatewayId, String label) {
		this(label);
		setGatewayId(gatewayId);
	}
	
	public void setDefaultGatewayId(String gatewayId) {
		m_defaultGatewayId = gatewayId;
	}
	
	@XmlTransient
	public String getDefaultGatewayId() {
		return m_defaultGatewayId;
	}
	
	@XmlAttribute(name="label")
	public String getLabel() {
		return m_label;
	}
	
	public void setLabel(String label) {
		m_label = label;
	}

	@XmlAttribute(name="gatewayId", required=false)
	public void setGatewayId(String gatewayId) {
		m_gatewayId = gatewayId;
	}

	public String getGatewayId() {
		return m_gatewayId;
	}

	@XmlElementRef
	public MobileSequenceRequest getRequest() {
		return m_request;
	}
	
	public void setRequest(MobileSequenceRequest request) {
		m_request = request;
	}
	
	@XmlElementRef
	public List<MobileSequenceResponse> getResponses() {
		return m_responses;
	}

	public synchronized void setResponses(List<MobileSequenceResponse> responses) {
		m_responses.clear();
		m_responses.addAll(responses);
	}
	
	public void addResponse(MobileSequenceResponse response) {
		m_responses.add(response);
		
	}
	public int compareTo(MobileSequenceTransaction o) {
		return new CompareToBuilder()
			.append(this.getRequest(), o.getRequest())
			.append(this.getResponses(), o.getResponses())
			.toComparison();
	}
	
	public String toString() {
		return new ToStringBuilder(this)
			.append("label", getLabel())
			.append("gatewayId", getGatewayId())
			.append("request", getRequest())
			.append("response(s)", getResponses())
			.toString();
	}

	public MobileMsgTransaction createTransaction(MobileSequenceConfig sequenceConfig, MobileSequenceSession session) throws SequencerException {

		MobileMsgResponseMatcher match =null;
		for (MobileSequenceResponse r : getResponses()) {
			match = r.getResponseMatcher(session.getProperties());
		}
		
		return getRequest().createTransaction(sequenceConfig, this, session, match);
	}

}
