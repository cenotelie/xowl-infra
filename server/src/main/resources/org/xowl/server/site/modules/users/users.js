'use strict';

angular.module('xOWLServer.users', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/users', {
      templateUrl: 'modules/users/users.html',
      controller: 'UsersCtrl'
    });
  }])

  .controller('UsersCtrl', ['$rootScope', '$scope', '$sce', '$location', function ($rootScope, $scope, $sce, $location) {
    $rootScope.xowl.getUsers(function (code, type, content) {
      if (code === 200) {
        $scope.users = content;
      } else {
        $scope.messages = $sce.trustAsHtml(getError(code, content));
      }
    });

    $scope.onNewUser = function () {
      var login = document.getElementById('field-user-login').value;
      var pass1 = document.getElementById('field-user-password-1').value;
      var pass2 = document.getElementById('field-user-password-2').value;
      if (pass1 !== pass2) {
        $scope.messages = $sce.trustAsHtml(getError("Passwords do not match!"));
        return;
      }
      $rootScope.xowl.createUser(function (code, type, content) {
        if (code === 200) {
          $location.path("/user/" + login);
        } else {
          $scope.messages = $sce.trustAsHtml(getError(code, content));
        }
      }, login, pass1);
      document.getElementById('field-user-login').value = "";
      document.getElementById('field-user-password-1').value = "";
      document.getElementById('field-user-password-2').value = "";
    };
  }]);