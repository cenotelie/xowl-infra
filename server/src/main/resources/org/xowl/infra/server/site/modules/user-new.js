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

function onCreateUser() {
	if (FLAG)
		return;
	var name = document.getElementById("field-name").value;
	var password1 = document.getElementById("field-password1").value;
	var password2 = document.getElementById("field-password2").value;
	if (name === null || name === "" || password1 === null || password1 === "" || password2 === null || password2 === "")
		return;
	if (password1 !== password2) {
		displayMessage("Passwords do not match!");
		return;
	}
	FLAG = true;
	displayMessage("Creating user ...");
	xowl.createUser(function (code, type, content) {
		FLAG = false;
		if (code === 200) {
			window.location.href = "user.html?id=" + encodeURIComponent(name);
		} else {
			displayMessage(getErrorFor(type, content));
		}
	}, name, password1);
}