package com.redfin.patient.selenium.example;

import com.redfin.patient.selenium.internal.CachingExecutor;
import com.redfin.patient.selenium.internal.AbstractPsDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public final class ExamplePsDriver
        extends AbstractPsDriver<WebDriver,
        WebElement,
        ExamplePsConfig,
        ExamplePsDriver,
        ExamplePsElementLocatorBuilder,
        ExamplePsElementLocator,
        ExamplePsElement> {

    public ExamplePsDriver(String description,
                           ExamplePsConfig config,
                           CachingExecutor<WebDriver> driverExecutor) {
        super(description, config, driverExecutor);
    }

    @Override
    protected ExamplePsElementLocatorBuilder createElementLocatorBuilder(String elementLocatorBuilderDescription) {
        return new ExamplePsElementLocatorBuilder(elementLocatorBuilderDescription,
                                                  getConfig(),
                                                  by -> withWrappedDriver().apply(d -> d.findElements(by)),
                                                  this);
    }

    // Add in the API methods desired for the element

    public ExamplePsDriver get(String url) {
        withWrappedDriver().accept(d -> d.get(url));
        return this;
    }
}
