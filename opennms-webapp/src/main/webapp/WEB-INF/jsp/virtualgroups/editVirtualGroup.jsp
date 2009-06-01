<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/springx" prefix="springx" %>

<jsp:include page="/includes/header.jsp" flush="false">
	<jsp:param name="title" value="Application" /> 
	<jsp:param name="headTitle" value="Application" />
	<jsp:param name="breadcrumb"
               value="<a href='admin/index.jsp'>Admin</a>" />
	<jsp:param name="breadcrumb"
	           value="<a href='admin/virtualGroups.htm'>Virtual Groups</a>" />
	<jsp:param name="breadcrumb" value="Edit" />
</jsp:include>

<script type='text/javascript' src='/opennms/dwr/interface/nodeUtil.js'></script>
<script type='text/javascript' src='/opennms/dwr/engine.js'></script>
<script type='text/javascript' src='/opennms/dwr/util.js'></script>


<script language="Javascript" type="text/javascript"  >
	function deleteAction(group) {
	    document.takeAction.itemToDelete.value = group;
	    document.takeAction.action.value = 'delete';
		document.takeAction.submit();
	}

   function edit(group) {
	    document.edit.groupName.value = group;
		document.edit.submit();
	}

	function deleteNodes(group) {
	   alert("Are you sure you want to delete all the nodes from the group?  This CANNOT be undone.");
	}

    function interfaceCallBack( el ) {
      dwr.util.removeAllOptions("interfaceToAdd");
      if ( el != null )
      {
        dwr.util.addOptions("interfaceToAdd", el );
        populateService();
      }
    }

    function populateInterface() {
       var el = document.getElementById( "srvNodeToAdd" ).value;
       if ( el != null && el != '' )
       {
         nodeUtil.getInterfaceList( el, interfaceCallBack );
       }
    }

    function serviceCallBack( el ) {
      if ( el != null )
      {
       dwr.util.addOptions("serviceToAdd", el );
      }
    }

     function populateService() {
       dwr.util.removeAllOptions("serviceToAdd");
       var node = document.getElementById( "srvNodeToAdd" ).value;
       var inter = document.getElementById( "interfaceToAdd" ).value;
       nodeUtil.getServiceInterfaceList( node, inter, serviceCallBack );
    }


   
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

addLoadEvent( populateInterface );
   
</script>
<c:url var="editUrl" value="admin/editVirtualGroup.htm" />

<div class="TwoColLAdmin">

  <h3>Adding Item to Virtual Group : ${groupEditForm.description} </h3>
  <div class="boxWrapper">
    <p>
       <h3>Add Group</h3>
       <div class="boxWrapper">
      <form method="post" name="addGroup">
        <input type="hidden" id="groupName" name="groupName" value="${groupEditForm.groupName}"/>
        <input id="action" name="action" type="hidden" value="addGroup"/>
        <select name="groupToAdd">
          <c:forEach var="group" items="${allgroups}">
              <option value="${group.id}">${group.description}</option>
          </c:forEach>
        </select>
      <input type="submit" value="Add" />
      </form>
      </div>
    </p>
    <p>
       <h3>Add Node</h3>
       <div class="boxWrapper">
      <form method="post" name="addNode">
        <input type="hidden" id="groupName" name="groupName" value="${groupEditForm.groupName}"/>
        <input id="action" name="action" type="hidden" value="addNode"/>
       
        <select id="nodeToAdd" name="nodeToAdd" >
          <c:forEach var="node" items="${filterednodes}">
               <option value="${node.id}">${node.label}</option>
          </c:forEach>
        </select>
        <input type="submit" value="Add" />
      </form>
      </div>
    </p>
     <p>
       <h3>Add Service</h3>
        <div class="boxWrapper">
      <form method="post" name="addService">
        <input type="hidden" id="groupName" name="groupName" value="${groupEditForm.groupName}"/>
        <input id="action" name="action" type="hidden" value="addService"/>
        Service:
        <select id="srvNodeToAdd" name="nodeToAdd" onchange="populateInterface();">
          <c:forEach var="node" items="${allnodes}">
               <option value="${node.id}">${node.label}</option>
          </c:forEach>
        </select>
        <select id="interfaceToAdd" name="interfaceToAdd" onchange="populateService();">
        </select>
        <select id="serviceToAdd" name="serviceToAdd">
        </select>
        <input type="submit" value="Add" />
      </form>
      </div>
    </p>
  </div>

</div>

        <div class="TwoColRAdmin">
      <h3>Add Items</h3>
        <p>
        Is it possible to add nodes and virtual nodes inside another virtual group.
        </p>
  </div>
  <hr />

<br />
<br />
<table>

<tr>
  <th>Type</th>
  <th>Id</th>
  <th>Description</th>
  <th>Delete</th>
</tr>

<c:forEach var="item" items="${groupEditForm.formData.items}">
<tr>
  <td>
    ${item.type}
  </td>
  <td>
    ${item.id}
  </td>
  <td>
    <c:choose>
      <c:when test="${item.type == 'GROUP'}">
       <a href="${editUrl}?groupName=${item.contentGroupId}">${item.description}</a>
      </c:when>
      <c:otherwise>
        ${item.description}
      </c:otherwise>
    </c:choose>
    
  </td>
  <td>
    <a href="javascript:deleteAction('${item.id}')" onclick="return confirm('Are you sure you want to delete the item : ${item.description}. This CANNOT be undone.')">Delete</a>
  </td>
</tr>
</c:forEach>
</table>




 <form style="display:none" name="takeAction" method="post">
    <input type="hidden" name="groupName" value="${groupEditForm.groupName}"/>
    <input type="hidden" name="itemToDelete" value=""/>
    <input type="hidden" name="action" value="addGroup" />
 </form>


<jsp:include page="/includes/footer.jsp" flush="false"/>