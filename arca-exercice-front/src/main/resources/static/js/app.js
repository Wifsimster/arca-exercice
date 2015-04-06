var app = angular.module('ArcaApp', ['ngTasty', 'toaster', 'ngRoute', 'nvd3ChartDirectives']);

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

app.controller('AppCtrl', function ($rootScope, $scope, $http, toaster) {

    $scope.url = "";

    $rootScope.dataCount = 0;

    // Get data count
    $http.get('/data/count').then(function (response) {
        console.log(response);
        if (response.status == 200 && response.data != null) {
            if (response.data.statusCode == 200) {
                $rootScope.dataCount = response.data.data;
            }
            if (response.data.statusCode == 400) {
                toaster.pop('error', "Error", response.data.message);
            }
        } else {
            toaster.pop('error', "Error", response.status);
        }
    });

});
