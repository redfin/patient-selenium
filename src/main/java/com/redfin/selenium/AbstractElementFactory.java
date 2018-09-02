package com.redfin.selenium;

import com.redfin.patience.PatientWait;
import com.redfin.patience.exceptions.PatientTimeoutException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.redfin.validity.Validity.validate;

/**
 * Base class for an element factory. This is the type that is intended to be
 * declared as fields on a page object and initialized by an instance of
 * {@link AbstractPageObjectInitializer}. It will produce instances of an
 * {@link AbstractElement} of type E as requested.
 *
 * Note that this class, like other classes in this library, is not intended for multi threaded use.
 *
 * @param <W> the type of {@link WebElement} for the element factory.
 * @param <E> the type of {@link AbstractElement} that this factory produces.
 */
public abstract class AbstractElementFactory<W extends WebElement,
                                             E extends AbstractElement<W>> {

    private static final String ELEMENT_DESCRIPTION_FORMAT = "%s.atIndex(%d)";

    private final String description;
    private final PatientWait wait;
    private final Predicate<W> filter;
    private final Duration timeout;
    private final Supplier<List<W>> elementListSupplier;
    private final Map<Integer, E> elementMap = new HashMap<>();

    /**
     * Create a new AbstractElementFactory instance with the given values.
     *
     * @param description         the String description for the element factory and
     *                            used to build the descriptions for created elements.
     *                            May not be null or empty.
     * @param wait                the {@link PatientWait} used to wait for selenium {@link WebElement}s
     *                            that match the given predicate.
     *                            May not be null.
     * @param filter              the {@link Predicate} filter used when locating selenium elements.
     *                            May not be null.
     * @param timeout             the {@link Duration} to wait for elements with the given wait.
     *                            A duration of zero means to only try once when locating an element.
     *                            May not be null or negative.
     * @param elementListSupplier the {@link Supplier} that returns a list of {@link WebElement} instances
     *                            of type W. Should never return a null list. Each call to {@link Supplier#get()}
     *                            should perform a new selenium element location attempt.
     *                            May not be null.
     *
     * @throws IllegalArgumentException if any argument is null, if timeout is negative, or if description is empty.
     */
    public AbstractElementFactory(String description,
                                  PatientWait wait,
                                  Predicate<W> filter,
                                  Duration timeout,
                                  Supplier<List<W>> elementListSupplier) {
        this.description = validate().that(description).isNotEmpty();
        this.wait = validate().that(wait).isNotNull();
        this.filter = validate().that(filter).isNotNull();
        this.timeout = validate().that(timeout).isGreaterThanOrEqualToZero();
        this.elementListSupplier = validate().that(elementListSupplier).isNotNull();
    }

    /**
     * Return the lazily located element created by this factory for the given index. Note that
     * this is NOT a selenium element terminating method so this does not actually
     * check that the element is on the current page. Use the {@link AbstractElement#isPresent()}
     * or {@link AbstractElement#isAbsent(Duration)} methods to check if they actually exist.
     *
     * @param index the element for the given index (note this is 0-based).
     *              May not be negative.
     *
     * @return the element for the given index.
     */
    public final E atIndex(int index) {
        validate().withMessage("Cannot find an element at a negative index")
                  .that(index)
                  .isAtLeast(0);
        return elementMap.computeIfAbsent(index,
                                          i -> buildElement(String.format(ELEMENT_DESCRIPTION_FORMAT, description, i),
                                                            nthElementAbsentFunction(i),
                                                            findNthElement(i),
                                                            null));
    }

    /**
     * Return the located elements created by this factory. Note that, unlike the {@link #atIndex(int)} method,
     * this DOES trigger a selenium element lookup to check the size of the returned list so the elements returned
     * are eagerly located.
     *
     * @return the List of located element objects.
     */
    public final List<E> getAll() {
        List<W> foundElements = findAllElements();
        List<E> builtElements = new ArrayList<>(foundElements.size());
        for (int i = 0; i < foundElements.size(); i++) {
            builtElements.add(elementMap.computeIfAbsent(i,
                                                         index -> buildElement(String.format(ELEMENT_DESCRIPTION_FORMAT, description, index),
                                                                               nthElementAbsentFunction(index),
                                                                               findNthElement(index),
                                                                               foundElements.get(index))));
        }
        return builtElements;
    }

    @Override
    public String toString() {
        return description;
    }

    /**
     * Abstract method to be implemented by concrete subclass instances. This will actually
     * create the desired concrete element types for this factory.
     *
     * @param elementDescription the String description for the built element.
     *                           May not be null or empty.
     * @param notPresentFunction the {@link Function} that takes in a {@link Duration} and returns true if the
     *                           element is not present or false  if it is.
     *                           May not be null.
     * @param elementSupplier    the {@link Supplier} that returns an {@link Optional} instance that wraps the
     *                           located selenium element, if it is present or an empty optional if it is not.
     * @param initialElement     the already located selenium element if it has been located.
     *                           It may be null if it hasn't been located yet.
     *
     * @return the built concrete element instance for the given values.
     *
     * @throws IllegalArgumentException if any element other than initialElement is null or if elementDescription is empty.
     */
    protected abstract E buildElement(String elementDescription,
                                      Function<Duration, Boolean> notPresentFunction,
                                      Supplier<Optional<W>> elementSupplier,
                                      W initialElement);

    /**
     * @return the description for this element factory.
     */
    protected final String getDescription() {
        return description;
    }

    /**
     * @return the {@link PatientWait} for this element factory.
     */
    protected final PatientWait getWait() {
        return wait;
    }

    /**
     * @return the {@link Predicate} used to filter selenium elements for this
     * element factory.
     */
    protected final Predicate<W> getFilter() {
        return filter;
    }

    /**
     * @return the {@link Duration} timeout used for when locating selenium
     * elements for this element factory.
     */
    protected final Duration getTimeout() {
        return timeout;
    }

    /**
     * @return the {@link Supplier} of a list of selenium elements for this
     * element factory.
     */
    protected final Supplier<List<W>> getElementListSupplier() {
        return elementListSupplier;
    }

    private List<W> findAllElements() {
        try {
            return wait.from(() -> elementListSupplier.get()
                                                      .stream()
                                                      .filter(filter)
                                                      .collect(Collectors.toList()))
                       .withFilter(list -> !list.isEmpty())
                       .get(timeout);
        } catch (PatientTimeoutException ignore) {
            return new ArrayList<>();
        }
    }

    private Supplier<Optional<W>> findNthElement(int index) {
        return () -> {
            try {
                return Optional.of(wait.from(() -> {
                    List<W> matchingElements = elementListSupplier.get()
                                                                  .stream()
                                                                  .filter(filter)
                                                                  .limit(index + 1)
                                                                  .collect(Collectors.toList());
                    if (matchingElements.size() > index) {
                        return matchingElements.get(index);
                    } else {
                        return null;
                    }
                }).get(timeout));
            } catch (PatientTimeoutException ignore) {
                return Optional.empty();
            }
        };
    }

    private Function<Duration, Boolean> nthElementAbsentFunction(int index) {
        return notPresentTimeout -> {
            try {
                wait.from(() -> {
                    List<W> matchingElements = elementListSupplier.get()
                                                                  .stream()
                                                                  .filter(filter)
                                                                  .limit(index + 1)
                                                                  .collect(Collectors.toList());
                    return matchingElements.size() <= index;
                }).get(notPresentTimeout);
                return true;
            } catch (PatientTimeoutException ignore) {
                return false;
            }
        };
    }
}
