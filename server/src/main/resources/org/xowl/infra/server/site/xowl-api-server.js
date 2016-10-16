// Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
// Provided under LGPL v3

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

XOWL.prototype.isLoggedIn = function () {
	return (this.authToken !== null && this.userName !== null);
}

XOWL.prototype.getUser = function () {
	return this.userName;
}

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
	}, "/whoami", "GET", null, "");
}

XOWL.prototype.logout = function () {
	this.authToken = null;
	this.userName = null;
	if (this.useLocal) {
		localStorage.removeItem('xowl.authToken');
		localStorage.removeItem('xowl.userName');
	}
}

XOWL.prototype.serverShutdown = function (callback) {
	this.command(callback, "/server?action=shutdown", "POST", null, "");
}

XOWL.prototype.serverRestart = function (callback) {
	this.command(callback, "/server?action=restart", "POST", null, "");
}

XOWL.prototype.getUsers = function (callback) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "/users", "GET", null, "");
}

XOWL.prototype.createUser = function (callback, login, pw) {
	this.command(callback, "/user/" + encodeURIComponent(login), "PUT", null, pw);
}

XOWL.prototype.deleteUser = function (callback, login) {
	this.command(callback, "/user/" + encodeURIComponent(login), "DELETE", null, "");
}

XOWL.prototype.changePassword = function (callback, pw) {
	this.command(callback, "/user/" + encodeURIComponent(this.getUser()), "PUT", null, pw);
}

XOWL.prototype.resetPassword = function (callback, login, pw) {
	this.command(callback, "/user/" + encodeURIComponent(login), "PUT", null, pw);
}

XOWL.prototype.getUserPrivileges = function (callback, login) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "/user/" + encodeURIComponent(login) + "/privileges", "GET", null, "");
}

XOWL.prototype.getDatabasePrivileges = function (callback, db) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "/db/" + encodeURIComponent(db) + "/privileges", "GET", null, "");
}

XOWL.prototype.grantDB = function (callback, db, right, login) {
	this.command(callback, "/db/" + encodeURIComponent(db) + "/privileges?action=grant&user=" + encodeURIComponent(logged) + "&access=" + encodeURIComponent(right), "POST", null, "");
}

XOWL.prototype.revokeDB = function (callback, db, right, login) {
	this.command(callback, "/db/" + encodeURIComponent(db) + "/privileges?action=revoke&user=" + encodeURIComponent(logged) + "&access=" + encodeURIComponent(right), "POST", null, "");
}

XOWL.prototype.grantServerAdmin = function (callback, login) {
	this.command(callback, "/user/" + encodeURIComponent(login) + "/privileges?action=grant&server=&access=" + encodeURIComponent(right), "POST", null, "");
}

XOWL.prototype.revokeServerAdmin = function (callback, login) {
	this.command(callback, "/user/" + encodeURIComponent(login) + "/privileges?action=revoke&server=&access=" + encodeURIComponent(right), "POST", null, "");
}

XOWL.prototype.getDatabases = function (callback) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "/databases", "GET", null, "");
}

XOWL.prototype.createDatabase = function (callback, db) {
	this.command(callback, "/db/" + encodeURIComponent(db), "PUT", null, "");
}

XOWL.prototype.dropDatabase = function (callback, db) {
	this.command(callback, "/db/" + encodeURIComponent(db), "DELETE", null, "");
}

XOWL.prototype.getEntailmentFor = function (callback, db) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "/db/" + encodeURIComponent(db) + "/entailment", "GET", null, "");
}

XOWL.prototype.setEntailmentFor = function (callback, db, regime) {
	this.command(callback, "/db/" + encodeURIComponent(db) + "/entailment", "PUT", null, regime);
}

XOWL.prototype.getDBRules = function (callback, db) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "/db/" + encodeURIComponent(db) + "/rules", "GET", null, "");
}

XOWL.prototype.getDBRule = function (callback, db, rule) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "/db/" + encodeURIComponent(db) + "/rules?id=" + encodeURIComponent(rule), "GET", null, "");
}

XOWL.prototype.addDBRule = function (callback, db, rule) {
	this.command(callback, "/db/" + encodeURIComponent(db) + "/rules", "PUT", null, rule);
}

