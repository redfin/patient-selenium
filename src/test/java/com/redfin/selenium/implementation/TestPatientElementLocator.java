package com.redfin.selenium.implementation;

import com.redfin.patience.PatientWait;
import com.redfin.selenium.AbstractPatientElementLocator;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class TestPatientElementLocator
           extends AbstractPatientElementLocator<WebElement, TestPatientConfig, TestPatientElementLocator, TestPatientElement> {

    public TestPatientElementLocator(TestPatientConfig config,
                                     String description,
                                     Supplier<List<WebElement>> elementListSupplier,
                                     PatientWait wait,
                                     Duration timeout,
                                     Predicate<WebElement> filter) {
        super(config, description, elementListSupplier, wait, timeout, filter);
    }

    @Override
    protected TestPatientElementLocator clone(PatientWait wait,
                                              Duration timeout,
                                              Predicate<WebElement> filter) {
        return new TestPatientElementLocator(getConfig(),
                                             getDescription(),
                                             getElementListSupplier(),
                                             wait,
                                             timeout,
                                             filter);
    }

    @Override
    protected String getElementDescription(int index) {
        return String.format("%s.get(%d)", this, index);
    }

    @Override
    protected TestPatientElement buildElement(String elementDescription,
                                              Supplier<Optional<WebElement>> elementSupplier) {
        return new TestPatientElement(getConfig(),
                                      elementDescription,
                                      elementSupplier,
                                      getWait(),
                                      getTimeout());
    }
}
