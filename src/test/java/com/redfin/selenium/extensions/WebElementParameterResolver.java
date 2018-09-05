package com.redfin.selenium.extensions;

import com.redfin.selenium.contract.Cacheable;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.WebElement;

import java.util.Optional;

import static org.mockito.Mockito.mock;

public final class WebElementParameterResolver
           extends AbstractTestExtension
        implements ParameterResolver {

    private static final String KEY = "WEB_ELEMENT_KEY";

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(Cacheable.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
        Optional<Cacheable> annotation = parameterContext.findAnnotation(Cacheable.class);
        if (!annotation.isPresent()) {
            throw new ParameterResolutionException("Shouldn't be here unless the parameter is annotation with @Cacheable");
        }
        WebElement element = getFromStore(extensionContext, WebElement.class, KEY);
        if (null == element) {
            element = mock(WebElement.class);
            saveToStore(extensionContext, KEY, element);
        }
        return element;
    }
}
