package com.redfin.patient.selenium.apps;

import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.internal.AbstractPsConfig;
import io.appium.java_client.MobileElement;

import java.time.Duration;
import java.util.function.Predicate;

public class NativeMobileConfig
        extends AbstractPsConfig<MobileElement,
        NativeMobileConfig,
        NativeMobileElementLocatorBuilder,
        NativeMobileElementLocator,
        NativeMobileElement> {

    public NativeMobileConfig(PatientWait defaultWait,
                              Duration defaultTimeout,
                              Predicate<MobileElement> defaultElementFilter) {
        super(defaultWait, defaultTimeout, defaultElementFilter);
    }

    public NativeMobileConfig(PatientWait defaultWait,
                              Duration defaultTimeout,
                              Duration defaultAssertNotPresentTimeout,
                              Predicate<MobileElement> defaultElementFilter) {
        super(defaultWait, defaultTimeout, defaultAssertNotPresentTimeout, defaultElementFilter);
    }
}
