package com.denimgroup.threadfix.selenium.tests;

import com.denimgroup.threadfix.CommunityTests;
import com.denimgroup.threadfix.selenium.pages.ScheduledEmailReportPage;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by ngrimaldo on 10/13/2015.
 */
public class EmailReportIT extends BaseIT {

    public ScheduledEmailReportPage initialize() {
        return loginPage.defaultLogin()
                .clickEmailReportsLink();
    }

    @Test
    public void testCreateDailyEmailReport(){
        String teamName = createTeam();
        ScheduledEmailReportPage emailReportPage = initialize();
        assertTrue("New Schedule Email Report button is not present", emailReportPage.isScheduleEmailReportButtonPresent());

        emailReportPage.clickScheduleNewReport()
                .clickTeamsConcernedDropDownButton()
                .setTeamsConcerned(teamName)
                .setSeverityThreshold("Medium")
                .setFrequency("Daily")
                .setHour("8")
                .setMinutes("45")
                .setPeriod("PM")
                .clickCreateScheduledReport();
    }

    @Test
    public void testCreateWeeklyEmailReport(){
        String teamName = createTeam();
        ScheduledEmailReportPage emailReportPage = initialize();
        assertTrue("New Schedule Email Report button is not present", emailReportPage.isScheduleEmailReportButtonPresent());

        emailReportPage.clickScheduleNewReport()
                .clickTeamsConcernedDropDownButton()
                .setTeamsConcerned(teamName)
                .setSeverityThreshold("Medium")
                .setFrequency("Weekly")
                .setHour("8")
                .setMinutes("45")
                .setPeriod("PM")
                .setDay("Thursday")
                .clickCreateScheduledReport();
    }

    @Test
    public void testCloseEmailReportWindow(){
        ScheduledEmailReportPage emailReportPage = initialize();

        emailReportPage.clickScheduleNewReport()
                .clickCloseButton();
    }

    @Test
    public void testAddEmailAddressToSchedule() {
        String email = getEmailAddress();
        String teamName = createTeam();
        ScheduledEmailReportPage emailReportPage = initialize();
        assertTrue("New Schedule Email Report button is not present", emailReportPage.isScheduleEmailReportButtonPresent());

        emailReportPage.clickScheduleNewReport()
                .clickTeamsConcernedDropDownButton()
                .setTeamsConcerned(teamName)
                .setSeverityThreshold("Medium")
                .setFrequency("Daily")
                .setHour("8")
                .setMinutes("45")
                .setPeriod("PM")
                .clickCreateScheduledReport()
                .clickShowHideEmailAddressButton("0")
                .setEmailAddress(email)
                .clickAddEmailButton()
                .clickDeleteEmailButton(email.substring(0,email.indexOf('@')));
    }

    @Test
    public void testAddEmailListToSchedule(){
        String teamName = createTeam();
        ScheduledEmailReportPage emailReportPage = initialize();
        assertTrue("New Schedule Email Report button is not present", emailReportPage.isScheduleEmailReportButtonPresent());

        emailReportPage.clickScheduleNewReport()
                .clickTeamsConcernedDropDownButton()
                .setTeamsConcerned(teamName)
                .setSeverityThreshold("Medium")
                .setFrequency("Daily")
                .setHour("8")
                .setMinutes("45")
                .setPeriod("PM")
                .clickCreateScheduledReport()
                .clickShowHideEmailAddressButton("0")
                .setEmailList("smkvnu7G46Nw")
                .clickAddListButton()
                .clickDeleteListButton("smkvnu7G46Nw");
    }

    @Test
    public void testDeleteEmailScheduleEmailReport(){
        String teamName = createTeam();
        ScheduledEmailReportPage emailReportPage = initialize();
        assertTrue("New Schedule Email Report button is not present", emailReportPage.isScheduleEmailReportButtonPresent());

        emailReportPage.clickScheduleNewReport()
                .clickTeamsConcernedDropDownButton()
                .setTeamsConcerned(teamName)
                .setSeverityThreshold("Medium")
                .setFrequency("Daily")
                .setHour("8")
                .setMinutes("45")
                .setPeriod("PM")
                .clickCreateScheduledReport()
                .clickEditDeleteButton("0")
                .clickDeleteButton();
    }

    @Test
    public void testEditScheduledEmailReport(){
        String teamName = createTeam();
        ScheduledEmailReportPage emailReportPage = initialize();
        assertTrue("New Schedule Email Report button is not present", emailReportPage.isScheduleEmailReportButtonPresent());

        emailReportPage.clickScheduleNewReport()
                .clickTeamsConcernedDropDownButton()
                .setTeamsConcerned(teamName)
                .setSeverityThreshold("Medium")
                .setFrequency("Daily")
                .setHour("8")
                .setMinutes("45")
                .setPeriod("PM")
                .clickCreateScheduledReport()
                .clickEditDeleteButton("0")
                .clickTeamsConcernedDropDownButton()
                .clickSelectAllButton()
                .clickResetButton()
                .clickSelectNoneButton()
                .setTeamsConcerned(teamName)
                .setSeverityThreshold("Info")
                .setFrequency("Weekly")
                .setHour("4")
                .setMinutes("30")
                .setPeriod("AM")
                .setDay("Monday")
                .clickCreateScheduledReport();
    }
}
