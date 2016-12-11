// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();
var dbName = getParameterByName("db");

function init() {
	doSetupPage(xowl, true, [{name: "Database " + dbName}], function() {
		if (!dbName || dbName === null || dbName === "")
			return;
		//document.getElementById('import-file').addEventListener('change', onFileSelected, false);
		document.getElementById("link-sparql").href = "db-sparql.html?db=" + encodeURIComponent(dbName);
		document.getElementById("link-accesses").href = "db-accesses.html?db=" + encodeURIComponent(dbName);
		document.getElementById("link-rules").href = "db-rules.html?db=" + encodeURIComponent(dbName);
		document.getElementById("link-procedures").href = "db-procedures.html?db=" + encodeURIComponent(dbName);
		/*var typesField = document.getElementById("import-file-type");
		for (var i = 0; i != MIME_TYPES.length; i++) {
			var option = document.createElement("option");
			option.value = MIME_TYPES[i].value;
			option.appendChild(document.createTextNode(MIME_TYPES[i].name));
			typesField.appendChild(option);
		}*/
		doGetData();
	});
}

function doGetData() {
	if (!onOperationRequest("Loading ...", 2))
		return;
	xowl.getEntailmentFor(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			document.getElementById('field-entailment').value = content;
		}
	}, dbName);
	xowl.getDBProcedures(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			renderProcedures(content);
		}
	}, dbName);
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

function onDeleteProcedure(procedure) {
	var result = confirm("Remove procedure " + procedure.name + "?");
	if (!result)
		return;
	if (!onOperationRequest("Removing procedure " + procedure.name + " ..."))
		return;
	xowl.removeDBProcedure(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			displayMessage("success", "Removed procedure " + procedure.name + ".");
			waitAndGo("db.html?db=" + encodeURIComponent(dbName));
		}
	}, dbName, procedure.name);
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
}

function renderProcedureName(procedure) {
	var a = document.createElement("a");
	a.href = "procedure.html?db=" + encodeURIComponent(dbName) + "&procedure=" + encodeURIComponent(procedure.name);
	a.appendChild(document.createTextNode(procedure.name));
	return a;
}