
function writeErrorMessage(errorMessage) {
	var errorsDiv = $("#errors");
	errorsDiv.empty();
	errorsDiv.append(errorMessage);
}

function editUser(btn, userName) {
	if(validString(userName)) {
		var form = document.forms['userForm'];
		form.elements['operation'].value = "edit";
		form.elements['userName'].value = userName;
		form.submit();
	}
}

function createUser() {
	var form = document.forms['userForm'];
	form.elements['operation'].value = "create";
	form.submit();
}

function removeUser(btn, userName) {
	if(validString(userName)) {
		yesNoDialog("Are you sure?", "Remove User "+userName, function() {
			var requestUrl = "user";
			$.ajax({
				url: requestUrl,
				type: "POST",
				data: {operation: "remove",
					   userName: userName},
				dataType: "JSON",
				headers: csrfAjaxHeader,
				success : function(Json) {
					try {
						if (Json == true) {
							$(btn).parents("tr").remove();
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
		});
	}
}

function validString(str) {
	return str && str != '' && str != 'undefined';
}
