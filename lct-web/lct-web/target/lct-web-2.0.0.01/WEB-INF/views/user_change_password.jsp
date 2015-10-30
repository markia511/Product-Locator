<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<head>
	<jsp:include page="include/html_head.jsp" />
</head>

<body>
	<jsp:include page="include/page_header.jsp" />
		<div class="wrapper">
			<div class="content">
				<form:form commandName="userForm" name="userForm">
					<div id="result" class="block result" style="width:auto">
						<h2>Change Password</h2>
						<div class="searchResults">
							<table style="width:auto">
								<tbody>
									<tr>
										<td><label for="newUserName">Name:</label></td>
										<td>
											<c:out value="${userForm.userName}" escapeXml="true" />
											<form:hidden path="userName" />
											<input type="hidden" name="edit" value="1" />
										</td>
										<td>
											<form:errors path="userName" cssClass="errorMessage" />
										</td>
									</tr>

									<tr>
										<td><label for="oldUserPassword">Old Password:</label></td>
										<td>
											<form:input id="oldUserPassword" type="password" maxlength="255" path="oldPassword" />
										</td>
										<td>
											<form:errors path="oldPassword" cssClass="errorMessage" />
										</td>
									</tr>
									
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
											<input type="button" class="btnGray" value="Cancel" onclick="javascript:window.location.href='<c:url value="/" />'"/>
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
		
</body>
</html>