app.controller('TableCtrl', function ($scope, $http, toaster) {
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

    // Get countries list
    $http.get('/sum/by/country').then(function (response) {
        console.log(response);
        var data = JSON.parse(response);
        console.log(data);

    });
});