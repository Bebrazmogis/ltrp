package lt.ltrp.house.event;

import lt.ltrp.event.property.PropertyEvent;
import lt.ltrp.house.object.House;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.20.
 */
public abstract class HouseEvent extends PropertyEvent {

    private LtrpPlayer player;

    public HouseEvent(House house, LtrpPlayer player) {
        super(house);
        this.player = player;
    }

    public HouseEvent(House house) {
        super(house);
    }

    @Override
    public House getProperty() {
        return (House)super.getProperty();
    }

    public LtrpPlayer getPlayer() {
        return player;
    }
}
