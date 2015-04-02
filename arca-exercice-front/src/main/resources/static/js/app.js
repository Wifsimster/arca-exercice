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

        // Batch state
        $scope.execution = false;

        $scope.extract = function () {
            $http.get('/job/start').then(function (response) {
                if (response.status == 200) {
                    if (response.data.statusCode == 200) {
                        toaster.pop('success', "Success", response.data.message);
                        $scope.execution = true;
                    } else {
                        toaster.pop('error', "Error", response.data.message);
                    }
                } else {
                    toaster.pop('error', "Error", response.status);
                }
            });
        };

        $scope.stop = function () {
            $http.get('/job/stop').then(function (response) {
                if (response.status == 200) {
                    if (response.data.statusCode == 200) {
                        $scope.execution = false;
                        toaster.pop('success', "Success", response.data.message);
                    } else {
                        toaster.pop('error', "Error", response.data.message);
                    }
                } else {
                    console.error(response);
                    toaster.pop('error', "Error", response.status);
                }
            });
        };

        $scope.status = function () {
            $http.get('/job/status').then(function (response) {
                console.log(response);
                if (response.status == 200) {
                    if (response.data.statusCode == 200) {
                        info = response.data.data;
                        toaster.pop('info', "Info", "<p> Status : " + info.status + "<br>Write count : " + info.writeCount + "</p>", 5000, 'trustedHtml');
                    } else {
                        toaster.pop('error', "Error", response.data.message);
                    }
                } else {
                    console.error(response);
                    toaster.pop('error', "Error", response.status);
                }
            });
        };

    });
