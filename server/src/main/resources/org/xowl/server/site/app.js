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
  'xOWLServer.users',
  'xOWLServer.user'
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


var MSG_ERROR_BAD_REQUEST = "Oops, wrong request.";
var MSG_ERROR_UNAUTHORIZED = "You must be logged in to perform this operation.";
var MSG_ERROR_FORBIDDEN = "You are not authorized to perform this operation.";
var MSG_ERROR_NOT_FOUND = "Can't find the requested data.";
var MSG_ERROR_INTERNAL_ERROR = "Something wrong happened ...";
var MSG_ERROR_CONNECTION = "Error while accessing the server!";

function getErrorFor(code, content) {
  if (content != null) {
    if (content == '' || (typeof content) == 'undefined')
      content = null;
  }
  switch (code) {
    case 400:
      return getError(MSG_ERROR_BAD_REQUEST + (content !== null ? "<br/>" + content : ""));
    case 401:
      return getError(MSG_ERROR_UNAUTHORIZED + (content !== null ? "<br/>" + content : ""));
    case 403:
      return getError(MSG_ERROR_FORBIDDEN + (content !== null ? "<br/>" + content : ""));
    case 404:
      return getError(MSG_ERROR_NOT_FOUND + (content !== null ? "<br/>" + content : ""));
    case 500:
      return getError(MSG_ERROR_INTERNAL_ERROR + (content !== null ? "<br/>" + content : ""));
    default:
      return getError(MSG_ERROR_CONNECTION + (content !== null ? "<br/>" + content : ""));
  }
}

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