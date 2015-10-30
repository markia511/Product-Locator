<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<head>
	<jsp:include page="include/html_head.jsp" />
	<script src="<c:url value="/resources/scripts/lctReport.js"/>" type="text/javascript"></script>
	<script type="text/javascript">

		function ValidateOnKeyPress(pointer, e) 
		{
		    var code;
		    if (e)
		        code = e.keyCode;
		    else
		        code = window.event.keyCode;
		    
		    if (code == 32 && pointer.value.length == 0) 
		    {        
		            window.event.keyCode = 0;
		    }
		 }

		
		function checkResult(ev) {
			ev = (ev)?ev:window.event;
			html = $("#searchResult").html();	
			var recordsCount = $("#recordsCount").val();
			if(recordsCount== 0){
				ev.returnValue = false;			
			}
			return false;
	   }
		
		$(function() {
			$( "#dateFrom" ).datepicker({
				  onSelect: function(dateText) {
					  	changeField();
				  }
		});
		});
		$(function() {
			$( "#dateTo" ).datepicker({
				  onSelect: function(dateText) {
					  	changeField();
				  }
		});
		});		
	</script> 
</head>	
<body>
	<jsp:include page="include/page_header.jsp" />
	<div class="wrapper">
		<div class="content">
			<form:form commandName="reportForm" method="get" action="runReport" name="reportForm">	
			<div class="contentTop clearfix">
				
				<div id="reportForm" class="block reportForm">									
					<h1>Reporting Module</h1>
					<div class="keyword">
						
						<table class="main_border">
							<tr>
								<td class="sizeA">
									<label for="reportType">Report Type:<span class="required">*</span></label>
									<div><select name="reportType" id="reportType" style="width: auto;" >
									<option value="-1">-Select-</option>
								    <option value="1">Report 1: Number of search queries per Brand</option>
								    <option value="2">Report 2: Product Locator API Usage</option>																			
									</select></div>
								</td>														
							</tr>
						</table>
						<table class="main_border">
							<tr>
								<td class="sizeA">
									<label for="dateField">Date Range:<span class="required">*</span></label>
									<p>From: <input name="dateFrom" id="dateFrom" type="text" /> To: <input name="dateTo" id="dateTo" type="text" /></p>
									<div style="display: none;" class="demo-description">
									<p>The datepicker is tied to a standard form input field.  Focus on the input (click, or use the tab key) to open an interactive calendar in a small overlay.  Choose a date, click elsewhere on the page (blur the input), or hit the Esc key to close. If a date is chosen, feedback is shown as the input's value.</p>
									</div><!-- End demo-description -->
								</td>																							
							</tr>																	
						</table>
						<div class="clearfix">																
							<div class="errorMessage dateError" style="display: none"><spring:message code="ER_007"/></div>						
						</div>	
						<div class="clearfix">																
							<div class="errorMessage dateValidError" style="display: none"><spring:message code="ER_009"/></div>						
						</div>	
						<div class="buttons clearfix">									
						<div class="controls">												
							<input class="btnGray" type="button" value="Clear" onclick="clearAll();" />		
							<input class="btnRed" disabled="disabled" name="runReport" type="submit" value="Run Report" id="runReport"/>																		
						</div>
					</div>		
					</div><!--Keyword Search-->															
														
				</div>
			</div><!--.contentTop-->	
		
			</form:form>
		</div>
		
		<jsp:include page="include/page_footer.jsp" />
		
	</div><!--.wrapper-->

</body>
</html>