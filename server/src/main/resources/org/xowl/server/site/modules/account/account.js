'use strict';

angular.module('xOWLServer.account', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/account', {
      templateUrl: 'modules/account/account.html',
      controller: 'AccountCtrl'
    });
  }])

  .controller('AccountCtrl', ['$scope', function ($scope) {
    $scope.privileges = [
      {
        database: "__admin",
        isAdmin: true,
        canWrite: true,
        canRead: true
      },
      {
        database: "test",
        isAdmin: false,
        canWrite: false,
        canRead: true
      }
    ];
  }]);