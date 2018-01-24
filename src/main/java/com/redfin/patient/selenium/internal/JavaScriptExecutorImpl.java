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

package com.redfin.patient.selenium.internal;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.function.Function;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

/**
 * Concrete implementation of the {@link JavaScriptExecutor} interface for
 * use with the Patient-Selenium library's abstract classes.
 *
 * @param <D> the type of {@link WebDriver} to be used.
 */
final class JavaScriptExecutorImpl<D extends WebDriver>
 implements JavaScriptExecutor {

    private final Executor<D> driverExecutor;

    /**
     * Create a new {@link JavaScriptExecutorImpl} instance with the given
     * driver executor.
     *
     * @param driverExecutor the {@link Executor} to be used to execute JavaScript.
     *                       May not be null.
     *
     * @throws IllegalArgumentException if driverExecutor is null.
     */
    public JavaScriptExecutorImpl(Executor<D> driverExecutor) {
        this.driverExecutor = validate().withMessage("Cannot use a null driver executor.")
                                        .that(driverExecutor)
                                        .isNotNull();
    }

    @Override
    public Object script(String script,
                         Object... args) {
        return driverExecutor.apply(driver -> {
            expect().withMessage("Cannot execute JavaScript as the wrapped driver is not a JavascriptExecutor.")
                    .that(driver)
                    .satisfies(d -> d instanceof JavascriptExecutor);
            return execute(array -> ((JavascriptExecutor) driver).executeScript(script, array),
                           args);
        });
    }

    @Override
    public Object asyncScript(String script,
                              Object... args) {
        return driverExecutor.apply(driver -> {
            expect().withMessage("Cannot execute JavaScript as the wrapped driver is not a JavascriptExecutor.")
                    .that(driver)
                    .satisfies(d -> d instanceof JavascriptExecutor);
            return execute(array -> ((JavascriptExecutor) driver).executeAsyncScript(script, array),
                           args);
        });
    }

    private Object execute(Function<Object[], Object> function, Object... args) {
        if (null == args) {
            args = new Object[0];
        }
        for (int i = 0; i < args.length; i++) {
            // We want any uses of elements in a script to be able to wait to be found
            // and to also resist stale elements which means they need to be used from
            // inside the element executor object.
            Object arg = args[i];
            int index = i;
            Object[] newArgs = Arrays.copyOf(args, args.length);
            if (arg instanceof AbstractPsElement<?, ?, ?, ?, ?, ?, ?>) {
                return ((AbstractPsElement<?, ?, ?, ?, ?, ?, ?>) arg).withWrappedElement().apply(e -> {
                    newArgs[index] = e;
                    return execute(function, newArgs);
                });
            }
        }
        return function.apply(args);
    }
}
