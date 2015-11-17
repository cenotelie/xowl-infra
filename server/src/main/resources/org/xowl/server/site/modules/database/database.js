'use strict';

angular.module('xOWLServer.database', ['ngRoute'])

	.config(['$routeProvider', function ($routeProvider) {
		$routeProvider.when('/database/:id', {
			templateUrl: 'modules/database/database.html',
			controller: 'DatabaseCtrl'
		});
	}])

	.controller('DatabaseCtrl', ['$scope', '$http', '$sce', '$routeParams', '$location', function ($scope, $http, $sce, $routeParams, $location) {
		$scope.database = {
			name: $routeParams.id,
			status: true
		}
		$http.post('/api', "DATABASE " + $scope.database.name + " ENTAILMENT", { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
			$scope.database.entailment = response.data;
			setupEntailment($scope.database.entailment);
		}, function (response) {
			$scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
		});
		reloadRules($scope, $sce, $http);
		document.getElementById("rule-def-new").value = DEFAULT_RULE;
		document.getElementById("sparql").value = DEFAULT_QUERY;

		$scope.onSetEntailment = function () {
			var regime = getEntailment();
			$http.post('/api', "DATABASE " + $scope.database.name + " ENTAILMENT " + regime, { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
				$scope.messages = $sce.trustAsHtml(getSuccess("The entailment regime was set."));
				$scope.database.entailment = regime;
			}, function (response) {
				$scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
				setupEntailment($scope.database.entailment);
			});
		}

		$scope.onDBDrop = function () {
			$http.post('/api', "ADMIN DROP DATABASE " + $scope.database.name, { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
				$location.path("/databases");
			}, function (response) {
				$scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
			});
		}

		$scope.onNewRule = function () {
			var data = document.getElementById("rule-def-new").value;
			$http.post('/api', "DATABASE " + $scope.database.name + " ADD RULE " + data, { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
				$scope.messages = $sce.trustAsHtml(getSuccess("Added new rule."));
				reloadRules($scope, $sce, $http);
			}, function (response) {
				$scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
			});
		}

		$scope.onRemoveRule = function (name) {
			$http.post('/api', "DATABASE " + $scope.database.name + " REMOVE RULE " + name, { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
				$scope.messages = $sce.trustAsHtml(getSuccess("Removed rule " + name));
				reloadRules($scope, $sce, $http);
			}, function (response) {
				$scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
			});
		}

		$scope.onActivateRule = function (name) {
		}

		$scope.onDeactivateRule = function (name) {
		}
		
		$scope.onSPARQL = function () {
			var query = document.getElementById("sparql").value;
			$http.post('/api/db/' + $scope.database.name + '/', query,
			{ headers: {
				"Content-Type": "application/sparql-query",
				"Accept": "application/n-quads, application/sparql-results+json"
				} }).then(function (response) {
				
			}, function (response) {
				$scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
			});
		}
	}]);

var DEFAULT_RULE =
	"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n" +
	"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n" +
	"@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.\n" +
	"@prefix owl: <http://www.w3.org/2002/07/owl#>.\n" +
	"@prefix xowl: <http://xowl.org/store/rules/xowl#>.\n" +
	"rule xowl:myrule distinct {\n" +
	"    ?x rdf:type ?y\n" +
	"    NOT (?x rdf:type owl:Class)\n" +
	"} => {\n" +
	"    ?x rdf:type xowl:MyClass\n" +
	"}";

var DEFAULT_QUERY =
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
	"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
	"PREFIX xowl: <http://xowl.org/store/rules/xowl#>\n" +
	"SELECT DISTINCT ?x ?y WHERE { GRAPH ?g { ?x a ?y } }";

function reloadRules($scope, $sce, $http) {
	$http.post('/api', "DATABASE " + $scope.database.name + " LIST RULES", { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
		$scope.rules = [];
		for (var i = 0; i != response.data.results.length; i++) {
			$scope.rules.push({ name: response.data.results[i], isActive: false });
		}
		$http.post('/api', "DATABASE " + $scope.database.name + " LIST ACTIVE RULES", { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
			for (var i = 0; i != response.data.results.length; i++) {
				var name = response.data.results[i];
				for (var j = 0; j != $scope.rules.length; i++) {
					if ($scope.rules[j].name === name) {
						$scope.rules[j].isActive = true;
					}
				}
			}
		}, function (response) {
			$scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
		});
	}, function (response) {
		$scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
	});
}

function getEntailment() {
	if (document.getElementById('entailment-option-none').checked)
		return 'none';
	if (document.getElementById('entailment-option-simple').checked)
		return 'simple';
	if (document.getElementById('entailment-option-rdf').checked)
		return 'RDF';
	if (document.getElementById('entailment-option-rdfs').checked)
		return 'RDFS';
	if (document.getElementById('entailment-option-owl2-rdf').checked)
		return 'OWL2_RDF';
	if (document.getElementById('entailment-option-owl2-direct').checked)
		return 'OWL2_DIRECT';
	return null;
}

function setupEntailment(regime) {
	switch (regime) {
		case 'none':
			document.getElementById('entailment-option-none').checked = true;
			document.getElementById('entailment-option-simple').checked = false;
			document.getElementById('entailment-option-rdf').checked = false;
			document.getElementById('entailment-option-rdfs').checked = false;
			document.getElementById('entailment-option-owl2-rdf').checked = false;
			document.getElementById('entailment-option-owl2-direct').checked = false;
			break;
		case 'simple':
			document.getElementById('entailment-option-none').checked = false;
			document.getElementById('entailment-option-simple').checked = true;
			document.getElementById('entailment-option-rdf').checked = false;
			document.getElementById('entailment-option-rdfs').checked = false;
			document.getElementById('entailment-option-owl2-rdf').checked = false;
			document.getElementById('entailment-option-owl2-direct').checked = false;
			break;
		case 'RDF':
			document.getElementById('entailment-option-none').checked = false;
			document.getElementById('entailment-option-simple').checked = false;
			document.getElementById('entailment-option-rdf').checked = true;
			document.getElementById('entailment-option-rdfs').checked = false;
			document.getElementById('entailment-option-owl2-rdf').checked = false;
			document.getElementById('entailment-option-owl2-direct').checked = false;
			break;
		case 'RDFS':
			document.getElementById('entailment-option-none').checked = false;
			document.getElementById('entailment-option-simple').checked = false;
			document.getElementById('entailment-option-rdf').checked = false;
			document.getElementById('entailment-option-rdfs').checked = true;
			document.getElementById('entailment-option-owl2-rdf').checked = false;
			document.getElementById('entailment-option-owl2-direct').checked = false;
			break;
		case 'OWL2_RDF':
			document.getElementById('entailment-option-none').checked = false;
			document.getElementById('entailment-option-simple').checked = false;
			document.getElementById('entailment-option-rdf').checked = false;
			document.getElementById('entailment-option-rdfs').checked = false;
			document.getElementById('entailment-option-owl2-rdf').checked = true;
			document.getElementById('entailment-option-owl2-direct').checked = false;
			break;
		case 'OWL2_DIRECT':
			document.getElementById('entailment-option-none').checked = false;
			document.getElementById('entailment-option-simple').checked = false;
			document.getElementById('entailment-option-rdf').checked = false;
			document.getElementById('entailment-option-rdfs').checked = false;
			document.getElementById('entailment-option-owl2-rdf').checked = false;
			document.getElementById('entailment-option-owl2-direct').checked = true;
			break;
		default:
			break;
	}
}