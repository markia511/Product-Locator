<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ page import="com.ko.lct.web.bean.UserRole" %>

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
					<spring:message var="operation" text="${operation}" javaScriptEscape="false" htmlEscape="true" />
					<input id="operation" name="operation" type="hidden" value="${operation}" />
					<spring:message var="create" text="${create}" javaScriptEscape="false" htmlEscape="true" />
					<input id="create" name="create" type="hidden" value="${create}" />
					<div id="result" class="block result" style="width:auto">
						
						<c:choose>
							<c:when test="${operation eq 'create'}">
								<h2>Create User</h2>
							</c:when>
							<c:otherwise>
								<h2>Edit User</h2>
							</c:otherwise>
						</c:choose>
						
						<div class="searchResults">
							<table style="width:auto">
								<tbody>
									<tr>
										<td><label for="newUserName">Name:</label></td>
										<td>
											<c:choose>
												<c:when test="${edit eq '1'}">
													<c:out value="${userForm.userName}" escapeXml="true" />
													<form:hidden path="userName" />
													<input type="hidden" name="edit" value="1" />
												</c:when>
												<c:otherwise>
													<form:input id="newUserName" type="text" maxlength="255" path="userName" />
												</c:otherwise>
											</c:choose>
											
										</td>
										<td>
											<form:errors path="userName" cssClass="errorMessage" />
										</td>
									</tr>
									<sec:authentication property="principal.username" var="loginUserName" />
									<c:if test="${(loginUserName eq userForm.userName) and (edit eq	'1')}">
									<tr>
										<td><label for="newUserPassword">Old Password:</label></td>
										<td>
											<form:input id="oldUserPassword" type="password" maxlength="255" path="oldPassword" />
										</td>
										<td>
											<form:errors path="oldPassword" cssClass="errorMessage" />
										</td>
									</tr>
									</c:if>
									<tr>
										<td><label for="newUserPassword">Password:</label></td>
										<td>
											<form:input id="newUserPassword" type="password" maxlength="255" path="password" />
										</td>
										<td>
											<form:errors path="password" cssClass="errorMessage" />
										</td>
									</tr>
									<tr>
										<td><label for="newUserPasswordConfirm">Confirm Password:</label></td>
										<td>
											<form:input id="newUserPasswordConfirm" type="password" maxlength="255" path="confirmPassword" />
										</td>
										<td>
											<form:errors path="confirmPassword" cssClass="errorMessage" />
										</td>
									</tr>
									<tr>
										<td><label for="newUserRole">Role:</label></td>
										<td>
											<c:set var="roles" value="<%=UserRole.values()%>" />
											<c:forEach var="role" items="${roles}">
												<form:radiobutton id="newUserRole" path="role" value="${role.name()}" label="${role.toString()}" />
											</c:forEach>
										</td>
										<td>
											<form:errors path="role" cssClass="errorMessage" />
										</td>
									</tr>
									<tr>
										<td><label for="newUserEnabled">Enabled:</label></td>
										<td>
											<form:checkbox id="newUserEnabled" path="enabled" />
										</td>
										<td>
											&nbsp;
										</td>
									</tr>
									<tr>
										<td colspan="3">
											<form:errors path="*" cssClass="errorMessage" element="div" />
											<div id="newUserError" class="errorMessage"></div>
										</td>
									</tr>
									<tr>
										<td>
											&nbsp;
										</td>
										<td colspan="2">
											<input type="submit" class="btnRed" value="Save" />
											<input type="button" class="btnGray" value="Cancel" onclick="javascript:window.location.href='<c:url value="/user" />'"/>
										</td>
									</tr>
								</tbody>
							</table>

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