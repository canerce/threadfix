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
package com.denimgroup.threadfix.selenium.tests;

import com.denimgroup.threadfix.CommunityTests;
import com.denimgroup.threadfix.selenium.pages.ApplicationDetailPage;
import com.denimgroup.threadfix.selenium.pages.TeamIndexPage;
import com.denimgroup.threadfix.selenium.pages.VulnerabilityDetailPage;
import com.denimgroup.threadfix.selenium.utils.DatabaseUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

@Category(CommunityTests.class)
public class ApplicationDetailVulnerabilitiesIT extends BaseDataTest{
    private ApplicationDetailPage applicationDetailPage;

    @Before
    public void initialize() {
        initializeTeamAndAppWithIbmScan();

        applicationDetailPage = loginPage.defaultLogin()
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName, teamName);
    }

    @Test
    public void testMarkSingleVulnerabilityClosed() {
        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilityByType("High790")
                .clickVulnerabilitiesActionButton()
                .clickCloseVulnerabilitiesButton()
                .sleepForResults();

        assertTrue("There should only be 9 High vulnerabilities shown.",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "9"));

        TeamIndexPage teamIndexPage = applicationDetailPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName);

        sleep(5000);

        assertTrue("The total number is not showing correctly", teamIndexPage.isTeamTotalNumberCorrect(teamName, "43"));
        assertTrue("The total number is not showing correctly",
                teamIndexPage.isApplicationTotalNumberCorrect(teamName,appName, "43"));
    }

    @Test
    public void testMarkMultipleVulnerabilitiesClosed() {
        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilitiesByCategory("High79")
                .clickVulnerabilitiesActionButton()
                .clickCloseVulnerabilitiesButton()
                .sleepForResults();

        assertTrue("There should only be 5 High vulnerabilities shown.",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "5"));

        TeamIndexPage teamIndexPage = applicationDetailPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName);

        sleep(5000);

        assertTrue("The total number is not showing correctly", teamIndexPage.isTeamTotalNumberCorrect(teamName, "39"));
        assertTrue("The total number is not showing correctly",
                teamIndexPage.isApplicationTotalNumberCorrect(teamName,appName, "39"));

    }

    @Test
    public void testReopenSingleVulnerability() {
        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilityByType("High790")
                .clickVulnerabilitiesActionButton()
                .clickCloseVulnerabilitiesButton()
                .sleepForResults();

        applicationDetailPage.expandFieldControls()
                .toggleStatusFilter("Open")
                .toggleStatusFilter("Closed");

        assertTrue("There should only be 1 critical vulnerability shown.",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "1"));

        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilityByType("High790")
                .clickVulnerabilitiesActionButton()
                .clickOpenVulnerabilitiesButton()
                .sleepForResults();

        assertTrue("There should be no closed vulnerabilities.",
                applicationDetailPage.areAllVulnerabilitiesHidden());

        applicationDetailPage.toggleStatusFilter("Closed")
                .toggleStatusFilter("Open");

        assertTrue("There should be 10 High vulnerabilities shown.",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "10"));

        TeamIndexPage teamIndexPage = applicationDetailPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName);

        assertTrue("The total number is not showing correctly", teamIndexPage.isTeamTotalNumberCorrect(teamName, "44"));
        assertTrue("The total number is not showing correctly",
                teamIndexPage.isApplicationTotalNumberCorrect(teamName,appName, "44"));
    }

    @Test
    public void testReopenMultipleVulnerabilities() {
        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilitiesByCategory("High79")
                .clickVulnerabilitiesActionButton()
                .clickCloseVulnerabilitiesButton()
                .sleepForResults();

        applicationDetailPage.expandFieldControls()
                .toggleStatusFilter("Open")
                .toggleStatusFilter("Closed");

        assertTrue("There should only be 5 High vulnerability shown.",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "5"));

        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilitiesByCategory("High79")
                .clickVulnerabilitiesActionButton()
                .clickOpenVulnerabilitiesButton()
                .sleepForResults();

        assertTrue("There should not be any closed vulnerabilities.",
                applicationDetailPage.areAllVulnerabilitiesHidden());

        applicationDetailPage.toggleSeverityFilter("Closed")
                .toggleStatusFilter("Open");

        assertTrue("There should be 10 High vulnerabilities shown.",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "10"));

        TeamIndexPage teamIndexPage = applicationDetailPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName);

        assertTrue("The total number is not showing correctly", teamIndexPage.isTeamTotalNumberCorrect(teamName, "44"));
        assertTrue("The total number is not showing correctly",
                teamIndexPage.isApplicationTotalNumberCorrect(teamName,appName, "44"));
    }

    @Test
    public void testMarkSingleVulnerabilityFalsePositive() {
        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilityByType("High790")
                .clickVulnerabilitiesActionButton()
                .clickMarkFalseVulnerability()
                .sleepForResults();

        assertTrue("There should only be 9 High vulnerabilities shown.",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "9"));

        TeamIndexPage teamIndexPage = applicationDetailPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName);

        sleep(5000);

        assertTrue("The total number is not showing correctly", teamIndexPage.isTeamTotalNumberCorrect(teamName, "43"));
        assertTrue("The total number is not showing correctly", teamIndexPage.isApplicationTotalNumberCorrect(teamName,appName, "43"));
    }

    @Test
    public void testUnmarkSingleVulnerabilityFalsePositive() {
        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilityByType("High790")
                .clickVulnerabilitiesActionButton()
                .clickMarkFalseVulnerability()
                .sleepForResults();

        applicationDetailPage.expandFieldControls()
                .toggleStatusFilter("Open")
                .toggleStatusFilter("FalsePositive");

        assertTrue("There should only be 1 High vulnerability shown.",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "1"));

        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilityByType("High790")
                .clickVulnerabilitiesActionButton()
                .clickUnMarkFalsePositive()
                .sleepForResults();

        assertTrue("There should be no vulnerabilities marked false positive.",
                applicationDetailPage.areAllVulnerabilitiesHidden());

        applicationDetailPage.toggleStatusFilter("FalsePositive")
                .toggleStatusFilter("Open");

        assertTrue("There should only be 10 vulnerabilities shown.",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "10"));

        TeamIndexPage teamIndexPage = applicationDetailPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName);

        assertTrue("The total number is not showing correctly", teamIndexPage.isTeamTotalNumberCorrect(teamName, "44"));
        assertTrue("The total number is not showing correctly",
                teamIndexPage.isApplicationTotalNumberCorrect(teamName,appName, "44"));
    }

    @Test
    public void testMarkMultipleVulnerabilitiesFalsePositive() {
        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilitiesByCategory("High79")
                .clickVulnerabilitiesActionButton()
                .clickMarkFalseVulnerability()
                .sleepForResults();

        assertTrue("There should only be 5 High vulnerabilities shown.",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "5"));

        TeamIndexPage teamIndexPage = applicationDetailPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName);

        sleep(5000);

        assertTrue("The total number is not showing correctly", teamIndexPage.isTeamTotalNumberCorrect(teamName, "39"));
        assertTrue("The total number is not showing correctly",
                teamIndexPage.isApplicationTotalNumberCorrect(teamName,appName, "39"));
    }

    @Test
    public void testUnmarkMultipleVulnerabilitiesFalsePositive() {
        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilitiesByCategory("High79")
                .clickVulnerabilitiesActionButton()
                .clickMarkFalseVulnerability()
                .sleepForResults();

        applicationDetailPage.expandFieldControls()
                .toggleStatusFilter("Open")
                .toggleStatusFilter("FalsePositive");

        assertTrue("There should only be 5 High vulnerability shown.",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "5"));

        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilitiesByCategory("High79")
                .clickVulnerabilitiesActionButton()
                .clickUnMarkFalsePositive()
                .sleepForResults();

        assertTrue("There should be no vulnerabilities marked false positive.",
                applicationDetailPage.areAllVulnerabilitiesHidden());

        applicationDetailPage.toggleStatusFilter("FalsePositive")
                .toggleStatusFilter("Open");

        assertTrue("There should only be 10 vulnerabilities shown.",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "10"));

        TeamIndexPage teamIndexPage = applicationDetailPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName);

        assertTrue("The total number is not showing correctly", teamIndexPage.isTeamTotalNumberCorrect(teamName, "44"));
        assertTrue("The total number is not showing correctly",
                teamIndexPage.isApplicationTotalNumberCorrect(teamName,appName, "44"));
    }

    @Test
    public void testChangeSingleVulnerabilitySeverity() {
        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilityByType("High790")
                .clickVulnerabilitiesActionButton()
                .setChangeSeverity("Critical");

        applicationDetailPage.waitForVulnerabilityCountUpdate("Critical", "1");

        assertTrue("Critical vulnerability count is not 1",
                applicationDetailPage.isVulnerabilityCountCorrect("Critical", "1"));
        assertTrue("High vulnerability count is not 9",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "9"));
    }

    @Test
    public void testChangeMultipleVulnerabilitiesSeverity() {
        applicationDetailPage.expandVulnerabilityByType("High79")
                .checkVulnerabilitiesByCategory("High79")
                .clickVulnerabilitiesActionButton()
                .setChangeSeverity("Critical");

        applicationDetailPage.waitForVulnerabilityCountUpdate("Critical", "5");

        assertTrue("Critical vulnerability count is not 5",
                applicationDetailPage.isVulnerabilityCountCorrect("Critical", "5"));
        assertTrue("High vulnerability count is not 5",
                applicationDetailPage.isVulnerabilityCountCorrect("High", "5"));
    }

    @Test
    public void testViewMoreLink() {
        applicationDetailPage.expandVulnerabilityByType("High79")
                .expandCommentSection("High790")
                .clickAddComment("High790")
                .setComment(getName())
                .clickModalSubmit();

        VulnerabilityDetailPage vulnerabilityDetailPage = applicationDetailPage.clickViewMoreVulnerabilityLink("High790");

        assertTrue("Vulnerability Detail Page navigation failed after click view more link of vulnerability.",
                vulnerabilityDetailPage.isUploadFileButtonPresent());
    }

    @Test
    public void testAddCommentToVulnerability() {
        String comment = getName();

        applicationDetailPage.expandVulnerabilityByType("High79")
                .expandCommentSection("High790")
                .clickAddComment("High790")
                .setComment(comment)
                .clickModalSubmit();

        assertTrue("There should be 1 comment associated with this vulnerability.",
                applicationDetailPage.isCommentCountCorrect("High790", "1"));

        assertTrue("The comment was not preserved correctly.",
                applicationDetailPage.isCommentCorrect("0", comment));
    }

    //TODO fix bad navigation
    @Test
    public void testVulnerabilityPaginationAvailable() {
        DatabaseUtils.uploadScan(teamName, appName, ScanContents.SCAN_FILE_MAP.get("AppScanEnterprise"));

        applicationDetailPage.refreshPage();

        applicationDetailPage.expandVulnerabilityByType("Low209");

        assertTrue("Pagination available", applicationDetailPage.isPaginationPresent("Low209"));
    }

    @Test
    public void testVulnerabilityPaginationUnavailable() {
        applicationDetailPage.expandVulnerabilityByType("High79");

        assertFalse("Pagination available", applicationDetailPage.isPaginationPresent("High79"));
    }

    @Test
    public void testBatchTaggingVulnerabilities() {
        String tagName = getName();
        DatabaseUtils.createTag(tagName, "Vulnerability");

        applicationDetailPage.refreshPage();

        applicationDetailPage.expandVulnerabilityByType("Medium74")
                .checkVulnerabilitiesByCategory("Medium74")
                .clickVulnerabilitiesActionButton()
                .clickBatchTagging()
                .attachTag(tagName)
                .clickModalSubmit();

        assertTrue("Tag was not added to first vulnerability.",
                applicationDetailPage.isVulnerabilityTagPresent("Medium", "74", "0", tagName));
        assertTrue("Tag was not added to second vulnerability.",
                applicationDetailPage.isVulnerabilityTagPresent("Medium", "74", "1", tagName));
        assertTrue("Tag was not added to third vulnerability.",
                applicationDetailPage.isVulnerabilityTagPresent("Medium", "74", "2", tagName));
    }
}
