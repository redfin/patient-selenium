package com.redfin.selenium;

import com.redfin.patience.PatientWait;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class TestElementFactory
    extends AbstractElementFactory<WebElement, TestElement> {

    public TestElementFactory(String description,
                              PatientWait wait,
                              Predicate<WebElement> filter,
                              Duration timeout,
                              Supplier<List<WebElement>> elementListSupplier) {
        super(description, wait, filter, timeout, elementListSupplier);
    }

    @Override
    protected TestElement buildElement(String elementDescription,
                                       Function<Duration, Boolean> notPresentFunction,
                                       Supplier<Optional<WebElement>> elementSupplier,
                                       WebElement initialElement) {
        return new TestElement(elementDescription,
                               1,
                               notPresentFunction,
                               elementSupplier,
                               initialElement);
    }
}
