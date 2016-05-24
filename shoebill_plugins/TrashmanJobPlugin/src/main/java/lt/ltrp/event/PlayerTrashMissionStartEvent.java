package lt.ltrp.event;

import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.impl.PlayerTrashMission;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class PlayerTrashMissionStartEvent extends PlayerEvent {

    private PlayerTrashMission trashMission;

    public PlayerTrashMissionStartEvent(LtrpPlayer player, PlayerTrashMission trashMission) {
        super(player);
        this.trashMission = trashMission;
    }

    public PlayerTrashMission getTrashMission() {
        return trashMission;
    }
}
