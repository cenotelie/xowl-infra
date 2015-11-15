'use strict';

angular.module('xOWLServer.login', ['ngRoute', 'angularBasicAuth'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/login', {
      templateUrl: 'modules/login/login.html',
      controller: 'LoginCtrl'
    });
  }])

  .controller('LoginCtrl', [
    'authDefaults',
    'authService',
    '$rootScope',
    '$scope',
    '$sce',
    '$location',
    function (authDefaults, authService, $rootScope, $scope, $sce, $location) {
      authDefaults.authenticateUrl = '/api';
      authService.addEndpoint();
      $scope.onLoginButton = function () {
        var login = document.getElementById('field-login').value;
        var password = document.getElementById('field-password').value;
        authService
          .login(login, password)
          .success(function () {
            $location.path("/databases");
          })
          .error(function () {
            $scope.messages = $sce.trustAsHtml(getError("Failed to login, login/password do not match a recognized user on this server."));
          });
      };
      $rootScope.onLogoutButton = function () {
        authService.logout();
        $location.path("/login");
      };
    }]);