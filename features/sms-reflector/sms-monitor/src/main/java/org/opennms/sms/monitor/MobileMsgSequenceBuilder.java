package org.opennms.sms.monitor;

import static org.opennms.core.utils.LogUtils.tracef;
import static org.opennms.core.utils.LogUtils.warnf;
import static org.opennms.sms.reflector.smsservice.MobileMsgResponseMatchers.and;
import static org.opennms.sms.reflector.smsservice.MobileMsgResponseMatchers.isSms;
import static org.opennms.sms.reflector.smsservice.MobileMsgResponseMatchers.isUssd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.opennms.core.tasks.DefaultTaskCoordinator;
import org.opennms.core.utils.PropertiesUtils;
import org.opennms.sms.monitor.internal.config.MobileSequenceConfig;
import org.opennms.sms.monitor.internal.config.MobileSequenceResponse;
import org.opennms.sms.monitor.internal.config.MobileSequenceTransaction;
import org.opennms.sms.monitor.internal.config.SequenceResponseMatcher;
import org.opennms.sms.monitor.internal.config.SequenceSessionVariable;
import org.opennms.sms.monitor.internal.config.SmsSequenceRequest;
import org.opennms.sms.monitor.internal.config.SmsSequenceResponse;
import org.opennms.sms.monitor.internal.config.UssdSequenceRequest;
import org.opennms.sms.monitor.internal.config.UssdSequenceResponse;
import org.opennms.sms.monitor.session.SessionVariableGenerator;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;
import org.opennms.sms.reflector.smsservice.MobileMsgSequence;
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;
import org.opennms.sms.reflector.smsservice.MobileMsgTransaction;
import org.opennms.sms.reflector.smsservice.MobileMsgTransaction.SmsTransaction;
import org.opennms.sms.reflector.smsservice.MobileMsgTransaction.UssdTransaction;

public class MobileMsgSequenceBuilder {
	public static final int DEFAULT_RETRIES = 0;
	public static final long DEFAULT_TIMEOUT = 10000L;

	public static abstract class MobileMsgTransactionBuilder {
		private MobileMsgResponseMatcher m_matcher;
		private String m_label;
		private String m_gatewayId;
		private long m_timeout;
		private int m_retries;

		public MobileMsgTransactionBuilder(String label, String gatewayId, long timeout, int retries) {
			m_label = label;
			m_gatewayId = gatewayId;
			m_timeout = timeout;
			m_retries = retries;
		}

		public abstract MobileMsgTransaction getTransaction();

		public MobileMsgTransactionBuilder expects(MobileMsgResponseMatcher matcher) {
			m_matcher = matcher;
			return this;
		}
		
		public String getLabel() {
			return m_label;			
		}
		
		public String getGatewayId() {
			return m_gatewayId;
		}

		public long getTimeout() {
			return m_timeout;
		}

		public MobileMsgTransactionBuilder setTimeout(long timeout) {
			m_timeout = timeout;
			return this;
		}

		public int getRetries() {
			return m_retries;
		}
		
		public MobileMsgTransactionBuilder setRetries(int retries) {
			m_retries = retries;
			return this;
		}
		
		public MobileMsgTransactionBuilder setGatewayId(String gatewayId) {
			m_gatewayId = gatewayId;
			return this;
		}
		
		public MobileMsgResponseMatcher getMatcher() {
			return m_matcher;
		}
	}

	public static class SmsTransactionBuilder extends MobileMsgTransactionBuilder {
		private MobileMsgSequence m_sequence;
		private String m_recipient;
		private String m_text;

		public SmsTransactionBuilder(MobileMsgSequence sequence, String label, String gatewayId, long timeout, int retries, String recipient, String text) {
			super(label, gatewayId, timeout, retries);
			m_sequence = sequence;
			m_recipient = recipient;
			m_text = text;
		}

		@Override
		public MobileMsgTransaction getTransaction() {
			return new SmsTransaction(
				m_sequence,
				getLabel(),
				getGatewayId(),
				getTimeout(),
				getRetries(),
				getRecipient(),
				getText(),
				getMatcher()
			);
		}

		private String getText() {
			return m_text;
		}

		private String getRecipient() {
			return m_recipient;
		}
	}

	public static class UssdTransactionBuilder extends MobileMsgTransactionBuilder {
		private MobileMsgSequence m_sequence;
		private String m_text;
		
		public UssdTransactionBuilder(MobileMsgSequence sequence, String label, String gatewayId, long timeout, int retries, String text) {
			super(label, gatewayId, timeout, retries);
			m_sequence = sequence;
			m_text = text;
		}

		@Override
		public MobileMsgTransaction getTransaction() {
			return new UssdTransaction(
				m_sequence,
				getLabel(),
				getGatewayId(),
				getTimeout(),
				getRetries(),
				m_text,
				getMatcher()
			);
		}
		
	}
	
	private MobileMsgSequence m_sequence = new MobileMsgSequence();
	private String m_gatewayId = "*";
	private MobileMsgTransactionBuilder m_currentBuilder;
	private long m_timeout = DEFAULT_TIMEOUT;
	private int m_retries = DEFAULT_RETRIES;
	
	private MobileSequenceConfig m_sequenceConfig;
	
	public MobileMsgSequenceBuilder(MobileSequenceConfig sequenceConfig) {
	    m_sequenceConfig = sequenceConfig;
    }
	
	public MobileSequenceConfig getSequenceConfig() {
	    return m_sequenceConfig;
	}

