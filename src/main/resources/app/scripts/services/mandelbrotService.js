angular.module('mandelbrotApp').factory('MandelbrotService', function ($http) {
    var mandelbrotService = {};

    mandelbrotService.getImage = function (configuration) {
        return $http({
            method: 'POST',
            url: 'api/mandelbrotpart',
            data: configuration,  // pass in data as strings
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}  // set the headers so angular passing info as form data (not request payload)
        }).success(function (data, status, $scope) {
            return data;
        }).error(function (data, status, headers, config) {
            return data;
        });
    };

    return mandelbrotService;
});