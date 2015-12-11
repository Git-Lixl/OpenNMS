package org.opennms.web.rest.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.HttpMethod;

import org.opennms.web.rest.api.model.Info;
import org.opennms.web.rest.client.api.BusinessServiceAPI;
import org.opennms.web.rest.client.api.ForeignSourceAPI;
import org.opennms.web.rest.client.api.IpInterfaceAPI;
import org.opennms.web.rest.client.api.MonitoredServiceAPI;
import org.opennms.web.rest.client.api.NodeAPI;
import org.opennms.web.rest.client.api.RequisitionAPI;
import org.opennms.web.rest.client.exceptions.RestClientException;

/**
 * RemoteHandler ReST API client for OpenNMS.
 *
 * Uses CXF to perform automatic marshaling/unmarshaling of request and
 * response objects.
 *
 * @author jwhite
 */
public class RestClient {

    private final RemoteHandler remoteHandler;

    /**
     * @param url The base url of the opennms web server (without context path "opennms")
     * @param username The username to use as basic auth username
     * @param password The password to use as basic auth password.
     */
    public RestClient(String url, String username, String password) {
        try {
            this.remoteHandler = new RemoteHandler(new URL(url), username, password);
        } catch (MalformedURLException ex) {
            throw new RestClientException("Error while creating a RestClient instance", ex);
        }
    }

    public Info info() {
        return remoteHandler.remoteInvoke(HttpMethod.GET, "info", Info.class, null);
    }

    public NodeAPI nodes() {
        return new NodeAPI(remoteHandler);
    }

    public BusinessServiceAPI businessServices() {
        return new BusinessServiceAPI(remoteHandler);
    }

    public RequisitionAPI requisitions() {
        return new RequisitionAPI(remoteHandler);
    }

    public ForeignSourceAPI foreignSources() {
        return new ForeignSourceAPI(remoteHandler);
    }

    public IpInterfaceAPI ipInterfaces() {
        return new IpInterfaceAPI(remoteHandler);
    }

    public MonitoredServiceAPI monitoredServices() {
        return new MonitoredServiceAPI(remoteHandler);
    }
}
