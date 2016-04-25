package lt.ltrp.event.player;


import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.07.
 */
public class PlayerEvent extends net.gtaun.shoebill.event.player.PlayerEvent {

    public PlayerEvent(LtrpPlayer player) {
        super(player);
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }
}
