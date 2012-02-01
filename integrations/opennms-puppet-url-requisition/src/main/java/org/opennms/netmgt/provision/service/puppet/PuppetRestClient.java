package org.opennms.netmgt.provision.service.puppet;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opennms.netmgt.provision.service.puppet.tools.Map2Bean;
import org.opennms.netmgt.provision.service.puppet.tools.RequistionAssetGen;

/**
 * <p>PuppetRestClient class.</p>
 *
 * @author Ronny Trommer <ronny@opennms.org>
 * @version $Id: $
 * @since 1.10.1
 */
public class PuppetRestClient {
    private Logger logger = LoggerFactory.getLogger(PuppetRequisitionUrlConnection.class);
    /**
     * HTTP client for ReST connection
     */
    private Client m_client;

    /**
     * HTTP client connection configuration
     */
    private DefaultClientConfig m_clientConfig;

    /**
     * Data structure from ReST call
     */
    private WebResource m_webResource;

    /**
     * Media type for ReST request to Puppet master
     */
    private final String MEDIA_TYPE_YAML = "yaml";

    /**
     * Default constructor with URL for ReST request
     *
     * @param url a {@link }
     * @throws URISyntaxException
     */
    public PuppetRestClient(URL url) throws URISyntaxException {
        m_clientConfig = new DefaultClientConfig();
        m_client = Client.create(m_clientConfig);

        logger.debug("Initialize Puppet ReST client with URL: '{}'", url.toString());
        m_webResource = m_client.resource(url.toURI());
    }
    
    public List<String> getPuppetNodeByFactsSearch(String environment, String search) {
        Yaml puppetNodeListYaml = new Yaml();
        ArrayList<String> puppetHosts = new ArrayList<String>();
        
        // https://{puppetmaster}:8140/{environment}/facts_search/search?facts.operatingsystem=Ubuntu
        // search = facts.operatingsystem=Ubuntu
        String puppetSearchResult = m_webResource.path(environment).path("facts_search").path("search?" + search).accept(MEDIA_TYPE_YAML).get(String.class);
        logger.debug("Search result for puppet nodes: '{}'", puppetSearchResult);

        puppetHosts = (ArrayList<String>) puppetNodeListYaml.load(puppetSearchResult);
        return puppetHosts;
    }

    public Map<String,String> getFactsByPuppetNode(String puppetNode) {
        Map<String,String> nodeFacts = new HashMap<String,String>();

        //https://{puppetmaster}:8140/production/node/patches.opennms-edu.net
        String puppetNodeYaml = m_webResource.path("production").path("node").path(puppetNode).accept(MEDIA_TYPE_YAML).get(String.class).replace("!ruby/", "");
        logger.debug("Get puppet node facts for node '{}': '{}'", puppetNode, puppetNodeYaml);

        try {
            ArrayList<String> lines = (ArrayList<String>) IOUtils.readLines(new StringReader(puppetNodeYaml));
            for (String line : lines) {
                nodeFacts.put(line.split(":")[0].trim(), line.split(":")[1].trim().replaceAll("\"", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return nodeFacts;
    }
}
