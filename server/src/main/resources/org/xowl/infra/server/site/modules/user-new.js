// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();

function init() {
	doSetupPage(xowl, true, [{name: "New User"}], function() {});
}

function onCreateUser() {
	var name = document.getElementById("field-name").value;
	var password1 = document.getElementById("field-password1").value;
	var password2 = document.getElementById("field-password2").value;
	if (name === null || name === "" || password1 === null || password1 === "" || password2 === null || password2 === "")
		return;
	if (password1 !== password2) {
		displayMessage("error", "Passwords do not match!");
		return;
	}
	if (!onOperationRequest("Creating user " + name + " ..."))
		return;
	xowl.createUser(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Created user " + name + ".");
			waitAndGo("user.html?id=" + encodeURIComponent(name));
		}
	}, name, password1);
}