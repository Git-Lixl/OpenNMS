package org.opennms.sms.monitor.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opennms.core.tasks.DefaultTaskCoordinator;
import org.opennms.core.tasks.SequenceTask;
import org.opennms.core.tasks.Task;
import org.opennms.sms.monitor.internal.config.MobileSequenceConfig;
import org.opennms.sms.monitor.internal.config.MobileSequenceTransaction;
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;


public class MobileMsgSequence {

	private List<MobileMsgTransaction> m_transactions = Collections.synchronizedList(new ArrayList<MobileMsgTransaction>());
	private Task m_mainTask;
	private boolean m_failed = false;
	private MobileSequenceConfig m_sequenceConfig;
	
	public MobileMsgSequence(MobileSequenceConfig mobileSequenceConfig) {
		m_sequenceConfig = mobileSequenceConfig;
	}

	public void addTransaction(MobileMsgTransaction t) {
		m_transactions.add(t);
	}
	
	public void addTransaction(MobileSequenceTransaction t){
		m_sequenceConfig.addTransaction(t);
	}

	public Map<String, Number> execute(MobileMsgTracker tracker, DefaultTaskCoordinator coordinator) throws Throwable {
		start(tracker, coordinator);
		return getLatency();
	}

	public void start(MobileMsgTracker tracker, DefaultTaskCoordinator coordinator) {
		Assert.notNull(tracker);
		Assert.notNull(coordinator);
		SequenceTask sequence = coordinator.createSequence(null);
		for (MobileMsgTransaction t : m_transactions) {
			Task task = t.createTask(tracker, coordinator, sequence);
			sequence.add(task);
		}
		sequence.schedule();
		m_mainTask = sequence;
	}

	public Map<String, Number> getLatency() throws Throwable {
		if (m_mainTask == null) {
			throw new IllegalStateException("getLatency called, but the sequence has never been started!");
		}
		m_mainTask.waitFor();
		
		Map<String,Number> response = new HashMap<String,Number>();
		for (MobileMsgTransaction t : m_transactions) {
			if (t.getError() != null) {
				throw t.getError();
			}
			response.put(t.getLabel(), t.getLatency());
		}
		return response;
	}

	public String toString() {
		return new ToStringCreator(this)
			.append("transactions", m_transactions)
			.toString();
	}

	public boolean hasFailed() {
		if (m_failed) {
			return true;
		}
		
		for (MobileMsgTransaction t : m_transactions) {
			if (t.getError() != null) {
				m_failed  = true;
				return true;
			}
		}
		return false;
	}
}
