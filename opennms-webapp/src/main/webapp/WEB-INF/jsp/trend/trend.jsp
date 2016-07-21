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

<%@ page language="java" contentType="text/html" session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<style type="text/css">

    .row.gutter-10 {
        margin-right: -5px;
        margin-left: -5px;
    }

    .gutter-10 > [class^="col-"], .gutter-10 > [class^=" col-"] {
        padding-right: 5px;
        padding-left: 5px;
    }

    .alert-trend {
        background-color: #4c9d29;
        border-color: #4c9d29;
        color: white;
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

</style>

<c:if test="${param['type'] == 'line'}">
    <div class="alert alert-trend" role="alert">
        <div class="row">
            <div class="col-xs-8">
                <h2 style="margin:0;"><%= request.getParameter("name") %></h2>
            </div>
            <div class="col-xs-4 text-right">
                <h2 style="margin:0;"><span class="glyphicon glyphicon-bell" aria-hidden="true"></span></h2>
            </div>
        </div>
        <span class="sparkline-<%= request.getParameter("name") %>"
              sparkWidth="100%"
              sparkHeight="70"
              sparkLineColor="white"
              sparkLineWidth="1.5"
              sparkFillColor="false"
              sparkSpotColor="white"
              sparkMinSpotColor="white"
              sparkMaxSpotColor="white"
              sparkSpotRadius="3"
              sparkHighlightSpotColor="white"
              sparkHighlightLineColor="white">
             <!-- 1,1,0,4,4,7,5,9,10,4,3,2,5,3,1,2 -->
            </span>
    </div>
</c:if>

<c:if test="${param['type'] == 'bar'}">
    <div class="alert alert-trend" role="alert">
        <div class="row">
            <div class="col-xs-4">
                <h3 style="margin:0;"><%= request.getParameter("name") %></h3><h4 style="margin:0;">13,477,573</h4>
            </div>
            <div class="col-xs-8 text-right">
                <div class="sparkline-<%= request.getParameter("name") %>"
                     sparkType="bar"
                     sparkBarColor="white"
                     sparkHeight="35"
                     sparkBarWidth="4"
                     sparkBarSpacing="3">
                    <!-- 28,15,40,12,13,20,17,29,10,6,11,12 -->
                </div>
            </div>
        </div>
        <hr style="margin-top:5px;margin-bottom:5px;"/>
        <a href="#">MORE</a>
    </div>
</c:if>

<c:if test="${param['type'] == 'pie'}">
    <div class="alert alert-trend" role="alert">
        <div class="row">
            <div class="col-xs-4">
                <h3 style="margin:0;"><%= request.getParameter("name") %></h3><h4 style="margin:0;">13,477,573</h4>
            </div>
            <div class="col-xs-8 text-right">

                <div class="sparkline-<%= request.getParameter("name") %>"
                     sparkType="pie"
                     sparkBarColor="white"
                     sparkHeight="35"
                     sparkBarWidth="4"
                     sparkBarSpacing="3"
                     sparkSliceColors="[#88DD88,#99DD99,#AADDAA,#BBDDBB,#CCDDCC,#DDDDDD,#EEDDEE]">
                    <!-- 20,20,15,15,10,10,10 -->
                </div>

            </div>
        </div>
        <hr style="margin-top:5px;margin-bottom:5px;"/>
        <a href="#">MORE</a>
    </div>
</c:if>

<script type="text/javascript">
    require(['jquery', '../js/jquery.sparkline.min'], function( $ ) {
        $('.sparkline-<%= request.getParameter("name") %>').sparkline('html', { enableTagOptions: true });
    });
</script>



