package com.redfin.selenium;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.redfin.validity.Validity.validate;

/**
 * Base class that handles the initialization of page objects. It is intended
 * to be be extended by a concrete type that knows how to build the types of
 * objects requested.
 * <p>
 * Note that this class, like other classes in this library, is not intended for multi threaded use.
 */
public abstract class AbstractPageObjectInitializer {

    private final List<Object> alreadyVisitedObjects = new ArrayList<>();

    /**
     * Initialize the given page object. It will first check all the fields of the object.
     * This includes all the fields on super classes of the object. Any field of that object
     * that is null will be given to the method to see if it needs to build a value. If a
     * value is returned it will save that value to the field. Any non-null field that is
     * a {@link PageObject} type will be handed to the pre-process method
     * if they haven't been visited before. Once the pre-processing is complete, it will
     * initialize the page object as well. This means that if your page object's object graph
     * represents and you initialize the root then the whole tree will be initialized. Any non-null
     * field that has already been visited will be ignored, avoiding infinite loops in the case of
     * a cycle in the object graph.
     *
     * @param page the page object to be initialized.
     *             May not be null.
     *
     * @throws IllegalArgumentException          if page is null.
     * @throws PageObjectInitializationException if there is an error while initializing object.
     */
    public final void initialize(PageObject page) {
        validate().withMessage("Cannot initialize a null page")
                  .that(page)
                  .isNotNull();
        try {
            initializePage(page, new ArrayList<>());
        } catch (PageObjectInitializationException e) {
            throw e;
        } catch (Exception e) {
            throw new PageObjectInitializationException("Caught unexpected exception while initializing the page object", e);
        }
    }

    /**
     * Pre-process the given page object, if necessary. From the root page object being initialized
     * each page object will be handed to this method first for any pre-processing that is necessary before
     * it is itself initialized. The root will be the first object given to this method.
     *
     * @param page       the {@link PageObject} that will be initialized next.
     *                   May not be null.
     * @param fieldsList the list of fields seen from the root of the page object graph to
     *                   the page being initialized next. If the page is the root then this
     *                   will be empty. The list is not modifiable. If this page is not the root
     *                   then the last field in the list will be the field of the parent page in
     *                   the graph that points to the page instance.
     *                   May not be null or empty.
     *
     * @throws IllegalArgumentException if page or fieldsList are null or if fieldsList is empty.
     */
    protected abstract void preProcessPage(PageObject page,
                                           List<Field> fieldsList);

    /**
     * Return an Optional wrapped object to be set as the value for the given field on
     * the object being initialized. An empty optional means don't set anything as
     * the value. The field is the one to be possibly initialized. The parentFields
     * are the list of fields seen in order for the graph leading from the root page
     * to the current field.
     *
     * @param fieldsList the List of {@link Field}s that lead from the root page object
     *                   being initialized to the current field. The field to have the
     *                   return object, if any, set to will be the last field in
     *                   the list. The list is not modifiable.
     *                   May not be null or empty.
     *
     * @return the Optional wrapped object to set as the value of the field on the object
     * currently being initialized.
     *
     * @throws IllegalArgumentException if fieldsList is null or empty.
     */
    protected abstract Optional<Object> getValue(List<Field> fieldsList);

    private void initializePage(PageObject page,
                                List<Field> fieldsList) {
        if (!alreadyVisited(page)) {
            alreadyVisitedObjects.add(page);
            preProcessPage(page, Collections.unmodifiableList(fieldsList));
            for (Field field : getAllDeclaredFields(page)) {
                List<Field> newFields = new ArrayList<>(fieldsList);
                newFields.add(field);
                initializeField(page, newFields);
            }
        }
    }

    private boolean alreadyVisited(Object object) {
        for (Object obj : alreadyVisitedObjects) {
            // We care if we've seen this particular object instance, not object equality.
            if (obj == object) {
                return true;
            }
        }
        return false;
    }

    private Set<Field> getAllDeclaredFields(Object object) {
        Set<Field> declaredFields = new HashSet<>();
        Class<?> clazz = object.getClass();
        do {
            declaredFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        } while (null != clazz && !clazz.equals(Object.class));
        return declaredFields;
    }

    /*
     * Note: the fieldsList should never be empty. The field to be initialized should
     * be the last element in the list.
     */

    private void initializeField(PageObject page,
                                 List<Field> fieldsList) {
        validate().that(page).isNotNull();
        validate().that(fieldsList).isNotEmpty();
        Field field = fieldsList.get(fieldsList.size() - 1);
        field.setAccessible(true);
        Object value;
        try {
            // Check if the field has a value set
            value = field.get(page);
        } catch (IllegalAccessException e) {
            throw new PageObjectInitializationException("Error getting the current value of field: " + field + ", for page object: " + page);
        }
        if (null != value) {
            // Non-null field, check if this is a page object
            if (value instanceof PageObject) {
                // It is a page object, recursively initialize it
                initializePage((PageObject) value, fieldsList);
            }
        } else {
            // A null field, check if we need to set a value to it
            Optional<Object> optionalValue = getValue(fieldsList);
            if (optionalValue.isPresent()) {
                Object newValue = optionalValue.get();
                try {
                    field.set(page, newValue);
                } catch (IllegalAccessException e) {
                    throw new PageObjectInitializationException("Error while setting field: " + field + ", with value: " + newValue, e);
                }
            }
        }
    }
}
