// Copyright (c) 2015 Laurent Wouters
// Provided under LGPL v3

var xowl = new XOWL();
var FLAG = false;

function init() {
	if (!xowl.isLoggedIn()) {
		document.location.href = "../index.html";
		return;
	}
	document.getElementById("btn-logout").innerHTML = "Logout (" + xowl.getUser() + ")";
	xowl.getUserPrivileges(function (code, type, content) {
		if (code === 200) {
			renderAccesses(content.accesses);
		} else {
			displayMessage(getErrorFor(type, content));
		}
	}, xowl.getUser());
}

function onButtonLogout() {
	xowl.logout();
	document.location.href = "../index.html";
}

function onChangePassword() {
	if (FLAG)
		return;
	var password1 = document.getElementById("field-password1").value;
	var password2 = document.getElementById("field-password2").value;
	if (password1 === null || password1 === "" || password2 === null || password2 === "")
		return;
	if (password1 !== password2) {
		displayMessage("Passwords do not match!");
		return;
	}
	FLAG = true;
	displayMessage("Changing password ...");
	xowl.changePassword(function (code, type, content) {
		FLAG = false;
		if (code === 200) {
			displayMessage(null);
		} else {
			displayMessage(getErrorFor(type, content));
		}
	}, password1);
}

function renderAccesses(accesses) {
	accesses.sort(function (a, b) {
		return a.database.localeCompare(b.database);
	});
	var table = document.getElementById("accesses");
	for (var i = 0; i != accesses.length; i++) {
		var cells = [
			document.createElement("td"),
			document.createElement("td"),
			document.createElement("td"),
			document.createElement("td")
		];
		cells[1].align = "center";
		cells[2].align = "center";
		cells[3].align = "center";
		cells[0].appendChild(renderDatabase(accesses[i].database));
		cells[1].appendChild(renderAccess(accesses[i].isAdmin, false));
		cells[2].appendChild(renderAccess(accesses[i].canWrite, accesses[i].isAdmin));
		cells[3].appendChild(renderAccess(accesses[i].canRead, accesses[i].isAdmin || accesses[i].canWrite));
		var row = document.createElement("tr");
		row.appendChild(cells[0]);
		row.appendChild(cells[1]);
		row.appendChild(cells[2]);
		row.appendChild(cells[3]);
		table.appendChild(row);
	}
	displayMessage(null);
}

function renderDatabase(dbName) {
	var icon = document.createElement("span");
	icon.classList.add("glyphicon");
	icon.classList.add("glyphicon-hdd");
	var text = document.createElement("span");
	text.appendChild(document.createTextNode(" " + dbName));
	var link = document.createElement("a");
	link.href = "db.html?id=" + encodeURIComponent(dbName);
	link.appendChild(icon);
	link.appendChild(text);
	return link;
}

function renderAccess(isGranted, isInferred) {
	if (isGranted) {
		var span = document.createElement("span");
		span.classList.add("glyphicon");
		span.classList.add("glyphicon-ok");
		span.classList.add("text-success");
		span.setAttribute("aria-hidden", "true");
		span.title = "GRANTED";
		return span;
	} else if (isInferred) {
		var span = document.createElement("span");
		span.classList.add("glyphicon");
		span.classList.add("glyphicon-plus");
		span.classList.add("text-info");
		span.setAttribute("aria-hidden", "true");
		span.title = "INFERRED";
		return span;
	} else {
		return document.createElement("span");
	}
}