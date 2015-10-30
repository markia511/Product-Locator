<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

	<div class="top">
		<div class="header">
			<h1>
				<a href="<c:url value="/"/>" style="text-decoration: none; color: #FFF;">CCNA Customer Care Product Locator</a>
			</h1>
			<div class="logout">
				<sec:authorize access="isAuthenticated()">
					<table>
						<tbody>
							<tr>
								<td>
									<c:url var="changePasswordUrl" value="/change_password"/>
									<form:form id="changePasswordForm" name="changePasswordForm" method="post" action="${changePasswordUrl}">
										<sec:authentication property="principal.username" var="loginUserName" />
										<input id="userName" name="userName" type="hidden" value="<c:out value="${loginUserName}" escapeXml="true" />" />
										<a href="#" onclick="javascript:document.forms['changePasswordForm'].submit();">Change password</a>
									</form:form>
								</td>
							</tr>
							<tr>
								<td>
									<c:url var="logoutUrl" value="/logout"/>
									<form:form id="logoutForm" name="logoutForm" method="post" action="${logoutUrl}">
										<a href="#" onclick="javascript:document.forms['logoutForm'].submit();">Logout</a>
									</form:form>
								</td>
							</tr>
						</tbody>
					</table>
				</sec:authorize>
			</div>
		</div>
	</div><!--.top-->
