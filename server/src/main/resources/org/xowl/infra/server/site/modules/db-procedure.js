// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");
var procName = getParameterByName("procedure");

function init() {
	doSetupPage(xowl, true, [
		{name: "Database " + dbName, uri: "db.html?db=" + encodeURIComponent(dbName)},
		{name: "Procedures", uri: "db-procedures.html?db=" + encodeURIComponent(dbName)},
		{name: "Procedure " + procName}
	], function() {
		if (!dbName || dbName === null || dbName === "")
			return;
		if (!procName || procName === null || procName === "")
			return;
		if (!onOperationRequest("Loading ..."))
			return;
		xowl.getDBProcedure(function (status, type, content) {
			if (onOperationEnded(status, content)) {
				document.getElementById("field-name").value = content.name;
				document.getElementById("field-params").value = content.parameters;
				document.getElementById("field-definition").value = content.definition;
			}
		}, dbName, procName);
	});
}