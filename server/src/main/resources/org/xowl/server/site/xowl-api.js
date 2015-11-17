/**
 * The default xOWL endpoint
 */
var XOWL_ENDPOINT = "/api";

/**
 * The current authentication token
 */
var XOWL_AUTH_TOKEN = null;

/**
 * The angular $http service to use for HTTP requests, if any
 */
var XOWL_ANGULAR_HTTP = null;


/**
 * Represents the expected callback for request to an xOWL enpoint
 * @callback commandCallback
 * @param {number} code - The response code
 * @param {string} type - The response content type
 * @param {string} content - The response content
 */

/**
 * Request the xOWL server shutdown
 * @method xowlServerShutdown
 * @param {commandCallback} callback - The callback for this request
 */
function xowlServerShutdown(callback) {
	xowlCommand(callback, "ADMIN SHUTDOWN");
}

/**
 * Request the xOWL server restart
 * @method xowlServerRestart
 * @param {commandCallback} callback - The callback for this request
 */
function xowlServerRestart(callback) {
	xowlCommand(callback, "ADMIN RESTART");
}

/**
 * Represents the expected callback for request of a list to an xOWL enpoint
 * @callback listCallback
 * @param {number} code - The response code
 * @param {string} type - The response content type
 * @param {string[]} content - The response content
 */

/**
 * Requests the list of the users on the xOWL server
 * @param xowlListUsers
 * @param {listCallback} callback - The callback for this request
 */
function xowlListUsers(callback) {
	xowlCommand(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).results);
		} else {
			callback(code, type, content);
		}
	}, "ADMIN LIST USERS");
}

/**
 * Request the creation of a new user
 * @method xowlCreateUser
 * @param {commandCallback} callback - The callback for this request
 * @param {string} login - The login for the new user
 * @param {string} pw - The password for the new user
 */
function xowlCreateUser(callback, login, pw) {
	xowlCommand(callback, "ADMIN CREATE USER " + login + " " + pw);
}

/**
 * Request the deletion of a new user
 * @method xowlDeleteUser
 * @param {commandCallback} callback - The callback for this request
 * @param {string} login - The login of the user to delete
 */
function xowlDeleteUser(callback, login) {
	xowlCommand(callback, "ADMIN DELETE USER " + login);
}

/**
 * Request the change of the password for the current user (identified by the current auth token)
 * @method xowlChangePassword
 * @param {commandCallback} callback - The callback for this request
 * @param {string} pw - The new password for the user
 */
function xowlChangePassword(callback, pw) {
	xowlCommand(callback, "ADMIN CHANGE PASSWORD " + pw);
}

/**
 * Request the reset of the password of another user
 * @method xowlResetPassword
 * @param {commandCallback} callback - The callback for this request
 * @param {string} login - The login of the user to reset the password for
 * @param {string} pw - The new password for the user
 */
function xowlResetPassword(callback, login, pw) {
	xowlCommand(callback, "ADMIN RESET PASSWORD " + login + " " + pw);
}

/**
 * Represents the expected callback for request of the privileges of a user
 * @callback privCallback
 * @param {number} code - The response code
 * @param {string} type - The response content type
 * @param {Object[]} content - The response content
 * @param {string} content[].database - The database for this privilege
 * @param {boolean} content[].isAdmin - Whether the user has admin privileges
 * @param {boolean} content[].canWrite - Whether the user can write to the database
 * @param {boolean} content[].canRead - Whether the user can read from the database
 */

/**
 * Requests the list of privileges for a user
 * @param xowlUserPrivileges
 * @param {privCallback} callback - The callback for this request
 * @param {string} login - The login of the user
 */
function xowlUserPrivileges(callback, login) {
	xowlCommand(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).results);
		} else {
			callback(code, type, content);
		}
	}, "ADMIN PRIVILEGES " + login);
}

/**
 * Requests the grant of a privilege to a user for a database
 * @param xowlGrantDB
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} right - The right to grant, one of ADMIN, WRITE and READ
 * @param {string} login - The login of the user
 */
function xowlGrantDB(callback, db, right, login) {
	xowlCommand(callback, "ADMIN GRANT " + right + " " + login + " " + db);
}

/**
 * Requests the revoke of a privilege to a user for a database
 * @param xowlRevokeDB
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} right - The right to revoke, one of ADMIN, WRITE and READ
 * @param {string} login - The login of the user
 */
