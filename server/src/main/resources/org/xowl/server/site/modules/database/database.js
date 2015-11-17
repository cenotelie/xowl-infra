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
		reloadRules($scope, $http, $sce);

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
		
		$scope.onNewRule = function() {
			
		}
	}]);

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