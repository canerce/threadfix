package com.denimgroup.threadfix.selenium.tests;

import com.denimgroup.threadfix.CommunityTests;
import com.denimgroup.threadfix.selenium.pages.ScheduledEmailReportPage;
import com.denimgroup.threadfix.selenium.utils.DatabaseUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.validation.constraints.AssertTrue;

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

        assertTrue("The report was not saved successfully", emailReportPage.isReportPresent(teamName, "Daily", "Medium", "8", "45", "PM"));
    }

    @Test
    public void testCreateWeeklyEmailReport(){
        DatabaseUtils.deleteAllEmailReports();
        String teamName = createTeam();
        ScheduledEmailReportPage emailReportPage = initialize();
        assertTrue("New Schedule Email Report button is not present", emailReportPage.isScheduleEmailReportButtonPresent());

        emailReportPage.clickScheduleNewReport()
                .clickTeamsConcernedDropDownButton()
                .setTeamsConcerned(teamName)
                .setSeverityThreshold("Medium")
                .setFrequency("Weekly")
                .setHour("12")
                .setMinutes("30")
                .setPeriod("PM")
                .setDay("Thursday")
                .clickCreateScheduledReport();

        assertTrue("The report was not saved successfully", emailReportPage.isReportPresent(teamName, "Thursdays", "Medium", "12", "30", "PM"));
    }

    @Test
    public void testCloseNewScheduledEmailReportWindow(){
        ScheduledEmailReportPage emailReportPage = initialize();
        emailReportPage.clickScheduleNewReport()
                .clickCloseButton();
        assertFalse("The New Scheduled Email Report Window did not close successfully", emailReportPage.isNewScheduledEmailReportWindowOpen());
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
                .clickAddEmailButton();

        assertTrue("Email address was not added to the email report", emailReportPage.isEmailAddressPresent(email));
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
                .clickAddListButton();

        assertTrue("Email list was not added to the email report", emailReportPage.isEmailListPresent("smkvnu7G46Nw"));
    }

    @Test
    public void testDeleteScheduleEmailReport(){
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

        assertFalse("Email schedule was not delete successfully", emailReportPage.isEmailSchedulePresent(teamName));
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

        assertTrue("The report was not saved successfully", emailReportPage.isReportPresent(teamName, "Mondays", "Info", "4", "30", "AM"));
    }
}
