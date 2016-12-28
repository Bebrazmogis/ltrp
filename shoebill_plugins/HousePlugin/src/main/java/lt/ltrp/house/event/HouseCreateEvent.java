package lt.ltrp.house.event;

import lt.ltrp.house.object.House;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HouseCreateEvent extends HouseEvent{

    public HouseCreateEvent(House house, LtrpPlayer player) {
        super(house, player);
    }

    public HouseCreateEvent(House house) {
        this(house, null);
    }
}
