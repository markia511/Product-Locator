
var DELIMETER_DICTIONARY = " and ";
var DELIMETER_DICTIONARY_REGEXP = DELIMETER_DICTIONARY.substring(0, DELIMETER_DICTIONARY.length - 1)+"$";
var NOT_SELECTED_VALUE = "-1";

var geocoder;
var timeout = 600;
var changed = false;
var searchBtnClicked = false;
var isAdvancedSearch = true;
var map;

var sortOutletName = "OUTLET_NAME";
var sortDistance = "DISTANCE";
//var sortTradeChannel = "TRADE_CHANNEL";
var sortTradeSubChannel = "TRADE_SUB_CHANNEL";

var sortIdMap = new Object();
sortIdMap[sortOutletName] = "outletNameHeader";
sortIdMap[sortDistance] = "distanceHeader";
//sortIdMap[sortTradeChannel] = "tradeChannelHeader";
sortIdMap[sortTradeSubChannel] = "subTradeChannelHeader";

/* autocomplete for keyword dictionary */
var availableTags;
var dictionaries = new Array();

/* sort result */
var ASCENDING = "ASC";
var DESCENDING = "DESC";
var sortColumn = "DISTANCE";
var sortOrder = ASCENDING;
var DEFAULT_DISTANCE = '5';

/* search outlet */
var lastSearchURLWthtPgn;

// var csrfToken = $("meta[name='_csrf']").attr("content");
// alert(csrfToken);


$ (function() {
	
	
	// $('#searchForm').find('.switchForm').bind('click.toggleForm', switchTab);

	/* set default distance unit in miles */
	$("input:radio[name='distanceUnitAdvanced'][value='mi']").click();
	$("input:radio[name='distanceUnitKeyword'][value='mi']").click();
	
	$("#"+sortIdMap[sortColumn]).parent().addClass("sort_asc");
	changeMapCSSAdvancedSearch();
	
	initialize_map();
	init_google_components();	
	init_autocomplete_keyword_dictionary();

	/* TODO: change to another way submit */
//	$("input#prodInfo").keyup(function(e) {
//		if(e.keyCode == $.ui.keyCode.ENTER) {
//			clearError();
//			validationKeywordProdInfo();
//			clickSearchBtn();
//		}
//	});
	
	$("input#searchTextField").keyup(function(e) {
		/* TODO: change to another way submit */
//		if(e.keyCode == $.ui.keyCode.ENTER) {
//			clearError();
//			validationKeywordAddress();
//			clickSearchBtn();
//		}
		if (e.keyCode != $.ui.keyCode.TAB) {
			changeAddress(); // Fix IE bug - onchange event caused only after lost focus		
		}
	});

	/* select tooltip by title */		
	$("select").each(function() {
		this.title = this.options[this.selectedIndex].text;
		$(this).find("option").each(function() {
			this.title = this.text;
		});
	});

	$("select").change(function() {
		this.title = this.options[this.selectedIndex].text;
	});
});

function changeMapCSSAdvancedSearch() {
	var $divSearchForm = $("div#contentTop");
/*	
	,
		$searchForm = $("#searchForm"),
		$blockMap = $divSearchForm.children("div.map"),
		$blockMapHeader = $blockMap.children("h2"),
		$mapCanvas = $blockMap.children("#map_canvas");
*/	
	$divSearchForm.removeClass("keyw");
	$divSearchForm.addClass("adv");
/*	
	$mapCanvas.height($searchForm.height() - $blockMapHeader.outerHeight(true));
*/
	/* Set magic height */
	/*
	$("#map_canvas").css("height", "413px");
	$("div.keyword").css('display', 'none');
	*/
}

function changeMapCSSKeywordSearch() {
	var $divSearchForm = $("div#contentTop");
	$divSearchForm.removeClass("adv");
	$divSearchForm.addClass("keyw");
	
	/* Set magic height */
	/*
	var $divKeyword = $("div.keyword");
	$divKeyword.css('display', 'block');
	
	var $mapCanvas = $("#map_canvas");
	if($.browser.msie) {		
		if($.browser.version < 9) {
			$divKeyword.css("min-height", "334px");		
			$mapCanvas.css("height", "364px");
		} else {
			$divKeyword.css("min-height", "331px");
			$mapCanvas.css("height", "361px");				
		}
	} else {
		$divKeyword.css("min-height", "310px");	
		$mapCanvas.css("height", "338px");
	}
	*/
}

function switchTab() {
	$('#searchForm').toggleClass('showKeywordForm');
	var switchBtn = document.getElementById('switchForm');
	clearError();
	if(!isAdvancedSearch) {
		switchBtn.value = "Switch to Keyword Search";
		switchAdvancedSearch();
	} else {
		switchBtn.value = "Switch to Advanced Search";
		switchKeywordSearch();
	}
	isAdvancedSearch = !isAdvancedSearch;
}

var lastIncludeFoodServiceLocation = false;
function switchAdvancedSearch() {
	clearAdvancedSearch(true);
	if(changed) {
		geocode(true);
	}
	var elements = split($("input#prodInfo").val());
	var isDictionaryElem;
	var element;
	var tradeChannelCode = NOT_SELECTED_VALUE;
	var subTradeChannelCode = NOT_SELECTED_VALUE;
	for(var i = 0; i < elements.length; i++) {
		element = $.trim(elements[i]);
		isDictionaryElem = false;
		if(element != '') {
			element = element.toLowerCase();
			for(var j = 0; j < dictionaries.length; j++) {
				if(element == dictionaries[j].name.toLowerCase()) {
					var code = dictionaries[j].code;
					switch(dictionaries[j].dictionary) {
						case 'BEV_CAT': selectOption("beverageCategory", code); isDictionaryElem = true; break;
						case 'BEV_BRAND': selectOption("beverageBrand", code); isDictionaryElem = true; break;
						case 'BEV_FLAVOR': selectOption("beverageFlavor", code); isDictionaryElem = true; break;
						case 'PROD': selectOption("product", code); isDictionaryElem = true; break;
						case 'CHANNEL': selectOption("tradeChannel", code); tradeChannelCode = code; isDictionaryElem = true; break;
						case 'SUB_CHANNEL': selectOption("subTradeChannel", code); subTradeChannelCode = code; isDictionaryElem = true; break;
//						case 'PR_CONT': selectOption("primaryContainer", code); isDictionaryElem = true; break;
//						case 'SEC_PKG': selectOption("secondaryPackage", code); isDictionaryElem = true; break;
						case 'CONT_TYPE': selectOption("shortPrimaryContainer", code); isDictionaryElem = true; break;
						case 'PACK_SIZE': selectOption("shortSecondarypackage", code); isDictionaryElem = true; break;
						case 'PR_PKG_TYPE': selectOption("productPackageType", code); isDictionaryElem = true; break;
						case 'BUS_TYPE': selectOption("businessType", code); isDictionaryElem = true; break;
						case 'PHYS_STATE': selectOption("physicalState", code); isDictionaryElem = true; break;
					}
					break;
				} 
			}
			if(!isDictionaryElem) {
				$("#outletName").val(elements[i]);
			}
		}
	}
	if($("#beverageCategory").val() != NOT_SELECTED_VALUE) {
		refreshBrand();
	} else if($("#beverageBrand").val() != NOT_SELECTED_VALUE) {
		refreshFlavor();
	} else if($("#beverageFlavor").val() != NOT_SELECTED_VALUE) {
		refreshProduct();
	} else if($("#product").val() != NOT_SELECTED_VALUE) {
		refreshShortPrimaryContainer();
	} else if($("#shortPrimaryContainer").val() != NOT_SELECTED_VALUE) {
		refreshShortSecondaryPackage();
	}
	
	if($("#tradeChannel").val() != NOT_SELECTED_VALUE) {
		getChildsListValue('subTradeChannel', 'tradeChannel');		
	}
	if(document.locatorForm.includeFoodService.checked != lastIncludeFoodServiceLocation) {
		changeIncludeFoodService(true, tradeChannelCode, subTradeChannelCode);		
	}
	lastIncludeFoodServiceLocation = document.locatorForm.includeFoodService.checked;
	var distanceUnit = $("input:radio[name='distanceUnitKeyword']:checked").val();
	$("input:radio[name='distanceUnitAdvanced'][value='" + distanceUnit + "']").click();
	$("#distance").val($("#distanceK").val());	
	changeMapCSSAdvancedSearch();
}

