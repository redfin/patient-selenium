package com.redfin.patient.selenium.example;

import com.redfin.patient.selenium.internal.AbstractPsWidgetObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class AbstractExampleWidgetObject
        extends AbstractPsWidgetObject<WebDriver,
        WebElement,
        ExampleConfig,
        ExampleDriver,
        ExampleElementLocatorBuilder,
        ExampleElementLocator,
        ExampleElement> {

    // Add any widget object specific methods here, for now it is just
    // to make it so that sub-classes not have to deal with the generics.
}
