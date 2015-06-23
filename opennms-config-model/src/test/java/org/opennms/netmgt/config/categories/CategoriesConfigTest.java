/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2013-2015 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.config.categories;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;
import org.opennms.core.test.xml.XmlTestNoCastor;

public class CategoriesConfigTest extends XmlTestNoCastor<Catinfo> {

    public CategoriesConfigTest(final Catinfo sampleObject,
            final String sampleXml, final String schemaFile) {
        super(sampleObject, sampleXml, schemaFile);
    }

    @Parameters
    public static Collection<Object[]> data() throws ParseException {
        Catinfo catinfo = new Catinfo();
        
        Header header = new Header();
        header.setRev("1.3");
        header.setCreated("Wednesday, February 6, 2002 10:10:00 AM EST");
        header.setMstation("checkers");
        catinfo.setHeader(header);

        Categorygroup categoryGroup = new Categorygroup();
        categoryGroup.setName("WebConsole");
        categoryGroup.setComment("Service Level Availability by Functional Group");
        
        Common common = new Common();
        common.setRule("IPADDR != '0.0.0.0'");
        categoryGroup.setCommon(common);

        Category category = new Category();
        category.setLabel("Overall Service Availability");
        category.setComment("This category reflects availability of all services currently being monitored by OpenNMS.");
        category.setNormal(99.99);
        category.setWarning(97);
        category.setRule("IPADDR != '0.0.0.0'");

        Categories categories = new Categories();
        categories.addCategory(category);
        categoryGroup.setCategories(categories);
        catinfo.addCategorygroup(categoryGroup);

        return Arrays.asList(new Object[][] { {
                catinfo,
                "<catinfo>" +
                "<header>" +
                  "<rev>1.3</rev>" +
                  "<created>Wednesday, February 6, 2002 10:10:00 AM EST</created>" +
                  "<mstation>checkers</mstation>" +
                "</header>" +
                "<categorygroup>" +
                  "<name>WebConsole</name>" +
                  "<comment>Service Level Availability by Functional Group</comment>" +
                  "<common>" +
                    "<rule>IPADDR != '0.0.0.0'</rule>" +
                  "</common>" +
                  "<categories>" +
                    "<category>" +
                    "<label>Overall Service Availability</label>" +
                    "<comment>This category reflects availability of all services currently being monitored by OpenNMS.</comment>" +
                    "<normal>99.99</normal>" + 
                    "<warning>97.0</warning>" +
                    "<rule>IPADDR != '0.0.0.0'</rule>" +
                    "</category>" +
                  "</categories>" +
                "</categorygroup>" +
                "</catinfo>",
                "target/classes/xsds/categories.xsd", }, });
    }
}
