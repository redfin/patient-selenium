package com.redfin.selenium.extensions;

import com.redfin.selenium.TestElement;
import com.redfin.selenium.contract.Cache;
import com.redfin.selenium.contract.IsPresent;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.WebElement;

import java.util.Optional;

import static org.mockito.Mockito.mock;

public final class TestElementParameterResolver
           extends AbstractTestExtension
        implements ParameterResolver {

    private static final String KEY = "ELEMENT_KEY";

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
        TestElement element = getFromStore(extensionContext, TestElement.class, KEY);
        if (null == element) {
            element = new TestElement("DefaultDescription",
                                      1,
                                      duration -> !isPresent,
                                      () -> isPresent ? Optional.of(mock(WebElement.class)) : Optional.empty(),
                                      null);
            saveToStore(extensionContext, KEY, element);
        }
        return element;
    }
}
