// Copyright (c) 2015 Laurent Wouters
// Provided under LGPL v3

var xowl = new XOWL();
var FLAG = false;

function init() {
	if (!xowl.isLoggedIn()) {
		document.location.href = "../index.html";
		return;
	}
	document.getElementById("panel-admin-server").style.display = "none";
	document.getElementById("panel-admin-new-db").style.display = "none";
	xowl.getUserPrivileges(function (code, type, content) {
		if (code === 200) {
			if (content.isServerAdmin) {
				document.getElementById("panel-admin-server").style.display = "";
				document.getElementById("panel-admin-new-db").style.display = "";
			}
			xowl.getDatabases(function (code, type, content) {
				if (code === 200) {
					renderDatabases(content);
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
	xowl.serverShutdown(function (code, type, content) {
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
		var icon = document.createElement("span");
		icon.classList.add("glyphicon");
		icon.classList.add("glyphicon-hdd");
		var text = document.createElement("span");
		text.appendChild(document.createTextNode(" " + databases[i].name));
		var link = document.createElement("a");
		link.href = "db.html?id=" + encodeURIComponent(databases[i].name);
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