'use strict';

angular.module('xOWLServer.databases', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/databases', {
      templateUrl: 'modules/databases/databases.html',
      controller: 'DatabasesCtrl'
    });
  }])

  .controller('DatabasesCtrl', ['$rootScope', '$scope', '$sce', '$location', function ($rootScope, $scope, $sce, $location) {
    $rootScope.xowl.getDatabases(function (code, type, content) {
      if (code === 200) {
        $scope.databases = content;
      } else {
        $scope.messages = $sce.trustAsHtml(getError(code, content));
      }
    });

    $scope.onNewDB = function () {
      var name = document.getElementById('field-db-name').value;
      $rootScope.xowl.createDatabase(function (code, type, content) {
        if (code === 200) {
          $location.path("/database/" + name);
        } else {
          $scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
        }
      }, name);
      document.getElementById('field-db-name').value = "";
    };
  }]);