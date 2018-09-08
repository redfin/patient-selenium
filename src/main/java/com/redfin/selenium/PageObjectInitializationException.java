package com.redfin.selenium;

/**
 * A {@link RuntimeException} that is thrown to signal an error state when
 * performing page object initialization.
 */
public final class PageObjectInitializationException
           extends RuntimeException {

    static final long serialVersionUID = 0L;

    /**
     * Create a new {@link PageObjectInitializationException} with a null
     * message and cause.
     */
    public PageObjectInitializationException() {
        super();
    }

    /**
     * Create a new {@link PageObjectInitializationException} with the given message
     * and a null cause.
     *
     * @param message the String message for the exception.
     *                May be null.
     */
    public PageObjectInitializationException(String message) {
        super(message);
    }

    /**
     * Create a new {@link PageObjectInitializationException} with the given cause
     * and a null message.
     *
     * @param cause the {@link Throwable} cause of the exception.
     *              May be null.
     */
    public PageObjectInitializationException(Throwable cause) {
        super(cause);
    }

    /**
     * Create a new {@link PageObjectInitializationException} with the given
     * message and cause.
     *
     * @param message the String message for the exception.
     *                May be null.
     * @param cause   the {@link Throwable} cause of the exception.
     *                May be null.
     */
    public PageObjectInitializationException(String message,
                                             Throwable cause) {
        super(message, cause);
    }
}