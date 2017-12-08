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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.redfin.validity.Validity.validate;

public abstract class AbstractPsElement<D extends WebDriver,
        W extends WebElement,
        C extends AbstractPsConfig<D, W, C, P, B, L, THIS>,
        P extends AbstractPsDriver<D, W, C, P, B, L, THIS>,
        B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, L, THIS>,
        L extends AbstractPsElementLocator<D, W, C, P, B, L, THIS>,
        THIS extends AbstractPsElement<D, W, C, P, B, L, THIS>>
        extends AbstractPsBase<W, C, B, L, THIS>
        implements PsElement<W, C, B, L, THIS> {

    private final CachingExecutor<W> elementExecutor;
    private final P driver;

    public AbstractPsElement(String description,
                             C config,
                             CachingExecutor<W> elementExecutor,
                             P driver) {
        super(description, config);
        this.elementExecutor = validate().withMessage("Cannot use a null element executor.")
                                         .that(elementExecutor)
                                         .isNotNull();
        this.driver = validate().withMessage("Cannot use a null driver.")
                                .that(driver)
                                .isNotNull();
    }

    protected final P getDriver() {
        return driver;
    }

    protected abstract B createElementLocatorBuilder(String elementLocatorBuilderDescription);

    @Override
    public final CachingExecutor<W> withWrappedElement() {
        return elementExecutor;
    }

    @Override
    public final B find() {
        return createElementLocatorBuilder(String.format("%s.find()",
                                                         getDescription()));
    }

    protected final void accept(Consumer<W> elementConsumer) {
        validate().withMessage("Cannot execute a null consumer.")
                  .that(elementConsumer)
                  .isNotNull();
        apply(e -> {
            elementConsumer.accept(e);
            return null;
        });
    }

    protected final void accept(BiConsumer<D, W> elementAndDriverConsumer) {
        validate().withMessage("Cannot execute a null consumer.")
                  .that(elementAndDriverConsumer)
                  .isNotNull();
        apply((d, e) -> {
            elementAndDriverConsumer.accept(d, e);
            return null;
        });
    }

    protected final <R> R apply(Function<W, R> elementFunction) {
        validate().withMessage("Cannot execute a null function.")
                  .that(elementFunction)
                  .isNotNull();
        return withWrappedElement().apply(elementFunction);
    }

    protected final <R> R apply(BiFunction<D, W, R> elementAndDriverFunction) {
        validate().withMessage("Cannot execute a null function.")
                  .that(elementAndDriverFunction)
                  .isNotNull();
        return withWrappedElement().apply(e -> getDriver().withWrappedDriver().apply(d -> elementAndDriverFunction.apply(d, e)));
    }
}
