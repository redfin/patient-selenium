package com.redfin.patient.selenium;

import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.internal.AbstractPsElementLocator;
import com.redfin.patient.selenium.internal.DefaultElementCachingExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PatientWebElementLocator
        extends AbstractPsElementLocator<WebDriver,
        WebElement,
        PatientWebConfig,
        PatientWebDriver,
        PatientWebElementLocatorBuilder,
        PatientWebElementLocator,
        PatientWebElement> {

    public PatientWebElementLocator(String description,
                                    PatientWebConfig config,
                                    PatientWebDriver driver,
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
    protected PatientWebElement buildElement(String description,
                                             WebElement initialElement,
                                             Supplier<WebElement> elementSupplier) {
        return new PatientWebElement(description,
                                     getConfig(),
                                     getDriver(),
                                     new DefaultElementCachingExecutor<>(elementSupplier.get(),
                                                                         elementSupplier));
    }
}
