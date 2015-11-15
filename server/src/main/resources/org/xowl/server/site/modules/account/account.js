'use strict';

angular.module('xOWLServer.account', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/account', {
      templateUrl: 'modules/account/account.html',
      controller: 'AccountCtrl'
    });
  }])

  .controller('AccountCtrl', ['$rootScope', '$scope', '$http', '$sce', function ($rootScope, $scope, $http, $sce) {
    $http.post('/api', "ADMIN PRIVILEGES " + $rootScope.currentUser, { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
      $scope.privileges = response.data.results;
    }, function (response) {
      $scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
    });
    $scope.onPasswordChange = function () {
      var pass1 = document.getElementById('field-password-1').value;
      var pass2 = document.getElementById('field-password-2').value;
      if (pass1 !== pass2) {
        $scope.messages = $sce.trustAsHtml(getError("Passwords do not match!"));
        return;
      }
      $http.post('/api', "ADMIN CHANGE PASSWORD " + pass1, { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
        $scope.messages = $sce.trustAsHtml(getSuccess("Password was successfully changed."));
      }, function (response) {
        $scope.messages = $sce.trustAsHtml(getErrorFor(response.status, response.data));
      });
      document.getElementById('field-password-1').value = "";
      document.getElementById('field-password-2').value = "";
    };
  }]);