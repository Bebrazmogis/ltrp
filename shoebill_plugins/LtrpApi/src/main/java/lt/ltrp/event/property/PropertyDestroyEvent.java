package lt.ltrp.event.property;

import lt.ltrp.object.Property;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class PropertyDestroyEvent extends PropertyEvent {

    public PropertyDestroyEvent(Property property) {
        super(property);
    }
}
