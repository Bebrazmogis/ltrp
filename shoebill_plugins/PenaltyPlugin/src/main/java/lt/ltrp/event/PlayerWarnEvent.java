package lt.ltrp.event;

import lt.ltrp.data.WarnData;
import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class PlayerWarnEvent extends PlayerEvent {

    private WarnData warnData;

    public PlayerWarnEvent(LtrpPlayer player, WarnData warnData) {
        super(player);
        this.warnData = warnData;
    }

    public WarnData getWarnData() {
        return warnData;
    }
}
