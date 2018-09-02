package com.redfin.selenium;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.redfin.validity.Validity.validate;

/**
 * Base class that handles the initialization of page objects. It is intended
 * to be be extended by a concrete type that simply knows how to build concrete
 * instances of the type T to be initialized.
 *
 * Note that this class, like other classes in this library, is not intended for multi threaded use.
 *
 * @param <T> the type of field to be initialized.
 */
public abstract class AbstractPageObjectInitializer<T> {

    private final Class<T> typeToInitialize;
    private final List<Object> alreadyVisitedObjects = new ArrayList<>();

    /**
     * Create a new {@link AbstractPageObjectInitializer} instance that will
     * initialize fields that are
     *
     * @param typeToInitialize the Class object of the type of field to be initialized.
     *                         May not be null.
     *
     * @throws IllegalArgumentException if typeToInitialize is null.
     */
    public AbstractPageObjectInitializer(Class<T> typeToInitialize) {
        this.typeToInitialize = validate().that(typeToInitialize).isNotNull();
    }

    /**
     * Initialize the given page object. It will first check all the fields of the object.
     * Any field of the object that is null will have it's type checked. If the type of
     * the field is exactly the type to be initialized, it will set the value of the field
     * to be equal to an instance of that type created via {@link #buildValue(List)}.
     * Any non-null field encountered will have it's value initialized as well so if object
     * is a root of a tree of page objects, the entire tree will be initialized.
     *
     * @param object the object to be initialized.
     *               May not be null.
     *
     * @throws IllegalArgumentException if object is null.
     */
    public final void initialize(Object object) {
        validate().withMessage("Cannot initialize a null object")
                  .that(object)
                  .isNotNull();
        initializeObject(object, new ArrayList<>());
    }

    /**
     * Return a new instance of type T built from the given fields. They list of
     * fields will have, in order, all of the fields that recursively lead from the
     * initial object to be initialized to the current field that is to
     * be initialized. The field that is to be set to the built type will be the last
     * field in the list. The list will never be null or empty.
     *
     * @param fields the List of {@link Field}s that lead
     *
     * @return the built instance of type T from the given field list.
     *
     * @throws IllegalArgumentException if fields is null or empty.
     */
    protected abstract T buildValue(List<Field> fields);

    private boolean alreadyVisited(Object object) {
        for (Object obj : alreadyVisitedObjects) {
            if (obj == object) {
                return true;
            }
        }
        return false;
    }

    private void initializeObject(Object object,
                                  List<Field> fields) {
        if (!alreadyVisited(object)) {
            alreadyVisitedObjects.add(object);
            for (Field field : getAllDeclaredFields(object)) {
                List<Field> newFields = new ArrayList<>(fields);
                newFields.add(field);
                initializeField(field, object, newFields);
            }
        }
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

    private void initializeField(Field field,
                                 Object object,
                                 List<Field> fields) {
        field.setAccessible(true);
        try {
            Object value = field.get(object);
            if (null != value) {
                // Recursively initialize non-null fields
                initializeObject(value, fields);
            } else {
                // Null field, check if it's the type we are initializing
                if (field.getType().equals(typeToInitialize)) {
                    field.set(object, buildValue(fields));
                }
            }
        } catch (IllegalAccessException e) {
            throw new PageObjectInitializationException("Error while initializing page object", e);
        }
    }
}
