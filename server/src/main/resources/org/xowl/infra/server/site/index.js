// Copyright (c) 2015 Laurent Wouters
// Provided under LGPL v3

var xowl = new XOWL();

function init() {
	displayMessage(null);
}

function onLoginButton() {
	var login = document.getElementById("field-login").value;
	var password = document.getElementById("field-password").value;
	if (login === null || login === "" || password === null || password === "")
		return;
	displayMessage("Trying to login ...");
	xowl.login(function (code, type, content) {
		if (code === 200) {
			window.location.href = "modules/main.html";
		} else {
			displayMessage(getErrorFor(type, content));
		}
	}, login, password);
}