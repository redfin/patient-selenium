package com.redfin.selenium.extensions;

import com.redfin.patience.PatientWait;
import com.redfin.selenium.TestElementFactory;
import com.redfin.selenium.contract.Cache;
import com.redfin.selenium.contract.IsPresent;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.mock;

public final class TestElementFactoryParameterResolver
           extends AbstractTestExtension
        implements ParameterResolver {

    private static final String KEY = "ELEMENT_FACTORY_KEY";

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(IsPresent.class) || parameterContext.isAnnotated(Cache.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
        boolean isPresent = parameterContext.findAnnotation(IsPresent.class)
                                            .map(IsPresent::value)
                                            .orElse(false);
        TestElementFactory element = getFromStore(extensionContext, TestElementFactory.class, KEY);
        if (null == element) {
            List<WebElement> list = new ArrayList<>();
            if (isPresent) {
                list.add(mock(WebElement.class));
            }
            element = new TestElementFactory("DefaultDescription",
                                             PatientWait.builder().build(),
                                             Objects::nonNull,
                                             Duration.ZERO,
                                             () -> new ArrayList<>(list));
            saveToStore(extensionContext, KEY, element);
        }
        return element;
    }
}
