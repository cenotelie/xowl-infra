// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();

function init() {
	doSetupPage(xowl, true, [
		{name: "About ...", uri: "product.html"},
		{name: "Embedded dependencies"}], function() {
		doGetData();
	});
}

function doGetData() {
	if (!onOperationRequest("Loading ..."))
		return;
	xowl.getServerProductDependencies(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			renderDependencies(content);
		}
	});
}

function renderDependencies(dependencies) {
	dependencies.sort(function (a, b) {
		return a.name.localeCompare(b.name);
	});
	var table = document.getElementById("dependencies");
	for (var i = 0; i != dependencies.length; i++) {
		var cells = [
			document.createElement("td"),
			document.createElement("td"),
			document.createElement("td"),
			document.createElement("td")
		];
		var link = document.createElement("a");
		link.href = dependencies[i].link;
		link.appendChild(document.createTextNode(dependencies[i].name));
		cells[0].appendChild(link);
		cells[1].appendChild(document.createTextNode(dependencies[i].version));
		cells[2].appendChild(document.createTextNode(dependencies[i].copyright));
		link = document.createElement("a");
		link.appendChild(document.createTextNode(dependencies[i].license.name));
		(function (dependency) {
			link.onclick = function () {
				document.getElementById("field-license-text").value = dependency.license.fullText;
			}
		})(dependencies[i]);
		cells[3].appendChild(link);
		var row = document.createElement("tr");
		row.appendChild(cells[0]);
		row.appendChild(cells[1]);
		row.appendChild(cells[2]);
		row.appendChild(cells[3]);
		table.appendChild(row);
	}
}