function selectOption(selectId, value) {
	var $select = $("#" + selectId);
	$select.val(value);
	if(!$select.val()) {
		$select.val(NOT_SELECTED_VALUE);
	}
	$select.attr('title', $('#' + selectId + ' :selected').text());
}

function switchKeywordSearch() {
	clearKeywordSearch();
	var prodInfo = '';	
	prodInfo += getSelectedNameValueWithAnd(document.locatorForm.beverageCategory);
	prodInfo += getSelectedNameValueWithAnd(document.locatorForm.beverageBrand);	
	prodInfo += getSelectedNameValueWithAnd(document.locatorForm.beverageFlavor);
	prodInfo += getSelectedNameValueWithAnd(document.locatorForm.product);
	prodInfo += getSelectedNameValueWithAnd(document.locatorForm.productPackageType);
//	prodInfo += getSelectedNameValueWithAnd(document.locatorForm.primaryContainer);
//	prodInfo += getSelectedNameValueWithAnd(document.locatorForm.secondaryPackage);	
	prodInfo += getSelectedNameValueWithAnd(document.locatorForm.shortPrimaryContainer);
	prodInfo += getSelectedNameValueWithAnd(document.locatorForm.shortSecondaryPackage);	
	prodInfo += getSelectedNameValueWithAnd(document.locatorForm.businessType);		
	prodInfo += getSelectedNameValueWithAnd(document.locatorForm.physicalState);			
	prodInfo += getSelectedNameValueWithAnd(document.locatorForm.tradeChannel);
	prodInfo += getSelectedNameValueWithAnd(document.locatorForm.subTradeChannel);
	prodInfo += getInputValueWithAnd(document.locatorForm.outletName);
	$("input#prodInfo").val(prodInfo);
	$("input#searchTextField").val(getAdvancedAddress());
	var distanceUnit = $("input:radio[name='distanceUnitAdvanced']:checked").val();
	$("input:radio[name='distanceUnitKeyword'][value='" + distanceUnit + "']").click();
	$("#distanceK").val($("#distance").val());
	if(!$("#distanceK").val()) {
		$("#distanceK").val(DEFAULT_DISTANCE);
	}
	changeMapCSSKeywordSearch();
}

function getSelectedNameValueWithAnd(select) {
	return getSelectedNameValueWithDelimeter(select, DELIMETER_DICTIONARY);
}

function getSelectedNameValueWithSpace(select) {
	return getSelectedNameValueWithDelimeter(select, ' ');
}

function getSelectedNameValueWithDelimeter(select, delimeter) {
	if(select.value != NOT_SELECTED_VALUE && select.selectedIndex >= 0) 
		return select.options[select.selectedIndex].text + delimeter;
	return '';
}


function getInputValueWithAnd(input) {
	return getInputValueWithDelimeter(input, DELIMETER_DICTIONARY);
}

function getInputValueWithSpace(input) {
	return getInputValueWithDelimeter(input, ' ');
}

function getInputValueWithDelimeter(input, delimeter) {
	if(input.value != '') 
		return input.value + delimeter;
	return '';
}

function init_google_components() { 
	geocoder = new google.maps.Geocoder();
	var input = document.locatorForm.searchTextField;
	/* TODO: change bounds for USA and Canada*/
    var p1 = new google.maps.LatLng(65.00, -82.00);
    var p2 = new google.maps.LatLng(45.00, -139.00);
    var defaultBounds = new google.maps.LatLngBounds(p1, p2);
	var options = {
	  bounds: defaultBounds
	};
	var autocomplete = new google.maps.places.Autocomplete(input, options);
	google.maps.event.addListener(autocomplete, 'place_changed', function () {
		var place = autocomplete.getPlace();
		if(place.geometry) {
			fillResultTextField(place);
		}
	});		
	input.value = '';
//	var searchBtn = document.locatorForm.searchBtn;
	if(input.addEventListener) {
//		searchBtn.addEventListener('click', clickSearchBtn, true);
		input.addEventListener('change', changeAddress, true);
	} else if (input.attachEvent){
//		searchBtn.attachEvent('onclick', clickSearchBtn);
		input.attachEvent('onchange', changeAddress);
	} 
}

function initialize_map() {
    var uslatlng = new google.maps.LatLng(42.090240,-95.7128910);
    drawMap(uslatlng, 3);
}

function processAjaxJsonError(e, xhr) {
	if (xhr == "parsererror" && e.status == 200) {
		if (e.responseText != "undefined" && e.responseText.indexOf("security_check") > 0) {
			window.location.reload();
		}
	}
}


var dictionaryLength = 0;

