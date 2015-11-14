'use strict';

angular.module('xOWLServer.databases', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/databases', {
      templateUrl: 'modules/databases/databases.html',
      controller: 'DatabasesCtrl'
    });
  }])

  .controller('DatabasesCtrl', ['$scope', '$http', '$sce', function ($scope, $http, $sce) {
    $http.post('/api', "ADMIN LIST DATABASES", { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
      $scope.databases = response.data.results;
    }, function (response) {
      $scope.messages = $sce.trustAsHtml(getError(MSG_ERROR_CONNECTION));
    });
  }]);