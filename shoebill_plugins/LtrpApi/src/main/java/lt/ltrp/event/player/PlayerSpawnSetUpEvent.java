package lt.ltrp.event.player;


import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2015.11.12.
 */
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
