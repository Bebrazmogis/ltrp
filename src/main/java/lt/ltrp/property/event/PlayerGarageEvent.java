package lt.ltrp.property.event;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.Garage;

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
