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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.redfin.validity.Validity.validate;

/**
 * Base class for any page object initializer implementation.
 * The page object initializer is responsible for initializing any element
 * locators in a page object or widget object recursively. What types of annotations
 * are supported and how they are converted into an element locator is up to the
 * concrete subclass.
 *
 * @param <D> the type of wrapped WebDriver.
 * @param <W> the type of wrapped WebElement.
 * @param <C> the type of {@link AbstractPsConfig} used by the implementing type.
 * @param <P> the type of {@link AbstractPsDriver} used by the implementing type.
 * @param <B> the type of {@link AbstractPsElementLocatorBuilder} used by the implementing type.
 * @param <L> the type of {@link AbstractPsElementLocator} used by the implementing type.
 * @param <E> the type of {@link AbstractPsElement} used by the implementing type.
 */
public abstract class AbstractPsPageObjectInitializer<D extends WebDriver,
                                                      W extends WebElement,
                                                      C extends AbstractPsConfig<D, W, C, P, B, L, E>,
                                                      P extends AbstractPsDriver<D, W, C, P, B, L, E>,
                                                      B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, L, E>,
                                                      L extends AbstractPsElementLocator<D, W, C, P, B, L, E>,
                                                      E extends AbstractPsElement<D, W, C, P, B, L, E>> {

    private final P driver;
    private final Class<L> elementLocatorClass;

    /**
     * Create a new {@link AbstractPsPageObjectInitializer} instance with the given
     * driver and element locator type.
     *
     * @param driver              the driver used as the parent search context for locating elements.
     *                            May not be null.
     * @param elementLocatorClass the type of element locator to be initialized. Note that
     *                            the initializer will not initialize sub class types of this.
     *                            May not be null.
     *
     * @throws IllegalArgumentException if either driver or elementLocatorClass are null.
     */
    public AbstractPsPageObjectInitializer(P driver,
                                           Class<L> elementLocatorClass) {
        this.driver = validate().withMessage("Cannot use a null patient driver.")
                                .that(driver)
                                .isNotNull();
        this.elementLocatorClass = validate().withMessage("Cannot use a null element locator class.")
                                             .that(elementLocatorClass)
                                             .isNotNull();
    }

    /**
     * @param searchContext the search context for the element to be located within.
     * @param fields        the list of fields, in order, in the hierarchy to the given
     *                      element from the outermost page object. The element field that
     *                      will be initialized will be the last element of the list.
     *                      May not be null or empty.
     *
     * @return an element locator built with the given values. May return null if
     * the element shouldn't be initialized.
     *
     * @throws IllegalArgumentException if searchContext or fields are null or if fields is empty.
     */
    protected abstract L buildElementLocator(FindsElements<D, W, C, P, B, L, E> searchContext,
                                             List<Field> fields);

    private Object getFieldValue(Field field, Object objectInstance) {
        validate().withMessage("Cannot use a null field.")
                  .that(field)
                  .isNotNull();
        validate().withMessage("Cannot use a null object instance.")
                  .that(objectInstance)
                  .isNotNull();
        try {
            field.setAccessible(true);
            return field.get(objectInstance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Error getting the value for the field %s with the object %s",
                                                     field.getName(),
                                                     objectInstance));
        }
    }

    private void setFieldValue(Field field,
                               Object objectInstance,
                               Object value) {
        validate().withMessage("Cannot use a null field.")
                  .that(field)
                  .isNotNull();
        validate().withMessage("Cannot use a null object instance.")
                  .that(objectInstance)
                  .isNotNull();
        try {
            field.setAccessible(true);
            field.set(objectInstance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Error setting the value of the field %s",
                                                     field.getName()),
                                       e);
        }
    }

    private void setFieldValue(Class<?> declaringClass,
                               String fieldName,
                               Object objectInstance,
                               Object value) {
        validate().withMessage("Cannot use a null declaring class.")
                  .that(declaringClass)
                  .isNotNull();
        validate().withMessage("Cannot use a null or empty field name.")
                  .that(fieldName)
                  .isNotEmpty();
        validate().withMessage("Cannot use a null object instance.")
                  .that(objectInstance)
                  .isNotNull();
        try {
            Field field = declaringClass.getDeclaredField(fieldName);
            setFieldValue(field, objectInstance, value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(String.format("The class %s does not have a field named %s",
                                                     declaringClass.getSimpleName(),
                                                     fieldName));
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeField(FindsElements<D, W, C, P, B, L, E> searchContext,
                                 AbstractPsPageObject<D, W, C, P, B, L, E> pageObject,
                                 List<Field> parentFields,
                                 Field field,
                                 List<AbstractPsPageObject<D, W, C, P, B, L, E>> initializedPageObjects) {
        validate().withMessage("Cannot use a null search context.")
                  .that(searchContext)
                  .isNotNull();
        validate().withMessage("Cannot use a null page object.")
                  .that(pageObject)
                  .isNotNull();
        validate().withMessage("Cannot use a null parent fields list.")
                  .that(parentFields)
                  .isNotNull();
        validate().withMessage("Cannot use a null field.")
                  .that(field)
                  .isNotNull();
        // Check the type of the field
        field.setAccessible(true);
        Object currentValue = getFieldValue(field, pageObject);
        if (AbstractPsPageObject.class.isAssignableFrom(field.getType())) {
            // It's a page object field, only initialize non-null fields
            if (null != currentValue) {
                List<Field> fields = new ArrayList<>(parentFields);
                fields.add(field);
                initializePage(searchContext,
                               (AbstractPsPageObject<D, W, C, P, B, L, E>) currentValue,
                               fields,
                               initializedPageObjects);
            }
        } else if (elementLocatorClass.equals(field.getType())) {
            // It's an element locator field, only initialize null fields
            if (null == currentValue) {
                List<Field> fields = new ArrayList<>(parentFields);
                fields.add(field);
                setFieldValue(field,
                              pageObject,
                              buildElementLocator(searchContext, fields));
            }
        }
    }

    private void initializePage(FindsElements<D, W, C, P, B, L, E> searchContext,
                                AbstractPsPageObject<D, W, C, P, B, L, E> pageObject,
                                List<Field> parentFields,
                                List<AbstractPsPageObject<D, W, C, P, B, L, E>> initializedPageObjects) {
        validate().withMessage("Cannot use a null search context.")
                  .that(searchContext)
                  .isNotNull();
        validate().withMessage("Cannot use a null page object.")
                  .that(pageObject)
                  .isNotNull();
        validate().withMessage("Cannot use a null parent fields list.")
                  .that(parentFields)
                  .isNotNull();
        validate().withMessage("Cannot use a null initialized page objects list.")
                  .that(initializedPageObjects)
                  .isNotNull();
        // Don't do anything if the given object has already been initialized
        if (initializedPageObjects.stream().noneMatch(o -> pageObject == o)) {
            // Add the page object to the initialized page objects list
            initializedPageObjects.add(pageObject);
            // Initialize the page object fields
            setFieldValue(AbstractPsPageObject.class,
                          "driver",
                          pageObject,
                          driver);
            setFieldValue(AbstractPsPageObject.class,
                          "pageContext",
                          pageObject,
                          searchContext);
            // Check if this is also a widget object
            Class<?> clazz = pageObject.getClass();
            if (AbstractPsWidgetObject.class.isAssignableFrom(clazz)) {
                // Set the widget base element field
                setFieldValue(AbstractPsWidgetObject.class,
                              "baseElement",
                              pageObject,
                              buildElementLocator(searchContext,
                                                  parentFields));
            }
            // Since we want to initialize private fields as well, we need to loop through the
            // inheritance hierarchy while initializing all element locators and nested
            // page objects. We can ignore interfaces since they don't have instance fields.
            while (clazz != Object.class) {
                Arrays.stream(clazz.getDeclaredFields())
                      // We don't want to deal with static fields though
                      .filter(field -> !Modifier.isStatic(field.getModifiers()))
                      .forEach(field -> this.initializeField(searchContext,
                                                             pageObject,
                                                             parentFields,
                                                             field,
                                                             initializedPageObjects));
                clazz = clazz.getSuperclass();
            }
        }
    }

    /**
     * @return the driver used to initialize page objects instances.
     */
    protected final P getDriver() {
        return driver;
    }

    /**
     * @return the class of element locator that will be initialized.
     */
    protected final Class<L> getElementLocatorClass() {
        return elementLocatorClass;
    }

    /**
     * Initialize all null element locators of the type returned by {@link #getElementLocatorClass()}
     * in the given page object and recursively initialize any non-null page objects that are fields of
     * the given page object.
     *
     * @param page the page object to be initialized.
     *             May not be null.
     * @param <T>  the type of the page argument.
     *
     * @throws IllegalArgumentException if page is null.
     */
    public final <T extends AbstractPsPageObject<D, W, C, P, B, L, E>> void initialize(T page) {
        initialize(page, driver);
    }

    /**
     * Same as the {@link #initialize(AbstractPsPageObject)} method except it uses the given
     * {@link FindsElements} value as the outer search context for the page rather than using the
     * given driver.
     *
     * @param page        the page object ot be initialized.
     *                    May not be null.
     * @param pageContext the search context to use to initialize the page.
     *                    May not be null.
     * @param <T>         the type of the page argument.
     *
     * @throws IllegalArgumentException if page or pageContext are null.
     */
    public final <T extends AbstractPsPageObject<D, W, C, P, B, L, E>> void initialize(T page,
                                                                                       FindsElements<D, W, C, P, B, L, E> pageContext) {
        validate().withMessage("Cannot use a null page.")
                  .that(page)
                  .isNotNull();
        validate().withMessage("Cannot use a null page context.")
                  .that(pageContext)
                  .isNotNull();
        initializePage(pageContext,
                       page,
                       Collections.emptyList(),
                       new ArrayList<>());
    }
}
