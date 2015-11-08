'use strict';

// Declare app level module which depends on views, and components
angular.module('xOWLServer', [
  'ngRoute',
  'xOWLServer.login',
  'xOWLServer.logout',
  'xOWLServer.databases',
  'xOWLServer.database',
  'xOWLServer.users'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/login'});
}]);
