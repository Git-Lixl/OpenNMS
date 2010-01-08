/**
 * 
 */
package org.opennms.sms.monitor;

import static org.opennms.core.utils.LogUtils.debugf;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.opennms.core.tasks.DefaultTaskCoordinator;
import org.opennms.core.utils.PropertiesUtils;
import org.opennms.sms.monitor.internal.config.MobileSequenceConfig;
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;

public class MobileMsgSequencer {
	private static MobileMsgTracker s_tracker;
	private static DefaultTaskCoordinator s_coordinator;
	private static boolean m_initialized = false;

	public synchronized static void initialize() {
		if (!m_initialized) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			s_coordinator = new DefaultTaskCoordinator(executor);
		    if (s_tracker == null) {
		    	throw new IllegalStateException("MobileMsgSequencer not yet initialized!!"); 
		    }
		    m_initialized = true;
		}
	}

	public synchronized static void setMobileMsgTracker(MobileMsgTracker tracker) {
	    debugf(MobileMsgSequencer.class, "Initializing MobileMsgSequencer with tracker %s", tracker);
	    s_tracker = tracker;
	}

	
	public static Map<String,Number> executeSequence(MobileSequenceConfig sequenceConfig, Properties session) throws Throwable {
		initialize();
        return new MobileMsgSequenceBuilder(sequenceConfig).execute(session, s_tracker, s_coordinator);
	}

}
