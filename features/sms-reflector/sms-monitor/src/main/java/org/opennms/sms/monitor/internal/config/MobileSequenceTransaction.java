package org.opennms.sms.monitor.internal.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.opennms.core.tasks.Async;
import org.opennms.core.tasks.Callback;
import org.opennms.core.tasks.DefaultTaskCoordinator;
import org.opennms.core.tasks.SequenceTask;
import org.opennms.core.tasks.Task;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;

@XmlRootElement(name="transaction")
@XmlType(propOrder={"request", "responses"})
public class MobileSequenceTransaction implements Comparable<MobileSequenceTransaction> {
    
    /* containing sequenceConfig */
    private MobileSequenceConfig m_sequenceConfig;

    /* attributes and sub elements */
    private String m_label;
	private String m_gatewayId;
	private MobileSequenceRequest m_request;
	private List<MobileSequenceResponse> m_responses;
	
    /* other data */
	private String m_defaultGatewayId;
	private Long m_latency;
	private Throwable m_error;

	public MobileSequenceTransaction() {
	}

	public MobileSequenceTransaction(String label) {
		setLabel(label);
	}
	
	public MobileSequenceTransaction(String gatewayId, String label) {
		this(label);
		setGatewayId(gatewayId);
	}
	
	@XmlAttribute(name="label")
	public String getLabel() {
		return m_label;
	}
	
	public void setLabel(String label) {
		m_label = label;
	}

    @XmlAttribute(name="gatewayId", required=false)
    public String getGatewayId() {
        return m_gatewayId;
    }

	public void setGatewayId(String gatewayId) {
		m_gatewayId = gatewayId;
	}

	@XmlElementRef
	public MobileSequenceRequest getRequest() {
		return m_request;
	}
	
	public void setRequest(MobileSequenceRequest request) {

	    if (m_request != null) {
	        m_request.setTransaction(null);
	    }

	    m_request = request;

		if (request != null) {
		    request.setTransaction(this);
		}

	}
	
	@XmlElementRef
	public List<MobileSequenceResponse> getResponses() {
	    if (m_responses == null) {
	        m_responses = createResponsesList();
	    }
		return m_responses;
	}

	private List<MobileSequenceResponse> createResponsesList() {
	    return new TriggeredList<MobileSequenceResponse>() {

            @Override
            protected void onAdd(int index, MobileSequenceResponse element) {
                element.setTransaction(MobileSequenceTransaction.this);
            }

            @Override
            protected void onRemove(int index, MobileSequenceResponse element) {
                element.setTransaction(null);
            }
	        
	    };
    }

    public synchronized void setResponses(List<MobileSequenceResponse> responses) {
		m_responses.clear();
		m_responses.addAll(responses);
	}
	
	public void addResponse(MobileSequenceResponse response) {
		getResponses().add(response);
		
	}

	@XmlTransient
	public MobileSequenceConfig getSequenceConfig() {
	    return m_sequenceConfig;
	}

	public void setSequenceConfig(MobileSequenceConfig sequenceConfig) {
	    m_sequenceConfig = sequenceConfig;
	}
	
    @XmlTransient
    public String getDefaultGatewayId() {
        return m_defaultGatewayId;
    }
    
    public void setDefaultGatewayId(String gatewayId) {
        m_defaultGatewayId = gatewayId;
    }
    
    @XmlTransient
    public Long getLatency() {
        return m_latency;
    }

    public void setLatency(Long latency) {
        m_latency = latency;
    }

    @XmlTransient
    public Throwable getError() {
        return m_error;
    }

    public void setError(Throwable error) {
        m_error = error;
    }


    public String getLabel(MobileSequenceSession session) {
        return session.substitute(getRequest().getLabel(getLabel()));
    }

    public MobileMsgResponseMatcher getResponseMatcher(MobileSequenceSession session) {
        
        MobileMsgResponseMatcher match =null;

        for ( MobileSequenceResponse r : getResponses() ) {
            match = r.getResponseMatcher(session);
        }

        return match;
    }

	private Callback<MobileMsgResponse> getCallback(final MobileSequenceSession session) {
        return new Callback<MobileMsgResponse>() {
            public void complete(MobileMsgResponse t) {
                if (t != null) {
                    setLatency((t.getReceiveTime() - t.getRequest().getSentTime()));
                }
                
                //session.setVariable()
            }

            public void handleException(Throwable t) {
                setError(t);
            }


        };
    }

    public Async<MobileMsgResponse> createAsync(MobileSequenceSession session) {
        return getRequest().createAsync(session);
    }

    public Task createTask(MobileSequenceSession session, DefaultTaskCoordinator coordinator, SequenceTask sequence) {
        return coordinator.createTask(sequence, createAsync(session), getCallback(session));
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

}
