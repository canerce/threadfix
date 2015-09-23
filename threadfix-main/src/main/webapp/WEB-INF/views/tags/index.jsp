<%@ include file="/common/taglibs.jsp"%>

<head>
    <title>Tags</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/tags-page-controller.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/modal-controller-with-config.js"></script>
</head>

<body id="tags" ng-controller="TagsPageController">

<h2>Tags</h2>

<%@ include file="/WEB-INF/views/successMessage.jspf" %>
<%@ include file="/WEB-INF/views/errorMessage.jspf" %>
<%@ include file="/WEB-INF/views/angular-init.jspf"%>
<%@ include file="/WEB-INF/views/tags/createTagForm.jsp" %>
<%@ include file="/WEB-INF/views/tags/editTagForm.jsp" %>

<div ng-hide="initialized" class="spinner-div"><span class="spinner dark"></span>Loading</div><br>

<a ng-show="initialized" id="createTagModalButton" ng-click="openNewModal()" class="btn">Create Tag</a>

<h3>Application Tags</h3>

<table ng-show="initialized" class="table table-striped">
    <thead>
    <tr>
        <th class="long first">Name</th>
        <th class="centered">Edit / Delete</th>
        <th class=" last">
                    <span class="vuln-tree-checkbox">
                        Check All
                        <input id="checkAll" type="checkbox" ng-model="tagChecked.allChecked" ng-change="applyAllTagsChecked(tagChecked.allChecked)" style="margin-top: -3px"/>
                    </span>
        </th>
    </tr>
    </thead>
    <tbody id="tagTableBody">
    <tr ng-hide="tags" class="bodyRow">
        <td colspan="4" style="text-align:center;">No Application Tags found.</td>
    </tr>
    <tr ng-show="tags" ng-repeat="tag in tags" class="bodyRow">
        <td class="details pointer" id="tagName{{ tag.name }}">
            <a ng-click="goToTag(tag)">{{ tag.name }}</a>
        </td>
        <td class="centered">
            <a id="editTagModalButton{{ tag.name }}" ng-click="openEditModal(tag)" class="btn" ng-class="{ disabled : tag.enterpriseTag }">Edit / Delete</a>
        </td>
        <td>
                    <span class="vuln-tree-checkbox">
                        <input id="checkbox{{ tag.name }}" type="checkbox" ng-model="tag.checked" ng-change="applyTagChecked(tag)"/>
                    </span>
        </td>
    </tr>
    <tr ng-show="tags">
        <td colspan="2"></td>
        <td>
            <a class="btn vuln-tree-checkbox" ng-show="initialized" id="batchTaggingButton" ng-click="goToBatchTagging()" class="btn">Batch Tagging</a>
        </td>
    </tr>
    </tbody>
</table>

<h3>Vulnerability Tags</h3>

<table ng-show="initialized" class="table table-striped">
    <thead>
    <tr>
        <th class="long first">Name</th>
        <th class="centered last">Edit / Delete</th>
    </tr>
    </thead>
    <tbody id="vulnTagTableBody">
    <tr ng-hide="vulnTags" class="bodyRow">
        <td colspan="2" style="text-align:center;">No Vulnerability Tags found.</td>
    </tr>
    <tr ng-show="vulnTags" ng-repeat="tag in vulnTags" class="bodyRow">
        <td class="details pointer" id="vulnTagName{{ tag.name }}">
            <a ng-click="goToTag(tag)">{{ tag.name }}</a>
        </td>
        <td class="centered">
            <a id="editVulnTagModalButton{{ tag.name }}" ng-click="openEditModal(tag)" class="btn" ng-class="{ disabled : tag.enterpriseTag }">Edit / Delete</a>
        </td>
    </tr>
    </tbody>
</table>

<h3>Vulnerability Comment Tags</h3>

<table ng-show="initialized" class="table table-striped">
    <thead>
    <tr>
        <th class="long first">Name</th>
        <th class="centered last">Edit / Delete</th>
    </tr>
    </thead>
    <tbody id="commentTagTableBody">
    <tr ng-hide="commentTags" class="bodyRow">
        <td colspan="2" style="text-align:center;">No Vulnerability Comment Tags found.</td>
    </tr>
    <tr ng-show="commentTags" ng-repeat="tag in commentTags" class="bodyRow">
        <td class="details pointer" id="commentTagName{{ tag.name }}">
            <a ng-click="goToTag(tag)">{{ tag.name }}</a>
        </td>
        <td class="centered">
            <a id="editCommentTagModalButton{{ tag.name }}" ng-click="openEditModal(tag)" class="btn" ng-class="{ disabled : tag.enterpriseTag }">Edit / Delete</a>
        </td>
    </tr>
    </tbody>
</table>
</body>
