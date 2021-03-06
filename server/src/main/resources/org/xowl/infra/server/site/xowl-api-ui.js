// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPLv3


/*****************************************************
 * Page management and setup
 ****************************************************/

/**
 * DOM node for the Title component
 */
var PAGE_COMPONENT_TITLE = null;
/**
 * DOM node for the Header component
 */
var PAGE_COMPONENT_HEADER = null;
/**
 * DOM node for the Footer component
 */
var PAGE_COMPONENT_FOOTER = null;
/**
 * The current breadcrumbs for the page
 */
var PAGE_BREADCRUMBS = [{name: "Home", uri: "/web/"}];
/**
 * The current xOWL object (access to the server API)
 */
var PLATFORM = null;
/**
 * The index to know whether the page is ready (ready on 100)
 */
var PAGE_READY_INDEX = 0;
/**
 * The hook to call when the page is ready
 */
var PAGE_READY_HOOK = null;
/**
 * Whether the current page is busy with a running operation
 * Null indicate that no operation is underway.
 * A non-null value is an object:
 * - count:   The number of concurrent ongoing operations.
 * - remover: The function to be called to remove the on-going message.
 */
var PAGE_BUSY = null;

/**
 * Get the value of an HTTP parameter
 *
 * @param name The name of the parameter to retrieve
 * @return The value associated to the parameter
 */
