<%@ include file="/common/taglibs.jsp"%>

<head>
    <title>Scan Result Filters</title>
</head>

<body>
    <%@ include file="/WEB-INF/views/angular-init.jspf"%>
    <div ng-controller="ScanResultFiltersController">
        <h2>Ignore Incoming Results</h2>

        <%@ include file="/WEB-INF/views/successMessage.jspf" %>
        <%@ include file="/WEB-INF/views/errorMessage.jsp" %>
        <%@ include file="newForm.jsp" %>
        <%@ include file="editForm.jsp" %>

        <div id="helpText">
            Scan Result Filters are used to filter out a severity level from a scan type.<br/>
        </div>

        <button class="btn" ng-click="openNewModal()" id="createNewScanResultFilterModalButton">Create New Scan Result Filter</button>

        <div ng-show="loading" style="float:right" class="modal-loading"><div><span class="spinner dark"></span>Loading...</div></div>

        <table id="table" ng-hide="loading" class="table table-striped" style="table-layout:fixed;">
            <thead>
                <tr>
                    <th class="first">Scanner Type</th>
                    <th class="medium">Severity</th>
                    <th class="centered last">Edit / Delete</th>
                </tr>
            </thead>
            <tbody>
                <tr ng-hide="scanResultFilters.length || loading">
                    <td colspan="3" style="text-align:center;">No Scan Result Filter found.</td>
                </tr>
                <tr ng-repeat="filter in scanResultFilters">
                    <td id="scannerType{{filter.channelType.name}}">
                        {{filter.channelType.name}}
                    </td>
                    <td id="scannerType{{filter.genericSeverity.name}}">
                        {{filter.genericSeverity.displayName}}
                    </td>
                    <security:authorize ifAnyGranted="ROLE_CAN_MANAGE_SCAN_RESULT_FILTERS">
                        <td class="centered">
                            <a id="editScanResultFilterButton{{ filter.id }}" class="btn" ng-click="openEditModal(filter)">Edit / Delete</a>
                        </td>
                    </security:authorize>
                </tr>
            </tbody>
        </table>
    </div>
</body>