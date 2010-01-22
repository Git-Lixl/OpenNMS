package org.opennms.sms.monitor.internal.config;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatchers;
import org.smslib.USSDSessionStatus;

@XmlRootElement(name="session-status")
public class UssdSessionStatusMatcher extends SequenceResponseMatcher {

	public UssdSessionStatusMatcher() {
	}

	public UssdSessionStatusMatcher(String text) {
		setText(text);
	}
	
	public UssdSessionStatusMatcher(USSDSessionStatus status) {
		setText(status.name());
	}

	@Override
	public MobileMsgResponseMatcher getMatcher(MobileSequenceSession session) {
		USSDSessionStatus status;
		String text = session.substitute(getText());
		try {
			int statusVal = Integer.parseInt(text);
			status = USSDSessionStatus.getByNumeric(statusVal);
		} catch (NumberFormatException e) {
			status = USSDSessionStatus.valueOf(text);
		}
		return MobileMsgResponseMatchers.ussdStatusIs(status);
	}

}
