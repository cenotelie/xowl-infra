'use strict';

angular.module('xOWLServer.users', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/users', {
    templateUrl: 'modules/users/users.html',
    controller: 'UsersCtrl'
  });
}])

.controller('UsersCtrl', [function() {

}]);