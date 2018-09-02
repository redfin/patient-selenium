package com.redfin.selenium;

import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

final class TestElement
    extends AbstractElement<WebElement> {

    public TestElement(String description,
                       int maxExecutionAttempts,
                       Function<Duration, Boolean> checkIfAbsentFunction,
                       Supplier<Optional<WebElement>> elementSupplier) {
        super(description, maxExecutionAttempts, checkIfAbsentFunction, elementSupplier);
    }

    public TestElement(String description,
                       int maxExecutionAttempts,
                       Function<Duration, Boolean> checkIfAbsentFunction,
                       Supplier<Optional<WebElement>> elementSupplier,
                       WebElement initialElement) {
        super(description, maxExecutionAttempts, checkIfAbsentFunction, elementSupplier, initialElement);
    }
}
