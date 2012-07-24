/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.provision.service.chef;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.lang.StringUtils;
import org.jclouds.Context;
import org.jclouds.ContextBuilder;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.domain.SearchResult;
import org.opennms.core.utils.LogUtils;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.netmgt.model.PrimaryType;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.netmgt.provision.persist.requisition.RequisitionInterface;
import org.opennms.netmgt.provision.persist.requisition.RequisitionMonitoredService;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * Implementation of <code>java.net.URLConnection</code> for handling
 * URLs specified in the Provisiond configuration requesting an import
 * requisition from nodes returned by a search operation against the
 * <a href="http://wiki.opscode.com/display/chef/Server+API">Chef Server API</a>
 *
 * @author <a href="mailto:jeffg@opennms.org">Jeff Gehlbach</a>
 */
public class ChefRequisitionUrlConnection extends URLConnection {

    private static final String SEARCH_QUERY_ARG = "q";
    
    private static final String QUERY_ARG_SEPARATOR = "&";

    /** Constant <code>URL_SCHEME="chef://"</code> */
    public static final String URL_SCHEME = "chef://";
    
    /** Constant <code>PROTOCOL="chef"</code> */
    public static final String PROTOCOL = "chef";

    private String m_scheme;

    private URL m_url;

    private int m_port;

    private String m_foreignSource;
    
    private String m_identity;
    
    private String m_credential;
    
    private String[] m_services;
    
    /**
     * <p>Constructor for ChefRequisitionUrlConnection.</p>
     *
     * @param url a {@link java.net.URL} object.
     * @throws java.net.MalformedURLException if any.
     */
    protected ChefRequisitionUrlConnection(URL url) throws MalformedURLException {
        super(url);
        validateChefUrl(url);
        m_port = url.getPort() == -1 ? 4000 : url.getPort();
        m_scheme = parseScheme(url);
        m_foreignSource = parseForeignSource(url);
        m_identity = parseChefUserIdentity(url);
        m_credential = getChefUserCredential(url);
        m_url = url;
        
        ChefContext con = ContextBuilder.newBuilder("chef").endpoint(getUnderlyingChefURL().toString()).credentials(m_identity, m_credential).build();
        ChefApi api = con.getApi();
        
        SearchResult<? extends Node> chefNodes = api.searchNodes();
        for (Node node : chefNodes) {
            node.getName();
        }
    }
    
    protected URL getUnderlyingChefURL() throws MalformedURLException {
        return new URL(m_scheme, m_url.getHost(), m_port, parseEndpointPath(m_url));
    }
    
    protected String parseChefUserIdentity(URL url) {
        String identity = "unspecified";
        String userInfo = url.getUserInfo();
        if (userInfo == null) return identity;
        if (StringUtils.countMatches(userInfo, ":") >= 1) {
            String[] components = userInfo.split(":", 2);
            identity = components[0];
        }
        return identity;
    }
    
    protected String getChefUserCredential(URL url) {
        String credential = "unspecified";
        String userInfo = url.getUserInfo();
        if (userInfo == null) return credential;
        if (StringUtils.countMatches(userInfo, ":") >= 1) {
            String[] components = userInfo.split(":", 2);
            credential = readChefUserPrivateKey(components[1]);
        }
        return credential;
    }
    
    protected String readChefUserPrivateKey(String pathname) {
        File pemFile = new File(pathname);
        if (!pemFile.exists()) {
            LogUtils.errorf(getClass(), "Cannot read Chef private key from file '%s' because file does not exist", pathname);
        }
        if (!pemFile.canRead()) {
            LogUtils.errorf(getClass(), "Cannot read Chef private key from file '%s' because file is not readable", pathname);
        }
        try {
            return Files.toString(pemFile, Charsets.UTF_8);
        } catch (IOException ioe) {
            LogUtils.errorf(getClass(), ioe, "Failed to read Chef private key from file '%s'", pathname);
        }
        return null;
    }
   
    /**
     * {@inheritDoc}
     *
     * This is a no-op.
     */
    @Override
    public void connect() throws IOException {
    }

    /**
     * {@inheritDoc}
     *
     * Creates a ByteArrayInputStream implementation of InputStream of the XML marshaled version
     * of the Requisition class.  Calling close on this stream is safe.
     */
    @Override
    public InputStream getInputStream() throws IOException {
        
        InputStream stream = null;
        
        try {
            Requisition r = buildRequisitionFromChefOutput();
            stream = new ByteArrayInputStream(jaxBMarshal(r).getBytes());
        } catch (Throwable e) {
            String message = "Problem getting input stream: "+e;
            log().warn(message, e);
            throw new IOExceptionWithCause(message,e );
        }
        
        return stream;
    }
    
    /**
     * Creates an instance of the JaxB annotated Requisition class.
     *
     * @return a Requisition representing the nodes returned by a search
     *   against the Chef Server API as specified in the URL
     */
    private Requisition buildRequisitionFromChefOutput() {
        Requisition r = new Requisition();
        return r;
    }
    
