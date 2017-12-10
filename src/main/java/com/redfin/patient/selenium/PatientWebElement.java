package com.redfin.patient.selenium;

import com.redfin.patient.selenium.internal.AbstractPsElement;
import com.redfin.patient.selenium.internal.CachingExecutor;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PatientWebElement
        extends AbstractPsElement<WebDriver,
        WebElement,
        PatientWebConfig,
        PatientWebDriver,
        PatientWebElementLocatorBuilder,
        PatientWebElementLocator,
        PatientWebElement> {

    public PatientWebElement(String description,
                             PatientWebConfig config,
                             PatientWebDriver driver,
                             CachingExecutor<WebElement> elementExecutor) {
        super(description,
              config,
              driver,
              elementExecutor);
    }

    @Override
    public PatientWebElementLocatorBuilder find() {
        return new PatientWebElementLocatorBuilder(String.format("%s.find()",
                                                                 getDescription()),
                                                   getConfig(),
                                                   getDriver(),
                                                   by -> withWrappedElement().apply(e -> e.findElements(by)));
    }

    public PatientWebElement click() {
        getConfig().getLogConsumer()
                   .accept(String.format("%s.click()",
                                         this));
        withWrappedElement().accept(WebElement::click);
        return this;
    }

    public PatientWebElement submit() {
        getConfig().getLogConsumer()
                   .accept(String.format("%s.submit()",
                                         this));
        withWrappedElement().accept(WebElement::submit);
        return this;
    }

    public PatientWebElement sendKeys(CharSequence... keysToSend) {
        getConfig().getLogConsumer()
                   .accept(String.format("%s.sendKeys(%s)",
                                         this,
                                         Arrays.toString(keysToSend)));
        withWrappedElement().accept(e -> e.sendKeys(keysToSend));
        return this;
    }

    public PatientWebElement clear() {
        getConfig().getLogConsumer()
                   .accept(String.format("%s.clear()",
                                         this));
        withWrappedElement().accept(WebElement::clear);
        return this;
    }

    public String getTagName() {
        return withWrappedElement().apply(WebElement::getTagName);
    }

    public String getText() {
        return withWrappedElement().apply(WebElement::getText);
    }

    public String getAttribute(String name) {
        return withWrappedElement().apply(e -> e.getAttribute(name));
    }

    public String getCssValue(String propertyName) {
        return withWrappedElement().apply(e -> e.getCssValue(propertyName));
    }

    public Point getLocation() {
        return withWrappedElement().apply(WebElement::getLocation);
    }

    public Dimension getSize() {
        return withWrappedElement().apply(WebElement::getSize);
    }

    public Rectangle getRect() {
        return withWrappedElement().apply(WebElement::getRect);
    }

    public boolean isSelected() {
        return withWrappedElement().apply(WebElement::isSelected);
    }

    public boolean isEnabled() {
        return withWrappedElement().apply(WebElement::isEnabled);
    }

    public boolean isDisplayed() {
        return withWrappedElement().apply(WebElement::isDisplayed);
    }
}
