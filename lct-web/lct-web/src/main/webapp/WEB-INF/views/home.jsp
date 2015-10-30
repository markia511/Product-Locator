<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<head>
	<jsp:include page="include/html_head.jsp" /> 
	<script src="<c:url value="/resources/scripts/lct.js"/>" type="text/javascript"></script>
	<script src="https://maps.googleapis.com/maps/api/js?sensor=false&libraries=places&client=${googleAPIClientId}&channel=${googleAPIChannel}" type="text/javascript"></script> 
	<script type="text/javascript">
		function ValidateOnKeyPress(pointer, e) {
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
				if(ev.preventDefault) ev.preventDefault();

			}
			return false;
	   }

	</script> 
</head>	
<body>
	<jsp:include page="include/page_header.jsp" />
	<div class="wrapper">
		<div class="content">
			<form:form commandName="locatorForm" name="locatorForm">
			
			<form:hidden path="maxOutletRecordsCount" id="maxOutletRecordsCount" />
				
			<div id="contentTop" class="contentTop clearfix adv">
				<div class="block map">
					<h2>Map View</h2>
					<div id="map_canvas"></div>
				</div>
				<div id="searchForm" class="block searchForm">
								
					<div class="advanced">
						<h2>Advanced Search</h2>
						<div class="productInfo">				
							<div align="center"><label for="productInformation">Product Information</label></div>			
							<fieldset id="productInformation">
									<table>
										<tr>
											<td class="sizeA">
												<label for="beverageCategory">Beverage Category:</label>
												<form:select path="beverageCategory"
														     onchange="refreshBrand();">
													<form:option  value="-1" label="All"/>
													<form:options items="${beverageCategories}" itemValue="code" itemLabel="name"/>
												</form:select>
											</td>
											<td colspan="2">
												<label for="productPackageType">Product Type:</label>
												<form:select path="productPackageType" >
													<form:option  value="-1" label="All"/>
													<form:options items="${productPackageTypes}" itemValue="id" itemLabel="name"/>
												</form:select>		
											</td>								
										</tr>
										<tr>
											<td class="sizeA">
												<label for="beverageBrand">Brand:</label>
												<form:select path="beverageBrand" onchange="refreshFlavor();">
													<form:option  value="-1" label="All"/>
													<form:options items="${beverageBrands}" itemValue="code" itemLabel="name"/>
												</form:select>												
											</td>
											<td class="sizeA">
												<label for="beverageFlavor">Flavor:</label>
												<form:select path="beverageFlavor"
															 onchange="refreshProduct();" >
													<form:option  value="-1" label="All"/>
													<form:options items="${beverageFlavors}" itemValue="code" itemLabel="name"/>
												</form:select>
											</td>
										</tr>
										<tr>
											<td class="sizeA" colspan="2">
												<label for="product">Product:</label>
												<form:select path="product"
														     onchange="refreshShortPrimaryContainer()">
													<form:option  value="-1" label="All"/>
													<form:options items="${products}" itemValue="code" itemLabel="name"/>
												</form:select>												
											</td>
										</tr>

										<!-- <tr>
											<td class="sizeA">
												<label for="primaryContainer">Primary Container:</label>
												<form:select path="primaryContainer" 
															 onchange="getChildsListValue('secondaryPackage', 'primaryContainer', 'beverageBrand', 'beverageCategory');" >
													<form:option  value="-1" label="All"/>
													<form:options items="${primaryContainers}" itemValue="code" itemLabel="name"/>
												</form:select>
											</td>
											<td>
												<label for="secondaryPackage">Secondary Package:</label>
												<form:select path="secondaryPackage" >
													<form:option  value="-1" label="All"/>
													<form:options items="${secondaryPackages}" itemValue="code" itemLabel="name"/>
												</form:select>
											</td>
										</tr> -->
										<tr>
											<td class="sizeA">
												<label for="shortPrimaryContainer">Container:</label>
												<form:select path="shortPrimaryContainer" onchange="refreshShortSecondaryPackage();" >
													<form:option  value="-1" label="All"/>
													<form:options items="${shortPrimaryContainers}" itemValue="code" itemLabel="name"/>
												</form:select>
											</td>
											<td>
												<label for="shortSecondaryPackage">Package Size:</label>
												<form:select path="shortSecondaryPackage">
													<form:option  value="-1" label="All"/>
													<form:options items="${shortSecondaryPackages}" itemValue="code" itemLabel="name"/>
												</form:select>
											</td>
										</tr>
										<tr>
											<td class="sizeA">
												<label for="businessType">CBS Business Type:</label>
												<form:select path="businessType" >
													<form:option  value="-1" label="All"/>
													<form:options items="${businessTypes}" itemValue="id" itemLabel="name"/>
												</form:select>
											</td>
											<td class="sizeA">
												<label for="physicalState">Physical State:</label>
												<form:select path="physicalState" >
													<form:option  value="-1" label="All"/>
													<form:options items="${physicalStates}" itemValue="code" itemLabel="name"/>
												</form:select>
											</td>								
										</tr>
										<tr>
											<td class="sizeA">
												<label for="tradeChannel">Trade Channel:</label>
												<form:select path="tradeChannel"
															 onchange="getChildsListValue('subTradeChannel', 'tradeChannel');">
													<form:option  value="-1" label="All"/>
													<form:options items="${tradeChannels}" itemValue="code" itemLabel="name"/>
												</form:select>
											</td>
											<td>
												<label for="subTradeChannel">Trade Sub-Channel:</label>
												<form:select path="subTradeChannel">
													<form:option  value="-1" label="All"/>
													<form:options items="${subTradeChannels}" itemValue="code" itemLabel="name"/>
												</form:select>
											</td>
										</tr> 
										<tr>
											<td class="sizeA">
												<label for="outletName">Outlet Name:</label>
												<form:input path="outletName" cssStyle="min-width: 100px;" maxlength="50" onkeypress="return ValidateOnKeyPress(this,event);"/>
											</td>
											<td class="checkboxInclude">
												<input type="checkbox" id="kosherProduct" name="kosherProduct" 
													   onclick="changeKosherIndicator();" />
												<label for="kosherProduct">Show Kosher product only</label>
											</td>
										</tr>
									</table>
							</fieldset>
						</div><!--Product Info-->
						<div class="customerLocation">
							<div align="center"><label for="cusotmerLocation">Customer Location</label></div>
							<fieldset id="cusotmerLocation">
								<table>
									<tr>
										<td colspan="2">
											<label for="address">Street Address:</label>
											<input class="text" type="text" name="address" id="address" maxlength="255"
												   onchange="changeAddress();"  />
										</td>
									</tr>
									<tr>
										<td class="sizeE">
											<label for="city">City:</label>
											<input class="text" type="text" name="city" id="city" maxlength="58" onchange="changeAddress();"/>
										</td>
										<td class="sizeF">
											<label for="state">State/Province:</label>
											<form:select path="state" onchange="changeState(); changeAddress();">
													<form:option  value="-1" label=""/>
													<form:options items="${states}" itemValue="code" itemLabel="name"/>
											</form:select>											
										</td>
									</tr>
									<tr>
										<td class="sizeE">
											<label for="zip">Zip (Postal) Code:</label>
											<input class="text" type="text" name="zip" id="zip" onchange="changeAddress();"
												   maxlength="10" />
										</td>
										<td class="sizeF">
											<label for="country">Country:</label>
											<form:select path="country" onchange="getChildsListValue('state', 'country'); changeAddress();">
													<form:option  value="-1" label="--"/>
													<form:options items="${countries}" itemValue="code" itemLabel="name"/>
											</form:select>											
										</td>
									</tr>
									<tr>
										<td colspan="2">
											<label for="distance">Distance:</label>
											<input type="text" name="distance" id="distance" maxlength="3" style="width: 60px;" value="5"  />
											<span class="sizeE radioCell">
												<form:radiobuttons path="distanceUnitAdvanced" items="${distanceUnits}" itemLabel="name"   />	
											</span>																		
										</td>
									</tr>
								</table>
							</fieldset>	
						</div><!--customerLocation-->
					</div><!--Advanced Search-->
					<div class="keyword">
						<h2>Keyword Search</h2>		
						<table class="main_border">
							<tr>
								<td class="sizeA">
									<label for="prodInfo">Product Information:<span class="required">*</span></label>
									<div><input class="text " id="prodInfo" type="text" name="prodInfo" maxlength="255" onkeypress="return ValidateOnKeyPress(this,event);"/></div>
								</td>
								<td class="sizeB">
									<p>Enter Brand, Flavor and Package related keywords in "Product Information" field.<br />
									<b>NOTE:</b> "AND" condition will be used for multiple entries.</p>
								</td>							
							</tr>
						</table>
						<table class="main_border">
							<tr>
								<td class="sizeA">
									<label for="searchTextField">Address (Customer Location):<span class="required">*</span></label>
									<input class="text" id="searchTextField" type="text" maxlength="255" placeholder="" onkeypress="return ValidateOnKeyPress(this,event);"/>
								</td>															
								<td rowspan="2" class="sizeB">
									<p>Use the "Address" field to enter Street Name, 
									City, State/Province <b><em>and/or</em></b> Zipcode/Postcode</p><br />
									<p><b>NOTE:</b> You may type in City, State or ZIP only, 
									or enter the full address information including
									Street Name</p>
								</td>
							</tr>										
							<tr>
								<td>
									<label for="distanceK">Distance:</label>
									<select name="distanceK" id="distanceK" style="width: 60px;" >
										<option>5</option>
										<option>10</option>
										<option>15</option>
										<option>25</option>
										<option>50</option>
										<option>75</option>
										<option>100</option>
									</select>
									<span class="radioCell">
										<form:radiobuttons path="distanceUnitKeyword" items="${distanceUnits}" itemLabel="name" />	
									</span>
								</td>
							</tr>
						</table>
					</div><!--Keyword Search-->
									
					<div class="clearfix">
						<div class="errorMessage productInfoError" style="display: none"><spring:message code="ER_001"/></div>
						<div class="errorMessage addressKeywordError" style="display: none"><spring:message code="ER_002"/></div>
						<div class="errorMessage maxOutletRecoredsError" style="display: none"><spring:message code="ER_003"/></div>
						<div class="errorMessage addressAdvancedError" style="display: none"><spring:message code="ER_005" htmlEscape="false"/></div>
						<div class="errorMessage distanceAdvancedError" style="display: none"><spring:message code="ER_006"/></div>
						<div class="errorMessage distanceTooBigAdvancedError" style="display: none"><spring:message code="ER_008"/></div>
						<div class="errorMessage noResultsError" style="display: none"><spring:message code="ER_004"/></div>
					</div>		
					<div class="buttons clearfix">		
								
						<div class="left_controls">
							<input type="button" class="btnGray switchForm" onclick="javascript:switchTab(); return false;" id="switchForm" value="Switch to Keyword Search" />
						</div>
						<div class="controls">							
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<span class="checkboxInclude">
											<input type="checkbox" id="includeFoodService" name="includeFoodService" onclick="changeIncludeFoodService();" />
											<label for="includeFoodService">Include Food Service Locations</label>
										</span>	
									</td>
									
									<td>
										<input type="button" class="btnGray" onclick="javascript:clearAll(); return false;" value="Clear" />										
									</td>
									
									<td width="15px"></td>
									
									<td>
										<input class="btnRed" type="submit" onclick="clickSearchBtn(); return false;" value="Search"/>
									</td>
									
								</tr>
							</table>							
						</div>
					</div>
					<input type="hidden" name="longitude" id="longitude" value="0" />
					<input type="hidden" name="latitude" id="latitude" value="0"/>
					<input type="hidden" name="frmt_addr" />									
				</div>
			</div><!--.contentTop-->	
			<div id="result" class="block result">
				<table><tr><td align="left"><h2>Search Results</h2></td><td align="right"><div id="progressbar" class="progressbar"></div></td></tr></table>
				<div class="searchResults">								
					<table id="resultTable">
						<thead>
							<tr class="tableH">
								<td colspan="5"><h3>Outlet Information</h3></td>
								<td colspan="4"><h3>Product Information</h3></td>
							</tr>
							<tr>
								<th width="10"></th>
								<th width="14%"><div><a id="outletNameHeader" href="#">Outlet Name</a></div></th>
								<th width="20%">Outlet Address</th>
								<th width="8%"><div><a id="distanceHeader" href="#">Distance</a></div></th>
								<!-- <th width="12%"><div><a id="tradeChannelHeader" href="#">Trade Channel</a></div></th> -->
								<th width="13%" class="border"><div><a id="subTradeChannelHeader" href="#">Trade Sub-Channel</a></div></th>
								<th width="16%">Product</th>
								<th width="8%">Flavor</th>
								<th width="10%">Primary Container</th>
								<th width="6%">Secondary Package</th>						
							</tr>
						</thead>
						<tbody id="searchResult">
							<tr><td colspan="9" align="center">&nbsp;</td></tr>
						</tbody>
					</table>
				</div><!--.searchResults-->	
				<div class="resultFooter clearfix">
					<ul class="action">
						<li><a href="javascript:clipbordPage();"  onclick="checkResult(event);" ><img src="<c:url value="/resources/images/copy.png"/>" title="Copy to Clipboard" alt="" /></a></li>
						<li><a href="javascript:printSearchResult();" onclick="checkResult(event);" ><img src="<c:url value="/resources/images/print.png"/>" title="Print" alt="" /></a></li>
						<li><a href="./SearchResultPdf" onclick="checkResult(event);" target="_blank"><img src="<c:url value="/resources/images/pdf.png"/>" title="Export to PDF" alt="" /></a></li>
						<li><a href="./SearchResultXls" onclick="checkResult(event);"><img src="<c:url value="/resources/images/excel.png"/>" title="Export to Excel" alt="" /></a></li>
					</ul>
					<div class="pager">
						<table>
							<tr>
							<td>Results per page 
									<form:select path="itemPerPage" onchange="changeItemPerPage();">
										<option value="5">5</option>
										<option value="10">10</option>
										<option value="25">25</option>
									</form:select>
								</td>
								<td>
									<a href="javascript:prevPage();" onclick=""><img src="<c:url value="/resources/images/left-arrow.png"/>" alt="Previos Page" /></a> 
									Page <form:select path="numPage" onchange="goToPage(this.value);">
										 	<form:options items="${pageList}" />
										 </form:select> 
									of <span id="countPage">${countPage}</span> 
									<a href="javascript:nextPage();"><img src="<c:url value="/resources/images/right-arrow.png"/>" alt="Next Page" /></a>
									<input type="hidden" id="recordsCount" value="0" />
								</td>
							</tr>
						</table>
					</div>		
				</div><!--resultFooter-->
			</div><!--.block result-->
			
				
				
			</form:form>
		</div><!--.content-->
		
		<jsp:include page="include/page_footer.jsp" />
		
	</div><!--.wrapper-->
	
	<!-- build version: <spring:message code="build.version"/>  build date: <spring:message code="build.date"/> -->
</body>
</html>