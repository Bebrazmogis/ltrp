package lt.ltrp.event.property.house;

import lt.ltrp.event.property.PlayerExitPropertyEvent;
import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerExitHouseEvent extends PlayerExitPropertyEvent {


    public PlayerExitHouseEvent(House property, LtrpPlayer player) {
        super(property, player);
    }

    @Override
    public House getProperty() {
        return (House)super.getProperty();
    }
}
