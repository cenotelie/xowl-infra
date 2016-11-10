// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("id");
var DEFAULT_RULE =
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
	"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
	"PREFIX xowl: <http://xowl.org/infra/store/rules/xowl#>\n\n" +
	"rule distinct xowl:myrule {\n" +
	"    ?x rdf:type ?y .\n" +
	"    NOT (?x rdf:type owl:Class)\n" +
	"} => {\n" +
	"    ?x rdf:type xowl:MyClass\n" +
	"}";

function init() {
	doSetupPage(xowl, true, [{name: "Database " + dbName}], function() {
		if (!dbName || dbName === null || dbName === "")
			return;
		document.getElementById("field-rule-definition").value = DEFAULT_RULE;
		document.getElementById('import-file').addEventListener('change', onFileSelected, false);
		document.getElementById("new-procedure-link").href = "procedure-new.html?db=" + encodeURIComponent(dbName);
		var typesField = document.getElementById("import-file-type");
		for (var i = 0; i != MIME_TYPES.length; i++) {
			var option = document.createElement("option");
			option.value = MIME_TYPES[i].value;
			option.appendChild(document.createTextNode(MIME_TYPES[i].name));
			typesField.appendChild(option);
		}
		doGetData();
	});
}

function doGetData() {
	if (!onOperationRequest("Loading ...", 4))
		return;
	xowl.getEntailmentFor(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			document.getElementById('field-entailment').value = content;
		}
	}, dbName);
	xowl.getDatabasePrivileges(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			renderAccesses(content.accesses);
		}
	}, dbName);
	xowl.getDBRules(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			renderRules(content);
		}
	}, dbName);
	xowl.getDBProcedures(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			renderProcedures(content);
		}
	}, dbName);
}

function toggleImport() {
	var panel = document.getElementById("panel-import");
	if (panel.style.display === "none")
		panel.style.display = "";
	else
		panel.style.display = "none";
}

function toggleGrant() {
	var panel = document.getElementById("panel-grant");
	if (panel.style.display === "none")
		panel.style.display = "";
	else
		panel.style.display = "none";
}

function toggleRule() {
	var panel = document.getElementById("panel-rule");
	if (panel.style.display === "none")
		panel.style.display = "";
	else
		panel.style.display = "none";
}

function onSetEntailment() {
	var regime = document.getElementById('field-entailment').value;
	if (!onOperationRequest("Setting entailment ..."))
		return;
	xowl.setEntailmentFor(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Entailment has been set.");
		}
	}, dbName, regime);
}

function onDrop() {
	var result = confirm("Drop database " + dbName + "?");
	if (!result)
		return;
	if (!onOperationRequest("Dropping database " + dbName + " ..."))
		return;
	xowl.dropDatabase(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Dropped database " + dbName + ".");
			waitAndGo("/web/index.html");
		}
	}, dbName);
}

function onFileSelected(evt) {
	var file = evt.target.files[0];
	var mime = file.type;
	var fileType = null;
	for (var i = 0; i != MIME_TYPES.length; i++) {
		if (MIME_TYPES[i].value === mime) {
			fileType = MIME_TYPES[i];
			break;
		}
		for (var j = 0; j != MIME_TYPES[i].extensions.length; j++) {
			var suffix = MIME_TYPES[i].extensions[j];
			if (file.name.indexOf(suffix, file.name.length - suffix.length) !== -1) {
				fileType = MIME_TYPES[i];
				break;
			}
		}
	}
	if (fileType !== null) {
		document.getElementById("import-file-type").value = fileType.value;
	}
}

function onImport() {
	if (document.getElementById("import-file").files.length == 0)
		return;
	var file = document.getElementById("import-file").files[0];
	var selectedMIME = document.getElementById("import-file-type").value;
	var progressBar = document.getElementById("import-progress");
	progressBar['aria-valuenow'] = 0;
	progressBar.style.width = "0%";
	progressBar.classList.remove("progress-bar-success");
	progressBar.classList.remove("progress-bar-error");
	progressBar.innerHTML = null;
	var reader = new FileReader();
	reader.onprogress = function (event) {
		var ratio = 50 * event.loaded / event.total;
		progressBar['aria-valuenow'] = ratio;
		progressBar.style.width = ratio.toString() + "%";
	}
	reader.onloadend = function (event) {
		onOperationEnded(200, null);
		if (reader.error !== null) {
			progressBar['aria-valuenow'] = 100;
			progressBar.style.width = "100%";
			progressBar.classList.add("progress-bar-error");
			displayMessage("error", "Failed to read.");
			return;
		}
		if (!onOperationRequest("Uploading ..."))
			return;
		xowl.upload(function (status, type, content) {
			if (onOperationEnded(status, content)) {
				displayMessage("success", "The file has been uploaded and imported.");
				progressBar.classList.add("progress-bar-success");
			} else {
				progressBar.classList.add("progress-bar-error");
			}
			progressBar['aria-valuenow'] = 100;
			progressBar.style.width = "100%";
		}, dbName, selectedMIME, reader.result);
	}
	reader.readAsBinaryString(file);
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
			waitAndGo("db.html?id=" + encodeURIComponent(dbName));
		}
	}, dbName, privilege, userName);
}

