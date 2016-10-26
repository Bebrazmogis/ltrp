package lt.ltrp.constant;

import java.lang.annotation.*;

/**
 * @author Bebras
 *         2016.03.01.
 *
 * Fields annotated with this annotation will be loaded by the DAO class
 * The value of this annotation will be used as a key name or prefix in the database
 * Available field types are
 * <ul>
 *     <li>int</li>
 *     <li>float</li>
 *     <li>[net.gtaun.shoebill.data.Location]</li>
 *     <li>String</li>
 * </ul>
 *
 * For complex objects, the specified name will be used as a prefix.
 * For example to load a Location object the following properties must be set in storage:
 * <ul>
 *     <li>field_name_x</li>
 *     <li>field_name_y</li>
 *     <li>field_name_z</li>
 *     <li>(optional)field_name_interior</li>
 *     <li>(optional)field_name_world</li>
 * </ul>
 */
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface JobProperty {
    String value();
}
