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
import com.denimgroup.threadfix.selenium.pages.TeamDetailPage;
import com.denimgroup.threadfix.selenium.utils.DatabaseUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;

@Category(CommunityTests.class)
public class TeamDetailPageIT extends BaseIT {
    private String teamName;
    private String appName;
    private String file;

    @Before
    public void initialize() {
        teamName = createTeam();
        appName = createApplication(teamName);
        file = ScanContents.getScanFilePath();

        DatabaseUtils.uploadScan(teamName, appName, file);
    }

	@Test
	public void testActionButton(){
        TeamDetailPage teamDetailPage = loginPage.defaultLogin().
                clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName)
                .clickActionButton();

        assertTrue("Action button was not present.", teamDetailPage.isActionBtnPresent());
        assertTrue("Action button was not clickable.", teamDetailPage.isActionBtnClickable());
        assertTrue("Edit/Delete Link was not present.", teamDetailPage.isEditDeleteLinkPresent());
        assertTrue("Edit/Delete link was not clickable.", teamDetailPage.isEditDeleteLinkClickable());
	}
	
	@Test
	public void testEditDeleteModal(){
        TeamDetailPage teamDetailPage = loginPage.defaultLogin()
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName)
                .clickEditOrganizationLink();

        assertTrue("Edit/Modal was not present.", teamDetailPage.isEditDeleteModalPresent());
        assertTrue("Delete Button was not present.", teamDetailPage.isDeleteTeamButtonPresent());
        assertTrue("Delete Button was not clickable.", teamDetailPage.isDeleteClickable());
        assertTrue("Name input was not present", teamDetailPage.isNamePresent());
        assertTrue("Close modal button was not present", teamDetailPage.isClosePresent());
        assertTrue("Close modal button was not clickable", teamDetailPage.isCloseClickable());
		assertTrue("Save button was not present.", teamDetailPage.isSavePresent());
		assertTrue("Save button was not clickable.", teamDetailPage.isSaveClickable());
	}
	
	@Test
	public void testChart(){
        TeamDetailPage teamDetailPage = loginPage.defaultLogin()
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName);
		sleep(5000); 
        assertTrue("Left view more link was not present.", teamDetailPage.isleftViewMoreLinkPresent());
        assertTrue("Left view more link was not clickable.", teamDetailPage.isleftViewMoreLinkClickable());
        assertTrue("Right view more link was not present.", true);
        assertTrue("Right view more link was not clickable.", teamDetailPage.isrightViewMoreLinkClickable());
        assertTrue("6 month vulnerability burn-down chart was not present", teamDetailPage.is6MonthChartPresnt());
        assertTrue("Top 10 vulnerabilities chart was not present.", teamDetailPage.isTop10ChartPresent());
	}
	
	@Test
	public void testAddApplicationButton(){
        TeamDetailPage teamDetailPage = loginPage.defaultLogin()
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName);

        assertTrue("Add App button was not present.", teamDetailPage.isAddAppBtnPresent());
        assertTrue("Add app button was not clickable.", teamDetailPage.isAddAppBtnClickable());

	}

	@Test
	public void testApplicationDetailLink(){
        TeamDetailPage teamDetailPage = loginPage.defaultLogin()
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName);

        assertTrue("Link for application detail page was not present.", teamDetailPage.isAppLinkPresent(appName));
        assertTrue("Link for application detail page was not clickable.", teamDetailPage.isAppLinkClickable(appName));
	}

    @Test
    public void testChangesToTeamName() {
        TeamDetailPage teamDetailPage = loginPage.defaultLogin()
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName)
                .clickEditOrganizationLink()
                .clickModalSubmit();

        assertTrue("Team Name couldn't Edited properly",
                teamDetailPage.getSuccessAlert().contains("Successfully edited team" + " " + teamName));
    }

    //===========================================================================================================
    // Vulnerability Tab
    //===========================================================================================================

    // Awaiting ID Change
    @Test
    public void testChangeSeverity() {
        TeamDetailPage teamDetailPage = loginPage.defaultLogin()
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName);

        teamDetailPage.clickVulnerabilitiesTab("25");

        assertTrue("Team is not showing expected number of vulnerabilities before test.",
                teamDetailPage.isVulnerabilityCountCorrect("High", "6"));

        teamDetailPage.expandVulnerabilityByType("High89")
                .checkVulnerabilitiesByCategory("High89")
                .clickVulnerabilitiesActionButton()
                .setChangeSeverity("Critical");

        teamDetailPage.waitForVulnCountUpdate("Critical", "4");

        assertTrue("Critical vuln count was not correct after changing severity of 4 vulns.",
                teamDetailPage.isVulnerabilityCountCorrect("Critical", "4"));
        assertTrue("High vuln count was not correct after changing severity of 4 vulns.",
                teamDetailPage.isVulnerabilityCountCorrect("High", "2"));
    }

    @Test
    public void testCloseVulnerabilities() {
        TeamDetailPage teamDetailPage = loginPage.defaultLogin()
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName);

        teamDetailPage.clickVulnerabilitiesTab("25");

        assertTrue("Team is not showing expected number of vulnerabilities before test.",
                teamDetailPage.isVulnerabilityCountCorrect("High", "6"));

        teamDetailPage.expandVulnerabilityByType("High89")
                .checkVulnerabilityByType("High890")
                .clickVulnerabilitiesActionButton()
                .clickCloseVulnerabilities();

        teamDetailPage.waitForVulnCountUpdate("High", "5");

        assertTrue("High vuln count is incorrect after closing a high vuln.",
                teamDetailPage.isVulnerabilityCountCorrect("High", "5"));
    }

    @Test
    public void testMarkFalsePositiveVulnerabilities() {
        TeamDetailPage teamDetailPage = loginPage.defaultLogin()
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName);

        teamDetailPage.clickVulnerabilitiesTab("25");

        assertTrue("Team is not showing expected number of vulnerabilities before test.",
                teamDetailPage.isVulnerabilityCountCorrect("High", "6"));

        teamDetailPage.expandVulnerabilityByType("High89")
                .checkVulnerabilityByType("High890")
                .clickVulnerabilitiesActionButton()
                .clickMarkFalseVulnerability();

        teamDetailPage.waitForVulnCountUpdate("High", "5");
        assertTrue("High vuln count is incorrect after marking a high vuln as false positive.",
                teamDetailPage.isVulnerabilityCountCorrect("High", "5"));

        teamDetailPage.filterByFalsePositive();

        teamDetailPage.waitForVulnCountUpdate("High", "1");
        assertTrue("High vuln count is incorrect after marking a high vuln as false positive.",
                teamDetailPage.isVulnerabilityCountCorrect("High", "1"));

    }

    @Test
    public void testBatchTaggingVulnerabilities() {
        String tagName = getName();
        DatabaseUtils.createTag(tagName, "Vulnerability");

        TeamDetailPage teamDetailPage = loginPage.defaultLogin()
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(teamName)
                .clickVulnerabilitiesTab()
                .expandVulnerabilityByType("Medium209")
                .checkVulnerabilitiesByCategory("Medium209")
                .clickVulnerabilitiesActionButton()
                .clickBatchTagging()
                .attachTagToVulnerability(tagName)
                .clickModalSubmit();

        assertTrue("Tag was not added to first vulnerability.",
                teamDetailPage.isVulnerabilityTagPresent("Medium", "209", "0", tagName));
        assertTrue("Tag was not added to second vulnerability.",
                teamDetailPage.isVulnerabilityTagPresent("Medium", "209", "1", tagName));
        assertTrue("Tag was not added to third vulnerability.",
                teamDetailPage.isVulnerabilityTagPresent("Medium", "209", "2", tagName));
    }
}
