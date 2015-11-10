'use strict';

// Declare app level module which depends on views, and components
angular.module('xOWLServer', [
  'ngRoute',
  'xOWLServer.login',
  'xOWLServer.databases',
  'xOWLServer.database',
  'xOWLServer.users'
])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.otherwise({ redirectTo: '/login' });
  }])

  .controller('xOWLServerCtrl', ['$scope', function ($scope) {
    $scope.databases = [
      {
        id: "__admin",
        name: "__admin"
      },
      {
        id: "test",
        name: "test"
      }
    ];
  }]);