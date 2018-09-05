package com.redfin.selenium;

import com.redfin.patience.PatientWait;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class TestElementFactory
           extends AbstractElementFactory<WebElement, TestElement> {

    private List<Field> fieldsList = null;

    public TestElementFactory() {
        this("DefaultDescription",
             PatientWait.builder().build(),
             Objects::nonNull,
             Duration.ZERO,
             ArrayList::new);
    }

    public TestElementFactory(String description,
                              PatientWait wait,
                              Predicate<WebElement> filter,
                              Duration timeout,
                              Supplier<List<WebElement>> elementListSupplier) {
        super(description, wait, filter, timeout, elementListSupplier);
    }

    @Override
    protected TestElement buildElement(String elementDescription,
                                       Function<Duration, Boolean> notPresentFunction,
                                       Supplier<Optional<WebElement>> elementSupplier,
                                       WebElement initialElement) {
        return new TestElement(elementDescription,
                               1,
                               notPresentFunction,
                               elementSupplier,
                               initialElement);
    }

    public void setFieldsList(List<Field> fieldsList) {
        this.fieldsList = fieldsList;
    }

    public List<Field> getFieldsList() {
        return fieldsList;
    }
}
