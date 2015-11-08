'use strict';

angular.module('xOWLServer.logout', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/logout', {
    templateUrl: 'modules/logout/logout.html',
    controller: 'LogoutCtrl'
  });
}])

.controller('LogoutCtrl', [function() {

}]);