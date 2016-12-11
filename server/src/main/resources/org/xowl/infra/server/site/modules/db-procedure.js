// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");
var procName = getParameterByName("procedure");
var procedure = null;
var PARAMETERS_VALUES = [];

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
				procedure = content;
				document.getElementById("field-definition").value = content.definition;
				renderParameters(content.parameters);
			}
		}, dbName, procName);
	});
}

function renderParameters(parameters) {
	var table = document.getElementById("parameters");
	for (var i = 0; i != parameters.length; i++) {
		var cells = [
			document.createElement("td"),
			document.createElement("td")
		];
		cells[0].appendChild(document.createTextNode(parameters[i]));
		var field = document.createElement("input");
		field.type = "text";
		field.classList.add("form-control");
		PARAMETERS_VALUES.push(field);
		cells[1].appendChild(field);
		var row = document.createElement("tr");
		row.appendChild(cells[0]);
		row.appendChild(cells[1]);
		table.appendChild(row);
	}
}

function createOption(id, name) {
	var option = document.createElement("option");
	option.value = id;
	option.appendChild(document.createTextNode(name));
	return option;
}

function onExecute() {
	if (!onOperationRequest("Working ..."))
		return;
	var context = {
		"type": "org.xowl.infra.server.api.XOWLStoredProcedureContext",
		"defaultIRIs": [],
		"namedIRIs": [],
		"parameters": []
	};
	for (var i = 0; i != procedure.parameters.length; i++) {
		var values = lexNQuads(PARAMETERS_VALUES[i].value);
		var value = values.length == 0 ? {
			"type": "literal",
			"value": ""
		} : values[0];
		var mapping = {};
		mapping[procedure.parameters[i]] = value;
		context.parameters.push(mapping);
	}
	xowl.executeDBProcedure(function (status, ct, content) {
		if (onOperationEnded(status, content)) {
			renderSparqlResults(ct, content);
		}
	}, dbName, procName, context);
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