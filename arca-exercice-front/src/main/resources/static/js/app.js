var app = angular.module('ArcaApp', ['ngTasty', 'toaster', 'ngRoute', 'Chart']);

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

app.controller('AppCtrl', function ($scope, $http) {

    $scope.url = "";

});
