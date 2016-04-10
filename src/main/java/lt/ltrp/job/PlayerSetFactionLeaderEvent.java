package lt.ltrp.job;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.event.PlayerEvent;

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
