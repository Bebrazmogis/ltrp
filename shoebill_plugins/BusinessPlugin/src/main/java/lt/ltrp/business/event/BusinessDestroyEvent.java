package lt.ltrp.business.event;

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
