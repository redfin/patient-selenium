package com.redfin.patient.selenium.example;

import com.redfin.patient.selenium.internal.AbstractPsPageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class AbstractExamplePageObject
        extends AbstractPsPageObject<WebDriver,
        WebElement,
        ExampleConfig,
        ExampleDriver,
        ExampleElementLocatorBuilder,
        ExampleElementLocator,
        ExampleElement> {

    // Add any page object specific methods here, for now it is just
    // to make it so that sub-classes not have to deal with the generics.
}
