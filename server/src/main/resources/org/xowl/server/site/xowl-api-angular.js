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
 * Executes a xOWL Server Protocol command
 * @method jsCommand
 * @param {commandCallback} callback - The callback for this request
 * @param {string} command - The command for the server
 */
XOWL.prototype.command = function (callback, command) {
	this.ngCommand(callback, command);
}

/**
 * Executes a SPARQL query on a database
 * @method sparql
 * @param {commandCallback} callback - The callback for this request
 * @param {string} db - The target database
 * @param {string} sparql - The SPARQL query
 */
XOWL.prototype.sparql = function (callback, db, sparql) {
	this.ngSPARQL(callback, db, sparql);
}

/**
* Executes a xOWL Server Protocol command (AngularJS API)
* @method ngCommand
* @param {commandCallback} callback - The callback for this request
* @param {string} command - The command for the server
*/
XOWL.prototype.ngCommand = function (callback, command) {
	var headers = { headers: {
			"Content-Type": "application/x-xowl-xsp",
			"Accept": "text/plain, application/json"
			},
			transformResponse: function(data, headersGetter, status) { return data; } };
	if (this.authToken !== null)
		headers.headers["Authorization"] = "Basic " + this.authToken;
	this.$http.post(this.endpoint, command, headers).then(function (response) {
			callback(response.status, response.headers("Content-Type"), response.data);
		}, function (response) {
			callback(response.status, response.headers("Content-Type"), response.data);
		});
}

/**
* Executes a SPARQL query on a database (AngularJS API)
* @method ngSPARQL
* @param {commandCallback} callback - The callback for this request
* @param {string} db - The target database
* @param {string} sparql - The SPARQL query
*/
XOWL.prototype.ngSPARQL = function (callback, db, sparql) {
	var headers = { headers: {
			"Content-Type": "application/sparql-query",
			"Accept": "application/n-quads, application/sparql-results+json"
			},
			transformResponse: function(data, headersGetter, status) { return data; } };
	if (this.authToken !== null)
		headers.headers["Authorization"] = "Basic " + this.authToken;
	this.$http.post(this.endpoint + "/db/" + db + "/", sparql, headers).then(function (response) {
			callback(response.status, response.headers("Content-Type"), response.data);
		}, function (response) {
			callback(response.status, response.headers("Content-Type"), response.data);
		});
}