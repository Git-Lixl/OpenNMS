/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2011 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.provision.service.puppet;

import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.lang.StringUtils;
import org.opennms.core.utils.url.GenericURLConnection;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.netmgt.provision.persist.requisition.RequisitionAsset;
import org.opennms.netmgt.provision.persist.requisition.RequisitionInterface;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;
import org.opennms.netmgt.provision.service.puppet.tools.SSLUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.List;
import java.util.Map;

/**
 * <p>PuppetRequisitionUrlConnection class.</p>
 *
 * @author Ronny Trommer <ronny@opennms.org>
 * @version $Id: $
 * @since 1.10.1
 */
public class PuppetRequisitionUrlConnection extends GenericURLConnection {

    /**
     * Log to OPENNMS_HOME/logs/daemon/provisiond.log
     */
    private Logger logger = LoggerFactory.getLogger("Provisiond." + PuppetRequisitionUrlConnection.class.getName());

    /**
     * Arguments from provisionds import url resource
     */
    private static Map<String, String> m_args;

    /**
     * Puppet ReST client implementation
     */
    private PuppetRestClient m_puppetRestClient;

    /**
     * Host name of puppet master server
     */
    private String m_host;

    /**
     * Port from puppet masters ReST API (default: 8140)
     */
    private Integer m_port;

    /**
     * Puppet masters ReST base URL
     */
    private URL m_puppetRestUrl;

    /**
     * OpenNMS foreign source for the import
     */
    private String m_foreignSource;

    /**
     * Puppet environment
     */
    private String m_puppetEnvironment;

    /**
     * Constructor for the URL handling
     *
     * @param url
     * @throws MalformedURLException
     */
    public PuppetRequisitionUrlConnection(URL url) throws MalformedURLException {
        super(url);

        // TODO indigo: For security reasons, please import your puppet masters SSL certificate in your JVM. Only for test environment
        // Figure out how we can handle this in a safe way even you run massively parallel. THIS IS A JVM SETTING!
        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();

        m_host = url.getHost();

        m_port = url.getPort();

        m_args = getQueryArgs(url);

        m_puppetRestUrl = new URL("https://" + m_host + ":" + m_port);

        m_foreignSource = parseForeignSource(url);

        m_puppetEnvironment = parsePuppetEnvironment(url);

        logger.debug("Initialize puppet requisition url connection: '{}'", "Host[" + m_host +
                "] Environment[" + m_puppetEnvironment +
                "] Foreign source[" + m_foreignSource +
                "] ReST URL[" + m_puppetRestUrl +
                "] Arguments[" + m_args);
    }

    /**
     * We have to override this method, we do not really handle a URL connection. We use just the information to
     * make a puppet connection via ReST
     *
     * @throws IOException
     */
    @Override
    public void connect() throws IOException {
        // No op
    }

