// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");
var rule = getParameterByName("rule");

function init() {
	doSetupPage(xowl, true, [
		{name: "Database " + dbName, uri: "db.html?db=" + encodeURIComponent(dbName)},
		{name: "Rules", uri: "db-rules.html?db=" + encodeURIComponent(dbName)},
		{name: "Rule " + rule}
	], function() {
		if (!dbName || dbName === null || dbName === "")
			return;
		if (!rule || rule === null || rule === "")
			return;
		if (!onOperationRequest("Loading ..."))
			return;
		xowl.getDBRule(function (status, type, content) {
			if (onOperationEnded(status, content)) {
				document.getElementById("field-rule-definition").value = content.definition;
			}
		}, dbName, rule);
	});
}