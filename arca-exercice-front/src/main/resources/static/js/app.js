angular.module('ArcaApp', ['ngTasty'])
    .controller('AppCtrl', function ($scope, $http) {

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
            console.log("Post - extract job");
            $http.get('/extract').then(function (response) {
                if (response.status == 200) {
                    console.log(response);
                } else {
                    console.error(response);
                }
            });
        };

        $scope.stop = function() {
            console.log("Post - stop job");
            $http.get('/stop').then(function (response) {
                if (response.status == 200) {
                    console.log(response);
                } else {
                    console.error(response);
                }
            });
        };

    });
