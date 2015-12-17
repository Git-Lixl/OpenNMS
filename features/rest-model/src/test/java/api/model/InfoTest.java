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

package api.model;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.opennms.core.test.xml.JsonTest;
import org.opennms.core.test.xml.XmlTest;
import org.opennms.web.rest.api.model.Info;

public class InfoTest {
    @Test
    public void testMarshalAndUnmarshalJson() throws IOException {
        Info info = createInfo();
        String jsonString = JsonTest.marshalToJson(info);
        JsonTest.assertJsonEquals("{\n" +
                "  \"displayVersion\" : \"v1.0-Display-Version\",\n" +
                "  \"version\" : \"v1.0-Version\",\n" +
                "  \"packageName\" : \"opennms\",\n" +
                "  \"packageDescription\" : \"Debian package for OpenNMS v1.0\"\n" +
                "}", jsonString);

        Info unmarshalled = JsonTest.unmarshalFromJson(jsonString, Info.class);
        Assert.assertEquals(info, unmarshalled);
    }

    @Test
    public void testMarshalAndUnmarshalXml() {
        final Info info = createInfo();
        final String xmlString = XmlTest.marshalToXmlWithJaxb(info);
        XmlTest.assertXmlEquals("<info>\n" +
                "   <displayVersion>v1.0-Display-Version</displayVersion>\n" +
                "   <version>v1.0-Version</version>\n" +
                "   <packageName>opennms</packageName>\n" +
                "   <packageDescription>Debian package for OpenNMS v1.0</packageDescription>\n" +
                "</info>", xmlString);

        Info unmarshalled = XmlTest.unmarshalFromXmlWithJaxb(xmlString, Info.class);
        Assert.assertEquals(info, unmarshalled);
    }

    private Info createInfo() {
        Info info = new Info();
        info.setDisplayVersion("v1.0-Display-Version");
        info.setVersion("v1.0-Version");
        info.setPackageDescription("Debian package for OpenNMS v1.0");
        info.setPackageName("opennms");
        return info;
    }
}
