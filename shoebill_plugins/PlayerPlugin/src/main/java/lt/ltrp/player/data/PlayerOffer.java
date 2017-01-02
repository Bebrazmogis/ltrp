package lt.ltrp.player.data;


import lt.ltrp.event.player.PlayerOfferExpireEvent;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.entities.Timer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.01.
 */
public class PlayerOffer {

    private int duration;
    private LtrpPlayer player, offeredBy;
    private Timer expireTimer;
    private boolean isExpired;
    private Class type;


    public PlayerOffer(LtrpPlayer player, LtrpPlayer offeredBy, EventManager eventManager, int duration, Class offerType) {
        this(player, offeredBy, offerType);
        this.expireTimer = Timer.create(duration * 1000, 1, (i) -> {
            isExpired = true;
            eventManager.dispatchEvent(new PlayerOfferExpireEvent(player, this));
            expireTimer.destroy();
        });
        expireTimer.start();
    }

    public PlayerOffer(LtrpPlayer player, LtrpPlayer offeredBy, Class offerType) {
        this.player = player;
        this.offeredBy = offeredBy;
        this.type = offerType;
        this.isExpired = false;
    }

    public int getDuration() {
        return duration;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public LtrpPlayer getOfferedBy() {
        return offeredBy;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public Class getType() {
        return type;
    }
}