function init_autocomplete_keyword_dictionary() {

	var $prodInfo = $("input#prodInfo");
    $.ajax({
        url: "keywordDictionary",
        type: "POST",
        dataType: "JSON",
        headers: csrfAjaxHeader,
        success: function (Json) {
        	dictionaries = Json;
        	availableTags = new Array();
        	$.each(Json, function (i, e) {
        		if(e.name) {
        			availableTags[i] = e.name;
        		}
            });
        	dictionaryLength = availableTags.length;
        	$prodInfo.bind( "keyup", function( event ) {
 				if ( (event.keyCode === $.ui.keyCode.TAB || event.keyCode === $.ui.keyCode.ENTER) &&
 						$( this ).data( "uiAutocomplete" ).menu.active ) {
 					event.preventDefault();
 				}
 				if(this.value.length > DELIMETER_DICTIONARY.length
 						&& (event.keyCode == 32 || (event.keyCode == $.ui.keyCode.BACKSPACE))) {
 					
 					if (this.value.substr(this.value.length - DELIMETER_DICTIONARY.length + 1, this.value.length) == DELIMETER_DICTIONARY.substr(0, DELIMETER_DICTIONARY.length - 1) ||
 							(event.keyCode == $.ui.keyCode.BACKSPACE && this.value.substr(this.value.length - DELIMETER_DICTIONARY.length) == DELIMETER_DICTIONARY)) {
 						var terms = split(this.value);
 						if(terms.length > 0) {
	 						availableTags = getUpdatedDictionaries(terms);
 						}
 					}
 				} else if ((this.value.length == 0 || (this.value.length >= 2 && this.value.length < 6))
 						&& dictionaryLength > availableTags.length) {
 					returnAllDictionaries();
 				}
 			}).autocomplete({
 				delay: 0,
 				minLength: 0,
 				source: function( request, response ) {
 					// delegate back to autocomplete, but extract the last term
 					response( $.ui.autocomplete.filter(
 							availableTags, extractLast( request.term ) ) );
 				},
 				focus: function() {
 					// prevent value inserted on focus
 					return false;
 				},
 				select: function( event, ui ) {
 					var terms = split( this.value );
 					// remove the current input
 					terms.pop();
 					// add the selected item
 					terms.push( ui.item.value );
 					
 					availableTags = getUpdatedDictionaries(terms);

 					// add placeholder to get the comma-and-space at the end
 					terms.push( "" );
 					
 					this.value = terms.join( DELIMETER_DICTIONARY );
 					return false;
 				}
 			});
        },
        error: function (e, xhr, errorThrown) {
        	$prodInfo.html("Exception load data: "+getErrorDetails(e, xhr, errorThrown));
        	processAjaxJsonError(e, xhr);
        }
    });
    
	function extractLast( term ) {
		return split( term ).pop();
	}
}

function split( val ) {
	if (val != null) {
		if (val.match(DELIMETER_DICTIONARY_REGEXP)) {
			val = val + " ";
		} 
		return val.split(/ and \s*/);
	} else {
		return val;
	}
}

function getUpdatedDictionaries(terms) {

	var removedDictionaries = new Array();
	var index = 0;
	var termsLength = terms.length;
	var curTerm;
	var isAlreadyRemoved;
	for(var j = 0; j < termsLength; j++) {
		curTerm = $.trim(terms[j]).toLowerCase();
		for(var i = 0; i < dictionaries.length; i++) {
			if(dictionaries[i] && curTerm == dictionaries[i].name.toLowerCase()) {
				isAlreadyRemoved = false;
				// "Coca-Cola Zero" can be a brand name and a product name
				for (var k = 0; k < removedDictionaries.length; k++) {
					if (removedDictionaries[k] == dictionaries[i].dictionary) {
						isAlreadyRemoved = true;
						break;
					}
				}
				if (!isAlreadyRemoved) {
					removedDictionaries[index++] = dictionaries[i].dictionary;
					break;
				}
			}
		}
	}
	if(removedDictionaries.length > 0) {
		var newAvailableTags = new Array();
		var isRemoved = false;
		var dictionariesLength = dictionaries.length;
		var curDictItem;
		for(var i = 0; i < dictionariesLength; i++) {
			curDictItem = dictionaries[i];
			if(curDictItem) {
				for(var j = 0; j < removedDictionaries.length; j++) {
					if(removedDictionaries[j] == curDictItem.dictionary) {
						isRemoved = true;
						break;
					}
				}
				if(!isRemoved) {
					newAvailableTags.push(curDictItem.name);
				}
				isRemoved = false;
			}
		}
		return newAvailableTags;
	}
	return availableTags;
}

function returnAllDictionaries() {
	availableTags = new Array();
	var dictionariesLength = dictionaries.length;
	for(var i = 0; i < dictionariesLength; i++) {
		availableTags.push(dictionaries[i].name);
	}
}

function changeAddress() {
	changed = true;
}
	
function clickSearchBtn() {
	if(validation()) {
		searchBtnClicked = true;
		if(!isAdvancedSearch) {
			if(changed) 
				geocode();
			else 
				submitSearchForm();
		} else {
			geocode();
		}
	}
}

/* validation functions */
function validation() {
	
	var error = false;
	clearError();
	
	if(isAdvancedSearch) {
	    	    
	    if(validationAdvancedAddress()) {
	    	error = true;
		}
	} else {
		if(validationKeywordProdInfo()) {
	    	error = true;
		} 
		if(validationKeywordAddress()) {
	    	error = true;
		}		
	}
	return !error;
}

function validateDistance() {
	var distance = $("#distance").val();
	var RE = /^[0-9]+$/;
	if (distance == null || distance == 'undefined' || $.trim(distance) == "" || !RE.test(distance)) {
		return 'distanceAdvancedError';
	}
	
	if (distance > 100) {
		return 'distanceTooBigAdvancedError'; 
	}
	
	return null;
}

function validationAdvancedAddress() {
	if(
//			isEmpty($("#address").val()) && 
    		isEmpty($.trim($("#city").val())) 
    		&& isUnselected($("#state").val()) 
    		&& isEmpty($.trim($("#zip").val()))) {
    	displayError('addressAdvancedError');
    	return true;
	}
	
	var distanceError = validateDistance();
	if (distanceError != null) {
		displayError(distanceError);
		return true;
	}
	return false;
}

function validationKeywordProdInfo() {
	if(isEmpty($("input#prodInfo").val())) {
		displayError('productInfoError');
    	return true;
	}
	return false;
}

function validationKeywordAddress() {
	if(isEmpty($.trim($("input#searchTextField").val()))) {
		displayError('addressKeywordError');
    	return true;
	}
	return false;
}

function validationRecordsCount(recordsCount, maxOutletRecordsCount) {
	clearError();
	if(recordsCount == 0) {
		displayError('noResultsError');
	} else if(recordsCount > maxOutletRecordsCount) {
		displayError('maxOutletRecoredsError');
	}
}

function isEmpty(data) {
	return data == '' ? true : false;
}
function isUnselected(data) {
	return data == NOT_SELECTED_VALUE ? true : false;
}

function displayError(error) {
	$("div."+error).css("display", "block");
}

function clearError() {
	$("div.addressAdvancedError").css("display", "none");
	$("div.distanceAdvancedError").css("display", "none");
	$("div.distanceTooBigAdvancedError").css("display", "none");
	$("div.addressKeywordError").css("display", "none");
	$("div.productInfoError").css("display", "none");
	$("div.maxOutletRecoredsError").css("display", "none");
	$("div.noResultsError").css("display", "none");
}

var cur_long;
var cur_lat;

