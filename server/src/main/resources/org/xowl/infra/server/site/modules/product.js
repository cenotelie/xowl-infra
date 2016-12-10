// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

var xowl = new XOWL();

function init() {
	doSetupPage(xowl, true, [{name: "About ..."}], function() {
		doGetData();
	});
}

function doGetData() {
	if (!onOperationRequest("Loading ..."))
		return;
	xowl.getServerProduct(function (status, type, content) {
		if (onOperationEnded(status, content)) {
			renderProduct(content);
		}
	});
}

function renderProduct(product) {
	document.getElementById("field-identifier").value = product.identifier;
	document.getElementById("field-name").value = product.name;
	document.getElementById("field-version-number").value = product.version.number;
	document.getElementById("field-version-scm-tag").value = product.version.scmTag;
	document.getElementById("field-version-build-user").value = product.version.buildUser;
	document.getElementById("field-version-build-tag").value = product.version.buildTag;
	document.getElementById("field-version-build-timestamp").value = product.version.buildTimestamp;
	document.getElementById("field-copyright").value = product.copyright;
	document.getElementById("field-vendor").value = product.vendor;
	document.getElementById("field-license-name").value = product.license.name;
	document.getElementById("field-license-text").value = product.license.fullText;
}