    public MobileMsgTransactionBuilder sendSms(String label, String gatewayId, String recipient, String text) {
		addCurrentBuilderToSequence();
		m_currentBuilder = new SmsTransactionBuilder(m_sequence, label, gatewayId == null? m_gatewayId : gatewayId, m_timeout, m_retries, recipient, text);
		return m_currentBuilder;
	}

	public MobileMsgTransactionBuilder sendUssd(String label, String gatewayId, String text) {
		addCurrentBuilderToSequence();
		m_currentBuilder = new UssdTransactionBuilder(m_sequence, label, gatewayId == null? m_gatewayId : gatewayId, m_timeout, m_retries, text);
		return m_currentBuilder;
	}

	public MobileMsgSequenceBuilder setDefaultGatewayId(String gatewayId) {
		m_gatewayId = gatewayId;
		return this;
	}

	public long getDefaultTimeout() {
		return m_timeout;
	}

	public MobileMsgSequenceBuilder setDefaultTimeout(long timeout) {
		m_timeout = timeout;
		return this;
	}
	
	public int getDefaultRetries() {
		return m_retries;
	}

	public MobileMsgSequenceBuilder setDefaultRetries(int retries) {
		m_retries = retries;
		return this;
	}
	
	public MobileMsgSequence getSequence() {
		addCurrentBuilderToSequence();
		return m_sequence;
	}

	private void addCurrentBuilderToSequence() {
		if (m_currentBuilder == null) {
			return;
		}

		m_sequence.addTransaction(m_currentBuilder.getTransaction());
		m_currentBuilder = null;
	}

    Map<String, Number> execute(Properties session, MobileMsgTracker tracker, DefaultTaskCoordinator coordinator)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, SequencerException, Throwable {
        setDefaultRetries(Integer.parseInt(session.getProperty("retry", String.valueOf(getDefaultRetries()))));
    	setDefaultTimeout(Long.parseLong(session.getProperty("timeout", String.valueOf(getDefaultTimeout()))));
    
    	Map<String,SessionVariableGenerator> sessionGenerators = new HashMap<String,SessionVariableGenerator>();
    
    	// FIXME: use the service registry for this
    	for (SequenceSessionVariable var : getSequenceConfig().getSessionVariables()) {
    		Class<?> c = Class.forName(var.getClassName());
    
    		Class<?> superclass = c.getSuperclass();
    		if (superclass != null && superclass.getName().equals("org.opennms.sms.monitor.session.BaseSessionVariableGenerator")) {
    			SessionVariableGenerator generator = (SessionVariableGenerator)c.newInstance();
    			generator.setParameters(var.getParametersAsMap());
    			sessionGenerators.put(var.getName(), generator);
    			String value = generator.checkOut();
    			if (value == null) {
    				value = "";
    			}
    			session.setProperty(var.getName(), value);
    		} else {
    		    warnf(this, "unable to get instance of session class: %s", c);
    		}
    	}
    
    	for (final MobileSequenceTransaction t : getSequenceConfig().getTransactions()) {
    		final MobileMsgTransactionBuilder transactionBuilder;
    
    		if (t.getGatewayId() != null) {
    			setDefaultGatewayId(t.getGatewayId());
    		}
    
    		String label = t.getRequest().getLabel();
    		if (label == null) label = t.getLabel();
    		
    		if (t.getRequest() instanceof SmsSequenceRequest) {
    			SmsSequenceRequest req = (SmsSequenceRequest)t.getRequest();
    			transactionBuilder = sendSms(
    				PropertiesUtils.substitute(label, session),
    				PropertiesUtils.substitute(req.getGatewayId(), session),
    				PropertiesUtils.substitute(req.getRecipient(), session),
    				PropertiesUtils.substitute(req.getText(), session)
    			);
    		} else if (t.getRequest() instanceof UssdSequenceRequest) {
    			UssdSequenceRequest req = (UssdSequenceRequest)t.getRequest();
    			transactionBuilder = sendUssd(
    				PropertiesUtils.substitute(label, session),
    				PropertiesUtils.substitute(req.getGatewayId(), session),
    				PropertiesUtils.substitute(req.getText(), session)
    			);
    		} else {
    			throw new SequencerException("Unknown request type: " + t.getRequest());
    		}
    		
    		for (MobileSequenceResponse r : t.getResponses()) {
    			List<MobileMsgResponseMatcher> matchers = new ArrayList<MobileMsgResponseMatcher>();
    			for (SequenceResponseMatcher m : r.getMatchers()) {
    				matchers.add(m.getMatcher(session));
    			}
    			if (r instanceof SmsSequenceResponse) {
    				matchers.add(isSms());
    			} else if (r instanceof UssdSequenceResponse) {
    				matchers.add(isUssd());
    			}
    			transactionBuilder.expects(and(matchers.toArray(new MobileMsgResponseMatcher[0])));
    		}
    	}
    
    	MobileMsgSequence seq = getSequence();
    	tracef(this, "MobileMsgSequence = %s", seq);
    	try {
    		long start = System.currentTimeMillis();
            Map<String, Number> responseTimes = seq.execute(tracker, coordinator);
    		long end = System.currentTimeMillis();
    		responseTimes.put("response-time", Long.valueOf(end - start));
    		return responseTimes;
    	} finally {
    		for (Map.Entry<String, SessionVariableGenerator> generator : sessionGenerators.entrySet()) {
    			generator.getValue().checkIn(session.getProperty(generator.getKey()));
    		}
    	}
    }

}