function submitSearchForm() {
	cur_long = document.locatorForm.longitude.value;
	cur_lat = document.locatorForm.latitude.value;
	if(cur_long != 0 
			&& cur_lat != 0) {
		ajaxSearchRequest(getSearchRequestUrl(cur_lat, cur_long));
	}
	searchBtnClicked = false;
}

function ajaxSearchRequest(responseJsonDataURL) {
	   lockPage();
	   $.ajax({
	        url: responseJsonDataURL,
	        type: "POST",
	        dataType: "JSON",
	        headers: csrfAjaxHeader,
	        success: function (Json) {
	        	try {
		        	var searchResult = "";
		        	var markers = new Array();
		        	var locations = Json.location;
		        	var distanceUnit = Json.distanceUnitName;
		        	var maxOutletRecordsCount = $("#maxOutletRecordsCount").val();
		        	var recordsCount = Json.recordsCount;
		        	validationRecordsCount(recordsCount, maxOutletRecordsCount);
		        	for(var i = 0; i < locations.length; i++) {
		        		var e = locations[i];
		            	var rowspan = e.productPackage.length;
		            	var outletLetter = getOutletLetter(i);
		            	var outletAddress = e.outlet.address.formattedAddress;         			       
		            	var outletName = e.outlet.chainName;
		            	var outletNameUri = encodeURIComponent(outletName);
		            	var outletAddressUri = encodeURIComponent(outletAddress);
		            	// var href = 'https://maps.google.com/maps?q=' + outletNameUri + '%20' + outletAddressUri + '&hl=en&hq=' + outletNameUri + '&hnear=' + outletAddressUri;
		            	var href = 'https://maps.google.com/maps?q=loc:' 
		            		+ e.outlet.address.latitude
		            		+ ','
		            		+ e.outlet.address.longitude
		            		+ '%20('
		            		+ outletNameUri + '%20' + outletAddressUri 
		            		+ ')&hl=en&hq=' + outletNameUri + '&hnear=' + outletAddressUri
		            		+ '&ll='
		            		+ e.outlet.address.latitude
		            		+ ','
		            		+ e.outlet.address.longitude
		            		+ '&daddr=loc:'
		            		+ e.outlet.address.latitude
		            		+ ','
		            		+ e.outlet.address.longitude
		            		+ '%20('
		            		+ outletAddressUri
		            		+ ')';
		            	if (cur_lat && cur_long) {
		            		href+='&saddr=loc:'+cur_lat+','+cur_long;
		            	}
		            	
		            	outletName = escapeHtml(outletName);
		            	outletAddress = escapeHtml(outletAddress);
		            	
		            	searchResult += ('<tr' + ((i+1)%2 == 0 ? ' class="cellBg"' : '') + '>' + 
		            			'<td width="10" rowspan="' + rowspan + '"><strong class="cap">' + outletLetter + '</strong></td>' + 
		            			'<td rowspan="' + rowspan + '"><a href="' + href + '" target="_blank">' + outletName + '</a></td>' +
		            			'<td rowspan="' + rowspan + '">' + outletAddress + '</td>' +
		            			'<td rowspan="' + rowspan + '">' + e.distance + ' ' + distanceUnit + '</td>' +
		            			/*'<td rowspan="' + rowspan + '">' + escapeHtml(e.outlet.tradeChannel.name) + '</td>' +*/
		            			'<td rowspan="' + rowspan + '" class="border">' + escapeHtml(e.outlet.subTradeChannel.name) + '</td>');
		            	
		            	for(var j = 0; j < rowspan; j++) {
		            		if(j > 0) {
		            			searchResult += ('<tr' + ((i+1)%2 == 0 ? ' class="cellBg"' : '') + '>');
		            		}
		            		searchResult += (	
		            			'<td title=\"' + escapeHtml(e.productPackage[j].product.brand.name) + '\">' + escapeHtml(e.productPackage[j].product.prod.name) + '</td>' +
		            			'<td>'+ escapeHtml(e.productPackage[j].product.flavor.name) + '</td>' +
		            			'<td' + (e.productPackage[j].bppName ? ' title=\"' + escapeHtml(e.productPackage[j].bppName) + '\"' : '') + '>' + escapeHtml(e.productPackage[j].package.primaryContainer.name) + '</td>' +
		            			'<td>' + escapeHtml(e.productPackage[j].package.secondaryPackage.name) + '</td>' + 
		            			'</tr>');
		            	}	
		            	var markerInfo = new Object();
		            	markerInfo.linkRedirect = href;
		            	markerInfo.lat = e.outlet.address.latitude;
		            	markerInfo.long = e.outlet.address.longitude;
		            	markerInfo.outletName = outletName;
		            	markerInfo.outletLetter = outletLetter;
		            	markers[i] = markerInfo;		            	
		            }
		        	
		            if(searchResult == "") {
		            	searchResult = '<tr><td colspan="10" align="center">&nbsp;</td></tr>';
		            } 
		            $('#searchResult').remove();
		            var tbodyHTML = '<tbody id="searchResult">' + searchResult + '</tbody>';
		            var $searchResultTable = $("#resultTable");
		            $searchResultTable.append(tbodyHTML);
		            
		            recalculatePagination(Math.min(recordsCount, maxOutletRecordsCount), Json.pageNumber);
		        	
		            if(markers.length > 0) {
		            	redrawMap(markers);
		            } else {
		        		var latlng = new google.maps.LatLng(cur_lat, cur_long); 		
		        		drawMap(latlng, DEFAULT_ZOOM);
		            }
	        	}
	        	finally {
	        		unLockPage();
	        	}
	        },
	        error: function (e, xhr, errorThrown) {
	        	var $searchResultTable = $("#searchResult");
	        	$searchResultTable.html("<tr><td colspan='10'>Exception load data: "+getErrorDetails(e, xhr, errorThrown) + "</td></tr>");
	            unLockPage();
	            processAjaxJsonError(e, xhr);
	        }
	    });
}

function recalculatePagination(recordsCount, pageNumber) {
	var itemPerPage = $("#itemPerPage").val();
	if(itemPerPage > 0) {
    	var countPage = Math.floor(recordsCount / itemPerPage);
    	if(recordsCount == 0
    			|| recordsCount%itemPerPage != 0) {
    		countPage++;
    	}
    	document.getElementById("countPage").innerHTML = countPage;
    	var numPageSelect = document.locatorForm.numPage;
    	for (var i = numPageSelect.length - 1; i >= 0; i--) {
    		numPageSelect.remove(i);
    	}
    	for(var i = 1; i <= countPage; i++) {
    		var elOptNew = document.createElement('option');
    	    elOptNew.text = i;
    	    elOptNew.value = i;
    	    numPageSelect.add(elOptNew, numPageSelect.options[i - 1]); 
    	}
    	$("#recordsCount").val(recordsCount);
    	$("#numPage").val(pageNumber);
	}
}

