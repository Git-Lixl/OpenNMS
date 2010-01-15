package org.opennms.sms.monitor.internal.config;

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
import org.opennms.core.tasks.SequenceTask;
import org.opennms.core.tasks.Task;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.monitor.SequencerException;
import org.opennms.sms.monitor.internal.MobileMsgSequence;
import org.opennms.sms.monitor.internal.MobileMsgTransaction;
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;
import org.springframework.util.Assert;

@XmlRootElement(name="mobile-sequence")
public class MobileSequenceConfig implements Serializable, Comparable<MobileSequenceConfig> {
	private static final long serialVersionUID = 1L;
	private List<MobileSequenceTransaction> m_transactions = Collections.synchronizedList(new ArrayList<MobileSequenceTransaction>());
	private List<SequenceSessionVariable> m_sessionVariables;
	private MobileMsgSequence m_sequence;
	private Task m_task;
	
	public MobileSequenceConfig(){
		m_sequence = new MobileMsgSequence(this);
	}
	
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

	public Map<String, Number> executeSequence(MobileSequenceSession session, MobileMsgTracker tracker, DefaultTaskCoordinator coordinator) throws SequencerException,
			Throwable {
		long start = System.currentTimeMillis();
		start(session, tracker, coordinator);
		Map<String, Number> responseTimes = waitFor();
		long end = System.currentTimeMillis();
		responseTimes.put("response-time", Long.valueOf(end - start));
		return responseTimes;
	}
	
	public void start(MobileSequenceSession session, MobileMsgTracker tracker, DefaultTaskCoordinator coordinator) throws SequencerException {
		computeDefaultGateways();
		
		for (MobileSequenceTransaction t : getTransactions()) {
			getSequence().addTransaction(t.createTransaction(this, session));
		}
		MobileMsgSequence r = getSequence();
		
		Assert.notNull(tracker);
		Assert.notNull(coordinator);
		SequenceTask sequence = coordinator.createSequence(null);
		for (MobileMsgTransaction t1 : r.getTransactions()) {
			Task task = t1.createTask(tracker, coordinator, sequence);
			sequence.add(task);
		}
		sequence.schedule();
		setTask(sequence);
	}

	public Map<String, Number> waitFor() throws Throwable {
		MobileMsgSequence r = getSequence();
		if (getTask() == null) {
			throw new IllegalStateException("getLatency called, but the sequence has never been started!");
		}
		getTask().waitFor();
		
		Map<String,Number> response = new HashMap<String,Number>();
		for (MobileMsgTransaction t : r.getTransactions()) {
			if (t.getTransaction().getError() != null) {
				throw t.getTransaction().getError();
			}
			response.put(t.getLabel(), t.getTransaction().getLatency());
		}
		return response;
	}

	public void setTask(Task task) {
		m_task = task;
	}
	
	@XmlTransient
	public Task getTask() {
		return m_task;
	}

	public boolean hasFailed() {
		for (MobileSequenceTransaction t : getTransactions()) {
			if (t.getError() != null) {
				return true;
			}
		}
		
		return false;
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

