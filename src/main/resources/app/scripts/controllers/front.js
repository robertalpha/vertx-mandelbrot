'use strict';

angular.module('mandelbrotApp').controller('FrontController',
    function ($scope, $resource, $interval, MandelbrotService) {

        var widthy = $(window).width() - 22;
        var heighty = $(window).height() - 50;

        $scope.configuration =
        {
            "width": widthy,
            "height": heighty,
            "max": 5000,
            "zoom": 200.0,
            "xmove": (widthy / 2),
            "ymove": (heighty / 2),
            "dim": 200,
            "rowDim": -1,
            "columnDim": -2
        };

        $scope.mousex = 0;
        $scope.mousey = 0;
        $scope.rectx = 0;
        $scope.recty = 0;


        $scope.part = {image64: "", status: "not yet loaded"};

        $scope.images = [];


        $scope.getGridRange = function (pixels) {
            var maxNum = Math.floor(pixels / $scope.configuration.dim);
            return Array.apply(null, {length: maxNum}).map(Number.call, Number);
        };

        $scope.getImage = function (x, y) {
            var imageconfig = jQuery.extend({}, $scope.configuration); // copy contents
            imageconfig.rowDim = x;
            imageconfig.columnDim = y;
            MandelbrotService.getImage(imageconfig).then(function (data) {
                var xarr = ( typeof $scope.images[x] != 'undefined' && $scope.images[x] instanceof Array ) ? $scope.images[x] : [];
                xarr[y] = data.data.image64;
                $scope.images[x] = xarr;
            });
        };

        $scope.reload = function () {
            $scope.getGridRange($scope.configuration.width).forEach(function (x) {
                $scope.getGridRange($scope.configuration.height).forEach(function (y) {
                    $scope.getImage(y, x);
                });
            });
        };

        $scope.reload();

        $(function () {
            $("body").click(function (e) {
                if (e.target.id == "mandelgrid" || $(e.target).parents("#mandelgrid").size()) {
                    var x;
                    var y;
                    if (e.pageX || e.pageY) {
                        x = e.pageX;
                        y = e.pageY;
                    } else {
                        x = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
                        y = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
                    }

                    var p = $( "#mandelgrid" );
                    var rect = p.position();
                    $scope.rectx = rect.left;
                    $scope.recty = rect.top;

                    $scope.mousex = x;
                    $scope.mousey = y;


                    $scope.configuration.xmove+=(x-($scope.configuration.width)/2) * (1.0/$scope.configuration.zoom);
                    $scope.configuration.ymove+=(y-($scope.configuration.height)/2) * (1.0/$scope.configuration.zoom);
                    $scope.configuration.zoom=$scope.configuration.zoom*2;

                    $scope.reload();

                    $scope.$apply();
                }
            });
        });
    });
