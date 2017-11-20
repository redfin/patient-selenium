package com.redfin.patient.selenium.example;

import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.internal.AbstractPsElementLocator;
import com.redfin.patient.selenium.internal.DefaultElementCachingExecutor;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ExamplePsElementLocator
        extends AbstractPsElementLocator<WebElement,
        ExamplePsConfig,
        ExamplePsElementLocatorBuilder,
        ExamplePsElementLocator,
        ExamplePsElement> {

    public ExamplePsElementLocator(String description,
                                   ExamplePsConfig config,
                                   PatientWait wait,
                                   Duration defaultTimeout,
                                   Duration defaultAssertNotPresentTimeout,
                                   Supplier<List<WebElement>> elementSupplier,
                                   Predicate<WebElement> elementFilter) {
        super(description,
              config,
              wait,
              defaultTimeout,
              defaultAssertNotPresentTimeout,
              elementSupplier,
              elementFilter);
    }

    @Override
    protected ExamplePsElement buildElement(String elementDescription,
                                            WebElement initialElement,
                                            Supplier<WebElement> elementSupplier) {
        return new ExamplePsElement(elementDescription,
                                    getConfig(),
                                    new DefaultElementCachingExecutor<>(initialElement,
                                                                        elementSupplier));
    }
}