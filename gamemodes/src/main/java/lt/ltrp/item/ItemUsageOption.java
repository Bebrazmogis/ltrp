package lt.ltrp.item;

import java.lang.annotation.*;

/**
 * @author Bebras
 *         2015.11.14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface ItemUsageOption {

    String name() default "";

}
