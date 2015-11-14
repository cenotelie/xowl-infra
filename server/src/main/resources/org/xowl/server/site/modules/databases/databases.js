'use strict';

angular.module('xOWLServer.databases', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/databases', {
      templateUrl: 'modules/databases/databases.html',
      controller: 'DatabasesCtrl'
    });
  }])

  .controller('DatabasesCtrl', ['$scope', '$http', '$sce', '$location', function ($scope, $http, $sce, $location) {
    $http.post('/api', "ADMIN LIST DATABASES", { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
      $scope.databases = response.data.results;
    }, function (response) {
      $scope.messages = $sce.trustAsHtml(getError(MSG_ERROR_CONNECTION));
    });
    $scope.onNewDB = function () {
      var name = document.getElementById('field-db-name').value;
      $http.post('/api', "ADMIN CREATE DATABASE " + name, { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
        $location.path("/database/" + name);
      }, function (response) {
        $scope.messages = $sce.trustAsHtml(getError("Failed to create the database."));
      });
      document.getElementById('field-db-name').value = "";
    };
  }]);