XOWL.prototype.removeDBRule = function (callback, db, rule) {
	this.command(callback, "/db/" + encodeURIComponent(db) + "/rules?id=" + encodeURIComponent(rule), "DELETE", null, rule);
}

XOWL.prototype.activateDBRule = function (callback, db, rule) {
	this.command(callback, "/db/" + encodeURIComponent(db) + "/rules?id=" + encodeURIComponent(rule) + "&action=activate", "POST", null, rule);
}

XOWL.prototype.deactivateDBRule = function (callback, db, rule) {
	this.command(callback, "/db/" + encodeURIComponent(db) + "/rules?id=" + encodeURIComponent(rule) + "&action=deactivate", "POST", null, rule);
}

XOWL.prototype.getDBRuleStatus = function (callback, db, rule) {
	this.command(function (code, type, content) {
    		if (code === 200) {
    			callback(code, "application/json", JSON.parse(content).payload);
    		} else {
    			callback(code, type, content);
    		}
    	}, "/db/" + encodeURIComponent(db) + "/rules?id=" + encodeURIComponent(rule) + "&status=", "GET", null, "");
}

XOWL.prototype.getDBProcedures = function (callback, db) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "/db/" + encodeURIComponent(db) + "/procedures", "GET", null, "");
}

XOWL.prototype.getDBProcedure = function (callback, db, procedure) {
	this.command(function (code, type, content) {
		if (code === 200) {
			callback(code, "application/json", JSON.parse(content).payload);
		} else {
			callback(code, type, content);
		}
	}, "/db/" + encodeURIComponent(db) + "/procedures?id=" + encodeURIComponent(procedure), "GET", null, "");
}

XOWL.prototype.addDBProcedure = function (callback, db, procedure) {
	this.command(callback, "/db/" + encodeURIComponent(db) + "/procedures", "PUT", "application/json", procedure);
}

XOWL.prototype.removeDBProcedure = function (callback, db, procedure) {
	this.command(callback, "/db/" + encodeURIComponent(db) + "/procedures?id=" + encodeURIComponent(procedure), "DELETE", null, rule);
}

XOWL.prototype.executeDBProcedure = function (callback, db, procedure, context) {
	this.command(callback, "/db/" + encodeURIComponent(db) + "/procedures?id=" + encodeURIComponent(procedure), "POST", "application/json", context);
}

XOWL.prototype.upload = function (callback, db, contentType, content) {
	this.command(callback, "/db/" + encodeURIComponent(db), "POST", contentType, content);
}

XOWL.prototype.command = function (callback, complement, method, contentType, content) {
	this.jsCommand(callback, complement, method, contentType, content);
}

XOWL.prototype.jsCommand = function (callback, complement, method, contentType, content) {
	if (this.authToken === null || this.authToken == "")
		callback(401, "text/plain", "");
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
	else
	    xmlHttp.setRequestHeader("Content-Type", "application/x-xowl-xsp");
	xmlHttp.withCredentials = true;
	xmlHttp.setRequestHeader("Authorization", "Basic " + this.authToken);
	if (content === null)
    	xmlHttp.send();
    else if (contentType === "application/json")
    	xmlHttp.send(JSON.stringify(content));
    else
    	xmlHttp.send(payload);
	xmlHttp.send(content);
}

XOWL.prototype.sparql = function (callback, db, sparql) {
	this.jsSPARQL(callback, db, sparql);
}

XOWL.prototype.jsSPARQL = function (callback, db, sparql) {
	if (this.authToken === null || this.authToken == "")
		callback(401, "text/plain", "");
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function () {
		if (xmlHttp.readyState == 4) {
			var ct = xmlHttp.getResponseHeader("Content-Type");
			callback(xmlHttp.status, ct, xmlHttp.responseText)
		}
	}
	xmlHttp.open("POST", this.endpoint + "/db/" + db + "/sparql", true);
	xmlHttp.setRequestHeader("Accept", "application/n-quads, application/sparql-results+json");
	xmlHttp.setRequestHeader("Content-Type", "application/sparql-query");
	xmlHttp.withCredentials = true;
	xmlHttp.setRequestHeader("Authorization", "Basic " + this.authToken);
	xmlHttp.send(sparql);
}