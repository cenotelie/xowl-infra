'use strict';

angular.module('xOWLServer.server', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/server', {
      templateUrl: 'modules/server/server.html',
      controller: 'ServerCtrl'
    });
  }])

  .controller('ServerCtrl', ['$scope', '$http', '$sce', function ($scope, $http, $sce) {
    $scope.onServerShutdown = function () {
      $http.post('/api', "ADMIN SHUTDOWN", { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
        $scope.messages = $sce.trustAsHtml(getSuccess("Server is shutting down."));
      }, function (response) {
        $scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
      });
    };
    $scope.onServerRestart = function () {
      $http.post('/api', "ADMIN RESTART", { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
        $scope.messages = $sce.trustAsHtml(getSuccess("Server is restarting."));
      }, function (response) {
        $scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
      });
    };
  }]);