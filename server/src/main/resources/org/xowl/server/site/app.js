'use strict';

// Declare app level module which depends on views, and components
angular.module('xOWLServer', [
  'ngRoute',
  'angularBasicAuth',
  'LocalStorageModule',
  'xOWLServer.login',
  'xOWLServer.server',
  'xOWLServer.account',
  'xOWLServer.databases',
  'xOWLServer.database',
  'xOWLServer.users'
])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.otherwise({ redirectTo: '/login' });
  }])

  .controller('xOWLServerCtrl', ['$rootScope', 'localStorageService', function ($rootScope, localStorage) {
    $rootScope.currentUser = localStorage.get('username');
    $rootScope.$on('login', function () {
      $rootScope.currentUser = localStorage.get('username');
    });
    $rootScope.$on('logout', function () {
      $rootScope.currentUser = null;
    });
  }]);

var MSG_ERROR_CONNECTION = "Error while accessing the server!";

function getError(msg) {
  return "<div class='alert alert-danger alert-dismissible' role='alert' style='margin-top: 20px;'>" +
    "<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>" +
    "<span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span> " + msg + "</div>";
}

function getSuccess(msg) {
  return "<div class='alert alert-success alert-dismissible' role='alert' style='margin-top: 20px;'>" +
    "<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>" +
    "<span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span> " + msg + "</div>";
}