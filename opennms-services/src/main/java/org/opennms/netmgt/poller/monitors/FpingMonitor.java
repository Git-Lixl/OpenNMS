//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2007 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//
// Tab Size = 8
//

package org.opennms.netmgt.poller.monitors;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;
import org.opennms.core.utils.CollectionMath;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.model.PollStatus;
import org.opennms.netmgt.ping.Pinger;
import org.opennms.netmgt.poller.Distributable;
import org.opennms.netmgt.poller.DistributionContext;
import org.opennms.netmgt.poller.MonitoredService;
import org.opennms.netmgt.poller.NetworkInterface;
import org.opennms.netmgt.poller.NetworkInterfaceNotSupportedException;
import org.opennms.netmgt.utils.ParameterMap;

/**
 * <P>
 * This class is designed to be used by the service poller framework to test the
 * availability of the ICMP service on remote interfaces. The class implements
 * the ServiceMonitor interface that allows it to be used along with other
 * plug-ins by the service poller framework.
 * It makes use of the external application fping.
 * </P>
 * 
 * @author <A HREF="mailto:jonathan.oddy@truphone.com">Jonathan Oddy</A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS</A>
 * 
 */

@Distributable
public class FpingMonitor extends StrafePingMonitor {
	
	private static final String DEFAULT_FPING_PATH = new String("/usr/bin/fping");
	private String fping_path = null;
	
    public FpingMonitor() throws IOException {
		super();
	}

	protected ArrayList<Number> getResponseTimes(InetAddress host, int count, long timeout, long pingInterval) throws Exception {
    	Process fping_proc = (Runtime.getRuntime()).exec(fping_path+" -q -B1 -C"+count+" -t"+timeout+" -i"+pingInterval+" -p"+pingInterval+" "+host.getHostAddress());
    	InputStream fpis = fping_proc.getErrorStream();
    	fping_proc.waitFor();
    	
    	byte[] opbar = new byte[255];
    	String fping_output = new String();
    	while (fpis.read(opbar) >= 0) {
    		fping_output += new String(opbar);
    	}
    	
    	if (fping_proc.exitValue() >= 2) {
    			throw new Exception("Error running fping: \""+fping_output+"\"");
    	}
    	
    	// Should now have a string containing the output of fping
    	String[] fpingres = fping_output.split(" ");
    	if (!fpingres[0].equals(host.getHostAddress()) || fpingres.length != count+2) {
    		throw new Exception("Couldn't parse the output of fping: \""+fping_output+"\"");
    	}
    	
    	ArrayList<Number> resarr = new ArrayList<Number>();
    	for(int i = 2; i<fpingres.length; i++) {
    		if (fpingres[i].equals("-")) {
    			resarr.add(null);
    		} else {
    			Double resmicro = new Double(Double.parseDouble(fpingres[i])*1000);
    			resarr.add(new Long(resmicro.longValue()));
    		}
    	}
    	
    	return resarr;
    }
	
    public PollStatus poll(MonitoredService svc, Map parameters) {
    	if (fping_path == null) {
    		fping_path = ParameterMap.getKeyedString(parameters, "fping", DEFAULT_FPING_PATH);
    	}
    	return super.poll(svc, parameters);
    }
}