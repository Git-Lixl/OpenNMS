/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.opennms.netmgt.collectd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetAddress;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.EventConstants;
import org.opennms.netmgt.config.BeanInfo;
import org.opennms.netmgt.config.DatabaseConnectionFactory;
import org.opennms.netmgt.config.JMXDataCollectionConfigFactory;
import org.opennms.netmgt.config.collectd.Attrib;
import org.opennms.netmgt.poller.monitors.NetworkInterface;
import org.opennms.netmgt.rrd.RRDDataSource;
import org.opennms.netmgt.rrd.RrdException;
import org.opennms.netmgt.rrd.RrdUtils;
import org.opennms.netmgt.utils.EventProxy;
import org.opennms.netmgt.utils.ParameterMap;
import org.opennms.netmgt.xml.event.Event;
/**
 * <P>
 * The SnmpCollector class ...
 * </P>
 * 
 * @author <A HREF="mailto:mike@opennms.org">Mike Davidson </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 * 
 */
final class JMXCollector implements ServiceCollector {
    
    /**
     * Name of monitored service.
     */
    private static final String SERVICE_NAME = "JMX";

    /**
     * SQL statement to retrieve interface's 'ipinterface' table information.
     */
    private static final String SQL_GET_NODEID = "SELECT nodeid FROM ipinterface WHERE ipaddr=? AND ismanaged!='D'"; 

    /**
     * /** SQL statement to retrieve node's system object id.
     */
    private static final String SQL_GET_NODESYSOID = "SELECT nodesysoid FROM node WHERE nodeid=? AND nodetype!='A'"; // we need to add this
    
    private static final String SQL_GET_ISSNMPPRIMARY = "SELECT ifindex,issnmpprimary FROM ipinterface WHERE nodeid=?";

    
    /**
     * RRD data source name max length.
     */
    private static final int MAX_DS_NAME_LENGTH = 19;

    /**
     * This defines the default maximum number of variables the collector is
     * permitted to pack into a single outgoing PDU. This value is intentionally
     * kept relatively small in order to communicate successfully with the
     * largest possible number of agents.
     */
    private static int DEFAULT_MAX_VARS_PER_PDU = 30;
    
    private static String JMX_PEER_KEY="????????";  // TODO
    
    private static String DEFAULT_OBJECT_IDENTIFIER=""; //TODO
    
    private static String IF_MAP_KEY=""; // TODO

    /**
     * Max number of variables permitted in a single outgoing JMX request..
     */
    private int m_maxVarsPerPdu;

    /**
     * Path to SNMP RRD file repository.
     */
    private String m_rrdPath;

    /**
     * Local host name
     */
    private String m_host;

    /* -------------------------------------------------------------- */
    /* Attr key names */
    /* -------------------------------------------------------------- */

    /**
     * Interface attribute key used to store a JMXNodeInfo object which holds data
     * about the node being polled.
     */
    static String NODE_INFO_KEY = "org.opennms.netmgt.collectd.JmxCollector.nodeInfo";

    /**
     * Interface attribute key used to store configured value for the maximum
     * number of variables permitted in a single outgoing SNMP PDU request.
     */
    static String MAX_VARS_PER_PDU_STORAGE_KEY = "org.opennms.netmgt.collectd.JMXCollector.maxVarsPerPdu";

    /**
     * <P>
     * Returns the name of the service that the plug-in collects ("SNMP").
     * </P>
     * 
     * @return The service that the plug-in collects.
     */
    public String serviceName() {
        return SERVICE_NAME;
    }