function geocode(isSwitchTab) {
	var requestAddress;
	if(!isAdvancedSearch) {
		requestAddress = document.locatorForm.searchTextField.value;
	} else {
		requestAddress = getAdvancedAddress();
	}
	if(requestAddress != '') {
		geocoder.geocode( { 'address': requestAddress}, function(results, status) {				
			if (status == google.maps.GeocoderStatus.OK) {
				fillResultTextField(results[0]);
			} else if (status == google.maps.GeocoderStatus.OVER_QUERY_LIMIT) {				
				setTimeout(function() { geocode(); }, (timeout * 3));			
			} else {
				displayError('noResultsError');
				if(isSwitchTab) {
					document.locatorForm.address.value = requestAddress;
					$("#city").val('');
					$("#state").val(NOT_SELECTED_VALUE);
					$("#country").val(NOT_SELECTED_VALUE);
					$("#zip").val('');
				}
			}		
		});
	}
}
		
function fillResultTextField(place) {			
	var location = place.geometry.location;
	document.locatorForm.longitude.value = location.lng();
	document.locatorForm.latitude.value = location.lat();
	document.locatorForm.frmt_addr.value = place.formatted_address;
	if(!isAdvancedSearch) {
		var address_components = place.address_components;
		var street_number = ''; 
		var route = ''; 
		var city = '';
		var state = NOT_SELECTED_VALUE;
		var country = NOT_SELECTED_VALUE;
		var zip = '';
		for(var i = 0; i < address_components.length; i++) {
			switch(address_components[i].types[0]) {
				case "street_number": street_number = address_components[i].long_name; break;
				case "route": route = address_components[i].long_name; break;
				case "locality": city = address_components[i].long_name; break;
				case "administrative_area_level_1": state = address_components[i].short_name; break;
				case "postal_code": zip = address_components[i].long_name; break;
				case "country": country = address_components[i].short_name; break;
			}
		} 
		$("#address").val((!isEmpty(street_number) ? street_number + ' ' : '') + route);
		$("#city").val(city);
		$("#state").val(state);
		$("#country").val(country);
		$("#zip").val(zip);
		if(state != '') {
			changeState();
		} else if (country != '') {
			getChildsListValue('state', 'country');
		}
	}
	if(searchBtnClicked) 
		submitSearchForm();
	changed = false;
}

var us_territories = new Array("AS", "FM", "GU", "MH", "MP", "PR", "VI");

function getAdvancedAddress() {
	var advancedAddress =  getInputValueWithSpace(document.locatorForm.address) + 
						   getInputValueWithSpace(document.locatorForm.city) +
						   getSelectedNameValueWithSpace(document.locatorForm.state) +
						   getInputValueWithSpace(document.locatorForm.zip);
	var isState = true;
	var stateCode = document.locatorForm.state.value;
	for(var i = 0; i < us_territories.length; i++) {
		if(us_territories[i] == stateCode) {
			isState = false;
			break;
		}
	}
	if(isState) {
		advancedAddress += getSelectedNameValueWithSpace(document.locatorForm.country);
	}
	return $.trim(advancedAddress);
}

/* Refresh child select options */
function getChildsListValue(childId, parentId, parent_parentId, parent_parent_parentId, childValue, relatedFunc) {
	var $select = $("select#"+childId);
	var select_old_val = childValue ? childValue : $select.val();
	var responseJsonDataURL = childId + "/";
	if(parent_parent_parentId
			&& parent_parent_parentId != '') {
		responseJsonDataURL += ($("select#"+parent_parent_parentId).val() + "/");
	}
	if(parent_parentId
			&& parent_parentId != '') {
		responseJsonDataURL += ($("select#"+parent_parentId).val() + "/");
	}
	responseJsonDataURL += $("select#"+parentId).val();
	if(childId == 'tradeChannel'
		|| childId == 'subTradeChannel') {
		responseJsonDataURL += ("/" + document.locatorForm.includeFoodService.checked);
	}
	responseJsonDataURL = responseJsonDataURL.replace(".", "strange");
    $.ajax({
        url: responseJsonDataURL,
        type: "POST",
        dataType: "JSON",
        headers: csrfAjaxHeader,
        success: function (Json) {
            var options = "<option value='" + NOT_SELECTED_VALUE + "' title='" + (childId == 'state' ? '' : 'All') + "'>" + 
            			  (childId == 'state' ? '' : 'All') + "</option>";
            $.each(Json, function (i, e) {
            	options += "<option value='" + e.code + "' title='" + e.name + "'>" + e.name + "</option>";
            });
            $select.html(options);
            $select.val(select_old_val);
        	$select.attr('title', $('#' + childId + ' :selected').text());
        	if (typeof relatedFunc === 'function') {
        		relatedFunc();
        	}
        },
        error: function (e, xhr, errorThrown) {
        	$select.html("<option value='" + NOT_SELECTED_VALUE + "'>Exception<!-- "+getErrorDetails(e, xhr, errorThrown) + " --></option>");
            processAjaxJsonError(e, xhr);
        }
    });
}

