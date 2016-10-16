// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");
var procName = getParameterByName("procedure");

function init() {
	if (!xowl.isLoggedIn()) {
		document.location.href = "../index.html";
		return;
	}
	if (!dbName || dbName === null || dbName === "" || !procName || procName === null || procName === "") {
		document.location.href = "main.html";
		return;
	}
	document.getElementById("btn-logout").innerHTML = "Logout (" + xowl.getUser() + ")";
	document.getElementById("placeholder-db").appendChild(document.createTextNode(dbName));
	document.getElementById("placeholder-db").href = "db.html?id=" + encodeURIComponent(dbName);
	document.getElementById("placeholder-procedure").appendChild(document.createTextNode(procName));
	xowl.getDBProcedure(function (code, type, content) {
		if (code === 200) {
			document.getElementById("field-name").value = content.name;
			document.getElementById("field-params").value = content.parameters;
			document.getElementById("field-definition").value = content.definition;
			displayMessage(null);
		} else {
			displayMessage(getErrorFor(type, content));
		}
	}, dbName, procName);
}

function onButtonLogout() {
	xowl.logout();
	document.location.href = "../index.html";
}