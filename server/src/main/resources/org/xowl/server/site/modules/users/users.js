'use strict';

angular.module('xOWLServer.users', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/users', {
      templateUrl: 'modules/users/users.html',
      controller: 'UsersCtrl'
    });
  }])

  .controller('UsersCtrl', ['$scope', '$http', '$sce', function ($scope, $http, $sce) {
    $http.post('/api', "ADMIN LIST USERS", { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
      $scope.users = response.data.results;
    }, function (response) {
      $scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
    });
  }]);