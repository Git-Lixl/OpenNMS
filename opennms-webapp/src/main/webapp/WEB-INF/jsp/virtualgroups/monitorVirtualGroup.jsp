<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/springx" prefix="springx" %>

<jsp:include page="/includes/header.jsp" flush="false">
	<jsp:param name="title" value="Application" /> 
	<jsp:param name="headTitle" value="Application" />
	<jsp:param name="breadcrumb" value="Monitor" />
</jsp:include>

<script type='text/javascript' src='/opennms/dwr/interface/nodeUtil.js'></script>
<script type='text/javascript' src='/opennms/dwr/engine.js'></script>
<script type='text/javascript' src='/opennms/dwr/util.js'></script>

<c:url var="editUrl" value="/element/monitorGroup.htm" />
<c:url var="editNodeUrl" value="/element/node.jsp" />
<c:url var="editServiceUrl" value="/element/service.jsp" />


<script language="Javascript" type="text/javascript"  >

   function addLoadEvent(func) {
  var oldonload = window.onload;
  if (typeof window.onload != 'function') {
    window.onload = func;
  } else {
    window.onload = function() {
      if (oldonload) {
        oldonload();
      }
      func();
    }
  }
}

function refreshAlarms( alarms )
{
    document.getElementById( 'alarmsBox').style.display = 'block';
    dwr.util.removeAllRows("alarmsbody", { filter:function(tr) {
      return (tr.id != "pattern");
    }});
    // Create a new set cloned from the pattern row
    var alarm, id;
    id = 0;
    for (var i = 0; i < alarms.length; i++) {
      alarm = alarms[i];
      id++;
      dwr.util.cloneNode("pattern", { idSuffix:id });
      dwr.util.setValue("node" + id, alarm.nodeLabel);
      dwr.util.setValue("description" + id, alarm.description, { escapeHtml:false });
      dwr.util.setValue("count" + id, alarm.count);
      dwr.util.setValue("fTime" + id, alarm.firstTime);
      dwr.util.setValue("lTime" + id, alarm.lastTime);
      $("pattern" + id).style.display = "table-row";
      $('descriptionContainer' + id).className = alarm.severityLevel;
    }
}

function fillTable( id, typ ) {
  if ( id == null || id == "" )
    {
      return;
    }
    if ( typ == 'group')
    {
      nodeUtil.getAlarmsForGroup( id, refreshAlarms );
    }
    else if ( typ == 'node' )
    {
      nodeUtil.getAlarmsForNode( id, refreshAlarms );
    }
    else if ( typ == 'service' )
    {
      nodeUtil.getAlarmsForService( id, refreshAlarms );
    }
}


var oldElm = null;

function changeRowColor( elm,color )
{
  if ( elm == null )
  {
    return;
  }
   var v = elm.getElementsByTagName( "td");
    for ( i = 0; i < v.length;i++)
    {
      v[i].style.backgroundColor = color;
    }
}



function selectRow( elm, id, typ )
{
    changeRowColor( oldElm, '');
    changeRowColor( elm, '#5d7cba');
    oldElm = elm;
    fillTable( id, typ );

}

function showAllAlarms()
{
   if ( '${groupId}' == '' )
   {
     document.getElementById("showAllAlarmsLink").style.display='none';
   }
  selectRow( null, '${groupId}', 'group');
}

function initializeAlertTable()
{
   if ( '${groupId}' != ''  )
   {
     fillTable( '${groupId}','group' );
   }
}

addLoadEvent( showAllAlarms );
addLoadEvent( initializeAlertTable )

</script>

<div id="content">

<div id="index-contentleft">

  <h3>Group List </h3>
  <div class="boxWrapper">
    <p>
        <table>
             <tr><th>Group:</th></tr>
             <tr>
               <td>
                 <a href="${editUrl}">All</a>
               </td>
            </tr>
        <c:forEach var="group" items="${allgroups}">
             <tr>
               <td>
                 <a href="${editUrl}?groupName=${group.id}">${group.description}</a>
               </td>
            </tr>
        </c:forEach>
        </table>
    </p>
  </div>

  </div>

    <div id="index-contentmiddle">
      <h3>Content of group : ${description}</h3>
      <div class="boxWrapper">
       <p>
      <table>
        <tr>
          <th>Type</th>
          <th>Description</th>
          <th>Status</th>
        </tr>
        <c:forEach var="entry" items="${entries}">
             <tr class="CellStatus"
                 <c:choose>
                   <c:when test="${entry.item.type == 'GROUP'}">
                      style="cursor:pointer" onclick="javascript:selectRow(this,${entry.item.contentGroupId},'group');"
                    </c:when>
                    <c:when test="${entry.item.type == 'NODE'}">
                      style="cursor:pointer" onclick="javascript:selectRow(this,${entry.item.contentNodeId},'node');"
                    </c:when>
                    <c:when test="${entry.item.type == 'SERVICE'}">
                      style="cursor:pointer" onclick="javascript:selectRow(this,${entry.item.contentServiceId},'service');"
                    </c:when>
                 </c:choose>

               >
               <td>${entry.item.type}
               </td>
               <td>
                 <c:choose>
                   <c:when test="${entry.item.type == 'GROUP'}">
                     <a href="${editUrl}?groupName=${entry.id}">${entry.description}</a>
                    </c:when>
                    <c:when test="${entry.item.type == 'NODE'}">
                      <a href="${editNodeUrl}?node=${entry.id}">${entry.description}</a>
                    </c:when>
                    <c:when test="${entry.item.type == 'SERVICE'}">
                       <a href="${editServiceUrl}?node=${entry.data.nodeId}&intf=${entry.data.ipAddress}&service=${entry.data.serviceId}">${entry.description}</a>
                    </c:when>

                    <c:otherwise>
                      ${entry.description}
                    </c:otherwise>
                 </c:choose>
               </td>
               <td class="${entry.colorClass}" align="right">${entry.numberOfDown} of ${entry.totalNumber}</td>
             </tr>
        </c:forEach>
      </table>
      </p>
      </div>

      <div class="boxWrapper" id="alarmsBox" style="display:none">
       <p>      
       <br />
       Alarms List <span id="showAllAlarmsLink">(<a onclick="showAllAlarms();return false;">Show all alarms</a>)</span>
      <table id="alarmsTable">
        <tr>
          <th>Node</th>
          <th>Description</th>
          <th>Count</th>
          <th>First Time</th>
          <th>Last Time</th>
        </tr>
          <tbody id="alarmsbody">
      <tr id="pattern" style="display:none;" class="CellStatus">
      <td><span id="node">Node</span><br/></td>
      <td id="descriptionContainer"><span id="description">Description</span></td>
      <td><span id="count">Count</span></td>
      <td><span id="fTime">fTime</span></td>
      <td><span id="lTime">lTime</span></td>
      </tr>
  </tbody>

      </table>
       </p>
      </div>

    </div>



     <div id="index-contentright">
       <h3>Info</h3>
       <div class="boxWrapper">
         Group monitor page.
       </div>
     </div>
   </div>



<jsp:include page="/includes/footer.jsp" flush="false"/>