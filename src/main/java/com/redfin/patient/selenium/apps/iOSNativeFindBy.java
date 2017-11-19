package com.redfin.patient.selenium.apps;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface iOSNativeFindBy {

    String id() default "";

    String accessibility() default "";

    String uiAutomation() default "";

    String xpath() default "";

    int tryingFor() default 30;
}
