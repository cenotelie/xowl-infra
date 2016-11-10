// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var userName = getParameterByName("id");

function init() {
	doSetupPage(xowl, true, [{name: "User " + userName}], function() {
		if (!userName || userName === null || userName === "")
			return;
		if (!onOperationRequest("Loading ..."))
			return;
		xowl.getUserPrivileges(function (status, type, content) {
			if (onOperationEnded(status, content)) {
				renderAccesses(content.accesses);
			}
		}, userName);
	});
}

function onGrant() {
	var database = document.getElementById('field-grant-db').value;
	var privilege = document.getElementById('field-grant-right').value;
	if (database === null || database === "" || privilege === null || privilege === "")
		return;
	if (!onOperationRequest("Granting " + privilege + " access to DB " + database + " ..."))
		return;
	xowl.grantDB(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Granted " + privilege + " access to DB " + database + ".");
			waitAndGo("user.html?id=" + encodeURIComponent(userName));
		}
	}, database, privilege, userName);
}

function onRevoke(database, privilege) {
	if (!onOperationRequest("Revoking " + privilege + " access to DB " + database + " ..."))
		return;
	xowl.revokeDB(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Revoked " + privilege + " access to DB " + database + ".");
			waitAndGo("user.html?id=" + encodeURIComponent(userName));
		}
	}, database, privilege, userName);
}

function onResetPassword() {
	var password1 = document.getElementById("field-password1").value;
	var password2 = document.getElementById("field-password2").value;
	if (password1 === null || password1 === "" || password2 === null || password2 === "")
		return;
	if (password1 !== password2) {
		displayMessage("error", "Passwords do not match!");
		return;
	}
	if (!onOperationRequest("Resetting password ..."))
		return;
	xowl.resetPassword(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Password has been reset.");
		}
	}, userName, password1);
}

function onUserDelete() {
	var result = confirm("Delete user " + userName + "?");
	if (!result)
		return;
	if (!onOperationRequest("Deleting user " + userName + " ..."))
		return;
	xowl.deleteUser(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Deleted user " + userName + ".");
			waitAndGo("/web/index.html");
		}
	}, userName);
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
		if (accesses[i].isAdmin) {
			var btn = renderRevoke();
			(function (access) {
				btn.onclick = function () {
					onRevoke(access.database, "ADMIN");
				}
			})(accesses[i]);
			cells[1].appendChild(btn);
		}
		cells[2].appendChild(renderAccess(accesses[i].canWrite, accesses[i].isAdmin));
		if (accesses[i].canWrite) {
			var btn = renderRevoke();
			(function (access) {
				btn.onclick = function () {
					onRevoke(access.database, "WRITE");
				}
			})(accesses[i]);
			cells[2].appendChild(btn);
		}
		cells[3].appendChild(renderAccess(accesses[i].canRead, accesses[i].isAdmin || accesses[i].canWrite));
		if (accesses[i].canRead) {
			var btn = renderRevoke();
			(function (access) {
				btn.onclick = function () {
					onRevoke(access.database, "READ");
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