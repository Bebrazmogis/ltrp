package lt.ltrp.event;

import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.impl.PlayerTrashMission;

/**
 * @author Bebras
 *         2016.05.24.
 *
 *         This event is dispatched once a player trash mission is ended
 *         This might happen for one of the following reasons:
 *         <ul>
 *             <li>Player has died</li>
 *             <li>He entered the command to end it</li>
 *             <li>He completed ethe whole mission</li>
 *             <li>He disconnected*</li>
 *         </ul>
 *         <b>* once the player disconnects there's a {@value lt.ltrp.TrashmanJobPlugin#DISCONNECT_MISSION_END_DELAY} delay before this event is called, meaning that if a player reconnects during this time this event will not be dispatched</b>
 */
public class PlayerTrashMissionEndEvent extends PlayerEvent {

    private PlayerTrashMission trashMission;

    public PlayerTrashMissionEndEvent(LtrpPlayer player, PlayerTrashMission trashMission) {
        super(player);
        this.trashMission = trashMission;
    }

    public PlayerTrashMission getTrashMission() {
        return trashMission;
    }
}
