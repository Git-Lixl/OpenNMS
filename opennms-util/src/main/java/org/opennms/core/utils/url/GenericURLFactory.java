package org.opennms.core.utils.url;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;

public class GenericURLFactory implements URLStreamHandlerFactory {

    private Logger logger = LoggerFactory.getLogger(GenericURLFactory.class);

    private HashMap<String, String> urlConnections = null;

    private static GenericURLFactory genericUrlFactory = null;

    private GenericURLFactory() {
        urlConnections = new HashMap<String, String>();

        // Map dns:// against DNS requisition URL connection
        urlConnections.put("dns", "org.opennms.netmgt.provision.service.dns.DnsRequisitionUrlConnection");
        logger.debug("Add dns protocol to map against org.opennms.netmgt.provision.service.dns.DnsRequisitionUrlConnection");

        // Map puppet:// against Puppet requisition URL connection
        urlConnections.put("puppet", "org.opennms.netmgt.provision.service.puppet.PuppetRequisitionUrlConnection");
        logger.debug("Add puppet protocol to map against org.opennms.netmgt.provision.service.puppet.PuppetRequisitionUrlConnection");
    }

    public static void initialize() {
        if (genericUrlFactory == null) {
            genericUrlFactory = new GenericURLFactory();
            URL.setURLStreamHandlerFactory(genericUrlFactory);
        }
    }

    public static GenericURLFactory getInstance() {
        if (genericUrlFactory == null)
            initialize();
        return genericUrlFactory;
    }

    public void addURLConnection(String protocol, String classname) {
        urlConnections.put(protocol, classname);
    }

    public void removeURLConnection(String protocol) {
        if (urlConnections.containsKey(protocol))
            urlConnections.remove(protocol);
    }

    public URLStreamHandler createURLStreamHandler(String protocol) {
        Class c = null;

        // There is not such a protocol defined
        if (!urlConnections.containsKey(protocol)) {
            logger.debug("There is not such protocol: '{}'", protocol);
            return null; // leave
        }

        try {
            // Get implementation class by protocol at runtime
            c = Class.forName(urlConnections.get(protocol));
            logger.debug("Get implementation class '{}' by protocol '{}'",c.toString(), protocol);
        } catch (ClassNotFoundException e) {
            // There is not such a class for this protocol
            e.printStackTrace();
            logger.error("There is not such a class for this protocl '{}'",protocol);
            return null;
        }

        // Return this
        return new GenericURLStreamHandler(c);
    }
}