/* Generage request url for search locations s*/
function getSearchRequestUrl(lat, long) {
	var distance;
	var distanceUnit;
	var includeFoodService = document.locatorForm.includeFoodService.checked;
	var beverageCategories = new Array();
	var productTypes = new Array();
	var beverageBrands = new Array();
	var beverageFlavors = new Array();
	var products = new Array();
	var physicalStates = new Array();
	var tradeChannels = new Array();
	var subTradeChannels = new Array();
//	var primaryContainers = new Array();
//	var secondaryPackages = new Array();
	var shortPrimaryContainers = new Array();
	var shortSecondaryPackages = new Array();
	var businessTypes = new Array();
	var outletNames = new Array();
	if(isAdvancedSearch) {
		distance = $.trim($("#distance").val());
		distanceUnit = $("input:radio[name='distanceUnitAdvanced']:checked").val();	
		if($("#beverageCategory").val() != NOT_SELECTED_VALUE)
			beverageCategories.push($("#beverageCategory").val());
		if($("#productPackageType").val() != NOT_SELECTED_VALUE)
			productTypes.push($("#productPackageType").val());
		if($("#beverageBrand").val() != NOT_SELECTED_VALUE)
			beverageBrands.push($("#beverageBrand").val());
		if($("#beverageFlavor").val() != NOT_SELECTED_VALUE)
			beverageFlavors.push($("#beverageFlavor").val());
		if($("#product").val() != NOT_SELECTED_VALUE)
			products.push($("#product").val());
//		if($("#primaryContainer").val() != NOT_SELECTED_VALUE)
//			primaryContainers.push($("#primaryContainer").val());
//		if($("#secondaryPackage").val() != NOT_SELECTED_VALUE)
//			secondaryPackages.push($("#secondaryPackage").val());
		if($("#shortPrimaryContainer").val() != NOT_SELECTED_VALUE)
			shortPrimaryContainers.push($("#shortPrimaryContainer").val());
		if($("#shortSecondaryPackage").val() != NOT_SELECTED_VALUE)
			shortSecondaryPackages.push($("#shortSecondaryPackage").val());
		if($("#businessType").val() != NOT_SELECTED_VALUE)
			businessTypes.push($("#businessType").val());
		if($("#physicalState").val() != NOT_SELECTED_VALUE)
			physicalStates.push($("#physicalState").val());
		if($("#tradeChannel").val() != NOT_SELECTED_VALUE)
			tradeChannels.push($("#tradeChannel").val());
		if($("#subTradeChannel").val() != NOT_SELECTED_VALUE)
			subTradeChannels.push($("#subTradeChannel").val());
		if($("#outletName").val() != '')
			outletNames.push($("#outletName").val());
	} else {
		distance = $("#distanceK").val();	
		distanceUnit = $("input:radio[name='distanceUnitKeyword']:checked").val();
		var elements = split($("input#prodInfo").val());
		var isDictionaryElem;
		var element;
		for(var i = 0; i < elements.length; i++) {
			element = $.trim(elements[i]);
			if(element != '') {
				element = element.toLowerCase();
				isDictionaryElem = false;
				for(var j = 0; j < dictionaries.length; j++) {
					if(dictionaries[j] && dictionaries[j].name && element == dictionaries[j].name.toLowerCase()) {
						var code = dictionaries[j].code;
						switch(dictionaries[j].dictionary) {
							case 'BEV_CAT': beverageCategories.push(code); isDictionaryElem = true; break;
							case 'BEV_BRAND': beverageBrands.push(code); isDictionaryElem = true; break;
							case 'BEV_FLAVOR': beverageFlavors.push(code); isDictionaryElem = true; break;
							case 'PROD': products.push(code); isDictionaryElem = true; break;
							case 'CHANNEL': tradeChannels.push(code); isDictionaryElem = true; break;
							case 'SUB_CHANNEL': subTradeChannels.push(code); isDictionaryElem = true; break;
//							case 'PR_CONT': primaryContainers.push(code); isDictionaryElem = true; break;
//							case 'SEC_PKG': secondaryPackages.push(code); isDictionaryElem = true; break;
							case 'CONT_TYPE': shortPrimaryContainers.push(code); isDictionaryElem = true; break;
							case 'PACK_SIZE': shortSecondaryPackages.push(code); isDictionaryElem = true; break;
							case 'PR_PKG_TYPE': productTypes.push(code); isDictionaryElem = true; break;
							case 'BUS_TYPE': businessTypes.push(code); isDictionaryElem = true; break;
							case 'PHYS_STATE': physicalStates.push(code); isDictionaryElem = true; break;
						}
						break;
					}
				}
				if(!isDictionaryElem) {
					outletNames.push(elements[i]);
				}
			}
		}
	}
	lastSearchURLWthtPgn = "search/" + 
						  lat + "/" + long + "/" + distance + "/" + distanceUnit + "/" +
						  getUrlSelectValues(beverageCategories) + "/" +
						  getUrlSelectValues(productTypes) + "/" +
						  getUrlSelectValues(beverageBrands) + "/" +
						  getUrlSelectValues(beverageFlavors) + "/" +
						  getUrlSelectValues(products) + "/" +
//						  getUrlSelectValues(primaryContainers) + "/" + 
//						  getUrlSelectValues(secondaryPackages) + "/" + 
						  getUrlSelectValues(shortPrimaryContainers) + "/" + 
						  getUrlSelectValues(shortSecondaryPackages) + "/" + 
						  getUrlSelectValues(businessTypes) + "/" +
						  getUrlSelectValues(physicalStates) + "/" +
						  getUrlSelectValues(tradeChannels)+"/" +
						  getUrlSelectValues(subTradeChannels) + "/" +
						  getUrlSelectValues(outletNames) + "/";
	var default_pagination = "/1/" + encodeURIComponent($("#itemPerPage").val());
	return (lastSearchURLWthtPgn + includeFoodService + "/" + getKosherIndicator() + default_pagination + getSorting());
}

