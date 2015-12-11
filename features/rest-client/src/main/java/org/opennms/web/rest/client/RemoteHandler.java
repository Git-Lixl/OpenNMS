/*******************************************************************************
 * This file is part of OpenNMS(R).
 * <p>
 * Copyright (C) 2015 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2015 The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR RemoteHandler PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 * http://www.gnu.org/licenses/
 * <p>
 * For more information contact:
 * OpenNMS(R) Licensing <license@opennms.org>
 * http://www.opennms.org/
 * http://www.opennms.com/
 *******************************************************************************/

package org.opennms.web.rest.client;

import java.net.URL;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.common.util.Base64Utility;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.opennms.web.rest.client.exceptions.ConnectionRefusedException;
import org.opennms.web.rest.client.exceptions.NotFoundException;
import org.opennms.web.rest.client.exceptions.RestClientException;
import org.opennms.web.rest.client.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles requests to the OpenNMS Rest API.
 *
 * @author Markus von Rüden
 */
public class RemoteHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteHandler.class);

    private static boolean isConnectionRefused(Response response) {
        int responseCode = response.getStatus();
        String responseReason = response.getStatusInfo().getReasonPhrase();

        boolean connectionRefused = responseCode == 500 && responseReason != null && responseReason.contains("Connection") && responseReason.contains("refused");
        return connectionRefused;
    }

    private final String authorizationHeader;

    private final URL url;

    public RemoteHandler(URL url, String username, String password) {
        this.url = url;
        authorizationHeader = "Basic " + Base64Utility.encode((username + ":" + password).getBytes());

    }

    /**
     * Calls a resource at the OpenNMS Rest service. The service url is provided by path.
     * The expected response type is provided by genericType.
     * If you want to send data to the server you should provide an entity.
     * If the response status is not 200 an exception is thrown.
     *
     * @param httpMethod          The http method to use (e.g. POST, GET,...). Must not be null.
     * @param path                The remote path to invoke (e.g. http://backup.opennms.com/some/path). Must not be null.
     * @param responseGenericType The response type encapsulated in a genericType.
     * @param requestEntity       The entity to send to the server. May be null
     * @param <T>                 The type of the response entity.
     * @param <X>                 The type of the request entity.
     * @return
     * @throws RestClientException if the response status is != 200.
     */
    public <T, X> T remoteInvoke(final String httpMethod,
                                 final String path,
                                 final GenericType<T> responseGenericType,
                                 final Entity<X> requestEntity,
                                 ResponseCallback<T> sucessCallback,
                                 ResponseCallback<Void> errorCallback) throws RestClientException {
        final Client client = ClientBuilder.newClient();
        client.register(JacksonJaxbJsonProvider.class);
        WebTarget target = client.target(String.format("http://%s:%d/opennms", url.getHost(), url.getPort()));
        target = target.path(path);
        LOG.debug("{}: {} with entity {}", httpMethod, target.getUri(), requestEntity);

        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE, MediaType.TEXT_HTML_TYPE);
        invocationBuilder.header("Authorization", authorizationHeader);

        try {
            Response response = invocationBuilder.method(httpMethod, requestEntity);
            LOG.debug("{}: {}: {} ({})", httpMethod, target.getUri(), response.getStatus(), response.getStatusInfo().getReasonPhrase());
            if (isSucessful(response) || isRedirect(response)) { // everything was fine
                sucessCallback = sucessCallback == null ? createDefaultSuccessCallback(responseGenericType) : sucessCallback;
                return sucessCallback.callback(response);
            } else {
                errorCallback = errorCallback == null ? createDefaultErrorCallback(path) : errorCallback;
                errorCallback.callback(response);
            }
            // unexpected response
            throw new RestClientException(String.format("Unexpected response received. (Expected: %s, Actual: %s)", "200-299", response.getStatus()));
        } catch (ProcessingException ex) {
            throw new RestClientException(ex.getMessage(), ex.getCause());
        }
    }

    public <T, X> T remoteInvoke(final String httpMethod,
                                 final String path,
                                 final GenericType<T> responseGenericType,
                                 final Entity<X> requestEntity) throws RestClientException {
        return remoteInvoke(httpMethod, path, responseGenericType, requestEntity, createDefaultSuccessCallback(responseGenericType), createDefaultErrorCallback(path));
    }

    public <T> T remoteInvoke(final String httpMethod,
                                        final String path,
                                        final Class<T> responseClass,
                                        final Entity<Object> entity) throws RestClientException {
        if (responseClass != null) {
            final GenericType<T> genericType = new GenericType<>(responseClass);
            return remoteInvoke(httpMethod, path, genericType, entity);
        }
        return remoteInvoke(httpMethod, path, (GenericType<T>) null, entity);
    }

    public <T> T remoteInvoke(final String httpMethod,
                              final String path,
                              final Class<T> responseClass,
                              final Entity<Object> entity,
                              final ResponseCallback<T> successCallback,
                              final ResponseCallback<Void> errorCallback) throws RestClientException {
        final GenericType<T> genericType = new GenericType<>(responseClass);
        return remoteInvoke(httpMethod, path, genericType, entity, successCallback, errorCallback);
    }


    private ResponseCallback<Void> createDefaultErrorCallback(String path) {
        return new ResponseCallback<Void>() {
            @Override
            public Void callback(Response response) throws RestClientException {
                if (response.getStatus() == 401 || response.getStatus() == 403) {
                    throw new UnauthorizedException(response.getStatus(), response.getStatusInfo().getReasonPhrase());
                } else if (isConnectionRefused(response)) {
                    throw new ConnectionRefusedException(path.toString());
                } else if (response.getStatus() == 404) {
                    throw new NotFoundException(path);
                }
                return null;
            }
        };
    }

    private <T> ResponseCallback<T> createDefaultSuccessCallback(final GenericType<T> responseGenericType) {
        return new ResponseCallback<T>() {
            @Override
            public T callback(Response response) throws RestClientException {
                if (responseGenericType != null) {
                    T responseEntity = response.readEntity(responseGenericType);
                    return responseEntity;
                }
                return null;
            }
        };
    }

    private boolean isSucessful(Response response) {
        return Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily());
    }

    private boolean isRedirect(Response response) {
        return Response.Status.Family.REDIRECTION.equals(response.getStatusInfo().getFamily());
    }

}