    /**
     * Creates an instance of the JaxB annotated RequisitionNode class.
     * 
     * @param rec
     * @return a populated RequisitionNode based on defaults and data from the
     *   A record returned from a DNS zone transfer query.
     */
    private RequisitionNode createRequisitionNode(Record rec) {
        String addr = null;
        if ("A".equals(Type.string(rec.getType()))) {
            ARecord arec = (ARecord)rec;
            addr = StringUtils.stripStart(arec.getAddress().toString(), "/");
        } else if ("AAAA".equals(Type.string(rec.getType()))) {
            AAAARecord aaaarec = (AAAARecord)rec;
            addr = aaaarec.rdataToString();
        } else {
            throw new IllegalArgumentException("Invalid record type " + Type.string(rec.getType()) + ". A or AAAA expected.");
        }

        RequisitionNode n = new RequisitionNode();
        
        String host = rec.getName().toString();
        String nodeLabel = StringUtils.stripEnd(StringUtils.stripStart(host, "."), ".");

        n.setBuilding(getForeignSource());
        
        n.setNodeLabel(nodeLabel);
        
        RequisitionInterface i = new RequisitionInterface();
        i.setDescr("DNS-" + Type.string(rec.getType()));
        i.setIpAddr(addr);
        i.setSnmpPrimary(PrimaryType.PRIMARY);
        i.setManaged(Boolean.TRUE);
        i.setStatus(Integer.valueOf(1));
        
        for (String service : m_services) {
            service = service.trim();
            i.insertMonitoredService(new RequisitionMonitoredService(service));
            log().debug("Adding provisioned service " + service);
            }
        
        n.putInterface(i);
        
        return n;
    }

    /**
     * Utility to marshal the Requisition class into XML.
     * 
     * @param r
     * @return a String of XML encoding the Requisition class
     * 
     * @throws JAXBException
     */
    private String jaxBMarshal(Requisition r) throws JAXBException {
    	return JaxbUtils.marshal(r);
    }
    
    /**
     * <p>getEnvironment</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getEnvironment() {
        return m_scheme;
    }
    
    /**
     * <p>getDescription</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return m_url.toString();
    }
    
    /**
     * <p>toString</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString() {
        return getDescription();
    }
    
    /**
     * <p>getUrl</p>
     *
     * @return a {@link java.net.URL} object.
     */
    public URL getUrl() {
        return m_url;
    }
    
    private static List<String> tokenizeQueryArgs(String query) throws IllegalArgumentException {
        
        if (query == null) {
            throw new IllegalArgumentException("The URL query is null");
        }

        List<String> queryArgs = Arrays.asList(StringUtils.split(query, QUERY_ARG_SEPARATOR));

        return queryArgs;
    }

    /**
     * Validate the format is:
     *   chef://<chef-server>/<environment>/[<foreign-source>]?expression=<search-expr>
     *
     *   there should be only one argument in the path
     *   there should only be one query parameter
     *
     * @param url a {@link java.net.URL} object.
     * @throws java.net.MalformedURLException if any.
     */
    protected static void validateChefUrl(URL url) throws MalformedURLException {
        
        String path = url.getPath();
        path = StringUtils.removeStart(path, "/");
        path = StringUtils.removeEnd(path, "/");
        
        if (path == null || StringUtils.countMatches(path, "/") < 2) {
            throw new MalformedURLException("The specified chef URL contains too few path components (expected: chef://<host>/<scheme>/<foreign-source>/<endpoint-path> ): "+url);
        }
        String[] paths = path.split("/");
        if (!"http".equalsIgnoreCase(paths[0]) && !"https".equalsIgnoreCase(paths[0])) {
            throw new MalformedURLException("The specified chef URL's first path component must be either 'http' or 'https': " +url);
        }
    }

    
    /**
     * Scheme (http or https) should be the first path entity
     *
     *   chef://<host>/<scheme>/<foreign source>/<endpoint-path>
     *
     * @param url a {@link java.net.URL} object.
     * @return a {@link java.lang.String} object.
     */
    protected static String parseScheme(URL url) {
        
        String path = url.getPath();
        
        path = StringUtils.removeStart(path, "/");
        path = StringUtils.removeEnd(path, "/");

        String scheme = path;
        if (path != null && StringUtils.countMatches(path, "/") >= 2) {
            String[] paths = path.split("/");
            scheme = paths[0];
        }
        return scheme;
    }
    
    
    /**
     * Foreign Source should be the second path entity
     *
     *   chef://<host>/<scheme>/<foreign source>/<endpoint-path>
     *
     * @param url a {@link java.net.URL} object.
     * @return a {@link java.lang.String} object.
     */
    protected static String parseForeignSource(URL url) {
        
        String path = url.getPath();
        
        path = StringUtils.removeStart(path, "/");
        path = StringUtils.removeEnd(path, "/");

        String foreignSource = path;
        if (path != null && StringUtils.countMatches(path, "/") >= 2) {
            String[] paths = path.split("/");
            foreignSource = paths[1];
        }
        return foreignSource;
    }
    
    /**
     * Endpoint path should start at the third path entity and continue to the end of the path
     * 
     *   chef://<host>/<scheme>/<foreign source>/<endpoint-path>
     * 
     * @param url a {@link java.net.URL} object
     * @return a {@link java.lang.String} object.
     */
    protected static String parseEndpointPath(URL url) {
        String path = url.getPath();
        
        path = StringUtils.removeStart(path, "/");
        path = StringUtils.removeEnd(path, "/");
        
        String endpointPath = path;
        if( path != null && StringUtils.countMatches(path, "/") >= 2) {
            String[] paths = path.split("/");
            endpointPath = paths[2];
        }
        return endpointPath;
    }
    
    private static ThreadCategory log() {
        return ThreadCategory.getInstance(ChefRequisitionUrlConnection.class);
    }


    /**
     * <p>setForeignSource</p>
     *
     * @param foreignSource a {@link java.lang.String} object.
     */
    public void setForeignSource(String foreignSource) {
        m_foreignSource = foreignSource;
    }


    /**
     * <p>getForeignSource</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getForeignSource() {
        return m_foreignSource;
    }

}
