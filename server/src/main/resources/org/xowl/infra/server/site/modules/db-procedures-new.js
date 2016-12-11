// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");
var DEFAULT_QUERY =
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
	"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
	"PREFIX xowl: <http://xowl.org/infra/store/rules/xowl#>\n\n" +
	"SELECT DISTINCT ?x ?y WHERE { GRAPH ?g { ?x a ?y } }";

function init() {
	doSetupPage(xowl, true, [
		{name: "Database " + dbName, uri: "db.html?db=" + encodeURIComponent(dbName)},
		{name: "Procedures", uri: "db-procedures.html?db=" + encodeURIComponent(dbName)},
		{name: "New Procedure"}
	], function() {
		if (!dbName || dbName === null || dbName === "")
			return;
		document.getElementById("field-definition").value = DEFAULT_QUERY;
	});
}

function onCreateProcedure() {
	var name = document.getElementById("field-name").value;
	var params = document.getElementById("field-params").value;
	var definition = document.getElementById("field-definition").value;
	if (name === null || name === "" || definition === null || definition === "")
		return;
	if (!onOperationRequest("Creating procedure " + name + " ..."))
		return;
	var parameters = [];
	params = params === null ? [] : params.split(",");
	for (var i = 0; i != params.length; i++) {
		var param = params[i].trim();
		if (param.length > 0)
			parameters.push(param);
	}
	xowl.addDBProcedure(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Created procedure " + name + ".");
			waitAndGo("db-procedures.html?db=" + encodeURIComponent(dbName));
		}
	}, dbName, {
		"type": "org.xowl.infra.server.api.XOWLStoredProcedure",
		"name": name,
		"definition": definition,
		"parameters": parameters
	});
}