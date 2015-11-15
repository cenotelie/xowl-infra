'use strict';

angular.module('xOWLServer.user', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/user/:id', {
      templateUrl: 'modules/user/user.html',
      controller: 'UserCtrl'
    });
  }])

  .controller('UserCtrl', ['$rootScope', '$scope', '$http', '$sce', '$routeParams', '$location', function ($rootScope, $scope, $http, $sce, $routeParams, $location) {
    $scope.user = $routeParams.id;

    $scope.updatePrivileges = function () {
      $http.post('/api', "ADMIN PRIVILEGES " + $scope.user, { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
        $scope.privileges = response.data.results;
      }, function (response) {
        $scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
      });
    }
    $scope.updatePrivileges();

    $scope.onPasswordChange = function () {
      var pass1 = document.getElementById('field-password-1').value;
      var pass2 = document.getElementById('field-password-2').value;
      if (pass1 !== pass2) {
        $scope.messages = $sce.trustAsHtml(getError("Passwords do not match!"));
        return;
      }
      $http.post('/api', "ADMIN RESET PASSWORD " + $scope.user + " " + pass1, { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
        $scope.messages = $sce.trustAsHtml(getSuccess("Password was successfully reset."));
      }, function (response) {
        $scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
      });
      document.getElementById('field-password-1').value = "";
      document.getElementById('field-password-2').value = "";
    };

    $scope.onRevoke = function (access, privilege) {
      var database = access.database;
      if (access.isAdmin) {
        $http.post('/api', "ADMIN REVOKE " + privilege + " " + $scope.user + " " + database, { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
          $scope.messages = $sce.trustAsHtml(getSuccess("Success!"));
          $scope.updatePrivileges();
        }, function (response) {
          $scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
          $scope.updatePrivileges();
        });
      }
    }

    $scope.onGrant = function () {
      var database = document.getElementById('field-grant-database').value;
      var privilege = document.getElementById('field-grant-privilege').value;
      $http.post('/api', "ADMIN GRANT " + privilege + " " + $scope.user + " " + database, { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
        $scope.messages = $sce.trustAsHtml(getSuccess("Success!"));
        $scope.updatePrivileges();
      }, function (response) {
        $scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
      });
    }

    $scope.onUserDelete = function () {
      $http.post('/api', "ADMIN DELETE USER " + $scope.user, { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
        $scope.messages = $sce.trustAsHtml(getSuccess("Success!"));
        $location.path("/users");
      }, function (response) {
        $scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
      });
    }
  }]);