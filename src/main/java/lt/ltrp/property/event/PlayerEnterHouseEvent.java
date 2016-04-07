package lt.ltrp.property.event;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.House;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerEnterHouseEvent extends PlayerHouseEvent {

    public PlayerEnterHouseEvent(LtrpPlayer player, House property) {
        super(player, property);
    }

}
