package com.denimgroup.threadfix.selenium.pages;

import com.denimgroup.threadfix.data.entities.ScheduledEmailReport;
import com.microsoft.tfs.core.clients.build.internal.soapextensions.Schedule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by dharrison on 10/12/2015.
 */
public class ScheduledEmailReportPage extends BasePage {

    public ScheduledEmailReportPage(WebDriver webDriver) { super(webDriver); }

    //===========================================================================================================
    // Action Methods
    //===========================================================================================================

    public ScheduledEmailReportPage clickScheduleNewReport() {
        driver.findElementById("scheduleNewReportButton").click();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage clickTeamsConcernedDropDownButton() {
        driver.findElementById("tagsButton").click();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage clickCreateScheduledReport() {
        driver.findElementById("submit").click();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage clickCloseButton() {
        driver.findElementById("closeModalButton").click();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage clickShowHideEmailAddressButton(String bttnNum) {
        driver.findElementById("showHideEmails"+bttnNum).click();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage clickAddEmailButton() {
        driver.findElementById("addEmailButton").click();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage clickDeleteEmailButton(String email) {
        driver.findElementById("deleteButton"+email).click();
        handleAlert();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage clickAddListButton() {
        driver.findElementById("addListButton").click();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage clickDeleteListButton(String listName) {
        driver.findElementById("deleteEmailList" + listName).click();
        handleAlert();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage clickEditDeleteButton(String bttnNum) {
        driver.findElementById("editDelete"+bttnNum).click();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage clickDeleteButton() {
        driver.findElementById("deleteButton").click();
        handleAlert();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage clickSelectAllButton() {
        driver.findElementById("tagsAllButton").click();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage clickResetButton() {
        driver.findElementById("tagsResetButton").click();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage clickSelectNoneButton() {
        driver.findElementById("tagsNoneButton").click();
        return new ScheduledEmailReportPage(driver);
    }

    //===========================================================================================================
    // Boolean Methods
    //===========================================================================================================

    public boolean isScheduleEmailReportButtonPresent() { return isElementPresent("scheduleNewReportButton"); }

    public boolean isTeamsConcernedButtonPresent() { return isElementPresent("tagsButton"); }

    public boolean isTeamNamePresent(String teamName) {return isElementVisible(teamName); }

    public boolean isSeverityThresholdButtonPresent() { return isElementPresent("severityThresholdSelect"); }

    public boolean isFrequencyButtonPresent() { return isElementPresent("frequency"); }

    public boolean isHoursButtonPresent() { return isElementPresent("hour"); }

    public boolean isMinutesButtonPresent() { return isElementPresent("minute"); }

    public boolean isPeriodButtonPresent() { return isElementPresent("selectedPeriod"); }

    public boolean isDayButtonPresent() { return isElementPresent("selectedDay"); }

    public boolean isCloseButtonPresent() { return isElementPresent("closeModalButton"); }

    public boolean isCreateButtonPresent() { return isElementPresent("submit"); }

    public boolean isShowHideEmailAddressButtonPresent(String bttnNum) { return isElementPresent("showHideEmails"+bttnNum); }

    public boolean isEmailInputTextFieldPresent() { return isElementPresent("emailField"); }

    public boolean isAddEmailButtonPresent() { return isElementPresent("addEmailButton"); }

    public boolean isDeleteEmailButtonPresent(String email) { return isElementPresent("deleteButton"+email); }

    public boolean isEmailListsDropdownButtonPresent() { return isElementPresent("emailListSelect"); }

    public boolean isAddListButtonPresent() { return isElementPresent("addListButton"); }

    public boolean isDeleteEmailListButtonPresent(String listName) { return isElementPresent("deleteEmailList"); }

    public boolean isEditDeleteButtonPresent(String bttnNum) { return isElementPresent("editDelete"+bttnNum); }

    public boolean isDeleteButtonPresent() { return isElementPresent("deleteButton"); }

    public boolean isSelectAllButtonPresent() { return isElementPresent("tagsAllButton"); }

    public boolean isSelectNoneButtonPresent() { return isElementPresent("tagsNoneButton"); }

    public boolean isResetButtonPresent() { return isElementPresent("tagsResetButton"); }

    public boolean isReportPresent(String teamName, String frequency, String severity, String hour, String min, String period){
        WebElement tableResults = driver.findElementById("table").findElement(By.tagName("tbody"));
        WebElement tableRow = tableResults.findElements(By.tagName("tr")).get(1);
        String savedTeamName = tableRow.findElement(By.id("teams0")).findElement(By.id("teamLink0Name" + teamName)).getText();
        String savedSeverity = tableRow.findElement(By.id("severity0")).getText();
        String time = tableRow.findElement(By.id("scheduledTime0")).getText();

        return savedTeamName.equals(teamName) &&
                savedSeverity.equals(severity) &&
                time.contains(hour+":"+min) &&
                time.contains(period) &&
                time.contains(frequency);
    }

    public boolean isEmailAddressPresent(String email){
        String savedEmail = driver.findElementById("email{ emailAddress | removeEmailDomain }}").getText();
        return savedEmail.equals(email);
    }

    public boolean isEmailListPresent(String listName){
        String savedEmailList = driver.findElementById("emailList"+listName).getText();
        return savedEmailList.equals(listName);
    }

    public boolean isEmailSchedulePresent(String teamName){
        return driver.findElements(By.id("teamLink0Name" + teamName)).size() > 0;
    }

    public boolean isNewScheduledEmailReportWindowOpen(){
        sleep(200);
        return driver.findElements(By.id("myModalLabel")).size() > 0;
    }
    //===========================================================================================================
    // Set Methods
    //===========================================================================================================

    public ScheduledEmailReportPage setTeamsConcerned(String teamName) {
        driver.findElementById(teamName).click();
        return new ScheduledEmailReportPage(driver);
    }

    public ScheduledEmailReportPage setSeverityThreshold(String severity) {
        driver.findElementById("severityThresholdSelect").click();
        driver.findElementById("severityThresholdSelect").sendKeys(severity);
        return this;
    }

    public ScheduledEmailReportPage setFrequency(String freq) {
        driver.findElementById("frequency").click();
        driver.findElementById("frequency").sendKeys(freq);;
        return this;
    }

    public ScheduledEmailReportPage setHour(String hour) {
        driver.findElementById("hour").click();
        driver.findElementById("hour").sendKeys(hour);
        return this;
    }

    public ScheduledEmailReportPage setMinutes(String minutes) {
        driver.findElementById("minute").click();
        driver.findElementById("minute").sendKeys(minutes);
        return this;
    }

    public ScheduledEmailReportPage setPeriod(String period) {
        driver.findElementById("selectedPeriod").click();
        driver.findElementById("selectedPeriod").sendKeys(period);
        return this;
    }

    public ScheduledEmailReportPage setDay(String day) {
        driver.findElementById("selectedDay").click();
        driver.findElementById("selectedDay").sendKeys(day);
        return this;
    }

    public ScheduledEmailReportPage setEmailList(String listName) {
        driver.findElementById("emailListSelect").sendKeys(listName);
        return this;
    }

    public ScheduledEmailReportPage setEmailAddress(String email) {
        driver.findElementById("emailField").sendKeys(email);
        return this;
    }
}
