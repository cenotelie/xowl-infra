/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

/**
 * Represents the expected callback for request to an xOWL enpoint
 * @callback commandCallback
 * @param {number} code - The response code
 * @param {string} type - The response content type
 * @param {string} content - The response content
 */

/**
 * Represents the expected callback for request of a list to an xOWL enpoint
 * @callback listCallback
 * @param {number} code - The response code
 * @param {string} type - The response content type
 * @param {string[]} content - The response content
 */

/**
 * Represents the expected callback for request of the privileges of a user
 * @callback userPrivCallback
 * @param {number} code - The response code
 * @param {string} type - The response content type
 * @param {Object} content - The response content
 * @param {boolean} content.isServerAdmin - Whether the user is a server administrator
 * @param {Object[]} content.accesses - The privileges granted to the user
 * @param {string} content.accesses[].database - The database for this privilege
 * @param {boolean} content.accesses[].isAdmin - Whether the user has admin privileges
 * @param {boolean} content.accesses[].canWrite - Whether the user can write to the database
 * @param {boolean} content.accesses[].canRead - Whether the user can read from the database
 */

/**
 * Represents the expected callback for request of the privileges of a database
 * @callback dbPrivCallback
 * @param {number} code - The response code
 * @param {string} type - The response content type
 * @param {Object[]} content - The response content
 * @param {string} content[].user - The user that has access
 * @param {boolean} content[].isAdmin - Whether the user has admin privileges
 * @param {boolean} content[].canWrite - Whether the user can write to the database
 * @param {boolean} content[].canRead - Whether the user can read from the database
 */

/**
 * Represents the expected callback for request of the matching status of a rule
 * @callback statusCallback
 * @param {number} code - The response code
 * @param {string} type - The response content type
 * @param {Object[]} steps - The matching steps
 * @param {Object} steps[].pattern.subject - The subject of the pattern matched at this step
 * @param {Object} steps[].pattern.property - The property of the pattern matched at this step
 * @param {Object} steps[].pattern.object - The object of the pattern matched at this step
 * @param {Object} steps[].pattern.graph - The graph of the pattern matched at this step
 * @param {Object[]} steps[].bindings - The bindings at this step
 */

/**
 * Represents the expected callback for request of the explanation of a quad
 * @callback explainCallback
 * @param {number} code - The response code
 * @param {string} type - The response content type
 * @param {Object} content - The explanation
 * @param {number} content.root - The index of the root explanation node
 * @param {Object[]} content.nodes - The explanation nodes
 */

/**
 * The MIME types of the recognized files
 * @const
 */
var XOWL_MIME_TYPES = [
	{ name: 'N-Triples', value: 'application/n-triples', extensions: ['.nt'] },
	{ name: 'N-Quads', value: 'application/n-quads', extensions: ['.nq'] },
	{ name: 'Turtle', value: 'text/turtle', extensions: ['.ttl'] },
	{ name: 'TriG', value: 'application/trig', extensions: ['.trig'] },
	{ name: 'JSON-LD', value: 'application/ld+json', extensions: ['.jsonld'] },
	{ name: 'RDF/XML', value: 'application/rdf+xml', extensions: ['.rdf'] },
	{ name: 'Functional OWL2', value: 'text/owl-functional', extensions: ['.ofn', '.fs'] },
	{ name: 'OWL/XML', value: 'application/owl+xml', extensions: ['.owx', '.owl'] },
	{ name: 'xOWL RDF Rules', value: 'application/x-xowl-rdft', extensions: ['.rdft'] },
	{ name: 'xOWL Ontology', value: 'application/x-xowl', extensions: ['.xowl'] }
];

/**
 * Creates a new XOWL connection
 * @class
 * @param {string} [endpoint] - The xOWL server endpoint to use, defaults to '/api'
 * @param {boolean} [useLocal] - Whether to use the browser local storage for persistency, default to true
 * @return {void}
 */
function XOWL(endpoint, useLocal) {
	this.endpoint = (!endpoint) ? '/api' : endpoint;
	this.useLocal = (!useLocal) ? true : useLocal;
	if (this.useLocal) {
		this.authToken = localStorage.getItem('xowl.authToken');
		this.userName = localStorage.getItem('xowl.userName');
	} else {
		this.authToken = null;
		this.userName = null;
	}
}

