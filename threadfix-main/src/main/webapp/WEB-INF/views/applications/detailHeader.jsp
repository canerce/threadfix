<%@ include file="/common/taglibs.jsp"%>
<spring:url value="/organizations/{orgId}" var="orgUrl">
    <spring:param name="orgId" value="${ application.organization.id }"/>
</spring:url>


<ul class="breadcrumb">
    <li><a id="applicationsIndexLink" href="<spring:url value="/teams"/>">Applications Index</a> <span class="divider">/</span></li>
    <li ng-show="application"><a id="teamLink" class="pointer" ng-click="goToTeam(application)">Team: {{ application.team.name }}</a> <span class="divider">/</span></li>
    <li ng-show="application" class="active">Application: {{ application.name }}</li>
    <li ng-hide="application"><a href="${ fn:escapeXml(orgUrl) }">Team: <c:out value="${ application.organization.name }"/></a> <span class="divider">/</span></li>
    <li ng-hide="application" class="active">Application: <c:out value="${ application.name }"/></li>
</ul>

<div ng-controller="ApplicationPageModalController">
    <h2 style="padding-bottom:5px;line-height:1">

        <span ng-if="!config" id="nameText" style="padding-top:5px;"><c:out value="${ application.name }"/></span>
        <span ng-if="config" id="nameText" style="padding-top:5px;">{{ config.application.name }}</span>
        <c:if test="${ not empty canManageApplications }">
            <div id="btnDiv1" class="btn-group">
                <button id="actionButton1" class="btn dropdown-toggle" data-toggle="dropdown" type="button">
                    Action <span class="caret"></span>
                </button>
                <ul class="dropdown-menu">

                    <c:if test="${ canManageApplications }">
                        <li><a class="pointer" id="editApplicationModalButton" ng-click="showEditModal()">Edit / Delete</a></li>
                    </c:if>
                    <c:if test="${ !canManageApplications }">
                        <li><a class="pointer" id="viewApplicationModalButton" ng-click="viewApplicationDetail()">Details	</a></li>
                    </c:if>

                    <c:if test="${ canManageVulnFilters }">
                        <spring:url value="/organizations/{orgId}/applications/{appId}/filters" var="vulnFiltersUrl">
                            <spring:param name="orgId" value="${ application.organization.id }"/>
                            <spring:param name="appId" value="${ application.id }"/>
                        </spring:url>
                        <li>
                            <a class="pointer" id="editVulnerabilityFiltersButton" href="<c:out value="${ vulnFiltersUrl }"/>" data-toggle="modal">
                                Customize ThreadFix Vulnerability Types and Severities
                            </a>
                        </li>
                    </c:if>

                    <c:if test="${ (canManageApplications || canManageUsers) && isEnterprise}">
                        <li><a class="pointer" id="userListModelButton" ng-click="showUsers()">View Permissible Users</a></li>
                    </c:if>

                    <c:if test="${ canUploadScans }">
                        <li><a class="pointer" id="uploadScanModalLink" ng-click="showUploadForm(false)">Upload Scan</a></li>
                        <li><a class="pointer" id="addManualFindingModalLink" ng-click="submitFindingForm()">Add Manual Finding</a></li>
                        <li ng-show="config.application.defectTracker">
                            <a class="pointer" id="updateDefectsLink" ng-click="updateDefectStatus()">
                                Update Defect Status
                            </a>
                        </li>
                        <li ng-show="config.application.grcApplication">
                            <a id="updateGRCControlsLink" ng-click="updateControlStatus()">
                                Update GRC Control Status
                            </a>
                        </li>
                    </c:if>

                    <c:if test="${ canManageApplications }">
                        <li><a class="pointer" id="editVersionModalButton" ng-click="manageVersions()">Manage Versions</a></li>
                    </c:if>
                    <c:if test="${ !canManageApplications }">
                        <li><a class="pointer" id="viewVersionModalButton" ng-click="manageVersions()">View Versions</a></li>
                    </c:if>

                </ul>
            </div>
        </c:if>
    </h2>

    <%@ include file="/WEB-INF/views/successMessage.jspf" %>
    <%@ include file="/WEB-INF/views/errorMessage.jspf"%>

    <div ng-show="config.application.policyStatuses">
        Policy Status
        <span id="appFilterPassing" ng-show="config.passFilters" style="cursor: pointer" ng-click="setTab('Policy')" class="badge" ng-class="{'badge-ac-status-passing': true}">PASSING</span>
        <span id="appFilterFailing" ng-hide="config.passFilters" style="cursor: pointer" ng-click="setTab('Policy')" class="badge" ng-class="{'badge-ac-status-failing': true}">FAILING</span>
    </div>
    <c:if test="${ canManageTags }">
        <span style="font-weight: bold;" ng-repeat="appTag in config.applicationTags" class="pointer badge" id="appTag{{ $index }}" ng-click="goToTag(appTag)" class="badge" ng-class="{'badge-application-tag': true}">{{appTag.name}}&nbsp;
        </span>
    </c:if>
    <c:if test="${ !canManageTags }">
        <span style="font-weight: bold;" id="appLabelTag{{ $index }}" ng-repeat="appTag in config.applicationTags" class="badge" ng-class="{'badge-application-tag': true}">{{appTag.name}}</span>
    </c:if>
</div>

<div id="editApplicationModal" class="modal hide fade" tabindex="-1"
     role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div id="editAppFormDiv">
        <%@ include file="/WEB-INF/views/applications/forms/editApplicationForm.jsp" %>
    </div>
</div>
