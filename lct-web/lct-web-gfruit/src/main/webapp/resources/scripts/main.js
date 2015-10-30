var father = new (function($) {
	var self = this;
	var $curtain = $("<div class='curtain'></div>");
	
	this.ajaxWrapper = function(options) {
		var defOptAjax = {
			type : "GET",
			cache : false,
			error : function(xhr, ajaxOptions, thrownError) {
			}
		};
		var resOpt = new Object();
		$.extend(resOpt, defOptAjax, options, {
			success : function(result) {
				self.unLockPage();
				if (typeof options.success === "function")
					options.success(result);
			},
			error : function(error) {
				self.unLockPage();
				if (typeof options.error === "function")
					options.error(error);
			}
		});
		$.ajax(resOpt);
	};

	this.invokeAction = function(obj, event) {
		var action = obj.getAttribute("data-action");
		if (action) {
			return father[action](obj, event);
		}
	};

	this.collectParams = function(selector) {
		if (selector === undefined)
			return {};
		var res = {};
		$(selector).each(function() {
			var $this = $(this);
			var value = $.trim($this.val());
			if (value && value != "") {
				res[$this.attr("name")] = value;
			}
		});
		return res;
	};
	
	this.buildInputEvents = function(selector) {
		$(selector).each(function() {
			var hndlr = null;
			$(this).on("keyup paste",function(event) {
				var input = this;
				if(hndlr) clearTimeout(hndlr);
				hndlr = setTimeout(function() {
					father.invokeAction(input,event);
				}, 600);
			});
		});
	};
	
	this.buildProductDropDownMenu = function() {
		$("#product").multipleSelect({
            placeholder: "product",
            selectAllText: "select all",
            selectAllDelimiter: ['', ''],
            minumimCountSelected: 1
		});
	};

	this.search = function() {
		self.lockPage();
		if(father.validationInputs()) {
			googleObj.geoCode(self.searchSubmit);
		} else {
			self.unLockPage();
		}
	};
	
	this.clear = function() {
		$("#product").multipleSelect("uncheckAll");
		$("#searchTextField").val('');
		$("#distance").val(5);
	};

	this.searchSubmit = function() {
		var data = utils.generateSearchRequestData();
		self.ajaxWrapper({
			url : "search",
			datatype : 'json',
			data : data,
			type : "POST",
			success : function(Json) {
	        	try {
		        	var $searchResult = $('<div></div>');
		        	var markers = new Array();
		        	var locations = Json.location;
		        	var distanceUnit = Json.distanceUnitName;
		        	var maxOutletRecordsCount = 5;
		        	var recordsCount = Json.recordsCount;
		        	if(locations && locations.length > 0) {
		        		for(var i = 0; i < locations.length; i++) {
			        		var e = locations[i];
			        		var rowspan = e.productPackage.length;
			            	var outletAddress = e.outlet.address.formattedAddress;         			       
			            	var outletName = e.outlet.chainName;
			            	var outletNameUri = encodeURIComponent(outletName);
			            	var outletAddressUri = encodeURIComponent(outletAddress);
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
			            	var cur_lat = $("#latitude").val();
			            	var cur_long = $("#longitude").val();
			            	if (cur_lat && cur_long) {
			            		href+='&saddr=loc:'+cur_lat+','+cur_long;
			            	}
			            	
			            	var $tr = $("<tr " + ((i+1)%2 == 0 ? "class='cellBg'" : "") + "></tr>");
			            	//outlet name
			            	var $td = $("<td style='width:18%; padding-right: 5px;'></td>");
			            	var $a = $("<a href='' target='_blank'></a>");
			            	$a.attr("href", href);
			            	$a.text(outletName);
			            	$td.append($a);
			            	$td.attr("rowspan", rowspan);
			            	$tr.append($td);
			            	
			            	//outlet address
			            	$td = $("<td style='width:32%; padding-right: 19px'></td>");
			            	$td.text(outletAddress);
			            	$td.attr("rowspan", rowspan);
			            	$tr.append($td);
			            	
			            	//outlet distance
			            	$td = $("<td style='width:8%; padding-right: 5px'></td>");
			            	$td.text(e.distance + ' ' + distanceUnit);
			            	$td.attr("rowspan", rowspan);
			            	$tr.append($td);
			            	
			            	//product information
			            	$td = $("<td class='product_information' style='width:42%'></td>");
			            	var $pruducts = $("#product");
			            	for(var j = 0; j < rowspan; j++) {
			            		//not escaping text!
			            		var text = $pruducts.children("[value='" + e.productPackage[j].product.prod.code + "']").text().replace(" fruitwater®","").replace("glacéau ", "");
		            			text = "glacéau <label>fruit</label>water® " + text;
			            		if(j > 0) {
			            			var $tr2 = $("<tr " + ((i+1)%2 == 0 ? "class='cellBg'" : "") + "></tr>");
			            			var $td2 = $("<td class='product_information'></td>");
			            			$td2.html(text);
			            			$tr2.append($td2);
			            			$searchResult.append($tr2); 
			            		} else {
			            			$td.html(text);
			            			$tr.append($td);
			            			$searchResult.append($tr);
			            		}
			            	}
			            	var markerInfo = new Object();
			            	markerInfo.linkRedirect = href;
			            	markerInfo.lat = e.outlet.address.latitude;
			            	markerInfo.long = e.outlet.address.longitude;
			            	markerInfo.outletName = outletName;
			            	markerInfo.outletLetter = 'circle';
			            	markers[i] = markerInfo;		            	
			            }
		        		$("#searchResult").html($searchResult.html());
		        		$("#noResultsBLock").addClass("hidden");
		        		$("#resultBlock").removeClass("hidden");
		        		
		        		var headTable = $("#wrapperTableHead table.top_table"),
		        		scrollableTable = $("#scrollableWrapper table#scrollableTable"),
		        		tableCol;
		        		headTable.width(scrollableTable.width());
		        		headTable = headTable.find("tr:first th");
		        		scrollableTable = scrollableTable.find("tr:first td");
		        		for (tableCol = 0; tableCol < 3 && tableCol < scrollableTable.length; tableCol++) {
		        			$(headTable[tableCol]).width($(scrollableTable[tableCol]).width());
		        		}

		        	} else {
		        		$("#resultBlock").addClass("hidden");
		        		$("#noResultsBLock").removeClass("hidden");
		        	}
	        		if(recordsCount > 25) {
	        			$("#moreThan").removeClass("hidden");
	        		} else {
	        			$("#moreThan").addClass("hidden");
	        		}
		            if(markers.length > 0) {
		            	googleObj.addMarkers(markers);
		            } else {
		        		var options = {
		        			center: new google.maps.LatLng(data.latitude, data.longitude)	
		        		};
		        		googleObj.drawMap(options);
		            }
	        	}
	        	finally {
	        		var $obj = $("#wrapperTableHead");
        			if($("#scrollableWrapper").hasScrollBar()) {
        				$obj.addClass("top_table_wrapper");
        			} else {
        				$obj.removeClass("top_table_wrapper");
        			}
        			$obj.remove();
        			$obj.insertAfter("#appendWrapper");
        			utils.windowResize();
	        		self.unLockPage();
	        	}
			}
		});
	};

	this.fillResultTextField = function(place) {
		var location = place.geometry.location;
		$("#longitude").val(location.lng());
		$("#latitude").val(location.lat());
		$("#frmt_addr").val(place.formatted_address);
	};

	this.lockPage = function() {
		$("body").prepend($curtain);
		$("#wheel").removeClass("hidden");
	};

	this.unLockPage = function() {
		$curtain.remove();
		$("#wheel").addClass("hidden");
	};
	
	this.validationInputs = function() {
		var result = true;
		var distance = $("#distance").val();
		if(distance == "" || $.isNumeric(distance) == false) {
			$("#distance").addClass("field_error");
			$("#distance").focus();
			result = false;
		} else {
			$("#distance").removeClass("field_error");
		}
		if($("#searchTextField").val() == "") {
			$("#searchTextField").addClass("field_error");
			$("#searchTextField").focus();
			result = false;
		} else {
			$("#searchTextField").removeClass("field_error");
		}
		return result;
	};
	
	this.downloadNutritionalPdf = function(obj) {
		window.open(obj.getAttribute("data-link"), "download");
	};
	
})(jQuery);

