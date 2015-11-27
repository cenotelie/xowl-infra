'use strict';

angular.module('xOWLServer.user', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/user/:id', {
      templateUrl: 'modules/user/user.html',
      controller: 'UserCtrl'
    });
  }])

  .controller('UserCtrl', ['$rootScope', '$scope', '$sce', '$routeParams', '$location', function ($rootScope, $scope, $sce, $routeParams, $location) {
    $scope.user = $routeParams.id;

    $scope.updatePrivileges = function () {
      $rootScope.xowl.getUserPrivileges(function (code, type, content) {
        if (code === 200) {
          $scope.privileges = content;
        } else {
          $scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
        }
      }, $scope.user);
    }
    $scope.updatePrivileges();

    $scope.onPasswordChange = function () {
      var pass1 = document.getElementById('field-password-1').value;
      var pass2 = document.getElementById('field-password-2').value;
      if (pass1 !== pass2) {
        $scope.messages = $sce.trustAsHtml(getError("Passwords do not match!"));
        return;
      }
      $rootScope.xowl.resetPassword(function (code, type, content) {
        if (code === 200) {
          $scope.messages = $sce.trustAsHtml(getSuccess("Password was successfully changed."));
        } else {
          $scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
        }
      }, $scope.user, pass1);
      document.getElementById('field-password-1').value = "";
      document.getElementById('field-password-2').value = "";
    };

    $scope.onRevoke = function (access, privilege) {
      var database = access.database;
      $rootScope.xowl.revokeDB(function (code, type, content) {
        if (code === 200) {
          $scope.messages = $sce.trustAsHtml(getSuccess("Success!"));
          $scope.updatePrivileges();
        } else {
          $scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
        }
      }, database, privilege, $scope.user);
    }

    $scope.onGrant = function () {
      var database = document.getElementById('field-grant-database').value;
      var privilege = document.getElementById('field-grant-privilege').value;
      $rootScope.xowl.grantDB(function (code, type, content) {
        if (code === 200) {
          $scope.messages = $sce.trustAsHtml(getSuccess("Success!"));
          $scope.updatePrivileges();
        } else {
          $scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
        }
      }, database, privilege, $scope.user);
    }

    $scope.onUserDelete = function () {
      $rootScope.xowl.deleteUser(function (code, type, content) {
        if (code === 200) {
          $location.path("/users");
        } else {
          $scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
        }
      }, $scope.user);
    }
  }]);