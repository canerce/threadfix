package com.denimgroup.threadfix.selenium.pages;

import org.openqa.selenium.WebDriver;

/**
 * Created by mghanizadeh on 9/22/2014.
 */
public class WafSecurityEventDetailsPage extends BasePage {


    public WafSecurityEventDetailsPage(WebDriver webdriver) {
        super(webdriver);
    }

    /*------------------------------------ Boolean Methods ------------------------------------*/

    public boolean isLogsNumberPresent() {
        return driver.findElementById("viewSecEvents").isDisplayed();
    }
}
