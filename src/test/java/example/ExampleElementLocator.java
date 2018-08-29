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

package example;

import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.AbstractPsElementLocator;
import com.redfin.patient.selenium.DefaultElementExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

public class ExampleElementLocator
     extends AbstractPsElementLocator<WebDriver,
                                      WebElement,
                                      ExampleConfig,
                                      ExampleDriver,
                                      ExampleElementLocatorBuilder,
                                      ExampleElementLocator,
                                      ExampleElement> {

    public ExampleElementLocator(String description,
                                 ExampleConfig config,
                                 ExampleDriver driver,
                                 PatientWait wait,
                                 Duration defaultTimeout,
                                 Duration defaultNotPresentTimeout,
                                 Supplier<List<WebElement>> elementSupplier,
                                 Predicate<WebElement> elementFilter) {
        super(description,
              config,
              driver,
              wait,
              defaultTimeout,
              defaultNotPresentTimeout,
              elementSupplier,
              elementFilter);
    }

    @Override
    protected ExampleElement buildElement(String description,
                                          WebElement initialElement,
                                          Supplier<WebElement> elementSupplier) {
        return new ExampleElement(description,
                                  getConfig(),
                                  getDriver(),
                                  new DefaultElementExecutor<>(elementSupplier.get(),
                                                               elementSupplier));
    }

    public void assertIsPresent() {
        assertIsPresent(null, getDefaultTimeout());
    }

    public void assertIsPresent(String message) {
        assertIsPresent(message, getDefaultTimeout());
    }

    public void assertIsPresent(Duration timeout) {
        assertIsPresent(null, timeout);
    }

    public void assertIsPresent(String message, Duration timeout) {
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        String finalMessage;
        if (null == message) {
            finalMessage = String.format("No element found within %s that matched: %s",
                                         timeout,
                                         getDescription());
        } else {
            finalMessage = message;
        }
        ifPresent(e -> {}, timeout).orElse(() -> { throw new AssertionError(finalMessage); });
    }

    public void assertIsNotPresent() {
        assertIsNotPresent(null, getDefaultTimeout());
    }

    public void assertIsNotPresent(String message) {
        assertIsNotPresent(message, getDefaultTimeout());
    }

    public void assertIsNotPresent(Duration timeout) {
        assertIsNotPresent(null, timeout);
    }

    public void assertIsNotPresent(String message, Duration timeout) {
        validate().withMessage("Cannot use a null or negative timeout.")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        String finalMessage;
        if (null == message) {
            finalMessage = String.format("Element still found within %s that matched: %s",
                                         timeout,
                                         getDescription());
        } else {
            finalMessage = message;
        }
        ifNotPresent(() -> {}, timeout).orElse(() -> { throw new AssertionError(finalMessage); });
    }
}