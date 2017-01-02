package lt.ltrp.player.event;


import lt.ltrp.player.data.PlayerOffer;
import lt.ltrp.player.object.LtrpPlayer;

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
