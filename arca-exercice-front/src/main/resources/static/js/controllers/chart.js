app.controller('ChartCtrl', function ($rootScope, $scope, $http, toaster) {

    $scope.loaded = false;

    // Chart init
    $scope.data = [{
        key: "Data",
        values: []
    }];

    // Parse timestamp into date format
    $scope.xAxisTickFormat_Date_Format = function () {
        return function (d) {
            return d3.time.format('%x')(new Date(d));
        }
    };

    // Tooltip content
    $scope.toolTipContentFunction = function () {
        return function (key, x, y, e, graph) {
            return '<h3>Sum</h3>' +
                '<p>' + y + '</p>'
        }
    };

    //Get days list
    $http.get('/sum/by/day').then(function (response) {
        $scope.loaded = true;

        console.log(response);

        if (response.status == 200 && response.data != null) {

            if (response.data.statusCode == 200) {
                var data = [];
                var sumByDay = {};
                sumByDay.key = "Data";
                sumByDay.values = [];

                $.map(response.data.data, function (value, index) {
                    sumByDay.values.push([index, parseInt(value)]);
                });

                data.push(sumByDay);

                $scope.sumByDay = data;
            }
            if (response.data.statusCode == 400) {
                toaster.pop('error', "Error", response.data.message);
            }

        } else {
            toaster.pop('error', "Error", "Something went wrong getting data.");
        }
    });
});