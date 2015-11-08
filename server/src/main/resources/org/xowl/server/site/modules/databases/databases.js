'use strict';

angular.module('xOWLServer.databases', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/databases', {
    templateUrl: 'modules/databases/databases.html',
    controller: 'DatabasesCtrl'
  });
}])

.controller('DatabasesCtrl', [function() {

}]);