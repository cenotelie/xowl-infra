'use strict';

angular.module('xOWLServer.server', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/server', {
      templateUrl: 'modules/server/server.html',
      controller: 'ServerCtrl'
    });
  }])

  .controller('ServerCtrl', ['$rootScope', '$scope', '$sce', function ($rootScope, $scope, $sce) {
    $scope.onServerShutdown = function () {
      $rootScope.xowl.serverShutdown(function (code, type, content) {
        if (code === 200) {
          $scope.messages = $sce.trustAsHtml(getSuccess("Server is shutting down."));
        } else {
          $scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
        }
      });
    };

    $scope.onServerRestart = function () {
      $rootScope.xowl.serverRestart(function (code, type, content) {
        if (code === 200) {
          $scope.messages = $sce.trustAsHtml(getSuccess("Server is restarting."));
        } else {
          $scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
        }
      });
    };
  }]);