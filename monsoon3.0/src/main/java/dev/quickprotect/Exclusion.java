package dev.quickprotect;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static dev.quickprotect.Strategy.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Retention(CLASS)
@Target({ TYPE, FIELD, METHOD, CONSTRUCTOR })
public @interface Exclusion {

    public Strategy[] value() default { NO_STRATEGY };

}