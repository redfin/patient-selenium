package com.redfin.patient.selenium.internal;

import static com.redfin.validity.Validity.validate;

public final class OptionalExecutor {

    private final boolean executeOther;

    public OptionalExecutor(boolean executeOther) {
        this.executeOther = executeOther;
    }

    public void orElse(Runnable runnable) {
        validate().withMessage("Cannot execute a null runnable.")
                  .that(runnable)
                  .isNotNull();
        if (executeOther) {
            runnable.run();
        }
    }
}
