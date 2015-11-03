/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2015 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.notifd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.core.test.OpenNMSJUnit4ClassRunner;
import org.opennms.core.test.db.annotations.JUnitTemporaryDatabase;
import org.opennms.core.test.http.JUnitHttpServerExecutionListener;
import org.opennms.core.test.http.annotations.JUnitHttpServer;
import org.opennms.core.test.http.annotations.Webapp;
import org.opennms.netmgt.model.notifd.Argument;
import org.opennms.netmgt.model.notifd.NotificationStrategy;
import org.opennms.test.JUnitConfigurationEnvironment;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OpenNMSJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:/META-INF/opennms/applicationContext-soa.xml",
        "classpath:/META-INF/opennms/applicationContext-mockDao.xml",
        "classpath*:/META-INF/opennms/component-dao.xml",
        "classpath:/META-INF/opennms/applicationContext-daemon.xml",
        "classpath:/META-INF/opennms/applicationContext-commonConfigs.xml",
        "classpath:/META-INF/opennms/applicationContext-minimal-conf.xml"
})
@JUnitConfigurationEnvironment
@JUnitTemporaryDatabase
public class MattermostNotificationStrategyIT {
    /*
     * Test method for 'org.opennms.netmgt.notifd.MattermostNotificationStrategy.send(List)'
     */
    @Test
    @JUnitHttpServer(webapps={
            @Webapp(context="/hooks", path="src/test/resources/MattermostNotificationStrategyTest")
    })
    public void testSendValidJustArgs() {
        final int port = JUnitHttpServerExecutionListener.getPort();
        assertTrue(port > 0);
        try {

            final NotificationStrategy ns = new MattermostNotificationStrategy();
            final List<Argument> arguments = new ArrayList<Argument>();

            arguments.add(new Argument("url", null, "http://localhost:" + port + "/hooks/abunchofstuffthatidentifiesawebhook", false));
            arguments.add(new Argument("-subject", null, "Test", false));
            arguments.add(new Argument("-tm", null, "This is only a test", false));
            
            System.setProperty("org.opennms.netmgt.notifd.mattermost.channel", "integrationtests");
            System.setProperty("org.opennms.netmgt.notifd.mattermost.iconURL", "http://opennms.org/logo.png");

            final int statusCode = ns.send(arguments);

            assertEquals(0, statusCode);
            
            final JSONObject inputJson = MattermostNotificationStrategyTestServlet.getInputJson();
            assertNotNull(inputJson);
            assertEquals(4, inputJson.size());
            assertEquals("opennms", inputJson.get("username"));
            assertEquals("#### Test\nThis is only a test", inputJson.get("text"));
            assertEquals("integrationtests", inputJson.get("channel"));
            assertEquals("http://opennms.org/logo.png", inputJson.get("icon_url"));
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Caught Exception: " + e.getMessage());
        }
    }

}