var googleObj = new (function($) {
	var self = this;
	var MAX_ZOOM = 16;
	var map;
	var timeoutRequest = 600;
	var defOptions = {
		zoom : 14,
		center : new google.maps.LatLng(42.090240, -95.7128910),
		mapTypeId : google.maps.MapTypeId.ROADMAP
	};
	var geocoder;

	this.initialize = function() {
		geocoder = new google.maps.Geocoder();
		var input = $("#searchTextField").get(0);
		/* TODO: change bounds for USA and Canada*/
	    var p1 = new google.maps.LatLng(65.00, -82.00);
	    var p2 = new google.maps.LatLng(45.00, -139.00);
	    var defaultBounds = new google.maps.LatLngBounds(p1, p2);
		var options = {
		  bounds: defaultBounds,
		  componentRestrictions: { 'country': 'us' }
		};
		new google.maps.places.Autocomplete(input, options);
	};
	
	this.drawMap = function(options, bounds) {
		var obj = $.extend({}, defOptions, options);
		map = new google.maps.Map(document.getElementById("map_canvas"), $
				.extend({}, defOptions, options));
		if (bounds) {
			map.fitBounds(bounds);
		}
		if (map.zoom > self.MAX_ZOOM) {
			map.setZoom(self.MAX_ZOOM);
		}
	};

	this.geoCode = function(func) {
		var requestAddress
		requestAddress = $("#searchTextField").val();
		if (requestAddress != '') {
			geocoder.geocode({ 'address': requestAddress}, function(results, status) {				
				if (status == google.maps.GeocoderStatus.OK) {
					father.fillResultTextField(results[0]);
					if(typeof func === "function" ) {
						func();
					} else {
						father.unLockPage();
					}
				} else if (status == google.maps.GeocoderStatus.OVER_QUERY_LIMIT) {				
					setTimeout(function() { geocode(); }, (timeoutRequest * 3));			
				} else {
					//displayError('noResultsError');
					father.unLockPage();
				}		
			});
		} else {
			father.unLockPage();
		}
	};
	
	this.addMarkers = function(markers) {
		if(markers.length > 0) {		
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
			var options = {
				zoom: MAX_ZOOM,
				center: new google.maps.LatLng(mapLat, mapLong)
			};
			self.drawMap(options, bounds);
			for(var i = 0; i < markers.length; i++) {
				self.addMarker(markers[i]);
			}
		}
	};
	
	this.addMarker = function(markerInfo) {
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
	};
	
})(jQuery);

