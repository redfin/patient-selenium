package com.redfin.patient.selenium;

import com.redfin.patient.selenium.internal.AbstractPsElementLocatorBuilder;
import com.redfin.patient.selenium.internal.Executor;
import com.redfin.patient.selenium.internal.PsConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

public final class PatientWebElementLocatorBuilder
        extends AbstractPsElementLocatorBuilder<WebElement,
        PatientWebElementLocatorBuilder,
        PatientWebElementLocator,
        PatientWebElement> {

    private static final String BY_FORMAT = "%s.find().by(%s)";

    private final Executor<? extends SearchContext> searchExecutor;

    public PatientWebElementLocatorBuilder(String previousDescription,
                                           Executor<? extends SearchContext> searchExecutor,
                                           PsConfig<WebElement> config) {
        super(previousDescription, config);
        this.searchExecutor = validate().withMessage("Cannot use a null search executor.")
                                        .that(searchExecutor)
                                        .isNotNull();
    }

    @Override
    protected PatientWebElementLocatorBuilder getThis() {
        return this;
    }

    @Override
    public PatientWebElementLocator by(By by) {
        validate().withMessage("Cannot use a null By locator")
                  .that(by)
                  .isNotNull();
        Supplier<List<WebElement>> elementSupplier = () -> searchExecutor.apply(e -> e.findElements(by));
        return new PatientWebElementLocator(String.format(BY_FORMAT,
                                                          getPreviousDescription(),
                                                          by),
                                            elementSupplier,
                                            getElementFilter(),
                                            getIsPresentWait(),
                                            getIsNotPresentWait(),
                                            getDefaultIsPresentTimeout(),
                                            getDefaultIsNotPresentTimeout(),
                                            getConfig());
    }
}
