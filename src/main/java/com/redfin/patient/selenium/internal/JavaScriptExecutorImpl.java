package com.redfin.patient.selenium.internal;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.function.Function;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

class JavaScriptExecutorImpl<D extends WebDriver>
        implements JavaScriptExecutor {

    private final Executor<D> driverExecutor;

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
