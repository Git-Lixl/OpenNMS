/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2011 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.provision.service.chef;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.core.test.MockLogAppender;
import org.opennms.netmgt.provision.service.ProvisioningUrlFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This class tests the new "chef" protocol handling created for the Provisioner.
 * 
 * @author <a href="mailto:jeffg@opennms.org">Jeff Gehlbach</a>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/META-INF/opennms/empty-context.xml"})
public class ChefRequisitionUrlConnectionTest {

    private static final String TEST_URL = "chef://jeffg@api.opscode.com/https/hosted_chef/organizations/jeffgtesting";

    @BeforeClass
    static public void setUp() {
        MockLogAppender.setupLogging();
    }

    @Before
    public void registerFactory() {
        
        try {
            new URL(TEST_URL);
        } catch (MalformedURLException e) {
            URL.setURLStreamHandlerFactory(new ProvisioningUrlFactory());
        }
        
    }
    
    @Test
    public void hcValidateChefApiURL() {
        
        MalformedURLException e = null;
        
        try {
            ChefRequisitionUrlConnection c = new ChefRequisitionUrlConnection(new URL(TEST_URL));
            Assert.assertEquals("https://api.opscode.com:443/organizations/jeffgtesting", c.getChefApiURL().toString());
            Assert.assertEquals("hosted_chef", c.getForeignSource());
            
        } catch (MalformedURLException e1) {
            e = e1;
        }
        Assert.assertNull(e);
    }

    @Test
    public void hcGetInputStream() throws IOException {
        URLConnection c = new ChefRequisitionUrlConnection(new URL(TEST_URL));
        InputStream s = c.getInputStream();
        Assert.assertNotNull(s);
        s.close();
    }
}
