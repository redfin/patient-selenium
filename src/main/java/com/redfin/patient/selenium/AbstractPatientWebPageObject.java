package com.redfin.patient.selenium;

import com.redfin.patient.selenium.internal.AbstractPsPageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class AbstractPatientWebPageObject
        extends AbstractPsPageObject<WebDriver,
        WebElement,
        PatientWebConfig,
        PatientWebDriver,
        PatientWebElementLocatorBuilder,
        PatientWebElementLocator,
        PatientWebElement> {

    // Add any page object specific methods here, for now it is just
    // to make it so that sub-classes not have to deal with the generics.
}
