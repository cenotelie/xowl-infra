'use strict';

angular.module('xOWLServer.databases', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/databases', {
      templateUrl: 'modules/databases/databases.html',
      controller: 'DatabasesCtrl'
    });
  }])

  .controller('DatabasesCtrl', ['$scope', '$http', '$sce', function ($scope, $http, $sce) {
    $http.post('/api', "ADMIN LIST DATABASES", { headers: { "Content-Type": "application/x-xowl-xsp" } }).then(function (response) {
      $scope.databases = response.data.results;
    }, function (response) {
      $scope.messages = $sce.trustAsHtml(MSG_ERROR);
    });
  }]);

var MSG_ERROR = "<div class='alert alert-danger alert-dismissible' role='alert' style='margin-top: 20px;'>" +
  "<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>" +
  "<span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span>" +
  " Error while accessing the data!" +
  "</div>";