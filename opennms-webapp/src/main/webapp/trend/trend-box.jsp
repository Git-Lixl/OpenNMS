<%--
/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2002-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
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

--%>

<%@page language="java" contentType="text/html" session="true" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%--

.severity .severity-critical, .severity .severity-Critical {
  background-color: #f5cdcd;
}

.severity .severity-major, .severity .severity-Major {
  background-color: #ffd7cd;
}

.severity .severity-minor, .severity .severity-Minor {
  background-color: blanchedalmond;
}

.severity .severity-warning, .severity .severity-Warning {
  background-color: #fff5cd;
}

.severity .severity-indeterminate, .severity .severity-Indeterminate {
  background-color: #ebebcd;
}

.severity .severity-normal, .severity .severity-Normal {
  background-color: #d7e1cd;
}

.severity .severity-cleared, .severity .severity-Cleared {
  background-color: #eeeeee;
}

--%>

<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Trend</h3>
    </div>

    <div class="panel-body">
        <div class="row gutter-10">
            <div class="col-xs-4">
                <jsp:include page="/trend/trend.htm" flush="false">
                    <jsp:param name="name" value="def1"/>
                </jsp:include>
            </div>
            <div class="col-xs-4">
                <jsp:include page="/trend/trend.htm" flush="false">
                    <jsp:param name="name" value="def2"/>
                </jsp:include>
            </div>
            <div class="col-xs-4">
                <jsp:include page="/trend/trend.htm" flush="false">
                    <jsp:param name="name" value="def3"/>
                </jsp:include>
            </div>
            <div class="col-xs-4">
                <jsp:include page="/trend/trend.htm" flush="false">
                    <jsp:param name="name" value="def4"/>
                </jsp:include>
            </div>
        </div>
    </div>
</div>


