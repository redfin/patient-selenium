package com.redfin.patient.selenium;

import com.redfin.patient.selenium.internal.AbstractPsElementLocatorBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

public class PatientWebElementLocatorBuilder
        extends AbstractPsElementLocatorBuilder<WebDriver,
        WebElement,
        PatientWebConfig,
        PatientWebDriver,
        PatientWebElementLocatorBuilder,
        PatientWebElementLocator,
        PatientWebElement> {

    public PatientWebElementLocatorBuilder(String description,
                                           PatientWebConfig config,
                                           PatientWebDriver driver,
                                           Function<By, List<WebElement>> baseSeleniumLocatorFunction) {
        super(description,
              config,
              driver,
              baseSeleniumLocatorFunction);
    }

    @Override
    protected PatientWebElementLocatorBuilder getThis() {
        return this;
    }

    @Override
    protected PatientWebElementLocator build(String description,
                                             Supplier<List<WebElement>> elementSupplier) {
        return new PatientWebElementLocator(description,
                                            getConfig(),
                                            getDriver(),
                                            getWait(),
                                            getDefaultTimeout(),
                                            getDefaultNotPresentTimeout(),
                                            elementSupplier,
                                            getElementFilter());
    }

    public PatientWebElementLocator byId(String id) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(id)
                  .isNotEmpty();
        return by(By.id(id));
    }

    public PatientWebElementLocator byClassName(String className) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(className)
                  .isNotEmpty();
        return by(By.className(className));
    }

    public PatientWebElementLocator byName(String name) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(name)
                  .isNotEmpty();
        return by(By.name(name));
    }

    public PatientWebElementLocator byTagName(String tagName) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(tagName)
                  .isNotEmpty();
        return by(By.tagName(tagName));
    }

    public PatientWebElementLocator byCss(String selector) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(selector)
                  .isNotEmpty();
        return by(By.cssSelector(selector));
    }

    public PatientWebElementLocator byXpath(String expression) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(expression)
                  .isNotEmpty();
        return by(By.xpath(expression));
    }

    public PatientWebElementLocator byLinkText(String linkText) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(linkText)
                  .isNotEmpty();
        return by(By.linkText(linkText));
    }

    public PatientWebElementLocator byPartialLinkText(String partialLinkText) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(partialLinkText)
                  .isNotEmpty();
        return by(By.partialLinkText(partialLinkText));
    }
}
