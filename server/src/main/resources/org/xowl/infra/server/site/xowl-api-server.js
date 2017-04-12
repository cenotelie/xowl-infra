// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

/*****************************************************
 * xOWL Triple Store Server API - V1
 ****************************************************/

function XOWL(endpoint, useLocal) {
	this.endpoint = (!endpoint) ? '/api/v1' : endpoint;
	this.useLocal = (!useLocal) ? true : useLocal;
	if (this.useLocal) {
		this.userName = localStorage.getItem('xowl.userName');
	} else {
		this.userName = null;
	}
}

XOWL.prototype.isLoggedIn = function () {
	return (this.userName !== null);
}

XOWL.prototype.getLoggedInUser = function () {
	return this.userName;
}

XOWL.prototype.login = function (callback, login, password) {
	var _self = this;
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			_self.userName = login;
			if (_self.useLocal) {
				localStorage.setItem('xowl.userName', login);
			}
			callback(code, type, content);
		} else {
			_self.userName = null;
			if (_self.useLocal) {
				localStorage.removeItem('xowl.userName');
			}
			callback(code, type, content);
		}
	}, "/me/login?login=" + encodeURIComponent(login), "POST", "text/plain", password);
}

XOWL.prototype.logout = function () {
	this.userName = null;
	if (this.useLocal) {
		localStorage.removeItem('xowl.userName');
	}
}



/*****************************************************
 * Server Management
 ****************************************************/

XOWL.prototype.getServerProduct = function (callback) {
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content));
		} else {
			callback(code, type, content);
		}
	}, "/server/product", "GET", null, null);
}

XOWL.prototype.getServerProductDependencies = function (callback) {
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content));
		} else {
			callback(code, type, content);
		}
	}, "/server/product/dependencies", "GET", null, null);
}

XOWL.prototype.serverShutdown = function (callback) {
	this.doRequest(callback, "/server/shutdown", "POST", null, null);
}

XOWL.prototype.serverRestart = function (callback) {
	this.doRequest(callback, "/server/restart", "POST", null, null);
}

XOWL.prototype.serverGrantAdmin = function (callback, login) {
	this.doRequest(callback, "/server/grantAdmin?user=" + encodeURIComponent(login), "POST", null, null);
}

XOWL.prototype.serverRevokeAdmin = function (callback, login) {
	this.doRequest(callback, "/server/revokeAdmin?user=" + encodeURIComponent(login), "POST", null, null);
}



/*****************************************************
 * Databases Management
 ****************************************************/

XOWL.prototype.getDatabases = function (callback) {
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content));
		} else {
			callback(code, type, content);
		}
	}, "/databases", "GET", null, null);
}

XOWL.prototype.createDatabase = function (callback, db) {
	this.doRequest(callback, "/databases/" + encodeURIComponent(db), "PUT", null, null);
}

XOWL.prototype.dropDatabase = function (callback, db) {
	this.doRequest(callback, "/databases/" + encodeURIComponent(db), "DELETE", null, null);
}

XOWL.prototype.getDBMetric = function (callback, db) {
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content));
		} else {
			callback(code, type, content);
		}
	}, "/databases/" + encodeURIComponent(db) + "/metric", "GET", null, null);
}

XOWL.prototype.getDBMetricSnapshot = function (callback, db) {
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content));
		} else {
			callback(code, type, content);
		}
	}, "/databases/" + encodeURIComponent(db) + "/statistics", "GET", null, null);
}

XOWL.prototype.sparql = function (callback, db, sparql) {
	this.doSPARQL(callback, db, sparql);
}

XOWL.prototype.getEntailmentFor = function (callback, db) {
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", content);
		} else {
			callback(code, type, content);
		}
	}, "/databases/" + encodeURIComponent(db) + "/entailment", "GET", null, null);
}

XOWL.prototype.setEntailmentFor = function (callback, db, regime) {
	this.doRequest(callback, "/databases/" + encodeURIComponent(db) + "/entailment", "PUT", "text/plain", regime);
}

XOWL.prototype.getDatabasePrivileges = function (callback, db) {
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content));
		} else {
			callback(code, type, content);
		}
	}, "/databases/" + encodeURIComponent(db) + "/privileges", "GET", null, null);
}

XOWL.prototype.grantDB = function (callback, db, right, login) {
	this.doRequest(callback, "/databases/" + encodeURIComponent(db) + "/privileges/grant?user=" + encodeURIComponent(login) + "&access=" + encodeURIComponent(right), "POST", null, null);
}

XOWL.prototype.revokeDB = function (callback, db, right, login) {
	this.doRequest(callback, "/databases/" + encodeURIComponent(db) + "/privileges/revoke?user=" + encodeURIComponent(login) + "&access=" + encodeURIComponent(right), "POST", null, null);
}

XOWL.prototype.getDBRules = function (callback, db) {
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content));
		} else {
			callback(code, type, content);
		}
	}, "/databases/" + encodeURIComponent(db) + "/rules", "GET", null, null);
}

XOWL.prototype.getDBRule = function (callback, db, rule) {
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content));
		} else {
			callback(code, type, content);
		}
	}, "/databases/" + encodeURIComponent(db) + "/rules/" + encodeURIComponent(rule), "GET", null, null);
}

