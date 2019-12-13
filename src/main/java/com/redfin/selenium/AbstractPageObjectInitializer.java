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
import java.util.concurrent.atomic.AtomicBoolean;

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
     * instance and the given {@link D} as the root locator for the page.
     *
     * <ol>
     * <li>Find all fields of the widget object</li>
     * <li>For each field that is null check if it is an element, an element locator, or a widget</li>
     * <li>If it is one of those types create the object and set it as the value for the field.</li>
     * <li>If it was a widget, then recursively initialize the widget.</li>
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
     *             May not be null and may not have been already initialized.
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

    /**
     * Initialize the given root {@link AbstractBaseWidgetObject} with this {@link AbstractPageObjectInitializer}
     * instance and the given widgetElement as the wrapping root element. The element should have been located
     * via the same {@link D} driver root that was used to instantiate this page object initializer.
     *
     * <ol>
     * <li>Find all fields of the widget object</li>
     * <li>For each field that is null check if it is an element, an element locator, or a widget</li>
     * <li>If it is one of those types create the object and set it as the value for the field.</li>
     * <li>If it was a widget, then recursively initialize the widget.</li>
     * <li>If the field was not-null check if it is a widget</li>
     * <li>If it is a widget create an element for the field and set that as the base then recursively initialize the widget</li>
     * <li>If the field was not-null and not a widget check if it is a page object</li>
     * <li>If it is a page object then recursively initialize the page</li>
     * </ol>
     * <p>
     * After this method is called, any object graph with the given page as the root will be initialized
     * and ready to use.
     *
     * @param widget        the {@link AbstractBaseWidgetObject} to initialize.
     *                      May not be null and may not have already been initialized.
     * @param widgetElement the {@link E} element that is the base element of the widget.
     *                      May not be null.
     *
     * @throws IllegalArgumentException          if widget or widgetElement is null.
     * @throws PageObjectInitializationException if there are any issues during the initialization process
     *                                           or if the widget has already been initialized.
     */
    public final void initializeWidget(AbstractBaseWidgetObject<W, C, L, E> widget,
                                       E widgetElement) {
        validate().withMessage("Cannot initialize a null widget object")
                  .that(widget)
                  .isNotNull();
        validate().withMessage("Cannot initialize a widget with a null element.")
                  .that(widgetElement).isNotNull();
        try {
            widget.setWidgetElement(widgetElement);
            initializeHelper(widget, widgetElement);
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

    /**
     * @return the actual element type for the implementing sub-class.
     */
    protected abstract Class<E> getElementClass();

    /**
     * @return the actual element locator type for the implementing sub-class.
     */
    protected abstract Class<L> getElementLocatorClass();

    /**
     * Create an instance of the desired widget type. Should not return null. The
     * base element should not be set yet, the page object initialization will create an
     * element for the base widget with this widget instance and the field declaring
     * the widget. The page object initializer will throw a {@link PageObjectInitializationException}
     * if the implementation of this method returns a null value or if the built
     * widget has a non-null base element.
     *
     * @param widgetClass the Class object of the widget to build.
     *                    May not be null.
     * @param <T>         the type of the widget to be returned.
     *
     * @return an instantiated widget object. Should not return null.
     * The base element of the widget should not be set yet.
     *
     * @throws IllegalArgumentException if widgetClass is null.
     */
    protected abstract <T extends AbstractBaseWidgetObject<W, C, L, E>> T buildWidget(Class<T> widgetClass);

    /**
     * Create an instance of the desired element. The page object initializer will throw
     * a {@link PageObjectInitializationException} if the implementation of this method
     * returns a null value.
     *
     * @param field         the Field declaring the element.
     *                      May not be null.
     * @param findsElements the parent {@link FindsElements} to use to locate the desired element.
     *                      May not be null.
     *
     * @return an instance of the element type declared by the given field.
     *
     * @throws IllegalArgumentException if field or findsElements is null.
     */
    protected abstract E buildElement(Field field,
                                      FindsElements<W, C, L, E> findsElements);

    /**
     * Create an instance of the desired element locator. The page object initializer will throw
     * a {@link PageObjectInitializationException} if the implementation of this method
     * returns a null value.
     *
     * @param field         the Field declaring the element locator.
     *                      May not be null.
     * @param findsElements the parent {@link FindsElements} to use to locate the desired element.
     *                      May not be null.
     *
     * @return an instance of the element locator type declared by the given field.
     *
     * @throws IllegalArgumentException if field or findsElements is null.
     */
    protected abstract L buildElementLocator(Field field,
                                             FindsElements<W, C, L, E> findsElements);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void initializeHelper(Object object,
                                  FindsElements<W, C, L, E> findsElements) {
        // Only continue with the given object if it hasn't been initialized
        // already. This protects us from an infinite loop in case of a cycle
        // in the object graph.
        if (!alreadyVisited(object)) {
            visitedObjects.add(object);
            // Next get all the declared fields on the object
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
            if (declaredFields.length > 0) {
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
            Optional<?> builtValue = Optional.empty();
            AtomicBoolean isWidget = new AtomicBoolean(false);
            String buildMethodNameString = null;
            if (getElementClass().equals(field.getType())) {
                // An element, build it
                builtValue = Optional.ofNullable(buildElement(field, findsElements));
                buildMethodNameString = "buildElement";
            } else if (getElementLocatorClass().equals(field.getType())) {
                // An element locator, build it
                builtValue = Optional.ofNullable(buildElementLocator(field, findsElements));
                buildMethodNameString = "buildElementLocator";
            } else if (AbstractBaseWidgetObject.class.isAssignableFrom(field.getType())) {
                // A widget, build it and set the boolean so we can re-initialize it
                builtValue = Optional.ofNullable(buildWidget((Class<AbstractBaseWidgetObject<W, C, L, E>>) field.getType()));
                buildMethodNameString = "buildWidget";
                isWidget.set(true);
            }
            // Validate the contents of the optional if a build method String was set
            if (null != buildMethodNameString) {
                if (!builtValue.isPresent()) {
                    throw new PageObjectInitializationException(String.format("Received a null value from the %s method with the field: %s", buildMethodNameString, field));
                }
                if (isWidget.get()) {
                    if (builtValue.get() instanceof AbstractBaseWidgetObject<?, ?, ?, ?>) {
                        AbstractBaseWidgetObject<?, ?, ?, ?> widget = (AbstractBaseWidgetObject<?, ?, ?, ?>) builtValue.get();
                        if (widget.isWidgetElementSet()) {
                            throw new PageObjectInitializationException(String.format("Received a widget from %s with a non-null base element for field: %s", buildMethodNameString, field));
                        }
                    } else {
                        throw new PageObjectInitializationException(String.format("Received a non-widget object from the %s method for field: %s", buildMethodNameString, field));
                    }
                }
            }
            // Check if a value was built
            builtValue.ifPresent(newValue -> {
                // An object was built, save it to the field for the object
                try {
                    field.set(object, newValue);
                } catch (IllegalAccessException e) {
                    throw new PageObjectInitializationException("Unable to set the field: " + field + ", with the value: " + newValue);
                }
                // In the case of a widget that was built, we need to rerun this method with the same values for the
                // now non-null widget
                if (isWidget.get()) {
                    initializeField(object, field, findsElements);
                }
            });
        } else {
            // The current value isn't null, check if it's already been initialized
            if (!alreadyVisited(currentValue)) {
                // It hasn't been initialized, recursively do so if necessary
                if (currentValue instanceof AbstractBaseWidgetObject<?, ?, ?, ?>) {
                    // The field is non-null and a widget type, build an element to set as the widget base
                    AbstractBaseWidgetObject<W, C, L, E> widget = (AbstractBaseWidgetObject<W, C, L, E>) currentValue;
                    E baseElement = buildElement(field, findsElements);
                    widget.setWidgetElement(baseElement);
                    // Recursively initialize the widget with the base element as the root element locator
                    initializeHelper(widget, baseElement);
                } else if (currentValue instanceof AbstractBasePageObject<?, ?, ?, ?, ?, ?>) {
                    // The field is non-null and a page object, set the driver for the page
                    ((AbstractBasePageObject<?, ?, ?, P, ?, ?>) currentValue).setDriver(driver);
                    // Recursively initialize the page with the driver as the root
                    initializeHelper(currentValue, driver);
                }
            }
        }
    }
}
