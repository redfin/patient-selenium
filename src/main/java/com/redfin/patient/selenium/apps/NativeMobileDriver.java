package com.redfin.patient.selenium.apps;

import com.redfin.patient.selenium.internal.AbstractPsDriver;
import com.redfin.patient.selenium.internal.CachingExecutor;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class NativeMobileDriver
        extends AbstractPsDriver<AppiumDriver<MobileElement>,
        MobileElement,
        NativeMobileConfig,
        NativeMobileElementLocatorBuilder,
        NativeMobileElementLocator,
        NativeMobileElement> {

    public NativeMobileDriver(String description,
                              NativeMobileConfig config,
                              CachingExecutor<AppiumDriver<MobileElement>> driverExecutor) {
        super(description, config, driverExecutor);
    }

    @Override
    protected final NativeMobileElementLocatorBuilder createElementLocatorBuilder(String elementLocatorBuilderDescription,
                                                                                  NativeMobileConfig config) {
        return new NativeMobileElementLocatorBuilder(elementLocatorBuilderDescription,
                                                     config,
                                                     by -> withWrappedDriver().apply(d -> d.findElements(by)));
    }
}
