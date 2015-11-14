'use strict';

// Declare app level module which depends on views, and components
angular.module('xOWLServer', [
  'ngRoute',
  'angularBasicAuth',
  'LocalStorageModule',
  'xOWLServer.login',
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