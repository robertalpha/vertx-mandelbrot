'use strict';

/**
 * @ngdoc overview
 * @name resourcesApp
 * @description
 * # resourcesApp
 *
 * Main module of the application.
 */
angular
  .module('mandelbrotApp', [
    'ngAnimate',
    'ngResource',
    'ngRoute'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'app/views/front.html',
        controller: 'FrontController'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
