'use strict';

angular.module('xOWLServer.login', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/login', {
      templateUrl: 'modules/login/login.html',
      controller: 'LoginCtrl'
    });
  }])

  .controller('LoginCtrl', [
    '$rootScope',
    '$scope',
    '$sce',
    '$location',
    function ($rootScope, $scope, $sce, $location) {

      $scope.onLoginButton = function () {
        var login = document.getElementById('field-login').value;
        var password = document.getElementById('field-password').value;
        $rootScope.xowl.login(function (code, type, content) {
          if (code !== 200) {
            $scope.messages = $sce.trustAsHtml(getError("Failed to login, login/password do not match a recognized user on this server."));
          } else {
            reloadUserData($rootScope);
            $location.path("/databases");
          }
        }, login, password);
      };

      $rootScope.onLogoutButton = function () {
        $rootScope.xowl.logout();
        reloadUserData($rootScope);
        $location.path("/login");
      };
    }]);
