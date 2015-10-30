<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<head>
	<title>Product Locator</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=8, IE=9" />
	<sec:csrfMetaTags />
	<link href="<c:url value="/resources/styles/login.css"/>" rel="stylesheet" type="text/css" />
</head>	
<body onload="document.f.j_username.focus();">
	<div class="header">
		<h1>The Coca-Cola Company</h1>
	</div>
	
	<div class="container">
		<div class="content clearfix" style="width: 545px;">
	        <div class="col-1">
	          <div class="box sign-in">
	            <h2>Sign In</h2>
	            <form name='f' action="<c:url value='security_check' />" method="post">
	            	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		            <table cellspacing="0">
		                <tbody><tr>
		                    <td>&nbsp;</td>
		                    <td>
								<c:if test="${not empty error}">
									<div class="errorblock">
										The User ID / Password combination is incorrect.<br />
									</div>

									<c:if test="${sessionScope['SPRING_SECURITY_LAST_EXCEPTION'].getClass().simpleName eq 'LockedException'}">
										<div class="errorblock">
											Account locked<br />
										</div>
									</c:if>
									
								</c:if>
								
								<c:if test="${not empty accessdenied}">
									<div class="errorblock">
										Access denied<br />
									</div>
								</c:if>
		                    </td>
		                </tr>
		                <tr>
		                    <td><label for="user-id">User ID:</label></td>
		                    <td>
		                    	<input type='text' name='j_username' value='' />
		                    </td>
		                </tr>
		                <tr>
		                    <td><label for="password">Password:</label></td>
		                    <td>
		                    	<input type='password' name='j_password' />
		                    </td>
		                </tr>
		                <tr>
		                    <td>&nbsp;</td>
		                    <td>
		                        <input name="submit" type="submit" value="" class="signin" />
		                    </td>
		                </tr>
		                <tr>
		                	<td colspan="2">
		                		This system is restricted to authorized users for legitimate business purposes and is subject to audit. Any actual or attempted unauthorized access, use, modification or copying of computer systems is a violation of U.S. Federal and State laws.
		                		By continuing, you acknowledge that the company may monitor the system, any user's use of the system and may record any or all activities on the system as permitted by local law.  
		                		If you are traveling outside your home office, your network traffic will use the local systems and will be subject to local monitoring procedures.
		                	</td>
		                </tr>
		              </tbody>
		        	</table>
	            </form>
	          </div>
	        </div>
		</div>
	</div>
	<div class="footer">
       	© 2012 <a href="http://www.coca-cola.com/">The Coca-Cola Company</a>.  All rights reserved.  "Coca-Cola" is a registered trademark of <a href="http://www.coca-cola.com/">The Coca-Cola Company</a>.
   	</div>

</body>
</html>