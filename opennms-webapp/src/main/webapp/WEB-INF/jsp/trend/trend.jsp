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

<%@ page language="java" contentType="text/html" session="true" import="org.opennms.netmgt.config.trend.TrendAttribute"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<style type="text/css">

    .alert-trend {
        background-color: #4c9d29;
        border-color: #4c9d29;
        height: 90px;
        color: white;
        margin-bottom: 5px;
    }

    .alert-trend hr {
        border-top-color: white;
    }

    .alert-trend .alert-link {
        color: white;
    }

    .alert-trend a {
        color: white;
    }

    .jqstooltip {
        width: auto !important;
        height: auto !important;
    }

</style>

<div class="alert alert-trend" role="alert">
    <table cellpadding="0" cellspacing="0" width="100%" border="0">
        <tr>
            <td width="1%">
                <h1 style="margin:0;"><span class="glyphicon glyphicon-bell" aria-hidden="true"></span></h1>
            </td>
            <td style="white-space: nowrap; padding-left:5px; padding-right:5px;">
                <h3 style="margin:0;">${trendDefinition.title}</h3><h4 style="margin:0;">${trendDefinition.description}</h4>
            </td>
            <td width="50%" align="right">

                <jsp:text><![CDATA[<span "]]></jsp:text>
                class="sparkline-${trendDefinition.name}"
                <c:forEach var="trendAttribute" items="${trendDefinition.trendAttributes}">
                    <c:if test="${fn:startsWith(trendAttribute.key,'spark')}">
                        ${trendAttribute.key}="${trendAttribute.value}"
                    </c:if>
                </c:forEach>
                >
                ${trendValuesString}
                <jsp:text><![CDATA[</span>]]></jsp:text>
            </td>
        </tr>
    </table>
    <hr style="margin-top:5px;margin-bottom:5px;"/>
    <%--
        <c:when test="${trendDefinition.link!=''}">
            <a href="${trendDefinition.link}">${trendDefinition.linkTitle}</a>
        </c:when>
    --%>
</div>

<script type="text/javascript">
    require(['jquery', '../js/jquery.sparkline.min'], function( $ ) {
        $('.sparkline-${trendDefinition.name}').sparkline('html', { enableTagOptions: true });
    });
</script>



