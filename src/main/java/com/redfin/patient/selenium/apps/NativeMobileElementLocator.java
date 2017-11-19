package com.redfin.patient.selenium.apps;

import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.internal.AbstractPsElementLocator;
import com.redfin.patient.selenium.internal.DefaultElementCachingExecutor;
import io.appium.java_client.MobileElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class NativeMobileElementLocator
        extends AbstractPsElementLocator<MobileElement,
        NativeMobileConfig,
        NativeMobileElementLocatorBuilder,
        NativeMobileElementLocator,
        NativeMobileElement> {

    public NativeMobileElementLocator(String description,
                                      NativeMobileConfig config,
                                      PatientWait wait,
                                      Duration defaultTimeout,
                                      Duration defaultAssertNotPresentTimeout,
                                      Supplier<List<MobileElement>> elementSupplier,
                                      Predicate<MobileElement> elementFilter) {
        super(description,
              config,
              wait,
              defaultTimeout,
              defaultAssertNotPresentTimeout,
              elementSupplier,
              elementFilter);
    }

    @Override
    protected NativeMobileElement buildElement(String elementDescription,
                                               MobileElement initialElement,
                                               Supplier<MobileElement> elementSupplier) {
        return new NativeMobileElement(elementDescription,
                                       getConfig(),
                                       new DefaultElementCachingExecutor<>(initialElement,
                                                                           elementSupplier));
    }
}
