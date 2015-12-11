/*******************************************************************************
 * This file is part of OpenNMS(R).
 * <p>
 * Copyright (C) 2011-2015 The OpenNMS Group, Inc.
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

import java.util.Iterator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.opennms.netmgt.bsm.service.model.BusinessServiceDTO;
import org.opennms.netmgt.bsm.service.model.IpServiceDTO;
import org.opennms.netmgt.model.OnmsMonitoredServiceDetail;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.smoketest.OpenNMSSeleniumTestCase;
import org.opennms.smoketest.RequisitionUtils;
import org.opennms.smoketest.VaadinSeleniumTestCase;
import org.opennms.web.rest.v2.bsm.model.BusinessServiceListDTO;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BSMDaemonIT extends VaadinSeleniumTestCase {

    private static final String BASIC_SERVICE_NAME = "BasicService";

    private final RequisitionUtils requisitionUtils = new RequisitionUtils(this);

    @Before
    public void createTestSetup() throws Exception {
        String requisitionXML = "<model-import foreign-source=\"" + OpenNMSSeleniumTestCase.REQUISITION_NAME + "\">" +
                "<node foreign-id=\"NodeA\" node-label=\"NodeA\">" +
                "<interface ip-addr=\"::1\" status=\"1\" snmp-primary=\"N\">" +
                "<monitored-service service-name=\"AAA\"/>" +
                "<monitored-service service-name=\"BBB\"/>" +
                "</interface>" +
                "<interface ip-addr=\"127.0.0.1\" status=\"1\" snmp-primary=\"N\">" +
                "<monitored-service service-name=\"CCC\"/>" +
                "<monitored-service service-name=\"DDD\"/>" +
                "</interface>" +
                "</node>" +
                "</model-import>";

        String foreignSourceXML = "<foreign-source name=\"" + OpenNMSSeleniumTestCase.REQUISITION_NAME + "\">\n" +
                "<scan-interval>1d</scan-interval>\n" +
                "<detectors/>\n" +
                "<policies/>\n" +
                "</foreign-source>";
        requisitionUtils.setupTestRequisition(requisitionXML, foreignSourceXML);
        wait.until(requisitionUtils.waitForNodesInDatabase(1));
    }

    @After
    public void removeTestSetup() throws Exception {
        requisitionUtils.deleteNode("NodeA");
        requisitionUtils.deleteForeignSource();
        removeAllBusinessServices();
    }

    private void removeAllBusinessServices() {
        BusinessServiceListDTO list = getRestClient().businessServices().list();
        Iterator<BusinessServiceDTO> iterator = list.iterator();
        while(iterator.hasNext()) {
            getRestClient().businessServices().delete(iterator.next());
        }
    }

    private BusinessServiceDTO createBusinessService() {
        //Create BusinessService
        BusinessServiceDTO businessServiceDTO = new BusinessServiceDTO();
        businessServiceDTO.setName(BASIC_SERVICE_NAME);

        OnmsNode node = getRestClient().nodes().get(OpenNMSSeleniumTestCase.REQUISITION_NAME, "NodeA");
        OnmsMonitoredServiceDetail service1 = getRestClient().monitoredServices().get(node.getNodeId(), "127.0.0.1", "CCC");
        OnmsMonitoredServiceDetail service2 = getRestClient().monitoredServices().get(node.getNodeId(), "127.0.0.1", "DDD");
        OnmsMonitoredServiceDetail[] services = new OnmsMonitoredServiceDetail[]{service1, service2};

        for (OnmsMonitoredServiceDetail eachService : services) {
            IpServiceDTO ipServiceDTO = new IpServiceDTO();
            ipServiceDTO.setId(eachService.getId());
            businessServiceDTO.addIpService(ipServiceDTO);
        }
        return getRestClient().businessServices().create(businessServiceDTO);
    }


    /**
     * This IT verifies that the BSM-Daemon knows about new Business Services.
     * See http://issues.opennms.org/browse/BSM-78 for more details.
     */
    @Test
    public void verifyBsmDaemonKnowsAboutNewBusinessServices() throws Exception {
        BusinessServiceDTO bs = createBusinessService();
        visit(Page.BSM_MASTER);
        switchToVaadinFrame();
        WebElement serviceElement = findElementByXpath(String.format("//div[@id='service-%s']//div[contains(text(), '%s')]", bs.getId(), bs.getName()));
        Assert.assertTrue("The created service must be of severity Normal", serviceElement.getAttribute("class").contains("Normal"));
    }

    /**
     * This IT verifies that the BSM-Daemon knows about new Business Services.
     * See http://issues.opennms.org/browse/BSM-79 for more details.
     */
    @Test
    public void verifyTodo() throws Exception {
        // TODO implement me
        throw new IllegalArgumentException("Test is not implemented yet");
    }
}
