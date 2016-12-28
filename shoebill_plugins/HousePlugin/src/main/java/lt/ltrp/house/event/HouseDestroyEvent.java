package lt.ltrp.house.event;

import lt.ltrp.house.object.House;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HouseDestroyEvent extends HouseEvent {

    public HouseDestroyEvent(House house) {
        super(house);
    }

}
