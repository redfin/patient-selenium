package com.redfin.selenium;

import com.redfin.patience.PatientWait;
import com.redfin.patience.exceptions.PatientTimeoutException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.redfin.validity.Validity.validate;

/**
 * Base class for a patient element locator.
 *
 * @param <W>    the type of {@link WebElement} the wrapped element types locate.
 * @param <C>    the concrete type of {@link AbstractPatientConfig} for the implementing subclass.
 * @param <THIS> the concrete subclass of this element locator.
 * @param <E>    the concrete type of {@link AbstractPatientElement} that this type builds.
 */
public abstract class AbstractPatientElementLocator<W extends WebElement,
                                                    C extends AbstractPatientConfig<W>,
                                                    THIS extends AbstractPatientElementLocator<W, C, THIS, E>,
                                                    E extends AbstractPatientElement<W, C, THIS, E>>
              extends AbstractBaseObject<W, C> {

    private final Supplier<List<W>> elementListSupplier;
    private final PatientWait wait;
    private final Duration timeout;
    private final Predicate<W> filter;
    private final Map<Integer, E> builtElementMap = new HashMap<>();

    /**
     * Create a new {@link AbstractPatientElementLocator} instance.
     *
     * @param config              the {@link C} for this and any elements it builds.
     *                            May not be null.
     * @param description         the String description for this element locator.
     *                            May not be null or empty.
     * @param elementListSupplier the {@link Supplier} of a list of {@link WebElement}s for this
     *                            element locator.
     *                            May not be null.
     * @param wait                the {@link PatientWait} for this element locator.
     *                            May not be null.
     * @param timeout             the {@link Duration} timeout for this element locator.
     *                            May not be null or negative.
     * @param filter              the {@link Predicate} for this element locator.
     *                            May not be null.
     *
     * @throws IllegalArgumentException if any argument is null, if description is empty, or if timeout is negative.
     */
    public AbstractPatientElementLocator(C config,
                                         String description,
                                         Supplier<List<W>> elementListSupplier,
                                         PatientWait wait,
                                         Duration timeout,
                                         Predicate<W> filter) {
        super(config, description);
        this.elementListSupplier = validate().that(elementListSupplier).isNotNull();
        this.wait = validate().that(wait).isNotNull();
        this.timeout = validate().that(timeout).isGreaterThanOrEqualToZero();
        this.filter = validate().that(filter).isNotNull();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Public instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Syntactic sugar for calling {@link #get(int)} with an argument of 0.
     *
     * @return the element instance for this element locator instance at index of 0.
     */
    public final E get() {
        return get(0);
    }

    /**
     * Return the element instance for this element locator at the given index. Repeated calls
     * to this method with the same index will return the same element instance. The first time
     * an element is created to be returned via this method it will be lazily initialized (e.g.
     * no selenium element lookup will have been performed). To find out if an element is actually
     * present or not you can call the {@link AbstractPatientElement#isPresent()} or the
     * {@link AbstractPatientElement#isAbsent(Duration)} methods.
     *
     * @param index the int index of the element to be returned. The first element
     *              located would have an index of 0.
     *              May not be less than 0.
     *
     * @return the {@link E} instance for the given index.
     *
     * @throws IllegalArgumentException if index is negative.
     */
    public final E get(int index) {
        validate().withMessage("Cannot get an element with a negative index")
                  .that(index)
                  .isAtLeast(0);
        return builtElementMap.computeIfAbsent(index, i -> buildElement(getElementDescription(i), () -> this.findElement(i)));
    }

    /**
     * Returns a list of element instances, one for each located base selenium web element.
     * This does perform an actual selenium lookup to find out how many elements need to be returned.
     * Any element that has previously been constructed via {@link #get(int)} will be returned along
     * with any newly constructed instances. All returned elements will have their internal cache
     * initialized or reset to the the newly located elements.
     *
     * @return a list of located elements. May be empty.
     */
    public final List<E> getAll() {
        List<W> foundElements = getListPatiently();
        List<E> builtElements = new ArrayList<>(foundElements.size());
        for (int index = 0; index < foundElements.size(); index++) {
            E element = builtElementMap.computeIfAbsent(index, i -> buildElement(getElementDescription(i), () -> findElement(i)));
            element.setCachedElement(foundElements.get(index));
            builtElements.add(element);
        }
        return builtElements;
    }

    /**
     * Create and return a new element locator instance with the given wait and the other values copied
     * from the current instance. Note that the new element locator will have a completely reset cache of
     * constructed elements so if you had previously received a {@link E} via the {@link #get(int)} method
     * the returned element locator from this method will not have a link to that previously created element
     * instance and would return a new element for the same index.
     *
     * @param wait the {@link PatientWait} for the new instance.
     *             May not be null.
     *
     * @return a new element locator instance.
     *
     * @throws IllegalArgumentException if wait is null.
     */
    public final THIS clone(PatientWait wait) {
        validate().withMessage("Cannot clone with a null wait")
                  .that(wait)
                  .isNotNull();
        return clone(wait, getTimeout(), getFilter());
    }

    /**
     * Create and return a new element locator instance with the given timeout and the other values copied
     * from the current instance. Note that the new element locator will have a completely reset cache of
     * constructed elements so if you had previously received a {@link E} via the {@link #get(int)} method
     * the returned element locator from this method will not have a link to that previously created element
     * instance and would return a new element for the same index.
     *
     * @param timeout the {@link Duration} for the new instance.
     *                May not be null or negative.
     *
     * @return a new element locator instance.
     *
     * @throws IllegalArgumentException if timeout is null or negative.
     */
    public final THIS clone(Duration timeout) {
        validate().withMessage("Cannot clone with a null or negative timeout")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        return clone(getWait(), timeout, getFilter());
    }

    /**
     * Create and return a new element locator instance with the given filter and the other values copied
     * from the current instance. Note that the new element locator will have a completely reset cache of
     * constructed elements so if you had previously received a {@link E} via the {@link #get(int)} method
     * the returned element locator from this method will not have a link to that previously created element
     * instance and would return a new element for the same index.
     *
     * @param filter the {@link Predicate} filter for the new instance.
     *               May not be null.
     *
     * @return a new element locator instance.
     *
     * @throws IllegalArgumentException if filter is null.
     */
    public final THIS clone(Predicate<W> filter) {
        validate().withMessage("Cannot clone with a null filter")
                  .that(filter)
                  .isNotNull();
        return clone(getWait(), getTimeout(), filter);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Protected instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @return the {@link Supplier} of a list of elements for this instance.
     */
    protected final Supplier<List<W>> getElementListSupplier() {
        return elementListSupplier;
    }

    /**
     * @return the {@link Predicate} for located elements for this instance.
     */
    protected final Predicate<W> getFilter() {
        return filter;
    }

    /**
     * @return the {@link PatientWait} for this instance.
     */
    protected final PatientWait getWait() {
        return wait;
    }

    /**
     * @return the {@link Duration} for this instance.
     */
    protected final Duration getTimeout() {
        return timeout;
    }

    /**
     * Create a new instance of this type with copies of the currrent values (e.g.
     * the config and element list supplier and description) and with the given
     * argument values.
     *
     * @param wait    the {@link PatientWait} for the new instance.
     *                Will never be null.
     * @param timeout the {@link Duration} for the new instance.
     *                Will never be null or negative.
     * @param filter  the {@link Predicate} for the new instance.
     *                Will never be null.
     *
     * @return the newly created element locator instance.
     */
    protected abstract THIS clone(PatientWait wait,
                                  Duration timeout,
                                  Predicate<W> filter);

    /**
     * @param index the int index of the element to be built.
     *              Will never be negative.
     *
     * @return the String description of a built element for the current element locator
     * and the given index.
     */
    protected abstract String getElementDescription(int index);

    /**
     * @param elementDescription the String description of the element to be built.
     *                           Will never be null or empty.
     * @param elementSupplier    the {@link Supplier} of elements for the given element.
     *                           Will never be null.
     *
     * @return an element for the given description and element supplier.
     */
    protected abstract E buildElement(String elementDescription,
                                      Supplier<Optional<W>> elementSupplier);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /*
     * Simply use the given supplier and filter to find the
     * n-th element and return it or an empty optional if none.
     * There is no waiting involved in this lookup as the
     * waiting will be done by the caller of the method if
     * an empty optional is returned. Unhandled and non-ignored
     * exceptions will be thrown.
     */

    private Optional<W> findElement(int index) {
        try {
            List<W> foundElements = elementListSupplier.get()
                                                       .stream()
                                                       .filter(filter)
                                                       .limit(index + 1)
                                                       .collect(Collectors.toList());
            if (foundElements.size() > index) {
                return Optional.of(foundElements.get(index));
            }
        } catch (RuntimeException e) {
            if (!getConfig().isIgnoredLookupException(e.getClass())) {
                throw e;
            }
        }
        return Optional.empty();
    }

    /*
     * Use the given supplier and filter to find the list
     * of matching elements. This uses the set wait and
     * timeout to wait patiently for results. If a non-empty
     * list is found simply return it. If the timeout is
     * reached before a non-empty list is found then
     * return an empty list. Unhandled and non-ignored
     * exceptions will be thrown.
     */

    private List<W> getListPatiently() {
        try {
            return wait.from(() -> {
                try {
                    return elementListSupplier.get();
                } catch (RuntimeException e) {
                    if (getConfig().isIgnoredLookupException(e.getClass())) {
                        return null;
                    }
                    throw e;
                }
            }).withFilter(list -> !list.isEmpty()).get(timeout);
        } catch (PatientTimeoutException ignore) {
            return Collections.emptyList();
        }
    }
}
