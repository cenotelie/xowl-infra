'use strict';

angular.module('xOWLServer.databases', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/databases', {
      templateUrl: 'modules/databases/databases.html',
      controller: 'DatabasesCtrl'
    });
  }])

  .controller('DatabasesCtrl', ['$scope', function ($scope) {
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