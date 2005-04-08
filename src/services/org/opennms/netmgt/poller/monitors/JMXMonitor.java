/*
 * Created on Feb 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.opennms.netmgt.poller.monitors;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Category;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.config.poller.Package;
import org.opennms.netmgt.utils.ParameterMap;

/**
 * @author mjamison
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JMXMonitor extends IPv4LatencyMonitor
 {
    private static final int DEFAULT_PORT = 9004;
    private static final int DEFAULT_RETRY = 0;
    private static final int DEFAULT_TIMEOUT = 3000;  // 3 sec wait for connect
    private static boolean INIT_ED = false;


    private MBeanServerConnection connection; 
    /* (non-Javadoc)
     * @see org.opennms.netmgt.poller.monitors.ServiceMonitor#poll(org.opennms.netmgt.poller.monitors.NetworkInterface, java.util.Map, org.opennms.netmgt.config.poller.Package)
     */
    public int poll(NetworkInterface iface, Map map, Package pkg) {
        try {
            String serverProtocol = ParameterMap.getKeyedString(map, "ServerProtocol", "rmi");
            String namingPort     = ParameterMap.getKeyedString(map, "NamingPort",     "9004");
            String jndiPath       = ParameterMap.getKeyedString(map, "JNDIPath",       "/jmxrmi");
            String rrdPath        = ParameterMap.getKeyedString(map, "rrd-repository",  null);
            String dsName         = ParameterMap.getKeyedString(map, "ds-name",        "jmx");
            
            Category log = ThreadCategory.getInstance(getClass());
            int retry   = ParameterMap.getKeyedInteger(map, "retry", DEFAULT_RETRY);
            int timeout = ParameterMap.getKeyedInteger(map, "timeout", DEFAULT_TIMEOUT);
            
            if (dsName == null) {
                dsName = DS_NAME;
            }
            
            InetAddress ipv4Addr = (InetAddress)iface.getAddress();
            String hostIP = ipv4Addr.getHostAddress();
            //log.debug("JMX POLLER service:jmx:rmi://host/jndi/rmi://" + hostIP + ":" + namingPort + jndiPath);

            //JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://" + hostIP + "/jndi/rmi://" + hostIP + ":" + namingPort + jndiPath);
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + hostIP + ":" + namingPort + jndiPath);
            
            // Connect a JSR 160 JMXConnector to the server side
            JMXConnector connector = JMXConnectorFactory.connect(url);
            
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            
            //if (log.isDebugEnabled())
            //    log.debug("JmxMonitor.poll: Polling interface: " + hostIP + " timeout: " + timeout + " retry: " + retry);
            
            int serviceStatus = ServiceMonitor.SERVICE_UNAVAILABLE;
            long t0 = 0;
            for (int attempts=0; attempts <= retry && serviceStatus != ServiceMonitor.SERVICE_AVAILABLE; attempts++)    {
                URL jmxLink = null;
                InputStream iStream = null;
                try {
                    
                    t0 = System.currentTimeMillis();
                    
                    Integer count = connection.getMBeanCount();
                    
                    long responseTime = System.currentTimeMillis() - t0;
                    
                    if (responseTime >= 0 && rrdPath != null) {
                        this.updateRRD(rrdPath, ipv4Addr, dsName, responseTime, pkg);
                    }
                    
                    serviceStatus = ServiceMonitor.SERVICE_AVAILABLE;
                    break;
                }      
                catch(Exception e) {
                    e.fillInStackTrace();
                    log.debug("JMXMonitor.poll: IOException while polling address: " + ipv4Addr, e);
                }
            }  // of for
            
            if (connector != null) {
            	connector.close();
            }
            connection = null;
            
            return serviceStatus;
            
        } catch (Exception e1) {
            //e1.printStackTrace();
        }
        return 0;
    }
    
}