/**
 * Tries to login onto the xOWL server endpoint.
 * If successful, the authentication token is kept.
 * @method login
 * @param {commandCallback} callback - The callback for this request
 * @param {string} login - The user login
 * @param {string} password - The user password
 */
XOWL.prototype.login = function (callback, login, password) {
	var _self = this;
	var token = window.btoa(unescape(encodeURIComponent(login + ':' + password)));
	this.authToken = token;
	this.command(function (code, type, content) {
		if (code === 200) {
			_self.authToken = token;
			_self.userName = login;
			if (_self.useLocal) {
				localStorage.setItem('xowl.authToken', token);
				localStorage.setItem('xowl.userName', login);
			}
			callback(code, type, content);
		} else {
			_self.authToken = null;
			_self.userName = null;
			if (_self.useLocal) {
				localStorage.removeItem('xowl.authToken');
				localStorage.removeItem('xowl.userName');
			}
			callback(code, type, content);
		}
	}, "WHOAMI");
}

/**
 * Clears up any stored information about a logged-in user, if any
 * @method logout
 */
XOWL.prototype.logout = function () {
	this.authToken = null;
	this.userName = null;
	if (this.useLocal) {
		localStorage.removeItem('xowl.authToken');
		localStorage.removeItem('xowl.userName');
	}
}

/**
 * Request the xOWL server shutdown
 * @method serverShutdown
 * @param {commandCallback} callback - The callback for this request
 */
XOWL.prototype.serverShutdown = function (callback) {
	this.command(callback, "ADMIN SHUTDOWN");
}

/**
 * Request the xOWL server restart
 * @method serverRestart
 * @param {commandCallback} callback - The callback for this request
 */
XOWL.prototype.serverRestart = function (callback) {
	this.command(callback, "ADMIN RESTART");
}

/**
 * Requests the list of the users on the xOWL server
 * @param getUsers
 * @param {listCallback} callback - The callback for this request
 */
XOWL.prototype.getUsers = function (callback) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "ADMIN LIST USERS");
}

/**
 * Request the creation of a new user
 * @method createUser
 * @param {commandCallback} callback - The callback for this request
 * @param {string} login - The login for the new user
 * @param {string} pw - The password for the new user
 */
XOWL.prototype.createUser = function (callback, login, pw) {
	this.command(callback, "ADMIN CREATE USER " + login + " " + pw);
}

/**
 * Request the deletion of a user
 * @method deleteUser
 * @param {commandCallback} callback - The callback for this request
 * @param {string} login - The login of the user to delete
 */
XOWL.prototype.deleteUser = function (callback, login) {
	this.command(callback, "ADMIN DELETE USER " + login);
}

/**
 * Request the change of the password for the current user (identified by the current auth token)
 * @method changePassword
 * @param {commandCallback} callback - The callback for this request
 * @param {string} pw - The new password for the user
 */
XOWL.prototype.changePassword = function (callback, pw) {
	this.command(callback, "ADMIN CHANGE PASSWORD " + pw);
}

/**
 * Request the reset of the password of another user
 * @method resetPassword
 * @param {commandCallback} callback - The callback for this request
 * @param {string} login - The login of the user to reset the password for
 * @param {string} pw - The new password for the user
 */
XOWL.prototype.resetPassword = function (callback, login, pw) {
	this.command(callback, "ADMIN RESET PASSWORD " + login + " " + pw);
}

/**
 * Requests the list of privileges for a user
 * @param getUserPrivileges
 * @param {userPrivCallback} callback - The callback for this request
 * @param {string} login - The login of the user
 */
XOWL.prototype.getUserPrivileges = function (callback, login) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "ADMIN PRIVILEGES FOR " + login);
}

/**
 * Requests the list of privileges on a database
 * @param getDatabasePrivileges
 * @param {dbPrivCallback} callback - The callback for this request
 * @param {string} db - The target database
 */
XOWL.prototype.getDatabasePrivileges = function (callback, db) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "ADMIN PRIVILEGES ON " + db);
}

/**
 * Requests the grant of a privilege to a user for a database
 * @param grantDB
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} right - The right to grant, one of ADMIN, WRITE and READ
 * @param {string} login - The login of the user
 */
XOWL.prototype.grantDB = function (callback, db, right, login) {
	this.command(callback, "ADMIN GRANT " + right + " " + login + " " + db);
}

