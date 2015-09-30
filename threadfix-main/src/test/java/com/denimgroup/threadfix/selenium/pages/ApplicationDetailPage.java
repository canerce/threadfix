////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationDetailPage extends BasePage {

    public ApplicationDetailPage(WebDriver webdriver) {
        super(webdriver);
    }

    //===========================================================================================================
    // Procedure Methods
    //===========================================================================================================

    public ApplicationDetailPage addDefectTracker(String defectTracker, String username,
                                                  String password, String productName) {
        clickEditDeleteBtn()
                .clickAddDefectTrackerButton()
                .selectDefectTracker(defectTracker)
                .setUsername(username)
                .setPassword(password)
                .clickGetProductNames()
                .selectProduct(productName)
                .clickUpdateApplicationButton()
                .clickUpdateApplicationButton();

        sleep(2000);

        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage addSavedFilter(String newFilter) {
        driver.findElementById("filterNameInput").clear();
        driver.findElementById("filterNameInput").sendKeys(newFilter);
        driver.findElementById("saveFilterButton").click();
        waitForElement(By.id("saveFilterSuccessMessage"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage addSavedFilterInvalid(String newFilter) {
        driver.findElementById("filterNameInput").clear();
        driver.findElementById("filterNameInput").sendKeys(newFilter);
        driver.findElementById("saveFilterButton").click();
        waitForElement(By.id("saveFilterErrorMessage"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage addScannerFilter(String scanner) {
        driver.findElementById("showScannerInput").click();
        driver.findElementById("scannerTypeahead").sendKeys(scanner);
        driver.findElementById("scannerTypeahead").sendKeys(Keys.RETURN);
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage addVulnerabilityTypeFilter(String vulnerabilityType) {
        driver.findElementById("showTypeInput").click();
        driver.findElementById("vulnerabilityTypeTypeahead").sendKeys(vulnerabilityType);
        driver.findElementById("vulnerabilityTypeTypeahead").sendKeys(Keys.RETURN);
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage attachTag(String tagName) {
        driver.findElementById("tagsButton").click();
        driver.findElementByXPath("//*[@id=\'tagSelect\']//input[contains(@class,'inputFilter')]").sendKeys(tagName);
        driver.findElementById(tagName).click();
        driver.findElementById("tagsButton").click();
        return this;
    }

    public TagDetailPage clickTagHeader(String number) {
        driver.findElementById("appTag" + number).click();
        waitForElement(By.linkText("Back to Tags Page"));
        return new TagDetailPage(driver);
    }

    public ApplicationDetailPage clickFilesTab() {
        driver.findElement(By.xpath("//a[contains(text(), ' Files')]")).click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickAddFileButton() {
        driver.findElementByLinkText("Add File").click();
        return new ApplicationDetailPage(driver);
    }

    //===========================================================================================================
    // Action Methods
    //===========================================================================================================

    public ApplicationDetailPage clickAddDefectTrackerButton() {
        driver.findElementById("addDefectTrackerButton").click();
        sleep(1000);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage saveWafAdd() {
        driver.findElementById("submit").click();
        return new ApplicationDetailPage(driver);
    }

    public WafIndexPage clickWafNameLink() {
        driver.findElementById("wafNameText").click();
        return new WafIndexPage(driver);
    }

    public ApplicationDetailPage clickActionButton() {
        waitForElement(By.id("actionButton1"));
        sleep(3000);
        driver.findElementById("actionButton1").click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickViewPermUsers() {
        clickActionButton();
        driver.findElementById("userListModelButton").click();
        waitForElement(By.id("myModalLabel"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickEditDeleteBtn() {
        sleep(2000);
        clickActionButton();
        sleep(2000);
        waitForElement(By.id("editApplicationModalButton"));
        driver.findElementById("editApplicationModalButton").click();
        waitForElement(By.id("deleteLink"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage expandSourceCodeFields() {
        driver.findElementByLinkText("Source Code Information").click();
        waitForElement(By.id("repositoryUrl"));
        return new ApplicationDetailPage(driver);
    }

    public TeamDetailPage clickDeleteLink() {
        driver.findElementById("deleteLink").click();
        handleAlert();
        return new TeamDetailPage(driver);
    }

    public ApplicationDetailPage clickVulnerabilitiesTab(int numberOfVulnerabilities) {
        driver.findElementByLinkText(numberOfVulnerabilities + " Vulnerabilities").click();
        waitForElement(By.id("actionItems"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickScansTab() {
        sleep(1000);
        driver.findElementByCssSelector("li#scanTab a").click();
        waitForElement(By.linkText("Delete Scan"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickUnmappedFindings(String unmappedFindingNumber) {
        driver.findElementByLinkText(unmappedFindingNumber).click();
        waitForElement(By.id("unmappedVulnType"));
        return new ApplicationDetailPage(driver);
    }

    public FindingDetailPage clickUnmappedViewFinding() {
        driver.findElementById("unmappedVulnType").click();
        return new FindingDetailPage(driver);
    }

    public ApplicationDetailPage clickScheduleScanTab(int scheduledScans) {
        sleep(1000);
        if (scheduledScans == 1) {
            driver.findElementByLinkText(scheduledScans + " Scheduled Scan").click();
        } else {
            driver.findElementByLinkText(scheduledScans + " Scheduled Scans").click();
        }
        waitForElement(By.linkText("Schedule New Scan"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickScheduleNewScanButton() {
        driver.findElementByLinkText("Schedule New Scan").click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickAddWaf() {
        driver.findElementById("addWafButton").click();
        waitForElement(By.id("wafSelect"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickCreateNewWaf() {
        sleep(1000);
        driver.findElementById("addWafButtonInModal").click();
        return this;
    }

    public ApplicationDetailPage clickCreateWafButton() {
        sleep(1000);
        driver.findElementById("submit").click();
        // waitForElement(By.id("addWafButton"));
        sleep(2000);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickSetWaf() {
        driver.findElementById("addWafButton").click();
        sleep(1500);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickCloseModalButton() {
        driver.findElementById("closeModalButton").click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickUpdateApplicationButton() {
        sleep(5000);
        driver.findElementById("submit").click();
        waitForInvisibleElement("myModalLabel");
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickUpdateApplicationButtonInvalid() {
        sleep(1000);
        driver.findElementById("submit").click();
        waitForInvisibleElement("myModalLabel");
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickAttachWaf() {
        driver.findElementById("submit").click();
        waitForElement(By.id("addWafButton"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickDeleteScanButton() {
        if (!tryClick(By.id("deleteScan0"))) {
            throw new StaleElementReferenceException("Delete button could not be clicked.");
        }
        handleAlert();
        waitForInvisibleElement("scanDeleting0");
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage cancelDeleteScanButton() {
        driver.findElementByLinkText("Delete Scan").click();
        dismissAlert();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickDeleteScanTaskButton(String id) {
        driver.findElementById("deleteButton" + id).click();
        handleAlert();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickDynamicSubmit() {
        driver.findElementById("submit").click();
        sleep(1000);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickVulnerabilitiesActionButton() {
        driver.findElementById("actionItems").click();
        return  new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickCloseVulnerabilitiesButton() {
        driver.findElementById("closeVulnsButton").click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickOpenVulnerabilitiesButton() {
        driver.findElementById("openVulnsButton").click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickMarkFalseVulnerability() {
        driver.findElementById("markFalsePositivesButton").click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickUnMarkFalsePositive() {
        driver.findElementById("unmarkFalsePositivesButton").click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickScanAgentTasksTab(int count) {
        if (count == 1) {
            driver.findElementByLinkText(count + " Scan Agent Task").click();
        } else {
            driver.findElementByLinkText(count + " Scan Agent Tasks").click();
        }
        sleep(1000);
        waitForElement(By.id("scanQueueTable"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickAddNewScanTask() {
        driver.findElementById("addScanQueueLink").click();
        waitForElement(By.id("submit"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage submitScanQueue() {
        driver.findElementById("submit").click();
        sleep(1000);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickDocumentTab(int count) {
        driver.findElementByLinkText(count + " Files").click();
        waitForElement(By.linkText("Add File"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickUploadDocLink() {
        driver.findElementByLinkText("Add File").click();
        waitForElement(By.id("docFileInput"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickTeamSelector() {
        driver.findElementById("organizationId").click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage expandVulnerabilityByType(String type) {
        waitForElement(By.id("expandVuln" + type));
        driver.findElementById("expandVuln" + type).click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage collapseVulnerabilityByType(String type) {
        driver.findElementById("collapseVuln" + type).click();
        return new ApplicationDetailPage(driver);
    }

    public VulnerabilityDetailPage clickViewMoreVulnerabilityLink(String vulnerability) {
        driver.findElementById("viewMoreLink" + vulnerability).click();
        return new VulnerabilityDetailPage(driver);
    }

    public AnalyticsPage clickViewMoreVulnerabilityTrending(){
        driver.findElementById("leftViewMore").click();
        sleep(2500);
        return new AnalyticsPage(driver);
    }

    public AnalyticsPage clickViewMoreTopVulnerabilities(){
        driver.findElementById("rightViewMore").click();
        sleep(2500);
        waitForElement(By.id("snapshotFilterDiv"));
        return new AnalyticsPage(driver);
    }

    public ApplicationDetailPage checkVulnerabilitiesByCategory(String category) {
        sleep(5000);
        driver.findElementById("checkCategory" + category).click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage checkVulnerabilityByType(String type) {
        sleep(5000);
        driver.findElementById("checkbox" + type).click();
        return this;
    }

    public ApplicationDetailPage expandCommentSection(String level) {
        sleep(5000);
        driver.findElementById("commentsButton" + level).click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickAddComment(String level) {
        driver.findElementById("addCommentButton" + level).click();
        return new ApplicationDetailPage(driver);
    }

    public TagDetailPage clickTagName(String tagName) {
        driver.findElementByLinkText(tagName).click();
        waitForElement(By.linkText("Back to Tags Page"));
        return new TagDetailPage(driver);
    }

    public TeamAppCustomizeVulnerabilityTypesPage clickEditVulnerabilityFilters() {
        waitForElement(By.id("editVulnerabilityFiltersButton"));
        driver.findElementById("editVulnerabilityFiltersButton").click();
        return new TeamAppCustomizeVulnerabilityTypesPage(driver);
    }

    public ApplicationDetailPage clickManualFindingButton() {
        driver.findElementById("addManualFindingModalLink").click();
        waitForElement(By.id("txtSearch"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickStaticRadioButton() {
        driver.findElementById("staticRadioButton").click();
        return new ApplicationDetailPage(driver);
    }

    public ScanDetailPage clickViewScan() {
        if (!tryClick(By.linkText("View Scan"))) {
            throw new StaleElementReferenceException("View Scan button could not be clicked.");
        }
        waitForElement(By.id("statisticButton"));
        return new ScanDetailPage(driver);
    }

    public ApplicationDetailPage clickSourceInfo(){
        waitForElement(By.linkText("Source Code Information"));
        driver.findElementByLinkText("Source Code Information").click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggleAllFilter() {
        driver.findElementById("toggleAllButton").click();
        sleep(2000);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggleClear() {
        driver.findElementById("clearFiltersButton").click();
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clearSavedFilter() {
        driver.findElementById("clearFiltersButtonSavedTab").click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickLoadFilters() {
        driver.findElementByLinkText("Load Filters").click();
        waitForElement(By.id("filterSelect"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickFiltersTab() {
        driver.findElementByLinkText("Filters").click();
        waitForElement(By.id("clearFiltersButton"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage expandSavedFilters() {
        waitForResultsToLoad();
        driver.findElementById("showSaveFilter").click();
        waitForElement(By.id("filterNameInput"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage loadSavedFilter(String savedFilter) {
        new Select(driver.findElementById("filterSelect")).selectByVisibleText(savedFilter);
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage expandScannerAndMerged() {
        waitForResultsToLoad();
        driver.findElementById("expandScannerFilters").click();
        waitForElement(By.id("set2MergedFindings"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggleTwoPlus() {
        driver.findElementByLinkText("2+").click();
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggleThreePlus() {
        driver.findElementByLinkText("3+").click();
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggleFourPlus() {
        driver.findElementByLinkText("4+").click();
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggleFivePlus() {
        driver.findElementByLinkText("5+").click();
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage expandFieldControls() {
        waitForResultsToLoad();
        driver.findElementById("showFieldControls").click();
        waitForElement(By.id("pathInput"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage addPathFilter(String path) {
        driver.findElementById("pathInput").sendKeys(path);
        driver.findElementById("pathInput").sendKeys(Keys.RETURN);
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggleSeverityFilter(String level) {
        driver.findElementById("show" + level).click();
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage addParameterFilter(String parameter) {
        driver.findElementById("parameterFilterInput").sendKeys(parameter);
        driver.findElementById("parameterFilterInput").sendKeys(Keys.RETURN);
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggleStatusFilter(String status) {
        driver.findElementById("show" + status).click();
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage expandAging() {
        waitForResultsToLoad();
        driver.findElementById("showDateControls").click();
        waitForElement(By.id("lessThan"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggleLessThan() {
        driver.findElementByLinkText("Less Than").click();
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggleMoreThan() {
        driver.findElementByLinkText("More Than").click();
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggleOneWeek() {
        driver.findElementByLinkText("1 Week").click();
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggle30Days() {
        driver.findElementByLinkText("30 days").click();
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggle60Days() {
        driver.findElementByLinkText("60 days").click();
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage toggle90Days() {
        driver.findElementByLinkText("90 days").click();
        waitForResultsToLoad();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage expandDateRange() {
        waitForResultsToLoad();
        driver.findElementById("showDateRange").click();
        waitForElement(By.id("startDateInput"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickUploadScan() {
        driver.findElementById("uploadScanModalLink").click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickCreateNewDefectTracker() {
        driver.findElementById("createDefectTrackerButton").click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickCreateDefectTracker() {
        driver.findElementById("submit").click();
        waitForElement(By.id("username"), 20);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickDeleteScheduledScan() {
        driver.findElementById("scheduledScanDeleteButton0").click();
        driver.switchTo().alert().accept();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickGetProductNames() {
        driver.findElementById("getProductNames").click();
        waitForElement(By.cssSelector("#productNamesSection:not(.ng-hide)"));
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage uploadScan(String file) {
        driver.findElementById("scanFileInput").sendKeys(file);
        waitForElement(By.id("toggleVulnTree"));
        sleep(1000);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage uploadEmptyScan(String file) {
        driver.findElementById("scanFileInput").sendKeys(file);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage uploadFile(String file) {
        sleep(2000);
        driver.findElementById("docFileInput").sendKeys(file);
        waitForElement(By.xpath("//a[text()='Download']"));
        sleep(1000);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage clickManagePolicy() {
        waitForElement(By.id("managePolicyButton"));
        driver.findElementById("managePolicyButton").click();
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage removePolicy(String policyName) {
        driver.findElementByCssSelector("#policy" + policyName + ">span").click();
        handleAlert();
        return this;
    }

    public void clickGitRepositoryType() {
        driver.findElementById("gitRadioButton").click();
    }

    //===========================================================================================================
    // Set Methods
    //===========================================================================================================

    public ApplicationDetailPage setUsername(String dtName) {
        driver.findElementById("username").clear();
        driver.findElementById("username").sendKeys(dtName);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setPassword(String dtPass) {
        driver.findElementById("password").clear();
        driver.findElementById("password").sendKeys(dtPass);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage selectProduct(String product) {
        new Select(driver.findElementById("productNameSelect")).selectByVisibleText(product);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage selectDefectTracker(String defectTracker) {
        waitForElement(By.id("defectTrackerId"));
        new Select(driver.findElementById("defectTrackerId"))
                .selectByVisibleText(defectTracker);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage selectWaf(String wafName) {
        new Select(driver.findElementById("wafSelect")).selectByVisibleText(wafName);
        sleep(4000);
        return this;
    }

    public ApplicationDetailPage setRemoteSourceCodeInformation(String url, String revision, String userName, String password) {
        clickGitRepositoryType();
        setRepositoryURLEdited(url);
        setRepositoryRevisionEdited(revision);
        setRepositoryUserNameEdited(userName);
        setRepositoryPasswordEdited(password);
        return this;
    }

    public ApplicationDetailPage setRepositoryURLEdited(String url) {
        clickGitRepositoryType();
        driver.findElementById("repositoryUrl").clear();
        driver.findElementById("repositoryUrl").sendKeys(url);
        return this;
    }

    public ApplicationDetailPage setRepositoryRevisionEdited(String revision) {
        sleep(1000);
        driver.findElementById("repositoryBranch").clear();
        driver.findElementById("repositoryBranch").sendKeys(revision);
        return this;
    }

    public ApplicationDetailPage setRepositoryUserNameEdited(String userName) {
        sleep(1000);
        driver.findElementById("repositoryUsername").clear();
        driver.findElementById("repositoryUsername").sendKeys(userName);
        return this;
    }

    public ApplicationDetailPage setRepositoryPasswordEdited(String password){
        sleep(1000);
        driver.findElementById("repositoryPassword").clear();
        driver.findElementById("repositoryPassword").sendKeys(password);
        return this;
    }

    public ApplicationDetailPage setScheduledScanFrequency(String frequency) {
        new Select(driver.findElementById("frequency")).selectByVisibleText(frequency);
        return this;
    }

    public ApplicationDetailPage setScheduledScanTime(String hour, String minute, String period){
        new Select(driver.findElementById("hour")).selectByVisibleText(hour);
        new Select(driver.findElementById("minute")).selectByVisibleText(minute);
        new Select(driver.findElementById("selectedPeriod")).selectByVisibleText(period);
        return this;
    }

    public ApplicationDetailPage setScheduledScanDay(String day) {
        new Select(driver.findElementById("selectedDay")).selectByVisibleText(day);
        return this;
    }

    public ApplicationDetailPage setScheduledScanScanner(String scanner, String appId) {
        new Select(driver.findElementById("scanner" + appId)).selectByVisibleText(scanner);
        return this;
    }

    public ApplicationDetailPage setWafName(String name) {
        sleep(1000);
        driver.findElementById("wafCreateNameInput").sendKeys(name);
        return this;
    }

    public ApplicationDetailPage setWafType(String type){
        new Select(driver.findElementById("typeSelect")).selectByVisibleText(type);
        return this;
    }

    public ApplicationDetailPage setNameInput(String appName2) {
        driver.findElementById("nameInput").clear();
        driver.findElementById("nameInput").sendKeys(appName2);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setUrlInput(String url) {
        driver.findElementById("urlInput").clear();
        driver.findElementById("urlInput").sendKeys(url);
        return this;
    }

    public ApplicationDetailPage setCWE(String Status) {
        driver.findElementById("txtSearch").clear();
        driver.findElementById("txtSearch").sendKeys(Status);
        sleep(500);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setSourceFile(String Status) {
        driver.findElementById("urlStaticSearch").clear();
        driver.findElementById("urlStaticSearch").sendKeys(Status);
        sleep(500);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setLineNumber(String Status) {
        driver.findElementById("lineNumberInput").clear();
        driver.findElementById("lineNumberInput").sendKeys(Status);
        sleep(500);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setSeverity(String status) {
        driver.findElementById("severityInput").sendKeys(status);
        new Select(driver.findElementById("severityInput")).selectByVisibleText(status);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setURL(String Status) {
        driver.findElementById("urlDynamicSearch").clear();
        driver.findElementById("urlDynamicSearch").sendKeys(Status);
        sleep(500);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setParameter(String Status) {
        driver.findElementById("parameterInput").clear();
        driver.findElementById("parameterInput").sendKeys(Status);
        sleep(500);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setDescription(String Status) {
        driver.findElementById("descriptionInput").clear();
        driver.findElementById("descriptionInput").sendKeys(Status);
        sleep(500);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setDocFileInput(String file) {
        driver.findElementById("docFileInput").sendKeys(file);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setScanQueueType(String scanQueueType) {
        new Select(driver.findElementById("scan")).selectByVisibleText(scanQueueType);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setTeam(String team) {
        new Select(driver.findElementById("organizationId")).selectByVisibleText(team);
        sleep(2000);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setComment(String comment) {
        driver.findElementById("commentInputBox").clear();
        driver.findElementById("commentInputBox").sendKeys(comment);
        return this;
    }

    public ApplicationDetailPage setInvalidNameSavedFilter(String newFilter) {
        driver.findElementById("filterNameInput").clear();
        driver.findElementById("filterNameInput").sendKeys(newFilter);
        return this;
    }

    public ApplicationDetailPage setStartDate(String date) {
        driver.findElementById("startDateInput").sendKeys(date);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setEndDate(String date) {
        driver.findElementById("endDateInput").sendKeys(date);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setDefectTrackerName(String name) {
        driver.findElementById("nameInput").clear();
        driver.findElementById("nameInput").sendKeys(name);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage setDefectTrackerType(String type) {
        new Select(driver.findElementById("defectTrackerTypeSelect")).selectByVisibleText(type);
        return this;
    }

    public ApplicationDetailPage setUniqueId(String uniqueId) {
        driver.findElementById("uniqueIdInput").sendKeys(uniqueId);
        return this;
    }

    public ApplicationDetailPage selectPolicy(String policyName) {
        new Select(driver.findElementById("policySelect")).selectByVisibleText(policyName);
        driver.findElementById("addButton").click();
        return this;
    }

    public ApplicationDetailPage setDefectTrackerDefaultUsername(String username) {
        driver.findElementById("defaultUsername").sendKeys(username);
        return this;
    }

    public ApplicationDetailPage setDefectTrackerDefaultPassword(String password) {
        driver.findElementById("defaultPassword").sendKeys(password);
        return this;
    }

    //===========================================================================================================
    // Get Methods
    //===========================================================================================================

    public int getNumPermUsers() {
        return driver.findElementById("userTableBody").findElements(By.className("bodyRow")).size();
    }

    public String getWafName() {
        waitForElement(By.id("wafNameText"));
        return driver.findElementById("wafNameText").getText();
    }

    public String getNameText() {
        return tryGetText(By.id("nameText"));
    }

    public String getUrlText() {
        return driver.findElementById("urlInput").getAttribute("value");
    }

    public String getApplicationId() {
        String hrefText = driver.getCurrentUrl();
        return hrefText.substring(hrefText.indexOf("applications/") + 13, hrefText.indexOf("?"));
    }

    public String getScheduledScanScanner() {
        return driver.findElementById("scheduledScanScanner0").getText();
    }

    public String getScheduledScanDay() {
        return driver.findElementById("scheduledScanDay0").getText();
    }

    public String getScheduledScanFrequency() {
        return driver.findElementById("scheduledScanFrequency0").getText();
    }

    public String getNameError() {
        return driver.findElementById("applicationNameInputNameError").getText();
    }

    public String getNameRequiredError() {
        return driver.findElementById("applicationNameInputRequiredError").getText();
    }

    public String getUrlError() {
        return driver.findElementById("applicationUrlInputInvalidUrlError").getText().trim();
    }

    public String getUrlRepositoryError() {
        return driver.findElementById("sourceUrlValidError").getText().trim();
    }

    public String getAlert() {
        return driver.findElementByClassName("alert-success").getText();
    }

    public int getScanQueueCount() {
        WebElement scanQueueTab;
        try {
            scanQueueTab = driver.findElementById("scanAgentTasksTab");
        } catch (NoSuchElementException e) {
            return 0;
        }

        String scanText = scanQueueTab.getText().trim();
        Pattern pattern = Pattern.compile("^\\s*(\\d+)");
        Matcher matcher = pattern.matcher(scanText);
        if (matcher.find()) {
            int temp = Integer.parseInt(matcher.group(1));
            return temp;
        }
        return -1;
    }

    public int getDocsCount() {
        WebElement scanQueueTab;
        try {
            scanQueueTab = driver.findElementById("documentsTab");
        } catch (NoSuchElementException e) {
            return 0;
        }

        String scanText = scanQueueTab.getText().trim();
        Pattern pattern = Pattern.compile("^\\s*(\\d+)");
        Matcher matcher = pattern.matcher(scanText);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    public String getSpecificVulnerabilityCount(String level) {
        List<WebElement> headers = driver.findElementsByClassName("vulnSectionHeader");

        for (WebElement header : headers) {
            if (header.getText().contains(level)) {
                String count = header.getText();
                count = count.substring(count.length() - 2, count.length() - 1);
                return count;
            }
        }
        return "0";
    }

    public int getFilterDivHeight() {
        return driver.findElement(By.className("filter-controls")).getSize().getHeight();
    }

    public String getScanTaskId(int row) {
        return driver.findElementById("scanAgentTask"+row).getText().trim();
    }

    public String[] getFirstScanInfo() {
        String scannerType = driver.findElementById("channelType0").getText().trim();
        String numVulns = driver.findElementById("numTotalVulnerabilities0").getText().trim();
        String[] scanValues = {scannerType,numVulns};
        return scanValues;
    }

    public String getModalTitle() {
        return driver.findElementById("myModalLabel").getText();
    }

    public String getFirstScanChannelType() {
        return driver.findElementById("channelType0").getText();
    }

    public String getScanAgentTaskScannerType(int position) {
        return driver.findElementById("scannerType" + position).getText();
    }

    public String getApplicationType() {
        return driver.findElementById("frameworkType").getAttribute("value");
    }

    public String getRepositoryUrl() {
        return driver.findElementById("repositoryUrl").getAttribute("value");
    }

    //===========================================================================================================
    // Boolean Methods
    //===========================================================================================================

    public boolean isVulnsFilteredOpen(int count) {
        return driver.findElementByLinkText(count + " Vulnerabilities").isDisplayed();
    }

    public boolean isCweErrorPresent() {
        return !driver.findElementByClassName("errors ng-binding").getAttribute("innerHTML").equals("");
    }

    public boolean isApplicationNamePresent() {
        return driver.findElementById("nameText").isDisplayed();
    }

    public boolean isApplicationNameCorrect(String appName) {
        return driver.findElementById("nameText").getText().equals(appName);
    }

    public boolean isBreadcrumbPresent() {
        return driver.findElementById("applicationsIndexLink").isDisplayed();
    }

    public boolean isApplicationBreadcrumbPresent() {
        return driver.findElementById("teamLink").isDisplayed();
    }

    public boolean isScanAgentTaskPresent(String taskId) {
        int rowCnt = driver.findElementsByCssSelector("tr[ng-repeat=\'task in scanAgentTasks\']").size();
        for (int i = 0; i < rowCnt; i++) {
            try {
                if (driver.findElementById("scanAgentTask" + i).getText().trim().equals(taskId)) {
                    return true;
                }
            } catch (NoSuchElementException e) {
                System.err.println("Scan Agent Task with ID of: " + taskId + " could not be found. " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean isTeamDisplayedinEditDeleteDropdown(String teamName) {
        return driver.findElementById("organizationId").getText().contains(teamName);
    }

    public boolean isUserPresentPerm(String user) {
        for (int i = 1; i <= getNumPermUsers(); i++) {
            if (driver.findElementById("name" + i).getText().contains(user)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAppTypeDetect() {
        return driver.findElementById("frameworkType").getText().contains("DETECT");
    }

    public boolean isDefectTrackerAttached() {
        return driver.findElementById("defectTrackerName").isEnabled();
    }

    public boolean isActionButtonPresent() {
        return driver.findElementById("actionButton1").isDisplayed();
    }

    public boolean isActionButtonClickable() {
        return isClickable("actionButton1");
    }

    public boolean isEditDeletePresent() {
        try {
            return driver.findElementById("editApplicationModalButton").isDisplayed();
        } catch(NoSuchElementException e) {
            return false;
        }
    }

    public boolean isEditDeleteClickable() {
        return isClickable("editApplicationModalButton");
    }

    public boolean isEditVulnFiltersPresent() {
        return driver.findElementById("editVulnerabilityFiltersButton").isDisplayed();
    }

    public boolean isEditVulnFiltersClickable() {
        return isClickable("editVulnerabilityFiltersButton");
    }

    public boolean isUploadScanPresent() {
        return driver.findElementById("uploadScanModalLink").isDisplayed();
    }

    public boolean isUploadScanClickable() {
        return isClickable("uploadScanModalLink");
    }

    public boolean isAddManualFindingsPresent() {
        return driver.findElementById("addManualFindingModalLink").isDisplayed();
    }

    public boolean isAddManualFindingsClickable() {
        return isClickable("addManualFindingModalLink");
    }

    public boolean isDeleteButtonPresent() {
        return driver.findElementById("deleteLink").isDisplayed();
    }

    public boolean isDeletebuttonClickable() {
        return isClickable("deleteLink");
    }

    public boolean isNameInputPresent() {
        return driver.findElementById("nameInput").isDisplayed();
    }

    public boolean isURLInputPresent() {
        return driver.findElementById("urlInput").isDisplayed();
    }

    public boolean isUniqueIDPresent() {
        return driver.findElementById("uniqueIdInput").isDisplayed();
    }

    public boolean isTeamSelectionPresent() {
        return driver.findElementById("organizationId").isDisplayed();
    }

    public boolean isCritcalityPresent() {
        return driver.findElementById("criticalityId").isDisplayed();
    }

    public boolean isAppTypePresent() {
        return driver.findElementById("frameworkType").isDisplayed();
    }

    public boolean isSourceURLPresent() {
        return driver.findElementById("repositoryUrl").isDisplayed();
    }

    public boolean isSourceFolderPresent() {
        return driver.findElementById("repositoryFolderInput").isDisplayed();
    }

    public boolean isDefectTrackerAddPresent() {
        return driver.findElementById("addDefectTrackerButton").isDisplayed();
    }

    public boolean isDefectTrackerAddClickable() {
        return isClickable("addDefectTrackerButton");
    }

    public boolean isWAFAddButtonPresent() {
        return driver.findElementById("addWafButton").isDisplayed();
    }

    public boolean isWAFAddButtonClickable() {
        return isClickable("addWafButton");
    }

    public boolean isDynamicRadioPresent() {
        return driver.findElementById("dynamicRadioButton").isDisplayed();
    }

    public boolean isStaticRadioPresent() {
        return driver.findElementById("staticRadioButton").isDisplayed();
    }

    public boolean isCWEInputPresent() {
        return driver.findElementById("txtSearch").isDisplayed();
    }

    public boolean isURLDynamicSearchPresent() {
        return driver.findElementById("urlDynamicSearch").isDisplayed();
    }

    public boolean isURLStaticSearchPresent() {
        return driver.findElementById("urlStaticSearch").isDisplayed();
    }

    public boolean isLineNumberInputPresent() {
        return driver.findElementById("lineNumberInput").isDisplayed();
    }

    public boolean isParameterPresent() {
        return driver.findElementById("parameterInput").isDisplayed();
    }

    public boolean isSeverityPresent() {
        return driver.findElementById("severityInput").isDisplayed();
    }

    public boolean isCveDescriptionInputPresent() {
        return driver.findElementById("descriptionInput").isDisplayed();
    }

    public boolean isSubmitManualFindingPresent() {
        return driver.findElementById("submit").isDisplayed();
    }

    public boolean isSubmitManualFindingClickable() {
        return isClickable("submit");
    }

    public boolean isManualFindingCloseButtonPresent() {
        return driver.findElementById("closeModalButton").isDisplayed();
    }

    public boolean isManualFindingCloseButtonClickable() {
        return isClickable("closeModalButton");
    }

    public boolean isScanDeleted() {
        return driver.findElementByLinkText("0 Scans").isDisplayed();
    }

    public boolean isCommentCountCorrect(String level, String expected) {
        return expected.equals(driver.findElementById("commentsButton" + level).getText().trim());
    }

    public boolean isCommentCorrect(String commentNumber, String comment) {
        return driver.findElementById("commentText" + commentNumber).getText().trim().contains(comment);
    }

    public boolean isWafCreationDenied() {
        sleep(2000);
        return driver.findElementById("myModalLabel").getText().equals("Add WAF");
    }


    public boolean isSeverityLevelShown(String level) {
        return driver.findElementsById("expand" + level).size() != 0;
    }

    public boolean isVulnerabilityCountCorrect(String level, String expected) {
        waitForElement(By.id("totalBadge" + level));
        return expected.equals(driver.findElementById("totalBadge" + level).getText().trim());
    }

    public boolean isVulnerabilityCountNonZero(String level) {
        return "0".equals(driver.findElementById("totalBadge" + level).getText().trim());
    }

    public boolean areAllVulnerabilitiesHidden() {
        try {
            return driver.findElementById("noResultsFound").getText().trim().equals("No results found.");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isSavedFilterSuccessMessageDisplayed() {
        return driver.findElementById("saveFilterSuccessMessage").isDisplayed();
    }

    public boolean isDuplicateNameErrorMessageDisplayed() {
        return driver.findElementById("saveFilterErrorMessage").isDisplayed();
    }

    public boolean isSaveFilterDisabled() {
        String attributeValue = driver.findElementById("saveFilterButton").getAttribute("disabled");
        if (attributeValue != null) {
            return attributeValue.contains("true");
        }
        return false;
    }

    public boolean isSavedFilterPresent(String savedFilter) {
        try {
            new Select(driver.findElementById("filterSelect")).selectByVisibleText(savedFilter);
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean isScheduledScanCountCorrect(String expected) {
        String actual = driver.findElementById("scheduledScanTab").getText().trim();
        if (expected.equals("1")) {
            actual = actual.replace("Scheduled Scan", "");
        } else {
            actual = actual.replace("Scheduled Scans", "");
        }
        return actual.trim().equals(expected);
    }

    public boolean isRepositoryURLCorrect(String repositoryURL) {
        return driver.findElementById("repositoryUrl").getAttribute("value").trim().equals(repositoryURL);
    }

    public boolean isRepositoryRevisionCorrect(String repositoryRevision) {
        return driver.findElementById("repositoryBranch").getAttribute("value").trim().equals(repositoryRevision);
    }

    public boolean isRepositoryUserNameCorrect(String repositoryUserName) {
        return driver.findElementById("repositoryUsername").getAttribute("value").trim().equals(repositoryUserName);
    }

    public boolean isRepositoryPasswordEmpty() {
        return driver.findElementById("repositoryPassword").getAttribute("value").isEmpty();
    }

    public boolean isRepositoryPathEmpty() {
        return driver.findElementById("repositoryFolderInput").getAttribute("value").isEmpty();
    }

    public boolean isWafPresent() {
        String temp = driver.findElementById("myModalLabel").getText().trim();
        return temp.equals("Add WAF");
    }

    public boolean isPaginationPresent(String name) {
        return driver.findElementById("pagination" + name).isDisplayed();
    }

    public boolean isScanUploadedAlready(String teamName, String appName) {
        return driver.findElementById("alert").getText().contains("Scan file has already been uploaded.");
    }

    public boolean isUniqueIdAvailabe(String uniqueName) {
        return driver.findElementById("uniqueIdInput").getAttribute("value").equals(uniqueName);
    }

    public boolean isNumberOfUnmappedCorrect(int expectedNumberOfFindings) {
        List<WebElement> bodyRows = driver.findElementById("1").findElements(By.className("bodyRow"));
        return bodyRows.size() == expectedNumberOfFindings;
    }

    public boolean isMessagePresent() {
        return !driver.findElementById("errorSpan").getText().equals("");
    }

    public boolean isLeftReportLinkPresent() {
        return driver.findElementsById("leftTileReport").size() != 0;
    }

    public boolean isRightReportLinkPresent() {
        return driver.findElementsById("rightTileReport").size() != 0;
    }

    public boolean isCreateDefectTrackerPresent() {
        try {
            return driver.findElementById("createDefectTrackerButton").isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isDefectTrackerNameLinkDisplay() {
        return driver.findElementById("linkDT").isDisplayed();
    }

    public boolean isDefectTrackerNameCorrect(String defectTrackerName) {
        return driver.findElementById("linkDT").getText().trim().equals(defectTrackerName);
    }

    public boolean isDetailLinkDisply() { return driver.findElementById("viewApplicationModalButton").isDisplayed();}

    public boolean isOrderOfSelectorCorrect(String firstTeam, String secondTeam) {
        int firstTeamValue;
        int secondTeamValue;

        this.setTeam(firstTeam);
        firstTeamValue = Integer.parseInt(new Select(driver.findElementById("organizationId")).getFirstSelectedOption().getAttribute("value"));

        this.setTeam(secondTeam);
        secondTeamValue = Integer.parseInt(new Select(driver.findElementById("organizationId")).getFirstSelectedOption().getAttribute("value"));

        return secondTeamValue > firstTeamValue;
    }

    public boolean isCveLinkDisplay(String expectedNumber) {
        return driver.findElementById("linkCve" + expectedNumber).isDisplayed();
    }

    public boolean isCveComponentDisplay(String expectedNumber) {
        return driver.findElementById("cveComponent" + expectedNumber).isDisplayed();
    }

    public boolean isCveDescriptionInputPresent(String expectedNumber) {
        return driver.findElementById("cveDescription" + expectedNumber).isDisplayed();
    }

    public boolean isCWEBarPresent(String teamName, String appName, String vulnerability) {
        return driver.findElementById(teamName + appName + vulnerability + "Bar").isDisplayed();
    }

    public boolean isTop10TipCorrect(String tipText) {
        return driver.findElementById("horizontalBarTip").getText().trim().contains(tipText);
    }

    public boolean isTop10BarCountCorrect(int expected) {
        return driver.findElementsByClassName("g").size() == expected;
    }

    public boolean isVulnerabilitySummaryElementCorrect(String element, String expected) {
        return driver.findElementById(element).getText().contains(expected);
    }

    public boolean isUploadedFilePresent(String fileName) {
        return driver.findElementByCssSelector("td#docName0").getText().contains(fileName);
    }

    public boolean isTagLinkPresent() {
        try {
            driver.findElementById("appTag0");
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    public boolean isPolicyInModal(String appName, String policyName) {
        return isElementPresent("policy" + policyName);
    }

    //===========================================================================================================
    // Helper Methods
    //===========================================================================================================

    public ApplicationDetailPage sleepForResults() {
        sleep(1500);
        return new ApplicationDetailPage(driver);
    }

    public ApplicationDetailPage waitForModalToClose() {
        waitForInvisibleElement("myModalLabel");
        return new ApplicationDetailPage(driver);
    }

    public void waitForResultsToLoad() {
        while (driver.findElementById("vulnTreeLoadingSpinner").isDisplayed()) {
            sleep(1000);
        }
    }

    public void waitForCWEBar(String teamName, String appName, String vulnerability) {
        waitForElement(By.id(teamName + appName + vulnerability + "Bar"));
    }
}
