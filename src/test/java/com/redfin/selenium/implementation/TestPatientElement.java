package com.redfin.selenium.implementation;

import com.redfin.patience.PatientWait;
import com.redfin.selenium.AbstractPatientElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class TestPatientElement
           extends AbstractPatientElement<WebElement, TestPatientConfig, TestPatientElementLocator, TestPatientElement> {

    public TestPatientElement(TestPatientConfig config,
                              String description,
                              Supplier<Optional<WebElement>> elementSupplier,
                              PatientWait wait,
                              Duration timeout) {
        super(config, description, elementSupplier, wait, timeout);
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
