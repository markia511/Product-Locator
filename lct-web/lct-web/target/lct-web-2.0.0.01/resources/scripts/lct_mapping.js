var lastMappingType = null;
var genericItems = new Array();
var showGenericItems = new Array();
var unmappedItems = new Array();
var mappedItems = new Array();
var lastSelectedGenericIndex = null;
var lastSelectedGenericValue = null;
var LIMIT_GENERIC_ITEMS_PARTITION = 29;

function mappingTypeChange(obj) {
	var newMappingType = $("#mappingType").val();
	$('#genericValueFilter').val('');
	if ($("#mappingType").val() != -1) {
		if (newMappingType != lastMappingType) { 
			ajaxGetGenericList(null);
		}
	} else {
		clearMappingTable(false);
	}
}

var SEE_MORE_CODE = 'SEE_MORE';
var SEE_MORE_OPTION_HTML = '<option style="text-align: center;" value="' + SEE_MORE_CODE + '">--See more--</option>\r\n'; 

function ajaxGetGenericList(selectedValue) {
	var requestUrl = "get_generic_list/" + $("#mappingType").val();
	var i;
	genericItems = new Array();
	showGenericItems = new Array();
	unmappedItems = new Array();
	mappedItems = new Array();
	lastSelectedGenericIndex = null;
	$.ajax({
		url: requestUrl,
		type: "POST",
		dataType: "JSON",
		headers: csrfAjaxHeader,
		success : function(Json) {
			try {
				clearMappingTable(true);
				hideMapUnmapButtons();
				lastMappingType = Json.mappingTypeCode; 
				genericItems = Json.genericItems;
				showGenericValues(selectedValue);
				
				/*var options = "";
				for (i = 0; i < genericItems.length && i < LIMIT_GENERIC_ITEMS_PARTITION; i++) {
					showGenericItems.push(genericItems[i]);
					options += '<option value="' + genericItems[i].code + '"';
					if (lastSelectedGenericIndex == null && selectedValue != null && genericItems[i].name == selectedValue) {
						lastSelectedGenericIndex = i;
					}
					options += '>'+escapeHtml(genericItems[i].name)+'</option>\r\n';
				}
				if(genericItems.length > LIMIT_GENERIC_ITEMS_PARTITION) {
					options += SEE_MORE_OPTION_HTML;
				}
				$('#genericValues').append(options);*/
						
				unmappedItems = Json.unmappedItems;
				var options = "";
				for (i = 0; i < unmappedItems.length; i++) {
					var unmappedValue = escapeHtml(unmappedItems[i]);
					options += '<option value="' + unmappedValue + '">'+unmappedValue+'</option>\r\n';
				}
				$('#unmappedValues').append(options);
				
			}
			catch (ex) {
				writeErrorMessage("Exception: " + escapeHtml(ex));
			}
		},
		error : function(e, xhr, errorThrown) {
			writeErrorMessage("Exception load data: " + getErrorDetails(e, xhr, errorThrown));
		}
	});
}


function ajaxGetMappedValues(genericCode) {
	var requestUrl = "get_mapped_values/" + $("#mappingType").val() + "/" + genericCode;
	var i;
	mappedItems = new Array();
	
	$.ajax({
		url: requestUrl,
		type: "POST",
		dataType: "JSON",
		headers: csrfAjaxHeader,
		success : function(Json) {
			try {
				$('#mappedValues').empty();
				mappedItems = Json;
				var options = "";
				for (i = 0; i < mappedItems.length; i++) {
					var mappedValue = escapeHtml(mappedItems[i]);
					options += '<option value="' + mappedValue + '">'+mappedValue+'</option>\r\n';
				}
				$('#mappedValues').append(options);
				showMapUnmapButtons();
			}
			catch (ex) {
				writeErrorMessage("Exception: " + escapeHtml(ex));
			}
		},
		error : function(e, xhr, errorThrown) {
			writeErrorMessage("Exception load data: " + getErrorDetails(e, xhr, errorThrown));
		}
	});
}

function ajaxGetUnmappedValues(genericCode) {
	var requestUrl = "get_unmapped_values/" + $("#mappingType").val();
	var i;
	unmappedItems = new Array();
	
	$.ajax({
		url: requestUrl,
		type: "POST",
		dataType: "JSON",
		headers: csrfAjaxHeader,
		success : function(Json) {
			try {
				$('#unmappedValues').empty();
				unmappedItems = Json;
				var options = "";
				for (i = 0; i < unmappedItems.length; i++) {
					var unmappedValue = escapeHtml(unmappedItems[i]);
					options += '<option value="' + unmappedValue + '">'+unmappedValue+'</option>\r\n';
				}
				$('#unmappedValues').append(options);
				
			}
			catch (ex) {
				writeErrorMessage("Exception: " + escapeHtml(ex));
			}
		},
		error : function(e, xhr, errorThrown) {
			writeErrorMessage("Exception load data: " + getErrorDetails(e, xhr, errorThrown));
		}
	});
}

