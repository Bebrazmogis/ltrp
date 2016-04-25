package lt.ltrp.event.property;


import lt.ltrp.object.House;
import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class HouseEvent extends Event {

    private House house;

    public HouseEvent(House h) {
        this.house = h;
    }

    public House getHouse() {
        return house;
    }
}
