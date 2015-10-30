function escapeHtml(val) {
	if (val == null || val == '' || val == 'undefined') {
		return val;
	}
	return val.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

function getErrorDetails(jqXHR, exception, errorThrown) {
	if (jqXHR.status === 0) {
        return 'Not connect.\n Verify Network.';
    } else if (jqXHR.status == 404) {
        return 'Requested page not found. [404]';
    } else if (jqXHR.status == 500) {
        return 'Internal Server Error [500].';
    } else if (exception === 'parsererror') {
        return 'Requested JSON parse failed.';
    } else if (exception === 'timeout') {
        return 'Time out error.';
    } else if (exception === 'abort') {
        return 'Ajax request aborted.';
    } else {
    	var retValue = 'Uncaught Error.\n', respText = jqXHR.responseText;
    	if (errorThrown) {
    		retValue += errorThrown;
    	}
    	if (jqXHR.status) {
    		retValue += " ["+jqXHR.status+"]";
    	}
    	if (respText) {
    		respText = respText.replace("</", "   </");
    		retValue += "\n<br/>" + $("<div/>").html(respText).text();
    	}
    	return retValue;
    }	
}

function encodeUrlParam(data) {
	if(typeof data == 'undefined' || data.length == 0 || data == '') {
		return data;
	} else {
		return encodeURIComponent(data.replace(/\./g, '%2E').replace(/\\/g, '%5C').replace(/\//g, '%2F').replace(/\;/g,'%3B'));
	} 
}


function checkBrowser() {
	var html = document.getElementsByTagName('html')[0];
	if (document.all && document.querySelector && !document.addEventListener) {
		html.setAttribute("class", "ie8");
	} else if (navigator.userAgent.match(/MSIE/) || !!navigator.userAgent.match(/Trident\//)) {
		if (navigator.userAgent.match(/MSIE 9\./)) {
			html.setAttribute("class", "all-ie ie9");
		}
		else {
			html.setAttribute("class", "all-ie");
		}
	}
}

function yesNoDialog(dialogText, dialogTitle, yesFunc) {
	$('<div></div>').appendTo('body')
    .html('<div><h3>'+dialogText+'</h3></div>')
    .dialog({
        modal: true,
        title: dialogTitle,
        zIndex: 10000,
        autoOpen: true,
        width: 500,
        minWidth: 300,
        resizable: true,
        buttons: [
                  {
                	  text: 'Yes',
                	  click: function () {
                		  yesFunc();
                		  $(this).dialog("close");
                	  }
                  },
                  {
                	  text: 'No',
                	  click: function () {
                		  $(this).dialog("close");
                	  }
                  }
                  ],
                  close: function (event, ui) {
                	  $(this).remove();
                  }
    });
}
