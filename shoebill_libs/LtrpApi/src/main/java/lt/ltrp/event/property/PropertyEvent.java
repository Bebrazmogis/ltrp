package lt.ltrp.event.property;

import lt.ltrp.object.Property;
import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.04.19.
 */
public abstract class PropertyEvent extends Event{

    private Property property;

    public PropertyEvent(Property property) {
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }
}
