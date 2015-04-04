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

app.controller('AppCtrl', function ($scope, $http) {

    $scope.url = "";

    $rootScope.dataCount = 0;

    $http.get('/data/count').then(function (response) {
        if (response.status == 200) {
            $rootScope.dataCount = response.data;
        } else {
            console.error(response);
        }
    });

});
