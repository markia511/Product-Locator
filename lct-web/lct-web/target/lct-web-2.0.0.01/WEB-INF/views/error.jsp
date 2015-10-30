<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
	<jsp:include page="include/html_head.jsp" />
</head>
	
<body>
	<jsp:include page="include/page_header.jsp" />
	<div class="wrapper">
		<div class="content" style="height:300px;">
		<b>
		<%
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode != null && statusCode.intValue() == 404) {
		%>
			<spring:message code="resource_not_found_exception" />
		<%}
		else {%>
			<spring:message code="general_exception" />
		<%}%>
		</b>
		</div>
		
		<jsp:include page="include/page_footer.jsp" />
		
	</div><!--.wrapper-->
		
</body>
</html>	