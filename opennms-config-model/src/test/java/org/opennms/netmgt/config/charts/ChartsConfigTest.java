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

package org.opennms.netmgt.config.charts;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;
import org.opennms.core.test.xml.XmlTestNoCastor;

public class ChartsConfigTest extends XmlTestNoCastor<ChartConfiguration> {

    public ChartsConfigTest(final ChartConfiguration sampleObject,
            final String sampleXml, final String schemaFile) {
        super(sampleObject, sampleXml, schemaFile);
    }

    @Parameters
    public static Collection<Object[]> data() throws ParseException {
        
        BarChart barChart = new BarChart();
        barChart.setName("sample-bar-chart");
        barChart.setVariation("2d");
        barChart.setDomainAxisLabel("Severity");
        barChart.setShowLegend(true);
        barChart.setPlotOrientation("vertical");
        barChart.setDrawBarOutline(true);
        barChart.setRangeAxisLabel("Count");
        barChart.setSubLabelClass("org.opennms.web.charts.SeveritySubLabels");
        barChart.setShowUrls(false);
        barChart.setShowToolTips(false);
        
        Title title = new Title();
        title.setFont("SansSerif");
        title.setStyle("");
        title.setValue("Alarms");
        title.setPitch(12);
        barChart.setTitle(title);

        ImageSize imageSize = new ImageSize();
        HzSize hzSize = new HzSize();
        hzSize.setPixels(450);
        imageSize.setHzSize(hzSize);
        VtSize vtSize = new VtSize();
        vtSize.setPixels(300);
        imageSize.setVtSize(vtSize);
        barChart.setImageSize(imageSize);

        SubTitle subTitle = new SubTitle();
        subTitle.setPosition("top");
        subTitle.setHorizontalAlignment("center");
        Title subTitleTitle = new Title();
        subTitleTitle.setFont("SansSerif");
        subTitleTitle.setStyle("");
        subTitleTitle.setValue("Severity Chart");
        subTitleTitle.setPitch(10);
        subTitle.setTitle(subTitleTitle);
        barChart.addSubTitle(subTitle);

        GridLines gridLines = new GridLines();
        gridLines.setVisible(true);
        Rgb rgb = new Rgb();
        Red red = new Red();
        red.setRgbColor(255);        
        rgb.setRed(red);
        Green green = new Green();
        green.setRgbColor(255);
        rgb.setGreen(green);
        Blue blue = new Blue();
        blue.setRgbColor(255);
        rgb.setBlue(blue);
        gridLines.setRgb(rgb);
        barChart.setGridLines(gridLines);

        SeriesDef seriesDef = new SeriesDef();
        seriesDef.setNumber(1);
        seriesDef.setSeriesName("Events");
        seriesDef.setUseLabels(true);
        
        JdbcDataSet jdbcDataSet = new JdbcDataSet();
        jdbcDataSet.setDbName("opennms");
        jdbcDataSet.setSql("select * from events");
        seriesDef.setJdbcDataSet(jdbcDataSet);
 
        Rgb seriesRgb = new Rgb();
        seriesRgb.setRed(red);
        seriesRgb.setGreen(green);
        Blue noBlue = new Blue();
        noBlue.setRgbColor(0);
        seriesRgb.setBlue(noBlue);
        seriesDef.setRgb(seriesRgb);

        barChart.addSeriesDef(seriesDef);

        ChartConfiguration chartConfig = new ChartConfiguration();
        chartConfig.addBarChart(barChart);

        return Arrays.asList(new Object[][] { {
                chartConfig,
                "<chart-configuration>" +
                "<bar-chart name=\"sample-bar-chart\" " +
                  "variation=\"2d\" " +
                  "domain-axis-label=\"Severity\" " +
                  "show-legend=\"true\" " +
                  "plot-orientation=\"vertical\" " +
                  "draw-bar-outline=\"true\" " +
                  "range-axis-label=\"Count\" " +
                  "sub-label-class=\"org.opennms.web.charts.SeveritySubLabels\" " +
                  "show-urls=\"false\" " +
                  "show-tool-tips=\"false\">" +
                  "<title font=\"SansSerif\" style=\"\" value=\"Alarms\" pitch=\"12\" />" +
                  "<image-size>" +
                    "<hz-size>" +
                      "<pixels>450</pixels>" +
                    "</hz-size>" +
                    "<vt-size>" +
                      "<pixels>300</pixels>" +
                    "</vt-size>" +
                  "</image-size>" +
                  "<sub-title position=\"top\" horizontal-alignment=\"center\">" +
                    "<title font=\"SansSerif\" style=\"\" value=\"Severity Chart\" pitch=\"10\" />" +
                  "</sub-title>" +
                  "<grid-lines visible=\"true\">" +
                    "<rgb>" +
                        "<red>" +
                            "<rgb-color>255</rgb-color>" +
                        "</red>" +
                        "<green>" +
                            "<rgb-color>255</rgb-color>" +
                        "</green>" +
                        "<blue>" +
                            "<rgb-color>255</rgb-color>" +
                        "</blue>" +
                    "</rgb>" +
                  "</grid-lines>" +
                  "<series-def number=\"1\" series-name=\"Events\" use-labels=\"true\" >" +
                    "<jdbc-data-set db-name=\"opennms\" sql=\"select * from events\" />" +
                    "<rgb>" +
                      "<red>" +
                        "<rgb-color>255</rgb-color>" +
                      "</red>" +
                      "<green>" +
                        "<rgb-color>255</rgb-color>" +
                      "</green>" +
                      "<blue>" +
                        "<rgb-color>0</rgb-color>" +
                      "</blue>" +
                    "</rgb>" +
                  "</series-def>" +
                "</bar-chart>" +
                "</chart-configuration>",
                "target/classes/xsds/chart-configuration.xsd", }, });
    }
}
