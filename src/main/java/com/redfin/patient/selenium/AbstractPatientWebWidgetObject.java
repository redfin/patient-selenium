package com.redfin.patient.selenium;

import com.redfin.patient.selenium.internal.AbstractPsWidgetObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class AbstractPatientWebWidgetObject
        extends AbstractPsWidgetObject<WebDriver,
        WebElement,
        PatientWebConfig,
        PatientWebDriver,
        PatientWebElementLocatorBuilder,
        PatientWebElementLocator,
        PatientWebElement> {

    // Add any widget object specific methods here, for now it is just
    // to make it so that sub-classes not have to deal with the generics.
}
