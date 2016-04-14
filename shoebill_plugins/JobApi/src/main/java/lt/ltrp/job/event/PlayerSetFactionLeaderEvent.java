package lt.ltrp.job.event;

import lt.ltrp.job.object.Faction;
import lt.ltrp.player.event.PlayerEvent;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class PlayerSetFactionLeaderEvent extends PlayerEvent {

    private Faction faction;

    public PlayerSetFactionLeaderEvent(LtrpPlayer player, Faction faction) {
        super(player);
        this.faction = faction;
    }

    public Faction getFaction() {
        return faction;
    }
}
