package lt.ltrp.business.event;

import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Property;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessEvent extends PropertyEvent {

    private LtrpPlayer player;

    public BusinessEvent(Business property, LtrpPlayer player) {
        super(property);
        this.player = player;
    }

    public BusinessEvent(Property property) {
        super(property);
    }

    @Override
    public Business getProperty() {
        return (Business)super.getProperty();
    }

    public LtrpPlayer getPlayer() {
        return player;
    }
}
