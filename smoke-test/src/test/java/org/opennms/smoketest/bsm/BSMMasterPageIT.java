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

package org.opennms.smoketest.bsm;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.opennms.core.web.HttpClientWrapper;
import org.opennms.smoketest.VaadinSeleniumTestCase;

import com.google.common.net.MediaType;

public class BSMMasterPageIT extends VaadinSeleniumTestCase {

    private static final String BSM_REST_API_URL = BASE_URL + "opennms/api/v2/business-services";

    private String businessServiceCreatedUrl;

    @After
    public void deleteAllCreatedBusinessServices() throws IOException {
        if (businessServiceCreatedUrl != null) {
            // We have to delete the created Business Services in order to not let other tests fail
            try (HttpClientWrapper httpClient = HttpClientWrapper.create()) {
                httpClient.addBasicCredentials(BASIC_AUTH_USERNAME, BASIC_AUTH_PASSWORD);
                httpClient.usePreemptiveAuth();
                HttpDelete request = new HttpDelete(businessServiceCreatedUrl);
                request.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    Assert.assertEquals(200, response.getStatusLine().getStatusCode());
                }
            }
        }
    }

    @Test
    public void testMasterPage() throws IOException, InterruptedException {
        gotoMasterPage();
        findElementByXpath("//div[contains(text(), 'no Business Services defined')]");

        // create service
        String servicePrefix = UUID.randomUUID().toString();
        businessServiceCreatedUrl = createServiceWithPrefix(servicePrefix);

        // refresh page and check for entry
        gotoMasterPage();
        findElementByXpath("//div[contains(text(), '" + servicePrefix + "-name')]");
    }

    private void gotoMasterPage() {
        visit(Page.BSM_MASTER);
        // we are embedding vaadin in an iframe, so we have to switch there, otherwise findElements does not work
        switchToVaadinFrame();
    }

    /**
     * Creates a BusinessService with a single attribute.
     *
     * The name of the service, and its key-value attributes are all prefixed
     * with the given value.
     */
    private static String createServiceWithPrefix(String prefix) throws IOException {
        try (HttpClientWrapper httpClient = HttpClientWrapper.create()) {
            httpClient.addBasicCredentials(BASIC_AUTH_USERNAME, BASIC_AUTH_PASSWORD);
            httpClient.usePreemptiveAuth();
            HttpPost request = new HttpPost(BSM_REST_API_URL);
            request.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());


            try (CloseableHttpResponse response = httpClient.execute(request)) {
                Assert.assertEquals(201, response.getStatusLine().getStatusCode());
                // Determine the LOCATION of the created object to delete it afterwards
                Header[] locations = response.getHeaders("Location");
                Assert.assertNotNull(locations);
                return locations[0].getValue();
            }
        }
    }
}
