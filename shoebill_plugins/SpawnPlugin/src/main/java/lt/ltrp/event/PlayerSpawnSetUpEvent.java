package lt.ltrp.event;


import lt.ltrp.event.player.PlayerEvent;import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2015.11.12.
 */
@Deprecated
public class PlayerSpawnSetUpEvent extends PlayerEvent {

    private LtrpPlayer player;

    public PlayerSpawnSetUpEvent(LtrpPlayer player) {
        super(player);
        this.player = player;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public void setPlayer(LtrpPlayer player) {
        this.player = player;
    }
}
