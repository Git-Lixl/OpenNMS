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

/**
 * <p>PuppetRestClient class.</p>
 *
 * @author Ronny Trommer <ronny@opennms.org>
 * @version $Id: $
 * @since 1.10.1
 */
public class PuppetRestClient {
    private Logger logger = LoggerFactory.getLogger("Provisiond." + PuppetRestClient.class.getName());
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
     * @param url a {@link java.net.URL} puppet master ReST URL
     * @throws URISyntaxException
     */
    public PuppetRestClient(URL url) throws URISyntaxException {

        // If you want base authentication go for defaultApacheClientConfig and credentials
        m_clientConfig = new DefaultClientConfig();
        m_client = Client.create(m_clientConfig);

        logger.debug("Initialize Puppet ReST client with URL: '{}'", url.toString());
        m_webResource = m_client.resource(url.toURI());
    }

    /**
     * <p>getPuppetNodesByFactsSearch</p>
     * 
     * Get puppet nodes specified by facts search
     * 
     * @param environment a {@link java.lang.String} puppet environment
     * @param search a {@link java.lang.String} puppet fact search expression
     * @return puppetHosts a {@link java.lang.String} list with puppet node names
     */
    public List<String> getPuppetNodesByFactsSearch(String environment, String search) {
        Yaml puppetNodeListYaml = new Yaml();
        ArrayList<String> puppetHosts = new ArrayList<String>();
        
        // https://{puppetmaster}:8140/{environment}/facts_search/search?{search}
        // environment = production
        // search = facts.operatingsystem=Ubuntu
        // production/facts_search/search?facts.productname=VMware%20Virtual%20Platform
        String puppetSearchResult = m_webResource.path(environment).path("facts_search").path("search?" + search).accept(MEDIA_TYPE_YAML).get(String.class);
        logger.debug("Search result with search '{}' for puppet nodes: '{}'","search?" + search, puppetSearchResult);

        puppetHosts = (ArrayList<String>) puppetNodeListYaml.load(puppetSearchResult);
        return puppetHosts;
    }

    /**
     * <p>getFactsByPuppetNode</p>
     * 
     * Get all facts from a specific puppet node by name
     * 
     * @param environment a {@link java.lang.String} puppet environment
     * @param puppetNode a {@link java.lang.String} puppet node name
     * @return nodeFacts a {@link java.util.Map} map with node facts
     */
    public Map<String,String> getFactsByPuppetNode(String environment, String puppetNode) {
        Map<String,String> nodeFacts = new HashMap<String,String>();

        //https://{puppetmaster}:8140/{environment}/node/{puppetNode}
        String puppetNodeYaml = m_webResource.path(environment).path("node").path(puppetNode).accept(MEDIA_TYPE_YAML).get(String.class).replace("!ruby/", "");
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