function getUrlSelectValues(data) {
	if(typeof data == 'undefined' || data.length == 0) {
		return '*';
	} else {
		return encodeURIComponent(data.join(",").replace(/\./g, '%2E').replace(/\\/g, '%5C').replace(/\//g, '%2F').replace(/\;/g,'%3B'));
	} 
}

/* Logarithm with any basis */
Math.log = (function() {
	  var log = Math.log;
	  return function(n, base) {
	    return log(n)/(base ? log(base) : 1);
	  };
})();

var MAX_ZOOM = 16;
var DEFAULT_ZOOM = 14;

function drawMap(latlng, zoom, bounds) {
    var myOptions = {
    	zoom: zoom,
    	center: latlng,
    	mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    		
    map = new google.maps.Map(document.getElementById("map_canvas"),
    	myOptions);	
    if(bounds) {
    	map.fitBounds(bounds);
    }
	if(map.zoom > MAX_ZOOM) {
		map.setZoom(MAX_ZOOM);
	}
}


/* redraw map: 
 *  - calculate center of map
 *  - calculate correct zoom for map 
 *  - draw markers for outlet
 */
function redrawMap(markers) {	
	if(markers.length > 0) {		
		/* TODO: consider our location for find center of map */
		/*var minLat = parseFloat(document.locatorForm.latitude.value);
		var maxLat = parseFloat(document.locatorForm.latitude.value);
		var minLong = parseFloat(document.locatorForm.longitude.value);
		var maxLong = parseFloat(document.locatorForm.longitude.value);*/
		var bounds = new google.maps.LatLngBounds();
		var minLat = markers[0].lat;
		var maxLat = markers[0].lat;
		var minLong = markers[0].long;
		var maxLong = markers[0].long;		
		for(var i = 0; i < markers.length; i++) {
			if(minLat > markers[i].lat) {
				minLat = markers[i].lat;
			} 
			if(maxLat < markers[i].lat) {
				maxLat = markers[i].lat;
			} 
			if(minLong > markers[i].long) {
				minLong = markers[i].long;
			} 
			if(maxLong < markers[i].long) {
				maxLong = markers[i].long;
			} 
			bounds.extend(new google.maps.LatLng(markers[i].lat, markers[i].long));
		}
		var mapLat = (maxLat + minLat)/2;
		var mapLong = (maxLong + minLong)/2; 
		
		var zoom = MAX_ZOOM;
		var latlng = new google.maps.LatLng(mapLat, mapLong); 		
		drawMap(latlng, zoom, bounds);
		for(var i = 0; i < markers.length; i++) {
			addMarker(markers[i]);
		}
	} else {
		initialize_map();
	}
}

function addMarker(markerInfo){
	var g_markers = new google.maps.Marker({
	     position: new google.maps.LatLng(markerInfo.lat, markerInfo.long),
	     map: map,
	     title: markerInfo.outletName,
	     clickable: true,
	     icon: 'resources/images/' + markerInfo.outletLetter.toLowerCase() + '.png'
		 
	});
	google.maps.event.addListener(g_markers, 'click', function() {
		window.open(markerInfo.linkRedirect);
		
	});
}

function getOutletLetter(digit) {
	if(digit >= 0) {
		switch(digit + 1) {
			case 1: return 'A';
			case 2: return 'B';
			case 3: return 'C';
			case 4: return 'D';
			case 5: return 'E';
			case 6: return 'F';
			case 7: return 'G';
			case 8: return 'H';
			case 9: return 'I';
			case 10: return 'J';
			case 11: return 'K';
			case 12: return 'L';
			case 13: return 'M';
			case 14: return 'N';
			case 15: return 'O';
			case 16: return 'P';
			case 17: return 'Q';
			case 18: return 'R';
			case 19: return 'S';
			case 20: return 'T';
			case 21: return 'U';
			case 22: return 'V';
			case 23: return 'W';
			case 24: return 'X';
			case 25: return 'Y';
			case 26: return 'Z';
			default: return 'A';
		}
	}
}

/* clear functions */
function clearAll() {
	lastSearchURLWthtPgn = null;
	var $searchResult = $("#searchResult");
	$searchResult.html("<tr><td colspan='10' align='center'>&nbsp;</td></tr>");
	clearError();
	clearAdvancedSearch(false);
	clearKeywordSearch();
	clearCheckBox("includeFoodService");
	recalculatePagination(0, 1);
	initialize_map();
}

function clearAdvancedSearch(isSwitch) {
	clearSelectValue("beverageCategory");
	clearSelectValue("productPackageType");
	clearSelectValue("beverageBrand");
	clearSelectValue("beverageFlavor");
	clearSelectValue("product");
//	clearSelectValue("primaryContainer");
//	clearSelectValue("secondaryPackage");
	clearSelectValue("shortPrimaryContainer");
	clearSelectValue("shortSecondaryPackage");	
	clearSelectValue("businessType");
	clearSelectValue("physicalState");
	clearSelectValue("tradeChannel");
	clearSelectValue("subTradeChannel");
	clearInputValue("outletName");
	if(!isSwitch) {
		clearInputValue("address");
		clearInputValue("city");
		clearSelectValue("state");
		clearSelectValue("country");
		clearInputValue("zip");
	}
	$("input:radio[name='distanceUnitAdvanced'][value='mi']").click();
	$("#distance").val(DEFAULT_DISTANCE);
	clearCheckBox("kosherProduct");
	document.locatorForm.country.disabled = false;
}

function clearKeywordSearch() {
	$("input#prodInfo").val('');
	$("input#searchTextField").val('');
	$("input:radio[name='distanceUnitKeyword'][value='mi']").click();
	$("#distanceK").val(DEFAULT_DISTANCE);
	returnAllDictionaries();
}

function clearSelectValue(selectId) {
	var sel = document.getElementById(selectId);
	var oldValue = sel.value; 
	sel.value = NOT_SELECTED_VALUE;
	if (oldValue != NOT_SELECTED_VALUE) {
		setTimeout(function() {
			var sel = document.getElementById(selectId);
			if (sel != null && sel != 'undefined') {
				try {
					if (sel.onclick) { sel.onclick(); }
					if (sel.onchange) { sel.onchange(); }
				}
				catch (err) {
					;
				}
			}
		}, 100);
	}
}

function clearInputValue(inputId) {
	var inp = document.getElementById(inputId);
	var oldValue = inp.value;
	inp.value = '';
	if (oldValue != null && oldValue != '') {
		setTimeout(function() {
			var inp = document.getElementById(inputId);
			if (inp != null && inp != 'undefined' && inp.onchange) {
				try {
					inp.onchange();
				}
				catch (err) {
					;
				}
			}
		}, 100);
	}
}

function clearCheckBox(inputId) {
	$('input[id='+ inputId +']').removeAttr('checked');
}

/* Pagination control */
function nextPage() {
	if(lastSearchURLWthtPgn) {
		var pageNumber = $("#numPage").val();
		var countPage = document.getElementById("countPage").innerHTML;
		if(pageNumber < countPage) {
			goToPage(++pageNumber);
		}
	}
}

function prevPage() {
	if(lastSearchURLWthtPgn) {
		var pageNumber = $("#numPage").val();
		if(pageNumber > 1) {
			goToPage(--pageNumber);
		}
	}
}

function goToPage(pageNumber) {
	if(lastSearchURLWthtPgn) {
		var searchRequestUrl = lastSearchURLWthtPgn + getIncludeFoodServiceIndicator() + "/" + 
		  					   getKosherIndicator() + getPagintionRequestUrl(pageNumber) + getSorting();
		ajaxSearchRequest(searchRequestUrl);
	}
}

function changeItemPerPage() {
	if(lastSearchURLWthtPgn) {
		var searchRequestUrl = lastSearchURLWthtPgn + getIncludeFoodServiceIndicator() + "/" + 
		  					   getKosherIndicator() + getCurrentPagination() + getSorting();
		ajaxSearchRequest(searchRequestUrl);
	}
}

function getPagintionRequestUrl(pageNumber) {
	var itemPerPage = $("#itemPerPage").val();
	var recordsCount = $("#recordsCount").val();
	if(recordsCount > 0 && (itemPerPage * (pageNumber - 1) + 1) > recordsCount) {
		pageNumber = Math.floor(recordsCount/itemPerPage);
		if(recordsCount%itemPerPage != 0) {
			pageNumber++;
		}
	}
	return "/" + pageNumber + "/" + itemPerPage;
}

function getCurrentPagination() {
	return getPagintionRequestUrl($("#numPage").val());
}

/* Print search result */
function printSearchResult() {
	window.print(); 
}

/* change country and disabled if select state */
function changeState() {
	var stateCode = $("#state").val();
	var $selectCountry = $('#country');
	if(stateCode != NOT_SELECTED_VALUE) {
		document.locatorForm.country.disabled = true;
		$.ajax({
			url: 'country/' + stateCode,
		    type: "POST",
		    dataType: "JSON",
		    headers: csrfAjaxHeader,
		    success: function (country) {
		    	if(country) {
		         	$selectCountry.val(country.code);
		        }
		    	if(!$selectCountry.val()) {
		    		$selectCountry.val(NOT_SELECTED_VALUE);
		    	}
		    	$selectCountry.attr('title', $('#country :selected').text());
		    },
		    error: function (e, xhr, errorThrown) {
		      	$selectCountry.html("<option value='" + NOT_SELECTED_VALUE + "'>Exception<!-- "+getErrorDetails(e, xhr, errorThrown) + " --></option>");
		       	processAjaxJsonError(e, xhr);
		    }
		});
	} else {
		document.locatorForm.country.disabled = false;
	}
	if(!$selectCountry.val()) {
		$selectCountry.val(NOT_SELECTED_VALUE);
	}
	$selectCountry.attr('title', $('#country :selected').text());
	$("#state").attr('title', $('#state :selected').text());
}

/* Logic Include Food Service Indicator */
function changeIncludeFoodService(isSwitch, tradeChannelCode, subTradeChannelCode) {
	var includeFoodService = document.locatorForm.includeFoodService.checked;
	if(isAdvancedSearch || isSwitch) { 
		var $tradeChannel = $("#tradeChannel");
		var select_old_val = tradeChannelCode ? tradeChannelCode : $tradeChannel.val();
		var responseJsonDataURL = 'tradeChannel/' + includeFoodService;
		$.ajax({
	        url: responseJsonDataURL,
	        type: "POST",
	        dataType: "JSON",
	        headers: csrfAjaxHeader,
	        success: function (Json) {
	        	var options = "<option value='" + NOT_SELECTED_VALUE + "' title='All'>All</option>";
	            $.each(Json, function (i, e) {
	            	options += "<option value='" + e.code + "' title='" + e.name + "'>" + e.name + "</option>";
	            });
	            $tradeChannel.html(options);
	            $tradeChannel.val(select_old_val);
	            $tradeChannel.attr('title', $('#tradeChannel :selected').text());
	            
	    		getChildsListValue('subTradeChannel', 'tradeChannel', '', '', subTradeChannelCode);
	        },
	        error: function (e, xhr, errorThrown) {
	        	$tradeChannel.html("<option value='" + NOT_SELECTED_VALUE + "'>Exception<!-- "+getErrorDetails(e, xhr, errorThrown) + " --></option>");
	        	processAjaxJsonError(e, xhr);
	        }
	    });
	}
	if(isAdvancedSearch) {
		lastIncludeFoodServiceLocation = document.locatorForm.includeFoodService.checked;
	}
	if(lastSearchURLWthtPgn && !isSwitch) {
		clickSearchBtn();
//		var searchRequesUrl = lastSearchURLWthtPgn + includeFoodService + "/" + 
//							  getKosherIndicator() + getCurrentPagination() + getSorting();
//		ajaxSearchRequest(searchRequesUrl);
	}
}

function getIncludeFoodServiceIndicator() {
	return document.locatorForm.includeFoodService.checked; 
}

/* Logic Kosher Product Only Indicator */
function changeKosherIndicator() {
	if(lastSearchURLWthtPgn) {
		clickSearchBtn();
//		var searchRequesUrl = lastSearchURLWthtPgn + getIncludeFoodServiceIndicator() + "/" +
//							  getKosherIndicator() + getCurrentPagination() + getSorting();
//		ajaxSearchRequest(searchRequesUrl);
	}
}

function getKosherIndicator() {
	return isAdvancedSearch ? document.locatorForm.kosherProduct.checked : false; 
}

//function for cliboarding current result page
function clipbordPage() {
	var el = document.getElementById('resultTable');
	el.focus();
    var body = document.body;
    
    if (document.createRange && window.getSelection) {
    	var range = document.createRange();
    	range.selectNodeContents(el);
        range.selectNode(el);
        var sel = window.getSelection();
        sel.removeAllRanges();
        sel.addRange(range);
        if (sel.execCommand) {
        	sel.execCommand('copy');
        }
        else {
        	document.execCommand('copy');
        }
    }
    else
    if (body.createTextRange) {
    	var range = body.createTextRange();
       	range.moveToElementText(el);
        range.select();
        range.execCommand('copy');
    } 
    if (document.selection) {
    	document.selection.empty();
    }
    else {
    	document.getSelection().empty();
    }
}

function sortResult(curSortColumn) {
	if(sortColumn == curSortColumn) {
		sortOrder = sortOrder == ASCENDING ? DESCENDING : ASCENDING;
	} else {
		sortOrder = ASCENDING;
		$("#"+sortIdMap[sortColumn]).parent().removeClass("sort_desc");
		$("#"+sortIdMap[sortColumn]).parent().removeClass("sort_asc");
	}		
	sortColumn = curSortColumn;
	var sortImage = $("#"+sortIdMap[sortColumn]).parent();
	if(sortOrder == ASCENDING) {
		sortImage.removeClass("sort_desc");
		sortImage.addClass("sort_asc");
	} else {
		sortImage.addClass("sort_desc");
		sortImage.removeClass("sort_asc");
	}
	if($("#recordsCount").val() > 0
			&& lastSearchURLWthtPgn) {
		var searchRequesUrl = lastSearchURLWthtPgn + getIncludeFoodServiceIndicator() + "/" +
		  					  getKosherIndicator() + getCurrentPagination() + getSorting();
		ajaxSearchRequest(searchRequesUrl);
	}
}

function getSorting() {
	return "/" + (sortColumn == '' ? '*'  : encodeURIComponent(sortColumn)) + "/" + (sortOrder == '' ? '*'  : encodeURIComponent(sortOrder));
}

function lockPage() {
//	document.locatorForm.searchBtn.disabled = true;
	$('#searchBtn').removeAttr('href');
	$('#progressbar').css("display","block");
	$('#itemPerPage').attr("disabled","disabled");
	$('#numPage').attr("disabled","disabled");
	$('#outletNameHeader').removeAttr('href');
	$('#distanceHeader').removeAttr('href');
//	$('#tradeChannelHeader').removeAttr('href');
	$('#subTradeChannelHeader').removeAttr('href');
}

function unLockPage() {
//	document.locatorForm.searchBtn.disabled = false;
	$('#searchBtn').attr('href', "");
	$('#progressbar').css("display","none");
	$('#itemPerPage').removeAttr("disabled");
	$('#numPage').removeAttr("disabled");
	$('#outletNameHeader').attr('href', "javascript:sortResult('" + sortOutletName + "');");
	$('#distanceHeader').attr('href', "javascript:sortResult('" + sortDistance + "');");
//	$('#tradeChannelHeader').attr('href', "javascript:sortResult('" + sortTradeChannel + "');");
	$('#subTradeChannelHeader').attr('href', "javascript:sortResult('" + sortTradeSubChannel + "');");
}

function refreshShortSecondaryPackage() {
	getChildsListValue('shortSecondaryPackage', 'shortPrimaryContainer', 'product', 'beverageBrand');
}

function refreshShortPrimaryContainer() {
	getChildsListValue('shortPrimaryContainer', 'product', 'beverageBrand',	null, null, refreshShortSecondaryPackage);
}

function refreshProduct() {
	getChildsListValue('product', 'beverageFlavor', 'beverageBrand', 'beverageCategory', null, refreshShortPrimaryContainer);
}

function refreshFlavor() {
	getChildsListValue('beverageFlavor', 'beverageBrand', 'beverageCategory', null, null, refreshProduct);
}

function refreshBrand() {
	getChildsListValue('beverageBrand', 'beverageCategory', null, null, null, refreshFlavor);
}
