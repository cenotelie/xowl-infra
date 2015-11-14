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
            $scope.messages = $sce.trustAsHtml(MSG_ERROR);
          });
      };
      $rootScope.onLogoutButton = function () {
        authService.logout();
      };
    }]);

var MSG_ERROR = "<div class='alert alert-danger alert-dismissible' role='alert' style='margin-top: 20px;'>" +
  "<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>" +
  "<span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span>" +
  " Login failed!" +
  "</div>";