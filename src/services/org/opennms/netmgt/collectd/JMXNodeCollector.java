/*
 * Created on Mar 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.opennms.netmgt.collectd;

import java.util.List;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.utils.Signaler;



/**
 * The JMXNodeCollector class is responsible for performing the actual JMX
 * data collection for a node over a specified network interface. The
 * JMXNodeCollector implements the SnmpHandler class in order to receive
 * notifications when an JMX reply is received or error occurs.
 * 
 * The JMXNodeCollector is provided a list of MIB objects to collect and an
 * interface over which to collect the data. Data collection can be via JMXv1
 * GetNext requests or JMXv2 GetBulk requests depending upon the parms used to
 * construct the collector.
 * 
 * @author <A HREF="mailto:mike@opennms.org">Mike </A>
 * @author <A>Jon Whetzel </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 * 
 * @version 1.1.1.1
 * 
 */
public class JMXNodeCollector { //implements SnmpHandler 
    /**
     * Flag indicating the success or failure of the informational query. If the
     * flag is set to false then either part of all of the information was
     * unable to be retreived. If it is set to true then all of the data was
     * received from the remote host.
     */
    private boolean m_error;

    /**
     * Reason that the JMX request failed. Please see
     * org.opennms.protocols.snmp.SnmpPduPacket class for a list of possible
     * JMX error codes. This variable only has meaning of m_error flag is true.
     */
    private int m_errorStatus;

    /**
     * Array of JMXv1 error strings. Please see
     * org.opennms.protocols.snmp.SnmpPduPacket class for list of JMX error
     * codes which serve as indices into this string array.
     */
    private static String[] m_errorText = { "ErrNoError", "ErrTooBig", "ErrNoSuchName", "ErrBadValue", "ErrReadOnly", "ErrGenError" };

    /**
     * If the JMX collection failed due to a problem with one or more varbinds
     * (for example if a particular object oid is requested which is not
     * implemented in the target's JMX agent) then this value will be set equal
     * to the *first* failing varbind in the request. This variable only has
     * meaning if m_error flag is true. Will be set to -1 if the JMX collection
     * failed for an unrelated reason.
     */
    private int m_errorIndex;

    /**
     * Flag indicating if the JMX collection failed due to the JMX request
     * timing out. Its value only has meaning if m_error flag is true.
     */
    private boolean m_timeout;

    /**
     * List of MibObject objects to be collected.
     */
    private List m_objList;

    /**
     * Initialized to zero. As each response PDU is received this value is
     * incremented by the number of vars contained in the response. Processing
     * will continue until this value reaches the total number of oids in the
     * MibObject list (m_olbjList) and all oid values have been retrieved.
     */
    private int m_oidListIndex;

    /**
     * Used to store the collected MIB data.
     */
    private JMXCollectorEntry m_collectorEntry;

    /**
     * Used for classifying the JMX version of the session.
     * 
     */
    private int m_version;

    /**
     * Holds the IP Address of the primary JMX iterface.
     */
    private String m_primaryIf;

    /**
     * Max number of variables permitted in a single outgoing JMX PDU request..
     */
    private int m_maxVarsPerPdu;

    /**
     * The default constructor is marked private and will always throw an
     * exception. This is done to disallow the default constructor. The reason
     * is that this object requires several arguments to perform it's duties.
     * 
     * @exception java.lang.UnsupportedOperationException
     *                Always thrown from this method since it is not supported.
     */
    private JMXNodeCollector() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Default Constructor not supported");
    }

    /**
     * The class constructor is used to initialize the collector and send out
     * the initial JMX packet requesting data. The data is then received and
     * store by the object. When all the data has been collected the passed
     * signaler object is <EM>notified</EM> using the notifyAll() method.
     * 
     * @param session
     *            The JMX session with the remote agent.
     * @param signaler
     *            The object signaled when data collection is done.
     * @param objList
     *            The list of object id's to be collected.
     * @param maxVarsPerPdu
     *            Max number of vars permitted in a single PDU
     */
    public JMXNodeCollector(List objList, int maxVarsPerPdu) {
        super();

        // Log4j category
        //
        Category log = ThreadCategory.getInstance(getClass());

        m_error = false;
        m_errorIndex = -1;
        m_timeout = false;

        m_collectorEntry = null;

        // Process parameters
        //
        /*
        m_primaryIf = session.getPeer().getPeer().getHostAddress();
        m_version = session.getPeer().getParameters().getVersion();
        m_signal = signaler;
        */
        m_objList = objList;
        m_oidListIndex = 0;
        m_maxVarsPerPdu = maxVarsPerPdu;

        if (log.isDebugEnabled())
            log.debug("SnmpNodeCollector: totalOids=" + objList.size() + " maxVarsPerPdu=" + maxVarsPerPdu);

    }

     /**
     * Returns the success or failure code for collection of the data.
     */
    public boolean failed() {
        return m_error;
    }

    /**
     * Returns true if JMX collection failed due to timeout. Otherwise, returns
     * false.
     */
    public boolean timedout() {
        if (m_error)
            return m_timeout;
        else
            return false;
    }

    /**
     * Returns the list of all entry maps that can be used to access all the
     * information from the service polling.
     */

    public JMXCollectorEntry getEntry() {

        return m_collectorEntry;
    }
}