function ajaxMapValue(genericCode, value) {
	var requestUrl = "map_value/" + $("#mappingType").val() + "/" + genericCode + "/" + encodeUrlParam(value);
	var i;

	lockPage();
	$.ajax({
		url: requestUrl,
		type: "POST",
		dataType: "JSON",
		headers: csrfAjaxHeader,
		success : function(Json) {
			try {
				if (Json == true) {
					$("#unmappedValues option[value='"+value+"']").remove();
					i = unmappedItems.indexOf(value);
					if (i >= 0) {
						unmappedItems.slice(i,1);
					}
					ajaxGetMappedValues(genericCode);
				}
			} catch (ex) {
				writeErrorMessage("Exception: " + escapeHtml(ex));
			} finally {
				unLockPage();
			}
		},
		error : function(e, xhr, errorThrown) {
			unLockPage();
			writeErrorMessage("Exception load data: " + getErrorDetails(e, xhr, errorThrown));
		}
	});
}

function ajaxUnmapValue(genericCode, value) {
	var requestUrl = "unmap_value/" + $("#mappingType").val() + "/" + genericCode + "/" + encodeUrlParam(value);
	var i;
	
	lockPage();
	$.ajax({
		url: requestUrl,
		type: "POST",
		dataType: "JSON",
		headers: csrfAjaxHeader,
		success : function(Json) {
			try {
				if (Json == true) {
					$("#mappedValues option[value='"+value+"']").remove();
					i = mappedItems.indexOf(value);
					if (i >= 0) {
						mappedItems.splice(i,1);
					}
					ajaxGetUnmappedValues(genericCode);
				}
			} catch (ex) {
				writeErrorMessage("Exception: " + escapeHtml(ex));
			} finally {
				unLockPage();
			}
		},
		error : function(e, xhr, errorThrown) {
			unLockPage();
			writeErrorMessage("Exception load data: " + getErrorDetails(e, xhr, errorThrown));
		}
	});
}

function ajaxAddGenericValue(autoGenerateCode, newCode, newValue) {
	var requestUrl = "add_generic_value/" + $("#mappingType").val() + "/" + autoGenerateCode + "/" + 
			encodeUrlParam(newCode) + "/" + encodeUrlParam(newValue);
	$.ajax({
		url: requestUrl,
		type: "POST",
		dataType: "JSON",
		headers: csrfAjaxHeader,
		success : function(Json) {
			try {
				if (Json == true) {
					ajaxGetGenericList(newValue);
					$('#addGenericDialog').dialog('close');
				}
			}
			catch (ex) {
				writeErrorMessage("Exception: " + escapeHtml(ex));
			}
		},
		error : function(e, xhr, errorThrown) {
			writeErrorMessage("Exception load data: " + getErrorDetails(e, xhr, errorThrown));
		}
	});
}

function ajaxRemoveGenericValue(code) {
	var requestUrl = "remove_generic_value/" + $("#mappingType").val() + "/" + encodeUrlParam(code);

	$.ajax({
		url: requestUrl,
		type: "POST",
		dataType: "JSON",
		headers: csrfAjaxHeader,
		success : function(Json) {
			try {
				if (Json == true) {
					ajaxGetGenericList(null);
				}
			}
			catch (ex) {
				writeErrorMessage("Exception: " + escapeHtml(ex));
			}
		},
		error : function(e, xhr, errorThrown) {
			writeErrorMessage("Exception load data: " + getErrorDetails(e, xhr, errorThrown));
		}
	});
}

function writeErrorMessage(errorMessage) {
	$('#mappingTable').css("display","none");
	errorsDiv = $('div.searchResults #mappingErrors');
	errorsDiv.empty();
	errorsDiv.append(errorMessage);
	errorsDiv.css("display","block");
}

function mapSelected() {
	if (lastSelectedGenericIndex != null && lastSelectedGenericIndex >= 0) {
		var selectedUnmappedOption = $("#unmappedValues option:selected")[0];
		if (typeof selectedUnmappedOption != 'undefined') {
			var selectedSourceStr = selectedUnmappedOption.value;
			if (selectedSourceStr != null && typeof selectedSourceStr != 'undefined') {
				ajaxMapValue(showGenericItems[lastSelectedGenericIndex].code, selectedSourceStr);
			}
		}

	}
}


