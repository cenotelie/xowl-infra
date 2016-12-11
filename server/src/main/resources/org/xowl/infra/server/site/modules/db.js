// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");

function init() {
	doSetupPage(xowl, true, [{name: "Database " + dbName}], function() {
		if (!dbName || dbName === null || dbName === "")
			return;
		document.getElementById("link-sparql").href = "db-sparql.html?db=" + encodeURIComponent(dbName);
		document.getElementById("link-accesses").href = "db-accesses.html?db=" + encodeURIComponent(dbName);
		document.getElementById("link-rules").href = "db-rules.html?db=" + encodeURIComponent(dbName);
		document.getElementById("link-procedures").href = "db-procedures.html?db=" + encodeURIComponent(dbName);
		document.getElementById("link-import").href = "db-import.html?db=" + encodeURIComponent(dbName);
		doGetData();
	});
}

function doGetData() {
	if (!onOperationRequest("Loading ..."))
		return;
	xowl.getEntailmentFor(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			document.getElementById('field-entailment').value = content;
		}
	}, dbName);
}

function onSetEntailment() {
	var regime = document.getElementById('field-entailment').value;
	if (!onOperationRequest("Setting entailment ..."))
		return;
	xowl.setEntailmentFor(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Entailment has been set.");
		}
	}, dbName, regime);
}

function onDrop() {
	var result = confirm("Drop database " + dbName + "?");
	if (!result)
		return;
	if (!onOperationRequest("Dropping database " + dbName + " ..."))
		return;
	xowl.dropDatabase(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Dropped database " + dbName + ".");
			waitAndGo("/web/index.html");
		}
	}, dbName);
}