var myAppModule = angular.module('threadfix')

myAppModule.controller('UploadScanController', function ($scope, $modalInstance, threadFixModalService, files, url, $upload, tfEncoder, $rootScope) {

    $scope.uploading = false;

    $scope.uploadedPercent = 0;

    $scope.isIE = /*@cc_on!@*/false || !!document.documentMode;

    $scope.ready = function() {
        return !$scope.uploading && !$scope.waiting;
    };

    $scope.$on('files', function(event, files) {
        $scope.onFileSelect(files);
    });

    $scope.onFileSelect = function($files) {
        $scope.uploading = true;
        $scope.confirmingScanUploadStyle = false;
        $scope.files = $files;
        if ($files.length > 1 && url.endsWith('/upload/remote')) {
            $scope.confirmingScanUploadStyle = true;
        } else {
            uploadScans(url, $files);
        }
    };

    $scope.uploadAsMultiScans = function(isMultiScans) {
        $scope.confirmingScanUploadStyle = false;
        var newUrl = (isMultiScans) ? url + "/bulk" : url;
        uploadScans(newUrl, $scope.files);
    };

    var uploadScans = function(requestUrl, $files) {
        //$files: an array of files selected, each file has name, size, and type.
        $scope.upload = $upload.upload({
            url: tfEncoder.encode(requestUrl),
            method: "POST",
            headers: {'Accept': 'application/json'},
            // withCredentials: true,
            file: $files //upload multiple files, this feature only works in HTML5 FromData browsers
            /* set file formData name for 'Content-Desposition' header. Default: 'file' */
            //fileFormDataName: myFile, //OR for HTML5 multiple upload only a list: ['name1', 'name2', ...]
            /* customize how data is added to formData. See #40#issuecomment-28612000 for example */
            //formDataAppender: function(formData, key, val){} //#40#issuecomment-28612000
        }).progress(function(evt) {
            $scope.uploadedPercent = parseInt(100.0 * evt.loaded / evt.total);
            if ($scope.uploading && $scope.uploadedPercent == 100) {
                $scope.uploading = false;
                $scope.waiting = true;
            }
        }).success(function(data, status, headers, config) {

            if (data.success) {
                $modalInstance.close(data.object); // pass the team back up to update stats
                $rootScope.$broadcast('scansUploaded');
            } else {
                if (!data.message) {
                    // If there's no message, this is often an uncaught RuntimeException. It should be stored in Error Messages.
                    $scope.alerts = [{ type: 'danger', msg: "An error has occurred. Please go to the 'Error Messages' page (under the cog) for more details." }];
                } else {
                    $scope.alerts = [{ type: 'danger', msg: data.message }];
                }
                $scope.showError = true;
                $scope.waiting = false;
                $scope.uploading = false;
            }
        });
    };

    $scope.alerts = [];

    $scope.closeAlert = function(index) {
        $scope.alerts.splice(index, 1);
    };

    if (files) {
        $scope.onFileSelect(files);
    }

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
