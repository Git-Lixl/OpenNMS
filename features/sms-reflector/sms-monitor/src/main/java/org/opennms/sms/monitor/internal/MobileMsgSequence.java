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
	private MobileSequenceConfig m_sequenceConfig;
	
	public MobileMsgSequence(MobileSequenceConfig mobileSequenceConfig) {
		setSequenceConfig(mobileSequenceConfig);
	}

	public void addTransaction(MobileMsgTransaction t) {
		getTransactions().add(t);
	}
	
	public void addTransaction(MobileSequenceTransaction t){
		getSequenceConfig().addTransaction(t);
	}

	public String toString() {
		return new ToStringCreator(this)
			.append("transactions", getTransactions())
			.toString();
	}

	public List<MobileMsgTransaction> getTransactions() {
		return m_transactions;
	}

	private void setSequenceConfig(MobileSequenceConfig sequenceConfig) {
		m_sequenceConfig = sequenceConfig;
	}

	public MobileSequenceConfig getSequenceConfig() {
		return m_sequenceConfig;
	}
}
