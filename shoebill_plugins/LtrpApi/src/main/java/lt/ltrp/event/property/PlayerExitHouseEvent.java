package lt.ltrp.event.property;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.House;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerExitHouseEvent extends PlayerHouseEvent {

    public PlayerExitHouseEvent(LtrpPlayer player, House property) {
        super(player, property);
    }

}
