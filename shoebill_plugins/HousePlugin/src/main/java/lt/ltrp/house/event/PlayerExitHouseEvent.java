package lt.ltrp.house.event;

import lt.ltrp.event.property.PlayerExitPropertyEvent;
import lt.ltrp.house.object.House;
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
