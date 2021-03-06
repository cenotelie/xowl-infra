// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");
var HISTORY = [];
var DEFAULT_QUERY =
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
	"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
	"PREFIX xowl: <http://xowl.org/infra/store/rules/xowl#>\n\n" +
	"SELECT DISTINCT ?x ?y WHERE { GRAPH ?g { ?x a ?y } }";
var resultCount = 0;

function init() {
	doSetupPage(xowl, true, [
		{name: "Database " + dbName, uri: "db.html?db=" + encodeURIComponent(dbName)},
		{name: "SPARQL"}
	], function() {
		if (!dbName || dbName === null || dbName === "")
			return;
		document.getElementById("sparql").value = DEFAULT_QUERY;
	});
}

function onExecute() {
	var query = document.getElementById("sparql").value;
	HISTORY.push(query);
	renderHistory(HISTORY.length - 1);
	if (!onOperationRequest("Working ..."))
		return;
	xowl.sparql(function (status, ct, content) {
		if (onOperationEnded(status, content)) {
			renderSparqlResults(ct, content);
		}
	}, dbName, query);
}

function renderHistory(index) {
	var date = new Date();
	var span = document.createElement("span");
	span.appendChild(document.createTextNode("recall " + (index + 1).toString()));
	span.classList.add("badge");
	span.style.cursor = "pointer";
	span.onclick = function () {
		document.getElementById("sparql").value = HISTORY[index];
	};
	var cell1 = document.createElement("td");
	cell1.appendChild(span);
	var cell2 = document.createElement("td");
	cell2.appendChild(document.createTextNode(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds()));
	var row = document.createElement("tr");
	row.appendChild(cell1);
	row.appendChild(cell2);
	document.getElementById("history").appendChild(row);
}

function renderSparqlResults(ct, content) {
	var index = ct.indexOf(";");
	if (index !== -1)
		ct = ct.substring(0, index);
	if (ct === "application/sparql-results+json") {
		var data = JSON.parse(content);
		if (data.hasOwnProperty("boolean")) {
			var isSuccess = data.boolean;
			if (isSuccess)
				alert("OK");
			else
				alert(data.error);
			return;
		}
		renderClear();
		var vars = data.head.vars;
		var solutions = data.results.bindings;
		renderSparqlHeader(vars);
		for (var i = 0; i != solutions.length; i++) {
			var solution = solutions[i];
			var cells = [];
			for (var j = 0; j != vars.length; j++) {
				if (solution.hasOwnProperty(vars[j])) {
					cells.push(solution[vars[j]]);
				} else {
					cells.push("");
				}
			}
			renderRdfNodes(cells, injectResult);
		}
	} else if (ct === "application/json") {
		var data = JSON.parse(content);
		renderClear();
		renderSparqlHeader(['s', 'p', 'o', 'g']);
		renderRdfQuads(data, injectResult);
	}
}

function renderClear() {
	resultCount = 0;
	var parent = document.getElementById("result-heads");
	while (parent.hasChildNodes()) {
		parent.removeChild(parent.lastChild);
	}
	parent = document.getElementById("result-data");
	while (parent.hasChildNodes()) {
		parent.removeChild(parent.lastChild);
	}
}

function renderSparqlHeader(columns) {
	var row = document.createElement("tr");
	var cell = document.createElement("td");
	cell.appendChild(document.createTextNode("#"));
	row.appendChild(cell);
	for (var i = 0; i != columns.length; i++) {
		cell = document.createElement("td");
		cell.appendChild(document.createTextNode(columns[i]));
		row.appendChild(cell);
	}
	var head = document.getElementById("result-heads");
	head.appendChild(row);
}

function injectResult(row) {
	resultCount++;
	var data = document.getElementById("result-data");
	var cell = document.createElement("td");
	cell.appendChild(document.createTextNode(resultCount.toString()));
	row.insertBefore(cell, row.firstChild);
	data.appendChild(row);
}