function xowlRevokeDB(callback, db, right, login) {
	xowlCommand(callback, "ADMIN REVOKE " + right + " " + login + " " + db);
}

/**
 * Requests the grant of server administration privileges to a user
 * @param xowlGrantServerAdmin
 * @param {commandCallback} callback - The callback for this request
 * @param {string} login - The login of the user
 */
function xowlGrantServerAdmin(callback, login) {
	xowlCommand(callback, "ADMIN GRANT SERVER ADMIN " + login);
}

/**
 * Requests the revoke of server administration privileges to a user
 * @param xowlRevokeServerAdmin
 * @param {commandCallback} callback - The callback for this request
 * @param {string} login - The login of the user
 */
function xowlRevokeServerAdmin(callback, login) {
	xowlCommand(callback, "ADMIN REVOKE SERVER ADMIN " + login);
}

/**
 * Requests the list of the databases on the xOWL server
 * @param xowlListDatabases
 * @param {listCallback} callback - The callback for this request
 */
function xowlListDatabases(callback) {
	xowlCommand(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).results);
		} else {
			callback(code, type, content);
		}
	}, "ADMIN LIST DATABASES");
}

/**
 * Requests the creation of a new database
 * @param xowlCreateDatabase
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The name of the database to create
 */
function xowlCreateDatabase(callback, db) {
	xowlCommand(callback, "ADMIN CREATE DATABASE " + db);
}

/**
 * Requests the drop of a database
 * @param xowlDropDatabase
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The name of the database to drop
 */
function xowlDropDatabase(callback, db) {
	xowlCommand(callback, "ADMIN DROP DATABASE " + db);
}

/**
 * Gets the entailment regime currently activated on a database
 * @param xowlGetDBEntailment
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The name of the database
 */
function xowlGetDBEntailment(callback, db) {
	xowlCommand(callback, "DATABASE " + db + " ENTAILMENT");
}

/**
 * Sets the entailment regime on a database
 * @param xowlGetDBEntailment
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The name of the database
 * @param {string} regime - The regime to activate, one of: none, simple, RDF, RDFS, OWL2_RDF, OWL2_DIRECT
 */
function xowlSetDBEntailment(callback, db, regime) {
	xowlCommand(callback, "DATABASE " + db + " ENTAILMENT " + regime);
}

/**
 * Requests the list of all the rules (active or not) in a database
 * @param xowlListDBRules
 * @param {listCallback} callback - The callback for this request
 * @param {string} db - The target database
 */
function xowlListDBRules(callback, db) {
	xowlCommand(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).results);
		} else {
			callback(code, type, content);
		}
	}, "DATABASE " + db + " LIST RULES");
}

/**
 * Requests the list of all the active rules in a database
 * @param xowlListDBActiveRules
 * @param {listCallback} callback - The callback for this request
 * @param {string} db - The target database
 */
function xowlListDBActiveRules(callback, db) {
	xowlCommand(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).results);
		} else {
			callback(code, type, content);
		}
	}, "DATABASE " + db + " LIST ACTIVE RULES");
}

/**
 * Adds a new (inactive) rule to a database
 * @param xowlGetDBEntailment
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} rule - The definition of the rule to add
 */
function xowlAddDBRule(callback, db, rule) {
	xowlCommand(callback, "DATABASE " + db + " ADD RULE " + rule);
}

/**
 * Removes an existing rule from a database. If the rule is active it is first deactivated.
 * @param xowlRemoveDBRule
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} rule - The URI of the rule to remove
 */
function xowlRemoveDBRule(callback, db, rule) {
	xowlCommand(callback, "DATABASE " + db + " REMOVE RULE " + rule);
}

/**
 * Activates an existing inactive rule in a database
 * @param xowlRemoveDBRule
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} rule - The URI of the rule to activate
 */
function xowlActivateDBRule(callback, db, rule) {
	xowlCommand(callback, "DATABASE " + db + " ACTIVATE RULE " + rule);
}

/**
 * Deactivates an existing active rule in a database
 * @param xowlDeactivateDBRule
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} rule - The URI of the rule to deactivate
 */
function xowlDeactivateDBRule(callback, db, rule) {
	xowlCommand(callback, "DATABASE " + db + " DEACTIVATE RULE " + rule);
}