function onRevoke(user, privilege) {
	if (!onOperationRequest("Revoking " + privilege + " access to user " + user + " ..."))
		return;
	xowl.revokeDB(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Revoked " + privilege + " access to user " + user + ".");
			waitAndGo("db.html?id=" + encodeURIComponent(dbName));
		}
	}, dbName, privilege, user);
}

function onCreateRule() {
	var definition = document.getElementById('field-rule-definition').value;
	if (definition === null || definition === "")
		return;
	if (!onOperationRequest("Adding new rule ..."))
		return;
	xowl.addDBRule(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "The rule has been inserted.");
			waitAndGo("db.html?id=" + encodeURIComponent(dbName));
		}
	}, dbName, definition);
}

function onDeleteRule(rule) {
	var result = confirm("Remove rule " + rule.name + "?");
	if (!result)
		return;
	if (!onOperationRequest("Removing rule " + rule.name + " ..."))
		return;
	xowl.removeDBRule(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Removed rule " + rule.name + ".");
			waitAndGo("db.html?id=" + encodeURIComponent(dbName));
		}
	}, dbName, rule.name);
}

function onActivateRule(rule) {
	if (!onOperationRequest("Activating rule " + rule.name + " ..."))
		return;
	xowl.activateDBRule(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Activated rule " + rule.name + ".");
			waitAndGo("db.html?id=" + encodeURIComponent(dbName));
		}
	}, dbName, rule.name);
}

function onDeactivateRule(rule) {
	if (!onOperationRequest("Deactivating rule " + rule.name + " ..."))
		return;
	xowl.deactivateDBRule(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Deactivated rule " + rule.name + ".");
			waitAndGo("db.html?id=" + encodeURIComponent(dbName));
		}
	}, dbName, rule.name);
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
			waitAndGo("db.html?id=" + encodeURIComponent(dbName));
		}
	}, dbName, procedure.name);
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
	displayMessage(null);
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

function renderRules(rules) {
	rules.sort(function (a, b) {
		return a.name.localeCompare(b.name);
	});
	var table = document.getElementById("rules");
	for (var i = 0; i != rules.length; i++) {
		var cells = [
			document.createElement("td"),
			document.createElement("td"),
			document.createElement("td")
		];
		var revoke = renderRevoke();
		(function (rule) {
			revoke.onclick = function () {
				onDeleteRule(rule);
			}
		})(rules[i]);
		var toggle = renderRuleToggle();
		if (rules[i].isActive)
			toggle.classList.add("toggle-button-selected");
		(function (rule) {
			toggle.onclick = function () {
				if (rule.isActive)
					onDeactivateRule(rule);
				else
					onActivateRule(rule);
			}
		})(rules[i]);
		cells[0].appendChild(revoke);
		cells[1].appendChild(renderRuleName(rules[i]));
		cells[2].appendChild(toggle);
		var row = document.createElement("tr");
		row.appendChild(cells[0]);
		row.appendChild(cells[1]);
		row.appendChild(cells[2]);
		table.appendChild(row);
	}
}

function renderRuleName(rule) {
	var a = document.createElement("a");
	a.href = "rule.html?db=" + encodeURIComponent(dbName) + "&rule=" + encodeURIComponent(rule.name);
	a.appendChild(document.createTextNode(rule.name));
	return a;
}

function renderRuleToggle() {
	var div = document.createElement("div");
	div.classList.add("toggle-button");
	div.appendChild(document.createElement("button"));
	return div;
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
	displayMessage(null);
}

function renderProcedureName(procedure) {
	var a = document.createElement("a");
	a.href = "procedure.html?db=" + encodeURIComponent(dbName) + "&procedure=" + encodeURIComponent(procedure.name);
	a.appendChild(document.createTextNode(procedure.name));
	return a;
}