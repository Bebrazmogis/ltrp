package lt.ltrp.house.event;

import lt.ltrp.house.object.House;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class HouseUpdateEvent extends HouseEvent {

    public HouseUpdateEvent(House house) {
        super(house);
    }
}
