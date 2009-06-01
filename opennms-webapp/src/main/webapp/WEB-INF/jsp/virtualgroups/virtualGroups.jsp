<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/tree" prefix="tree" %>
<%@ taglib tagdir="/WEB-INF/tags/springx" prefix="springx" %>

<jsp:include page="/includes/header.jsp" flush="false">
	<jsp:param name="title" value="Application" /> 
	<jsp:param name="headTitle" value="Application" />
	<jsp:param name="breadcrumb"
               value="<a href='admin/index.jsp'>Admin</a>" />
	<jsp:param name="breadcrumb"
	           value="<a href='admin/virtualGroups.htm'>Virtual Groups</a>" />
</jsp:include>

<h3>Virtual Groups</h3>

<script language="Javascript" type="text/javascript" >
	function takeAction(group, action) {
	    document.takeAction.groupName.value = group;
	    document.takeAction.action.value = action;
		document.takeAction.submit();
	}
	
   function edit(group) {
	    document.edit.groupName.value = group;
		document.edit.submit();
	}
	
	
	
</script>



<c:url var="editUrl" value="admin/editVirtualGroup.htm" />

<form action="${editUrl}" name="edit" method="post">
  <input name="groupName" type="hidden"/>
</form>

<table>

<tr>
  <th>Group Name</th>
  <th>Nodes in Group</th>
  <th>Group inside group</th>
  <th>Delete</th>
</tr>

<c:forEach var="group" items="${groups}">
<tr>
  <td><a href="javascript:edit('${group.id}')">${group.description}</a></td>
  <td>${group.numberOfNodes}</td>
  <td>${group.numberOfGroups}</td>
  <td>
    <a href="javascript:takeAction('${group.id}', 'deleteGroup')" onclick="return confirm('Are you sure you want to delete the group ${group.description}. This CANNOT be undone.')">Delete Group</a>
  </td>
</tr>
</c:forEach>
<tr>
   <td></td>
   <td colspan="4"><form action="${relativeRequestPath}" name="takeAction" method="post"><input type="text" name="groupName" size="20"/><input type="hidden" name="action" value="addGroup" /><input type="submit" value="Add New Group"/></form></td>
</tr>
</table>

<jsp:include page="/includes/footer.jsp" flush="false"/>