/**
 * Requests the revoke of a privilege to a user for a database
 * @param revokeDB
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} right - The right to revoke, one of ADMIN, WRITE and READ
 * @param {string} login - The login of the user
 */
XOWL.prototype.revokeDB = function (callback, db, right, login) {
	this.command(callback, "ADMIN REVOKE " + right + " " + login + " " + db);
}

/**
 * Requests the grant of server administration privileges to a user
 * @param grantServerAdmin
 * @param {commandCallback} callback - The callback for this request
 * @param {string} login - The login of the user
 */
XOWL.prototype.grantServerAdmin = function (callback, login) {
	this.command(callback, "ADMIN GRANT SERVER ADMIN " + login);
}

/**
 * Requests the revoke of server administration privileges to a user
 * @param revokeServerAdmin
 * @param {commandCallback} callback - The callback for this request
 * @param {string} login - The login of the user
 */
XOWL.prototype.revokeServerAdmin = function (callback, login) {
	this.command(callback, "ADMIN REVOKE SERVER ADMIN " + login);
}

/**
 * Requests the list of the databases on the xOWL server
 * @param getDatabases
 * @param {listCallback} callback - The callback for this request
 */
XOWL.prototype.getDatabases = function (callback) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "ADMIN LIST DATABASES");
}

/**
 * Requests the creation of a new database
 * @param createDatabase
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The name of the database to create
 */
XOWL.prototype.createDatabase = function (callback, db) {
	this.command(callback, "ADMIN CREATE DATABASE " + db);
}

/**
 * Requests the drop of a database
 * @param dropDatabase
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The name of the database to drop
 */
XOWL.prototype.dropDatabase = function (callback, db) {
	this.command(callback, "ADMIN DROP DATABASE " + db);
}

/**
 * Gets the entailment regime currently activated on a database
 * @param getEntailmentFor
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The name of the database
 */
XOWL.prototype.getEntailmentFor = function (callback, db) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "DATABASE " + db + " ENTAILMENT");
}

/**
 * Sets the entailment regime on a database
 * @param setEntailmentFor
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The name of the database
 * @param {string} regime - The regime to activate, one of: none, simple, RDF, RDFS, OWL2_RDF, OWL2_DIRECT
 */
XOWL.prototype.setEntailmentFor = function (callback, db, regime) {
	this.command(callback, "DATABASE " + db + " ENTAILMENT " + regime);
}

/**
 * Requests the list of all the rules (active or not) in a database
 * @param getDBRules
 * @param {listCallback} callback - The callback for this request
 * @param {string} db - The target database
 */
XOWL.prototype.getDBRules = function (callback, db) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "DATABASE " + db + " LIST RULES");
}

/**
 * Requests the list of all the active rules in a database
 * @param getDBActiveRules
 * @param {listCallback} callback - The callback for this request
 * @param {string} db - The target database
 */
XOWL.prototype.getDBActiveRules = function (callback, db) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "DATABASE " + db + " LIST ACTIVE RULES");
}

/**
 * Adds a new (inactive) rule to a database
 * @param addDBRule
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} rule - The definition of the rule to add
 */
XOWL.prototype.addDBRule = function (callback, db, rule) {
	this.command(callback, "DATABASE " + db + " ADD RULE " + rule);
}

/**
 * Removes an existing rule from a database. If the rule is active it is first deactivated.
 * @param removeDBRule
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} rule - The URI of the rule to remove
 */
XOWL.prototype.removeDBRule = function (callback, db, rule) {
	this.command(callback, "DATABASE " + db + " REMOVE RULE " + rule);
}

/**
 * Activates an existing inactive rule in a database
 * @param activateDBRule
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} rule - The URI of the rule to activate
 */
XOWL.prototype.activateDBRule = function (callback, db, rule) {
	this.command(callback, "DATABASE " + db + " ACTIVATE " + rule);
}

/**
 * Deactivates an existing active rule in a database
 * @param deactivateDBRule
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} rule - The URI of the rule to deactivate
 */
XOWL.prototype.deactivateDBRule = function (callback, db, rule) {
	this.command(callback, "DATABASE " + db + " DEACTIVATE " + rule);
}

/**
 * Gets wether a database rule is currently active
 * @param isDBRuleActive
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} rule - The URI of the rule
 */
XOWL.prototype.isDBRuleActive = function (callback, db, rule) {
	this.command(callback, "DATABASE " + db + " IS ACTIVE " + rule);
}

