package dev.JustRed23.abcm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configurable {
    /**
     * The name of the config file, if not specified it will be the name of the class in lowercase
     * <br>
     * <b>Note:</b> The file extension (.cfg) will be added automatically
     * @return
     */
    String name() default "";
}
