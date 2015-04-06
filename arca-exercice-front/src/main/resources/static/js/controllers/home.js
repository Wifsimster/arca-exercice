app.controller('HomeCtrl', function ($rootScope, $scope, $http, toaster) {

    // Batch state
    $rootScope.execution = false;
    $scope.loading = false;

    // Check if job is already started
    $http.get('/job/status').then(function (response) {
        if (response.status == 200 && response.data != null) {
            if (response.data.statusCode == "200") {
                // Job is already started
                $rootScope.execution = true;
            }
            if (response.data.statusCode == 400) {
                toaster.pop('error', "Error", response.data.message);
            }
        } else {
            toaster.pop('error', "Error", response.status);
        }
    });

    $scope.start = function () {
        $scope.loading = true;

        $http.get('/job/start').then(function (response) {
            $scope.loading = false;
            if (response.status == 200 && response.data != null) {
                if (response.data.statusCode == 200) {
                    toaster.pop('success', "Success", response.data.message);
                    $rootScope.execution = true;
                }
                if (response.data.statusCode == 400) {
                    toaster.pop('error', "Error", response.data.message);
                }
            } else {
                toaster.pop('error', "Error", response.status);
            }
        });
    };

    $scope.stop = function () {
        $http.get('/job/stop').then(function (response) {
            if (response.status == 200 && response.data != null) {
                if (response.data.statusCode == 200) {
                    $rootScope.execution = false;
                    toaster.pop('success', "Success", response.data.message);
                }
                if (response.data.statusCode == 400) {
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
            if (response.status == 200 && response.data != null) {
                if (response.data.statusCode == 200) {
                    info = response.data.data;
                    toaster.pop('info', "Info", "<p>" + response.data.message + "<br/> Status : " + info.status + "<br>Write count : " + info.writeCount + "</p>", 5000, 'trustedHtml');
                }
                if (response.data.statusCode == 400) {
                    toaster.pop('error', "Error", response.data.message);
                }
            } else {
                console.error(response);
                toaster.pop('error', "Error", response.status);
            }
        });
    };
});