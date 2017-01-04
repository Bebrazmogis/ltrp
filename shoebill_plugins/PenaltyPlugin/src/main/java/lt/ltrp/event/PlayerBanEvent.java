package lt.ltrp.event;

import lt.ltrp.data.BanData;
import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class PlayerBanEvent extends PlayerEvent {

    private BanData banData;

    public PlayerBanEvent(LtrpPlayer player, BanData banData) {
        super(player);
        this.banData = banData;
    }

    public BanData getBanData() {
        return banData;
    }
}
