package lt.ltrp.event.property;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Garage;


/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerGarageEvent extends PlayerPropertyEvent {

    public PlayerGarageEvent(LtrpPlayer player, Garage property) {
        super(player, property);
    }

    @Override
    public Garage getProperty() {
        return (Garage)super.getProperty();
    }


}
