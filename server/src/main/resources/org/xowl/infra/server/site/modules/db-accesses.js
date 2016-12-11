// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");

function init() {
	doSetupPage(xowl, true, [
		{name: "Database " + dbName, uri: "db.html?db=" + encodeURIComponent(dbName)},
		{name: "Accesses"}
	], function() {
		if (!dbName || dbName === null || dbName === "")
			return;
		doGetData();
	});
}

function doGetData() {
	if (!onOperationRequest("Loading ..."))
		return;
	xowl.getDatabasePrivileges(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			renderAccesses(content.accesses);
		}
	}, dbName);
}

function onGrant() {
	var userName = document.getElementById('field-grant-user').value;
	var privilege = document.getElementById('field-grant-right').value;
	if (userName === null || userName === "" || privilege === null || privilege === "")
		return;
	if (!onOperationRequest("Granting " + privilege + " access to user " + userName + " ..."))
		return;
	xowl.grantDB(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Granted " + privilege + " access to user " + userName + ".");
			waitAndRefresh();
		}
	}, dbName, privilege, userName);
}

function onRevoke(user, privilege) {
	if (!onOperationRequest("Revoking " + privilege + " access to user " + user + " ..."))
		return;
	xowl.revokeDB(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Revoked " + privilege + " access to user " + user + ".");
			waitAndRefresh();
		}
	}, dbName, privilege, user);
}

function renderAccesses(accesses) {
	accesses.sort(function (a, b) {
		return a.user.localeCompare(b.user);
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
		cells[0].appendChild(renderUser(accesses[i].user));
		cells[1].appendChild(renderAccess(accesses[i].isAdmin, false));
		if (accesses[i].isAdmin) {
			var btn = renderRevoke();
			(function (access) {
				btn.onclick = function () {
					onRevoke(access.user, "ADMIN");
				}
			})(accesses[i]);
			cells[1].appendChild(btn);
		}
		cells[2].appendChild(renderAccess(accesses[i].canWrite, accesses[i].isAdmin));
		if (accesses[i].canWrite) {
			var btn = renderRevoke();
			(function (access) {
				btn.onclick = function () {
					onRevoke(access.user, "WRITE");
				}
			})(accesses[i]);
			cells[2].appendChild(btn);
		}
		cells[3].appendChild(renderAccess(accesses[i].canRead, accesses[i].isAdmin || accesses[i].canWrite));
		if (accesses[i].canRead) {
			var btn = renderRevoke();
			(function (access) {
				btn.onclick = function () {
					onRevoke(access.user, "READ");
				}
			})(accesses[i]);
			cells[3].appendChild(btn);
		}
		var row = document.createElement("tr");
		row.appendChild(cells[0]);
		row.appendChild(cells[1]);
		row.appendChild(cells[2]);
		row.appendChild(cells[3]);
		table.appendChild(row);
	}
}

function renderUser(userName) {
	var icon = document.createElement("span");
	icon.classList.add("glyphicon");
	icon.classList.add("glyphicon-user");
	var text = document.createElement("span");
	text.appendChild(document.createTextNode(" " + userName));
	var link = document.createElement("a");
	link.href = "user.html?id=" + encodeURIComponent(userName);
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