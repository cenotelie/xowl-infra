// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");
var FLAG = false;
var DEFAULT_QUERY =
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
	"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
	"PREFIX xowl: <http://xowl.org/infra/store/rules/xowl#>\n\n" +
	"SELECT DISTINCT ?x ?y WHERE { GRAPH ?g { ?x a ?y } }";

function init() {
	if (!xowl.isLoggedIn()) {
		document.location.href = "../index.html";
		return;
	}
	if (!dbName || dbName === null || dbName === "") {
		document.location.href = "main.html";
		return;
	}
	document.getElementById("btn-logout").innerHTML = "Logout (" + xowl.getUser() + ")";
	document.getElementById("placeholder-db").appendChild(document.createTextNode(dbName));
	document.getElementById("placeholder-db").href = "db.html?id=" + encodeURIComponent(dbName);
	document.getElementById("field-definition").value = DEFAULT_QUERY;
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
	var params = document.getElementById("field-params").value;
	var definition = document.getElementById("field-definition").value;
	if (name === null || name === "" || definition === null || definition === "")
		return;
	FLAG = true;
	displayMessage("Creating procedure ...");

	var parameters = [];
	params = params === null ? [] : params.split(",");
	for (var i = 0; i != params.length; i++) {
		var param = params[i].trim();
		if (param.length > 0)
			parameters.push(param);
	}
	xowl.addDBProcedure(function (code, type, content) {
		FLAG = false;
		if (code === 200) {
			window.location.href = "db.html?id=" + encodeURIComponent(dbName);
		} else {
			displayMessage(getErrorFor(type, content));
		}
	}, dbName, {
	    "type": "org.xowl.infra.server.api.XOWLStoredProcedure",
	    "name": name,
	    "definition": definition,
	    "parameters": parameters
	});
}