package lt.ltrp.event.property.house;

import lt.ltrp.object.House;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class HouseUpdateEvent extends HouseEvent {

    public HouseUpdateEvent(House house) {
        super(house);
    }
}