function getParameterByName(name) {
	name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
		results = regex.exec(location.search);
	return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

/**
 * Performs the initial setup of the current page
 *
 * @param platform	   The current xOWL platform object (access to the platform API)
 * @param mustBeLoggedIn Whether a user must be logged-in to see the page
 * @param breadcrumbs	The current breadcrumbs for the page
 * @param onReady		The hook to call when the page is ready
 */
function doSetupPage(platform, mustBeLoggedIn, breadcrumbs, onReady) {
	if (mustBeLoggedIn && (platform === null || !platform.isLoggedIn())) {
		document.location.href = "/web/login.html";
		return;
	}
	PLATFORM = platform;
	PAGE_BREADCRUMBS = PAGE_BREADCRUMBS.concat(breadcrumbs);
	PAGE_READY_HOOK = onReady;
	loadComponent("/web/components/title.html", function (node) {
		PAGE_COMPONENT_TITLE = node;
		doSetupHeader();
	});
	loadComponent("/web/components/header.html", function (node) {
		PAGE_COMPONENT_HEADER = node;
		doSetupHeader();
	});
	loadComponent("/web/components/footer.html", function (node) {
		PAGE_COMPONENT_FOOTER = node;
		doSetupFooter();
	});
}

function loadComponent(component, callback) {
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function () {
		if (xmlHttp.readyState == 4) {
			if (xmlHttp.status == 200) {
				var doc = document.implementation.createHTMLDocument("example");
				doc.documentElement.innerHTML = xmlHttp.responseText;
				var node = doc.documentElement.children[1].children[0].cloneNode(true);
				callback(node);
			}
		}
	}
	xmlHttp.open("GET", component, true);
	xmlHttp.setRequestHeader("Accept", "text/html");
	xmlHttp.send();
}

function doSetupHeader() {
	if (PAGE_COMPONENT_TITLE === null || PAGE_COMPONENT_HEADER === null)
		return;
	document.getElementById("placeholder-header").appendChild(PAGE_COMPONENT_HEADER);
	document.getElementById("placeholder-title").appendChild(PAGE_COMPONENT_TITLE);
	var breadcrumbs = document.getElementById("placeholder-breadcrumbs");
	for (var i = 0; i != PAGE_BREADCRUMBS.length; i++) {
		var name = PAGE_BREADCRUMBS[i].name;
		var uri = PAGE_BREADCRUMBS[i].uri;
		if (uri instanceof String || typeof uri === 'string') {
			var a = document.createElement("a");
			a.appendChild(document.createTextNode(name));
			a.href = uri;
			var li = document.createElement("li");
			li.appendChild(a);
			breadcrumbs.appendChild(li);
		} else {
			var li = document.createElement("li");
			li.appendChild(document.createTextNode(name));
			li.classList.add("active");
			breadcrumbs.appendChild(li);
		}
	}
	if (PLATFORM !== null && PLATFORM.isLoggedIn()) {
		var userLink = document.getElementById("placeholder-user");
		userLink.appendChild(document.createTextNode(PLATFORM.getLoggedInUser()));
		userLink.href = "/web/modules/account.html";
	}
	PAGE_READY_INDEX += 50;
	if (PAGE_READY_INDEX >= 100)
		PAGE_READY_HOOK();
}

function doSetupFooter() {
	if (PAGE_COMPONENT_FOOTER === null)
		return;
	document.getElementById("placeholder-footer").appendChild(PAGE_COMPONENT_FOOTER);
	PAGE_READY_INDEX += 50;
	if (PAGE_READY_INDEX >= 100)
		PAGE_READY_HOOK();
}

/**
 * Reacts to the user clicking on the logout button
 */
function onClickLogout() {
	PLATFORM.logout();
	document.location.href = "/web/login.html";
}



/*****************************************************
 * HTTP operations management
 ****************************************************/

/**
 * When an operation has been requested by the user
 *
 * @param message The message to display for the operation
 * @param count   The number of concurrent ongoing operations
 * @return Whether the operation can be performed
 */
function onOperationRequest(message, count) {
	if (PAGE_BUSY != null) {
		displayMessage("error", "Another operation is going on ...");
		return false;
	}
	var c = 1;
	if ((typeof count) !== "undefined") {
		c = count;
	}
	var remover = displayLoader(message);
	PAGE_BUSY = { count: c, remover: remover };
	return true;
}

/**
 * When the current operation was aborted
 *
 * @param message The message to display
 * @return Whether the operation was successful
 */
function onOperationAbort(message) {
	if (PAGE_BUSY == null) {
		displayMessage("error", "No on-going operation ...");
		return false;
	}
	PAGE_BUSY.count--;
	if (PAGE_BUSY.count === 0) {
		PAGE_BUSY.remover();
		PAGE_BUSY = null;
		displayMessage("error", message);
	}
	return true;
}

/**
 * When an operation ended
 *
 * @param code		  The HTTP code (other that 200 - OK)
 * @param content	   The content of the HTTP response
 * @param customMessage A custom message to override the default one (may be undefined)
 * @return Whether the operation was successful
 */
function onOperationEnded(code, content, customMessage) {
	if (PAGE_BUSY == null) {
		displayMessage("error", "No on-going operation ...");
		return false;
	}
	PAGE_BUSY.count--;
	if (PAGE_BUSY.count === 0) {
		PAGE_BUSY.remover();
		PAGE_BUSY = null;
	}
	if (code != 200) {
		if ((typeof customMessage) === "undefined")
			displayMessageHttpError(code, content);
		else
			displayMessage("error", customMessage);
		if (code === 401 || code === 440)
			waitAndGo("/web/login.html?next=" + encodeURIComponent(window.location.pathname + window.location.search));
	}
	return (code === 200);
}



/*****************************************************
 * Message display
 ****************************************************/

/**
 * Displays a message for loading
 *
 * @param message The message to display
 * @return A function that can be called to remove the loading message
 */
function displayLoader(message) {
	var image = document.createElement("img");
	image.src = "/web/assets/spinner.gif";
	image.width = 32;
	image.height = 32;
	image.classList.add("header-message-icon");
	var content = renderMessage(message);
	var row = document.createElement("div");
	row.classList.add("header-message");
	row.appendChild(image);
	row.appendChild(content);
	var rows = document.getElementById("placeholder-messages");
	rows.appendChild(row);
	return function () {
		rows.removeChild(row);
	}
}

/**
 * Displays an information message
 *
 * @param type	The type of message (info, success, warning, error)
 * @param message The message to display
 */
function displayMessage(type, message) {
	var image = document.createElement("img");
	image.src = "/web/assets/message-" + type + ".svg";
	image.width = 32;
	image.height = 32;
	image.classList.add("header-message-icon");
	var content = renderMessage(message);
	var button = document.createElement("span");
	button.appendChild(document.createTextNode("×"));
	button.classList.add("header-message-button");
	var row = document.createElement("div");
	row.classList.add("header-message");
	row.classList.add("header-message-" + type);
	row.appendChild(image);
	row.appendChild(content);
	row.appendChild(button);
	var rows = document.getElementById("placeholder-messages");
	rows.appendChild(row);
	button.onclick = function () {
		rows.removeChild(row);
	}
}

/**
 * Displays an error message for a failed HTTP request
 *
 * @param code	The HTTP code (other that 200 - OK)
 * @param content The content of the HTTP response
 */
function displayMessageHttpError(code, content) {
	var message = null;
	switch (code) {
		case 400:
			message = "There is a problem with the request, see details.";
			break;
		case 401:
			message =  "You must be logged in to perform this operation.";
			break;
		case 403:
			message =  "You are not authorized to perform this operation.";
			break;
		case 404:
			message =  "Can't find the requested data.";
			break;
		case 440:
			message =  "The session has expired, login again to continue.";
			break;
		case 461:
			message =  "The SPARQL query failed.";
			break;
		case 500:
			message =  "An unexpected error occurred on the server.";
			break;
		case 501:
			message =  "This operation is not supported.";
			break;
		case 560:
			message =  "An unknown error error occurred on the server.";
			break;
		default:
			message =  "The connection failed." + "(" + code + ")";
			break;
	}
	if (content != null && (content instanceof String || typeof content === 'string')) {
		message += "\n" + content;
	}
	displayMessage("error", message);
}

/**
 * Renders in HTML the specified message
 *
 * @param message The message to render, may be a complex RichText message
 * @return The HTML DOM element corresponding to the rendered message
 */
function renderMessage(message) {
	var result = document.createElement("p");
	result.classList.add("header-message-content");
	if (message instanceof String || typeof message === 'string') {
		result.appendChild(renderMessagePart(message));
	} else {
		result.appendChild(document.createTextNode(JSON.stringify(message)));
	}
	return result;
}

/**
 * Renders in HTML the specified message part
 *
 * @param message The message to part render
 * @return The HTML DOM element corresponding to the rendered message part
 */
function renderMessagePart(part) {
	if (part instanceof String || typeof part === 'string') {
		var parts = part.split("\n");
		if (parts.length > 0) {
			var dom = document.createElement("span");
			dom.appendChild(document.createTextNode(parts[0]));
			for (var i = 1; i != parts.length; i++) {
				dom.appendChild(document.createElement("br"));
				dom.appendChild(document.createTextNode(parts[i]));
			}
			return dom;
		} else {
			return document.createTextNode(part);
		}
	}
	return document.createTextNode(JSON.stringify(part));
}

/**
 * Waits for a small time (1.5s) and then refreshes the current page
 */
function waitAndRefresh() {
	window.setTimeout(function () {
		window.location.reload(true);
	}, 1500);
}

/**
 * Waits for a small time (1.5s) and then go to the specified reference
 *
 * @param target The target reference to go to after the wait
 */
function waitAndGo(target) {
	window.setTimeout(function () {
		window.location.href = target;
	}, 1500);
}



/*****************************************************
 * RDF rendering
 ****************************************************/

/*
 * The known MIME types for the RDF and OWL datasets
 */
var MIME_TYPES = [
	{ name: 'N-Triples', value: 'application/n-triples', extensions: ['.nt'] },
	{ name: 'N-Quads', value: 'application/n-quads', extensions: ['.nq'] },
	{ name: 'Turtle', value: 'text/turtle', extensions: ['.ttl'] },
	{ name: 'TriG', value: 'application/trig', extensions: ['.trig'] },
	{ name: 'JSON-LD', value: 'application/ld+json', extensions: ['.jsonld'] },
	{ name: 'RDF/XML', value: 'application/rdf+xml', extensions: ['.rdf'] },
	{ name: 'Functional OWL2', value: 'text/owl-functional', extensions: ['.ofn', '.fs'] },
	{ name: 'OWL/XML', value: 'application/owl+xml', extensions: ['.owx', '.owl'] },
	{ name: 'xOWL - Executable RDF', value: 'application/x-xowl-xrdf', extensions: ['.xrdf'] },
	{ name: 'xOWL - Executable OWL', value: 'application/x-xowl-xowl', extensions: ['.xowl'] }
];

/*
 * The defaults known URI mappings
 */
var DEFAULT_URI_MAPPINGS = [
	["rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"],
	["rdfs", "http://www.w3.org/2000/01/rdf-schema#"],
	["xsd", "http://www.w3.org/2001/XMLSchema#"],
	["owl", "http://www.w3.org/2002/07/owl#"]];

/*
 * Gets the short URI (prefix:suffix) for the specified full URI
 *
 * @param value The full URI
 * @return The associated short URI (or the full URI if it cannot be shortened)
 */
function getShortURI(value) {
	for (var i = 0; i != DEFAULT_URI_MAPPINGS.length; i++) {
		if (value.indexOf(DEFAULT_URI_MAPPINGS[i][1]) === 0) {
			return DEFAULT_URI_MAPPINGS[i][0] + ":" + value.substring(DEFAULT_URI_MAPPINGS[i][1].length);
		}
	}
	return value;
}

/*
 * Gets the HTML DOM object rendering the specified RDF node
 *
 * @param value An RDF node (represented as a Javascript object)
 * @return The HTML DOM rendering of the node
 */
function renderRdfNode(value) {
	if (value.type === "uri") {
		var dom = document.createElement("a");
		dom.appendChild(document.createTextNode(getShortURI(value.value)));
		dom.classList.add("rdfIRI");
		return dom;
	} else if (value.type === "bnode") {
		var dom = document.createElement("span");
		dom.appendChild(document.createTextNode('_:' + value.value));
		dom.classList.add("rdfBlank");
		return dom;
	} else if (value.type === "literal") {
		var span1 = document.createElement("span");
		span1.appendChild(document.createTextNode('"' + value.value + '"'));
		var dom = document.createElement("span");
		dom.classList.add("rdfLiteral");
		dom.appendChild(span1);
		if (value.datatype !== null) {
			dom.appendChild(document.createTextNode("^^<"));
			var link = document.createElement("a");
			link.appendChild(document.createTextNode(getShortURI(value.datatype)));
			link.classList.add("rdfIRI");
			dom.appendChild(link);
			dom.appendChild(document.createTextNode(">"));
		}
		if (value["xml:lang"] !== null) {
			var span2 = document.createElement("span");
			span2.appendChild(document.createTextNode('@' + value.lang));
			span2.classList.add("badge");
			dom.appendChild(span2);
		}
		return dom;
	} else if (value.type === "variable") {
		var dom = document.createElement("span");
		dom.appendChild(document.createTextNode('?' + value.value));
		dom.classList.add("rdfVariable");
		return dom;
	} else if (value.type === "anon") {
		var dom = document.createElement("span");
		dom.appendChild(document.createTextNode('_:' + value.value));
		dom.classList.add("rdfAnonymous");
		return dom;
	} else if (value.type === "dynamic") {
		var dom = document.createElement("span");
		dom.appendChild(document.createTextNode('$ ' + value.value));
		dom.classList.add("rdfDynamic");
		return dom;
	}
	return null;
}

/*
 * Renders an array of RDF nodes
 *
 * @param nodes     The RDF nodes to render
 * @param injectRow The function to call when injecting a row into the DOM
 */
function renderRdfNodes(nodes, injectRow) {
	var row = document.createElement("tr");
	var cell = document.createElement("td");
	for (var i = 0; i != nodes.length; i++) {
		cell = document.createElement("td");
		if (nodes[i] !== "")
			cell.appendChild(renderRdfNode(nodes[i]));
		row.appendChild(cell);
	}
	injectRow(row);
}

/*
 * Renders RDF quads
 *
 * @param data      The object representing the quads
 * @param injectRow The function to call when injecting a row into the DOM
 */
function renderRdfQuads(data, injectRow) {
	for (var g = 0; g != data.length; g++) {
		var dataGraph = data[g];
		var nodeGraph = dataGraph.graph;
		for (var e = 0; e != dataGraph.entities.length; e++) {
			var dataEntity = dataGraph.entities[e];
			var nodeSubject = dataEntity.subject;
			for (var p = 0; p != dataEntity.properties.length; p++) {
				var dataProperty = dataEntity.properties[p];
				var nodeProperty = dataProperty.property;
				for (var v = 0; v != dataProperty.values.length; v++) {
					var dataValue = dataProperty.values[v];
					if (Array.isArray(dataValue)) {
						var root = renderRdfList(nodeGraph, dataValue, injectRow);
						renderRdfNodes([nodeGraph, nodeSubject, nodeProperty, root], injectRow);
					} else {
						renderRdfNodes([nodeGraph, nodeSubject, nodeProperty, dataValue], injectRow);
					}
				}
			}
		}
	}
}

/*
 * Renders a RDF list
 *
 * @param nodeGraph The node for the graph
 * @param values    The values in the list
 * @param injectRow The function to call when injecting a row into the DOM
 * @return The list's root node
 */
function renderRdfList(nodeGraph, values, injectRow) {
	if (values.length == 0)
		return {"type": "uri", "value": "http://www.w3.org/1999/02/22-rdf-syntax-ns#nil"};
	var current = newRdfBlank();
	var root = current;
	for (var i = 0; i != values.length - 1; i++) {
		var follower = newRdfBlank();
		renderRdfNodes([
			nodeGraph,
			current,
			{"type": "uri", "value": "http://www.w3.org/1999/02/22-rdf-syntax-ns#first"},
			values[i]], injectRow);
		renderRdfNodes([
			nodeGraph,
			current,
			{"type": "uri", "value": "http://www.w3.org/1999/02/22-rdf-syntax-ns#rest"},
			follower], injectRow);
		current = follower;
	}
	renderRdfNodes([
		nodeGraph,
		current,
		{"type": "uri", "value": "http://www.w3.org/1999/02/22-rdf-syntax-ns#first"},
		values[values.length - 1]], injectRow);
	renderRdfNodes([
		nodeGraph,
		current,
		{"type": "uri", "value": "http://www.w3.org/1999/02/22-rdf-syntax-ns#rest"},
		{"type": "uri", "value": "http://www.w3.org/1999/02/22-rdf-syntax-ns#nil"}], injectRow);
	return root;
}

/*
 * The identifier for the next blank node to be generated
 */
var NEXT_BLANK = 0;

/*
 * Generates a new blank node
 *
 * @return The new blank node
 */
function newRdfBlank() {
	var node = {
		"type": "bnode",
		"value": "list_" + NEXT_BLANK.toString()
	};
	NEXT_BLANK++;
	return node;
}