'use strict';

angular.module('mandelbrotApp').controller('FrontController',
    function ($scope, $resource, $interval, MandelbrotService) {
        var configuration =
        {
            "width": 1920,
            "height": 1080,
            "max": 1000,
            "zoom": 50.0,
            "xmove": (1920 / 2),
            "ymove": (1080 / 2),
            "dim": 400,
            "rowDim": 1,
            "columnDim": 2
        };


        $scope.part = {image64: "", status: "not yet loaded"};

        MandelbrotService.getImage(configuration).then(function (data) {
            $scope.part = data.data;
        });

        $scope.getImage = function () {
            return $scope.part.image64;
        }
    });