    /**
     * <P>
     * Initialize the service collector.
     * </P>
     * 
     * <P>
     * During initialization the JMX collector:
     *  - Initializes various configuration factories. - Verifies access to the
     * database - Verifies access to RRD file repository - Verifies access to
     * JNI RRD shared library - Determines if JMX to be stored for only the
     * node'sprimary interface or for all interfaces.
     * </P>
     * 
     * @param parameters
     *            Not currently used.
     * 
     * @exception RuntimeException
     *                Thrown if an unrecoverable error occurs that prevents the
     *                plug-in from functioning.
     * 
     */
    public void initialize(Map parameters) {
        // Log4j category
        //
        Category log = ThreadCategory.getInstance(getClass());

        // Get local host name (used when generating threshold events)
        //
/*
        try {
            m_host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            if (log.isEnabledFor(Priority.WARN))
                log.warn("initialize: Unable to resolve local host name.", e);
            m_host = "unresolved.host";
        }
*/
        // Initialize the JMXDataCollectionConfigFactory
        try {
            JMXDataCollectionConfigFactory.reload();

        } catch (MarshalException ex) {
            if (log.isEnabledFor(Priority.FATAL))
                log.fatal("initialize: Failed to load data collection configuration", ex);
            throw new UndeclaredThrowableException(ex);
        } catch (ValidationException ex) {
            if (log.isEnabledFor(Priority.FATAL))
                log.fatal("initialize: Failed to load data collection configuration", ex);
            throw new UndeclaredThrowableException(ex);
        } catch (IOException ex) {
            if (log.isEnabledFor(Priority.FATAL))
                log.fatal("initialize: Failed to load data collection configuration", ex);
            throw new UndeclaredThrowableException(ex);
        }

        // Make sure we can connect to the database
        //
        java.sql.Connection ctest = null;
        try {
            DatabaseConnectionFactory.init();
            ctest = DatabaseConnectionFactory.getInstance().getConnection();
        } catch (IOException ie) {
            if (log.isEnabledFor(Priority.FATAL))
                log.fatal("initialize: IOException getting database connection", ie);
            throw new UndeclaredThrowableException(ie);
        } catch (MarshalException me) {
            if (log.isEnabledFor(Priority.FATAL))
                log.fatal("initialize: Marshall Exception getting database connection", me);
            throw new UndeclaredThrowableException(me);
        } catch (ValidationException ve) {
            if (log.isEnabledFor(Priority.FATAL))
                log.fatal("initialize: Validation Exception getting database connection", ve);
            throw new UndeclaredThrowableException(ve);
        } catch (SQLException sqlE) {
            if (log.isEnabledFor(Priority.FATAL))
                log.fatal("initialize: Failed getting connection to the database.", sqlE);
            throw new UndeclaredThrowableException(sqlE);
        } catch (ClassNotFoundException cnfE) {
            if (log.isEnabledFor(Priority.FATAL))
                log.fatal("initialize: Failed loading database driver.", cnfE);
            throw new UndeclaredThrowableException(cnfE);
        } finally {
            if (ctest != null) {
                try {
                    ctest.close();
                } catch (Throwable t) {
                    if (log.isEnabledFor(Priority.WARN))
                        log.warn("initialize: an exception occured while closing the JDBC connection", t);
                }
            }
        }

        // Get path to RRD repository
        //
        m_rrdPath = JMXDataCollectionConfigFactory.getInstance().getRrdRepository();
        //System.out.println("*** m_rrdPath: " + m_rrdPath);
        if (m_rrdPath == null)
            throw new RuntimeException("Configuration error, failed to retrieve path to RRD repository.");

        // TODO: make a path utils class that has the below in it
        // Strip the File.separator char off of the end of the path
        if (m_rrdPath.endsWith(File.separator)) {
            m_rrdPath = m_rrdPath.substring(0, (m_rrdPath.length() - File.separator.length()));
        }
        if (log.isDebugEnabled())
            log.debug("initialize: JMX RRD file repository path: " + m_rrdPath);

        // If the RRD file repository directory does NOT already exist, create
        // it.
        //
        File f = new File(m_rrdPath);
        if (!f.isDirectory())
            if (!f.mkdirs())
                throw new RuntimeException("Unable to create RRD file repository, path: " + m_rrdPath);

        try {
            RrdUtils.initialize();
        } catch (RrdException e) {
            if (log.isEnabledFor(Priority.ERROR))
                log.error("initialize: Unable to initialize RrdUtils", e);
            throw new RuntimeException("Unable to initialize RrdUtils", e);
        }

        // Save local reference to singleton instance
        //
        // m_rrdInterface = org.opennms.netmgt.rrd.Interface.getInstance();
        if (log.isDebugEnabled())
            log.debug("initialize: successfully instantiated JNI interface to RRD...");

        return;
    }

