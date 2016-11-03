// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var FLAG = false;

function init() {
	if (!xowl.isLoggedIn()) {
		document.location.href = "../index.html";
		return;
	}
	document.getElementById("btn-logout").innerHTML = "Logout (" + xowl.getUser() + ")";
	document.getElementById("panel-admin-server").style.display = "none";
	document.getElementById("panel-admin-new-db").style.display = "none";
	document.getElementById("panel-admin-new-user").style.display = "none";
	xowl.getUserPrivileges(function (code, type, content) {
		if (code === 200) {
			if (content.isServerAdmin) {
				document.getElementById("panel-admin-server").style.display = "";
				document.getElementById("panel-admin-new-db").style.display = "";
				document.getElementById("panel-admin-new-user").style.display = "";
			}
			xowl.getDatabases(function (code, type, content) {
				if (code === 200) {
					renderDatabases(content);
					xowl.getUsers(function (code, type, content) {
						if (code === 200) {
							renderUsers(content);
						} else {
							displayMessage(getErrorFor(type, content));
						}
					});
				} else {
					displayMessage(getErrorFor(type, content));
				}
			});
		} else {
			displayMessage(getErrorFor(type, content));
		}
	}, xowl.getUser());
}

function onButtonLogout() {
	xowl.logout();
	document.location.href = "../index.html";
}

function onButtonShutdown() {
	if (FLAG)
		return;
	FLAG = true;
	displayMessage("Sending command ...");
	xowl.serverShutdown(function (code, type, content) {
		FLAG = false;
		if (code === 200) {
			displayMessage("Server is shutting down ...");
		} else {
			displayMessage(getErrorFor(type, content));
		}
	});
}

function onButtonRestart() {
	if (FLAG)
		return;
	FLAG = true;
	displayMessage("Sending command ...");
	xowl.serverRestart(function (code, type, content) {
		FLAG = false;
		if (code === 200) {
			displayMessage("Server is restarting ...");
		} else {
			displayMessage(getErrorFor(type, content));
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
		link_settings.href = "db.html?id=" + encodeURIComponent(databases[i].name);
		link_settings.appendChild(icon_db);
		link_settings.appendChild(text_name);
		var link_sparql = document.createElement("a");
		link_sparql.style.marginLeft = "10px";
		link_sparql.href = "sparql.html?db=" + encodeURIComponent(databases[i].name);
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
		link.href = "user.html?id=" + encodeURIComponent(users[i].name);
		link.appendChild(icon);
		link.appendChild(text);
		var cell = document.createElement("td");
		cell.appendChild(link);
		var row = document.createElement("tr");
		row.appendChild(cell);
		table.appendChild(row);
	}
	displayMessage(null);
}