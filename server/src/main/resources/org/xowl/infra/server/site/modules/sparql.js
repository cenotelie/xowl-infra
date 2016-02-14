// Copyright (c) 2015 Laurent Wouters
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");
var HISTORY = [];
var DEFAULT_QUERY =
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
	"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
	"PREFIX xowl: <http://xowl.org/store/rules/xowl#>\n\n" +
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
	document.getElementById("sparql").value = DEFAULT_QUERY;
	displayMessage(null);
}

function onButtonLogout() {
	xowl.logout();
	document.location.href = "../index.html";
}

function onExecute() {
	var query = document.getElementById("sparql").value;
	HISTORY.push(query);
	renderHistory(HISTORY.length - 1);
	displayMessage("Working ...");
	xowl.sparql(function (status, ct, content) {
		if (status == 200) {
			renderSparqlResults(ct, content);
			document.getElementById("loader").style.display = "none";
		} else {
			displayMessage(getErrorFor(status, content));
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
			renderSparqlResult(cells);
		}
	} else if (ct === "application/n-quads") {
		renderClear();
		renderSparqlHeader(['s', 'p', 'o', 'g']);
		var entities = parseNQuads(content);
		var names = Object.getOwnPropertyNames(entities);
		for (var p = 0; p != names.length; p++) {
			var entity = entities[names[p]];
			for (j = 0; j != entity.properties.length; j++) {
				var property = entity.properties[j];
				var cells = [];
				if (entity.isIRI)
					cells.push({ type: "iri", value: entity.id });
				else
					cells.push({ type: "bnode", value: entity.id });
				cells.push({ type: "iri", value: property.id });
				cells.push(property.value);
				cells.push({ type: "iri", value: property.graph });
				renderSparqlResult(cells);
			}
		}
	}
}

function renderClear() {
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

function renderSparqlResult(columns) {
	var data = document.getElementById("result-data");
	var row = document.createElement("tr");
	var cell = document.createElement("td");
	cell.appendChild(document.createTextNode((data.childElementCount + 1).toString()));
	row.appendChild(cell);
	for (var i = 0; i != columns.length; i++) {
		cell = document.createElement("td");
		if (columns[i] !== "")
			cell.appendChild(rdfToDom(columns[i]));
		row.appendChild(cell);
	}
	data.appendChild(row);
}