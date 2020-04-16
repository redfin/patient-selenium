package com.redfin.selenium.implementation;

import com.redfin.selenium.AbstractPatientDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Supplier;

public final class TestPatientDriver
           extends AbstractPatientDriver<WebDriver, WebElement, TestPatientConfig, TestPatientElementLocator, TestPatientElement> {

    public TestPatientDriver(TestPatientConfig config,
                             String description,
                             Supplier<WebDriver> webDriverSupplier) {
        super(config, description, webDriverSupplier);
    }

    @Override
    protected String getLocatorDescription(By by) {
        return String.format("%s.find(%s)", this, by);
    }

    @Override
    protected TestPatientElementLocator buildElementLocator(String locatorDescription,
                                                            Supplier<List<WebElement>> elementListSupplier) {
        return new TestPatientElementLocator(getConfig(),
                                             locatorDescription,
                                             elementListSupplier,
                                             getConfig().getDefaultWait(),
                                             getConfig().getDefaultTimeout(),
                                             getConfig().getDefaultFilter());
    }
}