/**
 * Gets wether a database rule is currently active
 * @param xowlIsActiveDBRule
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} rule - The URI of the rule
 */
function xowlIsActiveDBRule(callback, db, rule) {
	xowlCommand(callback, "DATABASE " + db + " IS ACTIVE " + rule);
}

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
 * Requests the matching status of a rule in a database
 * @param xowlGetDBRuleStatus
 * @param {statusCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} rule - The URI of the rule
 */
function xowlGetDBRuleStatus(callback, db, rule) {
	xowlCommand(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).steps);
		} else {
			callback(code, type, content);
		}
	}, "DATABASE " + db + " STATUS " + rule);
}

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
 * Requests the explanation of the how a quad has been produced in a database
 * @param xowlGetDBRuleStatus
 * @param {explainCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} quad - The quad to explain
 */
function xowlExplainquad(callback, db, quad) {
	xowlCommand(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content));
		} else {
			callback(code, type, content);
		}
	}, "DATABASE " + db + " EXPLAIN " + quad);
}

/**
 * Executes a xOWL Server Protocol command
 * @method xowlCommand
 * @param {commandCallback} callback - The callback for this request
 * @param {string} command - The command for the server
 */
function xowlCommand(callback, command) {
	if (XOWL_ANGULAR_HTTP === null) {
		var xmlHttp = new XMLHttpRequest();
		xmlHttp.onreadystatechange = function () {
			if (xmlHttp.readyState == 4) {
				var ct = xmlHttp.getResponseHeader("Content-Type");
				callback(xmlHttp.status, ct, xmlHttp.responseText)
			}
		}
		xmlHttp.open("POST", XOWL_ENDPOINT, true);
		xmlHttp.setRequestHeader("Accept", "text/plain, application/json");
		xmlHttp.setRequestHeader("Content-Type", "application/x-xowl-xsp");
		if (XOWL_AUTH_TOKEN !== null)
			xmlHttp.setRequestHeader("Authorization", "Basic " + XOWL_AUTH_TOKEN);
		xmlHttp.send(command);
	} else {
		var headers = { headers: {
				"Content-Type": "application/x-xowl-xsp",
				"Accept": "text/plain, application/json"
				},
				transformResponse: function(data, headersGetter, status) { return data; } };
		if (XOWL_AUTH_TOKEN !== null)
			headers.headers["Authorization"] = "Basic " + XOWL_AUTH_TOKEN;
		XOWL_ANGULAR_HTTP.post(XOWL_ENDPOINT, command, headers).then(function (response) {
				callback(response.status, response.headers("Content-Type"), response.data);
			}, function (response) {
				callback(response.status, response.headers("Content-Type"), response.data);
			});
	}
}

/**
 * Executes a SPARQL query on a database
 * @method xowlSPARQL
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} sparql - The SPARQL query
 */
function xowlSPARQL(callback, db, sparql) {
	if (XOWL_ANGULAR_HTTP === null) {
		var xmlHttp = new XMLHttpRequest();
		xmlHttp.onreadystatechange = function () {
			if (xmlHttp.readyState == 4) {
				var ct = xmlHttp.getResponseHeader("Content-Type");
				callback(xmlHttp.status, ct, xmlHttp.responseText)
			}
		}
		xmlHttp.open("POST", XOWL_ENDPOINT + "/db/" + db + "/", true);
		xmlHttp.setRequestHeader("Accept", "application/n-quads, application/sparql-results+json");
		xmlHttp.setRequestHeader("Content-Type", "application/sparql-query");
		if (XOWL_AUTH_TOKEN !== null)
			xmlHttp.setRequestHeader("Authorization", "Basic " + XOWL_AUTH_TOKEN);
		xmlHttp.send(sparql);
	} else {
		var headers = { headers: {
				"Content-Type": "application/sparql-query",
				"Accept": "application/n-quads, application/sparql-results+json"
				},
				transformResponse: function(data, headersGetter, status) { return data; } };
		if (XOWL_AUTH_TOKEN !== null)
			headers.headers["Authorization"] = "Basic " + XOWL_AUTH_TOKEN;
		XOWL_ANGULAR_HTTP.post(XOWL_ENDPOINT + "/db/" + db + "/", sparql, headers).then(function (response) {
				callback(response.status, response.headers("Content-Type"), response.data);
			}, function (response) {
				callback(response.status, response.headers("Content-Type"), response.data);
			});
	}
}