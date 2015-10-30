<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ page import="org.springframework.web.util.JavaScriptUtils" %>

<head>
	<jsp:include page="include/html_head.jsp" />
	<sec:authorize ifAllGranted="ROLE_ADMIN">
		<script src="<c:url value="/resources/scripts/lct_user.js"/>" type="text/javascript"></script>
	</sec:authorize>
</head>

<body>
	<jsp:include page="include/page_header.jsp" />
	<sec:authorize ifAllGranted="ROLE_ADMIN">
		<div class="wrapper">
			<div class="content">
				<form:form commandName="userForm" name="userForm">
					<input id="operation" name="operation" type="hidden" value="list" />
					<input id="userName" name="userName" type="hidden" value="" />
					<div id="errors" class="errorMessage">
						<form:errors path="*" cssClass="errorMessage"/>
					</div>
					<div id="result" class="block result">
						<table><tr><td align="left"><h2>Users</h2></td></tr></table>
						<div class="searchResults">
							<table style="width:auto;">
								<thead>
									<tr>
										<th>Name</th>
										<th>Role</th>
										<th>&nbsp;</th>
										<th>&nbsp;</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="user" items="${users}" varStatus="userStatus">
										<spring:message var="userName" text="${user.userName}" javaScriptEscape="true" htmlEscape="false" />
										<spring:message var="userName" text="${userName}" javaScriptEscape="false" htmlEscape="true" />
										<tr>
											<td>
												<c:out value="${user.userName}" escapeXml="true" />
											</td>
											<td>
												<c:out value="${user.role}" escapeXml="true" />
											</td>
											<td>
												<input id="editSelectUser" type="button" class="btnGray" value="Edit" onclick="javascript:editUser(this, '${userName}');" />
											</td>
											<td>
												<input id="removeSelectUser" type="button" class="btnGray" value="Remove" onclick="javascript:removeUser(this, '${userName}');" />
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table><!-- #mappingTable -->
							<input id="addNewUser" type="button" class="btnGray" value="Create" onclick="javascript:createUser();" />
						</div><!--.searchResults-->	
					
						<div class="resultFooter clearfix">
						</div><!--resultFooter-->
					</div><!--.block result-->
				</form:form>
			</div>
			
			<jsp:include page="include/page_footer.jsp" />
			
		</div>
		
	</sec:authorize>
	
</body>
</html>