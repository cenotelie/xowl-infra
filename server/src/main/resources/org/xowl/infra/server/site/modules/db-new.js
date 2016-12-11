// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();

function init() {
	doSetupPage(xowl, true, [{name: "New Database"}], function() {});
}

function onCreateDB() {
	var name = document.getElementById("field-name").value;
	if (name === null || name === "")
		return;
	if (!onOperationRequest("Creating database " + name + " ..."))
		return;
	xowl.createDatabase(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Created database " + name + ".");
			waitAndGo("db.html?db=" + encodeURIComponent(name));
		}
	}, name);
}