var utils = new (function($) {
	var self = this;
	var us_territories = new Array("AS", "FM", "GU", "MH", "MP", "PR", "VI");

	this.generateSearchRequestData = function() {
		var data = {
			longitude : $("#longitude").val(),
			latitude : $("#latitude").val(),
			distance : $.trim($("#distance").val()),
			flavorCode : self.correctSearchRequestValue(),
			productCode : self.correctSearchRequestValue(self.getMultipleSelectedCode("#product")),
			sortColumn : "DISTANCE",
			sortOrder : "ASC",
		};
		return data;
	};
	
	this.correctSearchRequestValue = function(value) {
		if(typeof value == 'undefined' || value.length == 0) { 
			return '*';
		} else {
			return value;
		}
	};

	this.getInputValue = function(input) {
		if ($(input).val != '')
			return $.trim($(input).val()) + ' ';
		return '';
	};
	
	this.getSelectedValue = function(selector) {
		if($(selector).val() != -1 && $(selector + " option:selected").index() >= 0) {
			return $(selector + " option:selected").text() + ' ';
		} 
		return '';
	};
	
	this.getSelectedCode = function(selector) {
		if($(selector).val() != -1 && $(selector + " option:selected").index() >= 0) {
			return $(selector).val();
		} else {
			return "*";
		}
	};
	
	this.getMultipleSelectedCode = function(selector) {
		var result = "*";
		var $el = $(selector).val();
		if($el != null && $el.length > 0) {
			if($el.length != $(selector + " option").size()) {
				result = "";
				for(var i=0; i < $el.length; i++) {
					result += $el[i] + ",";
				}
				if(result != "") {
					result = result.slice(0, -1);
				}
			}
		}
		return result;
	};
	
	this.windowResize = function() {
		var windowHeight = $(window).height();
		var allowedHeight = windowHeight - 666;
		if(allowedHeight < 100) {
			$("#scrollableWrapper").height(100);
		} else {
			$("#scrollableWrapper").height(allowedHeight);
		}
	};

})(jQuery);

// ////////////////////
// ///// #EVENTS //////
// ////////////////////
$(function() {

	$(document).on("click", "input[type=button]", function(event) {
		father.invokeAction(this, event);
	});
	
	father.buildProductDropDownMenu();
	father.buildInputEvents(".geoAddress");

	googleObj.initialize();
	
	$("#searchTextField, #distance").on("keyup", function(event) {
		if ( event.which == 13 ) {
			father.search();
		}
	});
	
	googleObj.drawMap({
		zoom : 3
	});
	
	if(Modernizr.cssanimations) {
		$("#bubbles").removeClass("hidden");
	}
	
	 $.fn.hasScrollBar = function() {
		 return this.get(0).scrollHeight > this.height();
	 };
	 
	 $(window).resize(utils.windowResize);
	 
});