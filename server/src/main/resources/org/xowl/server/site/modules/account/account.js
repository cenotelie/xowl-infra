'use strict';

angular.module('xOWLServer.account', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/account', {
      templateUrl: 'modules/account/account.html',
      controller: 'AccountCtrl'
    });
  }])

  .controller('AccountCtrl', ['$rootScope', '$scope', '$sce', '$http', function ($rootScope, $scope, $sce, $http) {
    XOWL_ANGULAR_HTTP = $http;
    xowlUserPrivileges(function (code, type, content) {
      if (code === 200) {
        $scope.privileges = content;
      } else {
        $scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
      }
    }, $rootScope.currentUser);

    $scope.onPasswordChange = function () {
      doChangePassword($rootScope, $scope, $sce);
    };
  }]);


function doChangePassword($rootScope, $scope, $sce) {
  var pass1 = document.getElementById('field-password-1').value;
  var pass2 = document.getElementById('field-password-2').value;
  if (pass1 !== pass2) {
    $scope.messages = $sce.trustAsHtml(getError("Passwords do not match!"));
    return;
  }
  xowlChangePassword(function (code, type, content) {
    if (code === 200) {
      $scope.messages = $sce.trustAsHtml(getSuccess("Password was successfully changed."));
    } else {
      $scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
    }
  }, pass1);
  document.getElementById('field-password-1').value = "";
  document.getElementById('field-password-2').value = "";
}