function unmapSelected() {
	if (lastSelectedGenericIndex != null && lastSelectedGenericIndex >= 0) {
		var selectedMappedOption = $("#mappedValues option:selected")[0];
		if (typeof selectedMappedOption != 'undefined') {
			var selectedSourceStr = selectedMappedOption.value;
			if (selectedSourceStr != null && typeof selectedSourceStr != 'undefined') {
				ajaxUnmapValue(showGenericItems[lastSelectedGenericIndex].code, selectedSourceStr);
			}
		}

	}
}

function genericValuesClick(genericValues) {
	var selectedIndex = genericValues.selectedIndex;
	if(genericValues[selectedIndex].value == SEE_MORE_CODE) {
		showGenericValues(null, true);
		return;
	}
	if (selectedIndex != lastSelectedGenericIndex) {
		$('#mappedValues').empty();
		if (selectedIndex >= 0) {			
			ajaxGetMappedValues(showGenericItems[selectedIndex].code);				
		}
		lastSelectedGenericIndex = selectedIndex;
		lastSelectedGenericValue = showGenericItems[selectedIndex].name;
	}
}

function clearMappingTable(isDisplay) {
	$('div.searchResults #mappingErrors').css("display","none");
	lastMappingType = null;
	genericItems = new Array();
	showGenericItems = new Array();
	lastSelectedGenericIndex = null;
	lastSelectedGenericValue = null;
	$('#genericValues').empty();
	$('#mappedValues').empty();
	$('#unmappedValues').empty();
	if (isDisplay) {
		$('#mappingTable').css("display","block");
	}
	else {
		$('#mappingTable').css("display","none");
	}
}

function hideMapUnmapButtons() {
	$('mapUnmapButtons').css("display","none");
}

function showMapUnmapButtons() {
	$('mapUnmapButtons').css("display","block");
}

var ERROR_EMPTY_CODE = 'Code is empty';
var ERROR_EMPTY_VALUE = 'Value is empty';
var ERROR_EXIST_CODE = 'Code already exists';
var ERROR_EXIST_VALUE = 'Value already exists';

function saveNewGenericValue() {
	var autoGenerateCode = $('input[name="newCodeAutogenerate"]').attr('checked') ? '1' : '0';
	var newCode = $('input[name="newCode"]').val();
	var newValue = $('input[name="newValue"]').val();
	var isError = false;
	
	if (autoGenerateCode == '1') {
		newCode = '*';
	} else {
		isError = valdiateField(newCode, ERROR_EMPTY_CODE, isError);
	}
	
	isError = valdiateField(newValue, ERROR_EMPTY_VALUE, isError);
	isError = validateExistGenericCode(newCode, ERROR_EXIST_CODE, isError, !isError && autoGenerateCode == '0');
	isError = validateExistGenericValue(newValue, ERROR_EXIST_VALUE, isError, !isError);
	
	if (!isError) {
		ajaxAddGenericValue(autoGenerateCode, newCode, newValue);
	}
}

function valdiateField(fieldValue, errorMessage, isError) {
	if (!isError && (fieldValue == null || typeof fieldValue == 'undefined' || fieldValue.length == 0)) {
		alert(errorMessage);
		return true;
	}
	return isError;
}

function validateExistGenericCode(fieldCode, errorMessage, isError, isShow, index) {
	if(isShow) {
	for (var i = 0; i < genericItems.length; i++) {
		if (genericItems[i].code == fieldCode
				&& (!index || index != i)) {
			isError = true;
			alert(errorMessage);
			break;
		}
	}	
	}
	return isError;
}

function validateExistGenericValue(fieldValue, errorMessage, isError, isShow, index) {
	if(isShow) {
	for (var i = 0; i < genericItems.length; i++) {
		if (genericItems[i].name == fieldValue
				&& (!index || index != i)) {
			isError = true;
			alert(errorMessage);
			break;
		}
	}	
	}
	return isError;
}

function newCodeAutogenerateClick(checkBox) {
	if (checkBox.checked) {
		$('input[name="newCode"]').attr("disabled","disabled");
	}
	else {
		$('input[name="newCode"]').removeAttr("disabled");
	}
}

