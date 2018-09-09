package com.redfin.selenium.implementation;

import com.redfin.selenium.AbstractPageObjectInitializer;
import com.redfin.selenium.FindsElements;
import com.redfin.selenium.PageObjectInitializationException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;

public final class TestPageObjectInitializer
           extends AbstractPageObjectInitializer<WebDriver,
        WebElement,
        TestPatientConfig,
        TestPatientDriver,
        TestPatientElementLocator,
        TestPatientElement> {

    public TestPageObjectInitializer(TestPatientDriver driver) {
        super(driver);
    }

    @Override
    protected Class<TestPatientElement> getElementClass() {
        return TestPatientElement.class;
    }

    @Override
    protected Class<TestPatientElementLocator> getElementLocatorClass() {
        return TestPatientElementLocator.class;
    }

    @Override
    protected TestPatientElement buildElement(Field field,
                                              FindsElements<WebElement, TestPatientConfig, TestPatientElementLocator, TestPatientElement> findsElements) {
        return findsElements.find(By.cssSelector(getCssString(field))).get(0);
    }

    @Override
    protected TestPatientElementLocator buildElementLocator(Field field,
                                                            FindsElements<WebElement, TestPatientConfig, TestPatientElementLocator, TestPatientElement> findsElements) {
        return findsElements.find(By.cssSelector(getCssString(field)));
    }

    private String getCssString(Field field) {
        if (null == field) {
            throw new PageObjectInitializationException("Cannot build a By for a null field");
        }
        FindByCss fieldFind = field.getAnnotation(FindByCss.class);
        if (null == fieldFind) {
            throw new PageObjectInitializationException("Cannot initialize with a null FindByCss");
        }
        return fieldFind.value().trim();
    }
}
