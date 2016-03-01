package lt.ltrp.event.player;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerOffer;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2016.03.01.
 */
public class PlayerOfferExpireEvent extends PlayerEvent {

    private PlayerOffer offer;

    public PlayerOfferExpireEvent(LtrpPlayer player, PlayerOffer offer) {
        super(player);
        this.offer = offer;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    public PlayerOffer getOffer() {
        return offer;
    }
}
