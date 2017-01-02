package lt.ltrp.player.event;

import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class PlayerDataLoadEvent extends PlayerEvent{


    public PlayerDataLoadEvent(LtrpPlayer player) {
        super(player);
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer) super.getPlayer();
    }
}
