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

<%--
  This page is included by other JSPs to create a box containing a treemap/heatmap of
  outages grouped by nodes, foreignSources or categories.

  It expects that a <base> tag has been set in the including page
  that directs all URLs to be relative to the servlet context.
--%>

<%@page language="java"
        contentType="text/html"
        session="true"
%>
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
        <div class="row">
            <div class="col-md-4">
                <div class="alert alert-success" role="alert">
                    <div class="row">
                        <div class="col-xs-8">
                            <h2 style="margin:0;">Alarms</h2>
                        </div>
                        <div class="col-xs-4 text-right">
                            <h2 style="margin:0;"><span class="glyphicon glyphicon-bell" aria-hidden="true"></span></h2>
                        </div>
                    </div>
                    <span class="inlinesparkline"
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
                          sparkHighlightLineColor="white">1,1,0,4,4,7,5,9,10,4,3,2,5,3,1,2</span>
                </div>
            </div>
            <div class="col-md-4">
                <div class="alert alert-success" role="alert">
                    <div class="row">
                        <div class="col-xs-8">
                            <h2 style="margin:0;">Alarms</h2>
                        </div>
                        <div class="col-xs-4 text-right">
                            <h2 style="margin:0;"><span class="glyphicon glyphicon-bell" aria-hidden="true"></span></h2>
                        </div>
                    </div>
                    <span class="inlinesparkline"
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
                          sparkHighlightLineColor="white">1,1,0,4,4,7,5,9,10,4,3,2,5,3,1,2</span>
                </div>
            </div>
            <div class="col-md-4">
                <div class="alert alert-success" role="alert">
                    <div class="row">
                        <div class="col-xs-8">
                            <h2 style="margin:0;">Alarms</h2>
                        </div>
                        <div class="col-xs-4 text-right">
                            <h2 style="margin:0;"><span class="glyphicon glyphicon-bell" aria-hidden="true"></span></h2>
                        </div>
                    </div>
                    <span class="inlinesparkline"
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
                          sparkHighlightLineColor="white">1,1,0,4,4,7,5,9,10,4,3,2,5,3,1,2</span>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-md-4">
                <div class="alert alert-success" role="alert">
                    <div class="row">
                        <div class="col-xs-4">
                            <h3 style="margin:0;">Alarms</h3><h4 style="margin:0;">13,477,573</h4>
                        </div>
                        <div class="col-xs-8 text-right">

                            <div class="bar-sparkline"
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
            </div>
            <div class="col-md-4">
                <div class="alert alert-success" role="alert">
                    <div class="row">
                        <div class="col-xs-4">
                            <h3 style="margin:0;">Severity</h3><h4 style="margin:0;">13,477,573</h4>
                        </div>
                        <div class="col-xs-8 text-right">

                            <div class="pie-sparkline"
                                 sparkType="pie"
                                 sparkBarColor="white"
                                 sparkHeight="35"
                                 sparkBarWidth="4"
                                 sparkBarSpacing="3"
                                 sparkSliceColors="[#f5cdcd,#ffd7cd,blanchedalmond,#fff5cd,#ebebcd,#d7e1cd,#eeeeee]">
                                <!-- 20,20,15,15,10,10,10 -->
                            </div>

                        </div>
                    </div>
                    <hr style="margin-top:5px;margin-bottom:5px;"/>
                    <a href="#">MORE</a>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    require(['jquery', '../js/jquery.sparkline.min'], function( $ ) {
        $('.inlinesparkline').sparkline('html', { enableTagOptions: true });
        $('.bar-sparkline').sparkline('html', { enableTagOptions: true });
        $('.pie-sparkline').sparkline('html', { enableTagOptions: true });
    });
</script>
