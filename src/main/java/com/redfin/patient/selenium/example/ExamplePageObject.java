package com.redfin.patient.selenium.example;

import com.redfin.patient.selenium.internal.AbstractPageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class ExamplePageObject<THIS extends ExamplePageObject<THIS>>
        extends AbstractPageObject<THIS,
        WebDriver,
        WebElement,
        ExamplePsDriver,
        ExamplePsConfig,
        ExamplePsElementLocatorBuilder,
        ExamplePsElementLocator,
        ExamplePsElement> {

    // add any base page object methods here, otherwise it is useful for allowing subclasses
    // to not deal with the generics.
}
