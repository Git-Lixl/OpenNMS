package org.opennms.sms.monitor.internal.config;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.core.utils.LogUtils;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.reflector.smsservice.MobileMsgRequest;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;
import org.opennms.sms.reflector.smsservice.MobileMsgResponseMatcher;

@XmlRootElement(name="matches")
public class TextResponseMatcher extends SequenceResponseMatcher {

	public TextResponseMatcher() {
	}

	public TextResponseMatcher(String text) {
		this();
		setText(text);
	}

	@Override
	public MobileMsgResponseMatcher getMatcher(final MobileSequenceSession session) {
		return new MobileMsgResponseMatcher() {
			
			public boolean matches(MobileMsgRequest request, MobileMsgResponse response) {
				LogUtils.tracef(this, "textMatches(%s, %s, %s)", session.substitute(getText()), request, response);
				return session.matches(getText(), response.getText());
			}

			public String toString() {
				return "textMatches(\"" + getText() + "\")";
			}
		};
	}

}
