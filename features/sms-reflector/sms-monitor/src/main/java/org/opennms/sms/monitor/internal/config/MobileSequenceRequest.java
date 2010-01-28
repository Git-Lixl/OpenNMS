package org.opennms.sms.monitor.internal.config;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.opennms.core.tasks.Async;
import org.opennms.core.tasks.Callback;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.reflector.smsservice.MobileMsgCallbackAdapter;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;

@XmlRootElement(name="request")
public abstract class MobileSequenceRequest extends MobileSequenceOperation {
    private MobileSequenceTransaction m_transaction;
	private String m_text;

	public MobileSequenceRequest() {
		super();
	}
	
	public MobileSequenceRequest(String label, String text) {
		super(label);
		setText(text);
	}

	public MobileSequenceRequest(String gatewayId, String label, String text) {
		super(gatewayId, label);
		setText(text);
	}

	@XmlAttribute(name="text")
	public String getText() {
		return m_text;
	}
	
	public void setText(String text) {
		m_text = text;
	}
	
	@XmlTransient
	public MobileSequenceTransaction getTransaction() {
	    return m_transaction;
	}
	
	public void setTransaction(MobileSequenceTransaction transaction) {
	    m_transaction = transaction;
	}
	
	public String getGatewayId(String defaultGatewayId) {
		return getGatewayId() == null? defaultGatewayId : getGatewayId();
	}

	public String getLabel(String defaultLabel) {
		return getLabel() == null ? defaultLabel : getLabel();
	}

    protected String getGatewayIdForRequest() {
        return getGatewayId(getTransaction().getDefaultGatewayId());
    }

    public abstract void send(final MobileSequenceSession session, MobileMsgResponseMatcher responseMatcher, MobileMsgCallbackAdapter mmrc);

    public Async<MobileMsgResponse> createAsync(final MobileSequenceSession session) {
        return new Async<MobileMsgResponse>() {
            public void submit(Callback<MobileMsgResponse> cb) {
                doSubmit(session, cb);
            }
        };
    }

    public void doSubmit(final MobileSequenceSession session, Callback<MobileMsgResponse> cb) {
    
        if (getTransaction().getSequenceConfig().hasFailed()) {
        	cb.complete(null);
        }
        
        send(session, getTransaction().getResponseMatcher(session), new MobileMsgCallbackAdapter(cb));
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("gatewayId", getGatewayId())
            .append("label", getLabel())
            .append("text", getText())
            .toString();
    }


}
