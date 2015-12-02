package lt.ltrp.event.property;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.House;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerExitHouseEvent extends PlayerHouseEvent {

    public PlayerExitHouseEvent(LtrpPlayer player, House property) {
        super(player, property);
    }

}
