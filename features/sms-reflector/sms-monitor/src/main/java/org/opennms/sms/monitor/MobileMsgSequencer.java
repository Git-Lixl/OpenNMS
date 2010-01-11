/**
 * 
 */
package org.opennms.sms.monitor;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.opennms.core.tasks.DefaultTaskCoordinator;
import org.opennms.sms.monitor.internal.config.MobileSequenceConfig;
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;

public class MobileMsgSequencer {

	private DefaultTaskCoordinator m_coordinator;
	private MobileMsgTracker m_tracker;

	public MobileMsgSequencer() {
	    m_coordinator = new DefaultTaskCoordinator(Executors.newSingleThreadExecutor());
	}
	
	public void setMobileMsgTracker(MobileMsgTracker tracker) {
	    m_tracker = tracker;
	}
	
	public void setTaskCoordinator(DefaultTaskCoordinator coordinator) {
	    m_coordinator = coordinator;
	}
	
	public Map<String, Number> executeSequence(MobileSequenceConfig sequenceConfig, Properties session) throws Throwable {
        return new MobileMsgSequenceBuilder(sequenceConfig).execute(session, m_tracker, m_coordinator);
	}


}
