var myAppModule = angular.module('threadfix')

myAppModule.controller('ApplicationsIndexController',
    function($scope, $log, $modal, $upload, $window, $rootScope, $timeout, tfEncoder, threadfixAPIService, appUsageService, customSeverityService, $http) {

        // Initialize
        $scope.initialized = false;

        $scope.numAppsPerTeam = 10;

        var nameCompare = function(a,b) {
            return a.name.localeCompare(b.name);
        };

        $scope.active = function(app) {
            return app.active;
        };

        // since we need the csrfToken to make the request, we need to wait until it's initialized
        $scope.$on('rootScopeInitialized', function() {
            threadfixAPIService.getTeams().
                success(function(data, status, headers, config) {
                    $scope.initialized = true;

                    if (data.success) {
                        $scope.teams = data.object.teams;

                        $scope.canEditIds = data.object.canEditIds;
                        $scope.canUploadIds = data.object.canUploadIds;

                        customSeverityService.setSeverities(data.object.genericSeverities);

                        $scope.teams.forEach(function(team) {

                            team.page = 1;

                            team.showEditButton = $scope.canEditIds.indexOf(team.id) !== -1;

                        });

                        $scope.teams.sort(nameCompare);

                        if ($scope.teams.length == 0 && $scope.canCreateTeams) {
                            $scope.openTeamModal();
                        }
                    } else {
                        $scope.output = "Failure. Message was : " + data.message;
                    }
                }).
                error(function(data, status, headers, config) {
                    $scope.errorMessage = "Failed to retrieve team list. HTTP status was " + status;
                });
        });
        // Table animations

        $scope.toggle = function(team) {

            if (typeof team.expanded === "undefined") {
                team.expanded = false;
            }

            team.expanded = !team.expanded;

            loadGraph(team);
        };

        $scope.expand = function() {
            $scope.teams.forEach(function(team) {
                team.expanded = true;
                loadGraph(team);
            });
        };

        $scope.contract = function() {
            $scope.teams.forEach(function(team) {
                team.expanded = false;
            });
        };

        var loadGraph = function(team) {

            $scope.searchApps(team.lastSearchString, team);

            if (team.report == null) {
                team.loading = true;
                threadfixAPIService.loadAppTableReport(team.id).
                    success(function(data, status, headers, config) {
                        team.loading = false;
                        if (data.object && data.object.length>0 && data.object[0].Critical==0
                            && data.object[0].High ==0
                            && data.object[0].Medium == 0
                            && data.object[0].Low == 0
                            && data.object[0].Info ==0)
                            team.report = undefined;
                        else
                            team.report = data.object;

                    }).
                    error(function(data, status, headers, config) {

                        // TODO improve error handling and pass something back to the users
                        team.report = true;
                        team.reportFailed = true;
                        team.loading = false;
                    });
            }
        };

        // Modal functions

        $scope.openTeamModal = function() {
            var modalInstance = $modal.open({
                templateUrl: 'newTeamModal.html',
                controller: 'GenericModalController',
                resolve: {
                    url: function() {
                        return tfEncoder.encode("/organizations/modalAdd");
                    },
                    object: function() {
                        return {};
                    },
                    config: function() {
                        return {};
                    },
                    buttonText: function() {
                        return "Add Team";
                    }
                }
            });

            modalInstance.result.then(function (object) {
                if (!$scope.teams || $scope.teams.length === 0) {
                    $scope.teams = [];
                }

                var newTeam = object.team;
                newTeam.showEditButton = object.canEdit;

                $scope.teams.push(newTeam);

                $scope.teams.sort(nameCompare);

                $scope.successMessage = "Successfully added team " + newTeam.name;

            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.showAppLimitMessage = function(number) {
            if (number != -1)
                alert('You have reached the application limit of ' + number + ' for your current license. To upgrade your license, please contact Denim Group.');
            else
                alert('It appears that your license file is not valid, the operation is currently not available. Please contact Denim Group.');
        };

        $scope.openAppModal = function (team) {

            var application = {
                team: {
                    id: team.id,
                    name: team.name
                },
                applicationCriticality: {
                    id: 2
                },
                frameworkType: 'DETECT'
            };

            var modalInstance = $modal.open({
                templateUrl: 'newApplicationModal.html',
                controller: 'ModalControllerWithConfig',
                resolve: {
                    url: function() {
                        return tfEncoder.encode("/organizations/" + team.id + "/modalAddApp");
                    },
                    object: function () {
                        return application;
                    },
                    config: function() {
                        return {};
                    },
                    buttonText: function() {
                        return "Add Application";
                    }
                }
            });

            modalInstance.result.then(function (object) {

                if (!team.applications || team.applications.length === 0) {
                    team.applications = [];
                }

                var newApplication = object.application;

                newApplication.active = true;
                newApplication.criticalVulnCount = 0;
                newApplication.highVulnCount = 0;
                newApplication.mediumVulnCount = 0;
                newApplication.lowVulnCount = 0;
                newApplication.totalVulnCount = 0;
                newApplication.infoVulnCount = 0;

                newApplication.showUploadScanButton = object.uploadScan;
                
                if (newApplication.showUploadScanButton) {
                    if (!$scope.canUploadIds || $scope.canUploadIds.length === 0) {
                        $scope.canUploadIds = [];
                    }
                    $scope.canUploadIds.push(newApplication.id);
                }

                team.applications.push(newApplication);

                team.applications.sort(nameCompare);

                $scope.successMessage = $scope.successMessage = appUsageService.getUsage(object);

            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.showUploadForm = function(team, app) {
            var modalInstance = $modal.open({
                templateUrl: 'uploadScanForm.html',
                controller: 'UploadScanController',
                resolve: {
                    url: function() {
                        return "/organizations/" + team.id + "/applications/" + app.id + "/upload/remote";
                    },
                    files: function() {
                        return false;
                    }
                }
            });

            modalInstance.result.then(function (updatedTeam) {
                $log.info("Successfully uploaded scan.");
                $scope.successMessage = "Successfully uploaded scan.";
                updateTeam(team, updatedTeam);
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });

        };

        $scope.fileModalOn = false;

        $scope.onFileSelect = function(team, app, $files) {
            if ($scope.fileModalOn) {
                $scope.$on('files', function(event, files) {
                    $rootScope.$broadcast('files', files);
                });
            } else {
                var modalInstance = $modal.open({
                    templateUrl: 'uploadScanForm.html',
                    controller: 'UploadScanController',
                    resolve: {
                        url: function() {
                            return "/organizations/" + team.id + "/applications/" + app.id + "/upload/remote";
                        },
                        files: function() {
                            return $files;
                        }
                    }
                });

                $scope.fileModalOn = true;

                modalInstance.result.then(function (updatedTeam) {
                    $log.info("Successfully uploaded scan.");
                    $scope.successMessage = "Successfully uploaded scan.";
                    updateTeam(team, updatedTeam);
                    $scope.fileModalOn = false;
                }, function () {
                    $log.info('Modal dismissed at: ' + new Date());
                    $scope.fileModalOn = false;
                });
            }
        };

        $scope.goTo = function(team) {
            $window.location.href = tfEncoder.encode("/organizations/" + team.id);
        };

        $scope.searchApps = function(searchText, team) {

            if (team.lastSearchString && team.lastSearchString === searchText &&
                team.lastNumber === $scope.numAppsPerTeam &&
                team.lastPage === team.page) {
                return;
            }

            var searchObject = {
                "searchString" : searchText,
                "page" : team.page,
                "number" : $scope.numAppsPerTeam
            };

            $http.post(tfEncoder.encode("/organizations/" + team.id + "/search"), searchObject).
                then(function(response) {
                    var data = response.data;
                    if (data.success) {
                        team.countApps = data.object.countApps;
                        team.applications = data.object.applications;

                        team.applications.forEach(function(application) {
                            application.showUploadScanButton = $scope.canUploadIds.indexOf(application.id) !== -1;
                        });

                        team.lastSearchString = searchText;
                        team.lastNumber = $scope.numAppsPerTeam;
                        team.lastPage = team.page;
                    } else {
                        $scope.errorMessage = "Failed to receive search results. Message was : " + data.message;
                    }
                });

        };


        $scope.updatePage = function(page, searchString, team) {
            team.page = page;
            $scope.searchApps(searchString, team);
        };

        $scope.goToPage = function(team, app) {
            $window.location.href = tfEncoder.encode("/organizations/" + team.id + "/applications/" + app.id);
        };

        var updateTeam = function(oldTeam, newTeam) {
            newTeam.applications.forEach(function(application) {
                application.showUploadScanButton = $scope.canUploadIds.indexOf(application.id) !== -1;
            });

            newTeam.showEditButton = oldTeam.showEditButton;

            var index = $scope.teams.indexOf(oldTeam);
            if (index > -1) { // let's hope it is
                $scope.teams.splice(index, 1);
            }

            $scope.teams.push(newTeam);
            $scope.teams.sort(nameCompare);

            newTeam.expanded = true;
            newTeam.report = null;
            loadGraph(newTeam);
        }

    });