function removeGenericValue() {
	if (lastSelectedGenericIndex >= 0 && showGenericItems.length > 0) {
		var valueItem = showGenericItems[lastSelectedGenericIndex];
		if (mappedItems.length > 0) {
			alert('Mapped values exist');
		}
		else 
		if (confirm('You are going to remove value "' + valueItem.name +'"\r\nAre you sure?')) {
			ajaxRemoveGenericValue(valueItem.code);
		}
	}
}

function editGenericValue() {
	if (lastSelectedGenericIndex >= 0 && showGenericItems.length > 0) {
		var valueItem = showGenericItems[lastSelectedGenericIndex];
		$('input[name="editCode"]').val(valueItem.code);
		$('input[name="editValue"]').val(valueItem.name);
		$('#editGenericDialog').dialog({resizable: false, title: 'Edit Generic Value', modal: true});
	}
}

function updateGenericValue() {
	var editCode = $('input[name="editCode"]').val();
	var editValue = $('input[name="editValue"]').val();
	var isError = false;
	isError = valdiateField(editValue, ERROR_EMPTY_VALUE, isError);
	isError = validateExistGenericValue(editValue, ERROR_EXIST_VALUE, isError, !isError, lastSelectedGenericIndex);
	if (!isError) {
		ajaxUpdateGenericValue(editCode, editValue);
	}
}

function ajaxUpdateGenericValue(editCode, editValue) {
	var requestUrl = "update_generic_value/" + $("#mappingType").val() + "/" + 
						encodeUrlParam(editCode) + "/" + encodeUrlParam(editValue);

	$.ajax({
		url: requestUrl,
		type: "POST",
		dataType: "JSON",
		headers: csrfAjaxHeader,
		success : function(Json) {
			try {
				if (Json == true) {
					ajaxGetGenericList(editValue);
					$('#editGenericDialog').dialog('close');
				}
			}
			catch (ex) {
				writeErrorMessage("Exception: " + escapeHtml(ex));
			}
		},
		error : function(e, xhr, errorThrown) {
			writeErrorMessage("Exception load data: " + getErrorDetails(e, xhr, errorThrown));
		}
	});
}

function showGenericValues(selectedValue, isShowMore) {
	var filterValue = $('#genericValueFilter').val().toUpperCase();
	var filterLength = filterValue == null ? '' : filterValue.length;
	var options = "";
	var limitGenericItems = LIMIT_GENERIC_ITEMS_PARTITION;
	var selectedIndex = null;
	if(isShowMore) {
		limitGenericItems += showGenericItems.length;
	}
	if(selectedValue == null) {
		selectedValue = lastSelectedGenericValue;
	}
	showGenericItems = new Array();
	for (var i = 0; i < genericItems.length && showGenericItems.length < limitGenericItems; i++) {
		if(filterLength == 0 
				|| filterValue == genericItems[i].name.substring(0, filterLength).toUpperCase()) {
			showGenericItems.push(genericItems[i]);	
			options += '<option value="' + genericItems[i].code + '"';
			options += '>'+escapeHtml(genericItems[i].name)+'</option>\r\n';
			if (selectedValue != null && genericItems[i].name == selectedValue) {
				selectedIndex = showGenericItems.length - 1;
			}
		} else if(filterLength > 0 && showGenericItems.length > 0) {
			break;
		}
	}
	if(showGenericItems.length >= limitGenericItems)
		options += SEE_MORE_OPTION_HTML;
	$('#genericValues').html(options);

	lastSelectedGenericIndex = selectedIndex;
	if(lastSelectedGenericIndex == null) {
		lastSelectedGenericValue = null;
		$('#mappedValues').empty();
	} else {
		lastSelectedGenericValue = selectedValue;	
		$('#genericValues option')[lastSelectedGenericIndex].selected = true;	
	}
}

function lockPage() {
	$('#mappingType').attr("disabled","disabled");
	$('#genericValues').attr("disabled","disabled");
	$('#mappedValues').attr("disabled","disabled");
	$('#unmappedValues').attr("disabled","disabled");
	$('#genericValueFilter').attr("disabled","disabled");
	$('#mapUnmapButtons').css("display","none");
	$('#modifyGenericButtons').css("display","none");
}

function unLockPage() {
	$('#mappingType').removeAttr("disabled");
	$('#genericValues').removeAttr("disabled");
	$('#mappedValues').removeAttr("disabled");
	$('#unmappedValues').removeAttr("disabled");
	$('#genericValueFilter').removeAttr("disabled");
	$('#mapUnmapButtons').css("display","block");
	$('#modifyGenericButtons').css("display","block");
}
