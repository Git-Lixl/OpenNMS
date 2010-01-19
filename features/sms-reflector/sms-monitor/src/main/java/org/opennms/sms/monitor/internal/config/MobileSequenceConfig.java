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
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;
import org.springframework.util.Assert;

@XmlRootElement(name="mobile-sequence")
public class MobileSequenceConfig implements Serializable, Comparable<MobileSequenceConfig> {
    
	private static final long serialVersionUID = 1L;
	
	private List<MobileSequenceTransaction> m_transactions = Collections.synchronizedList(new ArrayList<MobileSequenceTransaction>());
	private List<SequenceSessionVariable> m_sessionVariables;
	private Task m_task;
	
	/**
     * @return the task
     */
	@XmlTransient
    public Task getTask() {
        return m_task;
    }

    /**
     * @param task the task to set
     */
    public void setTask(Task task) {
        m_task = task;
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

	public MobileSequenceTransaction createTransaction(MobileSequenceRequest request, MobileSequenceResponse response) {

        MobileSequenceTransaction t = new MobileSequenceTransaction();
		addTransaction(t);
		
		t.setRequest(request);
		
		t.addResponse(response);
        return t;
    }

	public Map<String, Number> executeSequence(MobileSequenceSession session, MobileMsgTracker tracker, DefaultTaskCoordinator coordinator) throws SequencerException, Throwable {
        long start = System.currentTimeMillis();
	    start(session, tracker, coordinator);
	    Map<String, Number> responseTimes = waitFor(session);
		long end = System.currentTimeMillis();

		responseTimes.put("response-time", Long.valueOf(end - start));
		return responseTimes;
	}

    public Map<String, Number> waitFor(MobileSequenceSession session) throws Throwable {

        Task task = getTask();
        if (task == null) {
        	throw new IllegalStateException("getLatency called, but the sequence has never been started!");
        }
        task.waitFor();
        
        Map<String,Number> response = new HashMap<String,Number>();
        for(MobileSequenceTransaction transaction : getTransactions()) {
            if (transaction.getError() != null) {
            	throw transaction.getError();
            }
            response.put(transaction.getLabel(session), transaction.getLatency());
        }
        return response;
    }

    public void start(MobileSequenceSession session, MobileMsgTracker tracker, DefaultTaskCoordinator coordinator) throws SequencerException {
        
        Assert.notNull(tracker);
        Assert.notNull(coordinator);

        computeDefaultGateways();

        SequenceTask sequence = coordinator.createSequence(null);
        for(MobileSequenceTransaction transaction : getTransactions()) {
            sequence.add(transaction.createTask(this, session, tracker, coordinator, sequence));
        }
        
        sequence.schedule();
        
        setTask(sequence);

    }

    public boolean hasFailed() {
    	
        for (MobileSequenceTransaction transaction : getTransactions()) {
            if (transaction.getError() != null) {
                return true;
            }
    	}
    	return false;
    }


}

