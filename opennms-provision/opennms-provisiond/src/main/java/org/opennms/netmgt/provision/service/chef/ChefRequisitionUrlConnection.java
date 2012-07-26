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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jclouds.ContextBuilder;
import org.jclouds.chef.ChefApi;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.domain.SearchResult;
import org.jclouds.domain.JsonBall;
import org.opennms.core.utils.LogUtils;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.netmgt.model.PrimaryType;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.netmgt.provision.persist.requisition.RequisitionCategory;
import org.opennms.netmgt.provision.persist.requisition.RequisitionInterface;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;

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
    
    private URL m_chefApiURL;
    
    private ChefProvisioningConfiguration m_chefProvisioningConfig;
    
    /**
     * <p>Constructor for ChefRequisitionUrlConnection.</p>
     *
     * @param url a {@link java.net.URL} object.
     * @throws java.net.MalformedURLException if any.
     */
    protected ChefRequisitionUrlConnection(URL url) throws MalformedURLException {
        super(url);
        validateChefUrl(url);
        m_scheme = parseScheme(url);

        if (url.getPort() == -1) {
            if ("https".equals(m_scheme)) {
                m_port = 443;
            } else {
                m_port = 4000;
            }
        } else {
            m_port = url.getPort();
        }

        m_foreignSource = parseForeignSource(url);
        m_identity = parseChefUserIdentity(url);
        m_credential = getChefUserCredential(url);
        m_url = url;
        m_chefApiURL = getUnderlyingChefURL();
    }
    
    protected URL getUnderlyingChefURL() throws MalformedURLException {
        return new URL(m_scheme, m_url.getHost(), m_port, parseEndpointPath(m_url));
    }
    
    protected String parseChefUserIdentity(URL url) {
        String userInfo = url.getUserInfo();
        if (userInfo == null) {
            return "unspecified";
        } else {
            return userInfo;
        }
    }
    
    protected String getChefUserCredential(URL url) {
        String chefKeysDirectory = System.getProperty("chef.keysDirectory", "/etc/chef");
        
        String userInfo = url.getUserInfo();
        if (userInfo == null) return "unspecified";
        
        String keyPath = chefKeysDirectory + File.separator + userInfo + ".pem"; 
        return readChefUserPrivateKey(keyPath);
    }
    
    protected String readChefUserPrivateKey(String pathname) {
        LogUtils.debugf(getClass(), "Will attempt to read Chef private key from file '%s'", pathname);
        File pemFile = new File(pathname);
        if (!pemFile.exists()) {
            LogUtils.errorf(getClass(), "Cannot read Chef private key from file '%s' because file does not exist", pathname);
        }
        if (!pemFile.canRead()) {
            LogUtils.errorf(getClass(), "Cannot read Chef private key from file '%s' because file is not readable", pathname);
        }
        try {
            String cred = Files.toString(pemFile, Charsets.UTF_8);
            LogUtils.debugf(getClass(), "Successfully read %d bytes from Chef private key PEM file '%s'", cred.length(), pemFile);
            return cred;
        } catch (IOException ioe) {
            LogUtils.errorf(getClass(), ioe, "Failed to read Chef private key from file '%s'", pathname);
        }
        return "";
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
            LogUtils.warnf(getClass(), e, message);
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
        Requisition req = new Requisition();
        
        LogUtils.debugf(getClass(), "Creating ContextBuilder for Chef API connection to %s with identity '%s' and %d bytes of credentials", m_chefApiURL, m_identity, m_credential.length());
        ContextBuilder cb = ContextBuilder.newBuilder("chef").endpoint(m_chefApiURL.toString()).credentials(m_identity, m_credential);
        LogUtils.debugf(getClass(), "Got ContextBuilder, building.");
        ChefContext con = cb.build();
        LogUtils.debugf(getClass(), "Connected to Chef API '%s' as user '%s'", con.getId(), con.getIdentity());
        ChefApi api = con.getApi();
        
        m_chefProvisioningConfig = new ChefProvisioningConfiguration(api);
        SearchResult<? extends Node> chefNodes = api.searchNodes();
        for (Node chefNode : chefNodes) {
            req.putNode(createRequisitionNode(chefNode));
        }
        
        return req;
    }

    /**
     * Creates an instance of the JaxB annotated RequisitionNode class.
     * 
     * @param chefNode a node object from the Chef API
     * @return a populated RequisitionNode based on the data for the
     *   Chef API node object
     */
    private RequisitionNode createRequisitionNode(Node chefNode) {
        RequisitionNode reqNode = new RequisitionNode();
        
        reqNode.setForeignId(chefNode.getName());
        reqNode.setBuilding(getForeignSource());
        reqNode.setNodeLabel(determineNodeLabel(chefNode));

        handleNodeCategories(chefNode, reqNode);
        handleInterfaces(chefNode, reqNode);
        // TODO: Assets

        return reqNode;
    }
    
    private String determineNodeLabel(Node chefNode) {
        String source = m_chefProvisioningConfig.getNodeLabelSource();
        if (!"name".equals(source)) {
            LogUtils.warnf(getClass(), "Node label source '%s' ignored -- sources other than 'name' are not yet implemented", source);
        }
        return chefNode.getName();
    }
    
    private void handleInterfaces(Node chefNode, RequisitionNode reqNode) {
        Set<RequisitionInterface> reqIfaces = new HashSet<RequisitionInterface>();
        
        JSONObject cloud = getCloudData(chefNode);
        JSONObject network = getAutomaticNetworkData(chefNode);
        
        String cloudProvider = "default";
        if (cloud != null) {
            cloudProvider = cloud.optString("provider", "default");
        }
        
        if (network == null && cloud == null) {
            LogUtils.warnf(getClass(), "Chef node '%s' in foreign-source '%s' has neither cloud nor network info in its automatic data, skipping interfaces altogether", chefNode.getName(), m_foreignSource);
            return;
        }
        
        if (cloud == null) {
            LogUtils.infof(getClass(), "Chef node '%s' in foreign-source '%s' has no automatic cloud data, not trying to make cloud interfaces", chefNode.getName(), m_foreignSource);
        } else if (! m_chefProvisioningConfig.isSuppressCloudInterfaces(cloud.optString("provider", "default"))) {
            if (! m_chefProvisioningConfig.isSuppressCloudLocalInterfaces(cloud.optString("provider", "default"))) {
                reqIfaces.addAll(makeCloudInterfaces(cloud, "private_ips"));
            }
            if (! m_chefProvisioningConfig.isSuppressCloudPublicInterfaces(cloud.optString("provider", "default"))) {
                reqIfaces.addAll(makeCloudInterfaces(cloud, "public_ips"));
            }
        }
        
        if (network == null && cloud != null) {
            LogUtils.infof(getClass(), "Chef node '%s' in foreign-source '%s' has no automatic network data, not trying to make non-cloud interfaces", chefNode.getName(), m_foreignSource);
        } else if (! m_chefProvisioningConfig.isSuppressNonCloudInterfaces(cloudProvider)) {
            reqIfaces.addAll(makeAutomaticInterfaces(network));
        }
        
        for (RequisitionInterface reqIf : reqIfaces) {
            reqNode.putInterface(reqIf);
        }
    }
    
    private Set<RequisitionInterface> makeCloudInterfaces(JSONObject cloud, String key) {
        Set<RequisitionInterface> ifaces = new HashSet<RequisitionInterface>();
        JSONArray ifaceArray = cloud.optJSONArray(key);
        for (int i = 0; i < ifaceArray.length(); i++) {
            String ifAddr = ifaceArray.optString(i);
            if (!"".equals(ifAddr)) {
                LogUtils.debugf(getClass(), "Adding cloud.%s member '%s' in foreign-source '%s'", key, ifAddr, m_foreignSource);
                RequisitionInterface reqIf = new RequisitionInterface();
                reqIf.setIpAddr(ifAddr);
                reqIf.setDescr("cloud(" + cloud.optString("provider") + ") " + key);
                reqIf.setManaged(Boolean.TRUE);
                reqIf.setStatus(Integer.valueOf(1));
                if ("public_ips".equals(m_chefProvisioningConfig.getSnmpPrimaryInterface()) && "public_ips".equals(key)) {
                    if (ifaces.size() == 0) {
                        reqIf.setSnmpPrimary(PrimaryType.PRIMARY);
                    } else {
                        reqIf.setSnmpPrimary(PrimaryType.SECONDARY);
                    }
                } else if ("private_ips".equals(m_chefProvisioningConfig.getSnmpPrimaryInterface()) && "private_ips".equals(key)) {
                    if (ifaces.size() == 0) {
                        reqIf.setSnmpPrimary(PrimaryType.PRIMARY);
                    } else {
                        reqIf.setSnmpPrimary(PrimaryType.SECONDARY);
                    }
                } else {
                    reqIf.setSnmpPrimary(PrimaryType.NOT_ELIGIBLE);
                }
                ifaces.add(reqIf);
            }
        }
        return ifaces;
    }
    
    private Set<RequisitionInterface> makeAutomaticInterfaces(JSONObject automatic) {
        Set<RequisitionInterface> ifaces = new HashSet<RequisitionInterface>();
        String ifAddr = automatic.optString("ipaddress");
        if ("".equals(ifAddr)) {
            LogUtils.warnf(getClass(), "Empty value for automatic.ipaddress, returning empty set of automatic interfaces in foreign-source '%s'", m_foreignSource);
            return ifaces;
        } else if (ifAddr.startsWith("127.") || ifAddr.toLowerCase().startsWith("fe80:")) {
            LogUtils.warnf(getClass(), "Value '%s' of automatic.ipaddress is link-local, returning empty set of automatic interfaces in foreign-source '%s'", ifAddr, m_foreignSource);
            return ifaces;
        } else {
            RequisitionInterface reqIf = new RequisitionInterface();
            reqIf.setIpAddr(ifAddr.trim().toLowerCase());
            reqIf.setDescr("automatic.ipaddress");
            reqIf.setManaged(Boolean.TRUE);
            reqIf.setStatus(Integer.valueOf(1));
            if ("automatic".equals(m_chefProvisioningConfig.getSnmpPrimaryInterface())) {
                if (ifaces.size() == 0) {
                    reqIf.setSnmpPrimary(PrimaryType.PRIMARY);
                } else {
                    reqIf.setSnmpPrimary(PrimaryType.SECONDARY);
                }
            } else {
                reqIf.setSnmpPrimary(PrimaryType.NOT_ELIGIBLE);
            }
            ifaces.add(reqIf);
        }
        return ifaces;
    }
    
    private void handleNodeCategories(Node chefNode, RequisitionNode reqNode) {
        reqNode.putCategory(new RequisitionCategory("ChefNode"));
        // TODO get Node.chefEnvironment implemented upstream in jclouds-chef
        reqNode.putCategory(new RequisitionCategory(makeNodeCategoryName("ChefEnv_", chefNode.getChefEnvironment())));
        
        Map<String,JsonBall> automatic = chefNode.getAutomatic();
        if (automatic.containsKey("roles")) {
            try {
                JSONArray roles = new JSONArray(automatic.get("roles").toString());
                for (int i = 0; i < roles.length(); i++) {
                    reqNode.putCategory(new RequisitionCategory(makeNodeCategoryName("ChefRole_", roles.getString(i))));
                }
            } catch (JSONException e) {
                LogUtils.warnf(getClass(), e, "Caught JSONException while extracting roles for Chef node '%s' in foreign-source '%s'. No role-based node categories will be assigned on resulting OpenNMS node.", chefNode.getName(), m_foreignSource);
            }
        }
    }
    
    private String makeNodeCategoryName(String prefix, String remainder) {
        StringBuilder bldr = new StringBuilder(prefix);
        bldr.append(remainder.trim().replaceAll("\\W", "_"));
        String result = bldr.toString();
        
        if (result.length() > 64) {
            String truncResult = result.substring(0, 64);
            LogUtils.warnf(getClass(), "Truncating generated node category name '%s' to '%s' (64-character limit)", result, truncResult);
            return truncResult;
        }
        return result;
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
            throw new MalformedURLException("The specified chef URL's first path component is '" + paths[0] + "' but it must be either 'http' or 'https': " +url);
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
            String[] paths = path.split("/", 3);
            endpointPath = paths[2];
        }
        return "/" + endpointPath;
    }
    
    protected JSONObject getCloudData(Node chefNode) {
        Map<String,JsonBall> automatic = chefNode.getAutomatic();
        if (!automatic.containsKey("cloud")) {
            LogUtils.debugf(getClass(), "Chef node '%s' has no automatic cloud data", chefNode.getName());
            return null;
        }
        try {
            return new JSONObject(chefNode.getAutomatic().get("cloud").toString());
        } catch (JSONException e) {
            LogUtils.warnf(getClass(), e, "Caught exception while getting automatic cloud data for Chef node '%s'", chefNode.getName());
            return null;
        }
    }
    
    protected JSONObject getAutomaticNetworkData(Node chefNode) {
        try {
            return new JSONObject(chefNode.getAutomatic().get("network").toString());
        } catch (JSONException e) {
            LogUtils.warnf(getClass(), e, "No automatic network data found on Chef node '%s' in foreign-source '%s', this should not be", chefNode.getName(), m_foreignSource);
            return null;
        }
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
    
    /**
     * @return A URL object representing the underlying endpoint for
     *   the Chef Server API
     */
    public URL getChefApiURL() {
        return m_chefApiURL;
    }
}
