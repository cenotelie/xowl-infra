'use strict';

angular.module('xOWLServer.database', ['ngRoute'])

	.config(['$routeProvider', function ($routeProvider) {
		$routeProvider.when('/database', {
			templateUrl: 'modules/database/database.html',
			controller: 'DatabaseCtrl'
		});
	}])

	.controller('DatabaseCtrl', [function () {

	}]);