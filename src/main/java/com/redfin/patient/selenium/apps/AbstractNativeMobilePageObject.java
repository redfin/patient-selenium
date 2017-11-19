package com.redfin.patient.selenium.apps;

import com.redfin.patient.selenium.internal.AbstractPageObject;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public abstract class AbstractNativeMobilePageObject<T extends AbstractNativeMobilePageObject<T>>
        extends AbstractPageObject<AppiumDriver<MobileElement>,
        MobileElement,
        NativeMobileDriver,
        NativeMobileConfig,
        NativeMobileElementLocatorBuilder,
        NativeMobileElementLocator,
        NativeMobileElement> {

    protected abstract T getThis();
}
