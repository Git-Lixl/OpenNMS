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

package org.opennms.netmgt.config.databaseReports;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;
import org.opennms.core.test.xml.XmlTestNoCastor;

public class DatabaseReportsConfigTest extends XmlTestNoCastor<DatabaseReports> {

    public DatabaseReportsConfigTest(final DatabaseReports sampleObject,
            final String sampleXml, final String schemaFile) {
        super(sampleObject, sampleXml, schemaFile);
    }

    @Parameters
    public static Collection<Object[]> data() throws ParseException {
        DatabaseReports reports = new DatabaseReports();
        
        Report calendarReport = new Report();
        calendarReport.setId("defaultCalendarReport");
        calendarReport.setDisplayName("Default calendar report");
        calendarReport.setReportService("availabilityReportService");
        calendarReport.setDescription("standard opennms report in calendar format");
        reports.addReport(calendarReport);

        Report classicReport = new Report();
        classicReport.setId("defaultClassicReport");
        classicReport.setDisplayName("Default classic report");
        classicReport.setReportService("availabilityReportService");
        classicReport.setDescription("standard opennms report in tabular format");
        reports.addReport(classicReport);

        return Arrays.asList(new Object[][] { {
            reports,
            "<database-reports>" +
              "<report id=\"defaultCalendarReport\" " +
                "display-name=\"Default calendar report\" " +
                "report-service=\"availabilityReportService\" " +
                "description=\"standard opennms report in calendar format\"/>" +
              "<report id=\"defaultClassicReport\" " +
                "display-name=\"Default classic report\" " +
                "report-service=\"availabilityReportService\" " +
                "description=\"standard opennms report in tabular format\"/>" +
            "</database-reports>",
            "target/classes/xsds/database-reports.xsd", }, });
    }

}
