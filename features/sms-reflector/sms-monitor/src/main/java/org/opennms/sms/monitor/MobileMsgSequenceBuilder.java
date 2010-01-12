package org.opennms.sms.monitor;

import static org.opennms.core.utils.LogUtils.tracef;
import static org.opennms.core.utils.LogUtils.warnf;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.opennms.core.tasks.DefaultTaskCoordinator;
import org.opennms.sms.monitor.internal.config.MobileSequenceConfig;
import org.opennms.sms.monitor.internal.config.MobileSequenceRequest;
import org.opennms.sms.monitor.internal.config.MobileSequenceResponse;
import org.opennms.sms.monitor.internal.config.MobileSequenceTransaction;
import org.opennms.sms.monitor.internal.config.SequenceSessionVariable;
import org.opennms.sms.monitor.internal.config.SmsSequenceRequest;
import org.opennms.sms.monitor.internal.config.UssdSequenceRequest;
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
		private MobileSequenceRequest m_request;
		private MobileMsgSequence m_sequence;
		private MobileMsgResponseMatcher m_matcher;
		private String m_label;
		private String m_gatewayId;
		private long m_timeout;
		private int m_retries;

		public MobileMsgTransactionBuilder(MobileSequenceRequest request, MobileMsgSequence sequence, String label, String gatewayId, long timeout, int retries) {
			m_request = request;
			m_sequence = sequence;
			m_label = label;
			m_gatewayId = gatewayId;
			m_timeout = timeout;
			m_retries = retries;
		}

		public MobileSequenceRequest getRequest() {
			return m_request;
		}

		public MobileMsgSequence getSequence() {
			return m_sequence;
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
		private SmsSequenceRequest m_smsRequest;
		private String m_recipient;
		private String m_text;

		public SmsTransactionBuilder(SmsSequenceRequest request, MobileMsgSequence sequence, String label, String gatewayId, long timeout, int retries, String recipient, String text) {
			super(request, sequence, label, gatewayId, timeout, retries);
			m_smsRequest = request;
			m_recipient = recipient;
			m_text = text;
		}

		@Override
		public MobileMsgTransaction getTransaction() {
			return new SmsTransaction(
				getSequence(),
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
		
		public SmsSequenceRequest getSmsRequest() {
			return m_smsRequest;
		}

	}

	public static class UssdTransactionBuilder extends MobileMsgTransactionBuilder {
		private String m_text;
		private UssdSequenceRequest m_ussdRequest;
		
		public UssdSequenceRequest getUssdRequest() {
			return m_ussdRequest;
		}

		public UssdTransactionBuilder(UssdSequenceRequest ussdSequenceRequest, MobileMsgSequence sequence, String label, String gatewayId, long timeout, int retries, String text) {
			super(ussdSequenceRequest, sequence, label, gatewayId, timeout, retries);
			m_ussdRequest = ussdSequenceRequest;
			m_text = text;
		}

		@Override
		public MobileMsgTransaction getTransaction() {
			return new UssdTransaction(
				getSequence(),
				getLabel(),
				getGatewayId(),
				getTimeout(),
				getRetries(),
				getText(),
				getMatcher()
			);
		}

		public String getText() {
			return m_text;
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

    public MobileMsgTransactionBuilder sendSms(String label, String gatewayId, String recipient, String text) throws SequencerException {
		SmsSequenceRequest request = new SmsSequenceRequest();
		request.setLabel(label);
		request.setGatewayId(gatewayId);
		request.setRecipient(recipient);
		request.setText(text);
		
    	return sendRequest(request); 
	}

	private MobileMsgTransactionBuilder sendRequest(MobileSequenceRequest request) throws SequencerException {
		addCurrentBuilderToSequence();
		m_currentBuilder = request.getRequestTransaction(m_sequence, new Properties(), request.getLabel(), m_gatewayId, m_timeout, m_retries); 
		return m_currentBuilder;
	}

	public MobileMsgTransactionBuilder sendUssd(String label, String gatewayId, String text) throws SequencerException {
		UssdSequenceRequest request = new UssdSequenceRequest();
		request.setLabel(label);
		request.setGatewayId(gatewayId);
		request.setText(text);
		
		return sendRequest(request);
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
    		execute(session, t);
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

	private void execute(Properties session, final MobileSequenceTransaction t) throws SequencerException {
		
   
		if (t.getGatewayId() != null) {
			setDefaultGatewayId(t.getGatewayId());
		}
   
		String defaultLabel = t.getLabel();
		String defaultGatewayId = m_gatewayId;
		long defaultTimeout = m_timeout;
		int defaultRetries = m_retries;
		MobileMsgSequence defaultSequence = m_sequence;
		
		MobileSequenceRequest request = t.getRequest();
		
		MobileMsgTransactionBuilder transactionBuilder = request.getRequestTransaction(
				defaultSequence, session, defaultLabel,
				defaultGatewayId, defaultTimeout, defaultRetries);
		
		for (MobileSequenceResponse r : t.getResponses()) {
			transactionBuilder.expects(r.getResponseMatcher(session));
		}
		addCurrentBuilderToSequence();
		m_currentBuilder = transactionBuilder;
	}

}
