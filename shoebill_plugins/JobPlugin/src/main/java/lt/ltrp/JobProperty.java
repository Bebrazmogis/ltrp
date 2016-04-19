package lt.ltrp;

import java.lang.annotation.*;

/**
 * @author Bebras
 *         2016.03.01.
 */
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface JobProperty {
    String value() default "";
}
