<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<head>
	<jsp:include page="include/html_head.jsp" />
	<script src="<c:url value="/resources/scripts/lct_mapping.js"/>" type="text/javascript"></script>
</head>

<body>
	<jsp:include page="include/page_header.jsp" />
	<div class="wrapper">
		<div class="content">
			<form:form commandName="mappingForm" name="mappingForm">
				<div class="contentTop clearfix">
					<fieldset id="productInformation">
						<table>
							<tr>
								<td class="sizeA">
									<label for="mappingType">Mapping Type:</label>
								 	<form:select path="mappingType" onchange="mappingTypeChange(this);">
										<form:option  value="-1"  label="Select one"/>
										<form:options items="${mappingTypes}" itemValue="code" itemLabel="name" />
									</form:select>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
				<div id="result" class="block result">
					<table><tr><td align="left"><h2>Mapping</h2></td><td align="right"><div id="progressbar" class="progressbar"></div></td></tr></table>
					<div class="searchResults">
						<div id="mappingErrors" style="display: none;"></div>
						<table id="mappingTable" style="display: none;">
							<thead>
								<tr>
									<th class="tableH">Generic Value</th>
									<th class="tableH">Mapped Values</th>
									<th class="tableH">&nbsp;</th>
									<th class="tableH">Unmapped Values</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td><input type="text" id="genericValueFilter" onkeyup="showGenericValues()" /></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
								<tr>
									<td>
										<select id="genericValues" size="30" onclick="genericValuesClick(this)" onkeyup="genericValuesClick(this)"></select>
									</td>
									<td>
										<select id="mappedValues" size="30" ondblclick="javascript:unmapSelected()"></select>
									</td>
									<td style="width: 20px; vertical-align: middle;">
										<div id="mapUnmapButtons">
											<a href="javascript:unmapSelected()" style="display:block; height: 50px; width: 20px; text-decoration: none;">
												<img src="<c:url value="/resources/images/right_chevron.png" />" />
											</a>
											<a href="javascript:mapSelected()" style="display:block; height: 50px; width: 20px; text-decoration: none;">
												<img src="<c:url value="/resources/images/left_chevron.png" />" />
											</a>
										</div>
									</td>
									<td>
										<select id="unmappedValues" size="30" ondblclick="javascript:mapSelected()"></select>
									</td>
								</tr>
								<tr>
									<td>
										<div id="modifyGenericButtons">
											<input id="addNewGeneric" type="button" class="btnGray" value="Add New" onclick="$('#addGenericDialog').dialog({resizable: false, title: 'New Generic Value', modal: true})" />
											<!-- Hide removing generic items logic
											<input id="removeGeneric" type="button" value="Remove" onclick="removeGenericValue()"/> -->
											<input id="editGeneric" type="button" class="btnGray" value="Edit" onclick="editGenericValue()" />
										</div>
									</td>
									<td colspan="3">
									</td>
								</tr>
							</tbody>
						</table><!-- #mappingTable --> 								
					</div><!--.searchResults-->	
				
					<div class="resultFooter clearfix">
					</div><!--resultFooter-->
				</div><!--.block result-->
			
			</form:form>
		</div>
		
		<jsp:include page="include/page_footer.jsp" />

	</div>
	<!--.wrapper-->
	
	<div id="addGenericDialog" style="display:none;">
		<form name="addGenericForm">
			<table>
				<tbody>
					<tr>
						<td colspan="2">
							<input type="checkbox" name="newCodeAutogenerate" value="newCodeAutogenerate" checked="checked" onclick="newCodeAutogenerateClick(this)" />Automatically generate code
						</td>
					</tr>
					<tr>
						<td>
							<label for="newCode">Code:</label>
						</td>
						<td>
							<input type="text" name="newCode" maxlength="50" disabled="disabled" />
						</td>
					</tr>
					<tr>
						<td>
							<label for="newValue">Value:</label>
						</td>
						<td>
							<input type="text" name="newValue" maxlength="200" />
						</td>
					</tr>
					<tr>
						<td>
							<input type="button" class="btnGray" value="OK" onclick="saveNewGenericValue()" />
						</td>
						<td>
							<input type="button" class="btnGray" value="Cancel" onclick="$('#addGenericDialog').dialog('close')" />
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>	
	
	<div id="editGenericDialog" style="display:none;">
		<form name="editGenericForm">
			<table>
				<tbody>
					<tr>
						<td>
							<label for="editCode">Code:</label>
						</td>
						<td>
							<input type="text" name="editCode" maxlength="50" disabled="disabled" />
						</td>
					</tr>
					<tr>
						<td>
							<label for="editValue">Value:</label>
						</td>
						<td>
							<input type="text" name="editValue" maxlength="200" />
						</td>
					</tr>
					<tr>
						<td>
							<input type="button" class="btnGray" value="OK" onclick="updateGenericValue()" />
						</td>
						<td>
							<input type="button" class="btnGray" value="Cancel" onclick="$('#editGenericDialog').dialog('close')" />
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
	
</body>
</html>