/**
 * Gets the definition of a database rule
 * @param getDBRuleDefinition
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} rule - The URI of the rule
 */
XOWL.prototype.getDBRuleDefinition = function (callback, db, rule) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "DATABASE " + db + " RULE " + rule);
}

/**
 * Requests the matching status of a rule in a database
 * @param getDBRuleStatus
 * @param {statusCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} rule - The URI of the rule
 */
XOWL.prototype.getDBRuleStatus = function (callback, db, rule) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "DATABASE " + db + " STATUS " + rule);
}

/**
 * Requests the explanation of the how a quad has been produced in a database
 * @param explainQuad
 * @param {explainCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} quad - The quad to explain
 */
XOWL.prototype.explainQuad = function (callback, db, quad) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "DATABASE " + db + " EXPLAIN " + quad);
}

/**
 * Executes a xOWL Server Protocol command
 * @method jsCommand
 * @param {commandCallback} callback - The callback for this request
 * @param {string} command - The command for the server
 */
XOWL.prototype.command = function (callback, command) {
	this.jsCommand(callback, command);
}

/**
 * Executes a SPARQL query on a database
 * @method sparql
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} sparql - The SPARQL query
 */
XOWL.prototype.sparql = function (callback, db, sparql) {
	this.jsSPARQL(callback, db, sparql);
}

/**
 * Uploads into a database a piece of content
 * @method upload
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} contentType - The MIME type of the content to upload
 * @param {string} content - The content to upload to the database
 */
XOWL.prototype.upload = function (callback, db, contentType, content) {
	this.jsUpload(callback, db, contentType, content);
}

/**
 * Executes a xOWL Server Protocol command (pure JS with XMLHttpRequest)
 * @method jsCommand
 * @param {commandCallback} callback - The callback for this request
 * @param {string} command - The command for the server
 */
XOWL.prototype.jsCommand = function (callback, command) {
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function () {
		if (xmlHttp.readyState == 4) {
			var ct = xmlHttp.getResponseHeader("Content-Type");
			callback(xmlHttp.status, ct, xmlHttp.responseText)
		}
	}
	xmlHttp.open("POST", this.endpoint, true);
	xmlHttp.setRequestHeader("Accept", "text/plain, application/json");
	xmlHttp.setRequestHeader("Content-Type", "application/x-xowl-xsp");
	if (this.authToken !== null)
		xmlHttp.setRequestHeader("Authorization", "Basic " + this.authToken);
	xmlHttp.send(command);
}

/**
 * Executes a SPARQL query on a database (pure JS with XMLHttpRequest)
 * @method jsSPARQL
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} sparql - The SPARQL query
 */
XOWL.prototype.jsSPARQL = function (callback, db, sparql) {
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function () {
		if (xmlHttp.readyState == 4) {
			var ct = xmlHttp.getResponseHeader("Content-Type");
			callback(xmlHttp.status, ct, xmlHttp.responseText)
		}
	}
	xmlHttp.open("POST", this.endpoint + "/db/" + db + "/", true);
	xmlHttp.setRequestHeader("Accept", "application/n-quads, application/sparql-results+json");
	xmlHttp.setRequestHeader("Content-Type", "application/sparql-query");
	if (this.authToken !== null)
		xmlHttp.setRequestHeader("Authorization", "Basic " + this.authToken);
	xmlHttp.send(sparql);
}

/**
 * Uploads into a database a piece of content (pure JS with XMLHttpRequest)
 * @method jsUpload
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} contentType - The MIME type of the content to upload
 * @param {string} content - The content to upload to the database
 */
XOWL.prototype.jsUpload = function (callback, db, contentType, content) {
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function () {
		if (xmlHttp.readyState == 4) {
			var ct = xmlHttp.getResponseHeader("Content-Type");
			callback(xmlHttp.status, ct, xmlHttp.responseText);
		}
	}
	xmlHttp.upload.onprogress = function (event) {
		var ratio = event.loaded / event.total;
		callback(0, null, ratio);
	};
	xmlHttp.open("POST", this.endpoint + "/db/" + db + "/", true);
	xmlHttp.setRequestHeader("Accept", "text/plain, application/json");
	xmlHttp.setRequestHeader("Content-Type", contentType);
	if (this.authToken !== null)
		xmlHttp.setRequestHeader("Authorization", "Basic " + this.authToken);
	xmlHttp.send(content);
}