    /**
     * Responsible for freeing up any resources held by the collector.
     */
    public void release() {
        // Nothing to release...
    }

    /**
     * Responsible for performing all necessary initialization for the specified
     * interface in preparation for data collection.
     * 
     * @param iface
     *            Network interface to be prepped for collection.
     * @param parameters
     *            Key/value pairs associated with the package to which the
     *            interface belongs..
     * 
     */
    public void initialize(NetworkInterface iface, Map parameters) {
        Category log = ThreadCategory.getInstance(getClass());

        InetAddress ipAddr = (InetAddress) iface.getAddress();
        if (log.isDebugEnabled())
            log.debug("initialize: InetAddress=" + ipAddr.getHostAddress());

        // Retrieve the name of the JMX data collector
        String collectionName = ParameterMap.getKeyedString(parameters, "collection", "default");
        if (log.isDebugEnabled())
            log.debug("initialize: collectionName=" + collectionName);

        java.sql.Connection dbConn = null;
        try {
            dbConn = DatabaseConnectionFactory.getInstance().getConnection();
        } catch (SQLException sqlE) {
            if (log.isEnabledFor(Priority.ERROR))
                log.error("initialize: Failed getting connection to the database.", sqlE);
            throw new UndeclaredThrowableException(sqlE);
        }

        int nodeID = -1;
        // Prepare & execute the SQL statement to get the 'nodeid' from the
        // ipInterface table 'nodeid' will be used to retrieve the node's
        // system object id from the node table.
        // In addition to nodeid, the interface's ifIndex and isSnmpPrimary
        // fields are also retrieved.
        //
        PreparedStatement stmt = null;
        try {
            stmt = dbConn.prepareStatement(SQL_GET_NODEID);
            stmt.setString(1, ipAddr.getHostAddress()); // interface address
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nodeID = rs.getInt(1);
                if (rs.wasNull())
                    nodeID = -1;
            } else {
                nodeID = -1;
            }

            rs.close();
        } catch (SQLException sqle) {
            if (log.isDebugEnabled())
                log.debug("initialize: SQL exception!!", sqle);
            throw new RuntimeException("SQL exception while attempting to retrieve node id for interface " + ipAddr.getHostAddress());
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                // Ignore
            }
        }
        
        JMXNodeInfo nodeInfo = new JMXNodeInfo(nodeID, 1);

        // Retrieve list of MBeab objects to be collected from the
        // remote agent which are to be stored in the node-level RRD file.
        // These objects pertain to the node itself not any individual
        // interfaces.
        List attrList = JMXDataCollectionConfigFactory.getInstance().getAttributeList(collectionName, "foo", ipAddr.getHostAddress());
        nodeInfo.setAttributeList(attrList);
        HashMap dsList = buildDataSourceList(collectionName, attrList);
        nodeInfo.setDsMap(dsList);
        nodeInfo.setMBeans(JMXDataCollectionConfigFactory.getInstance().getMBeanInfo(collectionName));

        // Add the JMXNodeInfo object as an attribute of the interface
        //
        
        iface.setAttribute(NODE_INFO_KEY, nodeInfo);
        iface.setAttribute("collectionName", collectionName);
    }

    /**
     * Responsible for releasing any resources associated with the specified
     * interface.
     * 
     * @param iface
     *            Network interface to be released.
     */
    public void release(NetworkInterface iface) {
        // Nothing to release...
    }

    /**
     * Perform data collection.
     * 
     * @param iface
     *            Network interface to be data collected.
     * @param eproxy
     *            Eventy proxy for sending events.
     * @param parameters
     *            Key/value pairs from the package to which the interface
     *            belongs.
     */
    public int collect(NetworkInterface iface, EventProxy eproxy, Map map) {
        Category log = ThreadCategory.getInstance(getClass());
        log.debug("JMX: collect " + map);

        InetAddress ipaddr = (InetAddress) iface.getAddress();
        String collectionName = (String)iface.getAttribute("collectionName");
        
        JMXNodeInfo nodeInfo = (JMXNodeInfo)iface.getAttribute(NODE_INFO_KEY);
        HashMap list = nodeInfo.getDsMap();
        HashMap mbeans = nodeInfo.getMBeans();

        try {
            String serverProtocol = ParameterMap.getKeyedString(map, "ServerProtocol", "rmi");
            String namingPort     = ParameterMap.getKeyedString(map, "NamingPort",     "9004");
            String jndiPath       = ParameterMap.getKeyedString(map, "JNDIPath",       "/jmxrmi");
            String rrdPath        = ParameterMap.getKeyedString(map, "rrdRepository",  null);
            
            int retry   = ParameterMap.getKeyedInteger(map, "retry", 3);
            int timeout = ParameterMap.getKeyedInteger(map, "timeout", 3000);
            
            log.debug("namingPort: " + namingPort + " rrdPath: " + rrdPath);
            
            
            InetAddress ipv4Addr = (InetAddress)iface.getAddress();
            String hostIP = ipv4Addr.getHostAddress();
            log.debug("JMX POLLER service:jmx:rmi://host/jndi/rmi://" + hostIP + ":" + namingPort + jndiPath);

            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://" + hostIP + "/jndi/rmi://" + hostIP + ":" + namingPort + jndiPath);
            
            // Connect a JSR 160 JMXConnector to the server side
            JMXConnector connector = JMXConnectorFactory.connect(url);
            
            MBeanServerConnection connection = connector.getMBeanServerConnection();
           log.debug("MBeanServerConnection: " + connection); 
            if (log.isDebugEnabled())
                log.debug("JmxMonitor.poll: Polling interface: " + hostIP + " timeout: " + timeout + " retry: " + retry);
           
            int serviceStatus = COLLECTION_FAILED;
            for (int attempts=0; attempts <= retry; attempts++)    {
                URL jmxLink = null;
                InputStream iStream = null;
                try {    
                	/*
                	 * Iterate over the mbeans, for each object name perform a getAttributes,
                	 * the update the RRD.
                	 */
                    for (Iterator iter = mbeans.values().iterator(); iter.hasNext();) {
                    	BeanInfo beanInfo = (BeanInfo)iter.next();
                        String objectName = beanInfo.getObjectName();
                        String[] attrNames = beanInfo.getAttributeNames();
                        
                        log.debug("JMXCollector - getAttributes: " + objectName + " #attributes: " + attrNames.length);
                        AttributeList attrList = null;

                        try {
                            attrList = (AttributeList)connection.getAttributes(new ObjectName(objectName), attrNames);
                            updateRRDs(collectionName, iface, attrList);
                        } catch (InstanceNotFoundException e2) {
                            e2.printStackTrace();                        
                        }
                        serviceStatus = COLLECTION_SUCCEEDED;
                    }                    
                    break;
                }      
                catch(Exception e) {
                    e.fillInStackTrace();
                    log.debug("JMXCollector.collect: IOException while collect address: " + ipv4Addr, e);
                }
            }  // of for
            connection = null;
            if (connector != null) {
                connector.close();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
        // Retrieve max vars per pdu attribute
        //
        Integer maxVarsPerPdu = (Integer) iface.getAttribute(MAX_VARS_PER_PDU_STORAGE_KEY);

        // Retrieve this interface's JMX peer object
        //
        //
        // return the status of the collection
        //
        return COLLECTION_SUCCEEDED;
    }

    /**
     * Creates a single RRD file for the specified RRD data source.
     * 
     * @param collectionName
     *            Name of the collection
     * @param ipaddr
     *            Interface address
     * @param directory
     *            RRD repository directory
     * @param ds
     *            RRD data source
     * 
     * @return TRUE if new RRD file created, FALSE if RRD file was not created
     *         because it already existed.
     */
    public boolean createRRD(String collectionName, InetAddress ipaddr, String directory, RRDDataSource ds) throws RrdException {
    	//System.out.println("createRRD: " + collectionName + " DS: " + ds.getName());
        String creator = "primary JMX interface " + ipaddr.getHostAddress();
        int step = JMXDataCollectionConfigFactory.getInstance().getStep(collectionName);
        List rraList = JMXDataCollectionConfigFactory.getInstance().getRRAList(collectionName);
        
        //for (int i = 0; i < rraList.size(); i++) {
        //	System.out.println("CREATE RRD: " + rraList.get(i));
        //}

        return RrdUtils.createRRD(creator, directory, ds.getName(), step, ds.getType(), ds.getHeartbeat(), ds.getMin(), ds.getMax(), rraList);
    }

    /**
     * This method is responsible for building an RRDTool style 'update' command
     * which is issued via the RRD JNI interface in order to push the latest
     * JMX-collected values into the interface's RRD database.
     * 
     * @param collectionName
     *            JMX data Collection name from 'datacollection-config.xml'
     * @param iface
     *            NetworkInterface object of the interface currently being
     *            polled
     * @param nodeCollector
     *            Node level MBeab data collected via JMX for the polled
     *            interface
     * @param ifCollector
     *            Interface level MBeab data collected via JMX for the polled
     *            interface
     * 
     * @exception RuntimeException
     *                Thrown if the data source list for the interface is null.
     */
    private boolean updateRRDs(String collectionName, NetworkInterface iface, AttributeList attributeList) {//, SnmpIfCollector ifCollector) {;
        //System.out.println("JMX: updateRRDs: " + collectionName);
        
        // Log4j category
        //
        Category log = ThreadCategory.getInstance(getClass());

        InetAddress ipaddr = (InetAddress) iface.getAddress();

        // Get primary interface index from JMXNodeInfo object
        JMXNodeInfo nodeInfo = (JMXNodeInfo) iface.getAttribute(NODE_INFO_KEY);
        Integer primaryIfIndex = new Integer(nodeInfo.getPrimarySnmpIfIndex());

        // Retrieve interface map attribute
        //
        Map ifMap = (Map) iface.getAttribute(IF_MAP_KEY);

        // Write relevant collected JMX statistics to RRD database
        // 
        // First the node level RRD info will be updated.
        // Secondly the interface level RRD info will be updated.
        //
        boolean rrdError = false;

        // -----------------------------------------------------------
        // Node data
        // -----------------------------------------------------------
        //if (nodeCollector != null) {
            log.debug("updateRRDs: processing node-level collection...");

            // Build path to node RRD repository. createRRD() will make the
            // appropriate directories if they do not already exist.
            //
            String nodeRepository = m_rrdPath + File.separator + String.valueOf(nodeInfo.getNodeId());

            //JMXCollectorEntry nodeEntry = nodeCollector.getEntry();

            // Iterate over the node datasource list and issue RRD update
            // commands to update each datasource which has a corresponding
            // value in the collected JMX data
            //
            HashMap dsMap = nodeInfo.getDsMap();
            
            try {
                for (int i = 0; i < attributeList.size();i++) {
                    Attribute attribute = (Attribute)attributeList.get(i);
                    RRDDataSource ds = (RRDDataSource)dsMap.get(attribute.getName());

                    createRRD(collectionName, ipaddr, nodeRepository, ds);
                    RrdUtils.updateRRD(ipaddr.getHostAddress(), nodeRepository, ds.getName(), "" + attribute.getValue());
                }
            } catch (RrdException e) {
                e.printStackTrace();
                rrdError = true;
            }
        return rrdError;
        
        //return true;
    }

    /**
     * @param ds
     * @param collectorEntry
     * @param log
     * @param dsVal
     * @return
     * @throws Exception
     */
    public String getRRDValue(RRDDataSource ds, JMXCollectorEntry collectorEntry) throws IllegalArgumentException {
        Category log = ThreadCategory.getInstance(getClass());
        String dsVal = null;

        // Make sure we have an actual object id value.
        if (ds.getOid() == null)
            return null;
        
        return (String) collectorEntry.get(ds.getOid());

    }

    /**
     * This method is responsible for building a list of RRDDataSource objects
     * from the provided list of MBeabObject objects.
     * 
     * @param collectionName
     *            Collection name
     * @param oidList
     *            List of MBeabObject objects defining the oid's to be collected
     *            via JMX.
     * 
     * @return list of RRDDataSource objects
     */
    private HashMap buildDataSourceList(String collectionName, List attributeList) {
        // Log4j category
        //
        Category log = ThreadCategory.getInstance(getClass());

        // Retrieve the RRD expansion data source list which contains all
        // the expansion data source's. Use this list as a basis
        // for building a data source list for the current interface.
        //
        HashMap dsList = new HashMap();

        // Loop through the MBeab object list to be collected for this interface
        // and add a corresponding RRD data source object. In this manner
        // each interface will have RRD files create which reflect only the data
        // sources pertinent to it.
        //
        Iterator o = attributeList.iterator();
        while (o.hasNext()) {
            Attrib attr = (Attrib) o.next();
            
            RRDDataSource ds = null;

            // Verify that this object has an appropriate "integer" data type
            // which can be stored in an RRD database file (must map to one of
            // the supported RRD data source types: COUNTER or GAUGE).
            String ds_type = RRDDataSource.mapType(attr.getType());
            if (ds_type != null) {
                // Passed!! Create new data source instance for this MBeab object
                // Assign heartbeat using formula (2 * step) and hard code
                // min & max values to "U" ("unknown").
                ds = new RRDDataSource();
                ds.setHeartbeat(2 * JMXDataCollectionConfigFactory.getInstance().getStep(collectionName));
                // For completeness, adding a minval option to the variable.

                String ds_minval = attr.getMinval();
                if (ds_minval == null) {
                    ds_minval = "U";
                }
                ds.setMax(ds_minval);

                // In order to handle counter wraps, we need to set a max
                // value for the variable.

                String ds_maxval = attr.getMaxval();
                if (ds_maxval == null) {
                    ds_maxval = "U";
                }
                ds.setMax(ds_maxval);
                
                ds.setInstance(collectionName);

                // Truncate MBeab object name/alias if it exceeds 19 char max for
                // RRD data source names.
                String ds_name = attr.getAlias();
                if (ds_name.length() > MAX_DS_NAME_LENGTH) {
                    if (log.isEnabledFor(Priority.WARN))
                        log.warn("buildDataSourceList: alias '" + attr.getAlias() + "' exceeds 19 char maximum for RRD data source names, truncating.");
                    char[] temp = ds_name.toCharArray();
                    ds_name = String.copyValueOf(temp, 0, MAX_DS_NAME_LENGTH);
                }
                ds.setName(ds_name);

                // Map MBeab object data type to RRD data type
                ds.setType(ds_type);

                // Assign the data source object identifier and instance
                //ds.setName(attr.getName());
                ds.setOid(attr.getName());
               
                if (log.isDebugEnabled())
                    log.debug("buildDataSourceList: ds_name: " + ds.getName() + " ds_oid: " + ds.getOid() + "." + ds.getInstance() + " ds_max: " + ds.getMax() + " ds_min: " + ds.getMin());

                // Add the new data source to the list
                dsList.put(attr.getName(),ds);
            } else if (log.isEnabledFor(Priority.WARN)) {
                log.warn("buildDataSourceList: Data type '" + attr.getType() + "' not supported.  Only integer-type data may be stored in RRD.");
                log.warn("buildDataSourceList: MBeab object '" + attr.getAlias() + "' will not be mapped to RRD data source.");
            }
        }

        return dsList;
    }

    /**
     * This method is responsible for building a Capsd forceRescan event object
     * and sending it out over the EventProxy.
     * 
     * @param ifAddress
     *            interface address to which this event pertains
     * @param eventProxy
     *            proxy over which an event may be sent to eventd
     */
    private void generateForceRescanEvent(String ifAddress, EventProxy eventProxy) {
        // Log4j category
        //
        Category log = ThreadCategory.getInstance(getClass());

        if (log.isDebugEnabled())
            log.debug("generateForceRescanEvent: interface = " + ifAddress);

        // create the event to be sent
        Event newEvent = new Event();

        newEvent.setUei(EventConstants.FORCE_RESCAN_EVENT_UEI);

        newEvent.setSource("JMXServiceMonitor");

        newEvent.setInterface(ifAddress);

        newEvent.setService(SERVICE_NAME);

        if (m_host != null)
            newEvent.setHost(m_host);

        newEvent.setTime(EventConstants.formatToString(new java.util.Date()));

        // Send event via EventProxy
        try {
            eventProxy.send(newEvent);
        } catch (Exception e) {
            if (log.isEnabledFor(Priority.ERROR))
                log.error("generateForceRescanEvent: Unable to send forceRescan event.", e);
        }
    }

}
