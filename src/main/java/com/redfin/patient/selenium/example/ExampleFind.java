package com.redfin.patient.selenium.example;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Repeatable(ExampleFinds.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExampleFind {

    String id() default "";

    String className() default "";

    String name() default "";

    String tagName() default "";

    String css() default "";

    String xpath() default "";

    String linkText() default "";

    String partialLinkText() default "";
}
