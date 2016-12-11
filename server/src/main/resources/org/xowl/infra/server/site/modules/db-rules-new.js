// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");
var DEFAULT_RULE =
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
	"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
	"PREFIX xowl: <http://xowl.org/infra/store/rules/xowl#>\n\n" +
	"rule distinct xowl:myrule {\n" +
	"    ?x rdf:type ?y .\n" +
	"    NOT (?x rdf:type owl:Class)\n" +
	"} => {\n" +
	"    ?x rdf:type xowl:MyClass\n" +
	"}";

function init() {
	doSetupPage(xowl, true, [
		{name: "Database " + dbName, uri: "db.html?db=" + encodeURIComponent(dbName)},
		{name: "Rules", uri: "db-rules.html?db=" + encodeURIComponent(dbName)},
		{name: "New Rule"}
	], function() {
		if (!dbName || dbName === null || dbName === "")
			return;
		document.getElementById("field-rule-definition").value = DEFAULT_RULE;
	});
}

function onCreateRule() {
	var definition = document.getElementById('field-rule-definition').value;
	if (definition === null || definition === "")
		return;
	if (!onOperationRequest("Adding new rule ..."))
		return;
	xowl.addDBRule(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "The rule has been inserted.");
			waitAndGo("db-rules.html?db=" + encodeURIComponent(dbName));
		}
	}, dbName, definition);
}