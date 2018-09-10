package com.redfin.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.redfin.validity.Validity.validate;

/**
 * Base class for a page object initializer.
 *
 * @param <D> the type of {@link WebDriver} for this instance.
 * @param <W> the type of {@link WebElement} the for the implementing subclass.
 * @param <C> the concrete type of {@link AbstractPatientConfig} for the implementing subclass.
 * @param <P> the concrete type of {@link AbstractPatientDriver} for the implementing subclass.
 * @param <L> the concrete type of {@link AbstractPatientElementLocator} for the implementing subclass.
 * @param <E> the concrete type of {@link AbstractPatientElement} for the implementing subclass.
 */
public abstract class AbstractPageObjectInitializer<D extends WebDriver,
                                                    W extends WebElement,
                                                    C extends AbstractPatientConfig<W>,
                                                    P extends AbstractPatientDriver<D, W, C, L, E>,
                                                    L extends AbstractPatientElementLocator<W, C, L, E>,
                                                    E extends AbstractPatientElement<W, C, L, E>> {

    private final P driver;
    private final List<Object> visitedObjects = new ArrayList<>();

    /**
     * Create a new {@link AbstractPageObjectInitializer} instance with
     * the given driver.
     *
     * @param driver the {@link AbstractPatientDriver} instance to use as the root
     *               for pages that are initialized.
     *               May not be null.
     *
     * @throws IllegalArgumentException if driver is null.
     */
    public AbstractPageObjectInitializer(P driver) {
        this.driver = validate().that(driver).isNotNull();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Public instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Initialize the given root {@link AbstractBasePageObject} with this {@link AbstractPageObjectInitializer}
     * instance and the given {@link FindsElements} as the element location root.
     *
     * <ol>
     * <li>Find all fields of the page object</li>
     * <li>For each field that is null check if it is an element or an element locator</li>
     * <li>If is it an element or element locator type, create those and set the field's value</li>
     * <li>If the field was not-null check if it is a widget</li>
     * <li>If it is a widget create an element for the field and set that as the base then recursively initialize the widget</li>
     * <li>If the field was not-null and not a widget check if it is a page object</li>
     * <li>If it is a page object then recursively initialize the page</li>
     * </ol>
     * <p>
     * After this method is called, any object graph with the given page as the root will be initialized
     * and ready to use.
     *
     * @param page the {@link AbstractBasePageObject} to initialize.
     *             May not be null.
     *
     * @throws IllegalArgumentException          if page is null.
     * @throws PageObjectInitializationException if there are any issues during the initialization process
     *                                           or if the page has already been initialized.
     */
    public final void initializePage(AbstractBasePageObject<D, W, C, P, L, E> page) {
        validate().withMessage("Cannot initialize a null page object")
                  .that(page)
                  .isNotNull();
        try {
            page.setDriver(driver);
            initializeHelper(page, driver);
        } catch (RuntimeException e) {
            if (e instanceof PageObjectInitializationException) {
                // Simply propagate an exception
                throw e;
            } else {
                throw new PageObjectInitializationException("Unexpected exception caught during initialization", e);
            }
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Protected instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    protected abstract Class<E> getElementClass();

    protected abstract Class<L> getElementLocatorClass();

    protected abstract E buildElement(Field field,
                                      FindsElements<W, C, L, E> findsElements);

    protected abstract L buildElementLocator(Field field,
                                             FindsElements<W, C, L, E> findsElements);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void initializeHelper(Object object,
                                  FindsElements<W, C, L, E> findsElements) {
        // Only continue with the given page object if it hasn't been initialized
        // already. This protects us from an infinite loop in case of a cycle
        // in the object graph.
        if (!alreadyVisited(object)) {
            visitedObjects.add(object);
            // Next get all the declared fields on the page
            Set<Field> fields = getAllDeclaredFields(object);
            for (Field field : fields) {
                // Process each field
                initializeField(object, field, findsElements);
            }
        }
    }

    private boolean alreadyVisited(Object object) {
        for (Object next : visitedObjects) {
            // We specifically want to check if the particular instance has
            // been visited, not object equality in regards to the equals(Object) method.
            if (next == object)
                return true;
        }
        return false;
    }

    private Set<Field> getAllDeclaredFields(Object object) {
        // We want all fields, including private ones, so we
        // need to loop through the inheritance hierarchy up to Object
        Set<Field> fields = new HashSet<>();
        Class<?> clazz = object.getClass();
        while (null != clazz && !Object.class.equals(clazz)) {
            Field[] declaredFields = clazz.getDeclaredFields();
            if (null != declaredFields && declaredFields.length > 0) {
                fields.addAll(Arrays.asList(declaredFields));
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    @SuppressWarnings("unchecked")
    private void initializeField(Object object,
                                 Field field,
                                 FindsElements<W, C, L, E> findsElements) {
        field.setAccessible(true);
        // First get the current value of the field
        Object currentValue;
        try {
            currentValue = field.get(object);
        } catch (IllegalAccessException e) {
            throw new PageObjectInitializationException("Unable to get the current value of the field: " + field, e);
        }
        if (null == currentValue) {
            // Field's value is currently null, check the type of the field
            Optional<Object> builtValue = Optional.empty();
            if (getElementClass().equals(field.getType())) {
                // An element, build it
                builtValue = Optional.of(buildElement(field, findsElements));
            } else if (getElementLocatorClass().equals(field.getType())) {
                // An element locator, build it
                builtValue = Optional.of(buildElementLocator(field, findsElements));
            }
            // Check if an element or element locator was built
            builtValue.ifPresent(newValue -> {
                // An element or element locator was built, save it to the field for the object
                try {
                    field.set(object, newValue);
                } catch (IllegalAccessException e) {
                    throw new PageObjectInitializationException("Unable to set the field: " + field + ", with the value: " + newValue);
                }
            });
        } else if (currentValue instanceof AbstractBaseWidgetObject<?, ?, ?, ?>) {
            // The field is non-null and a widget type, build an element to set as the widget base
            AbstractBaseWidgetObject<W, C, L, E> widget = (AbstractBaseWidgetObject<W, C, L, E>) currentValue;
            E baseElement = buildElement(field, findsElements);
            widget.setWidgetElement(baseElement);
            // Recursively initialize the widget with the base element as the root
            initializeHelper(widget, baseElement);
        } else if (currentValue instanceof AbstractBasePageObject<?, ?, ?, ?, ?, ?>) {
            // The field is non-null and a page object, set the driver for the page
            ((AbstractBasePageObject<?, ?, ?, P, ?, ?>) currentValue).setDriver(driver);
            // Recursively initialize the page with the driver as the root
            initializeHelper(currentValue, driver);
        }
    }
}
