$ (function() {    	
	$("div.keyword").css('display', 'block');
	init_components();
});


function init_components() { 
	var reportType = document.reportForm.reportType;
	var dateFrom = document.reportForm.dateFrom;
	var dateTo = document.reportForm.dateTo;
	if(reportType.addEventListener) {		
		reportType.addEventListener('change', changeField, true);
		dateFrom.addEventListener('select', changeField, true);
		dateTo.addEventListener('select', changeField, true);
	} else if (reportType.attachEvent){
		reportType.attachEvent('onchange', changeField, true);		
		dateFrom.attachEvent('onkeyup', changeField, true);
		dateFrom.attachEvent('onSelect', changeField, true);		
		dateTo.attachEvent('onkeyup', changeField, true);		
		dateTo.attachEvent('onSelect', changeField, true);
	} 
}

function changeField() {
	var runReport = document.reportForm.runReport;
	var reportType = document.reportForm.reportType;
	var dateFrom = document.reportForm.dateFrom;
	var dateTo = document.reportForm.dateTo;
	if(!isUnselected(reportType.value)&& !isEmpty(dateFrom.value)&& !isEmpty(dateTo.value) && validation()){		
		runReport.disabled = false;
	}else{
		runReport.disabled = true;
	}
}
var NOT_SELECTED_VALUE = "-1";
/* validation functions */
function validation() {
	
	var error = false;
	clearError();	    	    
	if(validationReportPage()) {
	    	error = true;
	}	
	return !error;
}

function validationReportPage() {	
	textfrom = $("input#dateFrom").val();
	textto = $("input#dateTo").val();
	if(isEmpty(textfrom)||isEmpty(textto)) {
		
		var runReport = document.reportForm.runReport;
		runReport.disabled = true;
		return false;
	}
	if(!validateDate(textfrom)|| !validateDate(textto)){
		displayError('dateValidError');
    	return true;
	}
	from = new Date(textfrom);
	to = new Date(textto);
	var difference = to - from;
	if(difference<0) {
		displayError('dateError');
    	return true;
	}
	return false;
}

function validateDate(date) {
	var arrDateParts = date.split("/");
	var patt_date = new RegExp("^((0[123456789]|(10|11|12))/(0[1-9]|[1-2][0-9]|3[0-1]))/[0-9]{4}$");
	if (patt_date.test(date) == false){return false;}

	
	
	month=arrDateParts[0];
	year=arrDateParts[2];
	day=arrDateParts[1];
	if (month == 2) {
        if (day == 29) {
            if (year % 4 != 0 || year % 100 == 0 && year % 400 != 0) {
            	return false;
            }
        }
        else if (day > 28) {
        	return false;
        }
    }
    else if (month == 4 || month == 6 || month  == 9 || month == 11) {
        if (day > 30) {
        	return false;
        }
    }
    else {
        if (day > 31) {
        	return false;
        }
    }
	return true;
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
	$("div.dateError").css("display", "none");	
	$("div.dateValidError").css("display", "none");	
}

/* clear functions */
function clearAll() {
	clearError();
	clearFilds();	
	var runReport = document.reportForm.runReport;
	runReport.disabled = true;
}


function clearFilds() {	
	clearSelectValue("reportType");
	clearInputValue("dateFrom");
	clearInputValue("dateTo");
}


function clearSelectValue(selectId) {
	var sel = document.getElementById(selectId);
	var oldValue = sel.value; 
	sel.value = NOT_SELECTED_VALUE;	
}

function clearInputValue(inputId) {
	var inp = document.getElementById(inputId);
	var oldValue = inp.value;
	inp.value = '';	
}


