app.controller('ChartCtrl', function ($rootScope, $scope, $http, toaster) {

    $scope.loaded = false;

    // Example
    $scope.options = {
        chart: {
            type: 'lineChart',
            height: 450,
            margin: {
                top: 20,
                right: 20,
                bottom: 60,
                left: 55
            },
            x: function (d) {
                return new Date(d.x);
            },
            y: function (d) {
                return d.y;
            },
            autorefresh: true,
            useInteractiveGuideline: true,
            xAxis: {
                axisLabel: 'Date',
                tickFormat: function (d) {
                    return d3.time.format('%x')(new Date(d));
                }
            },
            yAxis: {
                axisLabel: 'Value'
            },
            callback: function (chart) {
                console.log("!!! lineChart callback !!!");
            }
        },
        title: {
            enable: true,
            text: 'Sum by date'
        }
    };

    $scope.data = [{
        key: "Data",
        values: []
    }];

    //Get days list
    $http.get('/sum/by/day').then(function (response) {
        $scope.loaded = true;

        if (response.statusCode == 200) {

            var sumByDay = [];

            $.map(response.data, function (value, index) {
                sumByDay.push({x: index, y: value});
            });

            $scope.sumByDay = sumByDay;

            $scope.data[0].values = sumByDay;

        } else {
            console.error(response.data);
            toaster.pop('error', "Error", "Something went wrong getting data.");
        }
    });
});