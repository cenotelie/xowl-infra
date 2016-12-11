// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");

function init() {
	doSetupPage(xowl, true, [
		{name: "Database " + dbName, uri: "db.html?db=" + encodeURIComponent(dbName)},
		{name: "Procedures"}
	], function() {
		if (!dbName || dbName === null || dbName === "")
			return;
		document.getElementById("link-new").href = "db-procedures-new.html?db=" + encodeURIComponent(dbName);
		doGetData();
	});
}

function doGetData() {
	if (!onOperationRequest("Loading ..."))
		return;
	xowl.getDBProcedures(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			renderProcedures(content);
		}
	}, dbName);
}


function onDeleteProcedure(procedure) {
	var result = confirm("Remove procedure " + procedure.name + "?");
	if (!result)
		return;
	if (!onOperationRequest("Removing procedure " + procedure.name + " ..."))
		return;
	xowl.removeDBProcedure(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Removed procedure " + procedure.name + ".");
			waitAndRefresh();
		}
	}, dbName, procedure.name);
}

function renderProcedures(procedures) {
	procedures.sort(function (a, b) {
		return a.name.localeCompare(b.name);
	});
	var table = document.getElementById("procedures");
	for (var i = 0; i != procedures.length; i++) {
		var cells = [
			document.createElement("td"),
			document.createElement("td")
		];
		var revoke = renderRevoke();
		(function (procedure) {
			revoke.onclick = function () {
				onDeleteProcedure(procedure);
			}
		})(procedures[i]);
		cells[0].appendChild(revoke);
		cells[1].appendChild(renderProcedureName(procedures[i]));
		var row = document.createElement("tr");
		row.appendChild(cells[0]);
		row.appendChild(cells[1]);
		table.appendChild(row);
	}
}

function renderProcedureName(procedure) {
	var a = document.createElement("a");
	a.href = "db-procedure.html?db=" + encodeURIComponent(dbName) + "&procedure=" + encodeURIComponent(procedure.name);
	a.appendChild(document.createTextNode(procedure.name));
	return a;
}

function renderRevoke() {
	var span = document.createElement("span");
	span.classList.add("glyphicon");
	span.classList.add("glyphicon-minus");
	span.setAttribute("aria-hidden", "true");
	var a = document.createElement("a");
	a.classList.add("btn");
	a.classList.add("btn-xs");
	a.classList.add("btn-danger");
	a.title = "REVOKE";
	a.style.marginLeft = "20px";
	a.appendChild(span);
	return a;
}