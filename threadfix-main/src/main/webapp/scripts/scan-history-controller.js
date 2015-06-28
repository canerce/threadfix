var myAppModule = angular.module('threadfix');

myAppModule.controller('ScanHistoryController', function($scope, $log, $rootScope, $http, $window, tfEncoder, customSeverityService) {

    $scope.initialized = false;

    $scope.pageNumber = 1;
    $scope.numScans = 0;

    // since we need the csrfToken to make the request, we need to wait until it's initialized
    $scope.$on('rootScopeInitialized', function() {
        $scope.loading = true;
        $http.post(tfEncoder.encode("/scans/table/" + $scope.pageNumber)).
            success(function(data, status, headers, config) {
                $scope.initialized = true;

                if (data.success) {
                    $scope.scans = data.object.scanList;
                    $scope.numScans = data.object.numScans;

                    customSeverityService.setSeverities(data.object.genericSeverities);
                } else {
                    $scope.output = "Failure. Message was : " + data.message;
                }
                $scope.loading = false;
            }).
            error(function(data, status, headers, config) {
                $scope.errorMessage = "Failed to retrieve team list. HTTP status was " + status;
                $scope.loading = false;
            });
    });

    $scope.goTo = function(scan) {
        $window.location.href = tfEncoder.encode("/organizations/" + scan.team.id + "/applications/" + scan.app.id + "/scans/" + scan.id);
    };

});
