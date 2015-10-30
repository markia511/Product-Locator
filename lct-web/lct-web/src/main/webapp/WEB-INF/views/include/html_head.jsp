<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

	<title>CCNA Customer Care Product Locator</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	
	<meta http-equiv="cache-control" content="max-age=0" />
	<meta http-equiv="cache-control" content="no-cache" />
	<meta http-equiv="expires" content="0" />
	<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
	<meta http-equiv="pragma" content="no-cache" />
	
	<sec:csrfMetaTags />
	
	<link href="<c:url value="/resources/styles/normalize.css"/>" rel="stylesheet" type="text/css"></link>
	<link href="<c:url value="/resources/styles/reset.css"/>" rel="stylesheet" type="text/css"></link>
	<link href="<c:url value="/resources/styles/jquery-ui.custom.css"/>" rel="stylesheet" type="text/css"></link>
	<link href="<c:url value="/resources/styles/style.css"/>" rel="stylesheet" type="text/css"></link>
	<link href="<c:url value="/resources/styles/print.css"/>" rel="stylesheet" type="text/css" media="print"></link>
	
	<script src="<c:url value="/resources/scripts/jquery.js"/>" type="text/javascript"></script>
	<script src="<c:url value="/resources/scripts/jquery-ui.custom.js"/>" type="text/javascript"></script>
	<script src="<c:url value="/resources/scripts/lct_common.js"/>" type="text/javascript"></script>
	<script type="text/javascript">
		var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfAjaxHeader = {};
		csrfAjaxHeader[csrfHeader] = csrfToken;
		
		checkBrowser();
	</script>