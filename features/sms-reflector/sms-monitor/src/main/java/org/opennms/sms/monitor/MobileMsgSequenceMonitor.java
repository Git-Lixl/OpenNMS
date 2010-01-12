package org.opennms.sms.monitor;


import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.opennms.core.tasks.DefaultTaskCoordinator;
import org.opennms.core.utils.BeanUtils;
import org.opennms.core.utils.ParameterMap;
import org.opennms.netmgt.model.PollStatus;
import org.opennms.netmgt.poller.Distributable;
import org.opennms.netmgt.poller.DistributionContext;
import org.opennms.netmgt.poller.MonitoredService;
import org.opennms.netmgt.poller.monitors.IPv4Monitor;
import org.opennms.sms.monitor.internal.SequenceException;
import org.opennms.sms.monitor.internal.config.MobileSequenceConfig;
import org.opennms.sms.monitor.internal.config.SequenceConfigFactory;
import org.opennms.sms.phonebook.Phonebook;
import org.opennms.sms.phonebook.PhonebookException;
import org.opennms.sms.ping.PingConstants;
import org.opennms.sms.reflector.smsservice.MobileMsgTracker;

@Distributable(DistributionContext.DAEMON)
public class MobileMsgSequenceMonitor extends IPv4Monitor {

    private static final String CONTEXT_NAME = "mobileMessagePollerContext";

    private static Logger log = Logger.getLogger(MobileMsgSequenceMonitor.class);

    private Phonebook m_phonebook;
	private MobileMsgTracker m_tracker;
	private DefaultTaskCoordinator m_coordinator;

	@Override
	public void initialize(Map<String,Object> params) {
		super.initialize(params);
		m_phonebook = BeanUtils.getBean(CONTEXT_NAME, "phonebook", Phonebook.class);
		m_tracker = BeanUtils.getBean(CONTEXT_NAME, "mobileMsgTracker", MobileMsgTracker.class);
		m_coordinator = BeanUtils.getBean(CONTEXT_NAME, "sequenceTaskCoordinator", DefaultTaskCoordinator.class);
	}

	@Override
	public PollStatus poll(MonitoredService svc, Map<String, Object> parameters) {

	    try {
	        
	        if (parameters.get("retry") == null) {
	            parameters.put("retry", String.valueOf(PingConstants.DEFAULT_RETRIES));
	        }
	        if (parameters.get("timeout") == null) {
	            parameters.put("timeout", String.valueOf(PingConstants.DEFAULT_TIMEOUT));
	        }
	        String config = ParameterMap.getKeyedString(parameters, "sequence", "");
	        if (config == null || "".equals(config)) {
	            return PollStatus.unavailable("Sequence configuration was empty.  You must specify a 'sequence' parameter in the SMSSequenceMonitor poller configuration!");
	        }

	        Properties session = new Properties();

	        // first, transfer anything from the parameters to the session
	        for (Map.Entry<String,Object> entry : parameters.entrySet()) {
	            if (entry.getKey() != null && entry.getValue() != null) {
	                session.put(entry.getKey(), entry.getValue());
	            }
	        }
	        session.setProperty("recipient", m_phonebook.getTargetForAddress(svc.getIpAddr()));


	        MobileSequenceConfig sequenceConfig = null;

	        SequenceConfigFactory factory = SequenceConfigFactory.getInstance();
	        sequenceConfig = factory.getSequenceForXml(config);


	        // FIXME: Decide the validity of an empty sequence; is it a failure to configure?  Or passing because no transactions failed?
	        if (sequenceConfig.getTransactions() == null || sequenceConfig.getTransactions().size() == 0) {
	            log.warn("No transactions were configured for host " + svc.getIpAddr());
	            return PollStatus.unavailable("No transactions were configured for host " + svc.getIpAddr());
	        }


	        Map<String, Number> responseTimes = new MobileMsgSequenceBuilder(sequenceConfig).execute(session, m_tracker, m_coordinator);
	        PollStatus response = PollStatus.available();
	        response.setProperties(responseTimes);
	        return response;


	    } catch (PhonebookException e) {
	        log.warn("Unable to locate recpient phone number for IP address " + svc.getIpAddr(), e);
	        return PollStatus.unavailable("Unable to find phone number for IP address " + svc.getIpAddr());
	    } catch (SequenceException e) {
	        log.warn("Unable to parse sequence configuration for host " + svc.getIpAddr(), e);
	        return PollStatus.unavailable("unable to read sequence configuration");
	    } catch (Throwable e) {
	        log.debug("Sequence failed", e);
	        return PollStatus.unavailable("Sequence failed: " + e.getLocalizedMessage());
	    } 
	}
}
