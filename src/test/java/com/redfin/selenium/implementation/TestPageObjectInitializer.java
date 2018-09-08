package com.redfin.selenium.implementation;

import com.redfin.selenium.AbstractPageObjectInitializer;
import com.redfin.selenium.FindsElements;
import com.redfin.selenium.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.util.Optional;

public final class TestPageObjectInitializer
           extends AbstractPageObjectInitializer<WebElement, TestPatientElementLocator, TestPatientElement> {

    @Override
    protected void preProcessPage(PageObject page) {
        // do nothing here
    }

    @Override
    protected Optional<Object> getValueForField(PageObject page,
                                                Field field,
                                                FindsElements<WebElement, TestPatientElementLocator, TestPatientElement> findsElements) {
        if (TestPatientElementLocator.class.isAssignableFrom(field.getType())) {
            String css = getCssString(field);
            if (null != css) {
                return Optional.of(findsElements.find(By.cssSelector(css)));
            }
        }
        return Optional.empty();
    }

    private String getCssString(Field field) {
        if (null == field) {
            return null;
        }
        FindByCss fieldFind = field.getAnnotation(FindByCss.class);
        return (null == fieldFind) ? null : fieldFind.value().trim();
    }
}
