package com.redfin.patient.selenium.apps;

import com.redfin.patient.selenium.internal.FindsElements;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Optional;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

public class AndroidNativePageObjectInitializer
        extends AbstractNativeMobilePageObjectInitializer {

    public AndroidNativePageObjectInitializer(NativeMobileDriver driver) {
        super(driver);
    }

    @Override
    protected Optional<NativeMobileElementLocator> buildPlatformSpecificElementLocatorOptional(FindsElements<MobileElement,
            NativeMobileConfig,
            NativeMobileElementLocatorBuilder,
            NativeMobileElementLocator,
            NativeMobileElement> currentContext,
                                                                                               Field field) {
        validate().withMessage("Cannot use a null current search context")
                  .that(currentContext)
                  .isNotNull();
        validate().withMessage("Cannot use a null field.")
                  .that(field)
                  .isNotNull();
        // Check if the page object field on the outer page object has an annotation on it
        AndroidNativeFindBy find = field.getAnnotation(AndroidNativeFindBy.class);
        if (null != find) {
            // There is an annotation, use it to build an element locator
            int locatorCount = 0;
            By by = null;
            if (!find.id().isEmpty()) {
                locatorCount++;
                by = MobileBy.id(find.id());
            }
            if (!find.accessibility().isEmpty()) {
                locatorCount++;
                by = MobileBy.AccessibilityId(find.accessibility());
            }
            if (!find.uiAutomator().isEmpty()) {
                locatorCount++;
                by = MobileBy.AndroidUIAutomator(find.uiAutomator());
            }
            if (!find.xpath().isEmpty()) {
                locatorCount++;
                by = MobileBy.xpath(find.xpath());
            }
            expect().withMessage("Must select exactly 1 locator strategy for the iOSNativeFindBy annotation")
                    .that(locatorCount)
                    .isEqualTo(1);
            expect().withMessage("There was a selector strategy located but the by is null.")
                    .that(by)
                    .isNotNull();
            Duration timeout = Duration.ofSeconds(find.tryingFor());
            return Optional.of(currentContext.find()
                                             .withTimeout(timeout)
                                             .by(by));
        } else {
            // No annotation, return an empty optional
            return Optional.empty();
        }
    }
}
