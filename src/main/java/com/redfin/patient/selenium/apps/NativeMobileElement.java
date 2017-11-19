package com.redfin.patient.selenium.apps;

import com.redfin.patient.selenium.internal.AbstractPsElement;
import com.redfin.patient.selenium.internal.CachingExecutor;
import io.appium.java_client.MobileElement;

public class NativeMobileElement
        extends AbstractPsElement<MobileElement,
        NativeMobileConfig,
        NativeMobileElementLocatorBuilder,
        NativeMobileElementLocator,
        NativeMobileElement> {

    public NativeMobileElement(String description,
                               NativeMobileConfig config,
                               CachingExecutor<MobileElement> elementExecutor) {
        super(description, config, elementExecutor);
    }

    @Override
    protected final NativeMobileElementLocatorBuilder createElementLocatorBuilder(String elementLocatorBuilderDescription,
                                                                                  NativeMobileConfig config) {
        return new NativeMobileElementLocatorBuilder(elementLocatorBuilderDescription,
                                                     config,
                                                     by -> withWrappedElement().apply(e -> e.findElements(by)));
    }

    public boolean isDisplayed() {
        return withWrappedElement().apply(MobileElement::isDisplayed);
    }

    public NativeMobileElement tap() {
        withWrappedElement().accept(MobileElement::click);
        return this;
    }

    public NativeMobileElement sendKeys(CharSequence... keysToSend) {
        withWrappedElement().accept(e -> e.sendKeys(keysToSend));
        return this;
    }
}
