/*
 * Copyright: (c) 2017 Redfin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redfin.patient.selenium.apps;

import com.redfin.patient.selenium.internal.AbstractPageObjectInitializer;
import com.redfin.patient.selenium.internal.FindsElements;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Optional;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

public class iOSNativePageObjectInitializer
        extends AbstractPageObjectInitializer<AppiumDriver<MobileElement>,
        MobileElement,
        NativeMobileDriver,
        NativeMobileConfig,
        NativeMobileElementLocatorBuilder,
        NativeMobileElementLocator,
        NativeMobileElement> {

    public iOSNativePageObjectInitializer(NativeMobileDriver driver) {
        super(driver);
    }

    @Override
    protected Optional<NativeMobileElementLocator> buildElementLocatorOptional(FindsElements<MobileElement,
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
        iOSNativeFindBy find = field.getAnnotation(iOSNativeFindBy.class);
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
            if (!find.uiAutomation().isEmpty()) {
                locatorCount++;
                by = MobileBy.IosUIAutomation(find.uiAutomation());
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
            Duration timeout = Duration.ofSeconds(find.tryingForSeconds());
            return Optional.of(currentContext.find()
                                             .withTimeout(timeout)
                                             .by(by));
        } else {
            // No annotation, return an empty optional
            return Optional.empty();
        }
    }
}
