// Copyright (c) 2015 Laurent Wouters
// Provided under LGPL v3

var xowl = new XOWL();
var FLAG = false;

function init() {
	if (!xowl.isLoggedIn()) {
		document.location.href = "../index.html";
		return;
	}
	document.getElementById("loader").style.display = "none";
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