XOWL.prototype.addDBRule = function (callback, db, rule) {
	this.doRequest(callback, "/databases/" + encodeURIComponent(db) + "/rules", "PUT", "application/x-xowl-rdft", rule);
}

XOWL.prototype.removeDBRule = function (callback, db, rule) {
	this.doRequest(callback, "/databases/" + encodeURIComponent(db) + "/rules/" + encodeURIComponent(rule), "DELETE", null, null);
}

XOWL.prototype.activateDBRule = function (callback, db, rule) {
	this.doRequest(callback, "/databases/" + encodeURIComponent(db) + "/rules/" + encodeURIComponent(rule) + "/activate", "POST", null, null);
}

XOWL.prototype.deactivateDBRule = function (callback, db, rule) {
	this.doRequest(callback, "/databases/" + encodeURIComponent(db) + "/rules/" + encodeURIComponent(rule) + "/deactivate", "POST", null, null);
}

XOWL.prototype.getDBRuleStatus = function (callback, db, rule) {
	this.doRequest(function (code, type, content) {
    		if (code === 200) {
    			callback(code, "application/json", JSON.parse(content));
    		} else {
    			callback(code, type, content);
    		}
    	}, "/databases/" + encodeURIComponent(db) + "/rules/" + encodeURIComponent(rule) + "/status", "GET", null, null);
}

XOWL.prototype.getDBProcedures = function (callback, db) {
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content));
		} else {
			callback(code, type, content);
		}
	}, "/databases/" + encodeURIComponent(db) + "/procedures", "GET", null, null);
}

XOWL.prototype.getDBProcedure = function (callback, db, procedure) {
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content));
		} else {
			callback(code, type, content);
		}
	}, "/databases/" + encodeURIComponent(db) + "/procedures/" + encodeURIComponent(procedure), "GET", null, null);
}

XOWL.prototype.addDBProcedure = function (callback, db, procedure) {
	this.doRequest(callback, "/databases/" + encodeURIComponent(db) + "/procedures/" + encodeURIComponent(procedure.name), "PUT", "application/json", procedure);
}

XOWL.prototype.removeDBProcedure = function (callback, db, procedure) {
	this.doRequest(callback, "/databases/" + encodeURIComponent(db) + "/procedures/" + encodeURIComponent(procedure), "DELETE", null, null);
}

XOWL.prototype.executeDBProcedure = function (callback, db, procedure, context) {
	this.doRequest(callback, "/databases/" + encodeURIComponent(db) + "/procedures/" + encodeURIComponent(procedure), "POST", "application/json", context);
}

XOWL.prototype.upload = function (callback, db, contentType, content) {
	this.doRequest(callback, "/databases/" + encodeURIComponent(db), "POST", contentType, content);
}



/*****************************************************
 * Users Management
 ****************************************************/

XOWL.prototype.getUsers = function (callback) {
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content));
		} else {
			callback(code, type, content);
		}
	}, "/users", "GET", null, null);
}

XOWL.prototype.createUser = function (callback, login, pw) {
	this.doRequest(callback, "/users/" + encodeURIComponent(login), "PUT", "text/plain", pw);
}

XOWL.prototype.deleteUser = function (callback, login) {
	this.doRequest(callback, "/users/" + encodeURIComponent(login), "DELETE", null, null);
}

XOWL.prototype.updatePassword = function (callback, user, pw) {
	this.doRequest(callback, "/users/" + encodeURIComponent(user), "POST", "text/plain", pw);
}

XOWL.prototype.getUserPrivileges = function (callback, login) {
	this.doRequest(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content));
		} else {
			callback(code, type, content);
		}
	}, "/users/" + encodeURIComponent(login) + "/privileges", "GET", null, null);
}



/*****************************************************
 * Utility API
 ****************************************************/

XOWL.prototype.doRequest = function (callback, complement, method, contentType, content) {
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function () {
		if (xmlHttp.readyState == 4) {
			var ct = xmlHttp.getResponseHeader("Content-Type");
			callback(xmlHttp.status, ct, xmlHttp.responseText)
		}
	}
	xmlHttp.open(method, this.endpoint + complement, true);
	xmlHttp.setRequestHeader("Accept", "text/plain, application/json");
	if (contentType !== null)
	    xmlHttp.setRequestHeader("Content-Type", contentType);
	xmlHttp.withCredentials = true;
	if (content === null)
    	xmlHttp.send();
    else if (contentType === "application/json")
    	xmlHttp.send(JSON.stringify(content));
    else
    	xmlHttp.send(content);
}

XOWL.prototype.doSPARQL = function (callback, db, sparql) {
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function () {
		if (xmlHttp.readyState == 4) {
			var ct = xmlHttp.getResponseHeader("Content-Type");
			callback(xmlHttp.status, ct, xmlHttp.responseText)
		}
	}
	xmlHttp.open("POST", this.endpoint + "/databases/" + db + "/sparql", true);
	xmlHttp.setRequestHeader("Accept", "application/sparql-results+json, application/json");
	xmlHttp.setRequestHeader("Content-Type", "application/sparql-query");
	xmlHttp.withCredentials = true;
	xmlHttp.send(sparql);
}