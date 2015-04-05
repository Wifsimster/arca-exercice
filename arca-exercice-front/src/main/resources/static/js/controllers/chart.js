app.controller('ChartCtrl', function ($rootScope, $scope, $http, toaster) {

    $scope.loaded = false;

    // Example
    $scope.options = {
        chart: {
            type: 'lineChart',
            autorefresh: true,
            useInteractiveGuideline: true,
            height: 450,
            margin: {
                top: 20,
                right: 20,
                bottom: 40,
                left: 55
            },
            x: function (d) {
                return new Date(d.x);
            },
            y: function (d) {
                return d.y;
            },
            xAxis: {
                axisLabel: 'Date',
                tickFormat: function (d) {
                    return d3.time.format('%x')(new Date(d));
                }
            },
            yAxis: {
                axisLabel: 'Value'
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


    $scope.xAxisTickFormat_Date_Format = function(){
        return function(d){
            console.log(d);
            console.log(d3.time.format('%x')(new Date(d)));
            return d3.time.format('%x')(new Date(d));
        }
    };

    //Get days list
    $http.get('/sum/by/day').then(function (response) {
        $scope.loaded = true;

        console.log(response);

        if (response.status == 200) {

            var data = [];
            var sumByDay = {};
            sumByDay.key = "Data";
            sumByDay.values = [];

            $.map(response.data.data, function (value, index) {
                sumByDay.values.push([index, parseInt(value)]);
            });

            data.push(sumByDay);

            $scope.sumByDay = data;

            // Refresh chart
            //$scope.api.refresh();

            // Show chart
            //$scope.options.chart.visible = true;

        } else {
            toaster.pop('error', "Error", "Something went wrong getting data.");
        }
    });
});