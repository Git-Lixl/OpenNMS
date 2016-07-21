/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
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

package org.opennms.web.controller.trend;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXB;

import org.apache.commons.lang.StringUtils;
import org.opennms.core.db.DataSourceFactory;
import org.opennms.netmgt.config.trend.TrendAttribute;
import org.opennms.netmgt.config.trend.TrendConfiguration;
import org.opennms.netmgt.config.trend.TrendDefinition;
import org.opennms.web.controller.support.SupportController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class TrendController extends AbstractController implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(TrendController.class);
    private final File CONFIG_FILE = new File("etc/trend-configuration.xml");

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView modelAndView = new ModelAndView("trend/trend");

        LOG.error("BMRHGA: handle request for definition name '{}'", request.getParameter("name"));

        TrendDefinition trendDefinition = getConfiguration().getTrendDefintionForName(request.getParameter("name"));

        if (trendDefinition != null) {
            final List<Double> valuesList = lookupData(trendDefinition.getQuery());
            final String valuesString = StringUtils.join(valuesList, ',');


            modelAndView.addObject("title", trendDefinition.getTitle());
            LOG.error("BMRHGA: title '{}'", trendDefinition.getTitle());

            modelAndView.addObject("description", trendDefinition.getDescription());
            LOG.error("BMRHGA: description '{}'", trendDefinition.getDescription());

            modelAndView.addObject("type", trendDefinition.getType());
            LOG.error("BMRHGA: type '{}'", trendDefinition.getType());

            modelAndView.addObject("values", valuesList);

            modelAndView.addObject("valuesString", valuesString);
            LOG.error("BMRHGA: values and valuesString '{}'", valuesString);


            for (TrendAttribute trendAttribute : trendDefinition.getTrendAttributes()) {
                modelAndView.addObject(trendAttribute.getKey(), trendAttribute.getValue());
                LOG.error("BMRHGA: attribute '{}'='{}'", trendAttribute.getKey(), trendAttribute.getValue());
            }
        } else {
            LOG.error("BMRHGA: trend definition is null for name '{}'", request.getParameter("name"));
        }

        LOG.error("BMRHGA: returning model for name '{}'", trendDefinition.getName());

        return modelAndView;
    }

    public TrendConfiguration getConfiguration() {
        return JAXB.unmarshal(CONFIG_FILE, TrendConfiguration.class);
    }

    public List<Double> lookupData(String query) throws SQLException {
        List<Double> dataSet = new ArrayList<Double>();

        Connection conn = null;

        try {
            conn = DataSourceFactory.getInstance().getConnection();

            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                dataSet.add(resultSet.getDouble(1));
            }

            resultSet.close();
            statement.close();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return dataSet;
    }

    @Override
    public void afterPropertiesSet() {
    }
}
