package lt.ltrp.player.job.event;

import lt.ltrp.event.player.PlayerEvent;import lt.ltrp.job.object.Faction;
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
