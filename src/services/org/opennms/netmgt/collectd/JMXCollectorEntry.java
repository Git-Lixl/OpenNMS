/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.opennms.netmgt.collectd;

import java.util.*;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.opennms.core.utils.ThreadCategory;

/**
 * @author mjamison
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JMXCollectorEntry extends TreeMap {
    
    /**
     * <P>
     * Creates a default instance of the JMXCollector entry map. The map
     * represents a singular instance from the MibObject. Each column in the
     * table for the loaded instance may be retrieved through its OID from the
     * MIBObject.
     * </P>
     * 
     * <P>
     * The initial table is constructed with zero elements in the map.
     * </P>
     */
    public JMXCollectorEntry() {
        super();
    }

    /**
     * <P>
     * The class constructor used to initialize the object to its initial state.
     * Although the object's member variables can change after an instance is
     * created, this constructor will initialize all the variables as per their
     * named variable from the passed array of JMX varbinds.
     * </P>
     * 
     * <P>
     * If the information in the object should not be modified then a <EM>final
     * </EM> modifier can be applied to the created object.
     * </P>
     * 
     * @param vars
     *            The array of collected JMX variable bindings
     * @param objList
     *            List of MibObject objects representing each of of the oid's
     *            configured for collection.
     * @param ifIndex
     *            The ifIndex (as a String) of the interface for which the
     *            collected JMX data is relevant. NOTE: NULL if the collected
     *            JMX data is for the node.
     */
    public JMXCollectorEntry(String[] vars, String[] types) {
        this();
        
        for (int i = 0; i < vars.length;i++ ) {
            put(vars[i], types[i]);
        }
    }

    /* (non-Javadoc)
     * @see java.util.TreeMap#keySet()
     */
    public Set attributeNames() {
        return super.keySet();
    }
}
