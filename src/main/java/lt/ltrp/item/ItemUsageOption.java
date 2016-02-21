package lt.ltrp.item;

import java.lang.annotation.*;

/**
 * @author Bebras
 *         2015.11.14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited()
public @interface ItemUsageOption {

    String name();

    /**
     * The lower the number, the higher its order. Consider it a Queue, using decimals, number one is the most important
     * It is a float to allow easy integration of new options without changing the old one order value
     * @return the order value
     */
    float order() default 10f;

    /**
     * The color in which the annotated option <i>should</i> appear(depends on the implementation)
     * @return the text color
     */
    int color() default 0xA9C4E4FF;

}
