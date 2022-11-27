package dev.JustRed23.abcm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigField {
    /**
     * The default value of the field if no value is found in the config file.
     */
    String defaultValue();

    /**
     * If a field is marked as optional, it is not required to be present in the config file.
     * If it is not present, the default value will be used.
     */
    boolean optional() default false;
}
