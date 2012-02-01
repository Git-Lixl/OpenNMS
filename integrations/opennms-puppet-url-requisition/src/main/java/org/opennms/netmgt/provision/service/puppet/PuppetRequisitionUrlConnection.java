package org.opennms.netmgt.provision.service.puppet;

import org.apache.commons.io.IOExceptionWithCause;
import org.opennms.core.utils.url.GenericURLConnection;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.netmgt.provision.persist.requisition.RequisitionInterface;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Map;
/**
 * <p>PuppetRequisitionUrlConnection class.</p>
 *
 * @author Ronny Trommer <ronny@opennms.org>
 * @version $Id: $
 * @since 1.10.1
 */
public class PuppetRequisitionUrlConnection extends GenericURLConnection {
    private Logger logger = LoggerFactory.getLogger(PuppetRequisitionUrlConnection.class);

    private static Map<String, String> m_args;

    private Requisition m_requisition = null;

    private PuppetRestClient m_puppetRestClient;
    
    private String m_host;
    
    private Integer m_port;
    
    private URL m_puppetRestUrl;

    protected PuppetRequisitionUrlConnection(URL url) throws MalformedURLException {
        super(url);
        
        m_host = url.getHost();
        
        m_port = url.getPort();

        m_args = getQueryArgs(url);
        
        m_puppetRestUrl = new URL("https://" + m_host + ":" + m_port);

        try {
            // https://{puppetmaster}:8140
            m_puppetRestClient = new PuppetRestClient(m_puppetRestUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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
        return new Requisition();
    }

    private RequisitionNode createRequisitionNode(String puppetNodeRecord) {
        RequisitionNode requisitionNode = new RequisitionNode();

        RequisitionInterface requisitionInterface = new RequisitionInterface();

        //puppetNodeRecord -> Puppet node name: itchy.opennms-edu.net
        m_puppetRestClient.getFactsByPuppetNode(puppetNodeRecord);

        /*
         * node label:
         * ip address:
         * uuid
         * operatingsystem:
         */

        // Setting the node label
        requisitionNode.setNodeLabel("<nodelabel>");

        // Foreign Id to map against uuid from puppetmaster
        requisitionNode.setForeignId("<puppet-uuid>");

        // Primary interface
        boolean primary = true;

        try {
            InetAddress inetAddress = InetAddress.getByName("");
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        requisitionNode.putInterface(requisitionInterface);

        return new RequisitionNode();
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
