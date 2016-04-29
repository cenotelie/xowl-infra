// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");
var rule = getParameterByName("rule");

function init() {
	if (!xowl.isLoggedIn()) {
		document.location.href = "../index.html";
		return;
	}
	if (!dbName || dbName === null || dbName === "" || !rule || rule === null || rule === "") {
		document.location.href = "main.html";
		return;
	}
	document.getElementById("btn-logout").innerHTML = "Logout (" + xowl.getUser() + ")";
	document.getElementById("placeholder-db").appendChild(document.createTextNode(dbName));
	document.getElementById("placeholder-db").href = "db.html?id=" + encodeURIComponent(dbName);
	document.getElementById("placeholder-rule").appendChild(document.createTextNode(rule));
	xowl.getDBRule(function (code, type, content) {
		if (code === 200) {
			document.getElementById("field-rule-definition").value = content.definition;
			displayMessage(null);
		} else {
			displayMessage(getErrorFor(type, content));
		}
	}, dbName, rule);
}

function onButtonLogout() {
	xowl.logout();
	document.location.href = "../index.html";
}