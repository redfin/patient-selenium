package com.redfin.patient.selenium.example;

import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.internal.AbstractPsElementLocator;
import com.redfin.patient.selenium.internal.DefaultElementCachingExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ExampleElementLocator
        extends AbstractPsElementLocator<WebDriver,
        WebElement,
        ExampleConfig,
        ExampleDriver,
        ExampleElementLocatorBuilder,
        ExampleElementLocator,
        ExampleElement> {

    public ExampleElementLocator(String description,
                                 ExampleConfig config,
                                 ExampleDriver driver,
                                 PatientWait wait,
                                 Duration defaultTimeout,
                                 Duration defaultNotPresentTimeout,
                                 Supplier<List<WebElement>> elementSupplier,
                                 Predicate<WebElement> elementFilter) {
        super(description,
              config,
              driver,
              wait,
              defaultTimeout,
              defaultNotPresentTimeout,
              elementSupplier,
              elementFilter);
    }

    @Override
    protected ExampleElement buildElement(String description,
                                          WebElement initialElement,
                                          Supplier<WebElement> elementSupplier) {
        return new ExampleElement(description,
                                  getConfig(),
                                  getDriver(),
                                  new DefaultElementCachingExecutor<>(elementSupplier.get(),
                                                                         elementSupplier));
    }
}
