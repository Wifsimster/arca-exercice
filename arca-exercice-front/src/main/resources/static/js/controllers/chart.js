app.controller('ChartCtrl', function ($scope, $http, $filter) {

    $scope.loading = true;


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
            useInteractiveGuideline: true,
            xAxis: {
                axisLabel: 'Date',
                tickFormat : function(d) {
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
        values: [{"x":"2008-12-13","y":"78"},{"x":"2008-12-14","y":"18"},{"x":"2008-12-15","y":"61"}]
    }];

     //Get days list
    $http.get('/sum/by/day').then(function (response) {
        if (response.status == 200) {

            var sumByDay = [];

            $.map(response.data, function (value, index) {
                sumByDay.push({x: index, y: value});
            });

            $scope.sumByDay = sumByDay;
            $scope.loading = true;

            //$scope.data = [{
            //    key: "Data",
            //    values: [sumByDay]
            //}];

        } else {
            console.error(response);
        }
    });
});