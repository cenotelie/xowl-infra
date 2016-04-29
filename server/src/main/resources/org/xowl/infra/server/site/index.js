// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var SENT = false;

function init() {
	displayMessage(null);
}

function onLoginButton() {
	if (SENT)
		return false;
	SENT = true;
	var login = document.getElementById("field-login").value;
	var password = document.getElementById("field-password").value;
	if (login === null || login === "" || password === null || password === "")
		return;
	displayMessage("Trying to login ...");
	xowl.login(function (code, type, content) {
		SENT = false;
		if (code === 200) {
			window.location.href = "modules/main.html";
		} else {
			displayMessage(getErrorFor(type, content));
		}
	}, login, password);
	return false;
}