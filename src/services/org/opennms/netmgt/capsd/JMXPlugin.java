/*
 * Created on Feb 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package org.opennms.netmgt.capsd;

import java.net.InetAddress;
import java.util.*;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.opennms.netmgt.utils.ParameterMap;

import org.apache.log4j.Category;
import org.opennms.core.utils.ThreadCategory;

/**
 * @author mjamison
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JMXPlugin extends AbstractPlugin {
    
    private static final String PROTOCOL_NAME   = "JMX";
    private static final int    DEFAULT_PORT    = 9004;
    private static final int    DEFAULT_RETRY   = 0;
    private static final int    DEFAULT_TIMEOUT = 5000; // in milliseconds
    

    /* (non-Javadoc)
     * @see org.opennms.netmgt.capsd.Plugin#getProtocolName()
     */
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }
    /* (non-Javadoc)
     * @see org.opennms.netmgt.capsd.Plugin#isProtocolSupported(java.net.InetAddress, java.util.Map)
     */
    public boolean isProtocolSupported(InetAddress address, Map map) {
        Category log = ThreadCategory.getInstance(getClass());

        try {
            String namingPort = ParameterMap.getKeyedString(map, "NamingPort",  "9004");
            String jndiPath   = ParameterMap.getKeyedString(map, "JNDIPath",    "/jmxrmi");
            
            log.debug("JMX: service:jmx:rmi://" + address.getHostAddress() + "/jndi/rmi://" + address.getHostAddress() + ":" + namingPort + jndiPath);
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://" + address.getHostAddress() + "/jndi/rmi://" + address.getHostAddress() + ":" + namingPort + jndiPath);

            // Connect a JSR 160 JMXConnector to the server side
            JMXConnector connector = JMXConnectorFactory.connect(url);

            MBeanServerConnection connection = connector.getMBeanServerConnection();
            Integer value = (Integer)connection.getMBeanCount();
            log.debug("JMX isProtocolSupported? " + connection + " value: " + value);
            if (value == null || value.intValue() == 0) {
                return false;
            }
            
            return connection != null;
            
        } catch (Exception e) {
            log.debug("JMX isProtocolSupported - failed! " + e.getMessage());
            //e.printStackTrace();
        }

        
        return false;
    }
    /* (non-Javadoc)
     * @see org.opennms.netmgt.capsd.Plugin#isProtocolSupported(java.net.InetAddress)
     */
    public boolean isProtocolSupported(InetAddress address) {
        HashMap map = new HashMap();
        map.put("NamingPort", "9004");
        map.put("JNDIPath",   "/jmxrmi");
        
        return isProtocolSupported(address, map);
    }
}
