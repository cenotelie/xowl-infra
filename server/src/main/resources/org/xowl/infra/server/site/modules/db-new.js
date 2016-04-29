// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var FLAG = false;

function init() {
	if (!xowl.isLoggedIn()) {
		document.location.href = "../index.html";
		return;
	}
	document.getElementById("btn-logout").innerHTML = "Logout (" + xowl.getUser() + ")";
	displayMessage(null);
}

function onButtonLogout() {
	xowl.logout();
	document.location.href = "../index.html";
}

function onCreateDB() {
	if (FLAG)
		return;
	var name = document.getElementById("field-name").value;
	if (name === null || name === "")
		return;
	FLAG = true;
	displayMessage("Creating database ...");
	xowl.createDatabase(function (code, type, content) {
		FLAG = false;
		if (code === 200) {
			window.location.href = "db.html?id=" + encodeURIComponent(name);
		} else {
			displayMessage(getErrorFor(type, content));
		}
	}, name);
}