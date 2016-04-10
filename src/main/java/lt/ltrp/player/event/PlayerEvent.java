package lt.ltrp.player.event;

import lt.ltrp.player.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class PlayerEvent extends net.gtaun.shoebill.event.player.PlayerEvent {

    public PlayerEvent(LtrpPlayer player) {
        super(player);
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    @Override
    public String toString() {
        return String.format("player=%s", getPlayer().toString());
    }
}
