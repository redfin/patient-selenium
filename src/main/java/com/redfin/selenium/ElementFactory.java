package com.redfin.selenium;

import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

/**
 * An ElementFactory represents a factory of {@link Element} instances and
 * is an abstraction for getting either a particular element instance or a list
 * of them. It is intended as the type used mainly for page objects. It extends
 * the {@link Element} interface and any {@link Element} methods (or methods from
 * {@link CachingExecutor}) are simply passed to the {@link Element} instance returned from
 * {@link #atIndex(int)} with an argument of {@literal 0}.
 *
 * @param <W> the type of {@link WebElement} wrapped by the element instances this builds.
 * @param <E> the type of {@link Element} instances that this builds.
 */
public interface ElementFactory<W extends WebElement,
                                E extends Element<W>>
         extends Element<W> {

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
    E atIndex(int index);

    /**
     * Return the located elements created by this factory. Note that, unlike the {@link #atIndex(int)} method,
     * this DOES trigger a selenium element lookup to check the size of the returned list so the elements returned
     * are eagerly located.
     *
     * @return the List of located element objects.
     */
    List<E> getAll();
}
