package lt.ltrp.event.property.house;

import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HouseCreateEvent extends HouseEvent{

    public HouseCreateEvent(House house, LtrpPlayer player) {
        super(house, player);
    }

}
