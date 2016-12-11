// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");

function init() {
	doSetupPage(xowl, true, [
		{name: "Database " + dbName, uri: "db.html?db=" + encodeURIComponent(dbName)},
		{name: "Rules"}
	], function() {
		if (!dbName || dbName === null || dbName === "")
			return;
		document.getElementById("link-new").href = "db-rules-new.html?db=" + encodeURIComponent(dbName);
		doGetData();
	});
}

function doGetData() {
	if (!onOperationRequest("Loading ..."))
		return;
	xowl.getDBRules(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			renderRules(content);
		}
	}, dbName);
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
			waitAndRefresh();
		}
	}, dbName, rule.name);
}

function onActivateRule(rule) {
	if (!onOperationRequest("Activating rule " + rule.name + " ..."))
		return;
	xowl.activateDBRule(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Activated rule " + rule.name + ".");
			waitAndRefresh();
		}
	}, dbName, rule.name);
}

function onDeactivateRule(rule) {
	if (!onOperationRequest("Deactivating rule " + rule.name + " ..."))
		return;
	xowl.deactivateDBRule(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Deactivated rule " + rule.name + ".");
			waitAndRefresh();
		}
	}, dbName, rule.name);
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
	a.href = "db-rule.html?db=" + encodeURIComponent(dbName) + "&rule=" + encodeURIComponent(rule.name);
	a.appendChild(document.createTextNode(rule.name));
	return a;
}

function renderRuleToggle() {
	var div = document.createElement("div");
	div.classList.add("toggle-button");
	div.appendChild(document.createElement("button"));
	return div;
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