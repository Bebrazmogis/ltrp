package lt.ltrp.house.event;

import lt.ltrp.house.object.House;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HouseEditEvent extends HouseEvent {


    public HouseEditEvent(House house, LtrpPlayer player) {
        super(house, player);
    }
}
