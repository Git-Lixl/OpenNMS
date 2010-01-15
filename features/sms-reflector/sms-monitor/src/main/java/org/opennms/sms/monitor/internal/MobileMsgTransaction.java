/**
 * 
 */
package org.opennms.sms.monitor.internal;

import org.opennms.core.tasks.DefaultTaskCoordinator;
import org.opennms.core.tasks.SequenceTask;
import org.opennms.core.tasks.Task;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.monitor.internal.config.MobileSequenceConfig;
import org.opennms.sms.monitor.internal.config.MobileSequenceTransaction;
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;

public class MobileMsgTransaction {

	private MobileSequenceConfig m_sequenceConfig;
	private MobileSequenceTransaction m_transaction;
	private MobileSequenceSession m_session;
	
	public MobileMsgTransaction(MobileSequenceConfig sequenceConfig, MobileSequenceTransaction transaction, MobileSequenceSession session) {
		m_sequenceConfig = sequenceConfig;
		m_transaction = transaction;
		m_session = session;
	}

	public MobileSequenceSession getSession() {
		return m_session;
	}

	public MobileSequenceTransaction getTransaction() {
		return m_transaction;
	}

	public MobileSequenceConfig getSequenceConfig() {
		return m_sequenceConfig;
	}

	public String getLabel() {
		return getSession().substitute(getTransaction().getRequest().getLabel(getTransaction().getLabel()));
	}

	public Task createTask(MobileMsgTracker tracker, DefaultTaskCoordinator coordinator, SequenceTask sequence) {
		return getTransaction().createTask(tracker, coordinator, sequence, getSession(), getSequenceConfig());
	}

	protected String getText() {
		return getSession().substitute(getTransaction().getRequest().getText());
	}
	
}
