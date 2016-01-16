// Copyright (c) 2015 Laurent Wouters
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("id");
var FLAG = false;
var RULE_TOGGLE = {};
var DEFAULT_RULE =
	"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n" +
	"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n" +
	"@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.\n" +
	"@prefix owl: <http://www.w3.org/2002/07/owl#>.\n" +
	"@prefix xowl: <http://xowl.org/store/rules/xowl#>.\n\n" +
	"rule xowl:myrule distinct {\n" +
	"    ?x rdf:type ?y\n" +
	"    NOT (?x rdf:type owl:Class)\n" +
	"} => {\n" +
	"    ?x rdf:type xowl:MyClass\n" +
	"}";

function init() {
	if (!xowl.isLoggedIn()) {
		document.location.href = "../index.html";
		return;
	}
	if (!dbName || dbName === null || dbName === "") {
		document.location.href = "main.html";
		return;
	}
	document.getElementById("btn-logout").innerHTML = "Logout (" + xowl.getUser() + ")";
	document.getElementById("placeholder-db").appendChild(document.createTextNode(dbName));
	document.getElementById("field-rule-definition").value = DEFAULT_RULE;
	document.getElementById('import-file').addEventListener('change', onFileSelected, false);
	var typesField = document.getElementById("import-file-type");
	for (var i = 0; i != MIME_TYPES.length; i++) {
		var option = document.createElement("option");
		option.value = MIME_TYPES[i].value;
		option.appendChild(document.createTextNode(MIME_TYPES[i].name));
		typesField.appendChild(option);
	}
	xowl.getEntailmentFor(function (code, type, content) {
		if (code === 200) {
			document.getElementById('field-entailment').value = content;
			xowl.getDatabasePrivileges(function (code, type, content) {
				if (code === 200) {
					renderAccesses(content.accesses);
					xowl.getDBRules(function (code, type, content) {
						if (code === 200) {
							renderRules(content);
							xowl.getDBActiveRules(function (code, type, content) {
								if (code === 200) {
									renderActiveRules(content);
								} else {
									displayMessage(getErrorFor(type, content));
								}
							}, dbName);
						} else {
							displayMessage(getErrorFor(type, content));
						}
					}, dbName);
				} else {
					displayMessage(getErrorFor(type, content));
				}
			}, dbName);
		} else {
			displayMessage(getErrorFor(type, content));
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

function onButtonLogout() {
	xowl.logout();
	document.location.href = "../index.html";
}

function onSetEntailment() {
	if (FLAG)
		return;
	FLAG = true;
	var regime = document.getElementById('field-entailment').value;
	displayMessage("Setting entailment ...");
	xowl.setEntailmentFor(function (code, type, content) {
		if (code === 200) {
			alert("OK");
			displayMessage(null);
		} else {
			displayMessage(getErrorFor(type, content));
		}
	}, dbName, regime);
}

function onSPARQL() {
	document.location.href = "sparql.html?db=" + encodeURIComponent(dbName);
}

function onDrop() {
	if (FLAG)
		return;
	if (!confirm("Drop database " + dbName + " (data will be erased)?"))
		return;
	FLAG = true;
	displayMessage("Dropping database ...");
	xowl.dropDatabase(function (code, type, content) {
		FLAG = false;
		if (code === 200) {
			document.location.href = "main.html";
		} else {
			displayMessage(getErrorFor(type, content));
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
	if (FLAG)
		return;
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
		displayMessage("Reading ...");
	}
	reader.onloadend = function (event) {
		if (reader.error !== null) {
			displayMessage("Error: " + reader.error.toString());
			progressBar['aria-valuenow'] = 100;
			progressBar.style.width = "100%";
			progressBar.classList.add("progress-bar-error");
			return;
		}
		displayMessage("Sending ...");
		xowl.upload(function (code, type, content) {
			if (code === 200) {
				alert("OK");
				displayMessage(null);
				progressBar.classList.add("progress-bar-success");
			} else {
				displayMessage(getErrorFor(code, content));
				progressBar.classList.add("progress-bar-error");
			}
			progressBar['aria-valuenow'] = 100;
			progressBar.style.width = "100%";
		}, dbName, selectedMIME, reader.result);
	}
	reader.readAsBinaryString(file);
}

function onGrant() {
	if (FLAG)
		return;
	var userName = document.getElementById('field-grant-user').value;
    var privilege = document.getElementById('field-grant-right').value;
	if (userName === null || userName === "" || privilege === null || privilege === "")
		return;
	FLAG = true;
	displayMessage("Granting ...");
	xowl.grantDB(function (code, type, content) {
		FLAG = false;
		if (code === 200) {
			document.location.reload();
		} else {
			displayMessage(getErrorFor(type, content));
		}
	}, dbName, privilege, userName);
}

function onRevoke(user, privilege) {
	if (FLAG)
		return;
	FLAG = true;
	displayMessage("Revoking ...");
	xowl.revokeDB(function (code, type, content) {
        if (code === 200) {
			document.location.reload();
        } else {
			displayMessage(getErrorFor(type, content));
        }
	}, dbName, privilege, user);
}

function onCreateRule() {
	if (FLAG)
		return;
	var definition = document.getElementById('field-rule-definition').value;
	if (definition === null || definition === "")
		return;
	FLAG = true;
	displayMessage("Adding new rule ...");
	xowl.addDBRule(function (code, type, content) {
		FLAG = false;
		if (code === 200) {
			document.location.reload();
		} else {
			displayMessage(getErrorFor(type, content));
		}
	}, dbName, definition);
}

function onDeleteRule(rule) {
	if (FLAG)
		return;
	FLAG = true;
	displayMessage("Removing rule ...");
	xowl.removeDBRule(function (code, type, content) {
        if (code === 200) {
			document.location.reload();
        } else {
			displayMessage(getErrorFor(type, content));
        }
	}, dbName, rule);
}

function onActivateRule(rule) {
	if (FLAG)
		return;
	FLAG = true;
	displayMessage("Activating rule ...");
	xowl.activateDBRule(function (code, type, content) {
        if (code === 200) {
			document.location.reload();
        } else {
			displayMessage(getErrorFor(type, content));
        }
	}, dbName, rule);
}

function onDeactivateRule(rule) {
	if (FLAG)
		return;
	FLAG = true;
	displayMessage("Deactivating rule ...");
	xowl.deactivateDBRule(function (code, type, content) {
        if (code === 200) {
			document.location.reload();
        } else {
			displayMessage(getErrorFor(type, content));
        }
	}, dbName, rule);
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
		return a.localeCompare(b);
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
		(function (rule) {
			toggle.onclick = function () {
				if (toggle.classList.contains("toggle-button-selected"))
					onDeactivateRule(rule);
				else
					onActivateRule(rule);
			}
		})(rules[i]);
		RULE_TOGGLE[rules[i]] = toggle;
		cells[0].appendChild(revoke);
		cells[1].appendChild(renderRuleName(rules[i]));
		cells[2].appendChild(toggle);
		var row = document.createElement("tr");
		row.appendChild(cells[0]);
		row.appendChild(cells[1]);
		row.appendChild(cells[2]);
		table.appendChild(row);
	}
	displayMessage(null);
}

function renderActiveRules(rules) {
	for (var i = 0; i != rules.length; i++) {
		if (RULE_TOGGLE.hasOwnProperty(rules[i])) {
			RULE_TOGGLE[rules[i]].classList.add("toggle-button-selected");
		}
	}
	displayMessage(null);
}

function renderRuleName(rule) {
	var a = document.createElement("a");
	a.href = "rule.html?db=" + encodeURIComponent(dbName) + "&rule=" + encodeURIComponent(rule);
	a.appendChild(document.createTextNode(rule));
	return a;
}

function renderRuleToggle() {
	var div = document.createElement("div");
	div.classList.add("toggle-button");
	div.appendChild(document.createElement("button"));
	return div;
}