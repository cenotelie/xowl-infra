// Copyright (c) 2015 Laurent Wouters
// Provided under LGPL v3

var xowl = new XOWL();

function init() {
	if (!xowl.isLoggedIn()) {
		document.location.href = "../index.html";
		return;
	}
	document.getElementById("panel-admin-new-user").style.display = "none";
	xowl.getUserPrivileges(function (code, type, content) {
		if (code === 200) {
			if (content.isServerAdmin) {
				document.getElementById("panel-admin-new-user").style.display = "";
			}
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
	}, xowl.getUser());
}

function onButtonLogout() {
	xowl.logout();
	document.location.href = "../index.html";
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