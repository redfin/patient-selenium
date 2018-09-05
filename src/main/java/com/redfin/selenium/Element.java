package com.redfin.selenium;

import org.openqa.selenium.WebElement;

import java.time.Duration;

/**
 * An Element represents a wrapped selenium {@link WebElement} and defines
 * the type of patient selenium element that can look itself up, check if
 * it is present without throwing an exception, and has an internal cache
 * to avoid multiple page look ups when not necessary.
 *
 * @param <W> the type of {@link WebElement} that this element wraps.
 */
public interface Element<W extends WebElement>
         extends CachingExecutor<W> {

    /**
     * Note that this method does NOT use the cached value and will clear out any
     * previously cached element.
     *
     * @return true if the selenium element this is representing can be located and
     * false otherwise.
     */
    boolean isPresent();

    /**
     * Wait until either the selenium element that is located by this element is either
     * no longer on the page or the timeout is reached.
     * <p>
     * Note that this method does NOT use the cached value and will clear out any
     * previously cached element.
     *
     * @param timeout the {@link Duration} to wait while waiting for a matching element to no longer be present.
     *                May not be null or negative.
     *
     * @return true if the selenium element this is representing can not be located and
     * false otherwise.
     *
     * @throws IllegalArgumentException if timeout is null or negative.
     */
    boolean isAbsent(Duration timeout);
}
