package lt.ltrp.item;

import java.lang.annotation.*;

/**
 * @author Bebras
 *         2015.11.14.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ItemUsageOption {

    String name() default "";

}
