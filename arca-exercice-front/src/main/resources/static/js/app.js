var app = angular.module('ArcaApp', ['ngTasty', 'toaster', 'ngRoute', 'nvd3']);

// App configuration
app.config(['$routeProvider', '$locationProvider',
    function ($routeProvider, $locationProvider) {
        $routeProvider
            .when('/', {
                templateUrl: '/partials/home.html',
                controller: 'HomeCtrl'
            })
            .when('/chart', {
                templateUrl: '/partials/chart.html',
                controller: 'ChartCtrl'
            })
            .when('/table', {
                templateUrl: '/partials/table.html',
                controller: 'TableCtrl'
            });

        $locationProvider.html5Mode({enabled: true, requireBase: false});
    }]);

app.controller('AppCtrl', function ($rootScope, $scope, $http) {

    $scope.url = "";

    $rootScope.dataCount = 0;

});
