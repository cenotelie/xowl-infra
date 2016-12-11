// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();

function init() {
	doSetupPage(xowl, true, [], function() {
		document.getElementById("panel-admin-server").style.display = "none";
		document.getElementById("panel-admin-new-db").style.display = "none";
		document.getElementById("panel-admin-new-user").style.display = "none";
		doGetData();
	});
}

function doGetData() {
	if (!onOperationRequest("Loading ...", 3))
		return;
	xowl.getUserPrivileges(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			if (content.isServerAdmin) {
				document.getElementById("panel-admin-server").style.display = "";
				document.getElementById("panel-admin-new-db").style.display = "";
				document.getElementById("panel-admin-new-user").style.display = "";
			}
		}
	}, xowl.getLoggedInUser());
	xowl.getDatabases(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			renderDatabases(content);
		}
	});
	xowl.getUsers(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			renderUsers(content);
		}
	});
}

function renderDatabases(databases) {
	databases.sort(function (a, b) {
		return a.name.localeCompare(b.name);
	});
	var table = document.getElementById("databases");
	for (var i = 0; i != databases.length; i++) {
		var icon_db = document.createElement("span");
		icon_db.classList.add("glyphicon");
		icon_db.classList.add("glyphicon-hdd");
		var text_name = document.createElement("span");
		text_name.appendChild(document.createTextNode(" " + databases[i].name));
		var icon_sparql = document.createElement("span");
		icon_sparql.classList.add("glyphicon");
		icon_sparql.classList.add("glyphicon-question-sign");
		var text_sparql = document.createElement("span");
		text_sparql.appendChild(document.createTextNode(" SPARQL"));
		var link_settings = document.createElement("a");
		link_settings.style.marginLeft = "10px";
		link_settings.href = "modules/db.html?db=" + encodeURIComponent(databases[i].name);
		link_settings.appendChild(icon_db);
		link_settings.appendChild(text_name);
		var link_sparql = document.createElement("a");
		link_sparql.style.marginLeft = "10px";
		link_sparql.href = "modules/sparql.html?db=" + encodeURIComponent(databases[i].name);
		link_sparql.appendChild(icon_sparql);
		link_sparql.appendChild(text_sparql);
		var cell1 = document.createElement("td");
		cell1.appendChild(link_settings);
		var cell2 = document.createElement("td");
		cell2.appendChild(link_sparql);
		var row = document.createElement("tr");
		row.appendChild(cell1);
		row.appendChild(cell2);
		table.appendChild(row);
	}
}

function renderUsers(users) {
	users.sort(function (a, b) {
		return a.name.localeCompare(b.name);
	});
	var table = document.getElementById("users");
	for (var i = 0; i != users.length; i++) {
		var icon = document.createElement("span");
		icon.classList.add("glyphicon");
		icon.classList.add("glyphicon-user");
		var text = document.createElement("span");
		text.appendChild(document.createTextNode(" " + users[i].name));
		var link = document.createElement("a");
		link.href = "modules/user.html?id=" + encodeURIComponent(users[i].name);
		link.appendChild(icon);
		link.appendChild(text);
		var cell = document.createElement("td");
		cell.appendChild(link);
		var row = document.createElement("tr");
		row.appendChild(cell);
		table.appendChild(row);
	}
}

function onClickShutdown() {
	var result = confirm("Shutdown the server?");
	if (!result)
		return;
	if (!onOperationRequest("Shutting down the server ..."))
		return;
	xowl.serverShutdown(function (status, ct, content) {
		if (status == 0) {
			displayMessage("success", "The server shut down.");
		} else if (onOperationEnded(status, content)) {
			displayMessage("success", "The server shut down.");
		}
	});
}

function onClickRestart() {
	var result = confirm("Restart the server?");
	if (!result)
		return;
	if (!onOperationRequest("Restarting the server ..."))
		return;
	xowl.serverRestart(function (status, ct, content) {
		if (status == 0) {
			displayMessage("success", "The server is restarting.");
		} else if (onOperationEnded(status, content)) {
			displayMessage("success", "The server is restarting.");
		}
	});
}
