/**
 * 
 */
package org.opennms.sms.monitor.internal.config;

import org.opennms.core.tasks.Async;
import org.opennms.core.tasks.Callback;
import org.opennms.core.tasks.DefaultTaskCoordinator;
import org.opennms.core.tasks.SequenceTask;
import org.opennms.core.tasks.Task;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;
import org.springframework.core.style.ToStringCreator;

public abstract class MobileMsgTransaction implements Callback<MobileMsgResponse> {
	public static class SmsTransaction extends MobileMsgTransaction {
		public SmsTransaction(MobileSequenceConfig sequenceConfig, MobileSequenceTransaction transaction, MobileSequenceSession session, String recipient, MobileMsgResponseMatcher matcher) {
			super(sequenceConfig, transaction, session, matcher);
		}

		@Override
		public Async<MobileMsgResponse> createAsync(MobileMsgTracker tracker) {
			return new SmsAsync(tracker, getSequence(), getGatewayId(), getTimeout(), getRetries(), getRecipient(), getText(), getMatcher());
		}

		public String toString() {
			return new ToStringCreator(this)
				.append("label", getLabel())
				.append("gatewayId", getGatewayId())
				.append("timeout", getTimeout())
				.append("retries", getRetries())
				.append("recipient", getRecipient())
				.append("text", getText())
				.append("matcher", getMatcher())
				.toString();
		}

		private String getRecipient() {
			return getSession().substitute(((SmsSequenceRequest)getTransaction().getRequest()).getRecipient());
		}
	}

	public static class UssdTransaction extends MobileMsgTransaction {
		public UssdTransaction(MobileSequenceConfig sequenceConfig, MobileSequenceTransaction transaction, MobileSequenceSession session, MobileMsgResponseMatcher matcher) {
			super(sequenceConfig, transaction, session, matcher);
		}

		@Override
		public Async<MobileMsgResponse> createAsync(MobileMsgTracker tracker) {
			return new UssdAsync(tracker, getSequence(), getGatewayId(), getTimeout(), getRetries(), getText(), getMatcher());
		}

		public String toString() {
			return new ToStringCreator(this)
				.append("label", getLabel())
				.append("gatewayId", getGatewayId())
				.append("timeout", getTimeout())
				.append("retries", getRetries())
				.append("text", getText())
				.append("matcher", getMatcher())
				.toString();
		}
	}


	private MobileMsgResponseMatcher m_matcher;
	private Long m_latency;
	private Throwable m_error;
	private MobileSequenceConfig m_sequenceConfig;
	private MobileSequenceTransaction m_transaction;
	private MobileSequenceSession m_session;
	
	public MobileMsgTransaction(MobileSequenceConfig sequenceConfig, MobileSequenceTransaction transaction, MobileSequenceSession session, MobileMsgResponseMatcher matcher) {
		m_sequenceConfig = sequenceConfig;
		m_transaction = transaction;
		m_session = session;
		m_matcher = matcher;
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

	public MobileMsgSequence getSequence() {
		return getSequenceConfig().getSequence();
	}

	public String getLabel() {
		return getSession().substitute(getTransaction().getRequest().getLabel(getTransaction().getLabel()));
	}

	public String getGatewayId() {
		return getSession().substitute(getTransaction().getRequest().getGatewayId(getTransaction().getDefaultGatewayId()));
	}
	
	public long getTimeout() {
		return getSession().getTimeout();
	}
	
	public int getRetries() {
		return getSession().getRetries();
	}
	
	public MobileMsgResponseMatcher getMatcher() {
		return m_matcher;
	}
	
	public void setMatch(MobileMsgResponseMatcher matcher) {
		m_matcher = matcher;
	}

	public Long getLatency() {
		return m_latency;
	}
	
	public Throwable getError() {
		return m_error;
	}

	public Task createTask(MobileMsgTracker tracker, DefaultTaskCoordinator coordinator, SequenceTask sequence) {
		return coordinator.createTask(sequence, createAsync(tracker), this);
	}

	public abstract Async<MobileMsgResponse> createAsync(MobileMsgTracker tracker);

	public void complete(MobileMsgResponse t) {
		if (t != null) {
			m_latency = t.getReceiveTime() - t.getRequest().getSentTime();
		}
	}

	public void handleException(Throwable t) {
		m_error = t;
	}

	public String toString() {
		return new ToStringCreator(this)
			.append("label", getLabel())
			.append("gatewayId", getGatewayId())
			.append("timeout", getTimeout())
			.append("retries", getRetries())
			.append("matcher", getMatcher())
			.toString();
	}

	protected String getText() {
		return getSession().substitute(getTransaction().getRequest().getText());
	}
}
