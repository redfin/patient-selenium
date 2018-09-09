package com.redfin.selenium;

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
 */
public abstract class AbstractPageObjectInitializer<W extends WebElement,
        L extends AbstractPatientElementLocator<W, L, E>,
        E extends AbstractPatientElement<W, L, E>> {

    private final List<PageObject> visitedObjects = new ArrayList<>();

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Public instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Initialize the given root {@link PageObject} with this {@link AbstractPageObjectInitializer}
     * instance and the given {@link FindsElements} as the element location root.
     *
     * <ol>
     * <li>Pre-process the page object</li>
     * <li>Find all fields of the page object</li>
     * <li>For each field that is null currently check if we need to set a value to it</li>
     * <li>If a value needs to be set do so</li>
     * <li>If the field is now not-null (or was previously) check if the field's type is a {@link PageObject}</li>
     * <li>If the field is a page object, recursively initialize it as well</li>
     * </ol>
     * <p>
     * After this method is called, any object graph with the given page as the root will be initialized
     * and ready to use.
     *
     * @param page          the {@link PageObject} to initialize.
     *                      May not be null.
     * @param findsElements the {@link FindsElements} to use as the root of element location.
     *                      May not be null.
     *
     * @throws IllegalArgumentException          if page or findsElements are null.
     * @throws PageObjectInitializationException if there are any issues during the initialization process.
     */
    public final void initializePage(PageObject page,
                                     FindsElements<W, L, E> findsElements) {
        validate().withMessage("Cannot initialize a null page object")
                  .that(page)
                  .isNotNull();
        validate().withMessage("Cannot initialize with a null finds elements")
                  .that(findsElements)
                  .isNotNull();
        try {
            initializePageHelper(page, null, findsElements);
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
     * Pre-process the page object.
     *
     * @param page          the {@link PageObject} to be pre-processed.
     *                      Will never be null.
     * @param field         the {@link Field} that declared the current page.
     *                      Will be null if the page is the root.
     * @param findsElements the {@link FindsElements} for the current initialization.
     *                      Will never be null.
     *
     * @throws PageObjectInitializationException if there are any exception states during the method.
     */
    protected abstract void preProcessPage(PageObject page,
                                           Field field,
                                           FindsElements<W, L, E> findsElements);

    /**
     * Return an optional with the value to set for the given field on the given
     * page object or an empty optional if no value should be set for the field.
     *
     * @param page          the {@link PageObject} currently being initialized.
     *                      Will never be null.
     * @param field         the {@link Field} for the page object that could have a value
     *                      set for it. Will never be null, will never have a non-null value
     *                      for the field and the given page.
     * @param findsElements the {@link FindsElements} used for initializing the current field.
     *                      Will never be null.
     *
     * @return an optional wrapped value or an empty optional. Should never return null.
     *
     * @throws PageObjectInitializationException if there are any exception states during the method.
     */
    protected abstract Optional<Object> getValueForField(PageObject page,
                                                         Field field,
                                                         FindsElements<W, L, E> findsElements);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void initializePageHelper(PageObject page,
                                      Field fieldOfPage,
                                      FindsElements<W, L, E> findsElements) {
        // Only continue with the given page object if it hasn't been initialized
        // already. This protects us from an infinite loop in case of a cycle
        // in the object graph.
        if (!alreadyVisited(page)) {
            visitedObjects.add(page);
            // Pre-process the page
            preProcessPage(page, fieldOfPage, findsElements);
            // Next get all the declared fields on the page
            Set<Field> fields = getAllDeclaredFields(page);
            for (Field field : fields) {
                // Process each field
                initializeField(page, field, findsElements);
            }
        }
    }

    private boolean alreadyVisited(PageObject page) {
        for (PageObject object : visitedObjects) {
            // We specifically want to check if the particular instance has
            // been visited, not object equality in regards to the equals(Object) method.
            if (object == page)
                return true;
        }
        return false;
    }

    private Set<Field> getAllDeclaredFields(PageObject page) {
        // We want all fields, including private ones, so we
        // need to loop through the inheritance hierarchy up to Object
        Set<Field> fields = new HashSet<>();
        Class<?> clazz = page.getClass();
        while (null != clazz && !Object.class.equals(clazz)) {
            Field[] declaredFields = clazz.getDeclaredFields();
            if (null != declaredFields && declaredFields.length > 0) {
                fields.addAll(Arrays.asList(declaredFields));
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private void initializeField(PageObject page,
                                 Field field,
                                 FindsElements<W, L, E> findsElements) {
        field.setAccessible(true);
        // First get the current value of the field
        Object currentValue;
        try {
            currentValue = field.get(page);
        } catch (IllegalAccessException e) {
            throw new PageObjectInitializationException("Unable to get the current value of the field: " + field, e);
        }
        if (null == currentValue) {
            // Field's value is currently null, see if we need to set a value for the field
            Optional<Object> newValue = getValueForField(page, field, findsElements);
            if (newValue.isPresent()) {
                // There is a value to be set, do so, and update our reference
                try {
                    field.set(page, newValue.get());
                } catch (IllegalAccessException e) {
                    throw new PageObjectInitializationException("Unable to set the field: " + field + ", with the value: " + newValue.get());
                }
                currentValue = newValue.get();
            }
        }
        // Check if a non-null current value is a page object instance
        if (currentValue instanceof PageObject) {
            // The field's value is a page object, recursively initialize that as well
            initializePageHelper((PageObject) currentValue, field, findsElements);
        }
    }
}
