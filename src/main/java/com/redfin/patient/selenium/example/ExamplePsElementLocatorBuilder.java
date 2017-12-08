package com.redfin.patient.selenium.example;

import com.redfin.patient.selenium.internal.AbstractPsElementLocatorBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ExamplePsElementLocatorBuilder
        extends AbstractPsElementLocatorBuilder<WebDriver,
        WebElement,
        ExamplePsConfig,
        ExamplePsDriver,
        ExamplePsElementLocatorBuilder,
        ExamplePsElementLocator,
        ExamplePsElement> {

    public ExamplePsElementLocatorBuilder(String description,
                                          ExamplePsConfig config,
                                          Function<By, List<WebElement>> baseSeleniumLocatorFunction,
                                          ExamplePsDriver driver) {
        super(description, config, baseSeleniumLocatorFunction, driver);
    }

    @Override
    protected ExamplePsElementLocatorBuilder getThis() {
        return this;
    }

    @Override
    protected ExamplePsElementLocator build(String description,
                                            Supplier<List<WebElement>> elementSupplier) {
        return new ExamplePsElementLocator(description,
                                           getConfig(),
                                           getDefaultWait(),
                                           getDefaultTimeout(),
                                           getDefaultAssertNotPresentTimeout(),
                                           elementSupplier,
                                           getElementFilter(),
                                           getDriver());
    }
}
