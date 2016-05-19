package lt.ltrp.event.property.house;

import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HouseEditEvent extends HouseEvent {


    public HouseEditEvent(House house, LtrpPlayer player) {
        super(house, player);
    }
}
