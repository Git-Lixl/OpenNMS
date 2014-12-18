package org.opennms.netmgt.poller.monitors;

import java.util.Date;
import java.util.Map;

import org.opennms.core.utils.ParameterMap;
import org.opennms.netmgt.config.BasicScheduleUtils;
import org.opennms.netmgt.config.common.BasicSchedule;
import org.opennms.netmgt.config.common.Time;
import org.opennms.netmgt.model.PollStatus;
import org.opennms.netmgt.poller.MonitoredService;

public class SnmpTimeMonitor extends SnmpMonitor {

    public PollStatus poll(MonitoredService svc, Map<String, Object> parameters) {
        String begins = ParameterMap.getKeyedString(parameters, "begins", "00:00:00");
        String ends = ParameterMap.getKeyedString(parameters, "ends", "23:59:59");
        
        Time time = new Time();
        time.setBegins(begins);
        time.setEnds(ends);
        BasicSchedule basicSchedule = new BasicSchedule();
        basicSchedule.setName("SnmpTimeMonitorSchedule");
        basicSchedule.addTime(time);
        
        if ( BasicScheduleUtils.isTimeInSchedule(new Date(), basicSchedule)) {
        	return super.poll(svc, parameters);
        }

        return PollStatus.up();
    	
    }
}
