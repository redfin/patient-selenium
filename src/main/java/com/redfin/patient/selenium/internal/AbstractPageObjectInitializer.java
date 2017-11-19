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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.redfin.validity.Validity.validate;

public abstract class AbstractPageObjectInitializer<D extends WebDriver,
        W extends WebElement,
        P extends PsDriver<D, W, C, B, L, E>,
        C extends PsConfig<W, C, B, L, E>,
        B extends PsElementLocatorBuilder<W, C, B, L, E>,
        L extends PsElementLocator<W, C, B, L, E>,
        E extends PsElement<W, C, B, L, E>> {

    private final P driver;

    public AbstractPageObjectInitializer(P driver) {
        this.driver = validate().withMessage("Cannot use a null patient driver.")
                                .that(driver)
                                .isNotNull();
    }

    protected final P getDriver() {
        return driver;
    }

    protected abstract Optional<L> buildElementLocatorOptional(FindsElements<W, C, B, L, E> currentContext,
                                                               Field field);

    public final <T extends AbstractPageObject<T, D, W, P, C, B, L, E>> void initialize(T page) {
        validate().withMessage("Cannot use a null page.")
                  .that(page)
                  .isNotNull();
        initialize(driver, page);
    }

    public final <T extends AbstractPageObject<T, D, W, P, C, B, L, E>> void initialize(FindsElements<W, C, B, L, E> searchContext,
                                                                                        T page) {
        validate().withMessage("Cannot use a null search context.")
                  .that(searchContext)
                  .isNotNull();
        validate().withMessage("Cannot use a null page.")
                  .that(page)
                  .isNotNull();
        // As we initialize, since we recursively initialize all page objects in the
        // given page we have to avoid infinite loops caused by dependency cycles so
        // we need to keep track of which objects have been visited by the initializer.
        List<Object> initializedObjects = new ArrayList<>();
        initializeHelper(driver,
                         searchContext,
                         page,
                         initializedObjects);
    }

    private void initializeHelper(P driver,
                                  FindsElements<W, C, B, L, E> searchContext,
                                  Object page,
                                  List<Object> visitedObjects) {
        validate().withMessage("Cannot use a null driver.")
                  .that(driver)
                  .isNotNull();
        validate().withMessage("Cannot use a null search context.")
                  .that(searchContext)
                  .isNotNull();
        validate().withMessage("Cannot use a null page.")
                  .that(page)
                  .isNotNull();
        validate().withMessage("Cannot use a null visited objects list.")
                  .that(visitedObjects)
                  .isNotNull();
        // Don't do anything if the given page has already been initialized
        if (visitedObjects.stream().noneMatch(o -> page == o)) {
            // Add the page to the visited objects list
            visitedObjects.add(page);
            // We want to initialize all the fields of the page object, including private ones
            // so we need to loop through the inheritance hierarchy. We don't need to worry about
            // interfaces since they can't have instance fields.
            Class<?> clazz = page.getClass();
            while (clazz != Object.class) {
                // Get all the fields of the current class for this page
                try {
                    for (Field field : clazz.getDeclaredFields()) {
                        // Check if the field is either an element locator or a page object
                        if (AbstractPageObject.class.isAssignableFrom(field.getType())) {
                            field.setAccessible(true);
                            // Only initialize non-null page object fields
                            Object nextPage = field.get(page);
                            if (null != nextPage) {
                                // Non-null page object field, initialize it as well starting with figuring out
                                // the page search context
                                Optional<L> optPageLocator = buildElementLocatorOptional(searchContext, field);
                                FindsElements<W, C, B, L, E> newPageSearchContext;
                                if (optPageLocator.isPresent()) {
                                    newPageSearchContext = optPageLocator.get().get();
                                } else {
                                    newPageSearchContext = searchContext;
                                }
                                initializeHelper(driver,
                                                 newPageSearchContext,
                                                 nextPage,
                                                 visitedObjects);
                            }
                        } else if (PsElementLocator.class.isAssignableFrom(field.getType())) {
                            field.setAccessible(true);
                            // Only initialize null element locator fields
                            Object elementLocator = field.get(page);
                            if (null == elementLocator) {
                                // Null element locator field, initialize it
                                Optional<L> optElementLocator = buildElementLocatorOptional(searchContext, field);
                                if (optElementLocator.isPresent()) {
                                    field.set(page, optElementLocator.get());
                                }
                            }
                        }
                    }
                    // Check if this is the AbstractPageObject class
                    if (clazz == AbstractPageObject.class) {
                        // initialize the driver field
                        Field field = AbstractPageObject.class.getDeclaredField("driver");
                        field.setAccessible(true);
                        field.set(page, driver);
                    }
                } catch (NoSuchFieldException | IllegalAccessException exception) {
                    throw new RuntimeException("Error initializing the page object.", exception);
                }
                // Go up the page object class hierarchy
                clazz = clazz.getSuperclass();
            }
        }
    }
}
