package lt.ltrp.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Bebras
 *         2015.11.13.
 *
 *         Annotation used to annotate {@link net.gtaun.shoebill.common.command.Command} parameters for user-friendly display.
 *         For example languages different from general code language or acroynms and such.
 */

@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CommandParam {

    String value() default "";
}
