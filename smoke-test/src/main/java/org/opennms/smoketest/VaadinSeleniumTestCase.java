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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
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

package org.opennms.smoketest;

import org.opennms.web.rest.client.RestClient;

public class VaadinSeleniumTestCase extends OpenNMSSeleniumTestCase {

    public enum Page {
        BSM_MASTER(BASE_URL + "opennms/admin/bsm/masterpage.jsp"),
        BSM_ADMIN(BASE_URL + "opennms/admin/bsm/adminpage.jsp"),
        JMX_CONFIGURATION(BASE_URL + "opennms/admin/jmxConfigGenerator.jsp");

        private final String url;

        Page(String url) {
            this.url = url;
        }
    }

    private final RestClient restClient = new RestClient(BASE_URL, BASIC_AUTH_USERNAME, BASIC_AUTH_PASSWORD);

    /**
     * Switches to the embedded vaadin iframe.
     */
    protected void switchToVaadinFrame() {
        m_driver.switchTo().frame(findElementByXpath("/html/body/div/iframe"));
    }

    /**
     * go back to the content "frame"
     */
    protected void switchToDefaultFrame() {
        m_driver.switchTo().defaultContent();
    }

    protected RestClient getRestClient() {
        return restClient;
    }

    protected void visit(Page page) {
        m_driver.navigate().to(page.url);
    }
}