    /**
     * <p>buildPuppetRequisition</p>
     * 
     * Build the OpenNMS requisition data structure from puppet master.
     * 
     * @return a {@link org.opennms.netmgt.provision.persist.requisition.Requisition} object
     */
    private Requisition buildPuppetRequisition() {
        Requisition requisition = new Requisition(m_foreignSource);

        try {
            // https://{puppetmaster}:8140
            m_puppetRestClient = new PuppetRestClient(m_puppetRestUrl);
            List<String> puppetNodeList = m_puppetRestClient.getPuppetNodesByFactsSearch(m_puppetEnvironment, "facts.operatingsystem=Ubuntu");

            for (String puppetNode : puppetNodeList) {
                requisition.insertNode(createRequisitionNode(puppetNode));
                logger.debug("Insert puppet requisition node '{}' from environment '{}'", puppetNode, m_puppetEnvironment);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return requisition;
    }

    /**
     * Create a specific OpenNMS requisition node from puppet master identified by the puppet node name. Retrieve puppet
     * facts and fill up the OpenNMS requisition node model.
     * 
     * @param puppetNode a {@link java.lang.String} puppet node name
     * @return a {@link org.opennms.netmgt.provision.persist.requisition.RequisitionNode} object
     */
    private RequisitionNode createRequisitionNode(String puppetNode) {
        logger.debug("Create requisition node for puppet node '{}'", puppetNode);

        // Initialize a node
        RequisitionNode requisitionNode = new RequisitionNode();

        // Initialize a interface
        RequisitionInterface requisitionInterface = new RequisitionInterface();

        // environment -> Puppet environment: production
        // puppetNode -> Puppet node name: itchy.opennms-edu.net
        Map<String, String> puppetNodeFacts = m_puppetRestClient.getFactsByPuppetNode(m_puppetEnvironment, puppetNode);

        // Set OpenNMS node label
        requisitionNode.setNodeLabel(puppetNode);
        logger.debug("Set node label: '{}'", puppetNode);

        // Foreign Id as unique ide in puppet master
        // TODO indigo: We have to be sure "uniqueid" is really unique in puppet
        requisitionNode.setForeignId(puppetNodeFacts.get("uniqueid"));
        logger.debug("Set node foreign ID: '{}'", puppetNodeFacts.get("uniqueid"));

        // Verify IP addresses an initialize the interface model
        // TODO indigo: We have to figure out what happen with more then one ip interface and IPv6?
        try {
            InetAddress inetAddress = InetAddress.getByName(puppetNodeFacts.get("ipaddress"));
            requisitionInterface.setIpAddr(puppetNodeFacts.get("ipaddress"));
            requisitionInterface.setSnmpPrimary("P");
        } catch (UnknownHostException e) {
            logger.error("Error parsing IP address '{}'. Error message: '{}'", puppetNodeFacts.get("ipaddress"), e.getMessage());
        }

        // Assign puppet facts to OpenNMS sssets
        RequisitionAsset manufacturerAsset = new RequisitionAsset("manufacturer", puppetNodeFacts.containsKey("manufacturer") ? puppetNodeFacts.get("manufacturer") : "");
        RequisitionAsset osAsset = new RequisitionAsset("operatingSystem", puppetNodeFacts.containsKey("lsbdistdescription") ? puppetNodeFacts.get("lsbdistdescription") : "");
        RequisitionAsset serialnumberAsset = new RequisitionAsset("serialNumber", puppetNodeFacts.containsKey("serialnumber") ? puppetNodeFacts.get("serialnumber") : "");
        RequisitionAsset modelnumberAsset = new RequisitionAsset("modelNumber", puppetNodeFacts.containsKey("productname") ? puppetNodeFacts.get("productname") : "");
        RequisitionAsset cpuAsset = new RequisitionAsset("cpu", puppetNodeFacts.containsKey("processor0") ? puppetNodeFacts.get("processor0") : "");

        // Add assets to requisistion node
        requisitionNode.putAsset(manufacturerAsset);
        requisitionNode.putAsset(osAsset);
        requisitionNode.putAsset(serialnumberAsset);
        requisitionNode.putAsset(modelnumberAsset);
        requisitionNode.putAsset(cpuAsset);

        // Configure the interface and initialize
        requisitionInterface.setManaged(Boolean.TRUE);
        requisitionInterface.setStatus(Integer.valueOf(1));
        requisitionNode.putInterface(requisitionInterface);

        return requisitionNode;
    }

    /**
     * Foreign Source should be the second path entity, if it exists, otherwise it is
     * set to the value of the zone.
     * <p/>
     * puppet://<host>/<environment>[/<foreign source>][/<?expression=<regex>>
     *
     * @param url a {@link java.net.URL} object.
     * @return a {@link java.lang.String} object.
     */
    protected static String parseForeignSource(URL url) {

        String path = url.getPath();

        path = StringUtils.removeStart(path, "/");
        path = StringUtils.removeEnd(path, "/");

        String foreignSource = path;

        if (path != null && StringUtils.countMatches(path, "/") == 1) {
            String[] paths = path.split("/");
            foreignSource = paths[1];
        }

        return foreignSource;
    }

    /**
     * Puppet environment should be the first path entity
     * <p/>
     * puppet://<host>/<puppet environment>[/<foreign source>][/<?expression=<regex>>
     *
     * @param url a {@link java.net.URL} object.
     * @return a {@link java.lang.String} object.
     */
    protected static String parsePuppetEnvironment(URL url) {

        String path = url.getPath();

        path = StringUtils.removeStart(path, "/");
        path = StringUtils.removeEnd(path, "/");

        String puppetEnvironment = path;

        if (path != null && StringUtils.countMatches(path, "/") == 1) {
            String[] paths = path.split("/");
            puppetEnvironment = paths[0];
        }

        return puppetEnvironment;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Creates a ByteArrayInputStream implementation of InputStream of the XML
     * marshaled version of the Requisition class. Calling close on this stream
     * is safe.
     */
    @Override
    public InputStream getInputStream() throws IOException {

        InputStream stream = null;

        try {
            Requisition r = buildPuppetRequisition();
            stream = new ByteArrayInputStream(jaxBMarshal(r).getBytes());
        } catch (Throwable e) {
            logger.warn("Problem getting input stream: '{}'", e);
            throw new IOExceptionWithCause("Problem getting input stream: " + e, e);
        }

        return stream;
    }

    /**
     * Utility to marshal the Requisition class into XML.
     *
     * @param r
     * @return a String of XML encoding the Requisition class
     * @throws javax.xml.bind.JAXBException
     */
    private String jaxBMarshal(Requisition r) throws JAXBException {
        return JaxbUtils.marshal(r);
    }
}
