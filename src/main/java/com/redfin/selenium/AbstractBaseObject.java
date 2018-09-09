package com.redfin.selenium;

import org.openqa.selenium.WebElement;

import static com.redfin.validity.Validity.validate;

/**
 * Base class for many of the patient selenium objects that contains a
 * configuration object for shared default values and a string description.
 *
 * @param <W> the type of {@link WebElement} for the implementing subclass.
 * @param <C> the concrete type of {@link AbstractPatientConfig} for the implementing subclass.
 */
public abstract class AbstractBaseObject<W extends WebElement,
                                         C extends AbstractPatientConfig<W>> {

    private final C config;
    private final String description;

    /**
     * Create a new {@link AbstractBaseObject} instance.
     *
     * @param config      the {@link C} config for the subclass.
     *                    May not be null.
     * @param description the String description for this instance.
     *                    May not be null or empty.
     *
     * @throws IllegalArgumentException if either argument is null or if description is empty.
     */
    public AbstractBaseObject(C config,
                              String description) {
        this.config = validate().that(config).isNotNull();
        this.description = validate().that(description).isNotEmpty();
    }

    /**
     * @return the {@link C} configuration object for this instance.
     */
    protected final C getConfig() {
        return config;
    }

    /**
     * @return the String description for this instance.
     */
    protected final String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
