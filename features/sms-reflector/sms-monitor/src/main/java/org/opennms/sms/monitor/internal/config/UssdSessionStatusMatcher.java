package org.opennms.sms.monitor.internal.config;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.core.utils.LogUtils;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.reflector.smsservice.MobileMsgRequest;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatchers;
import org.opennms.sms.reflector.smsservice.UssdResponse;
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
	public MobileMsgResponseMatcher getMatcher(final MobileSequenceSession session) {

		return new MobileMsgResponseMatcher() {
			
			public boolean matches(MobileMsgRequest request, MobileMsgResponse response) {

				LogUtils.tracef(this, "ussdStatusIs(%s, %s)", getText(), request, response);
				return response instanceof UssdResponse && session.ussdStatusMatches(getText(), ((UssdResponse)response).getSessionStatus());
				
			}

			public String toString() {
				return "ussdStatusIs(" + getText() + ")";
			}
		};
	}

}
