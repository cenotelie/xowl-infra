'use strict';

angular.module('xOWLServer.users', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/users', {
      templateUrl: 'modules/users/users.html',
      controller: 'UsersCtrl'
    });
  }])

  .controller('UsersCtrl', ['$scope', '$http', '$sce', '$location', function ($scope, $http, $sce, $location) {
    $http.post('/api', "ADMIN LIST USERS", { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
      $scope.users = response.data.results;
    }, function (response) {
      $scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
    });
    $scope.onNewUser = function () {
      var login = document.getElementById('field-user-login').value;
      var pass1 = document.getElementById('field-user-password-1').value;
      var pass2 = document.getElementById('field-user-password-2').value;
      if (pass1 !== pass2) {
        $scope.messages = $sce.trustAsHtml(getError("Passwords do not match!"));
        return;
      }
      $http.post('/api', "ADMIN CREATE USER " + login + " " + pass1, { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
        $location.path("/user/" + login);
      }, function (response) {
        $scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
      });
      document.getElementById('field-user-login').value = "";
      document.getElementById('field-user-password-1').value = "";
      document.getElementById('field-user-password-2').value = "";
    };
  }]);