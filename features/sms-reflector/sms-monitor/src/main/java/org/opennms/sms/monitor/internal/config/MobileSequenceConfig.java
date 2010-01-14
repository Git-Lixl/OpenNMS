package org.opennms.sms.monitor.internal.config;

import static org.opennms.core.utils.LogUtils.tracef;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.opennms.core.tasks.DefaultTaskCoordinator;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.monitor.SequencerException;
import org.opennms.sms.reflector.smsservice.MobileMsgSequence;
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;

@XmlRootElement(name="mobile-sequence")
public class MobileSequenceConfig implements Serializable, Comparable<MobileSequenceConfig> {
	private static final long serialVersionUID = 1L;
	private List<MobileSequenceTransaction> m_transactions = Collections.synchronizedList(new ArrayList<MobileSequenceTransaction>());
	private List<SequenceSessionVariable> m_sessionVariables;
	private MobileMsgSequence m_sequence = new MobileMsgSequence();

	@XmlTransient
	public MobileMsgSequence getSequence() {
		return m_sequence;
	}

	public void addSessionVariable(SequenceSessionVariable var) {
		getSessionVariables().add(var);
	}

	@XmlElement(name="session-variable")
	public List<SequenceSessionVariable> getSessionVariables() {
		if (m_sessionVariables == null) {
			m_sessionVariables = Collections.synchronizedList(new ArrayList<SequenceSessionVariable>());
		}
		return m_sessionVariables;
	}

	public void setSessionVariables(List<SequenceSessionVariable> sessionVariables) {
		m_sessionVariables = sessionVariables;
	}

	public void addTransaction(MobileSequenceTransaction transaction) {
		m_transactions.add(transaction);
	}

	@XmlElement(name="transaction")
	public List<MobileSequenceTransaction> getTransactions() {
		return m_transactions;
	}

	public synchronized void setTransactions(List<MobileSequenceTransaction> transactions) {
		m_transactions.clear();
		m_transactions.addAll(transactions);
	}

	public int compareTo(MobileSequenceConfig o) {
		return new CompareToBuilder()
			.append(this.getTransactions(), o.getTransactions())
			.toComparison();
	}
	
	public String toString() {
		return new ToStringBuilder(this)
			.append("transactions", getTransactions())
			.toString();
	}

	public void computeDefaultGateways() {
		String defaultGatewayId = "*";
		for (final MobileSequenceTransaction t : getTransactions()) {
			if (t.getGatewayId() != null) {
				defaultGatewayId = t.getGatewayId();
			}
			
			t.setDefaultGatewayId(defaultGatewayId);
		}
	}

	public void createTransaction(MobileSequenceRequest request, MobileSequenceResponse response)
			throws SequencerException {
		MobileSequenceTransaction t = new MobileSequenceTransaction();
		addTransaction(t);
		
		t.setRequest(request);
		
		t.addResponse(response);
		
		MobileSequenceSession session = new MobileSequenceSession(new HashMap<String, Object>());
	
		getSequence().addTransaction(t.createTransaction(this, session));
	}

	public MobileMsgSequence createSequence(MobileSequenceSession session) throws SequencerException {
		computeDefaultGateways();
	
		for (MobileSequenceTransaction t : getTransactions()) {
			getSequence().addTransaction(t.createTransaction(this, session));
		}
	
		MobileMsgSequence seq = getSequence();
		return seq;
	}

	public Map<String, Number> executeSequence(MobileSequenceSession session, MobileMsgTracker tracker, DefaultTaskCoordinator coordinator) throws SequencerException,
			Throwable {
		tracef(this, "MobileMsgSequence = %s", createSequence(session));
		long start = System.currentTimeMillis();
		Map<String, Number> responseTimes = createSequence(session).execute(tracker, coordinator);
		long end = System.currentTimeMillis();
		responseTimes.put("response-time", Long.valueOf(end - start));
		return responseTimes;
	}

}

/*
 * Sample XML (TODO):
 * 
 * <transactionTypes>
 *   <transactionType name="send-ussd" class="org.opennms.sms.monitor.internal.transactions.SendUssd" />
 *   <transactionType name="receive-ussd" class="org.opennms.sms.monitor.internal.transactions.ReceiveUssd" />
 *   <transactionType name="send-sms" class="org.opennms.sms.monitor.internal.transactions.SendSms" />
 *   <transactionType name="receive-sms" class="org.opennms.sms.monitor.internal.transactions.ReceiveSms" />
 * </transactionTypes>
 * 
 */

