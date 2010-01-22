package org.opennms.sms.monitor.internal.config;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatchers;

@XmlRootElement(name="validate-source")
public class SmsSourceMatcher extends SequenceResponseMatcher {

	public SmsSourceMatcher() {
		super();
	}
	
	public SmsSourceMatcher(String originator) {
		super(originator);
	}

	@Override
	public MobileMsgResponseMatcher getMatcher(MobileSequenceSession session) {
		return MobileMsgResponseMatchers.smsFrom(session.substitute(getText()));
	}

}
