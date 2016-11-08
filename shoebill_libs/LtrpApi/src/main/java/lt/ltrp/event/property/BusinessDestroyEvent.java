package lt.ltrp.event.property;

import lt.ltrp.object.Property;

/**
 * @author Bebras
 *         2016.04.27.
 */
public class BusinessDestroyEvent extends BusinessEvent {


    public BusinessDestroyEvent(Property property) {
        super(property);
    }
}
