package com.redfin.patient.selenium.example;

import com.redfin.patience.PatientExecutionHandlers;
import com.redfin.patience.PatientRetryHandlers;
import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.internal.AbstractPsConfig;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ExamplePsConfig
        extends AbstractPsConfig<WebElement,
        ExamplePsConfig,
        ExamplePsElementLocatorBuilder,
        ExamplePsElementLocator,
        ExamplePsElement> {

    public ExamplePsConfig() {
        super(PatientWait.builder()
                         .withExecutionHandler(PatientExecutionHandlers.ignoring(NoSuchElementException.class))
                         .withRetryHandler(PatientRetryHandlers.fixedDelay(Duration.ofMillis(500)))
                         .withDefaultTimeout(Duration.ofSeconds(30))
                         .build(),
              Duration.ofSeconds(30),
              Duration.ofSeconds(30),
              Objects::nonNull,
              NoSuchElementException::new);
    }

    public ExamplePsConfig(PatientWait defaultWait,
                           Duration defaultTimeout,
                           Predicate<WebElement> defaultElementFilter,
                           Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction) {
        super(defaultWait,
              defaultTimeout,
              defaultElementFilter,
              elementNotFoundExceptionBuilderFunction);
    }

    public ExamplePsConfig(PatientWait defaultWait,
                           Duration defaultTimeout,
                           Duration defaultAssertNotPresentTimeout,
                           Predicate<WebElement> defaultElementFilter,
                           Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction) {
        super(defaultWait,
              defaultTimeout,
              defaultAssertNotPresentTimeout,
              defaultElementFilter,
              elementNotFoundExceptionBuilderFunction);
    }
}
