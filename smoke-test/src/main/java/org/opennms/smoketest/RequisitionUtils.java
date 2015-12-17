/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2015 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2015 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/
package org.opennms.smoketest;

import java.io.IOException;
import java.util.Iterator;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsNodeList;
import org.opennms.netmgt.provision.persist.foreignsource.ForeignSource;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.web.rest.api.model.IpInterfaceDTO;
import org.opennms.web.rest.api.model.IpInterfaceListDTO;
import org.opennms.web.rest.client.RestClient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for dealing with provisioning requisitions.
 *
 * The {@link OpenNMSSeleniumTestCase} is meant to be version agnostic,
 * so we maintain these methods here instead.
 *
 * @author jwhite
 */
public class RequisitionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(RequisitionUtils.class);

    private final OpenNMSSeleniumTestCase m_testCase;

    private final RestClient m_restClient;

    public RequisitionUtils(OpenNMSSeleniumTestCase testCase) {
        m_testCase = testCase;
        m_restClient = new RestClient(OpenNMSSeleniumTestCase.BASE_URL, OpenNMSSeleniumTestCase.BASIC_AUTH_USERNAME, OpenNMSSeleniumTestCase.BASIC_AUTH_PASSWORD);
    }

    public void createNode(String nodeXML) throws IOException, InterruptedException {
        m_restClient.nodes().create(JaxbUtils.unmarshal(OnmsNode.class, nodeXML));
    }

    // TODO refactor to use one single restClient convenient method
    public void setupTestRequisition(String requisitionXML, String foreignSourceXML) throws IOException, InterruptedException {
        final Requisition requisition = JaxbUtils.unmarshal(Requisition.class, requisitionXML);
        m_restClient.requisitions().create(requisition);
        if (foreignSourceXML != null && !"".equals(foreignSourceXML)) {
            m_restClient.foreignSources().create(JaxbUtils.unmarshal(ForeignSource.class, foreignSourceXML));
        }
        m_restClient.requisitions().startImport(OpenNMSSeleniumTestCase.REQUISITION_NAME);
    }

    // TODO refactor to use one single restClient convenient method
    public void deleteNode(final String foreignId) throws IOException, InterruptedException {
        // At the moment we cannot delete the whole node object tree. We have to manually disconnect some links, eg. the ip interfaces
        IpInterfaceListDTO list = m_restClient.ipInterfaces().list(OpenNMSSeleniumTestCase.REQUISITION_NAME, foreignId);
        Iterator<IpInterfaceDTO> iterator = list.iterator();
        while (iterator.hasNext()) {
            IpInterfaceDTO ipInterfaceDTO = iterator.next();
            m_restClient.ipInterfaces().delete(OpenNMSSeleniumTestCase.REQUISITION_NAME, foreignId, ipInterfaceDTO.getIpAddress().toString());
        }
        m_restClient.nodes().delete(OpenNMSSeleniumTestCase.REQUISITION_NAME, foreignId);
    }

    public void deleteForeignSource() throws IOException, InterruptedException {
        m_restClient.foreignSources().delete(OpenNMSSeleniumTestCase.REQUISITION_NAME);
    }

    // TODO refactor to use restClient
    public void deleteTestRequisition() throws Exception {
        final Integer responseCode = m_testCase.doRequest(new HttpGet(OpenNMSSeleniumTestCase.BASE_URL + "/opennms/rest/requisitions/" + OpenNMSSeleniumTestCase.REQUISITION_NAME));
        LOG.debug("Checking for existing test requisition: {}", responseCode);
        if (responseCode == 404 || responseCode == 204) {
            LOG.debug("deleteTestRequisition: already deleted");
            return;
        }
        for (OnmsNode node : getNodesInDatabase()) {
            m_testCase.doRequest(new HttpDelete(OpenNMSSeleniumTestCase.BASE_URL + "/opennms/rest/nodes/" + node.getId()));
        }
        m_testCase.doRequest(new HttpDelete(OpenNMSSeleniumTestCase.BASE_URL + "/opennms/rest/requisitions/" + OpenNMSSeleniumTestCase.REQUISITION_NAME));
        m_testCase.doRequest(new HttpDelete(OpenNMSSeleniumTestCase.BASE_URL + "/opennms/rest/requisitions/deployed/" + OpenNMSSeleniumTestCase.REQUISITION_NAME));
        m_testCase.doRequest(new HttpDelete(OpenNMSSeleniumTestCase.BASE_URL + "/opennms/rest/foreignSources/" + OpenNMSSeleniumTestCase.REQUISITION_NAME));
        m_testCase.doRequest(new HttpDelete(OpenNMSSeleniumTestCase.BASE_URL + "/opennms/rest/foreignSources/deployed/" + OpenNMSSeleniumTestCase.REQUISITION_NAME));
        m_testCase.doRequest(new HttpGet(OpenNMSSeleniumTestCase.BASE_URL + "/opennms/rest/requisitions"));
    }

    public OnmsNodeList getNodesInDatabase() {
        return m_restClient.nodes().list();
    }

    public ExpectedCondition<Boolean> waitForNodesInDatabase(int numberOfNodesToMatch) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return getNodesInDatabase().size() == numberOfNodesToMatch;
            }
        };
    }

    public RestClient getRestClient() {
        return m_restClient;
    }
}
