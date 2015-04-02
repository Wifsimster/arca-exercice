angular.module('ArcaApp', ['ngTasty', 'toaster'])
    .controller('AppCtrl', function ($scope, $http, toaster) {

        $scope.init = {
            'count': 10,
            'page': 1,
            'sortBy': 'timestamp',
            'sortOrder': 'asc'
        };

        var header = [
            {
                "key": "timestamp",
                "name": "Timestamp"
            },
            {
                "key": "value",
                "name": "Value"
            },
            {
                "key": "country",
                "name": "Country"
            },
            {
                "key": "line",
                "name": "Line"
            }
        ];

        $scope.getResource = function (params, paramsObj) {
            var urlApi = '/data.' + params;
            return $http.get(urlApi).then(function (response) {
                if (response.status == 200) {
                    return {
                        'rows': response.data.dataEntities,
                        'header': header,
                        'pagination': {
                            count: response.data.count,
                            page: response.data.page,
                            pages: response.data.pages,
                            size: response.data.size
                        },
                        'sortBy': response.data.sortBy,
                        'sortOrder': response.data.sortOrder
                    }
                } else {
                    console.error(response);
                }
            });
        };

        $scope.extract = function() {
            $http.get('/job/start').then(function (response) {
                if (response.status == 200) {
                    console.log(response);
                    toaster.pop('success', "Success", response.data);
                } else {
                    console.error(response);
                    toaster.pop('error', "Error", response.status);
                }
            });
        };

        $scope.stop = function() {
            $http.get('/job/stop').then(function (response) {
                if (response.status == 200) {
                    toaster.pop('success', "Success", "Job stop !");
                } else {
                    console.error(response);
                    toaster.pop('error', "Error", response.status);
                }
            });
        };

        $scope.status = function() {
            $http.get('/job/status').then(function (response) {
                if (response.status == 200) {
                    console.log(response);
                    toaster.pop('info', "Status", response.data);
                } else {
                    console.error(response);
                    toaster.pop('error', "Error", response.status);
                }
            });
        };

    });
