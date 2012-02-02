package org.opennms.netmgt.provision.service.puppet;

import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.lang.StringUtils;
import org.opennms.core.utils.url.GenericURLConnection;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.netmgt.provision.persist.requisition.RequisitionInterface;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;
import org.opennms.netmgt.provision.service.puppet.tools.Map2BeanUtils;
import org.opennms.netmgt.provision.service.puppet.tools.RequisitionAssetUtils;
import org.opennms.netmgt.provision.service.puppet.tools.SSLUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
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

    private Logger logger = LoggerFactory.getLogger("Provisiond." + PuppetRequisitionUrlConnection.class.getName());

    private static Map<String, String> m_args;

    private PuppetRestClient m_puppetRestClient;
    
    private String m_host;
    
    private Integer m_port;
    
    private URL m_puppetRestUrl;

    private String m_foreignSource;
    
    private String m_puppetEnvironment;

    public PuppetRequisitionUrlConnection(URL url) throws MalformedURLException {
        super(url);

        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();
        
        m_host = url.getHost();
        logger.debug("Init puppet requisition host '{}'", m_host);
        
        m_port = url.getPort();
        logger.debug("Init puppet requisition port '{}'", m_port);
        
        m_args = getQueryArgs(url);
        
        m_puppetRestUrl = new URL("https://" + m_host + ":" + m_port);
        logger.debug("Init puppet ReST URL '{}'", m_puppetRestUrl);

        m_foreignSource = parseForeignSource(url);
        logger.debug("Init puppet foreign source '{}'", m_foreignSource);
                
        m_puppetEnvironment = parsePuppetEnvironment(url);
        logger.debug("Init puppet environment '{}'", m_puppetEnvironment);

        logger.debug("Initialized PuppetRequisitionUrlConnection with URL: '{}' and args: '{}'", url, m_args);
    }

    /**
     * We have to override this method, we do not really handle a URL connection. We use just the information to
     * make a puppet connection via ReST
     *
     * @throws IOException
     */
    @Override
    public void connect() throws IOException {
        // We do nothing here, cause we do not really open a connection
    }

    private Requisition buildPuppetRequisition() {
        Requisition requisition = new Requisition(m_foreignSource);
        
        try {
            // https://{puppetmaster}:8140
            m_puppetRestClient = new PuppetRestClient(m_puppetRestUrl);
            List<String> puppetNodeList = m_puppetRestClient.getPuppetNodesByFactsSearch(m_puppetEnvironment,"facts.operatingsystem=Ubuntu");
        
            for (String puppetNode : puppetNodeList) {
                requisition.insertNode(createRequisitionNode(puppetNode));
                logger.debug("Insert puppet requisition node '{}' from environment '{}'", puppetNode, m_puppetEnvironment);
             }
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        
        return requisition;
    }

    private RequisitionNode createRequisitionNode(String puppetNode) {
        logger.debug("Create requisition node for puppet node '{}'", puppetNode);
        RequisitionNode requisitionNode = new RequisitionNode();

        RequisitionInterface requisitionInterface = new RequisitionInterface();

        //environnment -> Puppet environment: production
        //puppetNode -> Puppet node name: itchy.opennms-edu.net
        Map<String,String> puppetNodeFacts = m_puppetRestClient.getFactsByPuppetNode(m_puppetEnvironment, puppetNode);

        // Setting the node label
        requisitionNode.setNodeLabel(puppetNode);
        logger.debug("Set node label: '{}'", puppetNode);

        // Foreign Id to map against uuid from puppetmaster
        requisitionNode.setForeignId(puppetNodeFacts.get("uniqueid"));
        logger.debug("Set node foreign ID: '{}'", puppetNodeFacts.get("uniqueid"));

        try {
            InetAddress inetAddress = InetAddress.getByName(puppetNodeFacts.get("ipaddress"));
            requisitionInterface.setIpAddr(puppetNodeFacts.get("ipaddress"));
            requisitionInterface.setSnmpPrimary("P");
        } catch (UnknownHostException e) {
            logger.error("Error parsing IP address '{}'. Error message: '{}'", puppetNodeFacts.get("ipaddress"), e.getMessage());
        }
        
        PuppetModel puppetAssetModel = new PuppetModel();
        try {
            puppetAssetModel = (PuppetModel) Map2BeanUtils.fill(puppetAssetModel,puppetNodeFacts);
            requisitionNode.setAssets(RequisitionAssetUtils.generateRequisitionAssets(puppetAssetModel));
        } catch (IllegalAccessException e) {
            logger.error("Illegal access ocured. Error message: '{}'", e.getMessage());
        } catch (InvocationTargetException e) {
            logger.error("Invocation target exception. Error message: '{}'", e.getMessage());
        } catch (NoSuchMethodException e) {
            logger.error("No such method exception. Error message: '{}'", e.getMessage());
        }

        requisitionInterface.setManaged(Boolean.TRUE);
        requisitionInterface.setStatus(Integer.valueOf(1));
        requisitionNode.putInterface(requisitionInterface);

        return requisitionNode;
    }

    /**
     * Foreign Source should be the second path entity, if it exists, otherwise it is
     * set to the value of the zone.
     *
     *   dns://<host>/<zone>[/<foreign source>][/<?expression=<regex>>
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
     *
     *   puppet://<host>/<puppet environment>[/<foreign source>][/<?expression=<regex>>
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
